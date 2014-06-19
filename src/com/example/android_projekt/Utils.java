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

public class Utils 
{
	private static final String TAG = "Brunst: Utils";
	
	public static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	
//	public static String dateToString(Date date) {
//		return DATE_FORMAT.format(date);
//	}
//	
//	public static Date stringToDate(String dateString) throws ParseException {
//			return DATE_FORMAT.parse(dateString);
//	}
	
	public static String timeToString(Calendar cal) {
		return TIME_FORMAT.format(cal.getTime());
	}
	
	public static Calendar stringToTime(String timeString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(TIME_FORMAT.parse(timeString));
		return cal;
	}
	
	public static String dateToString(Calendar cal) {
		return DATE_FORMAT.format(cal.getTime());
	}
	
	public static Calendar stringToDate(String dateString) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DATE_FORMAT.parse(dateString));
		return cal;
	}
	
	public static String datetimeToString(Calendar cal) {
		return ISO8601FORMAT.format(cal.getTime());
	}
	
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
	
//	/**
//	 * Set the contents of the ImageButton to a MICRO 96x96 thumbnail
//	 * of the image found at Uri.
//	 * Mix of solutions from SO:
//	 * http://stackoverflow.com/questions/5548645/get-thumbnail-uri-path-of-the-image-stored-in-sd-card-android
//	 * 
//	 * @param	ibThumb		The ImageButton to set the contents of
//	 * @param 	uri			Uri to the Image
//	 */
//	public static void setThumbnail(Context context, ImageButton ibThumb, Uri uri) {
//		String[] projection = { MediaStore.Images.Media._ID };
//	    String result = null;
//	    Cursor cursor = context.managedQuery(uri, projection, null, null, null);
//	    
//	    Log.d(TAG, "cursor: " + cursor);
//	    
//	    int column_index = cursor
//	            .getColumnIndexOrThrow(MediaStore.Images.Media._ID);
//	    
//	    Log.d(TAG, "column: " + column_index);
//
//	    cursor.moveToFirst();
//	    long imageId = cursor.getLong(column_index);
////	    cursor.close();		// can't close or else it crashes on a second attempt at changing pic
//	    Log.d(TAG, "imageID: " + imageId);
//
//	    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(	
//	            context.getContentResolver(), imageId,
//	            MediaStore.Images.Thumbnails.MICRO_KIND,
//	            null);
//	    
//	    Log.d(TAG, "bitmap: " + bitmap);
//	}

}
