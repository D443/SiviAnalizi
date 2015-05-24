package com.hamdyghanem.httprequest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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


public class DataRecords extends Activity {

    int TIMEOUT_MILLISEC = 10000; // = 10 seconds
    String[] times;
    Double[] graphAvgHeat;
    Double[] graphAvgPh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_records);

        getChartInfo();
        ListView lvDays = (ListView)findViewById(R.id.lvRecs);

        ListAdapter cAdapter = new CustomAdapter(this,times,graphAvgHeat,graphAvgPh);

        lvDays.setAdapter(cAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_records, menu);
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
            String url = "http://webserviceanaliz.comyr.com/todayInfo.php";

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
                times = new String[jArray.length()];
                graphAvgHeat = new Double[jArray.length()];
                graphAvgPh = new Double[jArray.length()];
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                   /* String res = "{ \"result\": "+jArray.getJSONObject(i)+"}";
                    String s = e.getString("result");*/
                    JSONObject jObject = new JSONObject(jArray.getJSONObject(i).toString());

                    map.put("RecTime", jObject.getString("RecTime"));
                    values[cnt] = jObject.getString("RecTime");
                    times[i] = values[cnt];
                    cnt++;
                    map.put("AvgHeat", jObject.getString("AvgHeat"));
                    values[cnt] = jObject.getString("AvgHeat");
                    try {
                        graphAvgHeat[i] = Double.parseDouble( values[cnt]);
                    } catch(NumberFormatException nfe) {
                    }

                    cnt++;
                    map.put("AvgPh", jObject.getString("AvgPh"));
                    values[cnt] = jObject.getString("AvgPh");
                    try {
                        graphAvgPh[i] = Double.parseDouble(values[cnt]);
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
}
