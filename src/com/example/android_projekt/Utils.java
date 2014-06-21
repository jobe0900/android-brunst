package com.example.android_projekt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Utility functions usable through out the App.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class Utils 
{
	private static final String TAG = "Brunst: Utils";
	
	public static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	
	/**
	 * Get "HH:mm" from Calendar.
	 * @param cal
	 * @return
	 */
	public static String timeToString(Calendar cal) {
		return TIME_FORMAT.format(cal.getTime());
	}
	
	/**
	 * Get a Calendar from timeString like "HH:mm".
	 * @param timeString
	 * @return
	 * @throws ParseException
	 */
	public static Calendar stringToTime(String timeString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(TIME_FORMAT.parse(timeString));
		return cal;
	}
	
	/**
	 * Get a date string like "yyyy-MM-dd" fom Calendar
	 * @param cal
	 * @return
	 */
	public static String dateToString(Calendar cal) {
		return DATE_FORMAT.format(cal.getTime());
	}
	
	/**
	 * Get a Calendar from the dateString like "yyyy-MM-dd"
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Calendar stringToDate(String dateString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DATE_FORMAT.parse(dateString));
		return cal;
	}
	
	/**
	 * Get an ISO 8601 string like "yyyy-MM-dd HH:mm:ss" from Calendar.
	 * @param cal
	 * @return
	 */
	public static String datetimeToString(Calendar cal) {
		return ISO8601FORMAT.format(cal.getTime());
	}
	
	/**
	 * Get a Calendar from ISO 8601 string like "yyyy-MM-dd HH:mm:ss"
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Calendar stringToDatetime(String dateString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(ISO8601FORMAT.parse(dateString));
		return cal;
	}
	
	/** Disable an EditText-field. */
	public static void disableEntry(EditText entry) {
		entry.setKeyListener(null);
		entry.setFocusable(false);
		entry.setInputType(InputType.TYPE_NULL);
	}
}
