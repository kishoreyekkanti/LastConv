package com.last.conversation;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.last.converation.R;
import com.last.conversation.adapters.ContactsCustomAdapter;
import com.last.conversation.data.UserContacts;
import com.last.conversation.service.ContactsRetriever;

public class LastConversation extends Activity {

	
	private ArrayList<UserContacts> contacts = new ArrayList<UserContacts>();
	private ArrayAdapter<UserContacts> contactListAdapter = null;
	private ContactsRetriever contactsRetriever = null;
	private final static String LOG_TAG = "Last Conversation";
	private ListView listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);
        String[] keywords = getIntent().getStringArrayExtra("keywords");
        contactsRetriever = new ContactsRetriever(keywords,getContentResolver());
        new ContactsFetchAsyncTask().execute();
    }
	private class ContactsFetchAsyncTask extends AsyncTask<Void, Void, ArrayList<UserContacts>> {
		private final ProgressDialog dialog = new ProgressDialog(LastConversation.this);

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Searching...");
			this.dialog.show();
		}
		
		@Override
		protected ArrayList<UserContacts> doInBackground(Void... params) {
			ArrayList<UserContacts> contactsReturned = LastConversation.this.contactsRetriever.fetchContacts();
			Log.d(LOG_TAG, "CONTACTS RETURNED::"+contactsReturned.size());
			return contactsReturned;
		}
		
		@Override
		protected void onPostExecute(ArrayList<UserContacts> result) {
		    LastConversation.this.contacts.addAll(result);
			LastConversation.this.updateListingContentsWith();

			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
		}
	}

	private void updateListingContentsWith() {
		contactListAdapter = new ContactsCustomAdapter(this,R.layout.contacts,contacts);
		listView=(ListView)findViewById(R.id.contact_list_view);
		listView.setAdapter(contactListAdapter);
	}
	
}