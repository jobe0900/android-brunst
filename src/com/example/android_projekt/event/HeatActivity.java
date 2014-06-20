package com.example.android_projekt.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.android_projekt.R;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.Utils;
import com.example.android_projekt.event.HeatEvent.Sign;
import com.example.android_projekt.event.HeatEvent.Strength;
import com.example.android_projekt.individ.IdNr;
import com.example.android_projekt.individ.Individual;
import com.example.android_projekt.individ.IndividualDB;
import com.example.android_projekt.individ.IndividualEventsActivity;
import com.example.android_projekt.individ.IndividualEditActivity.DatePickerFragment;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
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
	private static final String DIALOG_DATE = "pickDateDialog";
	private static final String DIALOG_TIME = "pickTimeDialog";
	
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
	
	private ArrayAdapter<CharSequence> heatSignAdapter;
	private ArrayAdapter<CharSequence> heatStrengthAdapter;
	
	private Individual individual;
	private HeatEvent heat;
	private boolean editing = true;	// editing a new or just viewing an old heat?
	private boolean createReminder = false;
	private EditText pickNumberForThis;		// the entry connected to a number picker

	
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
			// should have passed in individual in same event
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
	public boolean onPrepareOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.heat_activity_actions, menu);
		// set visibility of "Save" depending on editing or just viewing
		menu.getItem(0).setVisible(editing);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.heat_action_save) {
			showDialogSave();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Get the context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_delete, menu); 
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.context_menu_delete:
			Log.d(TAG, "Pick context Delete");
			etNote.setText("");
			return true;
		default:
			return super.onContextItemSelected(item);
		}
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
		Calendar cal = Calendar.getInstance();
		
		// top title
		String titleStr = getString(R.string.heat_label_individual_title) + individual.toString();
		tvIndividual.setText(titleStr);
		
		// lactation nr
		etLactationnr.setText(individual.getLactationNr() + "");
		
		// spinners
		heatSignAdapter = ArrayAdapter.createFromResource(this, R.array.heat_sign, R.layout.simple_spinner_item);
		spinSign.setAdapter(heatSignAdapter);
		heatStrengthAdapter = ArrayAdapter.createFromResource(this, R.array.heat_strength, R.layout.simple_spinner_item);
		spinStrength.setAdapter(heatStrengthAdapter);
		
		// remind
		cbRemind.setChecked(false);
		
		if(editing) {
			// heat round
			int lastRound = heatDB.getRoundNr();
			etHeatRound.setText(lastRound + "");
			
			// date
			etDate.setText(Utils.dateToString(cal));
			
			// time
			etTime.setText(Utils.timeToString(cal));
			
			// remind
			etRemind.setText(individual.getHeatcyclus() + "");
		}
		// just viewing
		else {
			etHeatRound.setText(heat.getHeatRound() + "");
			etDate.setText(Utils.dateToString(heat.getEventTime()));
			etTime.setText(Utils.timeToString(heat.getEventTime()));
			if(heat.hasNote()) {
				etNote.setText(heat.getNote());
			}
			// hide reminder
			cbRemind.setVisibility(View.GONE);
			etRemind.setVisibility(View.GONE);
			ibRemind.setVisibility(View.GONE);
			ibHeatRound.setEnabled(false);
			ibDate.setEnabled(false);
			ibTime.setEnabled(false);
			spinSign.setEnabled(false);
			spinStrength.setEnabled(false);
			Utils.disableEntry(etNote);
		}
		
		// disable entries
		Utils.disableEntry(etLactationnr);
		Utils.disableEntry(etHeatRound);
		Utils.disableEntry(etDate);
		Utils.disableEntry(etTime);
		Utils.disableEntry(etRemind);
	}

	/**
	 * Have the widgets listen for events
	 */
	private void setUpListeners() {
		setupOnClickListeners();
		setupOnItemSelectedListeners();
		// have the note listen for context menu (long click)
		registerForContextMenu(etNote);
	}

	/**
	 * Handle clicks on the checkbox
	 * @param view
	 */
	public void onCheckBoxClicked(View view) {
		// Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    // Make sure it is our one and only checkbox
	    switch(view.getId()) {
	        case R.id.heat_checkbox_remind:
	        	createReminder = checked;
	        	ibRemind.setEnabled(checked);	// set state of edit button
	            break;
	    }
	}
	
	/**
	 * Set up listeners for buttons
	 */
	private void setupOnClickListeners() {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.heat_imgbutton_edit_heatround:
					pickHeatRound();
					break;
				case R.id.heat_imgbutton_edit_date:
					pickDate();
					break;
				case R.id.heat_imgbutton_edit_time:
					pickTime();
					break;
				case R.id.heat_imgbutton_edit_remind:
					pickRemind();
					break;
				}
			}
		};
		ibHeatRound.setOnClickListener(clickListener);
		ibDate.setOnClickListener(clickListener);
		ibTime.setOnClickListener(clickListener);
		ibRemind.setOnClickListener(clickListener);
	}

	/**
	 * Listen for selections in the spinners. 
	 */
	private void setupOnItemSelectedListeners() {
//		OnItemSelectedListener selectListener = new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				switch(view.getId()) {
//				case R.id.heat_spinner_sign:
//					// something
//					break;
//				case R.id.heat_spinner_strength:
//					//
//					break;
//				}
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
		
	}
	
	/**
	 * Put up a NumberPicker for picking heat round
	 */
	protected void pickHeatRound() {
		pickNumberForThis = etHeatRound;
		pickNumberDialog();
	}

	/**
	 * Put up a NumberPicker for picking time till Reminder
	 */
	protected void pickRemind() {
		pickNumberForThis = etRemind;
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
		
		if(pickNumberForThis == etHeatRound) {
			title = getString(R.string.dialog_pick_number) + " " + getString(R.string.heat_label_heatround);
			value = Integer.parseInt(pickNumberForThis.getText().toString());
			low = 1;
			high = value + 5;
			Log.d(TAG, title + ": " + low + " < " + value + " < " + high);
		}
		else {	// Reminder
			title = getString(R.string.heat_label_remind);
			value = Integer.parseInt(pickNumberForThis.getText().toString());
			low = value - 10;
			high = value + 10;
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
	 * Put up a dialog to pick a date for heat event
	 */
	protected void pickDate() {
		Calendar baseDate;
		try {
			baseDate = Utils.stringToDate(etDate.getText().toString());
		} catch (ParseException ex) {
			Log.d(TAG, "Error parsing date");
			baseDate = null;
		}
		
		if(baseDate != null) {
			DialogFragment dialog = new DatePickerFragment();
			Bundle b = new Bundle();
			b.putInt(DatePickerFragment.YEAR, baseDate.get(Calendar.YEAR));
			b.putInt(DatePickerFragment.MONTH, baseDate.get(Calendar.MONTH));
			b.putInt(DatePickerFragment.DAY, baseDate.get(Calendar.DAY_OF_MONTH));
			dialog.setArguments(b);
			Log.d(TAG, "built a bundle for DatePicker: " + b);
			dialog.show(getSupportFragmentManager(), DIALOG_DATE);
		}
	}
	
	/**
	 * Put up a dialog to pick a time for heat event
	 */
	protected void pickTime() {
		Calendar baseTime;
		try {
			baseTime = Utils.stringToTime(etTime.getText().toString());
		} catch (ParseException ex) {
			Log.d(TAG, "Error parsing time");
			baseTime = null;
		}
		
		if(baseTime != null) {
			DialogFragment dialog = new TimePickerFragment();
			Bundle b = new Bundle();
			b.putInt(TimePickerFragment.HOUR, baseTime.get(Calendar.HOUR_OF_DAY));
			b.putInt(TimePickerFragment.MINUTE, baseTime.get(Calendar.MINUTE));
			dialog.setArguments(b);
			Log.d(TAG, "built arguments for Time Picker: " + b);
			dialog.show(getSupportFragmentManager(), DIALOG_TIME);
		}
		
	}
	
	/**
	 * Show a dialog, confirming the user's wish to save a ProductionSite.
	 */
	private void showDialogSave() {
//		if(true) {
			// ask for confirmation first
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			IdNr idnr = createIdnrFromForm(ID_OWN);
//			if(etName.length() > 0) {
//				siteStr += etName.getText().toString();
//			}
			builder.setMessage(getString(R.string.dialog_ask_save) + "?");
			builder.setCancelable(true);
			// YES button
			builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveHeatEvent();
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
//		}
//		else {
//			Toast.makeText(this, R.string.toast_form_not_correct, Toast.LENGTH_SHORT).show();
//		}
	}
	
	/**
	 * Save this heat event to database.
	 */
	protected void saveHeatEvent() {
		// heat round
		heat.setHeatRound(Integer.parseInt(etHeatRound.getText().toString())); // should not throw
		Log.d(TAG, "save heat round: " + heat.getHeatRound());
		// event time
		try {
			Calendar eventDate = Utils.stringToDate(etDate.getText().toString());
			Log.d(TAG, "save date: " + etDate.getText().toString());
			Log.d(TAG, "date saved: " + Utils.datetimeToString(eventDate));
			Calendar eventTime = Utils.stringToTime(etTime.getText().toString());
			Log.d(TAG, "save time: " + etTime.getText().toString());
			//get them in the same calendar
			Calendar result = Calendar.getInstance();
			result.set(Calendar.YEAR, eventDate.get(Calendar.YEAR));
			result.set(Calendar.MONTH, eventDate.get(Calendar.MONTH));
			result.set(Calendar.DATE, eventDate.get(Calendar.DATE));
			result.set(Calendar.HOUR, eventTime.get(Calendar.HOUR));
			result.set(Calendar.MINUTE, eventTime.get(Calendar.MINUTE));
			Log.d(TAG, "result date saved: " + Utils.datetimeToString(result));
			
//			eventDate.add(Calendar.HOUR_OF_DAY, eventTime.get(Calendar.HOUR_OF_DAY));
//			eventDate.add(Calendar.MINUTE, eventTime.get(Calendar.MINUTE));
			heat.setEventTime(result);
			Log.d(TAG, "save date and time: " + Utils.datetimeToString(heat.getEventTime()));
		} catch (ParseException e) {
			Log.d(TAG, "error parsing date and time fileds in form");
			return;
		}
		// heat sign
		String signString = (String) spinSign.getSelectedItem();
		HeatEvent.Sign sign = getSignFromString(signString);
		heat.setSign(sign);
		Log.d(TAG, "save sign :" + heat.getSign());
		// heat strength
		String strengthString = (String) spinStrength.getSelectedItem();
		HeatEvent.Strength stren = getStrengthFromString(strengthString);
		heat.setStrength(stren);
		Log.d(TAG, "save stren :" + heat.getStrength());
		// note
		if(etNote.length() > 0) {
			heat.setNote(etNote.getText().toString());
			Log.d(TAG, "save note:" + heat.getNote());
		}
		
		// perform the save
		heatDB.saveHeatEvent(heat);
		
		// TODO create reminder
		if(createReminder) {
			int remindIn = Integer.parseInt(etRemind.getText().toString());
			Calendar remindDate = Calendar.getInstance();
			remindDate.add(Calendar.DATE, remindIn);
			Log.d(TAG, "should create reminder at: " + Utils.datetimeToString(remindDate));
			// new reminder
		}
		
		// return to Individual Events
		Intent intent = new Intent(this, IndividualEventsActivity.class);
		intent.putExtra(IndividualEventsActivity.EXTRA_IDNR, heat.getIdnr().toString());
		startActivity(intent);
	}
	
	/**
	 * Find out which Sign was selected in the spinner.
	 * @param	signStr		The string in the Sign spinner 
	 * @return the corresponding Sign
	 */
	private HeatEvent.Sign getSignFromString(String signStr) {
		HeatEvent.Sign sign = Sign.HEATSIGN_DISCHARGE;
		if(signStr.equalsIgnoreCase(getString(R.string.heat_sign_unrest))) {
			sign = Sign.HEATSIGN_UNREST;
		}
		else if(signStr.equalsIgnoreCase(getString(R.string.heat_sign_discharge))) {
			sign = Sign.HEATSIGN_DISCHARGE;
		}
		else if(signStr.equalsIgnoreCase(getString(R.string.heat_sign_riding))) {
			sign = Sign.HEATSIGN_RIDING;
		}
		else if(signStr.equalsIgnoreCase(getString(R.string.heat_sign_sag))) {
			sign = Sign.HEATSIGN_SAG;
		}
		else if(signStr.equalsIgnoreCase(getString(R.string.heat_sign_swelling))) {
			sign = Sign.HEATSIGN_SWELLING;
		}
		else if(signStr.equalsIgnoreCase(getString(R.string.heat_sign_lowmilk))) {
			sign = Sign.HEATSIGN_LOWMILK;
		}
		return sign;
	}
	
	/**
	 * Find out which Strength was selected in the spinner.
	 * @param	str		The string in the Strength spinner 
	 * @return the corresponding Strength
	 */
	private HeatEvent.Strength getStrengthFromString(String str) {
		HeatEvent.Strength stren = Strength.HEATSTREN_3;
		if(str.equalsIgnoreCase(getString(R.string.heat_strength_1))) {
			stren = Strength.HEATSTREN_1;
		}
		else if(str.equalsIgnoreCase(getString(R.string.heat_strength_2))) {
			stren = Strength.HEATSTREN_2;
		}
		else if(str.equalsIgnoreCase(getString(R.string.heat_strength_3))) {
			stren = Strength.HEATSTREN_3;
		}
		else if(str.equalsIgnoreCase(getString(R.string.heat_strength_4))) {
			stren = Strength.HEATSTREN_4;
		}
		else if(str.equalsIgnoreCase(getString(R.string.heat_strength_5))) {
			stren = Strength.HEATSTREN_5;
		}
		return stren;
	}

	/***************************************************************************
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
        	case DIALOG_DATE:
        		et = (EditText) getActivity().findViewById(R.id.heat_entry_date);
        		et.setText(dateString);
        		break;
        	}
        }
        
        @Override
        public void onCancel(DialogInterface dialog) {
        	super.onCancel(dialog);
        }
	} // end DatePickerFragment	

	
	/***************************************************************************
	 * Get a time picker on Screen.
	 * Should have arguments in Bundle
	 */
	public static class TimePickerFragment extends DialogFragment
		implements TimePickerDialog.OnTimeSetListener 
	{
		public static final String HOUR = "TimePickerFragment.HOUR";
		public static final String MINUTE = "TimePickerFragment.MINUTE";
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle arguments = getArguments();
			Log.d(TAG, "arguments to TimePicker: " + arguments);
			
			int hour;
			int min;
			
			// if we have arguments
			if(arguments != null) {
				Log.d(TAG, "has arguments");
				hour = arguments.getInt(HOUR);
				min = arguments.getInt(MINUTE);
			}
			else {
				Calendar c = Calendar.getInstance();
				hour =  c.get(Calendar.HOUR_OF_DAY);
				min = c.get(Calendar.MINUTE);
			}
			
			final TimePickerDialog dialog = 
					new TimePickerDialog(getActivity(), null, hour, min, true);
			
			// handle buttons our selves
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);
			
//			dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.dialog_choose),
//        			new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface di, int which) {
//							TimePicker picker = dialog.get
//							picker.clearFocus();
//							onDateSet(picker, picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
//						}
//					});
//        	dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.dialog_cancel), 
//        			new DialogInterface.OnClickListener() {
//        				@Override
//        				public void onClick(DialogInterface di, int which) {
//        					// Nothing?
//        				}
//        			});
//        	
//        	return dialog;
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, min,
					DateFormat.is24HourFormat(getActivity()));
		}


		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			
			EditText etTime = (EditText) getActivity().findViewById(R.id.heat_entry_time);
			etTime.setText(Utils.timeToString(c));
		}
	}
}
