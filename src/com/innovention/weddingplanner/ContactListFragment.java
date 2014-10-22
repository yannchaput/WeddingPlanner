/**
 * 
 */
package com.innovention.weddingplanner;

import java.util.Iterator;
import java.util.LinkedList;

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

	/**
	 * ListView cursorAdapter
	 */
	private CursorAdapter mAdapter;

	/**
	 * List of db ids whose checkbox was checked Use a sparsearray for better
	 * performance while walking through the screen
	 */
	private SparseArray<Mapping> mListContacts;

	// columns requested from the database
	private static final String[] PROJECTION = { Contacts._ID, // _ID is always
																// required
			Contacts.LOOKUP_KEY, Contacts.DISPLAY_NAME_PRIMARY // that's what we
																// want to
																// display
	};
	
	/**
	 * Utility class for ContactListCursorAdapter. Represents the mapping between one row of the 
	 * list and what is saved in cache
	 * @author ychaput
	 *
	 */
	private class Mapping {
		private long _id;
		private String _lookupKey;
		private boolean _checked;
		
		private Mapping() {
			_checked=false;
		}
		
		private Mapping(long id, String lookupKey) {
			_id = id;
			_lookupKey = lookupKey;
			_checked = false;
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
		getLoaderManager().initLoader(0, null, this);
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
			importContacts();
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
	private void importContacts() {

		// Log.i(TAG, "Import and save contacts from address book");
		// // Transform into list so as to use CollectionUtils features
		// // Filter on contacts marked as to import
		// LinkedList<String> listIds = new LinkedList<String>();
		// for (int i=0; i < mListContactIds.size(); i++) {
		// int key = mListContactIds.keyAt(i);
		// boolean value = mListContactIds.valueAt(i);
		// // If contact is marked to import
		// if (value) {
		// listIds.add(String.valueOf(key));
		// }
		// }
		// // First iterate over the contact
		// for (String id : listIds) {
		// // Query db
		// String[] fields = {Contacts._ID, Contacts.LOOKUP_KEY,
		// Contacts.DISPLAY_NAME_PRIMARY, Contacts.HAS_PHONE_NUMBER};
		// String whereContact = Contacts._ID + "= ?";
		// String[] whereContactParams = new String[] {id};
		// Cursor c =
		// getActivity().getContentResolver().query(Contacts.CONTENT_URI,
		// fields, whereContact, whereContactParams, null);
		// for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
		// String displayName = c.getString(c.getColumnIndex(fields[2]));
		// Log.v(TAG, "Get contact " + displayName);
		// // Query name and surname
		// fields = new String[]
		// {ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
		// ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME};
		// whereContact = Data.MIMETYPE + "= ? AND " + Data.CONTACT_ID + "= ?";
		// whereContactParams = new String[]
		// {ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
		// id};
		// Cursor c2 =
		// getActivity().getContentResolver().query(Data.CONTENT_URI, fields,
		// whereContact, whereContactParams, null);
		// for (c2.moveToFirst();!c2.isAfterLast(); c2.moveToNext()) {
		// String name = c2.getString(c2.getColumnIndex(fields[0]));
		// Log.v(TAG, "Name: " + name);
		// String surname = c2.getString(c2.getColumnIndex(fields[1]));
		// Log.v(TAG, "Surname: " + surname);
		// }
		// }
		// c.close();
		// }
	}

	// -----------------------------------------------------------------------------------------
	// -- Implémentation LoaderCallbacks
	// -----------------------------------------------------------------------------------------

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		// load from the "Contacts table"
		Uri contentUri = Contacts.CONTENT_URI;

		// no sub-selection, no sort order, simply every row
		// projection says we want just the _id and the name column
		return new CursorLoader(getActivity(), contentUri, PROJECTION, null,
				null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Once cursor is loaded, give it to adapter
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// on reset take any old cursor away
		mAdapter.swapCursor(null);
	}
}