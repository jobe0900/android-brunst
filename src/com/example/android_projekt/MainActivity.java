package com.example.android_projekt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.example.android_projekt.individ.Individual;
import com.example.android_projekt.individ.IndividualDB;
import com.example.android_projekt.individ.IndividualEditActivity;
import com.example.android_projekt.individ.IndividualEventsActivity;
import com.example.android_projekt.notification.ReminderService;
import com.example.android_projekt.notification.ReminderServiceStartReceiver;
import com.example.android_projekt.productionsite.ProductionSite;
import com.example.android_projekt.productionsite.ProductionSiteActivity;
import com.example.android_projekt.productionsite.ProductionSiteDB;
import com.example.android_projekt.reminder.Reminder;
import com.example.android_projekt.reminder.ReminderActivity;
import com.example.android_projekt.reminder.ReminderAdapter;
import com.example.android_projekt.reminder.ReminderDB;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.app.AlarmManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Build;

/**
 * The starting point for the App.
 * Here you can Select, Edit and Add ProductionSite and Individual and see
 * Reminders for all ProductionSites.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class MainActivity extends ActionBarActivity 
{	
	public static final String EXTRA_SITE_UPDATED = "brunst.extra.MainActivity.siteUpdate";
	public static final String EXTRA_INDIVIDUAL_UPDATED = "brunst.extra.MainActivity.individualUpdate";
	private static final String TAG = "Brunst: MAIN";
	private static final String PREFS_SITE = "brunst.prefs.MainActivity.Site";
	private static final String PREFS_INDIVIDUAL = "brunst.prefs.MainActivity.Individual";
	private static final int DB_LOADER = 0;
	
	private ImageButton productionSiteButtonNew;
	private ImageButton productionSiteButtonEdit;
	private Spinner productionSiteSpinner;
	private ArrayAdapter<String> productionSiteSpinnerAdapter;
	private String selectedSiteStr;
	
	private ImageButton individualButtonNew;
	private ImageButton individualButtonEdit;
	private Spinner individualSpinner;
	private ArrayAdapter<String> individualSpinnerAdapter;
	private String selectedIndividualStr;
	
	private ListView lvReminders;

	/** "Constructor" */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// handle coming back to this activity
		if(savedInstanceState != null) {
			Log.d(TAG, "has individual: " + savedInstanceState.containsKey(PREFS_INDIVIDUAL));
			restoreValues(savedInstanceState);
		}
		else {
			// try to read from prefs
			SharedPreferences prefs = getPreferences(MODE_PRIVATE);
			if(prefs.contains(PREFS_SITE)) {
				selectedSiteStr = prefs.getString(PREFS_SITE, null);
			}
			if(prefs.contains(PREFS_INDIVIDUAL)) {
				selectedIndividualStr = prefs.getString(PREFS_INDIVIDUAL, null);
			}
		}
		
		// get references to widgets
		productionSiteSpinner = (Spinner) findViewById(R.id.main_spinner_production_site);
		productionSiteButtonNew = (ImageButton) findViewById(R.id.main_imgbutton_site_new);
		productionSiteButtonEdit = (ImageButton) findViewById(R.id.main_imgbutton_site_edit);
		
		individualSpinner = (Spinner) findViewById(R.id.main_spinner_individual);
		individualButtonNew = (ImageButton) findViewById(R.id.main_imgbutton_individual_new);
		individualButtonEdit = (ImageButton) findViewById(R.id.main_imgbutton_individual_edit);
		
		lvReminders = (ListView) findViewById(R.id.main_listview_reminder);
		
		// if started with intents: handle the extras
		if(getIntent().hasExtra(EXTRA_SITE_UPDATED)) {
			selectedSiteStr = getIntent().getStringExtra(EXTRA_SITE_UPDATED);	
			Log.d(TAG, "Main update Site: " + selectedSiteStr);
		}
		if(getIntent().hasExtra(EXTRA_INDIVIDUAL_UPDATED)) {
			selectedIndividualStr = getIntent().getStringExtra(EXTRA_INDIVIDUAL_UPDATED);
			Log.d(TAG, "Main update Individual: " + selectedIndividualStr);
		}
		
		// additional setup
		enableWidgetsOnSelectedSite(false);
		enableWidgetsOnSelectedIndividual(false);
		fillSpinners();
		setupClickListeners();
		setUpSpinnerListeners();
		
		startService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// have the same items selected
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(PREFS_SITE, (String) productionSiteSpinner.getSelectedItem());
		prefsEditor.putString(PREFS_INDIVIDUAL, (String) individualSpinner.getSelectedItem());
		prefsEditor.commit();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// save instance state bundle
		outState.putString(PREFS_SITE, (String) productionSiteSpinner.getSelectedItem());
		outState.putString(PREFS_INDIVIDUAL, (String) individualSpinner.getSelectedItem());
		Log.d(TAG, "saved selected site and individual");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		restoreValues(savedInstanceState);
	}
	
	/**
	 * Read a bundle and restore the values
	 * @param savedInstanceState
	 */
	private void restoreValues(Bundle savedInstanceState) {
		selectedSiteStr = savedInstanceState.getString(PREFS_SITE);
		selectedIndividualStr = savedInstanceState.getString(PREFS_INDIVIDUAL);
		Log.d(TAG, "restore site: " + selectedSiteStr + " and individual: " + selectedIndividualStr);
	}

	@Override
	protected void onResume() {
		super.onResume();
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
	 * Fill the spinners with data from the database.
	 */
	private void fillSpinners() {
		fillProductionSiteSpinner();
	}

	/**
	 * Kick off an AsyncTask to load ProductionSite data.
	 */
	private void fillProductionSiteSpinner() {
		Log.d(TAG, "fill Site spinner");
		new LoadSpinnerDataTask().execute(ProductionSiteDB.TABLE_NAME);
	}
	
	/**
	 * Kick off an AsyncTask to load Individual data for current site.
	 */
	private void fillIndividSpinner() {
		Log.d(TAG, "load Individuals for site: " + selectedSiteStr);
		// the ProductionSite spinner should have a selected site
		if(selectedSiteStr != null) {
			new LoadSpinnerDataTask().execute(IndividualDB.TABLE_NAME);
		}
	}
	
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
	
	/**
	 * Extract the Individuals IdNr from the title in Insividual spinner
	 * @return
	 */
	private String getSelectedIndividualIdNrAsString() {
		String idnrStr = null;
		String individualTitle = (String) individualSpinner.getSelectedItem();
		if(individualTitle != null) {
			idnrStr = individualTitle.split("[()]")[1];	// should get the idnr between parenthesis
		}
		return idnrStr;
	}

	/**
	 * Set up OnClickListeners for the buttons
	 */
	private void setupClickListeners() {
		OnClickListener clickListener = new OnClickListener() {
			Intent intent;
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.main_imgbutton_site_edit:
					intent = new Intent(getApplicationContext(), ProductionSiteActivity.class);
					Log.d(TAG, "sending site: " + selectedSiteStr);
					if(selectedSiteStr != null) {
						intent.putExtra(ProductionSiteActivity.EXTRA_PRODUCTION_SITE, getSelectedProductionSiteNrAsString());
					}
					startActivity(intent);
					break;
				case R.id.main_imgbutton_site_new:
					intent = new Intent(getApplicationContext(), ProductionSiteActivity.class);
					startActivity(intent);
					break;
				case R.id.main_imgbutton_individual_edit:
					Log.d(TAG, "edit individual: " + selectedIndividualStr);
					Log.d(TAG, "edit individual: " + getSelectedIndividualIdNrAsString());
					intent = new Intent(getApplicationContext(), IndividualEventsActivity.class);
					if(selectedIndividualStr != null) {
						intent.putExtra(IndividualEventsActivity.EXTRA_IDNR, getSelectedIndividualIdNrAsString());
					}
					startActivity(intent);
					break;
				
				case R.id.main_imgbutton_individual_new:
					intent = new Intent(getApplicationContext(), IndividualEditActivity.class);
					if(selectedSiteStr != null) {
						intent.putExtra(IndividualEditActivity.EXTRA_PRODUCTION_SITE_NR, getSelectedProductionSiteNrAsString());
					}
					startActivity(intent);
					break;
				}
			}
		};
		productionSiteButtonEdit.setOnClickListener(clickListener);
		productionSiteButtonNew.setOnClickListener(clickListener);
		individualButtonNew.setOnClickListener(clickListener);
		individualButtonEdit.setOnClickListener(clickListener);
	}
	
	/**
	 * Set up OnItemSelectedListeners for the spinners.
	 */
	private void setUpSpinnerListeners() {
		// Listener for the ProductionSiteSpinner
		OnItemSelectedListener productionSiteSpinnerListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selected = (String) parent.getItemAtPosition(position);
				Log.d(TAG, "PS Spinner Listener selected: " + selected);
				enableWidgetsOnSelectedSite(true);
				selectedSiteStr = getSelectedProductionSiteNrAsString();
				fillIndividSpinner();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				selectedSiteStr = null;
				enableWidgetsOnSelectedSite(false);
				setImageButtonEnabled(productionSiteButtonEdit, false);
			}
		};
		productionSiteSpinner.setOnItemSelectedListener(productionSiteSpinnerListener);
		
		// Listener for the IndividualSpinner
		OnItemSelectedListener individualSpinnerListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selected = (String) parent.getItemAtPosition(position);
				Log.d(TAG, "Individual Spinner Listener selected: " + selected);
				selectedIndividualStr = getSelectedIndividualIdNrAsString();
				enableWidgetsOnSelectedIndividual(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				selectedIndividualStr = null;
				enableWidgetsOnSelectedIndividual(false);
			}
		};
		individualSpinner.setOnItemSelectedListener(individualSpinnerListener);
	}
	
	/**
	 * Enable widgets depending on if there is a selected production site or not
	 * @param enable
	 */
	private void enableWidgetsOnSelectedSite(boolean enable) {
		setImageButtonEnabled(productionSiteButtonEdit, enable);
		setImageButtonEnabled(individualButtonNew, enable);
		individualSpinner.setEnabled(enable);
	}
	
	/**
	 * Enable widgets depending on if there is a selected individual or not
	 * @param enable
	 */
	private void enableWidgetsOnSelectedIndividual(boolean enable) {
		setImageButtonEnabled(individualButtonEdit, enable);
	}

	/**
	 * Load the data for a spinner in a background task.
	 * Which spinner is decided by passing the associated Table name as argument.
	 */
	private class LoadSpinnerDataTask extends AsyncTask<String, Long, List<String>> {
		
		String currentTable = null;
		
		@Override
		protected List<String> doInBackground(String... params) {
			Log.d(TAG, "bckground loading of table: " + params[0]);
			switch(params[0]) {
			case ProductionSiteDB.TABLE_NAME:
				currentTable = ProductionSiteDB.TABLE_NAME;
				return loadProductionSites();
			case IndividualDB.TABLE_NAME:
				currentTable = IndividualDB.TABLE_NAME;
				return loadIndividuals();
			default:
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<String> result) {
			switch(currentTable) {
			case ProductionSiteDB.TABLE_NAME:
				buildProductionSiteAdapter(result);
				setSelectedProductionSiteInSpinner();
				fillIndividSpinner();
				showReminderList();
				break;
			case IndividualDB.TABLE_NAME:
				buildIndividualAdapter(result);
				setSelectedIndividualInSpinner();
				break;
			}
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
			Log.d(TAG, "load Sites: " + titles);
			return titles;
		}
		
		/**
		 * Load the Individuals from the db
		 * @return
		 */
		private List<String> loadIndividuals() {
			IndividualDB db = new IndividualDB(getApplicationContext());
			String siteNrStr = getSelectedProductionSiteNrAsString();
			List<String> titles = null;
			if(siteNrStr != null) {
				db.open();
				titles = db.getAllIndividualsAtSiteAsSpinnerTitles(siteNrStr);
				db.close();
			}
			Log.d(TAG, "load individuals: " + titles);
			return titles;
		}

		/**
		 * Build the adapter for the ProductionSiteSpinner, 
		 * @param 	result	The list of ProductionSite titles
		 */
		private void buildProductionSiteAdapter(List<String> siteTitles) {
			// sort the titles
			Collections.sort(siteTitles);
			// create the adapter
			productionSiteSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), 
					R.layout.simple_spinner_item, siteTitles);
			productionSiteSpinner.setAdapter(productionSiteSpinnerAdapter);
		}
		
		/**
		 * Build the adapter for the IndividualSpinner, 
		 * @param 	result	The list of Individuals titles
		 */
		private void buildIndividualAdapter(List<String> titles) {
			// sort the titles
			Collections.sort(titles);
			// create the adapter
			individualSpinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), 
					R.layout.simple_spinner_item, titles);
			individualSpinner.setAdapter(individualSpinnerAdapter);
		}
	} // end LoadDataTask
	
	/**
	 * Make the site in selectedSiteNr be the selected site in the productionSiteSpinner
	 */
	private void setSelectedProductionSiteInSpinner() {
		if(selectedSiteStr != null) {
			int pos = productionSiteSpinnerAdapter.getPosition(selectedSiteStr);
			productionSiteSpinner.setSelection(pos);
		}
	}
	
	public void showReminderList() {
		// Get the reminders
		ReminderDB rdb = new ReminderDB(this);
		rdb.open();
//		List<Reminder> reminders = rdb.getAllCurrentReminders();
		List<Reminder> reminders = rdb.getAllReminders();
		rdb.close();

		Log.d(TAG, "fetched nr of reminders: " + reminders.size());
		ReminderAdapter adapter = new ReminderAdapter(this, reminders);
		lvReminders.setAdapter(adapter);
		lvReminders.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Reminder reminder = (Reminder) parent.getItemAtPosition(position);
				// show reminder in activity or dialog
				Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
				intent.putExtra(ReminderActivity.EXTRA_REMINDER, reminder);
				startActivity(intent);
			}
		});
	}

	/**
	 * Make the site in selectedSiteNr be the selected site in the productionSiteSpinner.
	 */
	private void setSelectedIndividualInSpinner() {
		if(selectedSiteStr != null) {
			int pos = individualSpinnerAdapter.getPosition(selectedIndividualStr);
			individualSpinner.setSelection(pos);
			Log.d(TAG, "attempt to set selected individaul at position: " + pos);
		}
	}
	
	/**
	 * Start the background service.
	 */
	private void startService() {
		// repeat once an hour
//		final long REPEAT_TIME = 1000 * 10; // once a minute for debug
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 10); // start after one minute

		Intent intent = new Intent(this, ReminderServiceStartReceiver.class);
		PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pintent);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		        cal.getTimeInMillis(), AlarmManager.INTERVAL_HALF_HOUR, pintent);
		Log.d(TAG, "starting the ReminderServiceStartReceiver");
	}
}
