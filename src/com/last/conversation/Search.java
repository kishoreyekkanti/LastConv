package com.last.conversation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView.OnItemClickListener;

import com.last.converation.R;
import com.last.conversation.service.ContactsRetriever;

public class Search extends Activity {

	private static final String LOG_TAG = "Search";
	private ContactsRetriever contactsRetriever;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setAutocompleteTextView();
		Log.d(LOG_TAG, "Created an instance of SEARCH activity");
	}

	private void setAutocompleteTextView() {
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_contact);
		setUIBindings(textView);
		String[] keywords = new String[] { textView.getText().toString() };
		contactsRetriever = new ContactsRetriever(keywords,
				getContentResolver());
		String[] contacts = contactsRetriever.fetchContactsForKeywords();
		ArrayAdapter<String> contactListAdapter = new ArrayAdapter<String>(
				this, R.layout.autocomplete_text, contacts);
		textView.setAdapter(contactListAdapter);
	}

	private void setUIBindings(AutoCompleteTextView textView) {
		textView.setOnItemClickListener(autoCompleteItemClickListener);
		textView.setOnClickListener(autoCompleteClickListener);
		textView.setOnKeyListener(autoCompleteEnterKeyListener);
	}

	private OnKeyListener autoCompleteEnterKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				searchForContacts();
				return true;
			}
			return false;
		}

	};
	private OnItemClickListener autoCompleteItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			searchForContacts();
		}
	};
	private OnClickListener autoCompleteClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			searchForContacts();
		}
	};

	private void searchForContacts() {
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_contact);
		String[] keywords = new String[] { textView.getText().toString() };
		createNewActivityToListMessagesFrom(keywords);
	}

	private void createNewActivityToListMessagesFrom(String[] keywords) {
		Intent listingActivity = new Intent(Search.this, LastConversation.class);
		listingActivity.putExtra("keywords", keywords);
		startActivity(listingActivity);
	}

}
