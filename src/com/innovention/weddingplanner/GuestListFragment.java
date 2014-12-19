package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_GUEST_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_VENDOR;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_NAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_SURNAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_INVITATION;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_NAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_RSVP;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_SURNAME;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.formatCurrency;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.getDefaultLocale;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.Contact.Category;
import com.innovention.weddingplanner.bean.Contact.ResponseType;
import com.innovention.weddingplanner.contentprovider.DBContentProvider;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.GuestsDao;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.DatabaseHelper;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the
 * ListView with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class GuestListFragment extends Fragment implements Refreshable {

	private final static String TAG = GuestListFragment.class.getSimpleName();

	private OnGuestSelectedListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private ExpandableListView expListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ExpAdapter expListAdapter;
	
	/**
	 * Ad widget
	 */
	private AdView adView;

	/**
	 * Adapter for Expandable list
	 * 
	 * @author YCH
	 * 
	 */
	private final class ExpAdapter extends CursorTreeAdapter {

		private LayoutInflater mInflator;

		public ExpAdapter(Cursor cursor, Context context) {
			super(cursor, context, true);
			mInflator = LayoutInflater.from(context);
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {

			ImageView icon = (ImageView) view.findViewById(R.id.itemGuestIcon);
			TextView surname = (TextView) view
					.findViewById(R.id.itemGuestSurname);
			TextView name = (TextView) view.findViewById(R.id.itemGuestName);
			surname.setText(cursor.getString(NUM_COL_SURNAME));
			name.setText(cursor.getString(NUM_COL_NAME));

			Boolean invite = DatabaseHelper.convertIntToBool(cursor
					.getInt(NUM_COL_INVITATION));
			ResponseType rsvp = ResponseType.toEnum(cursor
					.getString(NUM_COL_RSVP));

			int resId;
			if (!invite.booleanValue())
				resId = R.drawable.ic_action_unread;
			else {
				switch (rsvp) {
				case PENDING:
					resId = R.drawable.ic_action_help;
					break;
				case ATTEND:
					resId = R.drawable.ic_action_good;
					break;
				case NOTATTEND:
					resId = R.drawable.ic_action_bad;
					break;
				default:
					resId = R.drawable.ic_action_help;
				}
			}

			icon.setImageResource(resId);
		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			String category = cursor.getString(cursor
					.getColumnIndex(COL_GUEST_CATEGORY));
			TextView tvGrp = (TextView) view
					.findViewById(R.id.lblGuestListHeader);
			Category eCategory = null;
			try {
				eCategory = Category.valueOf(category);
			} catch (NullPointerException e) {
				eCategory = Category.OTHER;
			}
			switch (eCategory) {
			case FAMILY:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[0]);
				break;
			case FRIEND:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[1]);
				break;
			case COLLEGUE:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[2]);
				break;
			default:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[3]);
			}

		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			String category = groupCursor.getString(groupCursor
					.getColumnIndex(COL_GUEST_CATEGORY));
			Cursor c = DaoLocator
					.getInstance(getActivity().getApplicationContext())
					.get(SERVICES.GUEST)
					.getCursor(COL_GUEST_CATEGORY + "=?",
							new String[] { category },
							COL_NAME + " COLLATE NOCASE ASC");
			return c;

		}

		@Override
		protected View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			View view = mInflator.inflate(R.layout.fragment_guest_list_item,
					null);
			ImageView icon = (ImageView) view.findViewById(R.id.itemGuestIcon);
			TextView surname = (TextView) view
					.findViewById(R.id.itemGuestSurname);
			TextView name = (TextView) view.findViewById(R.id.itemGuestName);
			surname.setText(cursor.getString(NUM_COL_SURNAME));
			name.setText(cursor.getString(NUM_COL_NAME));

			Boolean invite = DatabaseHelper.convertIntToBool(cursor
					.getInt(NUM_COL_INVITATION));
			ResponseType rsvp = ResponseType.toEnum(cursor
					.getString(NUM_COL_RSVP));

			int resId;
			if (!invite.booleanValue())
				resId = R.drawable.ic_action_unread;
			else {
				switch (rsvp) {
				case PENDING:
					resId = R.drawable.ic_action_help;
					break;
				case ATTEND:
					resId = R.drawable.ic_action_good;
					break;
				case NOTATTEND:
					resId = R.drawable.ic_action_bad;
					break;
				default:
					resId = R.drawable.ic_action_help;
				}
			}

			icon.setImageResource(resId);
			return view;
		}

		@Override
		protected View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			View mView = mInflator.inflate(R.layout.fragment_guest_list_group,
					null);
			String category = cursor.getString(cursor
					.getColumnIndex(COL_GUEST_CATEGORY));
			TextView tvGrp = (TextView) mView
					.findViewById(R.id.lblGuestListHeader);
			Category eCategory = null;
			try {
				eCategory = Category.valueOf(category);
			} catch (NullPointerException e) {
				eCategory = Category.OTHER;
			}
			switch (eCategory) {
			case FAMILY:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[0]);
				break;
			case FRIEND:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[1]);
				break;
			case COLLEGUE:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[2]);
				break;
			default:
				tvGrp.setText(getResources().getStringArray(
						R.array.contact_category_list)[3]);
			}
			return mView;
		}

	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnGuestSelectedListener {
		void onSelectGuest(long id, final FragmentTags action);

		void onCallGuest(long id);

		void onMailGuest(long id);
	}

	// Contextual action mode in activity
	private ActionMode mActionMode = null;

	// Action mode callback handler
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
			inflater.inflate(R.menu.guest_context_menu, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
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

			switch (item.getItemId()) {
			case R.id.action_update_contact:
				Log.d(TAG,
						"ActionMode.Callback - onActionItemClicked - edit item with db id "
								+ id);
				mListener.onSelectGuest(id, FragmentTags.TAG_FGT_UPDATECONTACT);
				// Close the CAB
				mode.finish();
				return true;
			case R.id.action_delete_contact:
				Log.d(TAG,
						"ActionMode.Callback - onActionItemClicked - delete item with db id "
								+ id);
				// Notify activity to perform physical delete in db
				mListener.onSelectGuest(id, FragmentTags.TAG_FGT_DELETECONTACT);
				// Refresh list view
				refresh();
				// close the CAB
				mode.finish();
				return true;
			case R.id.action_call_contact:
				mListener.onCallGuest(id);
				mode.finish();
				return true;
			case R.id.action_mail_contact:
				mListener.onMailGuest(id);
				mode.finish();
				return true;
			default:
				return false;
			}
		}
	};

	public static GuestListFragment newInstance() {
		GuestListFragment fragment = new GuestListFragment();
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public GuestListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_guest_list,
				container, false);

		// get the listview
		expListView = (ExpandableListView) rootView
				.findViewById(android.R.id.list);
		expListView.setEmptyView(rootView.findViewById(R.id.empty));
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
		// Gets cursor of list of category
		expListAdapter = new ExpAdapter(null, getActivity());
		expListView.setAdapter(expListAdapter);

		refresh();

		// Load Ad
		adView = new AdView(this.getActivity());
		adView.setAdUnitId(Constantes.AD_ID);
		adView.setAdSize(AdSize.BANNER);
		FrameLayout layoutAd = (FrameLayout) rootView.findViewById(R.id.LayoutGuestListAd);
		layoutAd.addView(adView);

		// Initiez une demande générique.
		AdRequest adRequest = WeddingPlannerHelper.buildAdvert(
				this.getActivity(), Constantes.DEBUG);

		// Chargez l'objet adView avec la demande d'annonce.
		adView.loadAd(adRequest);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnGuestSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGuestSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adView != null)
			adView.resume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (adView != null)
			adView.pause();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}


	/**
	 * Refreshes the content of the list when invoked
	 */
	@Override
	public void refresh() {
		// Refresh list view
		Cursor c = ((GuestsDao) DaoLocator.getInstance(
				getActivity().getApplicationContext()).get(SERVICES.GUEST))
				.getCursorCategory();
		ExpAdapter adapter = (ExpAdapter) expListAdapter;
		adapter.changeCursor(c);
		adapter.notifyDataSetChanged();
	}

}
