package com.zulu.android.demo.discover;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.chat.CurrentContactActivity;
import com.zulu.android.demo.chat.MessageListener;
import com.zulu.android.demo.myzulu.MyZulusActivity;
import com.zulu.android.demo.splashandprofile.SplashScreenActivity;
import com.zulu.android.demo.util.NearbyUserDetails;

public class DiscoverActivity extends Activity
	implements OnClickListener, LocationListener {
	
	private static final String TAG = "ZuluDiscover";
	//private final static String TAG = "Discover";
	
	private Button discover;
	private Button leave;
	private Button contact;
	private CheckBox chkbx;
	private LocationManager mlocManager;
    
	public void onCreate(Bundle savedInstanceState) {
		
		if(Zulu.splash){
			Zulu.splash = false;
			Intent i = new Intent(DiscoverActivity.this, SplashScreenActivity.class);
			startActivity(i);		
		}
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		// Two buttons in this page
		discover = (Button) this.findViewById(R.id.discover_id_discover_button);
		discover.setOnClickListener(this);
		discover.setEnabled(false);
		leave = (Button) this.findViewById(R.id.discover_id_leave_button);
		leave.setOnClickListener(this);    
        leave.setEnabled(false);
		contact = (Button) this.findViewById(R.id.discover_id_contact_button);
		contact.setOnClickListener(this);    
        contact.setEnabled(false);
        
        // One check box in this page
        buttoncontrolledbycheckbox();
        
        // We need to request location updates in this activity
        mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		// Back to home screen
	    Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
	}

	/**
	 * We handle location updates in start.stop pair
	 */
	protected void onStart(){
		super.onStart();
		Zulu.getRecord().setLati(0);
		Zulu.getRecord().setLong(0);
		mlocManager.requestLocationUpdates("network", 0, 0, this);
		mlocManager.requestLocationUpdates("gps", 0, 0, this);
	}
	
	protected void onStop(){
		super.onStop();
		mlocManager.removeUpdates(this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    save(chkbx.isChecked());
	}

	@Override
	public void onResume() {
	    super.onResume();
	    boolean checked = load();
	    Log.i(TAG, "Zulu discover check enabled: " + checked);
	    chkbx.setChecked(checked);
	    if(checked){
			discover.setEnabled(true);
			leave.setEnabled(true);
			contact.setEnabled(true);
			if(!Zulu.getXMPPConnection().isConnected()){
				MessageListener ml = new MessageListener(DiscoverActivity.this);
				ml.start();
			}
	    } else {
			if(Zulu.getXMPPConnection().isConnected()){
				Zulu.getXMPPConnection().disconnect();
			}
			discover.setEnabled(false);
			leave.setEnabled(false);
			contact.setEnabled(false);
	    }
	}

	private void save(final boolean isChecked) {
	    SharedPreferences sharedPreferences = getSharedPreferences(Zulu.PREFS_NAME, Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sharedPreferences.edit();
	    editor.putBoolean("check", isChecked);
	    editor.commit();
	}

	private boolean load() { 
	    SharedPreferences sharedPreferences = getSharedPreferences(Zulu.PREFS_NAME, Context.MODE_PRIVATE);
	    return sharedPreferences.getBoolean("check", false);
	}
	
	/**
	 * We have a thread dedicated to handle incoming messages
	 */
	private void buttoncontrolledbycheckbox() {
		chkbx = (CheckBox) findViewById(R.id.discover_id_checkbx);
		 
		chkbx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					Log.i(TAG, "You check the button");
					discover.setEnabled(true);
					leave.setEnabled(true);
					contact.setEnabled(true);
					if(!Zulu.getXMPPConnection().isConnected()){
						Log.i(TAG, "You check the button, but it is not connected yet");
						MessageListener ml = new MessageListener(DiscoverActivity.this);
						ml.start();
					}
					
				} else {
					if(Zulu.getXMPPConnection() != null){
						Zulu.getXMPPConnection().disconnect();
					}
					discover.setEnabled(false);
					leave.setEnabled(false);
					contact.setEnabled(false);
				}	 
			}
		});
	}
	
    /**
     * button click 
     */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == discover){
			DiscoverTask task = new DiscoverTask();
			task.execute();
		} else if(v == leave){
        	Intent myIntent = new Intent(DiscoverActivity.this, MyZulusActivity.class);
        	DiscoverActivity.this.startActivity(myIntent);
		} else if(v == contact){
        	Intent myIntent = new Intent(DiscoverActivity.this, CurrentContactActivity.class);
        	DiscoverActivity.this.startActivity(myIntent);
		}
	}
	
	private class DiscoverTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
		    try {
		    	// We currently transmit name, imei and location
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		        nameValuePairs.add(new BasicNameValuePair("email", Zulu.email));
		        nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(Zulu.getRecord().getLati())));
		        nameValuePairs.add(new BasicNameValuePair("lng", String.valueOf(Zulu.getRecord().getLong())));
		        // Use HttpClient with params
		        HttpParams httpParams = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		        HttpConnectionParams.setSoTimeout(httpParams, 10000);
		        HttpClient client = new DefaultHttpClient(httpParams);
		        // Specify the server and prepare data
		        String url = Zulu.serverIP + Zulu.discoverPHP;
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
		        ArrayList<NearbyUserDetails> nearbyUsers = new ArrayList<NearbyUserDetails>();
		        //Log.i(MainActivity.class.toString(), "Response: " + finalResult);
		        
		        // To JSON representation, 
		        try {
		        	JSONArray jsonArray = new JSONArray(finalResult);
		        	for (int i = 0; i < jsonArray.length(); i++) {
		        		JSONObject jsonObject = jsonArray.getJSONObject(i);
		        		String name = jsonObject.getString("name");
		        		String lat = jsonObject.getString("lat");
		        		String lng = jsonObject.getString("lng");
		        		String status = jsonObject.getString("status");
		        		String email = jsonObject.getString("email");
		        		Log.i(SplashScreenActivity.class.toString(), name + ": " + lat + "  " + lng + "  " + status + " " + email);
		        		nearbyUsers.add(new NearbyUserDetails(name, status, lat, lng, email));
		        	}
		        	Zulu.nearbyUsers = nearbyUsers;
		        	Intent myIntent = new Intent(DiscoverActivity.this, DiscoverResultActivity.class);
		        	DiscoverActivity.this.startActivity(myIntent);
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }

		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
			return null;
		}		
	}
	
	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		Zulu.getRecord().setLati(loc.getLatitude());
		Zulu.getRecord().setLong(loc.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
