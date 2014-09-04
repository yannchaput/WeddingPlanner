package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_DESC;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_STATUS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DESC;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_REMINDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_STATUS;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.DatabaseHelper;
import com.innovention.weddingplanner.dao.TasksDao;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

public class TaskListFragment extends Fragment implements
		AbsListView.OnItemLongClickListener, Refreshable {

	private static final String TAG = TaskListFragment.class.getSimpleName();
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * The ad
	 */
	private AdView adView;
	/**
	 * The list adapter
	 */
	private TaskCursorAdapter mAdapter;

	// Listener on "select" action
	private OnTaskSelectedListener mListener = null;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

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
			inflater.inflate(R.menu.task_context_menu, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			long id = mAdapter.currentSelectionId;

			switch (item.getItemId()) {
			case R.id.action_update_task:
				Log.d(TAG,
						"ActionMode.Callback - onActionItemClicked - edit item with db id "
								+ id);
				if (mListener != null) {
					mListener.onSelectTask(id, FragmentTags.TAG_FGT_UPDATETASK);
				}
				// Close the CAB
				mode.finish();
				return true;
			case R.id.action_delete_task:
				Log.d(TAG,
						"ActionMode.Callback - onActionItemClicked - delete item with db id "
								+ id);
				if (mListener != null) {
					mListener.onSelectTask(id, FragmentTags.TAG_FGT_DELETETASK);
				}
				// refreshes list
				refresh();
				// close the CAB
				mode.finish();
				return true;
			default:
				return false;
			}
		}
	};

	/**
	 * Triggered when a task is selected by a long click
	 * 
	 * @author YCH
	 * 
	 */
	interface OnTaskSelectedListener {
		void onSelectTask(long id, final FragmentTags action);
	}

	interface OnTaskCheckListener {
		void onCheckTask(int id, boolean isChecked);
	}

	/**
	 * Override SimpleCursorAdapter in order to have a stronger control on this
	 * display to row
	 * 
	 * @author YCH
	 * 
	 */
	private class TaskCursorAdapter extends SimpleCursorAdapter {

		private Context context;
		// Saves current item on a long click action
		private int currentSelectionPos = -1;
		private long currentSelectionId = -1;
		// Saves the mapping id <-> status in a list ordered by the position in
		// the list view
		private ArrayList<Mapping> mappingList = new ArrayList<Mapping>();

		/**
		 * Internal holder object Holds a reference to a "row" view of the list
		 * Be careful the row view is reused every time the viewport changed so
		 * the view holder should be stateless
		 * 
		 * @author YCH
		 * 
		 */
		private class ViewHolder {
			// Views
			private CheckBox check;
			private ImageView alarmIcon;
			private TextView remindDateTxt;
			private TextView dueDateTxt;
		}

		/**
		 * Holds element in the arraylist with the id of the task and its state
		 * (checked/uncheked)
		 * 
		 * @author YCH
		 * 
		 */
		private class Mapping {
			private int id;
			private boolean checked;
		}

		public TaskCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			this.context = context;

			// init array of checkbox status
			Mapping bean = null;
			while (c.moveToNext()) {
				int pos = c.getPosition();
				bean = new Mapping();
				bean.id = c.getInt(NUM_COL_ID);
				bean.checked = !DatabaseHelper.convertIntToBool(c
						.getInt(NUM_COL_TASK_STATUS));
				mappingList.add(bean);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewHolder holder;

			/**
			 * Local class overriding the behavior of checkboxes
			 * 
			 * @author YCH
			 * 
			 */
			class OnCheckedChangeListener implements
					CompoundButton.OnCheckedChangeListener {

				// Holds reference of views inside the row view
				private ViewHolder view;

				/**
				 * Constructor
				 * 
				 * @param holder
				 */
				private OnCheckedChangeListener(final ViewHolder holder) {
					view = holder;
				}

				/**
				 * Triggered on a change of the state of the checkbox
				 */
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {

					int position = (Integer) buttonView.getTag();
					mappingList.get(position).checked = isChecked;
					displayCheck(isChecked, view, view.dueDateTxt.getText()
							.toString());
				}
			}

			// If view does not already exists we create it
			if (row == null) {
				row = View.inflate(context, R.layout.fragment_task_adapter,
						null);
				holder = new ViewHolder();
				holder.check = (CheckBox) row
						.findViewById(R.id.checkboxTaskList);
				// Listener on check box
				holder.check
						.setOnCheckedChangeListener(new OnCheckedChangeListener(
								holder));
				holder.alarmIcon = (ImageView) row
						.findViewById(R.id.iconAlarmTaskList);
				holder.remindDateTxt = (TextView) row
						.findViewById(R.id.textReminderTaskList);
				holder.dueDateTxt = (TextView) row
						.findViewById(R.id.textDueDateTaskHidden);
				row.setTag(holder);
			}

			holder = (ViewHolder) row.getTag();
			Cursor c = getCursor();
			c.moveToPosition(position);

			CheckBox check = holder.check;
			ImageView icon = holder.alarmIcon;
			TextView dateTxt = holder.remindDateTxt;
			TextView dueDateTxt = holder.dueDateTxt;

			String remindDate = c.getString(NUM_COL_TASK_REMINDDATE);
			String description = c.getString(NUM_COL_TASK_DESC);
			String expiryTxt = c.getString(NUM_COL_TASK_DUEDATE);

			// Saves the id of the item in a hidden field for future reference
			dueDateTxt.setText(expiryTxt);
			check.setText(description);

			// Set visibility of reminder
			if (!WeddingPlannerHelper.isEmpty(remindDate)) {
				icon.setVisibility(View.VISIBLE);
				dateTxt.setVisibility(View.VISIBLE);
				DateTime parsedDate = DateTime.parse(remindDate);
				dateTxt.setText(DateTimeFormat.mediumDate().print(parsedDate));
			} else {
				icon.setVisibility(View.GONE);
				dateTxt.setVisibility(View.GONE);
			}

			// Handle check/uncheck mechanism
			displayCheck(mappingList.get(position).checked, holder, expiryTxt);

			// Saves position of the current view to apply the right check
			check.setTag(position);
			// Sets the state of the checkbox according to array
			check.setChecked(mappingList.get(position).checked);

			return row;
		}

		/**
		 * Manage shape and color of the checkboxes according to its state and
		 * other field values
		 * 
		 * @param check
		 * @param holder
		 */
		private void displayCheck(boolean check, final ViewHolder holder,
				String dueDate) {

			int color = getResources().getColor(R.color.Black);
			int iconDrawable = R.drawable.ic_alarm_icon;

			CheckBox checkbox = holder.check;
			TextView reminderTxt = holder.remindDateTxt;
			ImageView icon = holder.alarmIcon;

			if (check) {
				// Strike through text
				checkbox.setPaintFlags(checkbox.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				reminderTxt.setPaintFlags(reminderTxt.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				color = getResources().getColor(R.color.DarkGray);
				iconDrawable = R.drawable.ic_alarm_icon;
				checkbox.setTextColor(color);
				reminderTxt.setTextColor(color);
				icon.setImageResource(iconDrawable);
			}
			// Task not completed => normal behavior
			else {
				checkbox.setPaintFlags(checkbox.getPaintFlags()
						& ~Paint.STRIKE_THRU_TEXT_FLAG);
				reminderTxt.setPaintFlags(reminderTxt.getPaintFlags()
						& ~Paint.STRIKE_THRU_TEXT_FLAG);
				// Reestablish the former view state
				setColorAndIcon(holder, dueDate);
			}
		}

		/**
		 * Sets color of a row according to its state
		 * 
		 * @param holder
		 * @param dueDate
		 */
		private void setColorAndIcon(final ViewHolder holder, String dueDate) {
			// Set color according to expiration date
			int color = getResources().getColor(R.color.Black);
			int imageRes = R.drawable.ic_alarm_icon;

			if (!WeddingPlannerHelper.isEmpty(dueDate)) {
				DateTime today = DateTime.now();
				DateTime expiry = DateTime.parse(dueDate);

				// Due date is overdue
				if (expiry.compareTo(today) < 0) {
					color = getResources().getColor(R.color.Crimson);
					imageRes = R.drawable.ic_alarm_icon_red;
				}
			}

			holder.alarmIcon.setImageResource(imageRes);
			holder.check.setTextColor(color);
			holder.remindDateTxt.setTextColor(color);
		}

	}

	public TaskListFragment() {
		// TODO Auto-generated constructor stub
	}

	public static TaskListFragment newInstance() {
		return new TaskListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "onCreate");

		int dropdownItem = 0;
		Cursor c;
		if (getArguments() != null) {
			dropdownItem = getArguments().getInt(ARG_SECTION_NUMBER);
		}

		switch (dropdownItem) {
		// Total list
		case 0:
			c = ((TasksDao) DaoLocator.getInstance(
					getActivity().getApplicationContext()).get(SERVICES.TASK))
					.getCursor();
			break;
		// Tasks in progress
		case 1:
			c = ((TasksDao) DaoLocator.getInstance(
					getActivity().getApplicationContext()).get(SERVICES.TASK))
					.getCursor(COL_TASK_STATUS + "=1", null);
			break;
		// completed tasks
		case 2:
			c = ((TasksDao) DaoLocator.getInstance(
					getActivity().getApplicationContext()).get(SERVICES.TASK))
					.getCursor(COL_TASK_STATUS + "<>1", null);
			break;
		// Tasks overdue
		case 3:
			StringBuilder query = new StringBuilder();
			query.append("date(").append(COL_TASK_DUEDATE).append(")")
					.append("<= date('now') AND ");
			query.append(COL_TASK_DUEDATE).append("<> '' AND ");
			query.append(COL_TASK_STATUS).append("=1");
			c = ((TasksDao) DaoLocator.getInstance(
					getActivity().getApplicationContext()).get(SERVICES.TASK))
					.getCursor(query.toString(), null);
			break;
		default:
			c = ((TasksDao) DaoLocator.getInstance(
					getActivity().getApplicationContext()).get(SERVICES.TASK))
					.getCursor();
		}

		// TODO update to CursorLoader
		getActivity().startManagingCursor(c);
		// TODO Use a CursorLoader
		mAdapter = new TaskCursorAdapter(getActivity(),
				R.layout.fragment_task_adapter, c,
				new String[] { COL_TASK_DESC },
				new int[] { R.id.checkboxTaskList }, 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_task_list,
				container, false);

		// Necessary to set the menu visible for fragment
		setHasOptionsMenu(true);

		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set the adapter
		mListView = (AbsListView) rootView.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setOnItemLongClickListener(this);
		// Set the empty view
		mListView.setEmptyView(rootView.findViewById(R.id.empty));

		// Load Ad
		adView = new AdView(this.getActivity());
		adView.setAdUnitId(Constantes.AD_ID);
		adView.setAdSize(AdSize.BANNER);
		FrameLayout layoutAd = (FrameLayout) rootView.findViewById(R.id.LayoutTaskAd);
		layoutAd.addView(adView);

		// Initiez une demande générique.
		AdRequest adRequest = WeddingPlannerHelper.buildAdvert(this.getActivity(),Constantes.DEBUG);

		// Chargez l'objet adView avec la demande d'annonce.
		adView.loadAd(adRequest);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		// super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.task, menu);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Log.v(TAG,
				String.format(
						"onItemLongClick - perform long click on item of the list [id=%d, pos=%d]",
						id, position));

		if ((null != mListener) && (mActionMode != null)) {
			return false;
		}

		// Starts the CAB using the ActionMode.Callbakc defined above
		mActionMode = getActivity().startActionMode(mActionModeCallback);
		mListView.setItemChecked(position, true);
		((TaskCursorAdapter) mAdapter).currentSelectionPos = position;
		((TaskCursorAdapter) mAdapter).currentSelectionId = id;
		view.setSelected(true);

		return true;
	}

	/**
	 * Refresh the adapter with an updated cursor
	 */
	@Override
	public void refresh() {
		Log.v(TAG, "Refresh list");
		// refreshes list
		Cursor c = (DaoLocator.getInstance(getActivity().getApplication())
				.get(SERVICES.TASK)).getCursor();
		mAdapter.changeCursor(c);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnTaskSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTaskSelectedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		mActionMode = null;
	}

	/**
	 * Called when the fragment is replaced or no more visible Save the item if
	 * it was checked as "completed"
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (adView != null) adView.pause();
		Log.d(TAG, "onPause - save all modified items of the list");
		TasksDao service = DaoLocator.getInstance(
				getActivity().getApplicationContext()).get(SERVICES.TASK);
		Task task = null;
		for (TaskCursorAdapter.Mapping elt : mAdapter.mappingList) {
			Log.v(TAG, "Item " + elt.id + " is saved with a check "
					+ elt.checked);
			task = service.get(elt.id);
			task.setActive(!elt.checked);
			service.update(elt.id, task);
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adView != null) adView.resume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (adView != null) adView.destroy();
		super.onDestroy();
	}
}
