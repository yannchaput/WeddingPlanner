/**
 * 
 */
package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_VENDOR_COMPANY;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.Vendor;
import com.innovention.weddingplanner.contentprovider.DBContentProvider;
import com.innovention.weddingplanner.contentprovider.DBContentProvider.Vendors;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

/**
 * Vendor list fragment class displays the list of vendor's contacts
 * 
 * @author YCH
 * 
 */
public final class VendorListFragment extends Fragment implements
		LoaderCallbacks<Cursor>, Refreshable {

	// log
	private static final String TAG = VendorListFragment.class.getSimpleName();

	// LoaderManager id
	private static final byte LOADER_ID = 0;

	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_VENDORLIST;

	// Ad banner
	private AdView adView;

	// Adapter
	private ExpAdapter expListAdapter;

	// List view
	private ExpandableListView expListView;

	private ActionMode mActionMode = null;

	// Action mode callback
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;

		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.vendor_context_menu, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Log.v(TAG, "ActionMode.Callback - onActionItemClicked");

			boolean returnCode = false;
			// Compute position in the expandable list
			// Flat position
			int flatPosition = expListView.getCheckedItemPosition();
			// Compute packed position
			long packedPosition = expListView
					.getExpandableListPosition(flatPosition);
			int packedGroupPos = ExpandableListView
					.getPackedPositionGroup(packedPosition);
			int packedChildPos = ExpandableListView
					.getPackedPositionChild(packedPosition);
			Log.v(TAG, String.format("Decoding position [group=%d,  child=%d]",
					packedGroupPos, packedChildPos));

			long id = expListAdapter.getChildId(packedGroupPos, packedChildPos);
			Log.v(TAG, "Get child id " + id);
			Uri uri = ContentUris.withAppendedId(Vendors.CONTENT_URI, id);

			// Handle action
			switch (item.getItemId()) {
			case R.id.action_delete_vendor:
				getActivity().getContentResolver().delete(uri, null, null);
				Toast.makeText(getActivity(),
						R.string.delete_vendor_OK_alert_message,
						Toast.LENGTH_SHORT).show();
				returnCode = true;
				break;
			case R.id.action_update_vendor:
				Cursor c = getActivity().getContentResolver().query(uri,
						Vendors.PROJECTION_ALL, null, null, null);
				Vendor vendor = Vendor.Transformer.transform(c);
				c.close();
				WeddingPlannerHelper.replaceFragment(getActivity(),
						FragmentTags.TAG_FGT_UPDATE_VENDOR, vendor);
				returnCode = true;
				break;
			default:
			}
			mode.finish();
			return returnCode;
		}
	};

	/**
	 * Adapter for Expandable list
	 * 
	 * @author YCH
	 * 
	 */
	private final class ExpAdapter extends CursorTreeAdapter {
		private LayoutInflater mInflator;

		public ExpAdapter(Cursor cursor, Context context) {
			super(cursor, context);
			mInflator = LayoutInflater.from(context);
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {
			TextView tvChild = (TextView) view
					.findViewById(R.id.lblVendorListItem);
			tvChild.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_VENDOR_COMPANY)));
		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			TextView tvGrp = (TextView) view
					.findViewById(R.id.lblVendorListHeader);
			tvGrp.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_VENDOR_CATEGORY)));
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			String category = groupCursor.getString(groupCursor
					.getColumnIndex(COL_VENDOR_CATEGORY));
			String[] projection = { COL_ID, COL_VENDOR_COMPANY };
			Cursor c = getActivity().getContentResolver().query(
					DBContentProvider.Vendors.CONTENT_URI, projection,
					COL_VENDOR_CATEGORY + "=?", new String[] { category },
					DBContentProvider.Vendors.SORT_ORDER_DEFAULT);
			return c;
		}

		@Override
		protected View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			View mView = mInflator.inflate(R.layout.fragment_vendor_list_item,
					null);
			TextView tvChild = (TextView) mView
					.findViewById(R.id.lblVendorListItem);
			tvChild.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_VENDOR_COMPANY)));
			return mView;
		}

		@Override
		protected View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			View mView = mInflator.inflate(R.layout.fragment_vendor_list_group,
					null);
			TextView tvGrp = (TextView) mView
					.findViewById(R.id.lblVendorListHeader);
			tvGrp.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_VENDOR_CATEGORY)));
			return mView;
		}

	}

	/**
	 * Default constructor
	 */
	public VendorListFragment() {
		mode = FragmentTags.TAG_FGT_VENDORLIST;
	}

	/**
	 * Default factory method
	 * 
	 * @return VendorListFragement instance
	 */
	public static VendorListFragment newInstance() {
		VendorListFragment fgt = new VendorListFragment();
		fgt.mode = FragmentTags.TAG_FGT_VENDORLIST;
		return fgt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_vendor_list, container,
				false);

		// get the listview
		expListView = (ExpandableListView) view
				.findViewById(R.id.expandableListVendor);
		expListView.setEmptyView(view.findViewById(R.id.empty));
		expListView
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						Log.v(TAG,
								String.format(
										"onChildClick - perform a click on child of expandable list [id=%d, groupPos=%d, childPos=%d",
										id, groupPosition, childPosition));
						if (null == mActionMode) {
							mActionMode = getActivity().startActionMode(
									mActionModeCallback);
							expListView
									.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
							int index = parent
									.getFlatListPosition(ExpandableListView
											.getPackedPositionForChild(
													groupPosition,
													childPosition));
							expListView.setItemChecked(index, true);
						}
						return true;
					}
				});

		fillContent();

		// Necessary to set the menu visible for fragment
		setHasOptionsMenu(true);

		// Load Ad
		adView = new AdView(this.getActivity());
		adView.setAdUnitId(Constantes.AD_ID);
		adView.setAdSize(AdSize.BANNER);
		FrameLayout layoutAd = (FrameLayout) view
				.findViewById(R.id.LayoutVendorAd);
		layoutAd.addView(adView);

		// Initiez une demande générique.
		AdRequest adRequest = WeddingPlannerHelper
				.buildAdvert(Constantes.DEBUG);

		// Chargez l'objet adView avec la demande d'annonce.
		adView.loadAd(adRequest);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adView != null) adView.resume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (adView != null) adView.pause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (adView != null) adView.destroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.vendor, menu);
	}

	/**
	 * Fill content of the list view
	 */
	private void fillContent() {

		getLoaderManager().initLoader(LOADER_ID, null, this);
		expListAdapter = new ExpAdapter(null, getActivity());
		expListView.setAdapter(expListAdapter);
	}

	// LoaderCallbacks interface implementation
	// -----------------------------------------

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { COL_ID, COL_VENDOR_CATEGORY };
		CursorLoader loader = new CursorLoader(getActivity(),
				Uri.withAppendedPath(Vendors.CONTENT_URI,
						Vendors.SUFFIX_CATEGORIES), projection, null, null,
				DBContentProvider.Vendors.SORT_ORDER_CATEGORY);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		expListAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data are not available any more, delete reference
		expListAdapter.changeCursor(null);
	}

	/**
	 * Called when cursor needs refreshing after a change
	 */
	@Override
	public void refresh() {
		getLoaderManager().restartLoader(LOADER_ID, null, this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		mActionMode = null;
	}
}
