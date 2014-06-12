package com.example.android_projekt.individ;

import com.example.android_projekt.R;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.productionsite.ProductionSiteNr;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.os.Build;

public class IndividualActivity extends ActionBarActivity 
{
	private final static String TAG = "Brunst: IndividualActivity";
	
	public final static String EXTRA_PRODUCTION_SITE_NR = "brunst.extra.IndividualActivity.ProductionSiteNr";
	
	// WIDGETS
	private EditText etIdnrOrg;
	private EditText etIdnrPpnr;
	private EditText etIdnrIndividnr;
	private EditText etIdnrChecknr;
	private EditText etShortnr;
	
	private ProductionSiteNr currentSiteNr;
	private boolean updating = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual);
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_NR)) {
			currentSiteNr = (ProductionSiteNr) intent.getSerializableExtra(EXTRA_PRODUCTION_SITE_NR);
			Log.d(TAG, "received current site nr: " + currentSiteNr.toString());
		}
		
		findViews();
		prepareViews();
		
		setupListeners();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.individual, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Get handles to the different widgets
	 */
	private void findViews() {
		etIdnrOrg = (EditText) findViewById(R.id.individual_entry_id_org);
		etIdnrPpnr = (EditText) findViewById(R.id.individual_entry_id_ppnr);
		etIdnrIndividnr = (EditText) findViewById(R.id.individual_entry_id_individnr);
		etIdnrChecknr = (EditText) findViewById(R.id.individual_entry_id_checknr);
		etShortnr = (EditText) findViewById(R.id.individual_entry_shortnr);
	}
	
	/**
	 * Pre-fill some information and set enabled state of widgets
	 */
	private void prepareViews() {
		if(currentSiteNr != null && !updating) {
			etIdnrOrg.setText(currentSiteNr.getOrg());
			etIdnrPpnr.setText(currentSiteNr.getPpnr());
		}
	}
	
	/**
	 * Prepare different listeners on widgets for events.
	 */
	private void setupListeners() {
//		setupOnEditorActionListeners();
		setupOnFocusChangeListeners();
		
	}

	/**
	 * Perform an action when widget loses focus
	 */
	private void setupOnFocusChangeListeners() {
		etIdnrIndividnr.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// if we lose focus and shortnr field is empty
				if(!hasFocus && etShortnr.getText().length() == 0) {
					createShortnr();
				}
			}
		});
	}

//	/**
//	 * Have widgets reposnd to certain events in the keyboard.
//	 */
//	private void setupOnEditorActionListeners() {
//		etIdnrIndividnr.setOnEditorActionListener(new OnEditorActionListener() {
//			@Override
//			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//				boolean handled = false;
//				if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//					createShortnr();
//					handled = true;
//				}
//				return handled;
//			}
//		});
//		
//	}

	/**
	 * Create a shortnr from the Individnr and set the shortnr field
	 */
	protected void createShortnr() {
		Log.d(TAG, "Trying to createshortnr");
		try {
			int nr = Integer.parseInt(etIdnrIndividnr.getText().toString());
			etShortnr.setText(nr + "");
		}
		catch (NumberFormatException ignore) {
			Log.d(TAG, ignore.getMessage());
		};
	}

}
