package com.zulu.android.demo.util;

public class ChatMessage {
	
	private String mSender;
	private String mContent;
	
	public ChatMessage(String content, String sender) {
		this.mContent = content;
		this.mSender = sender;
	}

	public void setSender(String sender){
		this.mSender = sender;
	}
	
	public void setCont(String content){
		this.mContent = content;
	}
	
	public String getSender(){
		return this.mSender;
	}
	
	public String getCont(){
		return this.mContent;
	}
}
