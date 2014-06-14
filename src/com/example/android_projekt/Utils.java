package com.example.android_projekt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils 
{
	public static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String dateToString(Date date) {
		return DATE_FORMAT.format(date);
	}
	
	public static Date stringToDate(String dateString) throws ParseException {
			return DATE_FORMAT.parse(dateString);
	}
	
	public static String calendarToString(Calendar cal) {
		return DATE_FORMAT.format(cal.getTime());
	}
	
	public static Calendar stringToCalendar(String dateString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DATE_FORMAT.parse(dateString));
		return cal;
	}

}
