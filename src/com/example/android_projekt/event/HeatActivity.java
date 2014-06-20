package com.example.android_projekt.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.android_projekt.R;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.Utils;
import com.example.android_projekt.individ.Individual;
import com.example.android_projekt.individ.IndividualDB;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
		Calendar cal = Calendar.getInstance();
		
		// top title
		String titleStr = getString(R.string.heat_label_individual_title) + individual.toString();
		tvIndividual.setText(titleStr);
		
		// lactation nr
		etLactationnr.setText(individual.getLactationNr());
		
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
			etHeatRound.setText(lastRound);
			
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
        	// TODO Auto-generated method stub
        	super.onCancel(dialog);
        }
	} // end DatePickerFragment	

	
	/**
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
