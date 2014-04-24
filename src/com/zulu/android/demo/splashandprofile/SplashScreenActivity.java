package com.zulu.android.demo.splashandprofile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.discover.DiscoverActivity;

public class SplashScreenActivity extends Activity {

	private static final String TAG = "ZuluWelcome";

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);	
	}

	public void onResume() {

	    super.onResume();
	    
	    Thread thread = new Thread() {
	        @Override
	        public void run() {
	            try {
	                Thread.sleep(4000);
	                goToNextScreen();
	            } catch (InterruptedException e) {
	            }
	        }
	    };
	    thread.start();
	}

	public void goToNextScreen() {
	    SharedPreferences sharedPreferences = getSharedPreferences(Zulu.PREFS_NAME, Context.MODE_PRIVATE);
		String uname = sharedPreferences.getString("Uname", null);
		String email = sharedPreferences.getString("Email", null);
		String pword = sharedPreferences.getString("Pword", null);
		
		Zulu.uname = uname;
		Zulu.email = email;
		Zulu.pword = pword;
		
		if(uname != null && email != null){
			Log.i(TAG, "User name is already registered: " + uname + " with password: " + email);
		    Intent intent = new Intent(this, DiscoverActivity.class);
		    startActivity(intent);
		    finish();
		} else {
			Log.i(TAG, "User name is not yet registered, go to profile page");
			Intent intent = new Intent(this, ProfileActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
