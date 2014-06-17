package com.example.android_projekt.event;

import com.example.android_projekt.BrunstDBHelper;
import com.example.android_projekt.individ.IndividualDB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", to handle database work for the Event class
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class EventDB implements BaseColumns
{
	private static final String TAG = "Brunst: EventDB";
	
	public static final String TABLE_NAME = "Event";
	public static final String COLUMN_IDNR = "idnr";
	public static final String COLUMN_EVENTTYPE = "eventtype";
	public static final String COLUMN_EVENTTIME = "eventtime";
	public static final String COLUMN_REGTIME = "regtime";
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_IDNR,
		COLUMN_EVENTTYPE,
		COLUMN_EVENTTIME,
		COLUMN_REGTIME
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_IDNR			+ " CHAR(16) NOT NULL, " +
			COLUMN_EVENTTYPE	+ " INTEGER NOT NULL, " +
			COLUMN_EVENTTIME	+ " DATETIME NOT NULL, " +
			COLUMN_REGTIME		+ " DATETIME NOT NULL, " +
			"FOREIGN KEY (" + COLUMN_IDNR + ") REFERENCES " + 
				IndividualDB.TABLE_NAME + "(" + IndividualDB.COLUMN_IDNR + ") " +
				"ON DELETE CASCADE, " +
			"FOREIGN KEY (" + COLUMN_EVENTTYPE + ") REFERENCES " +
				EventTypeDB.TABLE_NAME + "(" + EventTypeDB._ID + ") " +
				"ON DELETE CASCADE" +
		" )";
	
	private static final String SQL_DROP_TABLE = 
			"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	
	/**
	 * Constructor.
	 * @param context
	 */
	public EventDB(Context context) {
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
	}
	
	/** Upgrade this table to a new version in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// TODO
		// nothing for now.
	}
}
