/**
 * 
 */
package com.innovention.weddingplanner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;

/**
 * Fragment which displays a list of contacts stored in the cloud/phone so as to
 * select them
 * 
 * @author ychaput
 * 
 */
public class ContactListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	// LOG
	private static final String TAG = ContactListFragment.class.getSimpleName();
	// LoaderCallback ids
	private static final int QUERY_LIST_ID = 1;
	private static final int QUERY_DETAIL_ID = 2;

	/**
	 * ListView cursorAdapter
	 */
	private CursorAdapter mAdapter;

	/**
	 * List of db ids whose checkbox was checked Use a sparsearray for better
	 * performance while walking through the screen
	 */
	private SparseArray<Mapping> mListContacts;

	// columns requested from the database for query list
	private static final String[] PROJECTION_QUERY_LIST = { Contacts._ID, // _ID is always
																// required
			Contacts.LOOKUP_KEY, Contacts.DISPLAY_NAME_PRIMARY // that's what we
																// want to
																// display
	};
	// columns requested from the database for query contact details
	private static final String[] PROJECTION_QUERY_DETAILS = { StructuredName.GIVEN_NAME,
		StructuredName.FAMILY_NAME, Email.ADDRESS
	};
	
	private String mSelectionDetail;
	/**
	 * Where clause arguments for contact details
	 */
	private String[] mSelectionDetailArgs;
	/**
	 * Utility class for ContactListCursorAdapter. Represents the mapping between one row of the 
	 * list and what is saved in cache
	 * @author ychaput
	 *
	 */
	private class Mapping {
		private long _id = -1;
		private String _lookupKey;
		private boolean _checked = false;
		
		private Mapping() {
		}
		
		private Mapping(long id, String lookupKey) {
			_id = id;
			_lookupKey = lookupKey;
		}
		
		private Mapping(long id, String lookupKey, boolean checked) {
			this(id, lookupKey);
			_checked = checked;
		}
	}


	/**
	 * ContactListCursorAdapter is the adapter layout for a list of contact from
	 * the android device
	 * 
	 * @author ychaput
	 * 
	 */
	private class ContactListCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;
		

		public ContactListCursorAdapter(final Context ctx, final Cursor c,
				int flags) {
			super(ctx, c, flags);
			mInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.CursorAdapter#newView(android.content.Context,
		 * android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.fragment_contact_adapter,
					parent, false);
			CheckBox ck = (CheckBox) view
					.findViewById(R.id.checkboxContactList);
			ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Mapping bean = (Mapping) buttonView.getTag();
					Log.d(TAG, "Click on checkbox item whose id is " + bean._id);
					bean._checked = isChecked;
					// Map the value of the check to this id value in db
					// Will replace the previous entry if exists
					mListContacts.put((int) bean._id, bean);
				}
			});


			return view;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.CursorAdapter#bindView(android.view.View,
		 * android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			CheckBox ckView = (CheckBox) view.findViewById(R.id.checkboxContactList);
			// Set checkbox caption
			String name = cursor.getString(cursor
					.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
			ckView.setText(name);
			// Save db id as a tag of the checkbox
			long id = cursor.getLong(cursor.getColumnIndex(Contacts._ID));
			String lookupKey = cursor.getString(cursor.getColumnIndex(Contacts.LOOKUP_KEY));
			Mapping cachedBean = mListContacts.get((int) id);
			if (cachedBean == null) {
				cachedBean = new Mapping(id,lookupKey,false);
			}
			// We store the cache object temporarily as a tag
			// in order to retrieve it and save in the cache is selected
			ckView.setTag(cachedBean);
			// Set the check mark
			ckView.setChecked(cachedBean._checked );
		}

	}

	public ContactListFragment() {
		mListContacts = new SparseArray<Mapping>(30);
	}

	/**
	 * Factory method
	 * 
	 * @return
	 */
	public static ContactListFragment newInstance() {
		return new ContactListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create adapter once
		Context context = getActivity();
		Cursor c = null; // there is no cursor yet
		int flags = 0; // no auto-requery! Loader requeries.
		mAdapter = new ContactListCursorAdapter(context, c, flags);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// each time we are started use our listadapter
		setListAdapter(mAdapter);
		// and tell loader manager to start loading
		getLoaderManager().initLoader(QUERY_LIST_ID, null, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_contact_list, container,
				false);
		// Necessary to set the menu visible for fragment
		setHasOptionsMenu(true);

		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.import_contact_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_validate_import_contact:
			Log.d(TAG, "Click on validate import button");
			startImportContacts();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	/**
	 * Import contacts checked in the ListView and transforms them into Contacts
	 * in a view to persisting them in db
	 */
	private void startImportContacts() {

		Log.i(TAG, "Import and save contacts from address book");
		
		Mapping bean = null;
		//mSelectionDetailArgs = new String[mListContacts.size()+1];
		ArrayList<String> listWhereArgs = new ArrayList<String>();
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(Data.LOOKUP_KEY).append(" IN (");
		int i =0;
		for (; i < mListContacts.size(); i++) {
			bean = mListContacts.valueAt(i);
			if (bean != null && bean._checked && bean._id != -1
					&& !StringUtils.isEmpty(bean._lookupKey)) {
					whereClause.append("?,");
				//mSelectionDetailArgs[i] = bean._lookupKey;
				listWhereArgs.add(bean._lookupKey);
			}
		}
		whereClause.deleteCharAt(whereClause.length()-1);
		whereClause.append(")");
		whereClause.append(" AND ").append(Data.MIMETYPE).append(" = ?");
		mSelectionDetail = whereClause.toString();
		//mSelectionDetailArgs[i] = StructuredName.CONTENT_ITEM_TYPE;
		listWhereArgs.add(StructuredName.CONTENT_ITEM_TYPE);
		mSelectionDetailArgs = listWhereArgs.toArray(new String[listWhereArgs.size()]);
		Log.v(TAG, "Where clause = " + mSelectionDetail);
		Log.v(TAG, "Lookup keys: " + ArrayUtils.toString(mSelectionDetailArgs));
		// Get contact details
		getLoaderManager().restartLoader(QUERY_DETAIL_ID, null, this);
		// Results are received in onLoadReset method
	}
	
	private void processImportContacts(final Cursor data) {
		
		String name = "";
		String surname = "";
		String email = "";
		Log.v(TAG, "Get a cursor of contacts with size " + data.getCount());
		for (data.moveToFirst();!data.isAfterLast();data.moveToNext()) {
			Log.i(TAG, "Process contact:");
			name = data.getString(data.getColumnIndex(StructuredName.GIVEN_NAME));
			Log.i(TAG, "Name : " + name);
			surname = data.getString(data.getColumnIndex(StructuredName.FAMILY_NAME));
			Log.i(TAG, "Name : " + surname);
			email = data.getString(data.getColumnIndex(Email.ADDRESS));
			Log.i(TAG, "EMail : " + email);
		}
	}

	// -----------------------------------------------------------------------------------------
	// -- Implémentation LoaderCallbacks
	// -----------------------------------------------------------------------------------------

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		// load from the "Contacts table"
		Uri contentUri = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		switch (id) {
		case QUERY_LIST_ID:
		default:
			contentUri = Contacts.CONTENT_URI;
			projection = PROJECTION_QUERY_LIST;
			break;
		case QUERY_DETAIL_ID:
			contentUri = Data.CONTENT_URI;
			projection = PROJECTION_QUERY_DETAILS;
			selection = mSelectionDetail;
			selectionArgs = mSelectionDetailArgs;
		}

		// no sub-selection, no sort order, simply every row
		// projection says we want just the _id and the name column
		return new CursorLoader(getActivity(), contentUri, projection, selection,
				selectionArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case QUERY_LIST_ID:
		default:
			// Once cursor is loaded, give it to adapter
			mAdapter.swapCursor(data);
			break;
		case QUERY_DETAIL_ID:
			Log.v(TAG, "Get loader result for query contact details");
			processImportContacts(data);
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case QUERY_LIST_ID:
		default:
			// on reset take any old cursor away
			mAdapter.swapCursor(null);
			break;
		case QUERY_DETAIL_ID:
		}
	}
}