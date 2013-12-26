package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.Constantes.FONT_CURVED;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_NAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_SURNAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_INVITATION;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_NAME;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_RSVP;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_SURNAME;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.innovention.weddingplanner.bean.Contact.ResponseType;
import com.innovention.weddingplanner.dao.DaoLocator;
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
public class GuestListFragment extends Fragment implements
		AbsListView.OnItemLongClickListener {
	
	private final static String TAG = GuestListFragment.class.getSimpleName();

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnGuestSelectedListener mListener;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private ListAdapter mAdapter;
	
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
		// TODO: Update argument type and name
		public void onSelectGuest(int id);
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
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	/**
	 * Override SimpleCursorAdapter in order to have a fancier layout by adding icons
	 * to row
	 * @author YCH
	 *
	 */
	private class GuestCursorAdapter extends SimpleCursorAdapter {
		
		private Context context;
		private Cursor c;
		
		private class ViewHolder {
			private ImageView icon;
			private TextView surname;
			private TextView name;
		}

		public GuestCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			this.c = c;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewHolder holder;
			
			// If view does not already exists we create it
			if (row == null) {
				row = View.inflate(context, R.layout.fragment_contact_list, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) row.findViewById(R.id.itemGuestIcon);
				holder.surname = (TextView) row.findViewById(R.id.itemGuestSurname);
				holder.name = (TextView) row.findViewById(R.id.itemGuestName);
				row.setTag(holder);
			}
			
			holder = (ViewHolder) row.getTag();
			c.moveToPosition(position);
			
			ImageView icon = holder.icon;
			TextView surname = holder.surname;
			surname.setTypeface(WeddingPlannerHelper.getFont(GuestListFragment.this.getActivity(), FONT_CURVED));
			TextView name = (TextView) holder.name;
			name.setTypeface(WeddingPlannerHelper.getFont(GuestListFragment.this.getActivity(), FONT_CURVED));
			surname.setText(c.getString(NUM_COL_SURNAME));
			name.setText(c.getString(NUM_COL_NAME));
			
			Boolean invite = Boolean.valueOf(DatabaseHelper.convertIntToBool(c.getInt(NUM_COL_INVITATION)));
			ResponseType rsvp = ResponseType.toEnum(c.getString(NUM_COL_RSVP));
			
			int resId;
			if (!invite.booleanValue()) resId = R.drawable.ic_action_unread;
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
			
			return row;
		}
		
	}

	// TODO: Rename and change types of parameters
	public static GuestListFragment newInstance(String param1, String param2) {
		GuestListFragment fragment = new GuestListFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}
	
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

		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
		
		// Necessary to set the menu visible for fragment
		setHasOptionsMenu(true);
		
		Cursor c = DaoLocator
				.getInstance(getActivity().getApplicationContext())
				.get(SERVICES.GUEST).getCursor();

		// Todo Use a CursorLoader
		mAdapter = new GuestCursorAdapter(getActivity(),
				R.layout.fragment_contact_list, c,
				new String[] { COL_SURNAME, COL_NAME },
				new int[] {R.id.itemGuestSurname, R.id.itemGuestName },1);
		
//		mAdapter = new SimpleCursorAdapter(getActivity(),
//				R.layout.fragment_contact_list, c,
//				new String[] { COL_SURNAME, COL_NAME },
//				new int[] {R.id.itemGuestSurname, R.id.itemGuestName },1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guest, container, false);

		// Set the adapter
		mListView = (AbsListView) view.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

		// Set OnItemClickListener so we can be notified on item clicks
		mListView.setOnItemLongClickListener(this);
		

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		//super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.guest, menu);
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
	
	/**
	 * Triggered when a long click is fired on an list item
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 * @return
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Log.d(TAG, "onItemLongClick - Long click on item " + id);
		
		if ((null == mListener) || (mActionMode != null)) {
			return false;
		}
		
		// Starts the CAB using the ActionMode.Callbakc defined above
		mActionMode = getActivity().startActionMode(mActionModeCallback);
		view.setSelected(true);

//		// Gets selected item
//		Cursor c = (Cursor) mAdapter.getItem(position);
//		c.moveToPosition(position);
//		// Notify the active callbacks interface (the activity, if the
//		// fragment is attached to one) that an item has been selected.
//		mListener.onSelectGuest(c.getInt(NUM_COL_ID));

		return false;
	}

	/**
	 * The default content for this Fragment has a TextView that is shown when
	 * the list is empty. If you would like to change the text, call this method
	 * to supply the text it should use.
	 */
	public void setEmptyText(CharSequence emptyText) {
		View emptyView = mListView.getEmptyView();

		if (emptyText instanceof TextView) {
			((TextView) emptyView).setText(emptyText);
		}
	}

}
