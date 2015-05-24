package com.hamdyghanem.httprequest;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main extends Activity {
	/** Called when the activity is first created. */
	public int iLanguage = 0;
	TextView lbl;
	Typeface arabicFont = null;
	int TIMEOUT_MILLISEC = 10000; // = 10 seconds

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			getWindow().setLayout(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			// ////
			arabicFont = Typeface.createFromAsset(getAssets(),
					"fonts/DroidSansArabic.ttf");

			//lbl = (TextView) findViewById(R.id.lbl);
			//

		} catch (Throwable t) {
			Toast.makeText(this, "Request failed: " + t.toString(),
					Toast.LENGTH_LONG).show();
		}
	}

	public void clickbuttonRecieve(View v) {
		try {
			JSONObject json = new JSONObject();
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient client = new DefaultHttpClient(httpParams);
			//
			//Servis Adresi
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
                for (int i = 0; i < jArray.length(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    JSONObject e = jArray.getJSONObject(i);
                   /* String res = "{ \"result\": "+jArray.getJSONObject(i)+"}";
                    String s = e.getString("result");*/
                    JSONObject jObject = new JSONObject(jArray.getJSONObject(i).toString());

                    map.put("day_of_week", jObject.getString("day_of_week"));
                    values[cnt] = jObject.getString("day_of_week");
                    cnt++;
                    map.put("AvgHeat", jObject.getString("AvgHeat"));
                    values[cnt] = jObject.getString("AvgHeat");
                    cnt++;
                    map.put("AvgPh", jObject.getString("AvgPh"));
                    values[cnt] = jObject.getString("AvgPh");
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