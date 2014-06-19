package com.example.android_projekt.event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.example.android_projekt.BrunstDBHelper;
import com.example.android_projekt.Utils;
import com.example.android_projekt.individ.IdNr;
import com.example.android_projekt.individ.IndividualDB;
import com.example.android_projekt.productionsite.ProductionSiteNr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", to handle DB work for the Note class
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class NoteDB implements BaseColumns
{
	private static final String TAG = "Brunst: NoteDB";
	
	public static final String TABLE_NAME = "Note";
	public static final String COLUMN_EVENT_ID = "eventid";
	public static final String COLUMN_TEXT = "text";
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_EVENT_ID,
		COLUMN_TEXT
	};
	
	private static final String SQL_CREATE_TABLE =
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_EVENT_ID		+ " INTEGER NOT NULL, " +
			COLUMN_TEXT			+ " VARCHAR(200), " +
			"FOREIGN KEY (" + COLUMN_EVENT_ID + ") REFERENCES " +
				EventDB.TABLE_NAME + "(" + EventDB._ID +") ON DELETE CASCADE " +
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
	public NoteDB(Context context) {
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
		// TODO
		// nothing for now.
	}
	
	/**
	 * Attempt to save a Note. Must first save the Event-part of the Note.
	 * @param		note
	 * @return		the note's _id in the DB, or -1 if failed
	 */
	public long saveNote(Note note) {
		long eventId = eventDb.saveEvent(note);
		Log.d(TAG, "Save Event part gave eventID: " + eventId);
		if(eventId == -1) {
			Log.d(TAG, "could not save Event part of Note");
			return eventId;
		}
		note.setEventId(eventId);
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_EVENT_ID, note.getEventId());
		if(note.hasText()) {
			values.put(COLUMN_TEXT, note.getText());
		}
		
		long insertId = database.insert(TABLE_NAME, null, values);
		Log.d(TAG, "save Note, insertId: " + insertId);
		
		return insertId;
	}
	
	/**
	 * Delete an Note from the DB, and also the Event part of the note
	 * @param	noteId		The _id of the Note
	 * @return	The number of Notes deleted (should be 1)
	 */
	public int deleteNote(long noteId) {
		Note note = getNote(noteId);
		
		long eventId = note.getEventId();
		
		// this should cascade the deletion
		return eventDb.deleteEvent(eventId);
		
		
//		
//		String selection = BaseColumns._ID + " LIKE ?";
//		String[] selectionArgs = {noteId + ""};
//		return database.delete(TABLE_NAME, selection, selectionArgs);
	}
	
	/**
	 * Get a List of all Notes in the DB
	 * @return
	 */
	public List<Note> getAllNotes() {
		List<Note> notes = new ArrayList<Note>();
//		String sortOrder = COLUMN_EVENTTIME + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			Note note = createFromCursor(cursor);
			notes.add(note);
			cursor.moveToNext();
		}
		
		cursor.close();
		return notes;
	}
	
	/**
	 * Get a List of all Notes for a certain Individual, identified by its IdNr
	 * @return
	 */
	public List<Note> getAllNotesForIndividual(IdNr idNr) {
		List<Note> notes = new ArrayList<Note>();
		
		/*
		 * SELECT * FROM Note
		 * WHERE Note.eventid IN
		 * 		(SELECT Event._id FROM Event 
		 *		 WHERE Event.idnr = idNr
		 *		 ORDER BY Event.eventtime)
		 * 
		 */
		String rawQuery = 
				"SELECT Note.* FROM Note, Event"
				+ " WHERE Note.eventid = Event._id"
				+ " AND Event.idnr = '"
				+ idNr.toString()
				+ "' ORDER BY Event.eventtime";
//		String selection = "? IN (SELECT ? FROM ? WHERE ? = ? ORDER BY ?)";
//		String[] selectionArgs = {
//				COLUMN_EVENT_ID, 
//				EventDB.TABLE_NAME + "." + EventDB._ID,
//				EventDB.TABLE_NAME,
//				EventDB.TABLE_NAME + "." + EventDB.COLUMN_IDNR,
//				idNr.toString(),
//				EventDB.TABLE_NAME + "." + EventDB.COLUMN_EVENTTIME
//		};
//		
//		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, null);
		Cursor cursor = database.rawQuery(rawQuery, null);
		cursor.moveToFirst();
		
		Log.d(TAG, "Nr of Notes for individual: " + cursor.getCount());
		
		while(!cursor.isAfterLast()) {
			Note note = createFromCursor(cursor);
			notes.add(note);
			cursor.moveToNext();
		}
		
		cursor.close();
		return notes;
	}
	
	/**
	 * Fetch a single Note from the DB
	 * @param 	noteId		The Note's _id in DB
	 * @return
	 */
	public Note getNote(long noteId) {
		Note note = null;
		
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {noteId + ""};
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, null);
		if(cursor.getCount() == 1) {
			cursor.moveToFirst();
			note = createFromCursor(cursor);
		}
		cursor.close();
		return note;
	}

	/**
	 * Create a Note from the row in DB pointed to by the Cursor
	 * @param cursor
	 * @return
	 */
	private Note createFromCursor(Cursor cursor) {
		Note note = null;
		
		long eventId = cursor.getLong(1);
		// recreate the Event part of the Note
		Event eventPart = eventDb.getEvent(eventId);
		
		if(eventPart == null) {
			Log.d(TAG, "could not recreate the Event part of the Note");
			return null;
		}
		note = new Note(eventPart);
		note.setNoteId(cursor.getLong(0));
		if(!cursor.isNull(2)) {
			note.setText(cursor.getString(2));
		}
		
		return note;
	}
}
