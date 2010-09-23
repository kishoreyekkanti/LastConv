package com.last.conversation.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.CallLog.Calls;
import android.util.Log;

import com.last.conversation.data.UserContacts;

public class ContactsRetriever {
	private String LOG_TAG = "ContactsRetriever";
	private String[] keywords;
	private ContentResolver contentResolver;
	private static HashMap<String,String> SMS_URI = new HashMap<String,String>();
	protected static String _ID = "_id";
	protected static String BODY = "body";
	protected static String DATE = "date";
	
	public ContactsRetriever(String[] keywords, ContentResolver contentResolver) {
		this.keywords = keywords;
		this.contentResolver = contentResolver;
		SMS_URI.put("INBOX_URI", "content://sms/inbox");
		SMS_URI.put("SENT_URI", "content://sms/sent");
	}

	public ArrayList<UserContacts> fetchContacts() {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { 
				              ContactsContract.Contacts._ID,
							  ContactsContract.Contacts.DISPLAY_NAME,
							  ContactsContract.Contacts.HAS_PHONE_NUMBER,
							  ContactsContract.Contacts.LAST_TIME_CONTACTED };
		String[] selectionArgs = new String[] { createQueryableKeyword(keywords) };
		String sortOrder = null;
		Cursor contacts = null;
		try {
			contacts = contentResolver.query(
						uri, 
						projection,
					    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? ",
					    selectionArgs, 
					    sortOrder);
			if (contacts != null && contacts.moveToFirst()) {
				return extract(contacts);
			}
		} finally {
			contacts.close();
		}
		return new ArrayList<UserContacts>();
	}

	private String createQueryableKeyword(String[] keywords) {
		StringBuffer keyword = new StringBuffer("%");
		for (int i = 0; i < keywords.length; i++) {
			keyword.append(keywords[i]);
			keyword.append("%");
		}
		return keyword.toString();
	}

	private ArrayList<UserContacts> extract(Cursor contacts) {
		ArrayList<UserContacts> userContacts = new ArrayList<UserContacts>();
		if (contacts.moveToFirst()) {
			do {
				UserContacts uContact = new UserContacts();
				setUserNameAndContactId(contacts, uContact);
				if (isContactHavePhoneNumber(contacts)) {
					Cursor pCur = contentResolver.query(
										ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", 
										new String[] { String.valueOf(uContact.getId()) }, 
										null);
					setPhoneNumber(uContact, pCur);
					pCur.close();
					setCallTypes(uContact);
					setSMS(uContact);
				}
				Log.d(LOG_TAG,"USER CONTACT::"+userContacts);
				userContacts.add(uContact);
			} while (contacts.moveToNext());
		}
		return userContacts;
	}

	private void setSMS(UserContacts uContact) {
		uContact.setSentMessage(getSMSFor(uContact.getId(),SMS_URI.get("SENT_URI")));
		uContact.setReceivedMessage(getSMSFor(uContact.getId(),SMS_URI.get("INBOX_URI")));
	}

	private String getSMSFor(int id,String contentURI) {
		Cursor smsCursor = contentResolver.query(getContentURI(contentURI), fieldsToFetch(),
					whereClause(id), selectionArguments(), sortOrder());

		int bodyIndex = smsCursor.getColumnIndex(BODY);
		String message = "";
		if(smsCursor!=null && smsCursor.moveToFirst()){
			message = smsCursor.getString(bodyIndex);
		}
		Log.d(LOG_TAG,"CONTENT URI::"+contentURI+" MESSAGE:: "+message);
		smsCursor.close();
		return message;
	}
	
	protected Uri getContentURI(String contentURI) {
		return Uri.parse(contentURI);
	}

	protected String[] fieldsToFetch() {
		return new String[] { _ID, BODY };
	}

	protected String whereClause(int contactId) {
		return " body is not null and _id="+contactId;
	}

	protected String sortOrder() {
		return null;
	}

	protected String[] selectionArguments() {
		return null;
	}

	private void setCallTypes(UserContacts uContact) {
		Cursor managedCursor = getCallLogFor(uContact.getId());
		int typeColumn = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		Log.d(LOG_TAG,"NAME::"+uContact.getDisplayName()+" MANAGED CURSOR ::"+managedCursor.getCount());
		if (managedCursor!=null && managedCursor.moveToFirst()) {
			int type = Integer.parseInt(managedCursor.getString(typeColumn));
			try {
				switch (type) {
				case CallLog.Calls.OUTGOING_TYPE:
					uContact.setOutGoing(getCallDate(managedCursor));
					break;
				case CallLog.Calls.INCOMING_TYPE:
					uContact.setIncoming(getCallDate(managedCursor));
					break;
				case CallLog.Calls.MISSED_TYPE:
					uContact.setMissed(getCallDate(managedCursor));
					break;
				}
			} catch (NumberFormatException ex) {
				Log.e(LOG_TAG,ex.getMessage());
			}
	  }
		managedCursor.close();
	}

	private Cursor getCallLogFor(int contactId) {
		String[] projection = new String[] {BaseColumns._ID,CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME };
		Cursor managedCursor = contentResolver.query(
				                android.provider.CallLog.Calls.CONTENT_URI, 
								projection, 
								CallLog.Calls._ID +" = ? ",
								new String[]{String.valueOf(contactId)},
								CallLog.Calls.DATE + " DESC ");
		return managedCursor;
	}

	private Date getCallDate(Cursor managedCursor) {
		return new Date(managedCursor.getLong(managedCursor.getColumnIndex(Calls.DATE)));
	}

	private void setPhoneNumber(UserContacts uContact, Cursor pCur) {
		while (pCur.moveToNext()) {
			String number = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			uContact.setPhoneNumber(number);
			Log.d(LOG_TAG, "Number::" + number);
		}
	}

	private boolean isContactHavePhoneNumber(Cursor contacts) {
		return Integer.parseInt(contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0;
	}

	private void setUserNameAndContactId(Cursor contacts, UserContacts uContact) {
		String id = contacts.getString(contacts
				.getColumnIndex(ContactsContract.Contacts._ID));
		String name = contacts
				.getString(contacts
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		uContact.setDisplayName(name);
		uContact.setId(Integer.parseInt(id));
		Log.d(LOG_TAG, "CONTACT ID:)::" + id + "  NAME::" + name);
	}

}
