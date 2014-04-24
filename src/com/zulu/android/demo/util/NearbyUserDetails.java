package com.zulu.android.demo.util;

public class NearbyUserDetails {
	
	private String mLatitude;
	private String mLongitude;
	private String mName;
	private String mStatus;
	private String mUsername;
	
	public NearbyUserDetails(String name, String status, String lat, String lng, String email){
		this.mName = name;
		this.mStatus = status;
		this.mLatitude = lat;
		this.mLongitude = lng;
		this.mUsername = email.split("@")[0];
	}
	
	public void setLati(String lat){
		this.mLatitude = lat;
	}
	
	public void setLong(String lng){
		this.mLongitude = lng;
	}
	
	public void setName(String name){
		this.mName = name;
	}
	
	public void setStatus(String status) {
		this.mStatus = status;
	}
	
	public String getUsername(){
		return this.mUsername;
	}
	
	public String getLati(){
		return this.mLatitude;
	}
	
	public String getLong(){
		return this.mLongitude;
	}
	
	public String getName(){
		return this.mName;
	}

	public String getStatus() {
		return this.mStatus;
	}
}
