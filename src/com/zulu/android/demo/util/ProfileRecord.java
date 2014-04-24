package com.zulu.android.demo.util;

public class ProfileRecord {
	
	public static ProfileRecord instance = null; //singleton
	
	private double mLatitude;
	private double mLongitude;
	private String mUUID = null;
	private String mName = null;
	private String mEmail = null;
	
	protected ProfileRecord() {
		// Exists only to defeat instantiation.
	}
	
	public static ProfileRecord getInstance() {
		if(instance == null) {
			instance = new ProfileRecord();
		}
		return instance;
	}
	
	public void setLati(double lati){
		this.mLatitude = lati;
	}
	
	public void setLong(double longi){
		this.mLongitude = longi;
	}
	
	public void setUserID(String uuid){
		this.mUUID = uuid;
	}
	
	public void setName(String name){
		this.mName = name;
	}
	
	public void setEmail(String email){
		this.mEmail = email;
	}
	
	public double getLati(){
		return this.mLatitude;
	}
	
	public double getLong(){
		return this.mLongitude;
	}
	
	public String getUserID(){
		return this.mUUID;
	}
	
	public String getName(){
		return this.mName;
	}
	
	public String getEmail(){
		return this.mEmail;
	}
}
