package com.example.android_projekt.event;

import com.example.android_projekt.BrunstDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

public class HeatStrengthDB implements BaseColumns
{
	private static final String TAG = "Brunst: HeatStrengthDB";
	
	public static final String TABLE_NAME = "HeatStrength";
	public static final String COLUMN_NAME = "name";
	
	private static final String[] NAMES = {
		HeatEvent.Strength.HEATSTREN_1.toString(),
		HeatEvent.Strength.HEATSTREN_2.toString(),
		HeatEvent.Strength.HEATSTREN_3.toString(),
		HeatEvent.Strength.HEATSTREN_4.toString(),
		HeatEvent.Strength.HEATSTREN_5.toString(),
	};
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_NAME
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID		+ " INTEGER PRIMATY KEY AUTOINCREMENT, " +
			COLUMN_NAME			+ " VARCHAR(20) NOT NULL" +
		" ) ";
	
	private static final String SQL_DROP_TABLE = 
			"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	
	/**
	 * Constructor.
	 * @param context
	 */
	public HeatStrengthDB(Context context) {
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
		Log.d(TAG, "creating table HeatStrength");
		database.execSQL(SQL_CREATE_TABLE);
		// populate the table
		for(String name : NAMES) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME, name);
			database.insert(TABLE_NAME, null, values);
		}
	}
	
	/** Upgrade this table to a new version in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// TODO
		// nothing for now.
	}
}
