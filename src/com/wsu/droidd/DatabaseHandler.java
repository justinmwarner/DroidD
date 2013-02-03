package com.wsu.droidd;

/*
 * From http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "procs";

	// Procs table name
	private static final String TABLE_PROCS = "procs";

	// Procs Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_IP = "ip";
	private static final String KEY_PORT = "port";
	private static final String KEY_LAT = "lat";
	private static final String KEY_LON = "lon";
	private static final String KEY_STATE = "state";
	private static final String KEY_CITY = "city";
	private static final String KEY_ZIP = "zip";
	private static final String KEY_WHOIS = "whois";

	private static final String TAG = "DatabaseHandler";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DatabaseHandler(Context context, String name, CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	public DatabaseHandler(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// These is where we need to write create table statements. This is
		// called when database is created.
		String CREATE_PROCS_TABLE = "CREATE TABLE " + TABLE_PROCS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, " + KEY_IP + " TEXT, " + KEY_PORT
				+ " TEXT, " + KEY_LAT + " TEXT, " + KEY_LON + " TEXT, " + KEY_STATE + " TEXT, "
				+ KEY_CITY + " TEXT, " + KEY_ZIP + " TEXT,  " + KEY_WHOIS + " TEXT" + ")";
		db.execSQL(CREATE_PROCS_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This method is called when database is upgraded like modifying the
		// table structure, adding constraints to database etc.,
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROCS);

		// Create tables again
		onCreate(db);

	}

	// Adding new proc
	public void addProc(Proc proc) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, proc.getName());
		values.put(KEY_IP, proc.getIp());
		values.put(KEY_PORT, proc.getPort());
		values.put(KEY_LAT, proc.getLat());
		values.put(KEY_LON, proc.getLon());
		values.put(KEY_STATE, proc.getState());
		values.put(KEY_CITY, proc.getCity());
		values.put(KEY_ZIP, proc.getZip());
		values.put(KEY_WHOIS, proc.getWhois());

		// Inserting Row
		db.insert(TABLE_PROCS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single proc by name as each name can have multiple proc's
	// associated with it.
	public ArrayList<Proc> getProc(String name) {
		ArrayList<Proc> list = new ArrayList<Proc>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PROCS, new String[] { KEY_ID, KEY_NAME, KEY_IP, KEY_PORT,
				KEY_LAT, KEY_LON, KEY_STATE, KEY_CITY, KEY_ZIP, KEY_WHOIS }, KEY_NAME + "=?",
				new String[] { name }, null, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); cursor.isLast(); cursor.moveToNext()) {
				try {
					list.add(new Proc(cursor.getString(1), cursor.getString(2), Integer
							.parseInt(cursor.getString(3)), cursor.getString(4), cursor
							.getString(5), cursor.getString(6), cursor.getString(7), cursor
							.getString(8), cursor.getString(9)));
				} catch (Exception e) {
				}
			}
		}
		return list;
	}

	// Getting All Procs
	public ArrayList<String> getAllProcs() {
		ArrayList<String> procList = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_PROCS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// looping through all rows and adding to list
		if (cursor != null) {
			for (cursor.moveToFirst(); cursor.isLast(); cursor.moveToNext()) {
				procList.add(cursor.getString(1));
			}
			return procList;
		} else {
			return new ArrayList<String>();
		}
	}

	// Getting procs Count
	public int getProcsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PROCS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return count
		return cursor.getCount();
	}

	// Updating single proc. Might be needed later on.
	/*
	 * public int updateProc(Proc proc) { SQLiteDatabase db =
	 * this.getWritableDatabase();
	 * 
	 * ContentValues values = new ContentValues(); values.put(KEY_NAME,
	 * proc.getName()); values.put(KEY_IPS, proc.getIps());
	 * 
	 * // updating row return db.update(TABLE_PROCS, values, KEY_ID + " = ?",
	 * new String[] { String.valueOf(proc.getId()) }); }
	 */

	// Deleting single proc. Not needed yet. Implement later.
	/*
	 * public void deleteProc(Proc proc) { SQLiteDatabase db =
	 * this.getWritableDatabase(); db.delete(TABLE_PROCS, KEY_ID + " = ?", new
	 * String[] { String.valueOf(proc.getId()) }); db.close(); }
	 */

}
