/**
 * 
 */
package com.innovention.weddingplanner.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_COMPANY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_CONTACT;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_ADDRESS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_PHONE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_MAIL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_NOTE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.TABLE_VENDORS;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.innovention.weddingplanner.bean.Vendor;

/**
 * Vendor DAO class
 * @author YCH
 *
 */
public class VendorDao implements IDao<Vendor> {

	// Logger category
	public static final String TAG = VendorDao.class.getSimpleName();
	// Db  instance
	private SQLiteDatabase db;
	
	/**
	 * Default constructor
	 */
	public VendorDao(final SQLiteDatabase db) {
		this.db = db;
	}

	@Override
	public long insert(Vendor bean) {
		checkNotNull(bean,"vendor bean to save can not be null");
		Log.d(TAG, "insert - " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_VENDOR_COMPANY, bean.getCompanyName());
		values.put(COL_VENDOR_CONTACT, bean.getContactDetails());
		values.put(COL_VENDOR_ADDRESS, bean.getAddress());
		values.put(COL_VENDOR_MAIL, bean.getMail());
		values.put(COL_VENDOR_PHONE, bean.getTelephone());
		values.put(COL_VENDOR_CATEGORY, bean.getCategory());
		values.put(COL_VENDOR_NOTE, bean.getNote());
		return db.insert(TABLE_VENDORS, null, values);
	}

	@Override
	public int update(int id, Vendor bean) {
		checkNotNull(bean,"vendor bean to save can not be null");
		checkArgument(id > 0, "An id can not be negative");
		Log.d(TAG, "update - " + "Update table with id " + id + " and bean " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_VENDOR_COMPANY, bean.getCompanyName());
		values.put(COL_VENDOR_CONTACT, bean.getContactDetails());
		values.put(COL_VENDOR_ADDRESS, bean.getAddress());
		values.put(COL_VENDOR_MAIL, bean.getMail());
		values.put(COL_VENDOR_PHONE, bean.getTelephone());
		values.put(COL_VENDOR_CATEGORY, bean.getCategory());
		values.put(COL_VENDOR_NOTE, bean.getNote());
		int rows = db.update(TABLE_VENDORS, values, COL_ID + " = ?", new String[] {String.valueOf(bean.getId())});
		Log.d(TAG, "" + rows + "rows affected");
		return rows;
	}

	@Override
	public int removeWithId(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vendor get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vendor get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor getCursor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor getCursor(String selectionClause, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Vendor> getList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Vendor> getList(String selectionClause, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return null;
	}

}
