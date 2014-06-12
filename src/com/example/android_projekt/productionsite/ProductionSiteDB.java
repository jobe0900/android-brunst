package com.example.android_projekt.productionsite;

import java.util.ArrayList;
import java.util.List;

import com.example.android_projekt.BrunstDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Helper class, "contract", handling the database work for the ProductionSite.
 * @author	Jonas Bergman, <jobe0900@student.miun.se>
 */
public class ProductionSiteDB implements BaseColumns 
{
	public final static String TAG = "Brunst: ProductionSiteDB";
	public static final String TABLE_NAME = "ProductionSite";
	// _ID comes from BaseColumns?
	public static final String COLUMN_SITENR = "sitenr";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_POSTNR = "postnr";
	public static final String COLUMN_POSTADDRESS = "postaddress";
	public static final String COLUMN_COORDINATES = "coordinates";
	public static final String COLUMN_IMAGEURI = "imageuri";
	
	private static final String[] ALL_COLUMNS = {
		BaseColumns._ID,
		COLUMN_SITENR,
		COLUMN_NAME,
		COLUMN_ADDRESS,
		COLUMN_POSTNR,
		COLUMN_POSTADDRESS,
		COLUMN_COORDINATES,
		COLUMN_IMAGEURI
	};
	
	private static final String SQL_CREATE_TABLE = 
		"CREATE TABLE " + TABLE_NAME + " ( " +
			BaseColumns._ID 	+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_SITENR		+ " CHAR(9) UNIQUE NOT NULL, " +
			COLUMN_NAME 		+ " VARCHAR(30), " +
			COLUMN_ADDRESS 		+ " VARCHAR(25), " +
			COLUMN_POSTNR		+ " CHAR(5), " +
			COLUMN_POSTADDRESS	+ " VARCHAR(20), " +
			COLUMN_COORDINATES	+ " VARCHAR(40), " +
			COLUMN_IMAGEURI		+ " VARCHAR(255) " +
		" ) ";

	private static final String SQL_DROP_TABLE = 
		"DROP TABLE IF EXISTS " + TABLE_NAME;
	
	
	
	private SQLiteDatabase database;
	private BrunstDBHelper dbHelper;
	
	/** Constructor. */
	public ProductionSiteDB(Context context) {
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
		database.execSQL(SQL_CREATE_TABLE);
	}
	
	/** Upgrade this table to a new verison in the DB. */
	public static void onUpgrade(SQLiteDatabase database) {
		// TODO
		// nothing for now.
	}
	
	/**
	 * Save a new ProductionSite, with the fields filled in. 
	 * If it already exists then an update is performed.
	 * @param	site	The ProductionSite to save
	 * @return 	true if succesful write, false else
	 */
	public boolean saveProductionSite(ProductionSite site) {
		boolean retval = false;
		// The values to store in the database
		ContentValues values = new ContentValues();
//		values.put(BaseColumns._ID, "NULL");
		values.put(COLUMN_SITENR, site.getSiteNr().toString());
		if(site.hasName()) values.put(COLUMN_NAME, site.getName());
		if(site.hasAddress()) values.put(COLUMN_ADDRESS, site.getAddress());
		if(site.hasPostnr()) values.put(COLUMN_POSTNR, site.getPostnr());
		if(site.hasPostaddress()) values.put(COLUMN_POSTADDRESS, site.getPostaddress());
		if(site.hasCoordinates()) values.put(COLUMN_COORDINATES, site.getCoordinates());
		if(site.hasImageUriStr()) values.put(COLUMN_IMAGEURI, site.getImageUriStr());
		
		Log.d(TAG, "values to save to DB: " + values);
		Log.d(TAG, "site has ID: " + site.get_id());
		
		// Production site has not been saved before
		if(site.get_id() == ProductionSite.UNSAVED_ID) {
			long insertId = database.insert(TABLE_NAME, null, values);
			Log.d(TAG, "insertionID in DB: " + insertId);
			if(insertId != -1) {
				// saved ok
				retval = true;
			}
//			retval = true;
		}
		// Updating an existing site
		else {
			String selection = BaseColumns._ID + " LIKE ?";
			String[] selectionArgs = {String.valueOf(site.get_id())};
			int count = database.update(TABLE_NAME, values, selection, selectionArgs);
			// TODO something we need to do with count?
			if(count != 0) {
				retval =  true;
			}
//			retval = true;
		}
		return retval;
	}
	
	/**
	 * Delete a ProductionSite.
	 * @param	site	The ProductionSite to delete
	 * @return	The number of rows deleted (should be only 1)
	 */
	public int deleteProductionSite(ProductionSite site) {
		String selection = BaseColumns._ID + " LIKE ?";
		String[] selectionArgs = {String.valueOf(site.get_id())};
		return database.delete(TABLE_NAME, selection, selectionArgs);
	}
	
	/**
	 * Delete a ProductionSite.
	 * @param	siteNr	The Number of the ProductionSite to delete
	 * @return	The number of rows deleted (should be only 1)
	 */
	public int deleteProductionSite(ProductionSiteNr siteNr) {
		String selection = COLUMN_SITENR + " LIKE ?";
		String[] selectionArgs = {siteNr.toString()};
		return database.delete(TABLE_NAME, selection, selectionArgs);
	}
	
	/**
	 * Get a List of all the ProductionSites this user has.
	 * @return	list of all ProductionSites
	 */
	public List<ProductionSite> getAllProductionSites() {
		List<ProductionSite> sites = new ArrayList<ProductionSite>();
		String sortorder = COLUMN_SITENR + " ASC";
		
		Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, sortorder);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()) {
			ProductionSite site = createFromCursor(cursor);
			sites.add(site);
			cursor.moveToNext();
		}
		
		cursor.close();
		return sites;
	}
	
	/**
	 * Construct a ProductionSite from the row pointed to by the Cursor.
	 * @param 	cursor	Pointer to a row in the table
	 * @return	A ProductionSite created from the data
	 */
	private ProductionSite createFromCursor(Cursor cursor) {
		String siteNrString = cursor.getString(1);
		ProductionSite site = new ProductionSite(siteNrString);
		
		site.set_id(cursor.getLong(0));
		if(!cursor.isNull(2)) site.setName(cursor.getString(2));
		if(!cursor.isNull(3)) site.setAddress(cursor.getString(3));
		if(!cursor.isNull(4)) site.setPostnr(cursor.getString(4));
		if(!cursor.isNull(5)) site.setPostaddress(cursor.getString(5));
		if(!cursor.isNull(6)) site.setCoordinates(cursor.getString(6));
		if(!cursor.isNull(7)) site.setImageUriStr(cursor.getString(7));
		
		return site;
	}
	
	
}
