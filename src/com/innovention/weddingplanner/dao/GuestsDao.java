package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.*;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.WeddingInfo;

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
		values.put(COL_SURNAME, bean.getSurname());
		values.put(COL_NAME, bean.getName());
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
		values.put(COL_SURNAME, bean.getSurname());
		values.put(COL_NAME, bean.getName());
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
		return db.query(TABLE_GUESTS, new String[] {COL_ID, COL_SURNAME, COL_NAME, COL_TEL, COL_EMAIL,
				COL_ADDRESS, COL_INVITATION, COL_CHURCH, COL_TOWNHALL, COL_COCKTAIL, COL_PARTY, COL_RSVP}
		, null, null, null, null, null);
	}
	
	/**
	 * Returns a list of Contact beans from DB
	 */
	@Override
	public List<Contact> getList() {
		Log.d(TAG, "Get list of Contact beans");
		Cursor c = getCursor();
		Log.d(TAG, "Count = " + c.getCount());
		if (c.getCount() == 0){
			Log.d(TAG, "No data available");
			return null;
		}
		
		Contact bean = null;
		Contact.ContactBuilder builder = new Contact.ContactBuilder();
		List<Contact> list = new ArrayList<Contact>();
		c.moveToFirst();
		for (c.moveToFirst();!c.isLast();c.moveToNext()) {
			Log.d(TAG, "Entry " + c.getInt(NUM_COL_ID) +"," + c.getString(NUM_COL_WEDDATE));
			bean = builder.withId(c.getInt(NUM_COL_ID))
					.surname(c.getString(NUM_COL_SURNAME))
					.name(c.getString(NUM_COL_NAME))
					.telephone(c.getString(NUM_COL_TEL))
					.address(c.getString(NUM_COL_ADDRESS))
					.mail(c.getString(NUM_COL_MAIL))
					.build();
			
			Log.d(TAG, "Insert bean " + bean.toString() + " to list");
			list.add(bean);
		}
		
		c.close();
		return list;
	}

}