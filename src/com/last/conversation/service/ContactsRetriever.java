package com.last.conversation.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.CallLog.Calls;
import android.util.Log;

import com.last.conversation.data.UserContacts;

public class ContactsRetriever {
	private String LOG_TAG = "ContactsRetriever";
	private String[] keywords;
	private ContentResolver contentResolver;
	private static HashMap<String, String> SMS_URI = new HashMap<String, String>();
	protected static String _ID = "_id";
	protected static String BODY = "body";
	protected static String DATE = "date";
	protected static String ADDRESS = "address";
	protected static String PERSON = "person";

	public ContactsRetriever(String[] keywords, ContentResolver contentResolver) {
		this.keywords = keywords;
		this.contentResolver = contentResolver;
		SMS_URI.put("INBOX_URI", "content://sms/inbox");
		SMS_URI.put("SENT_URI", "content://sms/sent");
	}

	public ArrayList<UserContacts> fetchLastConversationDetails() {
		Cursor contacts = fetchContacts();
		try {
			if (contacts != null && contacts.moveToFirst()) {
				return extract(contacts, true);
			}
		} finally {
			contacts.close();
		}
		return new ArrayList<UserContacts>();
	}

	public String[] fetchContactsForKeywords() {
		Cursor contacts = fetchContacts();
		ArrayList<UserContacts> uContacts = new ArrayList<UserContacts>();
		try {
			if (contacts != null && contacts.moveToFirst()) {
				uContacts = extract(contacts, false);
			}
		} finally {
			contacts.close();
		}
		String[] names = new String[uContacts.size()];
		for (int i = 0; i < uContacts.size(); i++) {
			names[i] = uContacts.get(i).getDisplayName();
		}
		return names;
	}

	private Cursor fetchContacts() {
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER,
				ContactsContract.Contacts.LAST_TIME_CONTACTED };
		
		String[] selectionArgs = new String[] { createQueryableKeyword(keywords) };
		String sortOrder = null;
		Cursor contacts = contentResolver.query(uri, projection,
				ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? ",
				selectionArgs, sortOrder);
		return contacts;
	}
	
	private ArrayList<UserContacts> extract(Cursor contacts,
			boolean shouldFetchPhoneDetails) {
		ArrayList<UserContacts> userContacts = new ArrayList<UserContacts>();
		if (contacts.moveToFirst()) {
			do {
				UserContacts uContact = new UserContacts();
				setDisplayNameAndContactId(contacts, uContact);
				setPhoto(uContact);
				if (isContactHavePhoneNumber(contacts)
						&& shouldFetchPhoneDetails) {
					Log.d(LOG_TAG,"Contact have phone numbers. Fetching phone numbers and call details..");
					Cursor pCur = fetchPhoneNumber(uContact.getId());
					setCallDetails(uContact, pCur);
					pCur.close();
					setCallTypes(uContact);
					setSMS(uContact);
				}
				Log.d(LOG_TAG, "USER CONTACT::" + userContacts);
				userContacts.add(uContact);
			} while (contacts.moveToNext());
		}
		return userContacts;
	}

	private void setPhoto(UserContacts uContact) {
		Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, uContact.getId()); 
		InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
		if(inputStream!=null){
			uContact.setContactPhoto(BitmapFactory.decodeStream(inputStream));	
		}
	}

	private Cursor fetchPhoneNumber(int contactId) {
		return contentResolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
				new String[] { String.valueOf(contactId) }, ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED);
	}

	private void setSMS(UserContacts uContact) {
		uContact.setSentMessage(getSMSFor(uContact.getPhoneNumber(), SMS_URI
				.get("SENT_URI")));
		uContact.setReceivedMessage(getSMSFor(uContact.getPhoneNumber(), SMS_URI
				.get("INBOX_URI")));
	}

	private String getSMSFor(String addressId, String contentURI) {
		Cursor smsCursor = contentResolver.query(getContentURI(contentURI),
				fieldsToFetch(), 
				whereClause(), selectionArguments(addressId),
				sortOrder());

		int bodyIndex = smsCursor.getColumnIndex(BODY);
		String message = "";
		if (smsCursor != null && smsCursor.moveToFirst()) {
			message = smsCursor.getString(bodyIndex);
		}
		Log.d(LOG_TAG, "CONTENT URI::" + contentURI + " MESSAGE:: " + message);
		smsCursor.close();
		return message;
	}
	
	private String createQueryableKeyword(String[] keywords) {
		StringBuffer keyword = new StringBuffer("%");
		for (int i = 0; i < keywords.length; i++) {
			keyword.append(keywords[i]);
			keyword.append("%");
		}
		return keyword.toString();
	}
	
	private void setCallTypes(UserContacts uContact) {
		Cursor managedCursor = getCallLogFor(uContact.getDisplayName());
		int typeColumn = managedCursor.getColumnIndex(CallLog.Calls.TYPE);		
		Log.d(LOG_TAG, "NAME::" + uContact.getDisplayName() + "ID::"+ uContact.getId() +" PHONE NUMBER::"+ uContact.getPhoneNumber()
				+"NAME::"+uContact.getDisplayName()+ " MANAGED CURSOR ::" + managedCursor.getCount());
		if (managedCursor != null && managedCursor.moveToFirst()) {
			int type = Integer.parseInt(managedCursor.getString(typeColumn));
			Log.d(LOG_TAG,"CALL LOG ID::"+managedCursor.getInt(managedCursor.getColumnIndex(CallLog.Calls._ID)));
			Log.d(LOG_TAG,"CALL LOG PHONE NUMBER::"+managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
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
				Log.e(LOG_TAG, ex.getMessage());
			}
		}
		managedCursor.close();
	}

	private Cursor getCallLogFor(String name) {
		String[] projection = new String[] { CallLog.Calls._ID,
				CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.NUMBER,
				CallLog.Calls.CACHED_NAME };
		Cursor managedCursor = contentResolver.query(
				android.provider.CallLog.Calls.CONTENT_URI, projection,
				CallLog.Calls.CACHED_NAME + " = ? ", new String[] { String.valueOf(name) },
				CallLog.Calls.DATE + " DESC ");
		return managedCursor;
	}

	private Date getCallDate(Cursor managedCursor) {
		return new Date(managedCursor.getLong(managedCursor
				.getColumnIndex(Calls.DATE)));
	}

	private void setCallDetails(UserContacts uContact, Cursor pCur) {
		if (pCur.moveToNext()) {
			String number = pCur
					.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			Date lastContacted = new Date(pCur.getLong(pCur.getColumnIndex(
								 ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED)));
			uContact.setPhoneNumber(number);
			uContact.setLastContacted(lastContacted);
			Log.d(LOG_TAG, "Last Contacted::" + lastContacted);
		}
	}

	private boolean isContactHavePhoneNumber(Cursor contacts) {
		return Integer.parseInt(contacts.getString(contacts
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0;
	}

	private void setDisplayNameAndContactId(Cursor contacts,
			UserContacts uContact) {
		String id = contacts.getString(contacts
				.getColumnIndex(ContactsContract.Contacts._ID));
		String name = contacts.getString(contacts
				.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		uContact.setDisplayName(name);
		uContact.setId(Integer.parseInt(id));
		Log.d(LOG_TAG, "CONTACT ID:)::" + id + "  NAME::" + name);
	}
	
	protected Uri getContentURI(String contentURI) {
		return Uri.parse(contentURI);
	}

	protected String[] fieldsToFetch() {
		return new String[] { _ID, BODY, ADDRESS,PERSON};
	}

	protected String whereClause() {
		return " body is not null and address = ?";
	}

	protected String sortOrder() {
		return null;
	}

	protected String[] selectionArguments(String addressId) {
		return new String[]{addressId};
	}
	

}
