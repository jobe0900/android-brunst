package com.example.android_projekt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils 
{
	private static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String dateToString(Date date) {
		return iso8601Format.format(date);
	}
	
	public static Date stringToDate(String dateString) throws ParseException {
			return iso8601Format.parse(dateString);
	}

}
