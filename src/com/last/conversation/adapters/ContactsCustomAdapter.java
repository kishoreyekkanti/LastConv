package com.last.conversation.adapters;

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.last.converation.R;
import com.last.conversation.data.UserContacts;


public class ContactsCustomAdapter extends ArrayAdapter<UserContacts>{

	ArrayList<UserContacts> userContacts  = new ArrayList<UserContacts>();
	LayoutInflater mInflater;
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
		//TODO Need to refactor this code
		if(contact != null) {
			TextView name = (TextView) convertView.findViewById(R.id.name);
			TextView incoming = (TextView) convertView.findViewById(R.id.incoming);
			TextView outgoing = (TextView) convertView.findViewById(R.id.outgoing);
			TextView missed = (TextView) convertView.findViewById(R.id.missed);
			TextView inbox = (TextView) convertView.findViewById(R.id.inbox);
			TextView send = (TextView) convertView.findViewById(R.id.send);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
			String phoneNumber = contact.getPhoneNumber() != null ? contact.getPhoneNumber() : "";
			name.setText(contact.getDisplayName()+"\n"+phoneNumber);
			inbox.setText(contact.getReceivedMessage());
			send.setText(contact.getSentMessage());
			if(contact.getContactPhoto() == null ){
				imageView.setImageResource(R.drawable.user);
			}
			else{
				imageView.setImageBitmap(contact.getContactPhoto());			
			}
			incoming.setText("");
			outgoing.setText("");
			missed.setText("");
			if(contact.getIncoming()!=null)
     			incoming.setText(DateFormat.getDateTimeInstance().format(contact.getIncoming()));
			if(contact.getOutGoing()!=null)
				outgoing.setText(DateFormat.getDateTimeInstance().format(contact.getOutGoing()));
			if(contact.getMissed()!=null)
				missed.setText(DateFormat.getDateTimeInstance().format(contact.getMissed()));
				
		}
	}
	
}
