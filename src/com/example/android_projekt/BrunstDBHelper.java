package com.example.android_projekt;

import com.example.android_projekt.productionsite.ProductionSiteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A Helper class handling the database.
 */
public class BrunstDBHelper extends SQLiteOpenHelper 
{
	private static final String DB_NAME = "brunstkalender.db";
	private static final int DB_VERSION = 1;

	/** Constructor. */
	public BrunstDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Create the tables.
		ProductionSiteDB.onCreate(db);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys=ON");
	};

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		ProductionSiteDB.onUpgrade(db);
	}

}
