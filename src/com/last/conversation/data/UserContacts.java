package com.last.conversation.data;

import java.util.Date;

import android.graphics.Bitmap;

public class UserContacts {
	
	Bitmap contactPhoto;
	int id;
	String phoneNumber;
	String displayName;
	Date outGoing;
	Date incoming;
	Date missed;
	Date lastContacted;
	public Date getLastContacted() {
		return lastContacted;
	}
	public void setLastContacted(Date lastContacted) {
		this.lastContacted = lastContacted;
	}
	String sentMessage = "";
	String receivedMessage = "";
	String draftMessage = "";
	public String getDraftMessage() {
		return draftMessage;
	}
	public void setDraftMessage(String draftMessage) {
		this.draftMessage = draftMessage;
	}
	public String getSentMessage() {
		return sentMessage;
	}
	public void setSentMessage(String sentMessage) {
		this.sentMessage = sentMessage;
	}
	public String getReceivedMessage() {
		return receivedMessage;
	}
	public void setReceivedMessage(String receivedMessage) {
		this.receivedMessage = receivedMessage;
	}
	public Date getOutGoing() {
		return outGoing;
	}
	public void setOutGoing(Date outGoing) {
		this.outGoing = outGoing;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getIncoming() {
		return incoming;
	}
	public void setIncoming(Date incoming) {
		this.incoming = incoming;
	}
	public Date getMissed() {
		return missed;
	}
	public void setMissed(Date missed) {
		this.missed = missed;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public String getDisplayName() {
		return displayName;
	}
	public Bitmap getContactPhoto() {
		return contactPhoto;
	}
	public void setContactPhoto(Bitmap contactPhoto) {
		this.contactPhoto = contactPhoto;
	}	

}
