package com.example.android_projekt.event;

import com.example.android_projekt.BrunstDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", handling the database work for the EventType
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class EventTypeDB implements BaseColumns
{
	private static final String TAG = "Brunst: EventTypeDB";
	
	public static final String TABLE_NAME = "EventType";
	public static final String COLUMN_NAME = "name";

	private static final String[] NAMES = {
		EventType.EVENT_NOTE,
		EventType.EVENT_HEAT,
		EventType.EVENT_MATING,
		EventType.EVENT_PREGCHECK,
		EventType.EVENT_BIRTH
	};
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_NAME
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID		+ "INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_NAME			+ " VARCHAR(30) NOT NULL" +
		" )";
	
	private static final String SQL_POPULATE_TABLE = 
		"INSERT INTO " + TABLE_NAME + " " + NAMES[0] + " " +
		"INSERT INTO " + TABLE_NAME + " " + NAMES[1] + " " +
		"INSERT INTO " + TABLE_NAME + " " + NAMES[2] + " " +
		"INSERT INTO " + TABLE_NAME + " " + NAMES[3] + " " +
		"INSERT INTO " + TABLE_NAME + " " + NAMES[4];
		
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	
	/**
	 * Constructor.
	 * @param context
	 */
	public EventTypeDB(Context context) {
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
		Log.d(TAG, "creating table Individual");
		database.execSQL(SQL_CREATE_TABLE);
		database.execSQL(SQL_POPULATE_TABLE);
	}
	
	/** Upgrade this table to a new version in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// TODO
		// nothing for now.
	}
	
}
