package com.innovention.weddingplanner.dao;

import org.joda.time.DateTime;

import com.innovention.weddingplanner.bean.WeddingInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import static com.innovention.weddingplanner.dao.ConstantesDAO.*;

/**
 * DAO class for wedding info table
 * @author YCH
 *
 */
public class WeddingInfoDao<T extends WeddingInfo> implements IDao<T> {
	
	// Handle on the currently open database
	private SQLiteDatabase db;
	
	private static final String TAG=WeddingInfoDao.class.getName();
	
	public WeddingInfoDao(final Context context, final SQLiteDatabase db) {
		Log.d(TAG, "Constructor - " + "create WeddingInfoDAO service");
		this.db = db;
	}

	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDao#insert(com.innovention.weddingplanner.dao.IDtoBean)
	 */
	@Override
	public long insert(final T bean) {
		Log.d(TAG, "insertInfo - " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_WEDDATE, bean.getWeddingDate().toString());
		return db.insert(TABLE_WEDDINGINFO, null, values);
	}
	
	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDao#update(int, com.innovention.weddingplanner.dao.IDtoBean)
	 */
	@Override
	public int update(int id, final T bean) {
		Log.d(TAG, "updateInfo - " + "Update table with id " + id + " and date " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_WEDDATE, bean.getWeddingDate().toString());
		//int result = db.update(TABLE_WEDDINGINFO, values, null, null);
		int result = db.update(TABLE_WEDDINGINFO, values, COL_ID + "=?", new String[] {String.valueOf(id)});
		Log.d(TAG, "" + result + "rows affected");
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDao#removeWithId(int)
	 */
	@Override
	public int removeWithId(int id) {
		return db.delete(TABLE_WEDDINGINFO, COL_ID + " = " + id, null);
	}
	
	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDao#get()
	 */
	@Override
	public T get() {
		Cursor c = getCursorWeddingInfo();
		T bean = (T) cursorToBean(c);
		if (bean != null)
			Log.d(TAG, "getInfo - " + bean.toString());
		return bean;
		
	}
	
	
	/**
	 * Gets a cursor from WeddingInfo table
	 * @return
	 */
	private Cursor getCursorWeddingInfo() {
		return db.query(TABLE_WEDDINGINFO, new String[] {COL_ID, COL_WEDDATE}, null, null, null, null, null);
	}
	
	
	/**
	 * Convert the cursor from table to a the unique bean WeddingInfo
	 * @param c
	 * @return an item
	 */
	private T cursorToBean(final Cursor c) {
		Log.d(TAG, "cursorToBean");
		if (c.getCount() == 0) return null;
		Log.d(TAG, "Count = " + c.getCount());
		c.moveToFirst();
		Log.d(TAG, "Entry " + c.getInt(NUM_COL_ID) +"," + c.getString(NUM_COL_WEDDATE));
		T bean = (T) new WeddingInfo();
		bean.setId(c.getInt(NUM_COL_ID));
		bean.setWeddingDate(DateTime.parse(c.getString(NUM_COL_WEDDATE)));
		Log.d(TAG, "Pass bean " + bean.toString());
		c.close();
		return bean;
	}
	
}
