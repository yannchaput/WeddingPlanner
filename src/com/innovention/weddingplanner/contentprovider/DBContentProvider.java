/**
 * 
 */
package com.innovention.weddingplanner.contentprovider;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_ADDRESS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_COMPANY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_CONTACT;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_MAIL;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_NOTE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_PHONE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.TABLE_VENDORS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.TABLE_BUDGET;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_EXPENSE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_NOTE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_PAID_AMOUNT;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_TOTAL_AMOUNT;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_VENDOR;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.innovention.weddingplanner.Constantes;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

/**
 * Content provider to access persistent data
 * 
 * @author YCH
 * 
 */
public final class DBContentProvider extends ContentProvider {
	
	// Log
	private static final String TAG = DBContentProvider.class.getSimpleName();

	// Service locator
	private DaoLocator locator;
	// Authority
	private static final String AUTHORITY = "com.innovention.weddingplanner.contentprovider.DBContentProvider";
	private static final String BASE_PATH = "data";
	// Root content uri for content provider
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	/**
	 * Vendor table access details How to query it, constantes, etc...
	 * 
	 * @author YCH
	 * 
	 */
	public static final class Vendors implements BaseColumns {

		public static final String SUFFIX = "vendors";
		public static final String SUFFIX_CATEGORIES = "categories";
		// Content URI for vendors table
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				DBContentProvider.CONTENT_URI, SUFFIX);
		// Mime type of a directory of vendor items
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.com.innovention.vendors";
		// Mime type of a single vendor item
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.com.innovention.vendors";
		// A projection of all columns in the table
		public static final String[] PROJECTION_ALL = { COL_ID, 
				COL_VENDOR_COMPANY, COL_VENDOR_CONTACT, COL_VENDOR_ADDRESS,
				COL_VENDOR_MAIL, COL_VENDOR_PHONE, COL_VENDOR_CATEGORY,
				COL_VENDOR_NOTE };
		// Default sort order
		public static final String SORT_ORDER_DEFAULT = COL_VENDOR_COMPANY
				+ " ASC";
		public static final String SORT_ORDER_CATEGORY = COL_VENDOR_CATEGORY
				+ " ASC";
	}
	
	/**
	 * Contract class for Budget table content provider
	 * @author YCH
	 *
	 */
	public static final class Budget implements BaseColumns {
		public static final String SUFFIX = "budget";
		public static final String SUFFIX_CATEGORIES = "categories";
		public static final String SUFFIX_SUM_PAID_AMOUNT="sum_paid_amount";
		public static final String SUFFIX_SUM_TOTAL_AMOUNT="sum_total_amount";
		// Content URI for budget table
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
						DBContentProvider.CONTENT_URI, SUFFIX);
		// Mime type of a group of budget lines
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.com.innovention.budget";
		// Mime type of a single budget entry
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.com.innovention.budget";
		// A projection of all columns in the table
		public static final String[] PROJECTION_ALL = { COL_ID, COL_BUDGET_EXPENSE, 
				COL_BUDGET_VENDOR, COL_BUDGET_CATEGORY, COL_BUDGET_TOTAL_AMOUNT,
				COL_BUDGET_PAID_AMOUNT, COL_BUDGET_NOTE };
		// Default sort order
		public static final String SORT_ORDER_DEFAULT = COL_BUDGET_EXPENSE
				+ " ASC";
		public static final String SORT_ORDER_CATEGORY = COL_BUDGET_CATEGORY
				+ " ASC";
	}

	/**
	 * CODES enum return values
	 * @author YCH
	 *
	 */
	private enum CODES {
		CODE_VENDORS(10), CODE_VENDOR_ITEM(20), CODE_VENDOR_CATEGORIES(30), 
		CODE_BUDGET(40), CODE_BUDGET_ITEM(50), CODE_BUDGET_CATEGORIES(60),
		CODE_BUDGET_SUM_PAID(70), CODE_BUDGET_SUM_TOTAL(80),
		UNDEFINED(-1);
		
		private int code = -1;
		
		private CODES(final int code) {
			this.code = code;
		}
		
		/**
		 * factory method
		 * @param code
		 * @return a CODES enum
		 */
		private static final CODES toEnum(final int value) {
			
			for (CODES code: CODES.values()) {
				if (value == code.code) return code;
			}
			
			return UNDEFINED;
		}
	}
	
	

	// URI matcher
	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Vendors.SUFFIX,
				CODES.CODE_VENDORS.code);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Vendors.SUFFIX + "/#",
				CODES.CODE_VENDOR_ITEM.code);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Vendors.SUFFIX + "/"
				+ Vendors.SUFFIX_CATEGORIES, CODES.CODE_VENDOR_CATEGORIES.code);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Budget.SUFFIX,
				CODES.CODE_BUDGET.code);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Budget.SUFFIX + "/#",
				CODES.CODE_BUDGET_ITEM.code);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Budget.SUFFIX + "/"
				+ Budget.SUFFIX_CATEGORIES, CODES.CODE_BUDGET_CATEGORIES.code);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Budget.SUFFIX + "/"
				+ Budget.SUFFIX_SUM_TOTAL_AMOUNT, CODES.CODE_BUDGET_SUM_TOTAL.code);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Budget.SUFFIX + "/"
				+ Budget.SUFFIX_SUM_PAID_AMOUNT, CODES.CODE_BUDGET_SUM_PAID.code);
	}

	/**
	 * Default constructor
	 */
	public DBContentProvider() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// Create db if not exists
		locator = DaoLocator.getInstance(getContext());
		return true;
	}

	/**
	 * Query a cursor (only for load cursor purpose due to backward
	 * compatibility issue as of android 3.0)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 *      java.lang.String[], java.lang.String, java.lang.String[],
	 *      java.lang.String)
	 * @author YCH
	 * @return cursor
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		SQLiteDatabase database = locator.getReadDatabase();
		Cursor cursor = null;
		String query = null;
		StringBuilder buildQuery = null;
		int uriType = URI_MATCHER.match(uri);
		checkColumns(projection, uriType);
		switch (CODES.toEnum(uriType)) {
		case CODE_VENDORS:
			queryBuilder.setTables(TABLE_VENDORS);
			if (WeddingPlannerHelper.isEmpty(sortOrder)) {
				sortOrder = Vendors.SORT_ORDER_DEFAULT;
			}
			cursor = queryBuilder.query(database, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case CODE_VENDOR_ITEM:
			queryBuilder.setTables(TABLE_VENDORS);
			// Select a specific item
			queryBuilder.appendWhere(COL_ID + "=" + uri.getLastPathSegment());
			cursor = queryBuilder.query(database, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case CODE_VENDOR_CATEGORIES:
			projection = new String[] { COL_ID, COL_VENDOR_CATEGORY };
			queryBuilder.setTables(TABLE_VENDORS);
			cursor = queryBuilder.query(database, projection, selection,
					selectionArgs, COL_VENDOR_CATEGORY, null, sortOrder);
			break;
		case CODE_BUDGET:
			queryBuilder.setTables(TABLE_BUDGET);
			if (WeddingPlannerHelper.isEmpty(sortOrder)) {
				sortOrder = Budget.SORT_ORDER_DEFAULT;
			}
			if (projection.length == 0) {
				projection = Budget.PROJECTION_ALL;
			}
			cursor = queryBuilder.query(database, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case CODE_BUDGET_ITEM:
			queryBuilder.setTables(TABLE_BUDGET);
			if (projection.length == 0) {
				projection = Budget.PROJECTION_ALL;
			}
			queryBuilder.appendWhere(COL_ID + "=" + uri.getLastPathSegment());
			cursor = queryBuilder.query(database, projection, selection,
					selectionArgs, null, null, sortOrder);
			break;
		case CODE_BUDGET_CATEGORIES:
			projection = new String[] { COL_ID, COL_BUDGET_CATEGORY };
			queryBuilder.setTables(TABLE_BUDGET);
			cursor = queryBuilder.query(database, projection, selection,
					selectionArgs, COL_BUDGET_CATEGORY, null, sortOrder);
			break;
		case CODE_BUDGET_SUM_TOTAL:
			projection = new String[] {COL_ID, COL_BUDGET_TOTAL_AMOUNT};
			buildQuery = new StringBuilder().append("select sum(")
					.append(COL_BUDGET_TOTAL_AMOUNT)
					.append(") from ")
					.append(TABLE_BUDGET);
			if (null != selectionArgs && selectionArgs.length >= 1) {
				buildQuery.append(" group by ")
					.append(COL_BUDGET_CATEGORY)
					.append(" having ")
					.append(COL_BUDGET_CATEGORY)
					.append(" = ?");
			}
			query = buildQuery.toString();
			cursor = database.rawQuery(query, selectionArgs);
			break;
		case CODE_BUDGET_SUM_PAID:
			projection = new String[] {COL_ID, COL_BUDGET_PAID_AMOUNT};
			queryBuilder.setTables(TABLE_BUDGET);
			buildQuery = new StringBuilder().append("select sum(")
					.append(COL_BUDGET_PAID_AMOUNT)
					.append(") from ")
					.append(TABLE_BUDGET);
			if (null != selectionArgs && selectionArgs.length >= 1) {		
				buildQuery.append(" group by ")
					.append(COL_BUDGET_CATEGORY)
					.append(" having ")
					.append(COL_BUDGET_CATEGORY)
					.append(" = ?");
			}
			query = buildQuery.toString();
			cursor = database.rawQuery(query, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		// make sure that potential listener are notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	/**
	 * Check if columns asked are available
	 * 
	 * @param projection
	 *            name of the columns
	 */
	private void checkColumns(String[] projection, int uriType) {

		// Choose the right set for columns
		List<Integer> vendorCodeList = Arrays.asList( CODES.CODE_VENDORS.code, CODES.CODE_VENDOR_ITEM.code, CODES.CODE_VENDOR_CATEGORIES.code);
		List<Integer> budgetCodeList =Arrays.asList( CODES.CODE_BUDGET.code, CODES.CODE_BUDGET_ITEM.code, CODES.CODE_BUDGET_CATEGORIES.code,
				CODES.CODE_BUDGET_SUM_TOTAL.code,
				CODES.CODE_BUDGET_SUM_PAID.code);
		
		String[] available = null;

		if (vendorCodeList.contains(uriType)) {
			available = Vendors.PROJECTION_ALL;
		}
		else if (budgetCodeList.contains(uriType)) {
			available = Budget.PROJECTION_ALL;
		}
		else {
			throw new IllegalArgumentException("Unknown uri passed in parameter of the function");
		}
		
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		String type = null;
		switch (CODES.toEnum(URI_MATCHER.match(uri))) {
		case CODE_VENDORS:
			type = Vendors.CONTENT_TYPE;
			break;
		case CODE_VENDOR_ITEM:
			type = Vendors.CONTENT_ITEM_TYPE;
			break;
		case CODE_VENDOR_CATEGORIES:
			type = Vendors.CONTENT_TYPE;
			break;
		case CODE_BUDGET:
			type = Budget.CONTENT_TYPE;
			break;
		case CODE_BUDGET_ITEM:
			type = Budget.CONTENT_ITEM_TYPE;
			break;
		case CODE_BUDGET_CATEGORIES:
			type = Budget.CONTENT_TYPE;
			break;
		default:
			throw new IllegalArgumentException(Constantes.ILLEGAL_URI + uri);
		}
		return type;
	}

	/**
	 * insert an item in db based on the uri requested
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 *      android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, String.format("Insert values [%s] in content uri %s", values, uri));
		SQLiteDatabase db = locator.getWritableDatabase();
		long id = -1;
		
		switch (CODES.toEnum(URI_MATCHER.match(uri))) {
		case CODE_VENDORS:
		case CODE_VENDOR_CATEGORIES:
		case CODE_BUDGET_CATEGORIES:
			throw new UnsupportedOperationException("Insertion for these kind of data not yet implemented");
		case CODE_BUDGET:
			id = db.insert(TABLE_BUDGET, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI for insertion " + uri);
			
		}
		
		Uri result = getUriForId(id, uri);
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}
	
	/**
	 * Returns the corresponding uri appended with id of the item
	 * @param id id of the item
	 * @param uri content uri
	 * @return complete uri
	 */
	private Uri getUriForId(long id, Uri uri) {
		Uri itemUri = null;
		
		if (id > 0) {
			itemUri = ContentUris.withAppendedId(uri, id);
		}
		else throw new SQLException("Problem while inserting item into uri " + uri);
		
		return itemUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase db = locator.getWritableDatabase();
		String idStr = uri.getLastPathSegment();
		String where = null;
		switch (CODES.toEnum(URI_MATCHER.match(uri))) {
		case CODE_VENDOR_ITEM:
			Log.v(TAG, "Delete vendor with id " + idStr);
			where = ConstantesDAO.COL_ID + " = " + idStr;
			if (!WeddingPlannerHelper.isEmpty(selection)) {
				where += " AND " + selection;
			}
			count = db
					.delete(ConstantesDAO.TABLE_VENDORS, where, selectionArgs);
			break;
		case CODE_VENDORS:
			throw new UnsupportedOperationException(
					"Delete All vendors not implemented");
		case CODE_BUDGET_ITEM:
			Log.v(TAG, "Delete budget line with id " + idStr);
			where = ConstantesDAO.COL_ID + " = " + idStr;
			if (!WeddingPlannerHelper.isEmpty(selection)) {
				where += " AND " + selection;
			}
			count = db
					.delete(ConstantesDAO.TABLE_BUDGET, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException(Constantes.ILLEGAL_URI);
		}
		
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 * android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase db = locator.getWritableDatabase();
		String idStr = uri.getLastPathSegment();
		String where = null;
		switch (CODES.toEnum(URI_MATCHER.match(uri))) {
		case CODE_BUDGET_ITEM:
			Log.v(TAG, "Update budget line with id " + idStr);
			where = ConstantesDAO.COL_ID + " = " + idStr;
			if (!WeddingPlannerHelper.isEmpty(selection)) {
				where += " AND " + selection;
			}
			count = db.update(TABLE_BUDGET, values, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException(Constantes.ILLEGAL_URI);
		}
		
		if (count > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		
		return count;
	}

}
