package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.NOM_BDD;
import static com.innovention.weddingplanner.dao.ConstantesDAO.VERSION_BDD;

import java.util.HashMap;

import com.innovention.weddingplanner.bean.IDtoBean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DAO Service locator
 * @author YCH
 *
 */
public class DaoLocator {

	private static final String TAG = DaoLocator.class.getSimpleName();
	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	private HashMap<SERVICES, IDao<? extends IDtoBean>> services;
	private static DaoLocator instance = null;
	
	public enum SERVICES {
		INFO, GUEST;
	}
	
	private DaoLocator(Context context) {
		Log.d(TAG, "Constructor");
		dbHelper = new DatabaseHelper(context, NOM_BDD, null, VERSION_BDD);
		database = dbHelper.getWritableDatabase();
		services = new HashMap<DaoLocator.SERVICES, IDao<? extends IDtoBean>>();
		services.put(SERVICES.INFO, new WeddingInfoDao(database));
		services.put(SERVICES.GUEST, new GuestsDao(database));
		
	}
	
	public static DaoLocator getInstance(Context context) {
		if (instance == null) {
			Log.d(TAG, "Create new singleton instance");
			instance = new DaoLocator(context);
		}
		return instance;
	}
	
	/**
	 * Retrieve according DAO
	 * @param service
	 * @return
	 */
	public IDao<? extends IDtoBean> get(SERVICES service) {
		return services.get(service);
	}

	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public SQLiteDatabase getDatabase() {
		Log.d(TAG, "getDatabase - get db");
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}
	
	

}
