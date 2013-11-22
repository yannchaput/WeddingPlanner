package com.innovention.weddingplanner.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.innovention.weddingplanner.dao.ConstantesDAO.*;

/**
 * Database helper
 * @author YCH
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private final static String TAG = DatabaseHelper.class.getName();


	public DatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		Log.d(TAG, "Constructor of db helper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		Log.d(TAG, "onCreate : " + "create DB for the first time");
		db.execSQL(CREATE_BDD);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.d(TAG, "onUpgrade : " + "recreate DB after upgrade from " + oldVersion + "to " + newVersion);
		db.execSQL(DROP_DB);
		onCreate(db);
	}
	
	public void recreateDb(SQLiteDatabase db) {
		Log.d(TAG, "recreateDb : " + "recreate DB on user action");
		db.execSQL(DROP_DB);
		onCreate(db);
	}

}
