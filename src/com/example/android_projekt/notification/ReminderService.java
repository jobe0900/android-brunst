package com.example.android_projekt.notification;

import java.util.List;

import com.example.android_projekt.MainActivity;
import com.example.android_projekt.R;
import com.example.android_projekt.event.Event;
import com.example.android_projekt.reminder.Reminder;
import com.example.android_projekt.reminder.ReminderActivity;
import com.example.android_projekt.reminder.ReminderDB;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * A service that check for new active Reminders in the database.
 * To be started regularly by the AlarmManager.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class ReminderService extends IntentService
{
	private static final String TAG = "Brunst: ReminderService";
	
	/** Constructor. */
	public ReminderService() {
		super("BrunstReminderService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.d(TAG, "checking for reminders");
		
		// query the db for Reminders
		ReminderDB rdb = new ReminderDB(this);
		rdb.open();
//		List<Reminder> reminders = rdb.getAllCurrentReminders();
		List<Reminder> reminders = rdb.getAllReminders();
		rdb.close();
		
		int nrReminders = reminders.size();
		// if only one intent
		if(nrReminders == 1) {
			createSingleReminderNotification(reminders);
		}
		else if(nrReminders > 1) {
			createMultipleReminderNotification(reminders);
		}
	}

	/**
	 * Create a Notification that on click takes you directly to the
	 * ReminderActivity so you can view that specific notification.
	 * @param reminders
	 */
	private void createSingleReminderNotification(List<Reminder> reminders) {
		Reminder reminder = reminders.get(0);
		Log.d(TAG, "single reminder: " + reminder.toString());
		
		// pass the reminder as an intent extra
		Intent intent = new Intent(this, ReminderActivity.class);
		intent.putExtra(ReminderActivity.EXTRA_REMINDER, reminder);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
		
		String title = getString(Event.getStringIdForType(reminder.getEventType()));
		String text = reminder.getDescription();
		
		// Build the notification
		Notification not = new NotificationCompat.Builder(this)
			.setContentTitle(title)
			.setContentText(text)
			.setSmallIcon(R.drawable.ic_stat_notificationlogo)
			.setContentIntent(pintent).build();
		not.flags |= Notification.FLAG_AUTO_CANCEL;
		
		// get notification on screen
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, not);
	}

	/**
	 * Create a Notification that shows how many Reminders you have pending
	 * ant on click takes you to the MainActivity so you from there can click
	 * on Reminder of choice.
	 * @param reminders
	 */
	private void createMultipleReminderNotification(List<Reminder> reminders) {
		int nrReminders = reminders.size();
		Log.d(TAG, "several remindera: " + nrReminders);
		
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, 0);
		
		String title = nrReminders + getString(R.string.reminder);
		String text = reminders.get(0).getDescription();	// top reminder
		
		// Build the notification
		Notification not = new NotificationCompat.Builder(this)
			.setContentTitle(title)
			.setContentText(text)
			.setSmallIcon(R.drawable.ic_stat_notificationlogo)
			.setContentIntent(pintent).build();
		not.flags |= Notification.FLAG_AUTO_CANCEL;

		// get notification on screen
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, not);
	}

}
