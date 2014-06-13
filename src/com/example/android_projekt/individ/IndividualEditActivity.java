package com.example.android_projekt.individ;

import java.util.Calendar;

import com.example.android_projekt.R;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.Utils;
import com.example.android_projekt.productionsite.ProductionSiteNr;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.os.Build;
import android.provider.MediaStore;


public class IndividualEditActivity extends ActionBarActivity 
{
	private final static String TAG = "Brunst: IndividualEditActivity";
	
	public final static String EXTRA_PRODUCTION_SITE_NR = "brunst.extra.IndividualEditActivity.ProductionSiteNr";
	public final static String EXTRA_INDIVIDUAL_UPDATE = "brunst.extra.IndividualEditActivity.IndividualUpdate";
	
	// WIDGETS
	private EditText etIdnrOrg;
	private EditText etIdnrPpnr;
	private EditText etIdnrIndividnr;
	private EditText etIdnrChecknr;
	private EditText etShortnr;
	private EditText etName;
	private EditText etBirthdate;
	private EditText etMotherOrg;
	private EditText etMotherPpnr;
	private EditText etMotherIndividnr;
	private EditText etMotherChecknr;
	private EditText etFatherOrg;
	private EditText etFatherPpnr;
	private EditText etFatherIndividnr;
	private EditText etFatherChecknr;
	private EditText etHeatCyclus;
	private EditText etLactationNr;
	private EditText etLastBirth;
	private ImageButton ibThumb;
	private ImageButton ibBirthdateCalendar;
	private ImageButton ibHeatCyclus;
	private ImageButton ibLactationNr;
	private ImageButton ibLastBirthCalendar;
	private Spinner  spinSex;
	
	private ProductionSiteNr currentSiteNr;
	private Individual individual;
	private ArrayAdapter<CharSequence> sexAdapter;
	private Uri imageUri;
	private boolean updating = false;
	private boolean female = true;
	
	private IndividualDB individualDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_edit);
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_NR)) {
			currentSiteNr = (ProductionSiteNr) intent.getSerializableExtra(EXTRA_PRODUCTION_SITE_NR);
			Log.d(TAG, "received current site nr: " + currentSiteNr.toString());
		}
		if(intent.hasExtra(EXTRA_INDIVIDUAL_UPDATE)) {
			individual = (Individual) intent.getSerializableExtra(EXTRA_INDIVIDUAL_UPDATE);
			currentSiteNr = individual.getHomesiteNr();
			updating = true;
			Log.d(TAG, "received Individual: " + individual.toString());
		}
		
//		individualDB = new IndividualDB(this);
		
		findViews();
		prepareViews();
		
		setupListeners();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.individual, menu);
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
		super.onResume();
	}
	
	@Override
	protected void onPause() {
//		individualDB.close();
		super.onPause();
	}
	
	/**
	 * Get handles to the different widgets
	 */
	private void findViews() {
		etIdnrOrg = (EditText) findViewById(R.id.individual_edit_entry_id_org);
		etIdnrPpnr = (EditText) findViewById(R.id.individual_edit_entry_id_ppnr);
		etIdnrIndividnr = (EditText) findViewById(R.id.individual_edit_entry_id_individnr);
		etIdnrChecknr = (EditText) findViewById(R.id.individual_edit_entry_id_checknr);
		etShortnr = (EditText) findViewById(R.id.individual_edit_entry_shortnr);
		etName = (EditText) findViewById(R.id.individual_edit_entry_name);
		etBirthdate = (EditText) findViewById(R.id.individual_edit_entry_birthdate);
		etMotherOrg = (EditText) findViewById(R.id.individual_edit_entry_mother_org);
		etMotherPpnr = (EditText) findViewById(R.id.individual_edit_entry_mother_ppnr);
		etMotherIndividnr = (EditText) findViewById(R.id.individual_edit_entry_mother_individnr);
		etMotherChecknr = (EditText) findViewById(R.id.individual_edit_entry_mother_checknr);
		etFatherOrg = (EditText) findViewById(R.id.individual_edit_entry_father_org);
		etFatherPpnr = (EditText) findViewById(R.id.individual_edit_entry_father_ppnr);
		etFatherIndividnr = (EditText) findViewById(R.id.individual_edit_entry_father_individnr);
		etFatherChecknr = (EditText) findViewById(R.id.individual_edit_entry_father_checknr);
		etHeatCyclus = (EditText) findViewById(R.id.individual_edit_entry_heatcyclus);
		etLactationNr = (EditText) findViewById(R.id.individual_edit_entry_lactationnr);
		etLastBirth = (EditText) findViewById(R.id.individual_edit_entry_lastbirth);
		ibBirthdateCalendar = (ImageButton) findViewById(R.id.individual_edit_imgbutton_birthdate_calendar);
		ibHeatCyclus = (ImageButton) findViewById(R.id.individual_edit_imgbutton_heatcyclus_edit);
		ibLactationNr = (ImageButton) findViewById(R.id.individual_edit_imgbutton_lactationnr_edit);
		ibLastBirthCalendar = (ImageButton) findViewById(R.id.individual_edit_imgbutton_lastbirth_calendar);
		spinSex = (Spinner) findViewById(R.id.individual_edit_spinner_sex);
	}
	
	/**
	 * Pre-fill some information and set enabled state of widgets
	 */
	private void prepareViews() {
		setupSpinners();
		
		//disable some EditTexts
		disableEntry(etBirthdate);
		disableEntry(etHeatCyclus);
		disableEntry(etLactationNr);
		
		// if NEW
		if(currentSiteNr != null && !updating) {
			String org = currentSiteNr.getOrg();
			String ppnr = currentSiteNr.getPpnr();
			etIdnrOrg.setText(org);
			etIdnrPpnr.setText(ppnr);
			etMotherOrg.setText(org);
			etMotherPpnr.setText(ppnr);
			etFatherOrg.setText(org);
			etFatherPpnr.setText(ppnr);
		}
		// if UPDATING
		if(updating) {
			spinSex.setEnabled(false); 	// should not be able to change sex...
			populateViews();
		}
	}
	
	/**
	 * Fill in the different forms with data from the updating Individual.
	 */
	private void populateViews() {
		if(individual != null) {
			IdNr idnr = individual.getIdNr();
			ProductionSiteNr birthsiteNr = idnr.getBirthSiteNr();
			
			etIdnrOrg.setText(birthsiteNr.getOrg());
			etIdnrPpnr.setText(birthsiteNr.getPpnr());
			etIdnrIndividnr.setText(idnr.getIndividNr());
			etIdnrChecknr.setText(idnr.getCheckNr());
			
			etShortnr.setText(individual.getShortNr() + "");
			
			if(individual.hasName()) {
				etName.setText(individual.getName());
			}
			
			if(individual.hasBirthdate()) {
				etName.setText(Utils.dateToString(individual.getBirthdate()));
			}
			
			if(individual.hasMotherIdNr()) {
				IdNr motherId = individual.getMotherIdNr();
				etMotherOrg.setText(motherId.getBirthSiteNr().getOrg());
				etMotherPpnr.setText(motherId.getBirthSiteNr().getPpnr());
				etMotherIndividnr.setText(motherId.getIndividNr());
				etMotherChecknr.setText(motherId.getCheckNr());
			}
			
			if(individual.hasFatherIdNr()) {
				IdNr fatherId = individual.getFatherIdNr();
				etFatherOrg.setText(fatherId.getBirthSiteNr().getOrg());
				etFatherPpnr.setText(fatherId.getBirthSiteNr().getPpnr());
				etFatherIndividnr.setText(fatherId.getIndividNr());
				etFatherChecknr.setText(fatherId.getCheckNr());
			}
			
			if(sexAdapter != null) {
				// get the
				String sex;
				if(individual.getSex() == Individual.Sex.F) {
					sex = getString(R.string.individual_sex_female);
				}
				else {
					sex = getString(R.string.individual_sex_male);
				}
				spinSex.setSelection(sexAdapter.getPosition(sex));
			}
			
			etHeatCyclus.setText(individual.getHeatcyclus() + "");
			
			etLactationNr.setText(individual.getLactationNr() + "");
			
			if(individual.hasLastBirth()) {
				etName.setText(Utils.dateToString(individual.getLastBirth()));
			}
			
			if(individual.hasImageUri()) {
				imageUri = Uri.parse(individual.getImageUri());
				setThumbnail();
			}
		}
	}
	
	/**
	 * Set the contents of the Thumbnail image button
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

	/**
	 * Prepare different listeners on widgets for events.
	 */
	private void setupListeners() {
		setupOnFocusChangeListeners();
		setupOnItemSelectedListeners();
	}

	/**
	 * Populate spinners from arrays.
	 */
	private void setupSpinners() {
		// SEX
		sexAdapter = ArrayAdapter.createFromResource(
				this, R.array.sex, R.layout.simple_spinner_item);
		spinSex.setAdapter(sexAdapter);
	}

	/**
	 * Perform an action when widget loses focus
	 */
	private void setupOnFocusChangeListeners() {
		etIdnrIndividnr.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// if we lose focus and shortnr field is empty
				if(!hasFocus && etShortnr.getText().length() == 0) {
					createShortnr();
				}
			}
		});
	}
	
	/**
	 * Listen for selection in spinners.
	 */
	private void setupOnItemSelectedListeners() {
		spinSex.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String selected = (String) parent.getItemAtPosition(position);
				if(selected.equals(getString(R.string.individual_sex_male))) {
					female = false;
				}
				else {
					female = true;
				}
				// update visibility of widgets
				setWidgetVisibility();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// NOTHING
			}
		});
		
	}

	/**
	 * Disable all widgets not relevant for a Male Individual.
	 */
	protected void setWidgetVisibility() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Create a shortnr from the Individnr and set the shortnr field
	 */
	protected void createShortnr() {
		Log.d(TAG, "Trying to createshortnr");
		try {
			int nr = Integer.parseInt(etIdnrIndividnr.getText().toString());
			etShortnr.setText(nr + "");
		}
		catch (NumberFormatException ignore) {
			Log.d(TAG, ignore.getMessage());
		};
	}
	
	/**
	 * Find if the Individual is Male or Female.
	 * @return
	 */
	private boolean isFemale() {
		return female;
	}
	
	/** Disable an EditText-field. */
	private void disableEntry(EditText entry) {
		entry.setKeyListener(null);
		entry.setFocusable(false);
		entry.setInputType(InputType.TYPE_NULL);
	}

}
