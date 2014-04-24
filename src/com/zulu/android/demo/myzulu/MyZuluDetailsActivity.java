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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.splashandprofile.SplashScreenActivity;

public class MyZuluDetailsActivity extends Activity
	implements OnClickListener{
	
	Button modify, delete;

	protected void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zulus_myzulu_detail);
		
		modify = (Button) findViewById(R.id.lulus_id_myzuludetail_modify);
		delete = (Button) findViewById(R.id.lulus_id_myzuludetail_delete);
		modify.setOnClickListener(this);	
		delete.setOnClickListener(this);	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == modify){
			Zulu.readyToModifyZulu = true;
        	Intent myIntent = new Intent(MyZuluDetailsActivity.this, NewZuluActivity.class);
        	MyZuluDetailsActivity.this.startActivity(myIntent);
		} else if(v == delete){
			DeleteMyZuluTask task = new DeleteMyZuluTask();
			task.execute();
        	Intent myIntent = new Intent(MyZuluDetailsActivity.this, MyZulusActivity.class);
        	MyZuluDetailsActivity.this.startActivity(myIntent);
		}
	}
	
	private class DeleteMyZuluTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
		    try {
		    	// We currently transmit name, imei and location
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("email", Zulu.email));
		        nameValuePairs.add(new BasicNameValuePair("statusid", Zulu.selectedZulu.getZuluid()));
		        Log.i(SplashScreenActivity.class.toString(), "Status id:" + Zulu.selectedZulu.getZuluid());
		        // Use HttpClient with params
		        HttpParams httpParams = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		        HttpConnectionParams.setSoTimeout(httpParams, 10000);
		        HttpClient client = new DefaultHttpClient(httpParams);
		        // Specify the server and prepare data
		        String url = Zulu.serverIP + Zulu.deletezuluPHP;
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

		        // Error check and start status activity
		        String finalResult = builder.toString(); // Convert buffer to string
		        if(finalResult.startsWith("Success")){
		        	Log.i(SplashScreenActivity.class.toString(), "Successfully delete a Zulu");
		        } else {
		        	Log.i(SplashScreenActivity.class.toString(), "Failed, delete zulu error code: " + finalResult);
		        } 

		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
			return null;
		}
	}
}
