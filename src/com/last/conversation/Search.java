package com.last.conversation;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.last.converation.R;
import com.last.conversation.service.ContactsRetriever;

public class Search extends Activity {

	private static final String LOG_TAG = "Search";
	private ContactsRetriever contactsRetriever;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setUIBindings();
		setAutocompleteTextView();
		Log.d(LOG_TAG, "Created an instance of SEARCH activity");
	}

	private void setAutocompleteTextView() {
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_contact);
		String[] keywords = new String[]{textView.getText().toString()};
		contactsRetriever = new ContactsRetriever(keywords,getContentResolver());
		String[] contacts = contactsRetriever.fetchContactsForKeywords();
		ArrayAdapter<String> contactListAdapter = new ArrayAdapter<String>(this, R.layout.autocomplete_text, contacts);
		textView.setAdapter(contactListAdapter);
	}

	private void setUIBindings() {
		Button button = (Button) findViewById(R.id.searchButton);
		button.setOnClickListener(searchButtonClickListener);
	}

	private OnClickListener searchButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View searchView) {
			EditText textBox = (EditText) findViewById(R.id.autocomplete_contact);
			String searchBoxText = textBox.getText().toString();
			createNewActivityToListMessagesFrom(new String[] { searchBoxText });
		}
	};

	private void createNewActivityToListMessagesFrom(String[] keywords) {
		Intent listingActivity = new Intent(Search.this, LastConversation.class);
		listingActivity.putExtra("keywords", keywords);
		startActivity(listingActivity);
	}

}
