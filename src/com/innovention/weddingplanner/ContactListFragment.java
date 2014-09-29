/**
 * 
 */
package com.innovention.weddingplanner;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

/**
 * Fragment which displays a list of contacts stored in the cloud/phone so as to
 * select them
 * 
 * @author ychaput
 * 
 */
public class ContactListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	/**
	 * ListView cursorAdapter
	 */
	private CursorAdapter mAdapter;


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
		int layout = android.R.layout.simple_list_item_1;
		Cursor c = null; // there is no cursor yet
		int flags = 0; // no auto-requery! Loader requeries.
		mAdapter = new SimpleCursorAdapter(context, layout, c, FROM, TO, flags);
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

		return v;
	}

	// columns requested from the database
	private static final String[] PROJECTION = { Contacts._ID, // _ID is always
																// required
			Contacts.DISPLAY_NAME_PRIMARY // that's what we want to display
	};

	// and name should be displayed in the text1 textview in item layout
	private static final String[] FROM = { Contacts.DISPLAY_NAME_PRIMARY };
	private static final int[] TO = { android.R.id.text1 };

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