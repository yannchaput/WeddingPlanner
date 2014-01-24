package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.*;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper
 * 
 * @author YCH
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private final static String TAG = DatabaseHelper.class.getSimpleName();

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		Log.d(TAG, "Constructor of db helper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.d(TAG, "onCreate : " + "create DB for the first time");
		createDb(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		Log.d(TAG, "onUpgrade : " + "recreate DB after upgrade from "
				+ oldVersion + "to " + newVersion);
		dropDb(db);
		onCreate(db);
	}

	public void recreateDb(SQLiteDatabase db) {
		Log.d(TAG, "recreateDb : " + "recreate DB on user action");
		dropDb(db);
		onCreate(db);
	}

	/**
	 * Create database
	 * @param db
	 */
	private void createDb(final SQLiteDatabase db) {
		Log.d(TAG, "Create database");
		Log.v(TAG, "Create weddinginfo table");
		db.execSQL(CREATE_WEDDINGINFO_TABLE);
		Log.v(TAG, "Create guests table");
		db.execSQL(CREATE_GUEST_TABLE);
		Log.v(TAG, "Create tasks table");
		db.execSQL(CREATE_TASK_TABLE);
	}

	/**
	 * Drop the whole database
	 * 
	 * @param db
	 */
	private void dropDb(final SQLiteDatabase db) {
		Log.d(TAG, "Drop database");
		db.execSQL(DROP_WEDDINGINFO_TABLE);
		db.execSQL(DROP_GUEST_TABLE);
		db.execSQL(DROP_TASK_TABLE);
	}
	
	/**
	 * Convert an integer retrieved from SQLLite db to boolean
	 * @param value
	 * @return
	 */
	public static boolean convertIntToBool(int value) {
		return (value ==1) ? true: false;
	}

}
