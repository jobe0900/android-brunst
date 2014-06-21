package com.example.android_projekt.individ;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.android_projekt.MainActivity;
import com.example.android_projekt.R;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.Utils;
import com.example.android_projekt.individ.Individual.Sex;
import com.example.android_projekt.productionsite.ProductionSite;
import com.example.android_projekt.productionsite.ProductionSiteDB;
import com.example.android_projekt.productionsite.ProductionSiteNr;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

/**
 * Edit an Individual, setting fields and image for the Individual.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class IndividualEditActivity extends ActionBarActivity 
{
	private final static String TAG = "Brunst: IndividualEditActivity";
	private final static String DIALOG_BIRTHDATE = "pickBirthdate";
	private final static String DIALOG_LASTBIRTH = "pickLastBirth";
	
	public final static String EXTRA_PRODUCTION_SITE_NR = "brunst.extra.IndividualEditActivity.ProductionSiteNr";
	public final static String EXTRA_INDIVIDUAL_UPDATE = "brunst.extra.IndividualEditActivity.IndividualUpdate";
	
	private final static int INTENT_PICK_IMAGE = 10;	// id for the gallery intent
	// identify which id fields to read
	private final static String ID_OWN = "ownID";
	private final static String ID_MOTHER = "motherID";
	private final static String ID_FATHER = "fatherID";
	
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
	private TextView tvHeatCyclus;
	private TextView tvLactationNr;
	private TextView tvLastBirth;
	
	private ProductionSiteNr currentSiteNr;
	private ProductionSite currentSite;
	private Individual individual;
	private ArrayAdapter<CharSequence> sexAdapter;
	private Uri imageUri;
	private EditText pickNumberForThis;		// the edit text to set with number picker dialog
	private View contextMenuForThis;		// the view the context menu belongs to
	
	private boolean updating = false;
	private boolean female = true;
	
	private IndividualDB individualDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_individual_edit);

		individualDB = new IndividualDB(this);
		individualDB.open();
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_PRODUCTION_SITE_NR)) {
			currentSiteNr = new ProductionSiteNr(intent.getStringExtra(EXTRA_PRODUCTION_SITE_NR));
			Log.d(TAG, "received current site nr: " + currentSiteNr.toString());
		}
		if(intent.hasExtra(EXTRA_INDIVIDUAL_UPDATE)) {
			String idnrStr = intent.getStringExtra(EXTRA_INDIVIDUAL_UPDATE);
			Log.d(TAG, "edit individual: " + idnrStr);
			IdNr idnr = null;
			try {
				idnr = new IdNr(idnrStr);
			} catch (Exception ex) {
				Log.d(TAG, "Cannot parse the idnr: " + idnrStr);
			}
			if(idnr != null) {
				individual = individualDB.getIndividual(idnr);
				if(individual != null) {
					currentSiteNr = individual.getHomesiteNr();
					updating = true;
					Log.d(TAG, "received Individual: " + individual.toString());
				}
				else {
					String text = getString(R.string.toast_could_not_fetch) + " " + idnrStr;
					Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		if(currentSiteNr != null) {
			ProductionSiteDB pdb = new ProductionSiteDB(this);
			pdb.open();
			currentSite = pdb.getProductionSite(currentSiteNr);
			pdb.close();
			
			findViews();
			prepareViews();
			
			setupListeners();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.individual_edit_activity_actions, menu);
		// enabled / disable delete depending on update or not
		menu.getItem(0).setVisible(updating);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.individual_edit_action_delete:
			showDialogDelete();
			return true;
		case R.id.individual_edit_action_save:
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
	
	// Get the context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.individual_edit_context_menu, menu);
		contextMenuForThis = v;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.individual_edit_context_delete:
	        	Log.d(TAG, "Pick context Delete");
	            clearView(contextMenuForThis);
	            contextMenuForThis = null;
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	@Override
	protected void onResume() {
		individualDB.open();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		individualDB.close();
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
		ibThumb = (ImageButton) findViewById(R.id.individual_edit_imgbutton_thumb);
		ibBirthdateCalendar = (ImageButton) findViewById(R.id.individual_edit_imgbutton_birthdate_calendar);
		ibHeatCyclus = (ImageButton) findViewById(R.id.individual_edit_imgbutton_heatcyclus_edit);
		ibLactationNr = (ImageButton) findViewById(R.id.individual_edit_imgbutton_lactationnr_edit);
		ibLastBirthCalendar = (ImageButton) findViewById(R.id.individual_edit_imgbutton_lastbirth_calendar);
		spinSex = (Spinner) findViewById(R.id.individual_edit_spinner_sex);
		tvHeatCyclus = (TextView) findViewById(R.id.individual_edit_label_heatcyclus);
		tvLactationNr = (TextView) findViewById(R.id.individual_edit_label_lactationnr);
		tvLastBirth = (TextView) findViewById(R.id.individual_edit_label_lastbirth);
	}
	
	/**
	 * Pre-fill some information and set enabled state of widgets
	 */
	private void prepareViews() {
		setupSpinners();
		
		//disable some EditTexts
		Utils.disableEntry(etBirthdate);
		Utils.disableEntry(etHeatCyclus);
		Utils.disableEntry(etLactationNr);
		Utils.disableEntry(etLastBirth);
		
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
			etHeatCyclus.setText(Individual.DEFAULT_HEATCYCLUS + "");
			etLactationNr.setText("0");
		}
		// if UPDATING
		if(updating) {
			// disable editing IDnr and sex
			Utils.disableEntry(etIdnrOrg);
			Utils.disableEntry(etIdnrPpnr);
			Utils.disableEntry(etIdnrIndividnr);
			Utils.disableEntry(etIdnrChecknr);
			spinSex.setEnabled(false);
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
				etBirthdate.setText(Utils.dateToString(individual.getBirthdate()));
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
				etLastBirth.setText(Utils.dateToString(individual.getLastBirth()));
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
		setupOnClickListeners();
		setupContextMenuViews();
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
		// Set Short nr when IndividNr has text
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
	 * Set up OnClickListeners for buttons.
	 */
	private void setupOnClickListeners() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.individual_edit_imgbutton_thumb:
					openGallery();
					break;
				case R.id.individual_edit_imgbutton_birthdate_calendar:
					pickBirthdate();
					break;
				case R.id.individual_edit_imgbutton_lastbirth_calendar:
					pickLastBirth();
					break;
				case R.id.individual_edit_imgbutton_heatcyclus_edit:
					pickHeatCyclus();
					break;
				case R.id.individual_edit_imgbutton_lactationnr_edit:
					pickLactationNr();
					break;
				}
			}
		};
		ibThumb.setOnClickListener(clickListener);
		ibBirthdateCalendar.setOnClickListener(clickListener);
		ibLastBirthCalendar.setOnClickListener(clickListener);
		ibHeatCyclus.setOnClickListener(clickListener);
		ibLactationNr.setOnClickListener(clickListener);
	}
	
	/**
	 * Register which views to handle long click to pop up Context Menus.
	 */
	private void setupContextMenuViews() {
		registerForContextMenu(ibThumb);
		registerForContextMenu(etBirthdate);
		registerForContextMenu(etLastBirth);
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

	/**
	 * Fire up a NumberPicker dialog to set the HeatCyclus.
	 */
	protected void pickHeatCyclus() {
		pickNumberForThis = etHeatCyclus;
		pickNumberDialog();
	}
	
	/**
	 * Fire up NumberPicker dialog to set the LactationNr.
	 */
	protected void pickLactationNr() {
		pickNumberForThis = etLactationNr;
		pickNumberDialog();
	}

	/**
	 * Get the NumberPicker dialog on screen
	 */
	private void pickNumberDialog() {
		Log.d(TAG, "building NumberPicker");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.number_picker, null);
		
		final NumberPicker picker = (NumberPicker) view.findViewById(R.id.dialog_number_picker);
		String title;
		int value;
		int low;
		int high;
		
		if(pickNumberForThis == etHeatCyclus) {
			title = getString(R.string.dialog_pick_number) + " " + getString(R.string.individual_label_heatcyclus);
			value = Integer.parseInt(pickNumberForThis.getText().toString());
			low = value - 10;
			high = value + 10;
			Log.d(TAG, title + ": " + low + " < " + value + " < " + high);
		}
		else {	// lactation nr
			title = getString(R.string.dialog_pick_number) + " " + getString(R.string.individual_label_lactationnr);
			value = Integer.parseInt(pickNumberForThis.getText().toString());
			low = 0;
			high = 25;
			Log.d(TAG, title + ": " + low + " < " + value + " < " + high);
		}
		
		picker.setMinValue(low);
		picker.setMaxValue(high);
		picker.setValue(value);
		picker.setWrapSelectorWheel(false);
		
		// Build the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		builder.setTitle(title);
		builder.setCancelable(true);
		// SET
		builder.setPositiveButton(R.string.dialog_pick_number, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int val = picker.getValue();
				pickNumberForThis.setText(val + "");
			}
		});
		// CANCEL
		builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Nothing
			}
		});
		builder.show();
	}

	/**
	 * Put up a dialog to pick a date of last birth
	 */
	protected void pickLastBirth() {
		Calendar baseDate;
		if(individual != null && individual.hasLastBirth()) {
			baseDate = individual.getLastBirth();
		}
		else if(etLastBirth.getText().length() > 0) {
			try {
				baseDate = Utils.stringToDate(etLastBirth.getText().toString());
			} catch (ParseException ex) {
				Log.d(TAG, "Error parsing date");
				baseDate = null;
			}
		}
		else {
			baseDate = null;
		}
		DialogFragment dialog = new DatePickerFragment();
		if(baseDate != null) {
			Bundle b = new Bundle();
			b.putInt(DatePickerFragment.YEAR, baseDate.get(Calendar.YEAR));
			b.putInt(DatePickerFragment.MONTH, baseDate.get(Calendar.MONTH));
			b.putInt(DatePickerFragment.DAY, baseDate.get(Calendar.DAY_OF_MONTH));
			dialog.setArguments(b);
			Log.d(TAG, "built a bundle for DatePicker: " + b);
		}
		dialog.show(getSupportFragmentManager(), DIALOG_LASTBIRTH);
	}

	/**
	 * Put up a dialog to pick a date of birth
	 */
	protected void pickBirthdate() {
		Log.d(TAG, "Birthdate Text length: " + etBirthdate.getText().length());
		Calendar baseDate;
		if(individual != null && individual.hasBirthdate()) {
			baseDate = individual.getBirthdate();
		}
		else if(etBirthdate.getText().length() > 0) {
			try {
				baseDate = Utils.stringToDate(etBirthdate.getText().toString());
			} catch (ParseException ex) {
				Log.d(TAG, "Error parsing date");
				baseDate = null;
			}
		}
		else {
			baseDate = null;
		}
		DialogFragment dialog = new DatePickerFragment();
		if(baseDate != null) {
			Bundle b = new Bundle();
			b.putInt(DatePickerFragment.YEAR, baseDate.get(Calendar.YEAR));
			b.putInt(DatePickerFragment.MONTH, baseDate.get(Calendar.MONTH));
			b.putInt(DatePickerFragment.DAY, baseDate.get(Calendar.DAY_OF_MONTH));
			dialog.setArguments(b);
			Log.d(TAG, "built a bundle for DatePicker: " + b);
		}
		dialog.show(getSupportFragmentManager(), DIALOG_BIRTHDATE);
	}

	/**
	 * Disable all widgets not relevant for a Male Individual.
	 */
	protected void setWidgetVisibility() {
		int visibility = female ? View.VISIBLE : View.GONE;
		etHeatCyclus.setVisibility(visibility);
		etLactationNr.setVisibility(visibility);
		etLastBirth.setVisibility(visibility);
		ibHeatCyclus.setVisibility(visibility);
		ibLactationNr.setVisibility(visibility);
		ibLastBirthCalendar.setVisibility(visibility);
		tvHeatCyclus.setVisibility(visibility);
		tvLactationNr.setVisibility(visibility);
		tvLastBirth.setVisibility(visibility);
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
	
	/**
	 * Delete the contents of a view, like remove bitmap of thumb
	 * @param id
	 */
	private void clearView(View view) {
		Log.d(TAG, "clear view " + view);
		if(view == ibThumb) {
			Log.d(TAG, "clear image");
			ibThumb.setImageDrawable(getResources().getDrawable(R.drawable.ic_visualpharm_cow_add_96));
		}
		else if(view == etBirthdate) {
			Log.d(TAG, "clear birthdate");
			etBirthdate.setText("");
		}
		else if(view == etLastBirth) {
			Log.d(TAG, "clear last birth");
			etLastBirth.setText("");
		}
	}
	
	/**
	 * Attempt to save an individual from contents in form
	 */
	private void saveIndividual() {
		Log.d(TAG, "save action");
		
		boolean saveOK = false;
		if(updating) {
			getOptionalFields();
			saveOK = individualDB.saveIndividual(individual);
		}
		else {
			saveOK = createIndividualFromForm();
			Log.d(TAG, "attempt to save INdividual in DB");
			if(saveOK) {
				saveOK = individualDB.saveIndividual(individual);
			}
		}
		
		if(saveOK) {
			Log.d(TAG, "saved OK");
			Intent intent = new Intent(this, MainActivity.class);
			if(currentSite != null) {
				intent.putExtra(MainActivity.EXTRA_SITE_UPDATED, currentSite.getTitle());
			}
			intent.putExtra(MainActivity.EXTRA_INDIVIDUAL_UPDATED, individual.toString());
			startActivity(intent);
		}
		else {
			Log.d(TAG, "failed to save");
			String text = getString(R.string.toast_could_not_save) + " Individ";
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Read the form and try to create an individual
	 */
	private boolean createIndividualFromForm() {
		// first check that the ID nr fields are ok
		IdNr idnr = createIdnrFromForm(ID_OWN);
		if(idnr == null) {
			// put up a toast
			String toastText = getString(R.string.toast_could_not_save) + " Individ";
			Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
			return false;
		}
		Individual.Sex sex = female ? Sex.F : Sex.M;
		// now we can create an individual:
		individual = new Individual(idnr, currentSiteNr, sex);
		
		// and read the rest of the fields
		getOptionalFields();
		
		Log.d(TAG, "created the individual " + individual.toString());
		return true;
	}
	
	/**
	 * Read the optional fields for an individual and set them in the Indivudal object
	 */
	private void getOptionalFields() {
		if(individual != null) {
			// SHORTNR
			try {
				int shortnr = Integer.parseInt(etShortnr.getText().toString());
				individual.setShortNr(shortnr);
			} catch (NumberFormatException ex) {
				Log.d(TAG, "could not parse shortnr");
			}
			// NAME
			individual.setName(etName.getText().toString());
			// BIRTHDATE
			String birthdateString = etBirthdate.getText().toString();
			if(birthdateString.length() > 0) {
				try {
					individual.setBirthdate(Utils.stringToDate(birthdateString));
				} catch (ParseException e) {
					Log.d(TAG, "could not parse birthdate: " + birthdateString);
				}
			}
			// MOTHER ID
			IdNr motherIdNr = createIdnrFromForm(ID_MOTHER);
			if(motherIdNr != null) {
				individual.setMotherIdNr(motherIdNr);
			}
			// FATHER ID
			IdNr fatherIdNr = createIdnrFromForm(ID_FATHER);
			if(fatherIdNr != null) {
				individual.setFatherIdNr(fatherIdNr);
			}
			// HEAT CYCLUS
			try {
				int heat = Integer.parseInt(etHeatCyclus.getText().toString());
				individual.setHeatcyclus(heat);
			} catch (NumberFormatException ex) {
				Log.d(TAG, "could not parse heat");
			}
			// HEAT CYCLUS
			try {
				int lactation = Integer.parseInt(etLactationNr.getText().toString());
				individual.setLactationNr(lactation);
			} catch (NumberFormatException ex) {
				Log.d(TAG, "could not parse lactation nr");
			}
			// LAST BIRTH
			String lastBirthString = etLastBirth.getText().toString();
			if(lastBirthString.length() > 0) {
				try {
					individual.setLastBirth(Utils.stringToDate(lastBirthString));
				} catch (ParseException e) {
					Log.d(TAG, "could not parse last birth: " + lastBirthString);
				}
			}
			// IMAGE URI
			if(imageUri != null) {
				Log.d(TAG, "Saving URI: " + imageUri.toString());
				individual.setImageUri(imageUri.toString());
			}
		}
	}

	/**
	 * Read the related ID fields an try to create an IdNr from that
	 * @param	id	Which Id to construct: own, mother, father
	 * @return	a valid IdNr or null
	 */
	private IdNr createIdnrFromForm(String id) {
		IdNr idnr = null;
		
		String org = "";
		String ppnr = "";
		String individnr = "";
		String checknr = "";
		
		switch(id) {
		case ID_OWN:
			org = etIdnrOrg.getText().toString();
			ppnr = etIdnrPpnr.getText().toString();
			individnr = etIdnrIndividnr.getText().toString();
			checknr = etIdnrChecknr.getText().toString();
			break;
		case ID_MOTHER:
			org = etMotherOrg.getText().toString();
			ppnr = etMotherPpnr.getText().toString();
			individnr = etMotherIndividnr.getText().toString();
			checknr = etMotherChecknr.getText().toString();
			break;
		case ID_FATHER:
			org = etFatherOrg.getText().toString();
			ppnr = etFatherPpnr.getText().toString();
			individnr = etFatherIndividnr.getText().toString();
			checknr = etFatherChecknr.getText().toString();
			break;
		}
		
		if(org.length() > 0 && ppnr.length() > 0 && individnr.length() > 0 && checknr.length() > 0) {
			// pad the fields
			org = zeroPad(org, 2);
			ppnr = zeroPad(ppnr, 6);
			individnr = zeroPad(individnr, 4);
			checknr = zeroPad(checknr, 1);
			String idnrStr = org + "-" + ppnr + "-" + individnr + "-" + checknr;
			try {
				idnr = new IdNr(idnrStr);
			} catch (ParseException e) {
				Log.d(TAG, "malformed idnr string: " + idnrStr);
			}
		}
		return idnr;
	}
	
	/**
	 * Are all the fields for the IDnr filled in?
	 * @return
	 */
	private boolean validIdNrForm() {
		return (etIdnrOrg.length() > 0 && 
				etIdnrPpnr.length() > 0 && 
				etIdnrIndividnr.length() > 0 && 
				etIdnrChecknr.length() > 0);
	}

	/**
	 * Create a zero padded string of a certain length
	 * @param str
	 * @param len
	 * @return
	 */
	private String zeroPad(String str, int len) {
		while(str.length() < len) {
			str = "0" + str;
		}
		if(str.length() > len) {
			str = str.substring(0, len);
		}
		return str;
	}

	/**
	 * Perform deletion and return to MainActivity.
	 */
	private void doDelete() {
		int rowsAffected = individualDB.deleteIndividual(individual.getIdNr());
		Log.d(TAG, "nr of deleted rows: " + rowsAffected);
		
		// back to main
		Intent intent = new Intent(this, MainActivity.class);;
		startActivity(intent);
	}
	
	/**
	 * Show a dialog, confirming the user's wish to delete an Individual.
	 */
	private void showDialogDelete() {
		if(individual != null) {
			// ask for confirmation first
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.dialog_ask_delete_individual) + " " + individual.toString() + "?");
			builder.setCancelable(true);
			// YES button
			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doDelete();	
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
	}
	
	/**
	 * Show a dialog, confirming the user's wish to save an individual
	 */
	private void showDialogSave() {
		if(validIdNrForm()) {
			// ask for confirmation first
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			IdNr idnr = createIdnrFromForm(ID_OWN);
			builder.setMessage(getString(R.string.dialog_ask_save) + " " + idnr.toString() + "?");
			builder.setCancelable(true);
			// YES button
			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveIndividual();
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
	
	/**
	 * Put up a dialog to pick the date.
	 * Should give arguments in a bundle.
	 */
	public static class DatePickerFragment extends DialogFragment
    	implements DatePickerDialog.OnDateSetListener 
    {
		public static final String YEAR = "DatePickerFragment.YEAR";
		public static final String MONTH = "DatePickerFragment.MONTH";
		public static final String DAY = "DatePickerFragment.DAY";

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	Bundle arguments = getArguments();
        	Log.d(TAG, "arguments to DatePicker: " + arguments);
        	
        	int year;
        	int month;
        	int day;
        	// if we have set a date as the base
        	if(arguments != null) {
        		Log.d(TAG, "has arguments");
        		year = arguments.getInt(YEAR);
        		month = arguments.getInt(MONTH);
        		day = arguments.getInt(DAY);
        	}
        	else {
        		Calendar c = Calendar.getInstance();
        		year = c.get(Calendar.YEAR);
            	month = c.get(Calendar.MONTH);
            	day = c.get(Calendar.DAY_OF_MONTH);
        	}
        	
        	
        	final DatePickerDialog dialog = new DatePickerDialog(getActivity(), null, 
        			year, month, day);
        	
        	dialog.setCancelable(true);
        	dialog.setCanceledOnTouchOutside(true);
        	
        	dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_choose),
        			new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface di, int which) {
							DatePicker picker = dialog.getDatePicker();
							picker.clearFocus();
							onDateSet(picker, picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
						}
					});
        	dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_cancel), 
        			new DialogInterface.OnClickListener() {
        				@Override
        				public void onClick(DialogInterface di, int which) {
        					// Nothing?
        				}
        			});
        	
        	return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
        	Calendar c = Calendar.getInstance();
        	c.set(year, month, day);

        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String dateString = sdf.format(c.getTime());
        	EditText et;
        	
        	switch(getTag()) {
        	case DIALOG_BIRTHDATE:
        		et = (EditText) getActivity().findViewById(R.id.individual_edit_entry_birthdate);
        		et.setText(dateString);
        		break;
        	case DIALOG_LASTBIRTH:
        		et = (EditText) getActivity().findViewById(R.id.individual_edit_entry_lastbirth);
        		et.setText(dateString);
        		break;
        	}
        }
        
        @Override
        public void onCancel(DialogInterface dialog) {
        	// TODO Auto-generated method stub
        	super.onCancel(dialog);
        }
	} // end DatePickerFragment	

}
