package com.example.starlist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private String jsonResult;  
	private String url = "http://169.254.168.159/liststar.php";  
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView1);   
		accessDatabase(); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	public void accessDatabase() {   
		JsonReadTask task = new JsonReadTask();   
		task.execute(new String[] { url });  
	} 

	private class JsonReadTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();
				return jsonResult;
			} catch (HttpHostConnectException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			//Log.i("RESULT", result);

			listDrawer();
		}
	}
	//----------------------------------------------------------
	public class StarInfo {
		String name;
		String url;

		public StarInfo(String n, String u) {
			name = n;
			url = u;
		}

		public String getName() {
			return name;
		}
		public String getUrl() {
			return url;
		}
	}

	//-----------------------------------------------------
	public void listDrawer() {
		//List<Map<String, String>> employeeList = new ArrayList<Map<String, String>>();
		ArrayList<StarInfo> starList = new ArrayList<StarInfo>();

		try {
			JSONObject jsonResponse = new JSONObject(jsonResult);
			JSONArray jsonMainNode = jsonResponse.optJSONArray("star");

			for (int i = 0; i < jsonMainNode.length(); i++) {
				JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
				String name = jsonChildNode.optString("name");
				String picture = jsonChildNode.optString("picture");
				//String outPut = name + "-" + number;
				//employeeList.add(createEmployee("employees", outPut));
				starList.add(new StarInfo(name, picture));
			}
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Error" + e.toString(),
					Toast.LENGTH_SHORT).show();
		}
		//Log.i("JSON", starList.get(0).getName());

		StarAdapter starAdapter = new StarAdapter(this,starList);
		listView.setAdapter(starAdapter);
		
//		SimpleAdapter simpleAdapter = new SimpleAdapter(this, employeeList,
//				android.R.layout.simple_list_item_1,
//				new String[] { "employees" }, new int[] { android.R.id.text1 });
//		listView.setAdapter(simpleAdapter);
	}
	
	//-------------------------------------------------
	class ViewHolder{
        public TextView tvName = null;
        public ImageView ivImage = null;
    }
	
	// 참고자료 
	// http://arabiannight.tistory.com/77
	public class StarAdapter extends BaseAdapter {
	     
	    private LayoutInflater inflater = null;
	    private ArrayList<StarInfo> infoList = null;
	    private ViewHolder viewHolder = null;
	    private Context mContext = null;
	     
	    public StarAdapter(Context c , ArrayList<StarInfo> starList){
	        this.mContext = c;
	        this.inflater = LayoutInflater.from(c);
	        this.infoList = starList;
	    }

		@Override
		public int getCount() {
			return infoList.size();
		}

		@Override
		public Object getItem(int position) {
			return infoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertview, ViewGroup parent) {
			View v = convertview;
	         
	        if(v == null){
	            viewHolder = new ViewHolder();
	            v = inflater.inflate(R.layout.listitem, null);
	            viewHolder.tvName = (TextView)v.findViewById(R.id.nameView);
	            viewHolder.ivImage = (ImageView)v.findViewById(R.id.imageView);
	 
	            v.setTag(viewHolder);
	             
	        }else {
	            viewHolder = (ViewHolder)v.getTag();
	        }
	         
	        viewHolder.tvName.setText(infoList.get(position).getName());
	        ImageLoader imageLoader = new ImageLoader(mContext);
	        imageLoader.DisplayImage("http://169.254.168.159/" + 
	        						infoList.get(position).getUrl(), 
	        						viewHolder.ivImage);
	        
	        //viewHolder.ivImage.setTag(position);
	        //viewHolder.iv_image.setOnClickListener(buttonClickListener);
	         
	        return v;
		}
	}
}














