/**
 * 
 */
package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_COMPANY;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.contentprovider.DbContentProvider;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Vendor list fragment class
 * Displays the list of vendor's contacts
 * @author YCH
 *
 */
public final class VendorListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	
	// log
	private static final String TAG = VendorListFragment.class.getSimpleName();
	
	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_VENDORLIST;
	
	// Adapter
	private SimpleCursorAdapter adapter;

	/**
	 * Default constructor
	 */
	public VendorListFragment() {
		mode = FragmentTags.TAG_FGT_VENDORLIST;
	}
	
	/**
	 * Default factory method
	 * @return VendorListFragement instance
	 */
	public static VendorListFragment newInstance() {
		VendorListFragment fgt = new VendorListFragment();
		fgt.mode = FragmentTags.TAG_FGT_VENDORLIST;
		return fgt;
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_vendor_list, container, false);
		fillContent();
		return view;
	}
	
	/**
	 * Fill content of the list view
	 */
	private void fillContent() {
		// Fields from the database (projection)
		// Must include the _id column for the adapter to work out
		String[] from = { COL_VENDOR_COMPANY };
		// Fields on the ui to which we map
		int[] to = { R.id.textCompanyVendorList };
		
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_vendor_adapter, null, from, to, 0);
		setListAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { COL_ID, COL_VENDOR_COMPANY };
		CursorLoader loader = new CursorLoader(getActivity(),
				DbContentProvider.CONTENT_URI, projection, null, null, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Attention! Doit on le clore???
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data are not available any more, delete reference
		adapter.swapCursor(null);
	}

}
