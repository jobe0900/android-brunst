package com.example.android_projekt.event;

import com.example.android_projekt.R;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.individ.Individual;
import com.example.android_projekt.individ.IndividualDB;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Build;

/**
 * Activity to register Heat events
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class HeatActivity extends ActionBarActivity 
{
	private static final String TAG = "Brunst: HeatActivity";
	public static final String EXTRA_INDIVIUDAL = "brunst.extra.HeatActivity.Individual";
	public static final String EXTRA_HEATEVENT = "brunst.extra.HeatActivity.HeatEvent";
	
	// WIDGETS
	private CheckBox cbRemind;
	private EditText etLactationnr;
	private EditText etHeatRound;
	private EditText etDate;
	private EditText etTime;
	private EditText etRemind;
	private EditText etNote;
	private ImageButton ibHeatRound;
	private ImageButton ibDate;
	private ImageButton ibTime;
	private ImageButton ibRemind;
	private Spinner spinSign;
	private Spinner spinStrength;
	private TextView tvIndividual;
	
	private Individual individual;
	private HeatEvent heat;
	private boolean editing = true;	// editing a new or just viewing an old heat?
	
	private HeatEventDB heatDB;
	private IndividualDB individualDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_heat);
		
		heatDB = new HeatEventDB(this);
		heatDB.open();
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_INDIVIUDAL)) {
			individual = (Individual) intent.getSerializableExtra(EXTRA_INDIVIUDAL);
			Log.d(TAG, "received Individual: " + individual.toString());
			heat = new HeatEvent(individual.getIdNr());
		}
		if(intent.hasExtra(EXTRA_HEATEVENT)) {
			editing = false;
			heat = (HeatEvent) intent.getSerializableExtra(EXTRA_HEATEVENT);
			Log.d(TAG, "received Heat: " + heat.toString());
		}
		
		if(individual != null && heat != null) {
			findViews();
			prepareViews();
			setUpListeners();
		}
		
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.heat, menu);
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
		heatDB.open();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
//		individualDB.close();
		heatDB.close();
		super.onPause();
	}
	
	/**
	 * Get references to the different views.
	 */
	private void findViews() {
		cbRemind = (CheckBox) findViewById(R.id.heat_checkbox_remind);
		etDate = (EditText) findViewById(R.id.heat_entry_date);
		etHeatRound = (EditText) findViewById(R.id.heat_entry_heatround);
		etLactationnr = (EditText) findViewById(R.id.heat_entry_lactationnr);
		etNote = (EditText) findViewById(R.id.heat_entry_note);
		etRemind = (EditText) findViewById(R.id.heat_entry_remind);
		etTime = (EditText) findViewById(R.id.heat_entry_time);
		ibDate = (ImageButton) findViewById(R.id.heat_imgbutton_edit_date);
		ibHeatRound = (ImageButton) findViewById(R.id.heat_imgbutton_edit_heatround);
		ibRemind = (ImageButton) findViewById(R.id.heat_imgbutton_edit_remind);
		ibTime = (ImageButton) findViewById(R.id.heat_imgbutton_edit_time);
		spinSign = (Spinner) findViewById(R.id.heat_spinner_sign);
		spinStrength = (Spinner) findViewById(R.id.heat_spinner_strength);
		tvIndividual = (TextView) findViewById(R.id.heat_label_individual_title);
	}

	/**
	 * Set the contents, visibility and editability of views.
	 */
	private void prepareViews() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Have the widgets listen for events
	 */
	private void setUpListeners() {
		// TODO Auto-generated method stub
		
	}

}
