package com.last.conversation.service;

import java.util.ArrayList;
import java.util.Date;

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

	public ContactsRetriever(String[] keywords, ContentResolver contentResolver) {
		this.keywords = keywords;
		this.contentResolver = contentResolver;
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
		if (contacts.getCount() > 0) {
			do {
				UserContacts uContact = new UserContacts();
				String id = setUserName(contacts, uContact);
				if (isContactHavePhoneNumber(contacts)) {
					Cursor pCur = contentResolver.query(
										ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", 
										new String[] { id }, 
										null);
					setPhoneNumber(uContact, pCur);
					pCur.close();
					setCallTypes(uContact);
				}
				Log.d(LOG_TAG,"USER CONTACT::"+userContacts);
				userContacts.add(uContact);
			} while (contacts.moveToNext());
		}
		return userContacts;
	}

	private void setCallTypes(UserContacts uContact) {
		Cursor managedCursor = getCallLogFor(uContact.getPhoneNumber());
		int typeColumn = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		Log.d(LOG_TAG,"NAME::"+uContact.getDisplayName()+" MANAGED CURSOR ::"+managedCursor.getCount());
		while (managedCursor.moveToNext()) {
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

	private Cursor getCallLogFor(String phoneNumber) {
		String[] projection = new String[] {BaseColumns._ID,CallLog.Calls.TYPE, CallLog.Calls.DATE };
		Cursor managedCursor = contentResolver.query(
								CallLog.Calls.CONTENT_URI, 
								projection, 
								CallLog.Calls.NUMBER +" = ? ",
								new String[]{phoneNumber},
								null);
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

	private String setUserName(Cursor contacts, UserContacts uContact) {
		String id = contacts.getString(contacts
				.getColumnIndex(ContactsContract.Contacts._ID));
		String name = contacts
				.getString(contacts
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		uContact.setDisplayName(name);
		Log.d(LOG_TAG, "ID:)::" + id + "  NAME::" + name);
		return id;
	}

}