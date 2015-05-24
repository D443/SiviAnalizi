package com.hamdyghanem.httprequest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class MainMenu extends Activity {

    public int iLanguage = 0;
    TextView lbl;
    Typeface arabicFont = null;
    int TIMEOUT_MILLISEC = 10000; // = 10 seconds
    public String[] menuList;
    ListView lvMenu;
    String[] graphDays;
    int[] graphAvgHeat;
    int[] graphAvgPh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        lvMenu = (ListView)findViewById(R.id.lvMenu);
        //Intents
        final Intent DataRecords = new Intent(this,DataRecords.class);
        final Intent WeeklyList = new Intent(this,WeeklyList.class);
       //
        menuList = new String[]{"Son Kayıtlı Veriler", "Haftalık Grafik", "Haftalık Liste"};
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, menuList);
        lvMenu.setAdapter(adapter);
        // ListView Item Click Listener
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                switch (itemPosition){
                    case 0: startActivity(DataRecords); break;
                    case 1: showWeeklyGraph(); break;
                    case 2: startActivity(WeeklyList); break;
                }

            }

        });
        getLastInfo();
    }

        private void showWeeklyGraph() {
            LineGraph line = new LineGraph();
            getChartInfo();
            Intent lineIntent = line.getIntent(this,graphAvgHeat,graphAvgPh);
            startActivity(lineIntent);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getChartInfo(){
        try {
            JSONObject json = new JSONObject();
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient client = new DefaultHttpClient(httpParams);
            //
            //String url = "http://10.0.2.2:8080/sample1/webservice2.php?json={\"UserName\":1,\"FullName\":2}";
            String url = "http://webserviceanaliz.comyr.com/chartInfo.php";

            HttpPost request = new HttpPost(url);
            request.setEntity(new ByteArrayEntity(json.toString().getBytes(
                    "UTF8")));
            request.setHeader("json", json.toString());
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            if (entity != null) {
                InputStream instream = entity.getContent();

                String result = RestClient.convertStreamToString(instream);

                if (null != result && result.length() > 0 )
                {
                    int endIndex = result.indexOf("<");
                    if (endIndex != -1)
                    {
                        result = result.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                    }
                }
                result = "{ \"result\": "+result+"}";
                JSONObject json2;
                json2 = new JSONObject(result);
                JSONArray jArray = json2.getJSONArray("result");
                ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
                String[] values = new String[jArray.length()*3];
                int cnt =0;
                graphDays = new String[jArray.length()];
                graphAvgHeat = new int[jArray.length()];
                graphAvgPh = new int[jArray.length()];
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                   /* String res = "{ \"result\": "+jArray.getJSONObject(i)+"}";
                    String s = e.getString("result");*/
                    JSONObject jObject = new JSONObject(jArray.getJSONObject(i).toString());

                    map.put("day_of_week", jObject.getString("day_of_week"));
                    values[cnt] = jObject.getString("day_of_week");
                    graphDays[i] = values[cnt];
                    cnt++;
                    map.put("AvgHeat", jObject.getString("AvgHeat"));
                    values[cnt] = jObject.getString("AvgHeat");
                    try {
                        graphAvgHeat[i] = Integer.parseInt( values[cnt]);
                    } catch(NumberFormatException nfe) {
                    }

                    cnt++;
                    map.put("AvgPh", jObject.getString("AvgPh"));
                    values[cnt] = jObject.getString("AvgPh");
                    try {
                        graphAvgPh[i] = Integer.parseInt(values[cnt]);
                    } catch(NumberFormatException nfe) {
                    }

                    cnt++;

                    myList.add(map);
                }
                ListView lv = (ListView)findViewById(R.id.listView);
                Object[] items;
                items = myList.toArray();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        values);

                lv.setAdapter(arrayAdapter);

                Log.i("Read from server", result);
				/*Toast.makeText(this,  result,
						Toast.LENGTH_LONG).show();*/
            }
        } catch (Throwable t) {

        }
    }
    public void getLastInfo(){
        try {
            JSONObject json = new JSONObject();
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,
                    TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient client = new DefaultHttpClient(httpParams);

            String url = "http://webserviceanaliz.comyr.com/info.php";

            HttpPost request = new HttpPost(url);
            request.setEntity(new ByteArrayEntity(json.toString().getBytes(
                    "UTF8")));
            request.setHeader("json", json.toString());
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            if (entity != null) {
                InputStream instream = entity.getContent();

                String result = RestClient.convertStreamToString(instream);

                if (null != result && result.length() > 0 )
                {
                    int endIndex = result.indexOf("<");
                    if (endIndex != -1)
                    {
                        result = result.substring(0, endIndex); // not forgot to put check if(endIndex != -1)
                    }
                }
                //result = "{ \"result\": "+result+"}";
                JSONObject json2;
                json2 = new JSONObject(result);
                JSONArray jArray = json2.getJSONArray("info");
                ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
                String[] values = new String[jArray.length()*3];
                int cnt =0;
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);

                    JSONObject jObject = new JSONObject(jArray.getJSONObject(i).toString());

                    map.put("Heat", jObject.getString("Heat"));
                    values[cnt] = jObject.getString("Heat");
                    cnt++;
                    map.put("Ph", jObject.getString("Ph"));
                    values[cnt] = jObject.getString("Ph");
                    cnt++;

                    myList.add(map);
                }

                TextView tvValues = (TextView)findViewById(R.id.tvNowValue);

                tvValues.setTextColor(Color.GREEN);
                tvValues.setText("Heat : " + values[0] + " Ph : " + values[1]);



                Log.i("Read from server", result);
				/*Toast.makeText(this,  result,
						Toast.LENGTH_LONG).show();*/
            }
        } catch (Throwable t) {

        }
    }
}
