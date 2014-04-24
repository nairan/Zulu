package com.zulu.android.demo.chat;

import java.util.LinkedList;

import org.jivesoftware.smack.packet.Message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;
import com.zulu.android.demo.discover.DiscoverActivity;

public class ChatActivity extends Activity 
	implements Button.OnClickListener {

	private final static String TAG = "XMPPClient";
	
	public static MainHandler mHandler;
	
    private static LinkedList<String> messages = new LinkedList<String>();
    private EditText mSendText;
    private ListView mList;
    private Button send;
    
	public class MainHandler extends Handler {
		
		public void handleMessage(android.os.Message msg) {
            try {
                if (msg.what == 999) {
                	mHandler.post(new Runnable() {
                        public void run() {
                        	setListAdapter();
                        }
                    });
                }                 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent i = new Intent(ChatActivity.this, CurrentContactActivity.class);
		this.startActivity(i);
	}


    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chatting__main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        
        send = (Button) this.findViewById(R.id.button1);
        send.setOnClickListener(this);
        
        mHandler = new MainHandler();

        this.mSendText = (EditText) this.findViewById(R.id.editText1);
        this.mList = (ListView) this.findViewById(R.id.listView1);
        ((TextView) this.findViewById(R.id.textView1)).setText("Talk with " + Zulu.chattingWith);
        setListAdapter();
    }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Zulu.setChattingwith("");
	}

	/**
     * Text sent listener button
     */
	@Override
	public void onClick(View v) {

		
		
        String to = Zulu.chattingWith + Zulu.SERVICENAME;
        String text = mSendText.getText().toString();
        Log.i(TAG, "Sending text [" + text + "] to [" + to + "]");
        Message msg = new Message(to, Message.Type.chat);
        msg.setBody(text);
        Zulu.connection.sendPacket(msg); // at this moment, connection cannot be null
        Zulu.updateContactMessages(Zulu.chattingWith, msg.getBody());
        setListAdapter();
        mSendText.setText("");
	}
	
	private void setListAdapter() {
		if(Zulu.getContactMessages().containsKey(Zulu.chattingWith)){
			messages = Zulu.getContactMessages().get(Zulu.chattingWith);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.chatting_multi_line_list_item, messages);
			mList.setAdapter(adapter);
		}
	}
}
