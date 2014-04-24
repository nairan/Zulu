package com.zulu.android.demo.splashandprofile;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.discover.DiscoverActivity;

public class ProfileActivity extends Activity 
	implements Button.OnClickListener {

	private Button m_save;
	private Button mFemale;
	private Button mMale;
	private EditText mName;
	private EditText mEmail;
	private boolean hasName = false;
	private boolean hasEmail = false;

	protected void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.profile__main);
	    
		m_save = (Button) this.findViewById(R.id.profile_id_button_save);
		m_save.setOnClickListener(this);
		m_save.setEnabled(false);
		
		mFemale = (Button) this.findViewById(R.id.female);
		mFemale.setOnClickListener(this);
		mFemale.setEnabled(false);
		mMale = (Button) this.findViewById(R.id.male);
		mMale.setOnClickListener(this);
		mMale.setEnabled(true);
		
		mName = (EditText) this.findViewById(R.id.profile_id_value_name);
		mName.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				if(mName.getText().toString().length() > 5){
					hasName = true;
					enableSubmitIfReady();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

		});
			
		mEmail = (EditText) this.findViewById(R.id.profile_id_value_email);
		mEmail.setHint("alice@gmail.com");
		mEmail.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				if(mEmail.getText().toString().split("@").length != 2){
					hasEmail = true;
					enableSubmitIfReady();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

		});

	}
	
	private void enableSubmitIfReady() {
		// TODO Auto-generated method stub
		if(hasName && hasEmail){
			m_save.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == m_save) {
			UpdateProfile task = new UpdateProfile();
			task.execute();
		} else if(v == mFemale){
			mFemale.setEnabled(false);
			mMale.setEnabled(true);
		} else if(v == mMale){
			mFemale.setEnabled(true);
			mMale.setEnabled(false);
		}
	}
	
	private class UpdateProfile extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			Zulu.getRecord().setName(((EditText) findViewById(R.id.profile_id_value_name)).getText().toString());
			Zulu.getRecord().setEmail(((EditText) findViewById(R.id.profile_id_value_email)).getText().toString());
			
			SharedPreferences sharedPreferences = getSharedPreferences(Zulu.PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("Uname", ((EditText) findViewById(R.id.profile_id_value_name)).getText().toString());
			editor.putString("Email", ((EditText) findViewById(R.id.profile_id_value_email)).getText().toString());
			editor.putString("Pword", Zulu.getRecord().getUserID()); // imei
			editor.commit();
			
		    try {
		    	// We currently transmit name, imei and location
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		        nameValuePairs.add(new BasicNameValuePair("name", Zulu.uname));
		        nameValuePairs.add(new BasicNameValuePair("imei", Zulu.pword));
		        nameValuePairs.add(new BasicNameValuePair("email", Zulu.email));
		        
		        // Use HttpClient with params
		        HttpParams httpParams = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		        HttpConnectionParams.setSoTimeout(httpParams, 10000);
		        HttpClient client = new DefaultHttpClient(httpParams);
		        // Specify the server and prepare data
		        String url = Zulu.serverIP + Zulu.addUserPHP;
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
		        	Intent myIntent = new Intent(ProfileActivity.this, DiscoverActivity.class);
		    		ProfileActivity.this.startActivity(myIntent);
		    		finish();
		        } else {
		        	Log.i(SplashScreenActivity.class.toString(), "Failed, profile update server response error code: " + finalResult);
		        } 
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
		    return null;
		    
		}
	}
}
