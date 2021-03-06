package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ADDRESS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_CHURCH;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_COCKTAIL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_EMAIL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_INVITATION;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_NAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_PARTY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_RSVP;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_SURNAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TEL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TOWNHALL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_GUEST_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_ADDRESS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_CHURCH;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_COCKTAIL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_INVITATION;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_MAIL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_NAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_PARTY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_RSVP;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_SURNAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TEL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TOWNHALL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_GUEST_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.TABLE_GUESTS;
import static com.innovention.weddingplanner.dao.DatabaseHelper.convertIntToBool;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Objects;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.Contact.Category;
import com.innovention.weddingplanner.bean.Contact.ResponseType;

/**
 * DAO class to store and retrieve contacts from db
 * @author YCH
 *
 */
public class GuestsDao implements IDao<Contact> {
	
	private Context context;
		
	private static final String TAG=GuestsDao.class.getSimpleName();

	public GuestsDao(Context ctxt) {
		Log.v(TAG, "Constructor - " + "create GuestsDao service");
		this.context = ctxt;
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
		values.put(COL_GUEST_CATEGORY, bean.getCategory().toString());
		return DaoLocator.getInstance(context).getWritableDatabase()
				.insert(TABLE_GUESTS, null, values);
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
		values.put(COL_GUEST_CATEGORY, bean.getCategory().toString());
		int result = DaoLocator.getInstance(context).getWritableDatabase()
				.update(TABLE_GUESTS, values, COL_ID + "=?", new String[] {String.valueOf(id)});
		Log.d(TAG, "" + result + "rows affected");
		return result;
	}

	@Override
	public int removeWithId(int id) {
		return DaoLocator.getInstance(context).getWritableDatabase()
				.delete(TABLE_GUESTS, COL_ID + " = " + id, null);
	}

	@Override
	public Contact get() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Contact get(long id) {
		Contact bean = null;
		Contact.ContactBuilder builder = new Contact.ContactBuilder();
		
		Cursor c = DaoLocator.getInstance(context).getReadDatabase()
				.query(TABLE_GUESTS, new String[] {COL_ID, COL_SURNAME, COL_NAME, COL_TEL, COL_EMAIL,
				COL_ADDRESS, COL_INVITATION, COL_CHURCH, COL_TOWNHALL, COL_COCKTAIL, COL_PARTY, COL_RSVP, COL_GUEST_CATEGORY}
		, COL_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
		
		if (c.getCount() == 0){
			Log.d(TAG, "No data available");
			return null;
		}
		
		c.moveToFirst();
		bean = builder.withId(c.getInt(NUM_COL_ID))
				.surname(c.getString(NUM_COL_SURNAME))
				.name(c.getString(NUM_COL_NAME))
				.telephone(c.getString(NUM_COL_TEL))
				.address(c.getString(NUM_COL_ADDRESS))
				.mail(c.getString(NUM_COL_MAIL))
				.inviteSent(c.getInt(NUM_COL_INVITATION) == 1 ? Boolean.TRUE : Boolean.FALSE)
				.atChurch(c.getInt(NUM_COL_CHURCH) == 1 ? Boolean.TRUE : Boolean.FALSE)
				.atTownHall(c.getInt(NUM_COL_TOWNHALL) == 1 ? Boolean.TRUE : Boolean.FALSE)
				.AtCocktail(c.getInt(NUM_COL_COCKTAIL) == 1 ? Boolean.TRUE : Boolean.FALSE)
				.AtParty(c.getInt(NUM_COL_PARTY) == 1 ? Boolean.TRUE : Boolean.FALSE)
				.answerPending(Objects.equal(c.getString(NUM_COL_RSVP), ResponseType.PENDING.toString()) ? Boolean.TRUE : Boolean.FALSE )
				.answerAttend(Objects.equal(c.getString(NUM_COL_RSVP), ResponseType.ATTEND.toString()) ? Boolean.TRUE : Boolean.FALSE )
				.answerNotAttend(Objects.equal(c.getString(NUM_COL_RSVP), ResponseType.NOTATTEND.toString()) ? Boolean.TRUE : Boolean.FALSE )
				.withCategory(Category.valueOf(c.getString(NUM_COL_GUEST_CATEGORY)))
				.build();
		c.close();
		
		return bean;
	}

	@Override
	public Cursor getCursor() {
		// TODO Auto-generated method stub
		return DaoLocator.getInstance(context).getReadDatabase()
				.query(TABLE_GUESTS, new String[] {COL_ID, COL_SURNAME, COL_NAME, COL_TEL, COL_EMAIL,
				COL_ADDRESS, COL_INVITATION, COL_CHURCH, COL_TOWNHALL, COL_COCKTAIL, COL_PARTY, COL_RSVP,
				COL_GUEST_CATEGORY}
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
			Log.d(TAG, new StringBuilder().append("Get contact ")
					.append(c.getLong(NUM_COL_ID))
					.append(", ")
					.append(c.getString(NUM_COL_NAME))
					.append(", ")
					.append(c.getString(NUM_COL_SURNAME)).toString());
			
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
	
	@Override
	public Cursor getCursor(String selectionClause, String[] selectionArgs) {
		
		return getCursor(selectionClause, selectionArgs, null);
	}

	@Override
	public Cursor getCursor(String selectionClause, String[] selectionArgs, String orderBy) {
		Cursor c = DaoLocator.getInstance(context).getReadDatabase()
				.query(TABLE_GUESTS, new String[] {COL_ID, COL_SURNAME, COL_NAME, COL_TEL, COL_EMAIL,
				COL_ADDRESS, COL_INVITATION, COL_CHURCH, COL_TOWNHALL, COL_COCKTAIL, COL_PARTY, COL_RSVP, COL_GUEST_CATEGORY}
		, selectionClause, selectionArgs, null, null, orderBy);
		return c;
	}
	
	public Cursor getCursorCategory() {
		Cursor c = DaoLocator.getInstance(context).getReadDatabase()
				.query(TABLE_GUESTS, new String[] {COL_ID, COL_GUEST_CATEGORY}
		, null, null, COL_GUEST_CATEGORY, null, null);
		return c;
	}

	@Override
	public List<Contact> getList(String selectionClause, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cursor rawQuery(String selectionClause, String[] selectionArgs) {
		return DaoLocator.getInstance(context).getReadDatabase().rawQuery(selectionClause, selectionArgs);
	}

}
