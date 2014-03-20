/**
 * 
 */
package com.innovention.weddingplanner.contentprovider;

import static com.innovention.weddingplanner.dao.ConstantesDAO.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;

import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.dao.DaoLocator;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Content provider to access persistent data
 * 
 * @author YCH
 * 
 */
public class DbContentProvider extends ContentProvider {

	// Service locator
	private DaoLocator locator = null;

	private static final int CODE_VENDORS = 10;
	private static final int CODE_VENDOR_ITEM = 20;

	// UriMatcher
	private static final String AUTHORITY = "com.innovention.weddingplanner.contentprovider";
	private static final String BASE_PATH = "data";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vendors";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vendor";
	private static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		uriMatcher.addURI(AUTHORITY, BASE_PATH, CODE_VENDORS);
		uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CODE_VENDOR_ITEM);
	}

	/**
	 * Default constructor
	 */
	public DbContentProvider() {
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
		return false;
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
		int uriType = uriMatcher.match(uri);
		switch (uriType) {
		case CODE_VENDORS:
			queryBuilder.setTables(TABLE_VENDORS);
			break;
		case CODE_VENDOR_ITEM:
			queryBuilder.setTables(TABLE_VENDORS);
			// Select a specific item
			queryBuilder.appendWhere(COL_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = locator.getDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	/**
	 * Check if columns asked are available
	 * @param projection name of the columns
	 */
	private void checkColumns(String[] projection) {
		String[] available = { COL_ID, COL_VENDOR_COMPANY, COL_VENDOR_CONTACT,
				COL_VENDOR_ADDRESS, COL_VENDOR_MAIL, COL_VENDOR_PHONE,
				COL_VENDOR_CATEGORY, COL_VENDOR_NOTE };
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}

}
