package com.zulu.android.demo.myzulu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.splashandprofile.ProfileActivity;
import com.zulu.android.demo.splashandprofile.SplashScreenActivity;
import com.zulu.android.demo.util.MyZuluDetails;

public class MyZulusActivity extends Activity
	implements OnClickListener,AdapterView.OnItemClickListener{
	
	private Button post, updateProfile, refresh;
	private MyZuluListAdapter mAdapter;
	private ListView mMyzululist;
	
	private Object localObject = new Object();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zulus__main);
		
		post = (Button) this.findViewById(R.id.lulus_id_button_post);
		post.setOnClickListener(this);
		refresh = (Button) this.findViewById(R.id.lulus_id_button_refresh);
		refresh.setOnClickListener(this);
		updateProfile = (Button) this.findViewById(R.id.lulus_id_button_profile);
		updateProfile.setOnClickListener(this);		
	}
	
	public ArrayList<MyZuluDetails> getFilteredZulus(){
		return Zulu.nonExpiredZulus;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == post){
			Zulu.readyToModifyZulu = false;
        	Intent myIntent = new Intent(MyZulusActivity.this, NewZuluActivity.class);
        	MyZulusActivity.this.startActivity(myIntent);
		} else if(v == updateProfile){
        	Intent myIntent = new Intent(MyZulusActivity.this, ProfileActivity.class);
        	MyZulusActivity.this.startActivity(myIntent);
		} else if(v == refresh){
			getMyZulusTask task = new getMyZulusTask();
			task.execute();
			//while(EPApplication.nonExpiredZulus == null);
			synchronized(localObject){
				try {
					localObject.wait();
					// Set adapter
					this.mAdapter = new MyZuluListAdapter();
					this.mMyzululist = ((ListView)findViewById(R.id.lulus_id_myzulu_list));
					this.mMyzululist.setAdapter(this.mAdapter);
					this.mMyzululist.setClickable(true);
					this.mMyzululist.setOnItemClickListener(MyZulusActivity.this);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private class getMyZulusTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
		    try {
		    	// We currently transmit name, imei and location
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		        nameValuePairs.add(new BasicNameValuePair("email", Zulu.email));
		        // Use HttpClient with params
		        HttpParams httpParams = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		        HttpConnectionParams.setSoTimeout(httpParams, 10000);
		        HttpClient client = new DefaultHttpClient(httpParams);
		        // Specify the server and prepare data
		        String url = Zulu.serverIP + Zulu.refreshzuluPHP;
		        HttpPost request = new HttpPost(url);
		        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        
		        // Response	  
		        StringBuilder builder = new StringBuilder();
		        try{
		        	// Initiate communication
		        	HttpResponse response = client.execute(request);
		        	// Check communication successful
		        	StatusLine statusLine = response.getStatusLine();
		        	int statusCode = statusLine.getStatusCode();
		        	if(statusCode == 200){ // successful
		        		// Retrieve response
		        		HttpEntity entity = response.getEntity();
		        		InputStream content = entity.getContent();
		        		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		        		String line;
		        		// Put into the buffer
		        		while((line = reader.readLine()) != null){
		        			builder.append(line);
		        		}
		        	} else { // Simply report error
		        		Log.i(SplashScreenActivity.class.toString(), "Server response code is not 200!");
		        	}
		        } catch(ClientProtocolException e) {
		        	e.printStackTrace();
		        } catch (IOException e) {
		        	e.printStackTrace();
		        }
		        String finalResult = builder.toString(); // Convert buffer to string
		        ArrayList<MyZuluDetails> myZulus = new ArrayList<MyZuluDetails>();
		        
		        if(finalResult.startsWith("null")){
		        	Zulu.nonExpiredZulus = null;
		        	synchronized(localObject){
		        		localObject.notify();
		        	}
		        	return null;
		        } 
		        
		        // To JSON representation, 
		        try {
		        	JSONArray jsonArray = new JSONArray(finalResult);
		        	for (int i = 0; i < jsonArray.length(); i++) {
		        		JSONObject jsonObject = jsonArray.getJSONObject(i);
		        		String zulu = jsonObject.getString("status");
		        		String zuluid = jsonObject.getString("statusid");
		        		//String exit = jsonObject.getString("exit");
		        		myZulus.add(new MyZuluDetails(zulu, zuluid));
		        	}
		        	Log.i("getCount", "#:" + myZulus.size());
		        	Zulu.nonExpiredZulus = myZulus;
		        	
		        	synchronized(localObject){
		        		localObject.notify();
		        	}
		        	
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }

		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
			return null;
		}
	}

	protected class MyZuluListAdapter extends BaseAdapter {

		public int getCount(){
			/*
			synchronized(localObject){
				try {
					localObject.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
			if (getFilteredZulus() != null)
				return getFilteredZulus().size();	
			return -1;
		}

		public MyZuluDetails getItem(int position){
			if (getFilteredZulus() != null)
				return getFilteredZulus().get(position);
			return null;
		}

		public long getItemId(int position){
			return getFilteredZulus().get(position).hashCode();
		}

		public View getView(int position, View convertView, ViewGroup container){
			MyZuluDetails myZulusResultItem = (MyZuluDetails) getItem(position);
			if (convertView == null) // have to create a new view
				convertView = ((LayoutInflater) MyZulusActivity.this.getSystemService("layout_inflater")).inflate(R.layout.zulus_my_list_item, null);

			if (position % 2 == 0){
				convertView.setBackgroundResource(R.drawable.list_item_selector);
			} else {
				convertView.setBackgroundResource(R.drawable.list_item_selector);
			}
			
			if (myZulusResultItem != null){
				((TextView) convertView.findViewById(R.id.lulus_id_list_item_zulu)).setText(myZulusResultItem.getZulu());
				//((TextView) convertView.findViewById(R.id.lulus_id_list_item_expire)).setText(myZulusResultItem.getExit());
			}

			return convertView;
		}	
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
		// TODO Auto-generated method stub
		Zulu.selectedZulu = (MyZuluDetails) parent.getItemAtPosition(position);
    	Intent myIntent = new Intent(MyZulusActivity.this, MyZuluDetailsActivity.class);
    	MyZulusActivity.this.startActivity(myIntent);
	}
	
}
