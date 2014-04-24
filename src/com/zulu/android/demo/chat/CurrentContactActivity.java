package com.zulu.android.demo.chat;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.discover.DiscoverActivity;

public class CurrentContactActivity extends Activity
	implements AdapterView.OnItemClickListener{

	private ListView mCurrentContact;
	private LinkedList<String> mContacts;

	/**
	 * onCreate
	 */
	protected void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chatting_contact);
		mCurrentContact = (ListView) this.findViewById(R.id.listView2);
		mContacts = Zulu.currentContacts;
		setListAdapter();
		mCurrentContact.setOnItemClickListener(this);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
    	Intent myIntent = new Intent(CurrentContactActivity.this, DiscoverActivity.class);
    	CurrentContactActivity.this.startActivity(myIntent);
    	finish();
	}

	private void setListAdapter() {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.chatting_contact_list_item, mContacts);
		mCurrentContact.setAdapter(adapter);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		String selectedValue = (String) parent.getItemAtPosition(position);
		Zulu.setChattingwith(selectedValue);
    	Intent myIntent = new Intent(CurrentContactActivity.this, ChatActivity.class);
    	CurrentContactActivity.this.startActivity(myIntent);
	}
}