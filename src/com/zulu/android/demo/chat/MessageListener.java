package com.zulu.android.demo.chat;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.epresence.R;
import com.zulu.android.demo.Zulu;

/**
 * @author nairan
 *
 */
public class MessageListener extends Thread{
	
	private final static String TAG = "MessageListener";
	private final static int NOTIFID = 1;

	private XMPPConnection connection;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNM;
    private Context ctx;
    
    public MessageListener(Context ctx){
    	this.connection = Zulu.getXMPPConnection();
    	this.ctx = ctx;
    }
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		String uname = "";
		String pword = "";
		try {
			connection.connect();
			// I retrieve username and password from user setting storage
		    SharedPreferences sharedPreferences = this.ctx.getSharedPreferences(Zulu.PREFS_NAME, Context.MODE_PRIVATE);
		    String fullEmail = sharedPreferences.getString("Email", null);
			uname = fullEmail.split("@")[0];
			pword = sharedPreferences.getString("Pword", null);
			// then login 
			Log.i(TAG, "before login");
			connection.login(uname, pword);
			Log.i(TAG, "done login");
			Presence presence = new Presence(Presence.Type.available);
			Log.i(TAG, "before sending presence");
			connection.sendPacket(presence);
			Log.i(TAG, "should be online");
			setConnection();
		} catch (XMPPException e) {
			Log.i(TAG, "Connect and login failed: " + uname + " Pword: " + pword);
			e.printStackTrace();
		} catch (Exception e){
			Log.i(TAG, "For something I do not know");
			e.printStackTrace();
		}
	}
	
    /**
     * Called by Settings dialog when a connection is established with the XMPP server
     * All later messages come here
     *
     * @param connection
     */
    private void setConnection() {
    	Log.i(TAG, "In setconnection");
        if (connection.isConnected()) {
        	Log.i(TAG, "Connection conneted - checked");
        	// Initiate notif
			mNM = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);			
			mNotifyBuilder = new NotificationCompat.Builder(this.ctx)
			    .setContentTitle("New Zulus")
			    //.setContentText(Zulu.getUnread() + " user(s) sent you message(s).")
			    .setSmallIcon(R.drawable.icon)
			    .setAutoCancel(true);
			
			// Define the Notification's Action
			Intent resultIntent = new Intent(this.ctx, CurrentContactActivity.class);
			PendingIntent resultPendingIntent =
			    PendingIntent.getActivity(
			    this.ctx,
			    0,
			    resultIntent,
			    0
			);
			mNotifyBuilder.setContentIntent(resultPendingIntent);
			
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            Zulu.getXMPPConnection().addPacketListener(new PacketListener() {
            	
            	// Async event, process packet when we have packet
                public void processPacket(Packet packet) {
                	String fromName = packet.getFrom().trim().split("@")[0];
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        Log.i("XMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
                    	Zulu.updateContactMessages(fromName, fromName + ": " + message.getBody());
                    }

                    if(Zulu.getUnread() > 0){
                    	mNotifyBuilder
                    	.setContentText(Zulu.getUnread() + " user(s) sent you message(s).");
                    	// Because the ID remains unchanged, the existing notification is updated.
                    	mNM.notify(NOTIFID, mNotifyBuilder.build());
                    }
                }
            }, filter);
        }
    }
}
