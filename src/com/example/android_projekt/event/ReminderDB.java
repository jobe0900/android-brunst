package com.example.android_projekt.event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.example.android_projekt.BrunstDBHelper;
import com.example.android_projekt.Utils;
import com.example.android_projekt.individ.IdNr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", to handle the DB work for the Reminder class.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class ReminderDB 
	implements BaseColumns
{
	private static final String TAG = "Brunst: ReminderDB";

	public static final String TABLE_NAME = "Reminder";
	public static final String COLUMN_REMTYPE = "remtype";
	public static final String COLUMN_EVENTID = "eventid";
	public static final String COLUMN_EVENTTYPE = "eventtype";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_EVENTTIME = "eventtime";
	public static final String COLUMN_REMTIME = "remtime";
	public static final String COLUMN_REMINT_H = "remint_h";
	public static final String COLUMN_REMINT_D = "remint_d";
	public static final String COLUMN_ACTIVE = "active";
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_REMTYPE,
		COLUMN_EVENTID,
		COLUMN_EVENTTYPE,
		COLUMN_DESCRIPTION,
		COLUMN_EVENTTIME,
		COLUMN_REMTIME,
		COLUMN_REMINT_H,
		COLUMN_REMINT_D, 
		COLUMN_ACTIVE
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_REMTYPE		+ " INTEGER NOT NULL, " +
			COLUMN_EVENTID		+ " INTEGER NOT NULL, " +
			COLUMN_EVENTTYPE	+ " INTEGER NOT NULL, " +
			COLUMN_DESCRIPTION	+ " VARCHAR(100) NOT NULL, " +
			COLUMN_EVENTTIME	+ " DATETIME NOT NULL, " +
			COLUMN_REMTIME		+ " DATETIME NOT NULL, " +
			COLUMN_REMINT_H		+ " INTEGER, " +
			COLUMN_REMINT_D		+ " INTEGER, " +
			COLUMN_ACTIVE		+ " BOOLEAN NOT NULL, " +
			"FOREIGN KEY (" + COLUMN_REMTYPE + ") REFERENCES " +
				ReminderTypeDB.TABLE_NAME + "(" + ReminderTypeDB._ID + ") ON DELETE CASCADE, " +
			"FOREIGN KEY (" + COLUMN_EVENTID + ") REFERENCES " +
				EventDB.TABLE_NAME + "(" + EventDB._ID + ") ON DELETE CASCADE, " +
			"FOREIGN KEY (" + COLUMN_EVENTTYPE + ") REFERENCES " +
				EventTypeDB.TABLE_NAME + "(" + EventTypeDB._ID + ") ON DELETE CASCADE " +
		" )";
	
	private static final String SQL_DROP_TABLE = 
			"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	private EventDB eventDB;
	private EventTypeDB eventTypeDB;
	
	/**
	 * Constructor.
	 * @param context
	 */
	public ReminderDB(Context context) {
		dbHelper = new BrunstDBHelper(context);
		eventDB = new EventDB(context);
		eventTypeDB = new EventTypeDB(context);
	}
	
	/** Try to open the database to do some work. */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		eventDB.open();
		eventTypeDB.open();
	}
	
	/** Close the database again. */
	public void close() {
		dbHelper.close();
		eventDB.close();
		eventTypeDB.open();
	}
	
	/** Create this table in the DB. */
	public static void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "creating table Reminder");
		database.execSQL(SQL_CREATE_TABLE);
	}
	
	/** Upgrade this table to a new version in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// nothing for now.
	}
	
	/**
	 * Attempt to save a Reminder to the DB
	 * @param	reminder	The Reminder to save
	 * @return	The Reminder's _id in the DB or -1 if failed
	 */
	public long saveReminder(Reminder reminder) {
		ContentValues values = new ContentValues();
		
		// Reminder type
		values.put(COLUMN_REMTYPE, reminder.getType().ordinal() + 1); // enum 0-based, DB 1-based
		// Event Id
		values.put(COLUMN_EVENTID, reminder.getEvent().getEventId());
		// EventType
		values.put(COLUMN_EVENTTYPE, reminder.getEventType().ordinal() + 1); // enum 0-based, DB 1-based
		// Description
		values.put(COLUMN_DESCRIPTION, reminder.getDescription());
		// Event time
		values.put(COLUMN_EVENTTIME, Utils.datetimeToString(reminder.getEventTime()));
		// Reminder time
		values.put(COLUMN_REMTIME, Utils.datetimeToString(reminder.getReminderTime()));
		// Reminder interval Hours
		values.put(COLUMN_REMINT_H, reminder.getReminderIntervalHour());
		// Reminder interval day
		values.put(COLUMN_REMINT_D, reminder.getReminderIntervalDay());
		// Active
		values.put(COLUMN_ACTIVE, reminder.isActive());
		
		long insertId = database.insert(TABLE_NAME, null, values);
		Log.d(TAG, "save Reminder, insertId: " + insertId);
		
		return insertId;
	}
	
	/**
	 * Mark Reminder as inactive
	 * @param reminderId
	 * @return
	 */
	public int inactivateReminder(long reminderId) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ACTIVE, false);
		
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {reminderId + ""};
		int rows = database.update(TABLE_NAME, values, selection, selectionArgs);
		Log.d(TAG, "forgot nr rows: " + rows);
		return rows;
	}
	
	/**
	 * Delete a reminder
	 * @param	reminderId		The _id of the reminder
	 * @return	The number of reminders deleted (should be 1)
	 */
	public int deleteReminder(long reminderId) {
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {reminderId + "" };
		return database.delete(TABLE_NAME, selection, selectionArgs);
	}
	
	/**
	 * Get a list of all Reminders in the database.
	 * @return
	 */
	public List<Reminder> getAllReminders() {
		List<Reminder> reminders = new ArrayList<Reminder>();
		String selection = COLUMN_ACTIVE + " = '1'";
		String orderBy = COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, null, null, null, orderBy);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			Reminder reminder = createFromCursor(cursor);
			reminders.add(reminder);
			cursor.moveToNext();
		}
		cursor.close();
		return reminders;
	}
	
	/**
	 * Get a list of all current Reminders in the database, (they have reached the reminder time)
	 * @return
	 */
	public List<Reminder> getAllCurrentReminders() {
		List<Reminder> reminders = new ArrayList<Reminder>();
		String selection = COLUMN_ACTIVE + " = '1' AND " + COLUMN_REMTIME + " > 'now'";
		String orderBy = COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, null, null, null, orderBy);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			Reminder reminder = createFromCursor(cursor);
			reminders.add(reminder);
			cursor.moveToNext();
		}
		cursor.close();
		return reminders;
	}
	
	/**
	 * Get all active Reminder for an Individual
	 * @param idNr
	 * @return
	 */
	public List<Reminder> getAllRemindersForIndividual(IdNr idNr) {
		List<Reminder> reminders = new ArrayList<Reminder>();
		
		/*
		 * SELECT Reminder.* FROM Reminder, Event
		 * WHERE Reminder.eventid = Event._id
		 * AND Reminder.active = "1"
		 * AND Event.idnr = "idNr"
		 * ORDER BY Reminder.eventtime ASC
		 */
		String rawQuery = 
				"SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME + ", " + EventDB.TABLE_NAME +
				" WHERE " + TABLE_NAME + "." + COLUMN_EVENTID + " = " + EventDB.TABLE_NAME + "." + EventDB._ID +
				" AND " + TABLE_NAME + "." + COLUMN_ACTIVE + " = '1' " +
				" AND " + EventDB.TABLE_NAME + "." + EventDB.COLUMN_IDNR + " = '" + idNr.toString() + "'" +
				" ORDER BY " + TABLE_NAME + "." + COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.rawQuery(rawQuery, null);
		cursor.moveToFirst();
		Log.d(TAG, "Nr of Reminders for individual: " + cursor.getCount());
		
		while(!cursor.isAfterLast()) {
			Reminder reminder = createFromCursor(cursor);
			reminders.add(reminder);
			cursor.moveToNext();
		}
		cursor.close();
		return reminders;
	}
	
	/**
	 * Get all active Reminders that has reached the reminder dateTime (for an Individual).
	 * @param idNr
	 * @return
	 */
	public List<Reminder> getAllCurrentRemindersForIndividual(IdNr idNr) {
		List<Reminder> reminders = new ArrayList<Reminder>();
		
		/*
		 * SELECT Reminder.* FROM Reminder, Event
		 * WHERE Reminder.eventid = Event._id
		 * AND Reminder.active = "1"
		 * AND Reminder.remtime > "now"		-- don't display to early ?
		 * AND Event.idnr = "idNr"
		 * ORDER BY Reminder.eventtime ASC
		 */
		String rawQuery = 
				"SELECT " + TABLE_NAME + ".* FROM " + TABLE_NAME + ", " + EventDB.TABLE_NAME +
				" WHERE " + TABLE_NAME + "." + COLUMN_EVENTID + " = " + EventDB.TABLE_NAME + "." + EventDB._ID +
				" AND " + TABLE_NAME + "." + COLUMN_ACTIVE + " = '1' " +
				" AND " + TABLE_NAME + "." + COLUMN_REMTIME + " > 'now' " +
				" AND " + EventDB.TABLE_NAME + "." + EventDB.COLUMN_IDNR + " = '" + idNr.toString() + "'" +
				" ORDER BY " + TABLE_NAME + "." + COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.rawQuery(rawQuery, null);
		cursor.moveToFirst();
		Log.d(TAG, "Nr of Reminders for individual: " + cursor.getCount());
		
		while(!cursor.isAfterLast()) {
			Reminder reminder = createFromCursor(cursor);
			reminders.add(reminder);
			cursor.moveToNext();
		}
		cursor.close();
		return reminders;
	}
	
	/**
	 * Fetch a specific Reminder from the DB
	 * @param reminderId
	 * @return
	 */
	public Reminder getReminder(long reminderId) {
		Reminder reminder = null;
		
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {reminderId + ""};
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, null);
		if(cursor.getCount() == 1) {
			cursor.moveToFirst();
			reminder = createFromCursor(cursor);
		}
		cursor.close();
		return reminder;
	}

	/**
	 * Create a Reminder from the data in the row in DB pointed to by the cursor.
	 * @param cursor
	 * @return
	 */
	private Reminder createFromCursor(Cursor cursor) {
		Reminder reminder = null;
		
		// first find out the event and event type
		long eventId = cursor.getLong(2);
		Event event = eventDB.getEvent(eventId);
		int eventTypeId = cursor.getInt(3);
		Event.Type eventType = Event.Type.values()[eventTypeId - 1];
		
		// create the Reminder
		reminder = new Reminder(event, eventType);
		
		// fill in rest of values
		reminder.setReminderId(cursor.getLong(0));
		reminder.setType(Reminder.Type.values()[cursor.getInt(1) - 1]);
		reminder.setDescription(cursor.getString(4));
		try {
			reminder.setEventTime(Utils.stringToDatetime(cursor.getString(5)));
			reminder.setReminderTime(Utils.stringToDatetime(cursor.getString(6)));
		} catch (ParseException e) {
			Log.d(TAG, "date parse error");
			return null;
		}
		reminder.setReminderIntervalHour(cursor.getInt(7));
		reminder.setReminderIntervalDay(cursor.getInt(8));
		reminder.setActive(cursor.getInt(9) == 1);
		
		return reminder;
	}
}
