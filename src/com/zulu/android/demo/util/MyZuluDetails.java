package com.zulu.android.demo.util;

public class MyZuluDetails {
	
	private String mLatitude;
	private String mLongitude;
	private String mName;
	private String mZulu;
	private String mZuluid;
	private String mExpiration;
	
	public MyZuluDetails(String zulu, String zuluid){ //String lat, String lng, String expiration){
		this.mZulu = zulu;
		this.mZuluid = zuluid;
		//this.mExpiration = expiration;
		//this.mLatitude = lat;
		//this.mLongitude = lng;
	}
	
	public void setLati(String lat){
		this.mLatitude = lat;
	}
	
	public void setLong(String lng){
		this.mLongitude = lng;
	}
	
	public void setZulu(String zulu){
		this.mZulu = zulu;
	}
	
	public void setZuluid(String zuluid){
		this.mZuluid = zuluid;
	}
	
	public void setExit(String expiration) {
		this.mExpiration = expiration;
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

	public String getZulu() {
		return this.mZulu;
	}
	
	public String getZuluid() {
		return this.mZuluid;
	}
	
	public String getExit() {
		return this.mExpiration;
	}
}
