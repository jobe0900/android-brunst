package com.example.android_projekt.event;

import java.util.ArrayList;
import java.util.List;

import com.example.android_projekt.BrunstDBHelper;
import com.example.android_projekt.event.HeatEvent.Sign;
import com.example.android_projekt.event.HeatEvent.Strength;
import com.example.android_projekt.individ.IdNr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", to handle work for the HeatEvent class.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class HeatEventDB 
	implements BaseColumns
{
	private static final String TAG = "Brunst: HeatEventDB";
	
	public static final String TABLE_NAME = "HeatEvent";
	public static final String COLUMN_EVENT_ID = "enventid";
	public static final String COLUMN_HEATROUND = "heatround";
	public static final String COLUMN_HEATSIGN = "heatsign";
	public static final String COLUMN_HEATSTREN = "heatstren";
	public static final String COLUMN_NOTE = "note";
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_EVENT_ID,
		COLUMN_HEATROUND,
		COLUMN_HEATSIGN,
		COLUMN_HEATSTREN,
		COLUMN_NOTE
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_EVENT_ID		+ " INTEGER NOT NULL, " +
			COLUMN_HEATROUND	+ " INTEGER, " +
			COLUMN_HEATSIGN		+ " INTEGER NOT NULL, " +
			COLUMN_HEATSTREN	+ " INTEGER NOT NULL, " +
			COLUMN_NOTE			+ " VARCHAR(200), " +
			"FOREIGN KEY (" + COLUMN_EVENT_ID + ") REFERENCES " +
				EventDB.TABLE_NAME + "(" + EventDB._ID + ") ON DELETE CASCADE, " +
			"FOREIGN KEY (" + COLUMN_HEATSIGN + ") REFERENCES " +
				HeatSignDB.TABLE_NAME + "(" + HeatSignDB._ID + ") ON DELETE CASCADE, " +
			"FOREIGN KEY (" + COLUMN_HEATSTREN + ") REFERENCES " +
				HeatStrengthDB.TABLE_NAME + "(" + HeatStrengthDB._ID + ") ON DELETE CASCADE " +
		" ) ";
			
	private static final String SQL_DROP_TABLE = 
			"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	private EventDB eventDb;
	
	/**
	 * Constructor.
	 * @param context
	 */
	public HeatEventDB(Context context) {
		dbHelper = new BrunstDBHelper(context);
		eventDb = new EventDB(context);
	}
	
	/** Try to open the database to do some work. */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		eventDb.open();
	}
	
	/** Close the database again. */
	public void close() {
		dbHelper.close();
		eventDb.close();
	}
	
	/** Create this table in the DB. */
	public static void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "creating table Note");
		database.execSQL(SQL_CREATE_TABLE);
	}
	
	/** Upgrade this table to a new version in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// nothing for now.
	}
	
	/**
	 * Attempt to save a HeatEvent. Must first save the Event-part of the Heat.
	 * @param		note
	 * @return		the HeatEvent's _id in the DB, or -1 if failed
	 */
	public long saveHeatEvent(HeatEvent heat) {
		long eventId = eventDb.saveEvent(heat);
		Log.d(TAG, "Save Event part gave eventID: " + eventId);
		if(eventId == -1) {
			Log.d(TAG, "could not save Event part of Note");
			return eventId;
		}
		heat.setEventId(eventId);
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_EVENT_ID, heat.getEventId());
		values.put(COLUMN_HEATROUND, heat.getHeatRound());
		if(!heat.hasSign()) {
			// abort if not assigned a Sign
			eventDb.deleteEvent(eventId);
			return -1;
		}
		values.put(COLUMN_HEATSIGN, heat.getSign().ordinal() + 1); // enum 0-based, db 1-base
		if(!heat.hasStrength()) {
			// abort if note assigned a Strength
			eventDb.deleteEvent(eventId);
			return -1;
		}
		values.put(COLUMN_HEATSTREN, heat.getStrength().ordinal() + 1); // enum 0-based, db 1-base
		if(heat.hasNote()) {
			values.put(COLUMN_NOTE, heat.getNote());
		}
		
		long insertId = database.insert(TABLE_NAME, null, values);
		Log.d(TAG, "save Heat, insertId: " + insertId);
		
		return insertId;
	}
	
	/**
	 * Delete the HeatEvent and correspoding Event.
	 * @param	heatId		Id of the HeatEvents
	 * @return	the number of HeatEvents deleted
	 */
	public int deleteHeatEvent(long heatId) {
		HeatEvent heat = getHeatEvent(heatId);
		// cascading deletion of event
		return eventDb.deleteEvent(heat.getEventId());
	}
	
	/**
	 * Get a list of all HeatEvents in the DB
	 * @return
	 */
	public List<HeatEvent> getAllHeatEvents() {
		List<HeatEvent> heats = new ArrayList<HeatEvent>();
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			HeatEvent heat = createFromCursor(cursor);
			heats.add(heat);
			cursor.moveToNext();
		}
		cursor.close();
		return heats;
	}

	/**
	 * Get a list of all HeatEvents for an Individual, identified by the IdNr
	 * @param idNr
	 * @return
	 */
	public List<HeatEvent> getAllHeatsForIndividual(IdNr idNr) {
		List<HeatEvent> heats = new ArrayList<HeatEvent>();
		/*
		 * SELECT * FROM HeatEvent, Event
		 * WHERE HeatEvent.eventid = Event._id
		 * AND Event.idnr = idNr
		 * ORDER BY Event.eventtime DESC
		 */
		String rawQuery = 
				"SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME + ", " + EventDB.TABLE_NAME +
				" WHERE " + TABLE_NAME + "." + COLUMN_EVENT_ID + " = " + EventDB.TABLE_NAME + "." + EventDB._ID +
				" AND " + EventDB.TABLE_NAME + "." + EventDB.COLUMN_IDNR + " = '" + idNr.toString() + "'" +
				" ORDER BY " + EventDB.TABLE_NAME + "." + EventDB.COLUMN_EVENTTIME + " DESC";
		
		Cursor cursor = database.rawQuery(rawQuery, null);
		cursor.moveToFirst();
		
		Log.d(TAG, "Nr of Heats for individual: " + cursor.getCount());
		
		while(!cursor.isAfterLast()) {
			HeatEvent heat = createFromCursor(cursor);
			heats.add(heat);
			cursor.moveToNext();
		}
		cursor.close();
		return heats;
	}
	
	/**
	 * Fetch a specific HeatEvent from the DB
	 * @param heatId
	 * @return
	 */
	public HeatEvent getHeatEvent(long heatId) {
		HeatEvent heat = null;
		
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {heatId + ""};
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, null);
		if(cursor.getCount() == 1) {
			cursor.moveToFirst();
			heat = createFromCursor(cursor);
		}
		cursor.close();
		return heat;
	}
	
	/**
	 * Find the HeatRound for the latest heat event
	 * @return
	 */
	public int getRoundNr(IdNr idNr) {
		int round = 1;
		// Just get the last inserted row
		/*
		 * SELECT HeatEvent.rowid, HeatEvent.heatround FROM HeatEvent, Event
		 * WHERE HeatEvent.eventid = Event._id
		 * AND Event.idnr = 'idNr'
		 * ORDER BY HeatEvent.rowid DESC limit 1
		 */
		String rawQuery = 
				"SELECT " + TABLE_NAME + ".rowid," + TABLE_NAME + "." + COLUMN_HEATROUND + " FROM " + TABLE_NAME + ", " + EventDB.TABLE_NAME +
				" WHERE " + TABLE_NAME + "." + COLUMN_EVENT_ID + " = " + EventDB.TABLE_NAME + "." + EventDB._ID +
				" AND " + EventDB.TABLE_NAME + "." + EventDB.COLUMN_IDNR + " = '" + idNr.toString() + "'" +
				" ORDER BY " + TABLE_NAME + ".rowid DESC limit 1";
		
		Cursor cursor = database.rawQuery(rawQuery, null);
		if(cursor != null && cursor.moveToFirst()) {
			round = cursor.getInt(1);
		}
		Log.d(TAG, "heat ronund nr " + round);
		return round;
	}
	
	/**
	 * Create a HeatEvent from the row in DB pointed to by cursor
	 * @param cursor
	 * @return
	 */
	private HeatEvent createFromCursor(Cursor cursor) {
		HeatEvent heat = null;
		
		long eventId = cursor.getLong(1);
		Event event = eventDb.getEvent(eventId);
		
		if(event == null) {
			Log.d(TAG, "could not recreate the event part of the HeatEvetn");
			return null;
		}
		heat = new HeatEvent(event);
		heat.setHeatId(cursor.getLong(0));
		heat.setHeatRound(cursor.getInt(2));
		if(!cursor.isNull(3)) {
			heat.setSign(Sign.values()[cursor.getInt(3) - 1]);
		}
		if(!cursor.isNull(4)) {
			heat.setStrength(Strength.values()[cursor.getInt(4) - 1]);
		}
		if(!cursor.isNull(5)) {
			heat.setNote(cursor.getString(5));
		}
		
		return heat;
	}
}
