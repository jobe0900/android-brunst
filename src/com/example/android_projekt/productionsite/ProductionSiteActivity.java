package com.example.android_projekt.productionsite;

import com.example.android_projekt.MainActivity;
import com.example.android_projekt.R;
import com.example.android_projekt.Utils;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.provider.MediaStore;

/**
 * Activity for entering data for a ProductionSite.
 * Can be used to create a new, or if started with an intent with extras, 
 * to edit an existing ProductionSite.
 */
public class ProductionSiteActivity extends ActionBarActivity 
{
	private final static String TAG = "Brunst: ProductionSiteActivity";
	public final static String EXTRA_PRODUCTION_SITE = "brunst.extra.ProductionSiteActivity.ProductionSite";
	
	private final static int INTENT_PICK_IMAGE = 10;	// id for the gallery intent
	
	// WIDGETS
	private EditText etOrg;
	private EditText etPpnr;
	private EditText etName;
	private EditText etAddress;
	private EditText etPostnr;
	private EditText etPostaddress;
	private EditText etCoord;
	private ImageButton ibThumb;
	private ImageButton ibMap;
	private ImageButton ibHere;
	
	private boolean updating = false;
	private boolean hasLocationService = false;
	
	private ProductionSite site;
	private ProductionSiteDB productionSiteDB;
	private Uri imageUri;
	
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
		Utils.disableEntry(etCoord);
		
		// TODO Enable location buttons if we get the services working
		if(hasLocationService) {
			ibHere.setEnabled(true);
			ibMap.setEnabled(true);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.production_site_activity_actions, menu);
		// enabled / disable delete depending on update or not
		menu.getItem(0).setVisible(updating);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.production_site_action_delete:
			showDialogDelete();
			return true;
		case R.id.production_site_action_save:
			showDialogSave();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// Handle results from picking image in gallery
	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);
		
		Log.d(TAG, "Return from gallery");
		switch(request) {
		case INTENT_PICK_IMAGE:
			if(data != null) {
				imageUri = data.getData();
				Log.d(TAG, "Has URI: " + imageUri.toString());
				setThumbnail();
			}
			break;
		}
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
	
	/**
	 * Set the contents om the Thumbnail image button
	 */
	private void setThumbnail() {
		if(imageUri != null) {
			Bitmap thumb = getThumbnail(imageUri);
			Log.d(TAG, "setting thumbnail : " + thumb);
			if(thumb != null) {
				ibThumb.setImageBitmap(thumb);
			}
		}	
	}
	
	/**
	 * Get a micro (96x96) thumbnail from the image at the Uri.
	 * The method is a mix of solutions from this thread on SO:
	 * http://stackoverflow.com/questions/5548645/get-thumbnail-uri-path-of-the-image-stored-in-sd-card-android
	 * @param	uri		Uri to the image
	 * @return	Bitmap or null
	 */
	private Bitmap getThumbnail(Uri uri) {
	    String[] projection = { MediaStore.Images.Media._ID };
	    String result = null;
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    
	    Log.d(TAG, "cursor: " + cursor);
	    
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media._ID);
	    
	    Log.d(TAG, "column: " + column_index);

	    cursor.moveToFirst();
	    long imageId = cursor.getLong(column_index);
//	    cursor.close();		// can't close or else it crashes on a second attempt at changing pic
	    Log.d(TAG, "imageID: " + imageId);

	    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
	            getContentResolver(), imageId,
	            MediaStore.Images.Thumbnails.MICRO_KIND,
	            null);
	    
	    Log.d(TAG, "bitmap: " + bitmap);
	    return bitmap;
	}
	
	/** Save the ProductionSite to DB. */
	protected void saveForm() {
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
		
		if(savedOK) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(MainActivity.EXTRA_SITE_UPDATED, site.getTitle());
			startActivity(intent);
		}
		else {
			String text = getString(R.string.toast_could_not_save) + " " + site.getTitle();
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
		ibThumb = (ImageButton) findViewById(R.id.production_site_imgbutton_thumb);
		ibMap = (ImageButton) findViewById(R.id.production_site_imgbutton_map);
		ibMap.setEnabled(false);
		ibHere = (ImageButton) findViewById(R.id.production_site_imgbutton_here);
		ibHere.setEnabled(false);
	}
	
	/** Add OnClickListener to buttons. */
	private void setupClickListeners() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.production_site_imgbutton_thumb:
					openGallery();
					break;
				case R.id.production_site_imgbutton_map:
					placeOnMap();
					break;
				case R.id.production_site_imgbutton_here:
					getLocation();
					break;
				}
			}
		};
		ibThumb.setOnClickListener(clickListener);
		ibMap.setOnClickListener(clickListener);
		ibHere.setOnClickListener(clickListener);
	}
	
	/**
	 * Open the gallery to pick an image for the ProductionSite
	 */
	protected void openGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_pick_image)), INTENT_PICK_IMAGE);
	}
	
	/**
	 * Show a dialog, confirming the user's wish to delete a ProductionSite.
	 */
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
		int rowsAffected = productionSiteDB.deleteProductionSite(site);
		Log.d(TAG, "deleted nr of rows: " + rowsAffected);
		
		// back to main
		Intent intent = new Intent(this, MainActivity.class);;
		startActivity(intent);
	}
	
	/**
	 * Show a dialog, confirming the user's wish to save a ProductionSite.
	 */
	private void showDialogSave() {
		if(verifyForm()) {
			// ask for confirmation first
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			String siteStr = etOrg.getText().toString() + "-" + etPpnr.getText().toString();
			if(etName.length() > 0) {
				siteStr += etName.getText().toString();
			}
			builder.setMessage(getString(R.string.dialog_ask_save) + " " + siteStr + "?");
			builder.setCancelable(true);
			// YES button
			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveForm();
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
		else {
			Toast.makeText(this, R.string.toast_form_not_correct, Toast.LENGTH_SHORT).show();
		}
	}

	/** Fill fields with intent-extras, disable input on ProductionSiteNr. */
	private void setupUpdate() {
		updating = true;
		Intent intent = getIntent();
		
		String siteNrStr = intent.getStringExtra(EXTRA_PRODUCTION_SITE);
		ProductionSiteNr siteNr = new ProductionSiteNr(siteNrStr);
		
		if(siteNr != null) {
			site = productionSiteDB.getProductionSite(siteNr);
		}
		
		if(site == null) {
			String text = getString(R.string.toast_could_not_fetch) + " " + siteNrStr;
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Log.d(TAG, "site id: " + site.get_id());
		etOrg.setText(site.getSiteNr().getOrg());
		etPpnr.setText(site.getSiteNr().getPpnr());
		// disable input
		Utils.disableEntry(etOrg);
		Utils.disableEntry(etPpnr);
		
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
		if(site.hasImageUriStr()) {
			imageUri = Uri.parse(site.getImageUriStr());
			Log.d(TAG, "loaded URI: " + imageUri);
			setThumbnail();
		}
		else {
			Log.d(TAG, "no loaded URI");
		}
		
	}
	
	/** Read the form and create a ProductionSite with those values. */
	private void createProductionSiteFromForm() {
		// org = 2 chars, 0-padded
		String orgStr = etOrg.getText().toString();
		while(orgStr.length() < 2) {
			orgStr = "0" + orgStr;
		}
		if(orgStr.length() > 2) {
			orgStr = orgStr.substring(0, 2);
		}
		
		// ppnr = 6 chars, 0-padded
		String ppnrStr = etPpnr.getText().toString();
		while(ppnrStr.length() < 6) {
			ppnrStr = "0" + ppnrStr;
		}
		if(ppnrStr.length() > 6) {
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
		if(imageUri != null) {
			Log.d(TAG, "Saving URI: " + imageUri.toString());
			site.setImageUriStr(imageUri.toString());
		}
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
