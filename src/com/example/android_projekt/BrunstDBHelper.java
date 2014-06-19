package com.example.android_projekt;

import com.example.android_projekt.event.EventDB;
import com.example.android_projekt.event.EventTypeDB;
import com.example.android_projekt.event.HeatEventDB;
import com.example.android_projekt.event.HeatSignDB;
import com.example.android_projekt.event.HeatStrengthDB;
import com.example.android_projekt.event.NoteDB;
import com.example.android_projekt.individ.IndividualDB;
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
		ProductionSiteDB.onCreate(db);
		IndividualDB.onCreate(db);
		EventTypeDB.onCreate(db);
		EventDB.onCreate(db);
		NoteDB.onCreate(db);
		HeatSignDB.onCreate(db);
		HeatStrengthDB.onCreate(db);
		HeatEventDB.onCreate(db);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys=ON");
	};

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ProductionSiteDB.onUpgrade(db);
		IndividualDB.onUpgrade(db);
		EventTypeDB.onUpgrade(db);
		EventDB.onUpgrade(db);
		NoteDB.onUpgrade(db);
		HeatSignDB.onUpgrade(db);
		HeatStrengthDB.onUpgrade(db);
		HeatEventDB.onUpgrade(db);
	}

}
