package com.example.android_projekt;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Toast;
import android.os.Build;

/**
 * Activity for entering data for a ProductionSite.
 * Can be used to create a new, or if started with an intent with extras, 
 * to edit an existing ProductionSite.
 */
public class ProductionSiteActivity extends ActionBarActivity 
{
	public final static String TAG = "Brunst: ProductionSiteActivity";
	public final static String EXTRA_PRODUCTION_SITE = "brunst.extra.ProductionSiteActivity.object";
	private final static int DIALOG_DELETE_SITE = 10;	// id for a dialog to confirm delete
	
//	public final static String EXTRA_PRODUCTION_SITE_ID = "brunst.extra.ProductionSiteActivity.id";
//	public final static String EXTRA_PRODUCTION_SITE_PPNR_STRING = "brunst.extra.ProductionSiteActivity.ppnrString";
//	public final static String EXTRA_PRODUCTION_SITE_NAME = "brunst.extra.ProductionSiteActivity.name";
//	public final static String EXTRA_PRODUCTION_SITE_ADDRESS = "brunst.extra.ProductionSiteActivity.address";
//	public final static String EXTRA_PRODUCTION_SITE_POSTNR  = "brunst.extra.ProductionSiteActivity.postnr";
//	public final static String EXTRA_PRODUCTION_SITE_POSTADDRESS  = "brunst.extra.ProductionSiteActivity.postaddress";
//	public final static String EXTRA_PRODUCTION_SITE_COORD  = "brunst.extra.ProductionSiteActivity.coord";
	
	private EditText etOrg;
	private EditText etPpnr;
	private EditText etName;
	private EditText etAddress;
	private EditText etPostnr;
	private EditText etPostaddress;
	private EditText etCoord;
	private ImageButton ibMap;
	private ImageButton ibHere;
	private Button btnDelete;
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
		
		productionSiteDB = new ProductionSiteDB(this);
		productionSiteDB.open();
		
		// find out if it as new ProductionSite or update of existing.
		if(getIntent().hasExtra(EXTRA_PRODUCTION_SITE)) {
			setupUpdate();
		}
		// try to set focus, no effect if updating
		etPpnr.requestFocus();
		// disable direct entry of coordinates
		disableEntry(etCoord);
		
		if(!updating) {
			btnDelete.setEnabled(false);
		}
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
		Log.d(TAG, "Save form.");
		boolean savedOK = false;
		if(updating) {
			getOptionalFields();
			savedOK = productionSiteDB.saveProductionSite(site);
			Log.d(TAG, "Updated the ProductionSite " + site.toString() + ": " + savedOK);
		}
		else { // save
			savedOK = verifyForm();
			Log.d(TAG, "Verified the form: " + savedOK);
			if(savedOK) {
				createProductionSiteFromForm();
				Log.d(TAG,  "Has created site from form: " + site.get_id() + ", " + site.toString());
				savedOK = productionSiteDB.saveProductionSite(site);
			}
		}
		
		// TODO Return to Main on success
		if(savedOK) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(MainActivity.EXTRA_SITE_UPDATED, site);
			startActivity(intent);
		}
		else {
			Toast.makeText(this, "Could not save Site", Toast.LENGTH_SHORT).show();
		}
	}


	/** Have the user place the ProductionSite on a map. */
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
		btnDelete = (Button) findViewById(R.id.production_site_button_delete);
		btnSave = (Button) findViewById(R.id.production_site_button_save);
	}
	
	/** Add OnClickListener to buttons. */
	private void setupClickListeners() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.production_site_button_delete:
					showDialogDelete();
//					deleteProductionSite();
//					showDialog(DIALOG_DELETE_SITE);
					break;
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
		btnDelete.setOnClickListener(clickListener);
		btnSave.setOnClickListener(clickListener);
	}
	
	private void showDialogDelete() {
		// ask for confirmation first
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.dialog_ask_delete_site) + " " + site.getTitle() + "?");
		builder.setCancelable(true);
		// YES button
		builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteProductionSite();	
			}
		});
		// NO button
		builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// nothing?
			}
		});

		builder.show();
	}
	
	/**
	 * Perform the actual deletion of the production site.
	 */
	protected void deleteProductionSite() {
		int rowsAffected = 0;

//		ProductionSiteDB db = new ProductionSiteDB(getApplicationContext());
//		db.open();
		rowsAffected = productionSiteDB.deleteProductionSite(site);
//		db.close();
		Log.d(TAG, "deleted nr of rows: " + rowsAffected);
		
		// back to main
		Intent intent = new Intent(this, MainActivity.class);;
		startActivity(intent);
	}
	
//	public class DeleteDialog extends DialogFragment {
//		@Override
//		public Dialog onCreateDialog(Bundle savedInstanceState) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//			builder.setMessage(getString(R.string.dialog_ask_delete_site) + " " + site.getTitle() + "?");
//			builder.setCancelable(true);
//			// YES button
//			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					deleteProductionSite();	
//				}
//			});
//			// NO button
//			builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// nothing?
//				}
//			});
//			
//			return builder.create();
//		}
//	}
	
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch(id) {
//		case DIALOG_DELETE_SITE:
//			Builder builder = new AlertDialog.Builder(getApplicationContext());
//			builder.setMessage(getString(R.string.dialog_ask_delete_site) + " " + site.getTitle() + "?");
//			builder.setCancelable(true);
//			builder.setPositiveButton(R.string.dialog_yes, new OkOnClickListener());
//			builder.setNegativeButton(R.string.dialog_no, new CancelOnClickListener());
//			AlertDialog dialog = builder.create();
//			dialog.show();
//		}
//		return super.onCreateDialog(id);
//	}
//	
////	private final class CancelOnClickListener implements
//	DialogInterface.OnClickListener {
//		public void onClick(DialogInterface dialog, int which) {
//			//do nothing
//		}
//	}
//
//	private final class OkOnClickListener implements
//	DialogInterface.OnClickListener {
//		public void onClick(DialogInterface dialog, int which) {
//			deleteProductionSite();
//		}
//	}

	/** Fill fields with intent-extras, disable input on ProductionSiteNr. */
	private void setupUpdate() {
		updating = true;
		Intent intent = getIntent();
		
		site = (ProductionSite) intent.getSerializableExtra(EXTRA_PRODUCTION_SITE);
		Log.d(TAG, "received serializable site id: " + site.get_id());
		etOrg.setText(site.getSiteNr().getOrg());
		etPpnr.setText(site.getSiteNr().getPpnr());
		// disable input
		disableEntry(etOrg);
		disableEntry(etPpnr);
		
		if(site.hasName())
			etName.setText(site.getName());
		if(site.hasAddress())
			etAddress.setText(site.getAddress());
		if(site.hasPostnr())
			etPostnr.setText(site.getPostnr());
		if(site.hasPostaddress())
			etPostaddress.setText(site.getPostaddress());
		if(site.hasCoordinates())
			etCoord.setText(site.getCoordinates());
		
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
		getOptionalFields();
	}
	
	/**
	 * Read the optional fields in the form and fill in the site
	 */
	private void getOptionalFields() {
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
