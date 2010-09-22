package com.last.conversation.data;

import java.util.Date;

public class UserContacts {
	
	int id;
	String phoneNumber;
	String displayName;
	Date outGoing;
	Date incoming;
	Date missed;
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
	

}
