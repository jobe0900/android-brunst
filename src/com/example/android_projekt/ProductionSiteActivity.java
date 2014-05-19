package com.example.android_projekt;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.os.Build;

/**
 * Activity for entering data for a ProductionSite.
 * Can be used to create a new, or if started with an intent with extras, 
 * to edit an existing ProductionSite.
 */
public class ProductionSiteActivity extends ActionBarActivity 
{
	public final static String TAG = "Brunst: ProductionSiteActivity";
	public final static String EXTRA_PRODUCTION_SITE_ID = "brunst.extra.ProductionSiteActivity.id";
	public final static String EXTRA_PRODUCTION_SITE_PPNR_STRING = "brunst.extra.ProductionSiteActivity.ppnrString";
	public final static String EXTRA_PRODUCTION_SITE_NAME = "brunst.extra.ProductionSiteActivity.name";
	public final static String EXTRA_PRODUCTION_SITE_ADDRESS = "brunst.extra.ProductionSiteActivity.address";
	public final static String EXTRA_PRODUCTION_SITE_POSTNR  = "brunst.extra.ProductionSiteActivity.postnr";
	public final static String EXTRA_PRODUCTION_SITE_POSTADDRESS  = "brunst.extra.ProductionSiteActivity.postaddress";
	public final static String EXTRA_PRODUCTION_SITE_COORD  = "brunst.extra.ProductionSiteActivity.coord";
	
	private EditText etOrg;
	private EditText etPpnr;
	private EditText etName;
	private EditText etAddress;
	private EditText etPostnr;
	private EditText etPostaddress;
	private EditText etCoord;
	private ImageButton ibMap;
	private ImageButton ibHere;
	private Button btnSave;
	
	private boolean updating = false;
	
	private ProductionSite site;
	private ProductionSiteDB productionSiteDB;
	
	/** "Constructor." */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_production_site);
		
		findViews();
		setupClickListeners();
		
		productionSiteDB = new ProductionSiteDB(getApplicationContext());
		productionSiteDB.open();
		
		// find out if it as new ProductionSite or update of existing.
		if(getIntent().hasExtra(EXTRA_PRODUCTION_SITE_PPNR_STRING)) {
			setupUpdate();
		}
		// try to set focus, no effect if updating
		etPpnr.requestFocus();
		// disable direct entry of coordinates
		disableEntry(etCoord);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.production_site, menu);
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
		productionSiteDB.open();
		super.onResume();
	}
	

	@Override
	protected void onPause() {
		productionSiteDB.close();
		super.onPause();
	}
	
	/** Save the ProductionSite to DB. */
	protected void saveForm() {
		// TODO Auto-generated method stub		
		if(updating) {
			boolean savedOK = productionSiteDB.saveProductionSite(site);
			Log.d(TAG, "Updated the ProductionSite " + site.toString() + ": " + savedOK);
		}
		else { // save
			boolean savedOK = verifyForm();
			if(savedOK) {
				createProductionSiteFromForm();
				savedOK = productionSiteDB.saveProductionSite(site);
			}
			Log.d(TAG, "Save the ProductionSite: " + site.toString() + ": " + savedOK);
		}
		
		// TODO Return to listView on success
	}


	/** Have hte user place the ProductionSite on a map. */
	protected void placeOnMap() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Place site on map");	
	}
	
	/** Use the location services to find the current coordinates. */
	protected void getLocation() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Get current location");
	}
	
	/** Get references to the widgets. */
	private void findViews() {
		etOrg = (EditText) findViewById(R.id.production_site_entry_org);
		etPpnr = (EditText) findViewById(R.id.production_site_entry_ppnr);
		etName = (EditText) findViewById(R.id.production_site_entry_name);
		etAddress = (EditText) findViewById(R.id.production_site_entry_address);
		etPostnr = (EditText) findViewById(R.id.production_site_entry_postnr);
		etPostaddress = (EditText) findViewById(R.id.production_site_entry_postaddress);
		etCoord = (EditText) findViewById(R.id.production_site_entry_coord);
		ibMap = (ImageButton) findViewById(R.id.production_site_imgbutton_map);
		ibHere = (ImageButton) findViewById(R.id.production_site_imgbutton_here);
		btnSave = (Button) findViewById(R.id.production_site_button_save);
	}
	
	/** Add OnClickListener to buttons. */
	private void setupClickListeners() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.production_site_button_save:
					saveForm();
					break;
				case R.id.production_site_imgbutton_map:
					placeOnMap();
					break;
				case R.id.production_site_imgbutton_here:
					getLocation();
				}
				
			}
		};
		
		ibMap.setOnClickListener(clickListener);
		ibHere.setOnClickListener(clickListener);
		btnSave.setOnClickListener(clickListener);
	}
	
	/** Fill fields with intent-extras, disable input on ProductionSiteNr. */
	private void setupUpdate() {
		Intent intent = getIntent();
		
		String ppnrString = intent.getStringExtra(EXTRA_PRODUCTION_SITE_PPNR_STRING);
		site = new ProductionSite(ppnrString);
		etOrg.setText(site.getSiteNr().getOrg());
		etPpnr.setText(site.getSiteNr().getPpnr());
		// disable input
		disableEntry(etOrg);
		disableEntry(etPpnr);
		
		// _ID field
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_ID)) {
			long id = intent.getLongExtra(EXTRA_PRODUCTION_SITE_ID, ProductionSite.UNSAVED_ID);
			site.set_id(id);
		}
		// name field
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_NAME)) {
			String name = intent.getStringExtra(EXTRA_PRODUCTION_SITE_NAME);
			etName.setText(name);
			site.setName(name);
		}
		
		// address field
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_ADDRESS)) {
			String address = intent.getStringExtra(EXTRA_PRODUCTION_SITE_ADDRESS);
			etAddress.setText(address);
			site.setAddress(address);
		}
		
		// postnr field
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_POSTNR)) {
			String postnr = intent.getStringExtra(EXTRA_PRODUCTION_SITE_POSTNR);
			etPostnr.setText(postnr);
			site.setPostnr(postnr);
		}
		
		// postaddress field
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_POSTADDRESS)) {
			String postaddress = intent.getStringExtra(EXTRA_PRODUCTION_SITE_POSTADDRESS);
			etPostaddress.setText(postaddress);
			site.setPostaddress(postaddress);
		}
		
		// coordinates field
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_COORD)) {
			String coord = intent.getStringExtra(EXTRA_PRODUCTION_SITE_COORD);
			etCoord.setText(coord);
			site.setCoordinates(coord);
		}
		
		// save button =  update button
		btnSave.setText(R.string.button_update);
	}
	
	/** Disable an EditText-field. */
	private void disableEntry(EditText entry) {
		entry.setKeyListener(null);
		entry.setFocusable(false);
		entry.setInputType(InputType.TYPE_NULL);
	}
	
	/** Read the form and create a ProductionSite with those values. */
	private void createProductionSiteFromForm() {
		// org = 2 chars, 0-padded
		String orgStr = etOrg.getText().toString();
		int len = orgStr.length();
		while(len < 2) {
			orgStr = "0" + orgStr;
		}
		if(len > 2) {
			orgStr = orgStr.substring(0, 2);
		}
		
		// ppnr = 6 chars, 0-padded
		String ppnrStr = etPpnr.getText().toString();
		len = ppnrStr.length();
		while(len < 6) {
			ppnrStr = "0" + ppnrStr;
		}
		if(len > 6) {
			ppnrStr = ppnrStr.substring(0, 6);
		}
		
		// now we can create the ProductionSite
		site = new ProductionSite(orgStr, ppnrStr);
		
		// get the optional fields
		site.setName(etName.getText().toString());
		site.setAddress(etAddress.getText().toString());
		site.setPostnr(etPostnr.getText().toString());
		site.setPostaddress(etPostaddress.getText().toString());
		site.setCoordinates(etCoord.getText().toString());
	}

	/** Check the required fields are filled in. */
	private boolean verifyForm() {
		boolean formOK = false;
		// check it is filled correctly
		String orgStr = etOrg.getText().toString();
		String ppnrStr = etPpnr.getText().toString();
		
		if(!orgStr.equals("") && !ppnrStr.equals("")) {
			formOK = true;
		}
		return formOK;
	}

}
