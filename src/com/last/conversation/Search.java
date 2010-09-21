package com.last.conversation;

import com.last.converation.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class Search extends Activity {
	
    private static final String LOG_TAG = "Search";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setUIBindings();
		Log.d(LOG_TAG, "Creating an instance of SEARCH activity");
	}

	private void setUIBindings() {
		Button button = (Button) findViewById(R.id.searchButton);
		button.setOnClickListener(searchButtonClickListener);
	}

	private OnClickListener searchButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View searchView) {
			EditText textBox = (EditText) findViewById(R.id.keywordBox);
			String searchBoxText = textBox.getText().toString();
			createNewActivityToListMessagesFrom(new String[]{searchBoxText});
		}
	};
	
	private void createNewActivityToListMessagesFrom(String[] keywords){
    	Intent listingActivity = new Intent(Search.this, LastConversation.class);
    	listingActivity.putExtra("keywords", keywords);
    	startActivity(listingActivity);
    }
	
}
