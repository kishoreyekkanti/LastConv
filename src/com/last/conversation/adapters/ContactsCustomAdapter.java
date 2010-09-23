package com.last.conversation.adapters;

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.last.converation.R;
import com.last.conversation.data.UserContacts;


public class ContactsCustomAdapter extends ArrayAdapter<UserContacts>{

	ArrayList<UserContacts> userContacts  = new ArrayList<UserContacts>();
	LayoutInflater mInflater = null;
	private final static String LOG_TAG = "Contacts Custom Adapter";
	public ContactsCustomAdapter(Context context, int textViewResourceId,
			ArrayList<UserContacts> userContacts) {
		super(context, textViewResourceId, userContacts);
		this.userContacts = userContacts;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		Log.d(LOG_TAG, "CUSTOM ADAPTER VIEW::"+convertView);
		if(convertView==null) {
			convertView = mInflater.inflate(R.layout.contacts, null);
		}
		UserContacts contact = userContacts.get(position);
		prepareTextView(convertView, contact);
		return convertView;
	}

	private void prepareTextView(View convertView, UserContacts contact) {
		//TODO will refactor this code with appropriate images in future
		if(contact != null) {
			TextView name = (TextView) convertView.findViewById(R.id.name);
			TextView phoneNumber = (TextView) convertView.findViewById(R.id.phone_number);
			TextView incoming = (TextView) convertView.findViewById(R.id.incoming);
			TextView outgoing = (TextView) convertView.findViewById(R.id.outgoing);
			TextView missed = (TextView) convertView.findViewById(R.id.missed);
			TextView inbox = (TextView) convertView.findViewById(R.id.inbox);
			TextView send = (TextView) convertView.findViewById(R.id.send);
			name.setText(contact.getDisplayName());
			phoneNumber.setText(contact.getPhoneNumber());
			inbox.setText("INBOX::"+contact.getReceivedMessage());
			send.setText("SENT::"+contact.getSentMessage());
			incoming.setText("INCOMING::");
			outgoing.setText("OUTGOING::");
			missed.setText("MISSED::");
			if(contact.getIncoming()!=null)
     			incoming.setText("INCOMING::"+DateFormat.getDateTimeInstance().format(contact.getIncoming()));
			if(contact.getOutGoing()!=null)
				outgoing.setText("OUTGOING::"+DateFormat.getDateTimeInstance().format(contact.getOutGoing()));
			if(contact.getMissed()!=null)
				missed.setText("MISSED::"+DateFormat.getDateTimeInstance().format(contact.getMissed()));
				
		}
	}
	
}
