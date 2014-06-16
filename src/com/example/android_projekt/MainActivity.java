package com.example.android_projekt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.android_projekt.individ.Individual;
import com.example.android_projekt.individ.IndividualEditActivity;
import com.example.android_projekt.productionsite.ProductionSite;
import com.example.android_projekt.productionsite.ProductionSiteActivity;
import com.example.android_projekt.productionsite.ProductionSiteDB;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.os.Build;

public class MainActivity extends ActionBarActivity 
{	
	public static final String EXTRA_SITE_UPDATED = "brunst.extra.MainActivity.siteUpdate";
	public static final String EXTRA_INDIVIDUAL_UPDATED = "brunst.extra.MainActivity.individualUpdate";
	private static final String TAG = "Brunst: MAIN";
	private static final int DB_LOADER = 0;
	
	private Spinner productionSiteSpinner;
	private ArrayAdapter<String> productionSiteSpinnerAdapter;
	
	private ImageButton productionSiteButtonNew;
	private ImageButton productionSiteButtonEdit;
	private ImageButton individualButtonNew;
	private ImageButton individualButtonEdit;
	
	private OnClickListener clickListener;
	private OnItemSelectedListener productionSiteSpinnerListener;
	
//	private List<ProductionSite> productionSites;
	private List<String> productionSitesList;
	private String selectedSiteStr;
	
	private List<String> indivdualsList;
	private String selectedIndividualStr;
	
	
//	private ProductionSite currentSite;
//	private ProductionSite updatedSite = null;
//	private Individual updatedIndividual = null;
	
	
//	private String updatedSiteStr;
	
//	private String updatedIndividua

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		productionSiteSpinner = (Spinner) findViewById(R.id.main_spinner_production_site);
		
//		productionSiteButtonNew = (Button) findViewById(R.id.main_btn_production_site_new);
		productionSiteButtonNew = (ImageButton) findViewById(R.id.main_imgbutton_site_new);
//		productionSiteButtonEdit = (Button) findViewById(R.id.main_btn_production_site_edit);
		productionSiteButtonEdit = (ImageButton) findViewById(R.id.main_imgbutton_site_edit);
//		individualButtonNew = (Button) findViewById(R.id.main_btn_individual_new);
		individualButtonNew = (ImageButton) findViewById(R.id.main_imgbutton_individual_new);
		individualButtonEdit = (ImageButton) findViewById(R.id.main_imgbutton_individual_edit);
		
		if(getIntent().hasExtra(EXTRA_SITE_UPDATED)) {
//			updatedSite = (ProductionSite) getIntent().getSerializableExtra(EXTRA_SITE_UPDATED);
			selectedSiteStr = getIntent().getStringExtra(EXTRA_SITE_UPDATED);	
		}
		if(getIntent().hasExtra(EXTRA_INDIVIDUAL_UPDATED)) {
//			updatedIndividual = (Individual) getIntent().getSerializableExtra(EXTRA_INDIVIDUAL_UPDATED);
//			updatedSite = new ProductionSite(updatedIndividual.getHomesiteNr());
			selectedIndividualStr = getIntent().getStringExtra(EXTRA_INDIVIDUAL_UPDATED);
		}
		
//		setImageButtonEnabled(productionSiteButtonEdit, false);
		
		fillSpinners();
		
		setupClickListeners();
		setUpSpinnerListeners();
		
		updateEnabledWidgets();
		
		
//		productionSiteButtonEdit.setEnabled(false);
//		productionSiteButtonEdit.setAlpha(128);
		
	}

	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		fillSpinners();	// need this?
	}
	
	/**
	 * Change appearance of button depending on state
	 * @param button
	 * @param enabled
	 */
	private void setImageButtonEnabled(ImageButton button, boolean enabled) {
		button.setEnabled(enabled);
		int alpha = enabled ? 255 : 128;
		button.setAlpha(alpha);
	}
	
	/**
	 * Change the enabled state / visibility depending on selected spinners.
	 */
	private void updateEnabledWidgets() {
		// TODO Auto-generated method stub
		// discover which state....
		
	}

	/**
	 * Fill the spinners with data from the database.
	 */
	private void fillSpinners() {
		fillProductionSiteSpinner();
//		fillIndividSpinner();
	}

	/**
	 * Kick off an AsyncTask to load ProductionSite data.
	 */
	private void fillProductionSiteSpinner() {
		new LoadProductionSiteSpinnerDataTask().execute(ProductionSiteDB.TABLE_NAME);
	}
	
	/**
	 * Kick off an AsyncTask to load Individual data for current site.
	 */
	private void fillIndividSpinner() {
		// TODO
//		if(currentSite != null) {
//			//TODO kick of the background task to load the spinner data
////			new LoadSpinnerDataTask().execute(IndividualDB.TABLE_NAME, siteNr);
//		}
	}
	
	
//	/**
//	 * Get the site that is selected in the ProductionSite spinner.
//	 * @return	A ProductionSite or null if none selected
//	 */
//	private ProductionSite getSelectedProductionSite() {
//		// first check if there is a site selected in ProductionSiteSpinner
//		String siteString = (String) productionSiteSpinner.getSelectedItem();
//		ProductionSite site = null;
//		String siteNr = null;
//		// if there is a site selected (and not just the title for the spinner)
//		if(siteString != null && !siteString.equalsIgnoreCase(getString(R.string.production_site))) {
//			Log.d(TAG, "The line in spinner: " + siteString);
//			// get the sitenr
//			siteNr = siteString.split("\\s+")[0]; // get the nr part of the line
//			Log.d(TAG, "The number part: " + siteNr);
//			for(ProductionSite ps : productionSites) {
//				Log.d(TAG, "comparing: " + siteNr + " to " + ps.getSiteNr().toString());
//				if(ps.getSiteNr().toString().equals(siteNr)) {
//					site = ps;
//					break;
//				}
//			}
//		}
//		return site;
//	}
	
	/**
	 * Get the selected ProductionSite as a string with the ProductionSiteNr
	 * @return	ProductionSiteNr as string
	 */
	private String getSelectedProductionSiteNrAsString() {
		String siteNrString = null;
		String siteTitle = (String) productionSiteSpinner.getSelectedItem();
		// if something is selected
		if(siteTitle != null) {
			siteNrString = siteTitle.split("\\s+")[0];	// the nr part of the title
		}
		return siteNrString;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	 * Set up OnClickListeners for the buttons
	 */
	private void setupClickListeners() {
		clickListener = new OnClickListener() {
			Intent intent;
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
//				case R.id.main_btn_production_site_edit:
				case R.id.main_imgbutton_site_edit:
					intent = new Intent(getApplicationContext(), ProductionSiteActivity.class);
					Log.d(TAG, "sending site: " + selectedSiteStr);
					if(selectedSiteStr != null) {
						intent.putExtra(ProductionSiteActivity.EXTRA_PRODUCTION_SITE, getSelectedProductionSiteNrAsString());
					}
//					if(currentSite != null) {
//						intent.putExtra(ProductionSiteActivity.EXTRA_PRODUCTION_SITE, currentSite);
//					}
					startActivity(intent);
					break;
//				case R.id.main_btn_production_site_new:
				case R.id.main_imgbutton_site_new:
					intent = new Intent(getApplicationContext(), ProductionSiteActivity.class);
					startActivity(intent);
					break;
				case R.id.main_imgbutton_individual_new:
					intent = new Intent(getApplicationContext(), IndividualEditActivity.class);
//					if(currentSite != null) {
//						intent.putExtra(IndividualEditActivity.EXTRA_PRODUCTION_SITE_NR, currentSite.getSiteNr());
//					}
//					startActivity(intent);
					break;
				case R.id.main_imgbutton_individual_edit:
					intent = new Intent(getApplicationContext(), IndividualEditActivity.class);
//					if()
					// TODO
					break;
				}
			}
		};
		productionSiteButtonEdit.setOnClickListener(clickListener);
		productionSiteButtonNew.setOnClickListener(clickListener);
		individualButtonNew.setOnClickListener(clickListener);
	}
	
	/**
	 * Set up OnItemSelectedListeners for the spinners.
	 */
	private void setUpSpinnerListeners() {
		// Listener for the ProductionSiteSpinner
		productionSiteSpinnerListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selected = (String) parent.getItemAtPosition(position);
				Log.d(TAG, "PS Spinner Listener selected: " + selected);
//				if(!selected.equals(getString(R.string.production_site))) {
//					Log.d(TAG, "PS Spinner Title: " + selected);
//					Log.d(TAG, "Equals: " + selected.equals(getString(R.string.production_site)));
					// we have a selected item
					enableWidgetsOnSelectedSite(true);
//					currentSite = getSelectedProductionSite();
					selectedSiteStr = getSelectedProductionSiteNrAsString();
					fillIndividSpinner();
//				}
//				else {
//					enableWidgetsOnSelectedSite(false);
//				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
//				currentSite = null;
				selectedSiteStr = null;
				enableWidgetsOnSelectedSite(false);
				setImageButtonEnabled(productionSiteButtonEdit, false);
//				productionSiteButtonEdit.setEnabled(false);
//				productionSiteButtonEdit.setClickable(false);
				// TODO disable individ spinner
			}
		};
		productionSiteSpinner.setOnItemSelectedListener(productionSiteSpinnerListener);
	}
	
	/**
	 * Enable widgets depending on if there is a selected production site or not
	 * @param enable
	 */
	private void enableWidgetsOnSelectedSite(boolean enable) {
		setImageButtonEnabled(productionSiteButtonEdit, enable);
//		productionSiteButtonEdit.setEnabled(enable);
		individualButtonNew.setEnabled(enable);
	}

	/**
	 * Load the data for the spinners in a background task.
	 */
	private class LoadProductionSiteSpinnerDataTask extends AsyncTask<String, Long, List<String>> {
		
		private Spinner currentSpinner;
		private ArrayAdapter<String> currentAdapter;
//		private List<String> currentList;

		@Override
		protected List<String> doInBackground(String... params) {
			List<String> currentList = new ArrayList<String>();
			
			// Which spinner to load, the ProductionSite or the Individual?
			switch(params[0]) {
			case ProductionSiteDB.TABLE_NAME:
				// load the production site names and nrs
				currentSpinner = productionSiteSpinner;
				currentAdapter = productionSiteSpinnerAdapter;
				currentList = loadProductionSites();
				break;
//			case IndividualDB.TABLE_NAME:
				// load the Individs in agiven ProductionSite
			}
			return currentList;
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			productionSitesList = result;
			buildProductionSiteAdapter(productionSitesList);
		}

		/**
		 * Load the ProductionSites from db
		 * @return 	All production sites as Spinner Title strings
		 */
		private List<String> loadProductionSites() {
			ProductionSiteDB db = new ProductionSiteDB(getApplicationContext());
			db.open();
			List<String> titles = db.getAllProductionSiteSpinnerTitles();
			db.close();
			return titles;
		}
	}

	/**
	 * Build a list of lines to put in ProductionSiteSpinner, 
	 * like "SE-012345 (Nygarden)", and set the list as source for the spinner.
	 * @param result
	 */
	protected void buildProductionSiteAdapter(List<String> siteTitles) {
		// build the title lines
//		ArrayList<String> siteTitles = new ArrayList<String>();
//		for(ProductionSite site : sites) {
//			siteTitles.add(site.getTitle());
//			Log.d(TAG, "adding Site: " + site.getTitle());
//		}
		// sort the titles
		Collections.sort(siteTitles);
		// Set the first item to a Title for the spinner, i.e. Produktionsplats
//		String spinnerTitle = getString(R.string.production_site);
//		siteTitles.add(0, spinnerTitle);
		
		// create the adapter
		productionSiteSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), 
				R.layout.simple_spinner_item, siteTitles);
		productionSiteSpinner.setAdapter(productionSiteSpinnerAdapter);
		// set the selected item
//		if(selectedSiteNr != null) {
//			int pos = productionSiteSpinnerAdapter.getPosition(selecteSite.getTitle());
//			productionSiteSpinner.setSelection(pos);	
//		}
		
	}
	
}
