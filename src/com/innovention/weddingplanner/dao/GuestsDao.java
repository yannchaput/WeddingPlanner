package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.*;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.innovention.weddingplanner.bean.Contact;

/**
 * DAO class to store and retrieve contacts from db
 * @author YCH
 *
 */
public class GuestsDao implements IDao<Contact> {
	
	// Handle on the currently open database
	private SQLiteDatabase db;
		
	private static final String TAG=GuestsDao.class.getSimpleName();

	public GuestsDao(final SQLiteDatabase db) {
		Log.v(TAG, "Constructor - " + "create GuestsDao service");
		this.db = db;
	}

	/**
	 * Store a contact entity bean into db
	 */
	@Override
	public long insert(Contact bean) {
		Log.d(TAG, "insert - " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_NAME, bean.getName());
		values.put(COL_SURNAME, bean.getSurname());
		values.put(COL_TEL, bean.getTelephone());
		values.put(COL_EMAIL, bean.getMail());
		values.put(COL_ADDRESS, bean.getAddress());
		values.put(COL_INVITATION, bean.getInviteSent());
		values.put(COL_CHURCH, bean.getChurch());
		values.put(COL_TOWNHALL, bean.getTownHall());
		values.put(COL_COCKTAIL, bean.getCocktail());
		values.put(COL_PARTY, bean.getParty());
		values.put(COL_RSVP, bean.getResponse().toString());
		return db.insert(TABLE_GUESTS, null, values);
	}

	@Override
	public int update(int id, Contact bean) {
		Log.d(TAG, "update - " + "Update table with id " + id + " and bean " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_NAME, bean.getName());
		values.put(COL_SURNAME, bean.getSurname());
		values.put(COL_TEL, bean.getTelephone());
		values.put(COL_EMAIL, bean.getMail());
		values.put(COL_ADDRESS, bean.getAddress());
		values.put(COL_INVITATION, bean.getInviteSent());
		values.put(COL_CHURCH, bean.getChurch());
		values.put(COL_TOWNHALL, bean.getTownHall());
		values.put(COL_COCKTAIL, bean.getCocktail());
		values.put(COL_PARTY, bean.getParty());
		values.put(COL_RSVP, bean.getResponse().toString());
		int result = db.update(TABLE_GUESTS, values, COL_ID + "=?", new String[] {String.valueOf(id)});
		Log.d(TAG, "" + result + "rows affected");
		return result;
	}

	@Override
	public int removeWithId(int id) {
		return db.delete(TABLE_GUESTS, COL_ID + " = " + id, null);
	}

	@Override
	public Contact get() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Cursor getCursor() {
		// TODO Auto-generated method stub
		return db.query(TABLE_GUESTS, new String[] {COL_ID, COL_NAME, COL_SURNAME, COL_TEL, COL_EMAIL,
				COL_ADDRESS, COL_INVITATION, COL_CHURCH, COL_TOWNHALL, COL_COCKTAIL, COL_PARTY, COL_RSVP}
		, null, null, null, null, null);
	}

}
