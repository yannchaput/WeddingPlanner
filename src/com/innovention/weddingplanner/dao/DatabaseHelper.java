package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.CREATE_GUEST_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.CREATE_TASK_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.CREATE_VENDOR_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.CREATE_WEDDINGINFO_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.CREATE_BUDGET_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.DROP_GUEST_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.DROP_TASK_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.DROP_VENDOR_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.DROP_WEDDINGINFO_TABLE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.DROP_BUDGET_TABLE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.commons.lang3.StringUtils;

import com.innovention.weddingplanner.Constantes;
import com.innovention.weddingplanner.bean.Contact.Category;
import com.innovention.weddingplanner.exception.TechnicalException;

import android.app.DownloadManager.Query;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.util.Log;

/**
 * Database helper
 * 
 * @author YCH
 * 
 */
public final class DatabaseHelper extends SQLiteOpenHelper {

	private final static String TAG = DatabaseHelper.class.getSimpleName();
	private Context ctxt = null;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		Log.d(TAG, "Constructor of db helper");
		ctxt = context;
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
		try {
			recreateDb(db);
		} catch (TechnicalException e) {
			Log.e(TAG, Constantes.BACKUP_ERROR);
		}
	}

	public void recreateDb(SQLiteDatabase db) throws TechnicalException {
		SQLiteDatabase backup = null;
		backup = backupDb(db);
		dropDb(db);
		createDb(db);
		transferData(db, backup);
	}
	
	public void resetDb(SQLiteDatabase db) throws TechnicalException {
		dropDb(db);
		createDb(db);
	}

	/**
	 * Backup the db in order to transfer data after an upgrade
	 * 
	 * @param db
	 *            the backup
	 * @throws TechnicalException
	 */
	private SQLiteDatabase backupDb(final SQLiteDatabase db)
			throws TechnicalException {

		File sd = Environment.getExternalStorageDirectory();
		// File data = ctxt.getFilesDir();
		FileChannel src = null;
		FileChannel dst = null;
		File currentDB = null;
		File backupDB = null;
		final String currentDBPath = ctxt
				.getDatabasePath(ConstantesDAO.NOM_BDD).getAbsolutePath();
		final String backupDBPath = ctxt.getExternalFilesDir(null)
				.getAbsolutePath() + "//" + ConstantesDAO.BDD_BACKUP_NOM;

		Log.d(TAG, "current DB location : " + currentDBPath);
		Log.d(TAG, "Backup DB location: " + backupDBPath);

		try {
			if (sd.canWrite()) {

				Log.d(TAG, "Backing up database onto " + backupDBPath);

				currentDB = new File(currentDBPath);
				backupDB = new File(backupDBPath);

				if (!currentDB.exists()) {
					Log.e(TAG,
							"Following file not found :"
									+ currentDB.getAbsolutePath());
					throw new TechnicalException(
							Constantes.BACKUP_ERROR_FILENOTFOUND);
				}

				src = new FileInputStream(currentDB).getChannel();
				dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());

				Log.d(TAG,
						"Back up available there :  "
								+ backupDB.getAbsolutePath());

			} else
				Log.d(TAG,
						"Unable to backup on sd card. SD card is probably locked or unavailable.");
		} catch (FileNotFoundException e) {
			throw new TechnicalException(Constantes.BACKUP_ERROR, e);
		} catch (IOException e) {
			throw new TechnicalException(Constantes.BACKUP_ERROR, e);
		} finally {
			try {
				if (src != null)
					src.close();
				if (dst != null)
					dst.close();
			} catch (IOException e) {
				throw new TechnicalException(Constantes.BACKUP_ERROR, e);
			}
		}

		return SQLiteDatabase.openDatabase(backupDB.getAbsolutePath(), null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * Copy data from source db to backup one
	 * 
	 * @param backup
	 * @param dst
	 */
	private void transferData(final SQLiteDatabase dst,
			final SQLiteDatabase backup) throws TechnicalException {

		final File sd = Environment.getExternalStorageDirectory();
		final File data = ctxt.getFilesDir();
		final String currentDBPath = data.getAbsolutePath() + "//"
				+ ConstantesDAO.BDD_PATH + "//" + ConstantesDAO.NOM_BDD;
		final String backupDBPath = sd.getAbsolutePath()
				+ data.getAbsolutePath() + "//" + ConstantesDAO.BDD_PATH + "//"
				+ ConstantesDAO.BDD_BACKUP_NOM;
		try {
			Log.d(TAG, String.format("Transfer data from %s to %s",
					currentDBPath, backupDBPath));

			if (!dst.isOpen())
				SQLiteDatabase.openDatabase(currentDBPath, null,
						SQLiteDatabase.OPEN_READONLY);
			if (!backup.isOpen())
				SQLiteDatabase.openDatabase(backupDBPath, null,
						SQLiteDatabase.OPEN_READWRITE);

			String[] columns = null;
			String query = null;
			Cursor c = null;

			// Copy Wedding info table
			columns = new String[] { ConstantesDAO.COL_ID,
					ConstantesDAO.COL_WEDDATE };
			query = SQLiteQueryBuilder.buildQueryString(false,
					ConstantesDAO.TABLE_WEDDINGINFO, columns, null, null, null,
					null, null);
			c = backup.rawQuery(query, null);
			if (c != null) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					int id = c.getInt(0);
					String date = c.getString(1);
					ContentValues values = new ContentValues();
					values.put(ConstantesDAO.COL_ID, id);
					values.put(ConstantesDAO.COL_WEDDATE, date);
					dst.insert(ConstantesDAO.TABLE_WEDDINGINFO, null, values);
					c.moveToNext();
				}
				c.close();
			}
			// Copy Guests table
			columns = new String[] { ConstantesDAO.COL_ID,
					ConstantesDAO.COL_SURNAME, ConstantesDAO.COL_NAME,
					ConstantesDAO.COL_TEL, ConstantesDAO.COL_EMAIL,
					ConstantesDAO.COL_ADDRESS, ConstantesDAO.COL_INVITATION,
					ConstantesDAO.COL_CHURCH, ConstantesDAO.COL_TOWNHALL,
					ConstantesDAO.COL_COCKTAIL, ConstantesDAO.COL_PARTY,
					ConstantesDAO.COL_RSVP };
			query = SQLiteQueryBuilder.buildQueryString(false,
					ConstantesDAO.TABLE_GUESTS, columns, null, null, null,
					null, null);
			c = backup.rawQuery(query, null);
			if (c != null) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					int id = c.getInt(ConstantesDAO.NUM_COL_ID);
					String surname = c.getString(ConstantesDAO.NUM_COL_SURNAME);
					String name = c.getString(ConstantesDAO.NUM_COL_NAME);
					String tel = c.getString(ConstantesDAO.NUM_COL_TEL);
					String mail = c.getString(ConstantesDAO.NUM_COL_MAIL);
					String address = c.getString(ConstantesDAO.NUM_COL_ADDRESS);
					int inv = c.getInt(ConstantesDAO.NUM_COL_INVITATION);
					int church = c.getInt(ConstantesDAO.NUM_COL_CHURCH);
					int townhall = c.getInt(ConstantesDAO.NUM_COL_TOWNHALL);
					int cocktail = c.getInt(ConstantesDAO.NUM_COL_COCKTAIL);
					int party = c.getInt(ConstantesDAO.NUM_COL_PARTY);
					String rsvp = c.getString(ConstantesDAO.NUM_COL_RSVP);

					ContentValues values = new ContentValues();
					values.put(ConstantesDAO.COL_ID, id);
					values.put(ConstantesDAO.COL_SURNAME, surname);
					values.put(ConstantesDAO.COL_NAME, name);
					values.put(ConstantesDAO.COL_TEL, tel);
					values.put(ConstantesDAO.COL_EMAIL, mail);
					values.put(ConstantesDAO.COL_ADDRESS, address);
					values.put(ConstantesDAO.COL_INVITATION, inv);
					values.put(ConstantesDAO.COL_CHURCH, church);
					values.put(ConstantesDAO.COL_TOWNHALL, townhall);
					values.put(ConstantesDAO.COL_COCKTAIL, cocktail);
					values.put(ConstantesDAO.COL_PARTY, party);
					values.put(ConstantesDAO.COL_RSVP, rsvp);
					values.put(ConstantesDAO.COL_GUEST_CATEGORY, Category.OTHER.toString());
					dst.insert(ConstantesDAO.TABLE_GUESTS, null, values);
					c.moveToNext();
				}
				c.close();
			}
			// Copy tasks table
			columns = new String[] { ConstantesDAO.COL_ID,
					ConstantesDAO.COL_TASK_STATUS, ConstantesDAO.COL_TASK_DESC,
					ConstantesDAO.COL_TASK_DUEDATE,
					ConstantesDAO.COL_TASK_REMINDDATE,
					ConstantesDAO.COL_TASK_REMINDOPTION };
			query = SQLiteQueryBuilder.buildQueryString(false,
					ConstantesDAO.TABLE_TASKS, columns, null, null, null, null,
					null);
			c = backup.rawQuery(query, null);
			if (c != null) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					int id = c.getInt(ConstantesDAO.NUM_COL_ID);
					int status = c.getInt(ConstantesDAO.NUM_COL_TASK_STATUS);
					String desc = c.getString(ConstantesDAO.NUM_COL_TASK_DESC);
					String dueDate = c
							.getString(ConstantesDAO.NUM_COL_TASK_DUEDATE);
					String remindDate = c
							.getString(ConstantesDAO.NUM_COL_TASK_REMINDDATE);
					String option = c
							.getString(ConstantesDAO.NUM_COL_TASK_REMINDOPTION);

					ContentValues values = new ContentValues();
					values.put(ConstantesDAO.COL_ID, id);
					values.put(ConstantesDAO.COL_TASK_STATUS, status);
					values.put(ConstantesDAO.COL_TASK_DESC, desc);
					values.put(ConstantesDAO.COL_TASK_DUEDATE, dueDate);
					values.put(ConstantesDAO.COL_TASK_REMINDDATE, remindDate);
					values.put(ConstantesDAO.COL_TASK_REMINDOPTION, option);
					dst.insert(ConstantesDAO.TABLE_TASKS, null, values);
					c.moveToNext();
				}
				c.close();
			}
			// Copy budget table
			columns = new String[] { ConstantesDAO.COL_ID,
					ConstantesDAO.COL_BUDGET_EXPENSE,
					ConstantesDAO.COL_BUDGET_VENDOR,
					ConstantesDAO.COL_BUDGET_CATEGORY,
					ConstantesDAO.COL_BUDGET_TOTAL_AMOUNT,
					ConstantesDAO.COL_BUDGET_PAID_AMOUNT,
					ConstantesDAO.COL_BUDGET_NOTE };
			query = SQLiteQueryBuilder.buildQueryString(false,
					ConstantesDAO.TABLE_BUDGET, columns, null, null, null,
					null, null);
			c = backup.rawQuery(query, null);
			if (c != null) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					int id = c.getInt(ConstantesDAO.NUM_COL_ID);
					String expense = c
							.getString(ConstantesDAO.NUM_COL_BUDGET_EXPENSE);
					String vendor = c
							.getString(ConstantesDAO.NUM_COL_BUDGET_VENDOR);
					String category = c
							.getString(ConstantesDAO.NUM_COL_BUDGET_CATEGORY);
					Double total = c
							.getDouble(ConstantesDAO.NUM_COL_BUDGET_TOTAL_AMOUNT);
					Double paid = c
							.getDouble(ConstantesDAO.NUM_COL_BUDGET_PAID_AMOUNT);
					String note = c
							.getString(ConstantesDAO.NUM_COL_BUDGET_NOTE);

					ContentValues values = new ContentValues();
					values.put(ConstantesDAO.COL_ID, id);
					values.put(ConstantesDAO.COL_BUDGET_EXPENSE, expense);
					values.put(ConstantesDAO.COL_BUDGET_VENDOR, vendor);
					values.put(ConstantesDAO.COL_BUDGET_CATEGORY, category);
					values.put(ConstantesDAO.COL_BUDGET_TOTAL_AMOUNT, total);
					values.put(ConstantesDAO.COL_BUDGET_PAID_AMOUNT, paid);
					values.put(ConstantesDAO.COL_BUDGET_NOTE, note);
					dst.insert(ConstantesDAO.TABLE_BUDGET, null, values);
					c.moveToNext();
				}
				c.close();
			}
			// Copy vendor table
			columns = new String[] { ConstantesDAO.COL_ID,
					ConstantesDAO.COL_VENDOR_COMPANY,
					ConstantesDAO.COL_VENDOR_CONTACT,
					ConstantesDAO.COL_VENDOR_ADDRESS,
					ConstantesDAO.COL_VENDOR_PHONE,
					ConstantesDAO.COL_VENDOR_MAIL,
					ConstantesDAO.COL_VENDOR_CATEGORY,
					ConstantesDAO.COL_VENDOR_NOTE };
			query = SQLiteQueryBuilder.buildQueryString(false,
					ConstantesDAO.TABLE_VENDORS, columns, null, null, null,
					null, null);
			c = backup.rawQuery(query, null);
			if (c != null) {
				c.moveToFirst();
				while (!c.isAfterLast()) {
					int id = c.getInt(ConstantesDAO.NUM_COL_ID);
					String company = c
							.getString(ConstantesDAO.NUM_COL_VENDOR_COMPANY);
					String contact = c
							.getString(ConstantesDAO.NUM_COL_VENDOR_CONTACT);
					String address = c
							.getString(ConstantesDAO.NUM_COL_VENDOR_ADDRESS);
					String phone = c
							.getString(ConstantesDAO.NUM_COL_VENDOR_PHONE);
					String mail = c
							.getString(ConstantesDAO.NUM_COL_VENDOR_MAIL);
					String category = c
							.getString(ConstantesDAO.NUM_COL_VENDOR_CATEGORY);
					String note = c
							.getString(ConstantesDAO.NUM_COL_VENDOR_NOTE);

					ContentValues values = new ContentValues();
					values.put(ConstantesDAO.COL_ID, id);
					values.put(ConstantesDAO.COL_VENDOR_COMPANY, company);
					values.put(ConstantesDAO.COL_VENDOR_CONTACT, contact);
					values.put(ConstantesDAO.COL_VENDOR_ADDRESS, address);
					values.put(ConstantesDAO.COL_VENDOR_PHONE, phone);
					values.put(ConstantesDAO.COL_VENDOR_MAIL, mail);
					values.put(ConstantesDAO.COL_VENDOR_CATEGORY, category);
					values.put(ConstantesDAO.COL_VENDOR_NOTE, note);
					dst.insert(ConstantesDAO.TABLE_VENDORS, null, values);
					c.moveToNext();
				}
				c.close();
			}

			backup.close();
		} catch (SQLiteException e) {
			throw new TechnicalException(Constantes.BACKUP_ERROR, e);
		} finally {
			File dbFile = new File(backup.getPath());
			if (dbFile != null && dbFile.exists()) {
				dbFile.delete();
			}
		}
	}

	/**
	 * Create database
	 * 
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
		Log.v(TAG, "Create vendors table");
		db.execSQL(CREATE_VENDOR_TABLE);
		Log.v(TAG, "Create budget table");
		db.execSQL(CREATE_BUDGET_TABLE);
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
		db.execSQL(DROP_VENDOR_TABLE);
		db.execSQL(DROP_BUDGET_TABLE);
	}

	/**
	 * Convert an integer retrieved from SQLLite db to boolean
	 * 
	 * @param value
	 * @return
	 */
	public static boolean convertIntToBool(int value) {
		return (value == 1) ? true : false;
	}

}
