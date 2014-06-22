package com.example.android_projekt.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receive an intent from the AlarmManager to start the ReminderService
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class ReminderServiceStartReceiver extends BroadcastReceiver
{
	private static final String TAG = "Brunst: ReminderServiceStartReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "starting the ReminderService");
		Intent serviceIntent = new Intent(context, ReminderService.class);
		context.startService(serviceIntent);
	}
}
