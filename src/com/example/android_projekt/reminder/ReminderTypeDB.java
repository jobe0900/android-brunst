package com.example.android_projekt.reminder;

import com.example.android_projekt.BrunstDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", for the Reminder.Type type.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class ReminderTypeDB implements BaseColumns
{
	private static final String TAG = "Brunst: ReminderTypeDB";
	
	public static final String TABLE_NAME = "ReminderType";
	public static final String COLUMN_NAME = "name";
	
	private static final String[] NAMES = {
		Reminder.Type.REMINDER_NORMAL.toString(),
		Reminder.Type.REMINDER_ALERT.toString(),
		Reminder.Type.REMINDER_SERIOUS.toString()
	};
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_NAME
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_NAME			+ " VARCHAR(20) NOT NULL " +
		" )";
	
	private static final String SQL_DROP_TABLE = 
			"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	
	/**
	 * Constructor.
	 * @param context
	 */
	public ReminderTypeDB(Context context) {
		dbHelper = new BrunstDBHelper(context);
	}
	
	/** Try to open the database to do some work. */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	/** Close the database again. */
	public void close() {
		dbHelper.close();
	}
	
	/** Create this table in the DB. */
	public static void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "creating table ReminderType");
		database.execSQL(SQL_CREATE_TABLE);
		for(String name : NAMES) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME, name);
			database.insert(TABLE_NAME, null, values);
		}
	}
	
	/** Upgrade this table to a new version in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// nothing for now.
	}
}
