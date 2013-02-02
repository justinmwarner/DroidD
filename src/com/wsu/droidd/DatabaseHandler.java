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
	private static final String KEY_IPS = "ips";

	private static final String TAG = "DatabaseHandler";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DatabaseHandler(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	public DatabaseHandler(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// These is where we need to write create table statements. This is
		// called when database is created.
		String CREATE_PROCS_TABLE = "CREATE TABLE " + TABLE_PROCS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_IPS + " TEXT" + ")";
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
		Log.d(TAG, "Adding proc:" + proc);
		ContentValues values = new ContentValues();
		values.put(KEY_ID, proc.getId());
		values.put(KEY_NAME, proc.getName());
		values.put(KEY_IPS, proc.getIps());

		// Inserting Row
		db.insert(TABLE_PROCS, null, values);
		db.close(); // Closing database connection
	}

	// Getting single proc
	public Proc getProc(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PROCS, new String[] { KEY_ID, KEY_NAME, KEY_IPS }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
		Proc proc = null;
		if (cursor != null) {
			try {
				cursor.moveToFirst();
				proc = new Proc(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
				Log.d(TAG, proc.toString());
			} catch (Exception e) {
				proc = null;
			}
		}
		return proc;
	}

	// Getting All Procs
	public ArrayList<Proc> getAllProcs() {
		ArrayList<Proc> procList = new ArrayList<Proc>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_PROCS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					Proc proc = new Proc();
					proc.setId(Integer.parseInt(cursor.getString(0)));
					proc.setName(cursor.getString(1));
					proc.setIps(cursor.getString(2));
					// Adding proc to list
					procList.add(proc);
				} while (cursor.moveToNext());
			} else {
				return new ArrayList<Proc>();
			}
			return procList;

		} else {
			return new ArrayList<Proc>();
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

	// Updating single proc
	public int updateProc(Proc proc) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, proc.getName());
		values.put(KEY_IPS, proc.getIps());

		// updating row
		return db.update(TABLE_PROCS, values, KEY_ID + " = ?", new String[] { String.valueOf(proc.getId()) });
	}

	// Deleting single proc
	public void deleteProc(Proc proc) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PROCS, KEY_ID + " = ?", new String[] { String.valueOf(proc.getId()) });
		db.close();
	}

}
