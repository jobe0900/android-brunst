package com.example.android_projekt;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
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

public class ProductionSiteActivity extends ActionBarActivity 
{
	public final static String TAG = "Brunst: ProductionSiteActivity";
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
	
	/** "Constructor." */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_production_site);
		
		findViews();
		setupClickListeners();
		
		// find out if it as new ProductionSite or update of existing.
		if(getIntent().hasExtra(EXTRA_PRODUCTION_SITE_PPNR_STRING)) {
			setupUpdate();
		}
		// try to set focus, no effect if updating
		etPpnr.requestFocus();
	}
	
	/** Fill fields with intent-extras, disable input on ProductionSiteNr. */
	private void setupUpdate() {
		Intent intent = getIntent();
		
		String ppnrString = intent.getStringExtra(EXTRA_PRODUCTION_SITE_PPNR_STRING);
		site = new ProductionSite(ppnrString);
		etOrg.setText(site.getNr().getOrg());
		etPpnr.setText(site.getNr().getPpnr());
		// disable input
		disableEntry(etOrg);
		disableEntry(etPpnr);
		
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
	
	private void disableEntry(EditText entry) {
		entry.setKeyListener(null);
		entry.setFocusable(false);
		entry.setInputType(InputType.TYPE_NULL);
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

	protected void saveForm() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Save the form");
		
	}

	protected void placeOnMap() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Place site on map");
		
	}
	
	protected void getLocation() {
		// TODO Auto-generated method stub
		Log.d(TAG, "Get current location");
		
	}

}
