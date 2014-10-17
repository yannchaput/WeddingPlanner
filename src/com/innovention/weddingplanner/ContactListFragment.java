/**
 * 
 */
package com.innovention.weddingplanner;

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
	 * List of db ids whose checkbox was checked
	 * Use a sparsearray for better performance while walking through the screen
	 */
	private SparseBooleanArray mListContactIds;
	
	/**
	 * ContactListCursorAdapter is the adapter layout for a list of contact from
	 * the android device
	 * @author ychaput
	 *
	 */
	private class ContactListCursorAdapter extends CursorAdapter {
		
		private LayoutInflater mInflater;
		
		/**
		 * Should be stateless and contain only reference to the checkbox view.
		 * Content and state of the checkbox should be discarded in bindView and replace with actual data
		 * @author ychaput
		 *
		 */
		private class ViewHolder {
			// Views
			private CheckBox check;
		}
		
		public ContactListCursorAdapter(final Context ctx, final Cursor c, int flags) {
			super(ctx,c,flags);
			mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/* (non-Javadoc)
		 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.fragment_contact_adapter, parent, false);
			CheckBox ck = (CheckBox) view.findViewById(R.id.checkboxContactList);
			ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Integer id = (Integer) buttonView.getTag();
					Log.d(TAG, "Click on checkbox item whose id is " + id);
					// Map the value of the check to this id value in db
					// Will replace the previous entry if exists
					mListContactIds.put(id, isChecked);
				}
			});
			
			ViewHolder holder = new ViewHolder();
			holder.check = ck;
			view.setTag(holder);
			
			return view;
		}

		/* (non-Javadoc)
		 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			
			ViewHolder holder = (ViewHolder) view.getTag();
			CheckBox ckView = holder.check;
			// Set checkbox caption
			String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
			ckView.setText(name);
			// Save db id as a tag of the checkbox
			int id = cursor.getInt(cursor.getColumnIndex(Contacts._ID));
			ckView.setTag(id);
			// Set the check mark
			ckView.setChecked(mListContactIds.get(id));
		}
		
	}

	
	public ContactListFragment() {
		mListContactIds = new SparseBooleanArray(30);
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

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.import_contact_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
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
	 * Import contacts checked in the ListView
	 * and transforms them into Contacts in a view to persisting them in db
	 */
	private void importContacts() {
		Log.i(TAG, "Import and save contacts from address book");
		// Transform into list so as to use CollectionUtils features
		LinkedList<String> listIds = new LinkedList<String>();
		for (int i=0; i < mListContactIds.size(); i++) {
			int key = mListContactIds.keyAt(i);
			boolean value = mListContactIds.valueAt(i);
			// If contact is marked to import
			if (value) {
				listIds.add(String.valueOf(key));
			}
		}
		// Build query
		StringBuilder clauseWhere = new StringBuilder()
		.append(Data.CONTACT_ID)
		.append(" IN (")
		.append(StringUtils.join(listIds, ','))
		.append(")");
		Log.v(TAG, "Clause where :" + clauseWhere);
		// Query db
		final String[] fields = {ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME};
		Cursor c = getActivity().getContentResolver().query(Data.CONTENT_URI, fields, clauseWhere.toString(), null, null);
		// Transform into Contacts
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
			Log.v(TAG, "Name: " + name);
			String surname = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
			Log.v(TAG, "Surname: " + surname);
		}
		c.close();
	}


	// -----------------------------------------------------------------------------------------
	// -- Implémentation LoaderCallbacks
	// -----------------------------------------------------------------------------------------
	
	// columns requested from the database
	private static final String[] PROJECTION = { Contacts._ID, // _ID is always
																	// required
				Contacts.DISPLAY_NAME_PRIMARY // that's what we want to display
		};
		
		
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