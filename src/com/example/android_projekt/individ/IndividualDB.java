package com.example.android_projekt.individ;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.android_projekt.BrunstDBHelper;
import com.example.android_projekt.Utils;
import com.example.android_projekt.productionsite.ProductionSite;
import com.example.android_projekt.productionsite.ProductionSiteNr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", handling the database work for the Individual
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class IndividualDB implements BaseColumns
{
	private final static String TAG = "Brunst: IndividualDB";
	
	public static final String TABLE_NAME = "Individual";
	public static final String COLUMN_IDNR = "idnr";
	public static final String COLUMN_SHORTNR = "shortnr";
	public static final String COLUMN_BIRTHDATE = "birthdate";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_SEX = "sex";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_LASTBIRTH = "lastbirth";
	public static final String COLUMN_LACTATIONNR = "lactationnr"; 
	public static final String COLUMN_HEATCYCLUS = "heatcyclus"; 
	public static final String COLUMN_HOMESITE = "homesite"; 
	public static final String COLUMN_MOTHERIDNR = "motheridnr"; 
	public static final String COLUMN_FATHERIDNR = "fatheridnr"; 
	public static final String COLUMN_IMAGEURI = "imageuri"; 
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_IDNR,
		COLUMN_SHORTNR,
		COLUMN_BIRTHDATE,
		COLUMN_NAME,
		COLUMN_SEX,
		COLUMN_ACTIVE,
		COLUMN_LASTBIRTH,
		COLUMN_LACTATIONNR,
		COLUMN_HEATCYCLUS,
		COLUMN_HOMESITE,
		COLUMN_MOTHERIDNR,
		COLUMN_FATHERIDNR,
		COLUMN_IMAGEURI
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " (" +
			BaseColumns._ID 	+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_IDNR			+ " CHAR(16) UNIQUE	NOT NULL, " +
			COLUMN_SHORTNR		+ " SMALLINT, " +
			COLUMN_BIRTHDATE	+ " DATE, " +
			COLUMN_NAME			+ " VARCHAR(40), " +
			COLUMN_SEX			+ " CHAR(1) NOT NULL, " +
			COLUMN_ACTIVE		+ " BOOLEAN	NOT NULL, " +
			COLUMN_LASTBIRTH	+ " DATE, " +
			COLUMN_LACTATIONNR	+ " SMALLINT NOT NULL, " +
			COLUMN_HEATCYCLUS	+ " SMALLINT NOT NULL, " +
			COLUMN_HOMESITE		+ " CHAR(9) NOT NULL, " +
			COLUMN_MOTHERIDNR	+ " CHAR(16), " +
			COLUMN_FATHERIDNR	+ " CHAR(16), " +
			COLUMN_IMAGEURI		+ " VARCHAR(255), " +
			"FOREIGN KEY (homesite) REFERENCES ProductionSite(sitenr) ON DELETE CASCADE" +
		" ) ";
	
	private static final String SQL_DROP_TABLE = 
		"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	
	/** Constructor. */
	public IndividualDB(Context context) {
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
	
	/**
	 * Save a new Individual, or update an existing.
	 * @param 	individ		The Individual to save/update.
	 * @return	true on successful write, or else false
	 */
	public boolean saveIndividual(Individual individ) {
		boolean retval = false;
		
		// The values to store
		ContentValues values = new ContentValues();
		values.put(COLUMN_IDNR, individ.getIdNr().toString());
		values.put(COLUMN_SHORTNR, individ.getShortNr());
		if(individ.hasBirthdate())
			values.put(COLUMN_BIRTHDATE, Utils.calendarToString(individ.getBirthdate()));
		if(individ.hasName())
			values.put(COLUMN_NAME, individ.getName());
		values.put(COLUMN_SEX, individ.getSexAsString());
		values.put(COLUMN_ACTIVE, individ.isActive());
		if(individ.hasLastBirth())
			values.put(COLUMN_LASTBIRTH, Utils.calendarToString(individ.getLastBirth()));
		values.put(COLUMN_LACTATIONNR, individ.getLactationNr());
		values.put(COLUMN_HEATCYCLUS, individ.getHeatcyclus());
		values.put(COLUMN_HOMESITE, individ.getHomesiteNr().toString());
		if(individ.hasMotherIdNr())
			values.put(COLUMN_MOTHERIDNR, individ.getMotherIdNr().toString());
		if(individ.hasFatherIdNr())
			values.put(COLUMN_FATHERIDNR, individ.getFatherIdNr().toString());
		if(individ.hasImageUri())
			values.put(COLUMN_IMAGEURI, individ.getImageUri());
		
		// unsaved Individual
		if(individ.get_id() == Individual.UNSAVED_ID) {
			long insertId = database.insert(TABLE_NAME, null, values);
			Log.d(TAG, "save new individual, insertID: " + insertId);
			if(insertId != -1) {
				retval = true;
			}
		}
		// updating existing Individual
		else {
			String selection = BaseColumns._ID + " LIKE ?";
			String[] selectionArgs = {String.valueOf(individ.get_id())};
			int count = database.update(TABLE_NAME, values, selection, selectionArgs);
			Log.d(TAG, "update individual, count: " + count);
			if(count != 0) {
				retval = true;
			}
		}
		return retval;
	}
	
	// 
	/**
	 * Don't actually delete, just mark as not active, meaning the information
	 * for the Individual is available in the future if necessary.
	 * @param idNr
	 * @return
	 */
	public int deleteIndividual(IdNr idNr) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ACTIVE, "FALSE");
		
		String selection = COLUMN_IDNR + " LIKE ?";
		String[] selectionArgs = {idNr.toString()};
		return database.update(TABLE_NAME, values, selection, selectionArgs);
	}
	
//	/**
//	 * Delete an Individual.
//	 * @param	individ		The Individual to delete
//	 * @return	The number of Individuals deleted (should be 1)
//	 */
//	public int deleteIndividual(Individual individ) {
//		String selection = BaseColumns._ID + " LIKE ?";
//		String[] selectionArgs = {String.valueOf(individ.get_id())};
//		return database.delete(TABLE_NAME, selection, selectionArgs);
//	}
//	
//	/**
//	 * Delete an Individual.
//	 * @param	idNr		The IdNr of the Individual to delete
//	 * @return	The number of Individuals deleted (should be 1)
//	 */
//	public int deleteIndividual(IdNr idNr) {
//		String selection = COLUMN_IDNR + " LIKE ?";
//		String[] selectionArgs = {idNr.toString()};
//		return database.delete(TABLE_NAME, selection, selectionArgs);
//	}
	
	/**
	 * Get a single individual from the DB.
	 * @param		idNr	ID for the individual to fetch.
	 * @return		Individual or null
	 */
	public Individual getIndividual(IdNr idNr) {
		String selection = COLUMN_IDNR + " LIKE ? AND " + COLUMN_ACTIVE + " LIKE ?";
		String[] selectionArgs = {idNr.toString(), "1"};
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, null);
		Individual individ = null;
		if(cursor.getCount() == 1) {
			cursor.moveToFirst();
			individ = createFromCursor(cursor);
		}
		cursor.close();
		return individ;
	}
	
	/**
	 * Get a list of all Individs at a ProductionSite
	 * @param	site	The ProductionSite where the Individs belong
	 * @return	list of all Individs at ProductionSite
	 */
	public List<Individual> getAllIndividualsAtSite(ProductionSite site) {
		List<Individual> individs = new ArrayList<Individual>();
		
		String selection = COLUMN_HOMESITE + " LIKE ? AND " + COLUMN_ACTIVE + " LIKE ?";
		String[] selectionArgs = {site.getSiteNr().toString(), "1"};
		String sortorder = COLUMN_SHORTNR + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, selection, selectionArgs, null, null, sortorder);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			Individual individ = createFromCursor(cursor);
			individs.add(individ);
			cursor.moveToNext();
		}
		cursor.close();
		return individs;
	}
	
	/**
	 * Get a list of all individuals at a ProductionSite, as string
	 * to be used as Spinner titles, like "123 Rosa (SE-012345-0123-5)"
	 * @param siteNrStr
	 * @return
	 */
	public List<String> getAllIndividualsAtSiteAsSpinnerTitles(String siteNrStr) {
		List<String> individs = new ArrayList<String>();

		String[] titleCols = {COLUMN_SHORTNR, COLUMN_NAME, COLUMN_IDNR};
		String selection = COLUMN_HOMESITE + " LIKE ? AND " + COLUMN_ACTIVE + " LIKE ?";
		String[] selectionArgs = {siteNrStr, "1"};
		String sortorder = COLUMN_SHORTNR + " ASC";

		Cursor cursor = database.query(TABLE_NAME, titleCols, selection, selectionArgs, null, null, sortorder);
		cursor.moveToFirst();
		
		Log.d(TAG, "nr individs hit count: " + cursor.getCount());

		while(!cursor.isAfterLast()) {
			String individ = cursor.getInt(0) + " "; // short nr
			if(!cursor.isNull(1)) {	// name
				individ += cursor.getString(1) + " ";
			}
			individ += "(" + cursor.getString(2) + ")";
			
			Log.d(TAG, "consutrcted individual title: " + individ);
			
			individs.add(individ);
			cursor.moveToNext();
		}
		cursor.close();
		return individs;	
	}
	
	/**
	 * Construct an Individual from the row pointed to by the Cursor.
	 * @param	cursor		Pointer to a row in the Individual DB table.
	 * @return	An Individual
	 */
	private Individual createFromCursor(Cursor cursor) {
		String idNrStr = cursor.getString(1);
		String homeSiteStr = cursor.getString(10);
		Individual individ = null;
		try {
			Log.d(TAG, "cursor count: " + cursor.getCount() + ", columns: " + cursor.getColumnCount());
			for(int i = 0; i < cursor.getColumnCount(); ++i) {
				Log.d(TAG, "    " + i + ": " + cursor.getColumnName(i) + ": " + cursor.getString(i));
			}
			
			individ = new Individual(new IdNr(idNrStr), new ProductionSiteNr(homeSiteStr));
			individ.set_id(cursor.getLong(0));
			if(!cursor.isNull(2))	individ.setShortNr(cursor.getInt(2));
			if(!cursor.isNull(3))	individ.setBirthdate(Utils.stringToCalendar(cursor.getString(3)));
			if(!cursor.isNull(4))	individ.setName(cursor.getString(4));
			if(!cursor.isNull(5))	individ.setSex(cursor.getString(5));
			if(!cursor.isNull(6))	individ.setActive(cursor.getInt(6) == 1);
			if(!cursor.isNull(7))	individ.setLastBirth(Utils.stringToCalendar(cursor.getString(7)));
			if(!cursor.isNull(8))	individ.setLactationNr(cursor.getInt(8));
			if(!cursor.isNull(9))	individ.setHeatcyclus(cursor.getInt(9));
			if(!cursor.isNull(11))	individ.setMotherIdNr(new IdNr(cursor.getString(11)));
			if(!cursor.isNull(12))	individ.setFatherIdNr(new IdNr(cursor.getString(12)));
			if(!cursor.isNull(13))	individ.setImageUri(cursor.getString(13));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Unabled to create Individual from String");
			e.printStackTrace();
		}
		return individ;
	}

	
	
	
} 
