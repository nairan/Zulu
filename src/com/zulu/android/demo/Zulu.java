package com.zulu.android.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zulu.android.demo.chat.ChatActivity;
import com.zulu.android.demo.util.MyZuluDetails;
import com.zulu.android.demo.util.NearbyUserDetails;
import com.zulu.android.demo.util.ProfileRecord;

public class Zulu extends Application {

	private static Zulu application;
	public static final String dbname = "com.epresence.android.demo";
	public static final String serverIP = "http://155.98.37.150/";
	public static final String CHATSERVERIP = "155.98.37.150";
	public static final String PORT = "5222";
	public static final String SERVICENAME = "@pc.chatting.ch-geni-net.emulab.net";
	private static final String FIRST_LAUNCH = "first_launch";
	public static final String PREFS_NAME = "MyPrefsFile";
	
	// Add-user
	// Input: name, imei, email, lat, lng
	// Output: "Success"
	public static final String addUserPHP = "add-user.php";
	// Add-zulu
	// Input: email, lat, lng, zulu
	// Output: "Success#statusID(should be an integer)"
	public static final String addZuluPHP = "add-status.php";	
	// Modify-zulu
	// Input: email, lat, lng, zulu, zuluid
	// Output: "Success"
	public static final String modifyZuluPHP = "modify-status.php";	
	// Discover
	// Input: email, lat, lng
	// Output: JSON array list of nearby users
	public static final String discoverPHP = "discover.php";
	// Refresh-zulu
	// Input: email, lat, lng
	// Output: JSON array list of nearby users
	public static final String refreshzuluPHP = "refresh-status.php";
	// Delete-zulu
	// Input: email, zuluid
	// Output: "Success"
	public static final String deletezuluPHP = "delete-status.php";
	private static final String TAG = "ZuluApplication";
	
	public static boolean inForground = false;
	public static boolean splash = true;
	public boolean isAppStartedFromQuitState = true;
	public boolean isAppTerminating = false;
	public boolean isPopupUse = false;
	private volatile int m_activityCount = 0;
	
	// Persistent storage
	public static String uname = "";
	public static String email = "";
	public static String pword = "";

	//private LocationUtil m_locUtil;
	//private HashMap<String, YPLocationChangeListener> m_locationListeners;
	//private volatile boolean m_newActivitesStarted = false;
	//private SubmitRatingScheduler m_ratingScheduler;
	//private String m_requestId = UUID.randomUUID().toString();
	//public UserInfo m_userInfo = null;
	
	// We need this data structure to keep all contact and char information
	public static HashMap<String, LinkedList<String>> contactMassages = new HashMap<String, LinkedList<String>>();
	public static String chattingWith = "";
	public static LinkedList<String> currentContacts = new LinkedList<String>();
	public static LinkedList<String> unreadContacts = new LinkedList<String>();
	public static String selectedfromcontacts;

	// The owner stuff
	private String m_visitorId = null;
	public static ArrayList<MyZuluDetails> nonExpiredZulus = null;
	public static MyZuluDetails selectedZulu = null;
	public static boolean readyToModifyZulu = false;
	
	// SelectedNearbyZulu
	public static ArrayList<NearbyUserDetails> nearbyUsers = null;
	public static NearbyUserDetails selectedNearbyZulu = null;
	
    // XMPP connection
    public static XMPPConnection connection;
    
    public static String getUname(){
    	if(getRecord().getEmail()==null){
    		return null;
    	}
    	return getRecord().getEmail().split("@")[0] + SERVICENAME;
    };
    
    public static String getPword(){
    	return getRecord().getUserID();
    }
	
	public static HashMap<String, LinkedList<String>> getContactMessages(){
		return contactMassages;
	}
	
	public static void updateContactMessages(String contact, String message){
		if(contactMassages.containsKey(contact)){
			if(contactMassages.get(contact).size() == 50){
				contactMassages.get(contact).removeFirst();
				contactMassages.get(contact).add(message);
			} else {
				contactMassages.get(contact).add(message);
			}
		} else {
			LinkedList<String> newQueue = new LinkedList<String>();
			newQueue.add(message);
			contactMassages.put(contact, newQueue);
		}
		
		if(!currentContacts.contains(contact)){
			Log.i(TAG, "We have a new contact");
			currentContacts.addFirst(contact);
		} else {
			for(int i = 0; i < currentContacts.size(); ++i){
				if(currentContacts.get(i).equals(contact)){
					currentContacts.remove(i);
					break;
				}
			}
			currentContacts.addFirst(contact);
		}
		
		if(contact.equals(chattingWith)){
			for(int i = 0; i < unreadContacts.size(); ++i){
				if(unreadContacts.get(i).equals(contact)){
					unreadContacts.remove(i);
					break;
				}
			}
			ChatActivity.mHandler.removeMessages(999);
	        Message m = Message.obtain(ChatActivity.mHandler, 999);
	        ChatActivity.mHandler.sendMessageAtFrontOfQueue(m);
		} else {
			if(!unreadContacts.contains(contact)){
				unreadContacts.addFirst(contact);
			}
		}
		
		for(int i = 0; i < currentContacts.size(); ++i){
			Log.i(TAG, "A: " + currentContacts.get(i));
		}
		
		for(int i = 0; i < unreadContacts.size(); ++i){
			Log.i(TAG, "B: " + unreadContacts.get(i));
		}
	}
	
	public static LinkedList<String> getNotifContacts(){
		return unreadContacts;
	}
	
	public static int getUnread(){
		return unreadContacts.size();
	}
    
    public static XMPPConnection getXMPPConnection(){
    	return connection;
    }
    
	public static Zulu getApplication(){
	    return application;
	}
	
	public static ProfileRecord getRecord(){
		return ProfileRecord.getInstance();
	}
	  
	public void decreaseActivityCount(){
	    try
	    {
	      this.m_activityCount = (-1 + this.m_activityCount);
	      if (this.m_activityCount == 0)
	        exitApplication();
	      return;
	    }
	    finally {
	      //localObject = finally;
	      //throw localObject;
	    }
	}
	
	public void exitApplication(){
		System.gc();
		//Process.killProcess(Process.myPid());
	}

	public void onCreate(){
		super.onCreate();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		application = this;
		//DB.init(getSharedPreferences("com.yellowpages.android.ypmobile", 0));
		//_parseVersion();

		if (prefs.getBoolean(FIRST_LAUNCH, true)) {
			obtainID();		
		}
		
		ConnectionConfiguration connConfig = new ConnectionConfiguration(CHATSERVERIP, Integer.parseInt(PORT), Zulu.SERVICENAME);
		connection = new XMPPConnection(connConfig);
	}

	private void obtainID() {
	    /**
	     * Obtain visitor ID. If the ID is not in the record, fetch UUID from system service
	     */
	    //this.m_visitorId = RecordKeeper.getInstance().getString("visitorid");
	    if ((this.m_visitorId == null) || (this.m_visitorId.length() == 0))
	    {
	      String uuid = Settings.Secure.getString(getContentResolver(), "android_id");
	      if ("9774d56d682e549c".equals(uuid)) // Emulator 
	    	  uuid = null;
	      if (uuid == null)
	    	  uuid = ((TelephonyManager)getSystemService("phone")).getDeviceId();

	      this.m_visitorId = UUID.nameUUIDFromBytes(uuid.getBytes()).toString();
	      getRecord().setUserID(this.m_visitorId);
	    }
	}

	public static void setChattingwith(String selectedValue) {
		// TODO Auto-generated method stub
		chattingWith = selectedValue.trim();
	}

	public static NearbyUserDetails getSelectedNearbuZulu() {
		// TODO Auto-generated method stub
		return selectedNearbyZulu;
	}
}
