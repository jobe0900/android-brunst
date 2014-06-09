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
	private static final String TAG = "Brunst: MAIN";
	private Spinner productionSiteSpinner;
	private ArrayAdapter<String> productionSiteSpinnerAdapter;
	private Button productionSiteButton;
	private OnClickListener clickListener;
	private OnItemSelectedListener productionSiteSpinnerListener;
//	private SimpleCursorAdapter productionSiteAdapter;
//	private SimpleCursorAdapter individualAdapter;
	
	private static final int DB_LOADER = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		productionSiteSpinner = (Spinner) findViewById(R.id.main_spinner_production_site);
		productionSiteButton = (Button) findViewById(R.id.main_btn_production_site);
		
		fillSpinners();
		
		setupClickListeners();
		setUpSpinnerListeners();
		
		
	}
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		fillSpinners();	// need this?
	}

	private void fillSpinners() {
//		getLoaderManager().initLoader(DB_LOADER, null, (LoaderCallbacks<Cursor>) this);
		fillProductionSiteSpinner();
//		fillIndividSpinner();
	}

	// TODO create a background task,
	// call PSDB to select columns, and put them in a arrayadapter
	// to present in the spinner
	// http://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array
	private void fillProductionSiteSpinner() {
		new LoadSpinnerDataTask().execute(ProductionSiteDB.TABLE_NAME);
	}
	
	private void fillIndividSpinner() {
		// TODO
		// first check if there is a site selected in ProductionSiteSpinner
		String siteString = (String) productionSiteSpinner.getSelectedItem();
		// if there is a site selected (and not just the title for the spinner)
		if(siteString != null && !siteString.equalsIgnoreCase(ProductionSiteDB.TABLE_NAME)) {
			// get the sitenr
			String siteNr = siteString.split("\\s*")[0]; // get the nr part of the line
			//TODO kick of the background task to load the spinner data
//			new LoadSpinnerDataTask().execute(IndividDB.TABLE_NAME, siteNr);
		}
		
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

	private void setupClickListeners() {
		clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.main_btn_production_site:
					Intent psIntent = new Intent(getApplicationContext(), ProductionSiteActivity.class);
					startActivity(psIntent);
				}
			}
		};
		productionSiteButton.setOnClickListener(clickListener);
	}
	
	private void setUpSpinnerListeners() {
		// Listener for the ProductionSiteSpinner
		productionSiteSpinnerListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selected = (String) parent.getItemAtPosition(position);
				if(!selected.equals(ProductionSiteDB.TABLE_NAME)) {
					// we have a selected item, fill the corresponding individs
					fillIndividSpinner();
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// nothing
				}
		};
		productionSiteSpinner.setOnItemSelectedListener(productionSiteSpinnerListener);
	}
	

	/**
	 * Load the data for the spinners in a background task.
	 */
	private class LoadSpinnerDataTask extends AsyncTask<String, Long, List<String>> {
		
		private Spinner currentSpinner;
		private ArrayAdapter<String> currentAdapter;

		@Override
		protected List<String> doInBackground(String... params) {
			List<String> spinnerList = new ArrayList<String>();
			
			// Which spinner to load, the ProductionSite or the Individ?
			switch(params[0]) {
			case ProductionSiteDB.TABLE_NAME:
				// load the production site names and nrs
				currentSpinner = productionSiteSpinner;
				currentAdapter = productionSiteSpinnerAdapter;
				spinnerList = loadProductionSiteTitles();
				break;
//			case IndividDB.TABLE_NAME:
				// load the Individs in agiven ProductionSite
			}
			return spinnerList;
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			Log.d(TAG, "buidling spinner with data: " + result);
			// set the contents of the spinner
			currentAdapter = new ArrayAdapter<String>(getApplicationContext(), 
							android.R.layout.simple_spinner_item, result);
			currentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			currentSpinner.setAdapter(currentAdapter);
		}

		/**
		 * Load the ProductionSites from db, build their titles as
		 * name + number, like "SE-012345 (Nygården)"
		 * @return 	A string of the ProductionSites name and nr.
		 */
		private List<String> loadProductionSiteTitles() {
			ProductionSiteDB db = new ProductionSiteDB(getApplicationContext());
			db.open();
			List<ProductionSite> sites = db.getAllProductionSites();
			db.close();
			Log.d(TAG, "fetched sites from db: " + sites);
			// extract the name and nr
			ArrayList<String> siteTitles = new ArrayList<String>();
			for(ProductionSite site : sites) {
				String title = site.getSiteNr().toString() + "(" + site.getName()+ ")";
				siteTitles.add(title);
				Log.d(TAG, "adding Site: " + title);
			}
			// sort the titles
			Collections.sort(siteTitles);
			// Set the first item to a Title for the spinner, i.e. Produktionsplats
			String spinnerTitle = getString(R.string.production_site);
			siteTitles.add(0, spinnerTitle);
			return siteTitles;
		}
	}


}
