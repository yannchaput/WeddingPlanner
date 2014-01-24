package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_WEDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_WEDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.TABLE_WEDDINGINFO;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.innovention.weddingplanner.bean.WeddingInfo;

/**
 * DAO class for wedding info table
 * @author YCH
 *
 */
public class WeddingInfoDao implements IDao<WeddingInfo> {
	
	// Handle on the currently open database
	private SQLiteDatabase db;
	
	private static final String TAG=WeddingInfoDao.class.getSimpleName();
	
	public WeddingInfoDao(final SQLiteDatabase db) {
		Log.v(TAG, "Constructor - " + "create WeddingInfoDAO service");
		this.db = db;
	}

	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDao#insert(com.innovention.weddingplanner.dao.IDtoBean)
	 */
	@Override
	public long insert(final WeddingInfo bean) {
		Log.d(TAG, "insertInfo - " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_WEDDATE, bean.getWeddingDate().toString());
		return db.insert(TABLE_WEDDINGINFO, null, values);
	}
	
	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDao#update(int, com.innovention.weddingplanner.dao.IDtoBean)
	 */
	@Override
	public int update(int id, final WeddingInfo bean) {
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
	public WeddingInfo get() {
		Cursor c = getCursor();
		WeddingInfo bean = cursorToBean(c);
		if (bean != null)
			Log.d(TAG, "getInfo - " + bean.toString());
		return bean;
		
	}
	
	
	@Override
	public WeddingInfo get(long id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets a cursor from WeddingInfo table
	 * @return
	 */
	public Cursor getCursor() {
		return db.query(TABLE_WEDDINGINFO, new String[] {COL_ID, COL_WEDDATE}, null, null, null, null, null);
	}
	
	/**
	 * Returns a list of WeddingInfo beans
	 */
	@Override
	public List<WeddingInfo> getList() {
		Log.d(TAG, "Get list of WeddingInfo beans");
		Cursor c = getCursor();
		Log.d(TAG, "Count = " + c.getCount());
		if (c.getCount() == 0){
			Log.d(TAG, "No data available");
			return null;
		}
		
		WeddingInfo bean = null;
		List<WeddingInfo> list = new ArrayList<WeddingInfo>();
		c.moveToFirst();
		for (c.moveToFirst();!c.isLast();c.moveToNext()) {
			Log.d(TAG, "Entry " + c.getInt(NUM_COL_ID) +"," + c.getString(NUM_COL_WEDDATE));
			bean = new WeddingInfo();
			bean.setId(c.getInt(NUM_COL_ID));
			bean.setWeddingDate(DateTime.parse(c.getString(NUM_COL_WEDDATE)));
			Log.d(TAG, "Insert bean " + bean.toString() + " to list");
			list.add(bean);
		}
		
		c.close();
		return list;
	}
	
	
	/**
	 * Convert the cursor from table to a the unique bean WeddingInfo
	 * @param c
	 * @return an item
	 */
	private WeddingInfo cursorToBean(final Cursor c) {
		Log.d(TAG, "cursorToBean");
		if (c.getCount() == 0) return null;
		Log.d(TAG, "Count = " + c.getCount());
		c.moveToFirst();
		Log.d(TAG, "Entry " + c.getInt(NUM_COL_ID) +"," + c.getString(NUM_COL_WEDDATE));
		WeddingInfo bean = new WeddingInfo();
		bean.setId(c.getInt(NUM_COL_ID));
		bean.setWeddingDate(DateTime.parse(c.getString(NUM_COL_WEDDATE)));
		Log.d(TAG, "Pass bean " + bean.toString());
		c.close();
		return bean;
	}
	
}
