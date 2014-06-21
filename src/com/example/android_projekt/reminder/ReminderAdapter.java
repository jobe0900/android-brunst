package com.example.android_projekt.reminder;

import java.util.Calendar;
import java.util.List;

import com.example.android_projekt.R;
import com.example.android_projekt.Utils;
import com.example.android_projekt.reminder.Reminder.Type;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An Adapter to display Reminders in a list
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class ReminderAdapter extends ArrayAdapter<Reminder>
{
	private final Context context;
	private final List<Reminder> reminders;
	
	/** Constructor. */
	public ReminderAdapter(Context context, List<Reminder> reminders) {
		super(context, R.layout.list_row_reminder, reminders);
		this.context = context;
		this.reminders = reminders;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.list_row_reminder, parent, false);
		ImageView img = (ImageView) rowView.findViewById(R.id.list_row_reminder_img);
		TextView line1 = (TextView) rowView.findViewById(R.id.list_row_reminder_line1);
		TextView line2 = (TextView) rowView.findViewById(R.id.list_row_reminder_line2);
		
		Reminder reminder = reminders.get(position);
		
		// fill in the text
		line1.setText(Utils.dateToString(reminder.getEventTime()));
		line2.setText(reminder.getDescription());
		
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
		// if it is not time to remind just yet, make background green
		if(now.before(reminder.getReminderTime())) {
			backgroundColorId = R.color.green_semi_transparent;
		}
		img.setBackgroundResource(backgroundColorId);
		return rowView;
	}
}
