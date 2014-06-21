package com.example.android_projekt.event;

import java.util.List;

import com.example.android_projekt.R;
import com.example.android_projekt.Utils;

import android.content.Context;
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
		
		// set background of img
		Reminder.Type remType = reminder.getType();
		int backgroundId = R.color.orange_light;
		switch(remType) {
		case REMINDER_NORMAL:
			backgroundId = R.color.orange_light;
			break;
		case REMINDER_ALERT:
			backgroundId = R.color.purple_light;
			break;
		case REMINDER_SERIOUS:
			backgroundId = R.color.red_light;
			break;
		}
		img.setBackgroundResource(backgroundId);
		return rowView;
	}
}
