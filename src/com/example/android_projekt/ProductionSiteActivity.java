package com.example.android_projekt;

import java.io.File;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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
import android.provider.MediaStore;

/**
 * Activity for entering data for a ProductionSite.
 * Can be used to create a new, or if started with an intent with extras, 
 * to edit an existing ProductionSite.
 */
public class ProductionSiteActivity extends ActionBarActivity 
{
	public final static String TAG = "Brunst: ProductionSiteActivity";
	public final static String EXTRA_PRODUCTION_SITE = "brunst.extra.ProductionSiteActivity.object";
//	private final static int DIALOG_DELETE_SITE = 10;	// id for a dialog to confirm delete
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
	private Button btnDelete;
	private Button btnSave;
	
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
		disableEntry(etCoord);
		
		if(!updating) {
			btnDelete.setEnabled(false);
		}
		
		// TODO Enable location buttons if we get the services working
		if(hasLocationService) {
			ibHere.setEnabled(true);
			ibMap.setEnabled(true);
		}
	}
	
	/**
	 * Set the contents om the Thumbnail image button
	 */
	private void setThumbnail() {
		if(imageUri != null) {
//			String thumbPath = getThumbnailPath(imageUri);
//			Log.d(TAG, "path to thumbnail: " + thumbPath);
//			if(thumbPath != null && !thumbPath.equals("")) {
//				File imgFile = new File(thumbPath);
//				if(imgFile.exists()) {
//					Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//					ibThumb.setImageBitmap(bitmap);
//				}
//				
//			}
			
			Bitmap thumb = getThumbnail(imageUri);
			Log.d(TAG, "setting thumbnail : " + thumb);
			if(thumb != null) {
				ibThumb.setImageBitmap(thumb);
			}
			
//			Bitmap thumb = getThumbnailBitmap(imageUri);
//			Log.d(TAG, "setting thumbnail : " + thumb);
//			if(thumb != null) {
//				ibThumb.setImageBitmap(thumb);
//			}
		}	
	}
	
	public Bitmap getThumbnail(Uri uri) {
	    String[] projection = { MediaStore.Images.Media._ID };
	    String result = null;
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    
	    Log.d(TAG, "cursor: " + cursor);
	    
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media._ID);
	    
	    Log.d(TAG, "column: " + column_index);

	    cursor.moveToFirst();
	    long imageId = cursor.getLong(column_index);
//	    cursor.close();
	    
	    Log.d(TAG, "imageID: " + imageId);

	    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
	            getContentResolver(), imageId,
//	            MediaStore.Images.Thumbnails.MINI_KIND,
	            MediaStore.Images.Thumbnails.MICRO_KIND,
	            null);
	    
	    Log.d(TAG, "bitmap: " + bitmap);
	    
//	    if (cursor != null && cursor.getCount() > 0) {
//	    	Log.d(TAG, "cursor has a hit");
//	        cursor.moveToFirst();
//	        result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
//	        Log.d(TAG, "cursor result: " + result);
//	        cursor.close();
//	    }
	    return bitmap;
	}
	
	public String getThumbnailPath(Uri uri) {
	    String[] projection = { MediaStore.Images.Media._ID };
	    String result = null;
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    
	    Log.d(TAG, "cursor: " + cursor);
	    
	    int column_index = cursor
	            .getColumnIndexOrThrow(MediaStore.Images.Media._ID);
	    
	    Log.d(TAG, "column: " + column_index);

	    cursor.moveToFirst();
	    long imageId = cursor.getLong(column_index);
	    cursor.close();
	    
	    Log.d(TAG, "imageID: " + imageId);

	    cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
	            getContentResolver(), imageId,
//	            MediaStore.Images.Thumbnails.MINI_KIND,
	            MediaStore.Images.Thumbnails.MICRO_KIND,
	            null);
	    
	    Log.d(TAG, "cursor: " + cursor);
	    
	    if (cursor != null && cursor.getCount() > 0) {
	    	Log.d(TAG, "cursor has a hit");
	        cursor.moveToFirst();
	        result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
	        Log.d(TAG, "cursor result: " + result);
	        cursor.close();
	    }
	    return result;
	}
	
	/**
	 * Get a thumbnail bitmap 96x96 of an image.
	 * Based on http://stackoverflow.com/a/24136023
	 * @param 	uri		Uri to the fullsize image
	 * @return	Bitmap or null
	 */
	private Bitmap getThumbnailBitmap(Uri uri){
	    String[] proj = { MediaStore.Images.Media.DATA };

	    CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
	    Cursor cursor = cursorLoader.loadInBackground();

	    int column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID);
	    
	    if(column_index == -1) {
	    	Log.d(TAG, "Could not load cursor");
	    	return null;
	    }

	    cursor.moveToFirst();
	    long imageId = cursor.getLong(column_index);
	    //cursor.close();

	    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
	            getContentResolver(), imageId,
	            MediaStore.Images.Thumbnails.MICRO_KIND,
	            (BitmapFactory.Options) null );

	    Log.d(TAG, "Loaded bitmap: " + bitmap);
	    return bitmap;
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
			intent.putExtra(MainActivity.EXTRA_SITE_UPDATED, site);
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
					break;
				case R.id.production_site_button_save:
					saveForm();
					break;
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
		btnDelete.setOnClickListener(clickListener);
		btnSave.setOnClickListener(clickListener);
	}
	
	/**
	 * Open the gallery to pick an image for the ProductionSite
	 */
	protected void openGallery() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, getString(R.string.dialog_pick_image)), INTENT_PICK_IMAGE);
	}
	
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
		if(site.hasImageUriStr()) {
			imageUri = Uri.parse(site.getImageUriStr());
			Log.d(TAG, "loaded URI: " + imageUri);
			setThumbnail();
		}
		else {
			Log.d(TAG, "no loaded URI");
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
