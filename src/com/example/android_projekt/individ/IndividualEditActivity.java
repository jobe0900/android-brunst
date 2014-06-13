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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.os.Build;


public class IndividualEditActivity extends ActionBarActivity 
{
	private final static String TAG = "Brunst: IndividualEditActivity";
	
	public final static String EXTRA_PRODUCTION_SITE_NR = "brunst.extra.IndividualActivity.ProductionSiteNr";
	
	// WIDGETS
	private EditText etIdnrOrg;
	private EditText etIdnrPpnr;
	private EditText etIdnrIndividnr;
	private EditText etIdnrChecknr;
	private EditText etShortnr;
	private Spinner  spinSex;
	
	private ProductionSiteNr currentSiteNr;
	private boolean updating = false;
	private boolean female = true;
	
	private IndividualDB individualDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_edit);
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_NR)) {
			currentSiteNr = (ProductionSiteNr) intent.getSerializableExtra(EXTRA_PRODUCTION_SITE_NR);
			Log.d(TAG, "received current site nr: " + currentSiteNr.toString());
		}
		
//		individualDB = new IndividualDB(this);
		
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
	
	@Override
	protected void onResume() {
//		individualDB.open();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
//		individualDB.close();
		super.onPause();
	}
	
	/**
	 * Get handles to the different widgets
	 */
	private void findViews() {
		etIdnrOrg = (EditText) findViewById(R.id.individual_edit_entry_id_org);
		etIdnrPpnr = (EditText) findViewById(R.id.individual_edit_entry_id_ppnr);
		etIdnrIndividnr = (EditText) findViewById(R.id.individual_edit_entry_id_individnr);
		etIdnrChecknr = (EditText) findViewById(R.id.individual_edit_entry_id_checknr);
		etShortnr = (EditText) findViewById(R.id.individual_edit_entry_shortnr);
		spinSex = (Spinner) findViewById(R.id.individual_edit_spinner_sex);
	}
	
	/**
	 * Pre-fill some information and set enabled state of widgets
	 */
	private void prepareViews() {
		setupSpinners();
		if(currentSiteNr != null && !updating) {
			etIdnrOrg.setText(currentSiteNr.getOrg());
			etIdnrPpnr.setText(currentSiteNr.getPpnr());
		}
		if(updating) {
			spinSex.setEnabled(false); 	// should not be able to change sex...
		}
	}
	
	/**
	 * Prepare different listeners on widgets for events.
	 */
	private void setupListeners() {
		setupOnFocusChangeListeners();
		setupOnItemSelectedListeners();
	}

	/**
	 * Populate spinners from arrays.
	 */
	private void setupSpinners() {
		// SEX
		ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(
				this, R.array.sex, R.layout.simple_spinner_item);
		spinSex.setAdapter(sexAdapter);
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
	
	/**
	 * Listen for selection in spinners.
	 */
	private void setupOnItemSelectedListeners() {
		spinSex.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selected = (String) parent.getItemAtPosition(position);
				if(selected.equals(getString(R.string.individual_sex_male))) {
					female = false;
				}
				else {
					female = true;
				}
				// update visibility of widgets
				setWidgetVisibility();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// NOTHING
			}
		});
		
	}

	/**
	 * Disable all widgets not relevant for a Male Individual.
	 */
	protected void setWidgetVisibility() {
		// TODO Auto-generated method stub
		
	}

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
	
	/**
	 * Find if the Individual is Male or Female.
	 * @return
	 */
	private boolean isFemale() {
		return female;
	}

}
