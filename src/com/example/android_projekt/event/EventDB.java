package com.example.android_projekt.event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.example.android_projekt.BrunstDBHelper;
import com.example.android_projekt.Utils;
import com.example.android_projekt.individ.IdNr;
import com.example.android_projekt.individ.IndividualDB;
import com.example.android_projekt.productionsite.ProductionSiteNr;

import android.animation.TypeEvaluator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
				"ON DELETE CASCADE " +
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
		Log.d(TAG, "creating table Event");
		database.execSQL(SQL_CREATE_TABLE);
	}
	
	/** Upgrade this table to a new version in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// TODO
		// nothing for now.
	}
	
	/**
	 * Save an event to DB.
	 * @param 	event	The Event to save
	 * @return	the id for the event in DB, or -1 if failed
	 */
	public long saveEvent(Event event) {
		
		// the values to store
		ContentValues values = new ContentValues();
		values.put(COLUMN_IDNR, event.getIdnr().toString());
		values.put(COLUMN_EVENTTYPE, EventType.getTypeAsString(event.getType()));
		values.put(COLUMN_EVENTTIME, Utils.datetimeToString(event.getEventTime()));
		values.put(COLUMN_REGTIME, Utils.datetimeToString(event.getRegTime()));
		
		long insertId = database.insert(TABLE_NAME, null, values);
		Log.d(TAG, "save Event, insertId: " + insertId);
		return insertId;
	}
	
	/**
	 * Delete an Event from the DB
	 * @param	eventId		The _id of the Event
	 * @return	The number of events deleted (should be 1)
	 */
	public int deleteEvent(long eventId) {
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {eventId + ""};
		return database.delete(TABLE_NAME, selection, selectionArgs);
	}
	
	/**
	 * Get a List of all events in the DB
	 * @return
	 */
	public List<Event> getAllEvents() {
		List<Event> events = new ArrayList<Event>();
		String sortOrder = COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, sortOrder);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			Event event = createFromCursor(cursor);
			events.add(event);
			cursor.moveToNext();
		}
		
		cursor.close();
		return events;
	}
	
	/**
	 * Get a List of all events at a certain ProductionSite identified by its Nr
	 * @return
	 */
	public List<Event> getAllEventsAtProductionSite(ProductionSiteNr siteNr) {
		List<Event> events = new ArrayList<Event>();
		
		/*
		 * SELECT * FROM Event
		 * WHERE Event.idnr IN
		 * 		(SELECT Individual.idnr FROM Individual 
		 *		 WHERE Indivudal.homesite = siteNr)
		 * 
		 */
		String selection = "? IN (SELECT ? FROM ? WHERE ? = ?)";
		String[] selectionArgs = {
				COLUMN_IDNR, 
				IndividualDB.COLUMN_IDNR,
				IndividualDB.TABLE_NAME,
				IndividualDB.COLUMN_HOMESITE,
				siteNr.toString()
		};
		String sortOrder = COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, sortOrder);
		cursor.moveToFirst();
		
		Log.d(TAG, "Nr of events at site: " + cursor.getCount());
		
		while(!cursor.isAfterLast()) {
			Event event = createFromCursor(cursor);
			events.add(event);
			cursor.moveToNext();
		}
		
		cursor.close();
		return events;
	}
	
	/**
	 * Get a List of all events concerning a specific Individual
	 * @return
	 */
	public List<Event> getAllEventsForIndividual(IdNr idNr) {
		List<Event> events = new ArrayList<Event>();
		
		/*
		 * SELECT * FROM Event
		 * WHERE idnr = idNr 
		 */
		String selection = "? = ?";
		String[] selectionArgs = { COLUMN_IDNR, idNr.toString() };
		String sortOrder = COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, sortOrder);
		cursor.moveToFirst();
		
		Log.d(TAG, "Nr of events at site: " + cursor.getCount());
		
		while(!cursor.isAfterLast()) {
			Event event = createFromCursor(cursor);
			events.add(event);
			cursor.moveToNext();
		}
		
		cursor.close();
		return events;
	}
	
	/**
	 * Fetch a single event from the DB
	 * @param 	eventId		The Event's _id in DB
	 * @return
	 */
	public Event getEvent(long eventId) {
		Event event = null;
		
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {eventId + ""};
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, null);
		if(cursor.getCount() == 1) {
			cursor.moveToFirst();
			event = createFromCursor(cursor);
		}
		cursor.close();
		return event;
	}

	/**
	 * Try to recreate an Event from the DB row pointed to by the cursor.
	 * @param cursor
	 * @return
	 */
	private Event createFromCursor(Cursor cursor) {
		Event event = null;
		
		try {
			String idnrStr = cursor.getString(1);
			IdNr idnr = new IdNr(idnrStr);
			
			String typeStr = cursor.getString(2);
			EventType.Type type = EventType.parseString(typeStr);
			
			event = new Event(type, idnr);
			
			event.set_id(cursor.getLong(0));
			event.setEventTime(Utils.stringToDatetime(cursor.getString(3)));
			event.setRegTime(Utils.stringToDatetime(cursor.getString(4)));
		}
		catch (ParseException ex) {
			Log.d(TAG, "unable to create the Event from cursor");
			event = null;
		}
		
		return event;
	}
}
