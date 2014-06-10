package com.example.android_projekt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.widget.Spinner;
import android.os.Build;

public class MainActivity extends ActionBarActivity 
{	
	public static final String EXTRA_SITE_UPDATED = "brunst.extra.MainActivity.siteUpdate";
//	public static final String EXTRA_SITE_DELETED = "brunst.extra.MainActivity.siteDelete";
	private static final String TAG = "Brunst: MAIN";
	private static final int DB_LOADER = 0;
	
	private Spinner productionSiteSpinner;
	private ArrayAdapter<String> productionSiteSpinnerAdapter;
	
	private Button productionSiteButtonNew;
	private Button productionSiteButtonEdit;
	
	private OnClickListener clickListener;
	private OnItemSelectedListener productionSiteSpinnerListener;
//	private SimpleCursorAdapter productionSiteAdapter;
//	private SimpleCursorAdapter individualAdapter;
	
	private List<ProductionSite> productionSites;
	private ProductionSite currentSite;
//	private String currentSiteNr = null;
	private ProductionSite updatedSite = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		productionSiteSpinner = (Spinner) findViewById(R.id.main_spinner_production_site);
		
		productionSiteButtonNew = (Button) findViewById(R.id.main_btn_production_site_new);
		productionSiteButtonEdit = (Button) findViewById(R.id.main_btn_production_site_edit);
		
		if(getIntent().hasExtra(EXTRA_SITE_UPDATED)) {
			updatedSite = (ProductionSite) getIntent().getSerializableExtra(EXTRA_SITE_UPDATED);
		}
		
		fillSpinners();
		
		setupClickListeners();
		setUpSpinnerListeners();
		
		productionSiteButtonEdit.setEnabled(false);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		fillSpinners();	// need this?
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
	 * Kick off an AsyncTask to load Individ data for current site.
	 */
	private void fillIndividSpinner() {
		// TODO
		if(currentSite != null) {
			//TODO kick of the background task to load the spinner data
//			new LoadSpinnerDataTask().execute(IndividDB.TABLE_NAME, siteNr);
		}
	}
	
	
	/**
	 * Get the site that is selected in the ProductionSite spinner.
	 * @return	A ProductionSite or null if none selected
	 */
	private ProductionSite getSelectedProductionSite() {
		// first check if there is a site selected in ProductionSiteSpinner
		String siteString = (String) productionSiteSpinner.getSelectedItem();
		ProductionSite site = null;
		String siteNr = null;
		// if there is a site selected (and not just the title for the spinner)
		if(siteString != null && !siteString.equalsIgnoreCase(getString(R.string.production_site))) {
			Log.d(TAG, "The line in spinner: " + siteString);
			// get the sitenr
			siteNr = siteString.split("\\s+")[0]; // get the nr part of the line
			Log.d(TAG, "The number part: " + siteNr);
			for(ProductionSite ps : productionSites) {
				Log.d(TAG, "comparing: " + siteNr + " to " + ps.getSiteNr().toString());
				if(ps.getSiteNr().toString().equals(siteNr)) {
					site = ps;
					break;
				}
			}
		}
		return site;
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
				case R.id.main_btn_production_site_edit:
					intent = new Intent(getApplicationContext(), ProductionSiteActivity.class);
					Log.d(TAG, "sending serializable: " + currentSite);
					if(currentSite != null) {
						intent.putExtra(ProductionSiteActivity.EXTRA_PRODUCTION_SITE, currentSite);
					}
					startActivity(intent);
					break;
				case R.id.main_btn_production_site_new:
					intent = new Intent(getApplicationContext(), ProductionSiteActivity.class);
					startActivity(intent);
					break;
				}
			}
		};
		productionSiteButtonEdit.setOnClickListener(clickListener);
		productionSiteButtonNew.setOnClickListener(clickListener);
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
				if(!selected.equals(getString(R.string.production_site))) {
					Log.d(TAG, "PS Spinner Title: " + ProductionSiteDB.TABLE_NAME);
					Log.d(TAG, "Equals: " + selected.equals(getString(R.string.production_site)));
					// we have a selected item
					productionSiteButtonEdit.setEnabled(true);
//					productionSiteButtonEdit.setClickable(true);
					currentSite = getSelectedProductionSite();
					fillIndividSpinner();
				}
				else {
					productionSiteButtonEdit.setEnabled(false);
//					productionSiteButtonEdit.setClickable(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				currentSite = null;
				productionSiteButtonEdit.setEnabled(false);
//				productionSiteButtonEdit.setClickable(false);
				// TODO disable individ spinner
			}
		};
		productionSiteSpinner.setOnItemSelectedListener(productionSiteSpinnerListener);
	}
	

	/**
	 * Load the data for the spinners in a background task.
	 */
	private class LoadProductionSiteSpinnerDataTask extends AsyncTask<String, Long, List<ProductionSite>> {
		
		private Spinner currentSpinner;
		private ArrayAdapter<String> currentAdapter;
//		private List<String> currentList;

		@Override
		protected List<ProductionSite> doInBackground(String... params) {
			List<ProductionSite> currentList = new ArrayList<ProductionSite>();
			
			// Which spinner to load, the ProductionSite or the Individ?
			switch(params[0]) {
			case ProductionSiteDB.TABLE_NAME:
				// load the production site names and nrs
				currentSpinner = productionSiteSpinner;
				currentAdapter = productionSiteSpinnerAdapter;
				currentList = loadProductionSites();
				break;
//			case IndividDB.TABLE_NAME:
				// load the Individs in agiven ProductionSite
			}
			return currentList;
		}
		
		@Override
		protected void onPostExecute(List<ProductionSite> result) {
			productionSites = result;
			buildProductionSiteAdapter(productionSites);
		}

		/**
		 * Load the ProductionSites from db
		 * @return 	All production sites
		 */
		private List<ProductionSite> loadProductionSites() {
			ProductionSiteDB db = new ProductionSiteDB(getApplicationContext());
			db.open();
			List<ProductionSite> sites = db.getAllProductionSites();
			db.close();
			return sites;
		}
	}

	/**
	 * Build a list of lines to put in ProductionSiteSpinner, 
	 * like "SE-012345 (Nygarden)", and set the list as source for the spinner.
	 * @param result
	 */
	protected void buildProductionSiteAdapter(List<ProductionSite> sites) {
		// build the title lines
		ArrayList<String> siteTitles = new ArrayList<String>();
		for(ProductionSite site : sites) {
			siteTitles.add(site.getTitle());
			Log.d(TAG, "adding Site: " + site.getTitle());
		}
		// sort the titles
		Collections.sort(siteTitles);
		// Set the first item to a Title for the spinner, i.e. Produktionsplats
		String spinnerTitle = getString(R.string.production_site);
		siteTitles.add(0, spinnerTitle);
		
		// create the adapter
		productionSiteSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), 
				R.layout.simple_spinner_item, siteTitles);
		productionSiteSpinner.setAdapter(productionSiteSpinnerAdapter);
		// set the selected item
		if(updatedSite != null) {
			int pos = productionSiteSpinnerAdapter.getPosition(updatedSite.getTitle());
			productionSiteSpinner.setSelection(pos);	
		}
		
	}
	
}
