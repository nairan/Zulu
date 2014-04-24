package com.zulu.android.demo.discover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.chat.ChatActivity;

/**
 * 
 * We need detail info of the zulu and publisher
 * 
 * @author nairan
 *
 */
public class ShowResultItemActivity extends Activity
	implements OnClickListener{
	
	Button chat;

	protected void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.discoverresult_detail);
		
		chat = (Button) findViewById(R.id.button1);
		chat.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Zulu.setChattingwith(Zulu.getSelectedNearbuZulu().getUsername());
		Intent myIntent = new Intent(ShowResultItemActivity.this, ChatActivity.class);
		ShowResultItemActivity.this.startActivity(myIntent);
		finish();
	}	
}
