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

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
public final class VendorContentProvider extends ContentProvider {
	
	// Log
	private static final String TAG = VendorContentProvider.class.getSimpleName();

	// Service locator
	private DaoLocator locator;
	// Authority
	private static final String AUTHORITY = "com.innovention.weddingplanner.contentprovider.VendorContentProvider";
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
				VendorContentProvider.CONTENT_URI, SUFFIX);
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

	// Return codes
	private static final int CODE_VENDORS = 10;
	private static final int CODE_VENDOR_ITEM = 20;
	private static final int CODE_VENDOR_CATEGORIES = 30;

	// URI matcher
	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Vendors.SUFFIX,
				CODE_VENDORS);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Vendors.SUFFIX + "/#",
				CODE_VENDOR_ITEM);
		URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/" + Vendors.SUFFIX + "/"
				+ Vendors.SUFFIX_CATEGORIES, CODE_VENDOR_CATEGORIES);
	}

	/**
	 * Default constructor
	 */
	public VendorContentProvider() {
		// TODO Auto-generated constructor stub
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
		checkColumns(projection);
		SQLiteDatabase database = locator.getDatabase();
		Cursor cursor = null;
		int uriType = URI_MATCHER.match(uri);
		switch (uriType) {
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
	private void checkColumns(String[] projection) {
		String[] available = Vendors.PROJECTION_ALL;
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
		switch (URI_MATCHER.match(uri)) {
		case CODE_VENDORS:
			type = Vendors.CONTENT_TYPE;
			break;
		case CODE_VENDOR_ITEM:
			type = Vendors.CONTENT_ITEM_TYPE;
			break;
		case CODE_VENDOR_CATEGORIES:
			type = Vendors.CONTENT_TYPE;
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
		// TODO Auto-generated method stub
		return null;
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
		SQLiteDatabase db = locator.getDatabase();
		switch (URI_MATCHER.match(uri)) {
		case CODE_VENDOR_ITEM:
			String idStr = uri.getLastPathSegment();
			Log.v(TAG, "Delete vendor with id " + idStr);
			String where = ConstantesDAO.COL_ID + " = " + idStr;
			if (!WeddingPlannerHelper.isEmpty(selection)) {
				where += " AND " + selection;
			}
			count = db
					.delete(ConstantesDAO.TABLE_VENDORS, where, selectionArgs);
			break;
		case CODE_VENDORS:
			throw new UnsupportedOperationException(
					"Delete All vendors not implemented");
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
		return count;
	}

}
