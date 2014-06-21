package com.example.android_projekt.event;

import java.util.Calendar;

import com.example.android_projekt.MainActivity;
import com.example.android_projekt.R;
import com.example.android_projekt.Utils;
import com.example.android_projekt.R.color;
import com.example.android_projekt.R.id;
import com.example.android_projekt.R.layout;
import com.example.android_projekt.R.menu;
import com.example.android_projekt.R.string;
import com.example.android_projekt.event.Reminder.Type;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.os.Build;

/**
 * Simple activity to display contents of a Reminder
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class ReminderActivity extends ActionBarActivity 
{
	private static final String TAG = "Brunst: ReminderActivity";
	
	public static final String EXTRA_REMINDER = "brunst.extra.ReminderActivity.Reminder";
	
	// WIDGETS
	private Button btnAgain;
	private Button btnNot;
//	private EditText etDescription;
	private EditText etDays;
	private EditText etHours;
	private ImageButton ibDays;
	private ImageButton ibHours;
	private ImageView ivImg;
	private TextView tvEventTime;
	private TextView tvEventType;
	private TextView etDescription;
	
	private EditText pickNumberForThis;	// the EditText for the NumberPicker to set
	
	private Reminder reminder;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		
		Intent intent = getIntent();
		if(intent.hasExtra(EXTRA_REMINDER)) {
			reminder = (Reminder) intent.getSerializableExtra(EXTRA_REMINDER);
			Log.d(TAG, "received intent extra reminder: " + reminder.toString());
		}
		
		if(reminder != null) {
			findVews();
			prepareViews();
			setupListeners();
		}
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reminder, menu);
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
	
	/**
	 * Get references to the different Views.
	 */
	private void findVews() {
		btnAgain = (Button) findViewById(R.id.reminder_button_remind_again);
		btnNot = (Button) findViewById(R.id.reminder_button_remind_not);
		etDescription = (TextView) findViewById(R.id.reminder_entry_description);
		etDays = (EditText) findViewById(R.id.reminder_entry_days);
		etHours = (EditText) findViewById(R.id.reminder_entry_hours);
		ibDays = (ImageButton) findViewById(R.id.reminder_imgbutton_edit_days);
		ibHours = (ImageButton) findViewById(R.id.reminder_imgbutton_edit_hours);
		ivImg = (ImageView) findViewById(R.id.reminder_img);
		tvEventTime = (TextView) findViewById(R.id.reminder_label_eventtime);
		tvEventType = (TextView) findViewById(R.id.reminder_label_eventtype);
	}

	/**
	 * Disable TextEntries and fill Views with contents.
	 */
	private void prepareViews() {
		Utils.disableEntry(etDays);
//		Utils.disableEntry(etDescription);
		Utils.disableEntry(etHours);
		
		// background of image depending on type of reminder
		Reminder.Type remType = reminder.getType();
		// if 'now' has passed eventTime, set to Seriuos
		Calendar now = Calendar.getInstance();
		if(reminder.getEventTime().before(now)) {
			remType = Type.REMINDER_SERIOUS;
		}
		int backgroundColorId = R.color.orange_light;
		switch(remType) {
		case REMINDER_NORMAL:
			backgroundColorId = R.color.orange_light;
			break;
		case REMINDER_ALERT:
			backgroundColorId = R.color.purple_light;
			break;
		case REMINDER_SERIOUS:
			backgroundColorId = R.color.red_light;
			break;
		}
		Log.d(TAG, "reminder type: " + reminder.getType());
		// if it is not time to remind just yet, make background green
		if(now.before(reminder.getReminderTime())) {
			backgroundColorId = R.color.green_semi_transparent;
		}
		
		ivImg.setBackgroundResource(backgroundColorId);
		// event time
		tvEventTime.setText(Utils.dateToString(reminder.getEventTime()));
		// event type
		Log.d(TAG, "reminder event type: " + reminder.getEventType());
		String typeStr = getString(R.string.reminder_label_eventtype) + " ";
		switch (reminder.getEventType()) {
		case EVENT_NOTE:
			typeStr += getString(R.string.event_type_note);
			break;
		case EVENT_HEAT:
			typeStr += getString(R.string.event_type_heat);
			break;
		case EVENT_MATING:
			typeStr += getString(R.string.event_type_mating);
			break;
		case EVENT_PREGCHECK:
			typeStr += getString(R.string.event_type_pregcheck);
			break;
		case EVENT_BIRTH:
			typeStr += getString(R.string.event_type_birth);
			break;
		}
		tvEventType.setText(typeStr);
		// description
		etDescription.setText(reminder.getDescription());
		// remind in days
		etDays.setText(reminder.getReminderIntervalDay() + "");
		// remind in hours
		etHours.setText(reminder.getReminderIntervalHour() + "");
	}

	/**
	 * Have widgets listen for events.
	 */
	private void setupListeners() {
		setupOnClickListeners();
	}

	/**
	 * Have buttons listen for clicks.
	 */
	private void setupOnClickListeners() {
		OnClickListener cl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.reminder_button_remind_again:
					remindAgain();
					break;
				case R.id.reminder_button_remind_not:
					remindNoMore();
					break;
				case R.id.reminder_imgbutton_edit_days:
					pickDays();
					break;
				case R.id.reminder_imgbutton_edit_hours:
					pickHours();
					break;
				}
				
			}
		};
		ibDays.setOnClickListener(cl);
		ibHours.setOnClickListener(cl);
		btnAgain.setOnClickListener(cl);
		btnNot.setOnClickListener(cl);
	}

	/**
	 * Create a new reminder
	 */
	protected void remindAgain() {
		Reminder newReminder = new Reminder(reminder);
		// set time of new reminder
		int dayInterval = Integer.parseInt(etDays.getText().toString());
		int hourInterval = Integer.parseInt(etHours.getText().toString());
		Calendar newTime = (Calendar) reminder.getReminderTime().clone();
		newTime.add(Calendar.DATE, dayInterval);
		newTime.add(Calendar.HOUR_OF_DAY, hourInterval);
		// make sure the reminder time is not after event time
		if(reminder.getEventTime().before(newTime)) {
			newReminder.setReminderTime(reminder.getEventTime());
		}
		else {
			newReminder.setReminderTime(newTime);
		}
		Log.d(TAG, "old time: " + Utils.datetimeToString(reminder.getReminderTime()));
		Log.d(TAG, "new time:" + Utils.datetimeToString(newReminder.getReminderTime()));
		
		newReminder.setReminderIntervalDay(dayInterval);
		newReminder.setReminderIntervalHour(hourInterval);
		
		ReminderDB rdb = new ReminderDB(this);
		rdb.open();
		rdb.saveReminder(newReminder);
		rdb.inactivateReminder(reminder.getReminderId());
		rdb.close();
		
		// back to Main
		startActivity(new Intent(this, MainActivity.class));
		
	}

	/**
	 * Don't remind about this any more.
	 */
	protected void remindNoMore() {
		ReminderDB rdb = new ReminderDB(this);
		rdb.open();
		rdb.inactivateReminder(reminder.getReminderId());
		rdb.close();
		
		// back to Main
		startActivity(new Intent(this, MainActivity.class));
	}

	/**
	 * Get a Number picker dialog up
	 */
	protected void pickDays() {
		pickNumberForThis = etDays;
		pickNumberDialog();
	}

	/**
	 * Get a number picker dialog up
	 */
	protected void pickHours() {
		pickNumberForThis = etHours;
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
		
		if(pickNumberForThis == etDays) {
			title = getString(R.string.dialog_pick_number) + " " + getString(R.string.reminder_label_days);
			value = Integer.parseInt(pickNumberForThis.getText().toString());
			low = 0;
			high = daysTilEvent();
//			high = value + 10;	// hm, should check against evetnTime
			Log.d(TAG, title + ": " + low + " < " + value + " < " + high);
		}
		else {	// Hours
			title = getString(R.string.dialog_pick_number) + " " + getString(R.string.reminder_label_hours);
			value = Integer.parseInt(pickNumberForThis.getText().toString());
			low = 0;
			high = 24;
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
	 * Find the difference in days from now till EventTime
	 * @return
	 */
	private int daysTilEvent() {
		int days = 0;
		Calendar cal = Calendar.getInstance();
		while(cal.before(reminder.getEventTime())) {
			++days;
			cal.add(Calendar.DATE, 1);
		}
		
		return days;
	}
	
	

}
