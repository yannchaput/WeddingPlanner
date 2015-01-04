package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_PERIOD;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_STATUS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DESC;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_REMINDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_STATUS;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.isEmpty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
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
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.internal.ep;
import com.google.common.base.Objects;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.bean.Task.Period;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DatabaseHelper;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
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
	private ExpAdapter mAdapter;

	// Listener on "select" action
	private OnTaskSelectedListener mListener = null;

	/**
	 * The fragment's ListView/GridView.
	 */
	private ExpandableListView mListView;

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

			// Compute position in the expandable list
			// Flat position
			int flatPosition = mListView.getCheckedItemPosition();
			// Compute packed position
			long packedPosition = mListView
					.getExpandableListPosition(flatPosition);
			int packedGroupPos = ExpandableListView
					.getPackedPositionGroup(packedPosition);
			int packedChildPos = ExpandableListView
					.getPackedPositionChild(packedPosition);
			Log.v(TAG, String.format("Decoding position [group=%d,  child=%d]",
					packedGroupPos, packedChildPos));

			long id = mAdapter.getChildId(packedGroupPos, packedChildPos);
			Log.v(TAG, "Get child id " + id);

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
	 * Adapter for Expandable list
	 * 
	 * @author YCH
	 * 
	 */
	private final class ExpAdapter extends CursorTreeAdapter {

		private LayoutInflater mInflator;
		// Saves the mapping id <-> status in a list ordered by the position in
		// the list view
		private SparseArray<Mapping> mappingList = new SparseArray<Mapping>();

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

		/**
		 * Local class overriding the behavior of checkboxes
		 * 
		 * @author YCH
		 * 
		 */
		class OnCheckedChangeListener implements
				android.widget.CompoundButton.OnCheckedChangeListener {

			private View view;

			/**
			 * Constructor
			 * 
			 * @param holder
			 */
			private OnCheckedChangeListener(final View view) {
				this.view = view;
			}

			/**
			 * Triggered on a change of the state of the checkbox
			 */
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				TextView txtHidden = (TextView) view
						.findViewById(R.id.textDueDateTaskHidden);
				displayCheck(isChecked, view, txtHidden.getText().toString());
				int id = (Integer) buttonView.getTag();
				mappingList.get(id).checked = isChecked;
			}
		}

		public ExpAdapter(Cursor cursor, Context context) {
			super(cursor, context, true);
			mInflator = LayoutInflater.from(context);
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {

			CheckBox ck = (CheckBox) view.findViewById(R.id.checkboxTaskList);
			ImageView icon = (ImageView) view
					.findViewById(R.id.iconAlarmTaskList);
			TextView txtRemind = (TextView) view
					.findViewById(R.id.textReminderTaskList);
			TextView txtHidden = (TextView) view
					.findViewById(R.id.textDueDateTaskHidden);

			int id = cursor.getInt(NUM_COL_ID);
			String remindDate = cursor.getString(NUM_COL_TASK_REMINDDATE);
			String description = cursor.getString(NUM_COL_TASK_DESC);
			String expiryTxt = (!WeddingPlannerHelper.isEmpty(cursor
					.getString(NUM_COL_TASK_DUEDATE))) ? cursor
					.getString(NUM_COL_TASK_DUEDATE) : "";
			txtHidden.setText(expiryTxt);
			ck.setTag(id);
			ck.setText(description);

			Mapping bean = mappingList.get(id);
			ck.setChecked(bean.checked);

			// Set visibility of reminder
			if (!WeddingPlannerHelper.isEmpty(remindDate)) {
				icon.setVisibility(View.VISIBLE);
				txtRemind.setVisibility(View.VISIBLE);
				DateTime parsedDate = DateTime.parse(remindDate);
				txtRemind
						.setText(DateTimeFormat.mediumDate().print(parsedDate));
			} else {
				icon.setVisibility(View.GONE);
				txtRemind.setVisibility(View.GONE);
			}

			// // Handle check/uncheck mechanism
			displayCheck(ck.isChecked(), view, expiryTxt);

		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			String category = cursor.getString(cursor
					.getColumnIndex(COL_TASK_PERIOD));
			TextView tvGrp = (TextView) view
					.findViewById(R.id.lblTaskListHeader);
			Period eCategory = null;
			try {
				eCategory = Period.valueOf(category);
			} catch (NullPointerException e) {
				eCategory = Period.CUSTOM;
			}
			switch (eCategory) {
			case OVER_YEAR:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[0]);
				break;
			case FOURTH_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[1]);
				break;
			case THIRD_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[2]);
				break;
			case SECOND_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[3]);
				break;
			case FIRST_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[4]);
				break;
			case OVER_WEEK:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[5]);
				break;
			case BEFORE_WEEK:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[6]);
				break;
			case DDAY:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[7]);
				break;
			case CUSTOM:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[8]);
				break;
			default:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[8]);
			}
			TextView tvCount = (TextView) view
					.findViewById(R.id.lblCountTaskListHeader);
			StringBuilder buildQuery = new StringBuilder()
					.append("select count(").append(ConstantesDAO.COL_ID)
					.append(") from ").append(ConstantesDAO.TABLE_TASKS)
					.append(" where ").append(ConstantesDAO.COL_TASK_PERIOD)
					.append(" = ?");
			String[] selectionArgs = new String[] { eCategory.name() };
			Cursor c = DaoLocator.getInstance(getActivity().getApplication())
					.get(SERVICES.TASK)
					.rawQuery(buildQuery.toString(), selectionArgs);
			buildQuery = new StringBuilder().append("select count(")
					.append(ConstantesDAO.COL_ID).append(") from ")
					.append(ConstantesDAO.TABLE_TASKS).append(" where ")
					.append(ConstantesDAO.COL_TASK_PERIOD).append(" = ?")
					.append(" AND ")
					.append(COL_TASK_STATUS).append("<>1");
			Cursor c2 = DaoLocator.getInstance(getActivity().getApplication())
					.get(SERVICES.TASK)
					.rawQuery(buildQuery.toString(), selectionArgs);
			if (c != null && c.getCount() > 0 && c2 != null && c2.getCount() > 0) {
				c.moveToFirst();
				c2.moveToFirst();
				tvCount.setText(String.valueOf(c2.getInt(0)) + "/" + String.valueOf(c.getInt(0)));
			}
			c.close();
			c2.close();

		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {

			int dropdownItem = 0;
			if (getArguments() != null) {
				dropdownItem = getArguments().getInt(ARG_SECTION_NUMBER);
			}

			String category = groupCursor.getString(groupCursor
					.getColumnIndex(COL_TASK_PERIOD));
			Cursor c;
			switch (dropdownItem) {
			// Tasks in progress
			case 1:
				c = DaoLocator
						.getInstance(getActivity().getApplicationContext())
						.get(SERVICES.TASK)
						.getCursor(
								COL_TASK_STATUS + "=1" + " AND "
										+ COL_TASK_PERIOD + " = ?",
								new String[] { category });
				break;
			// completed tasks
			case 2:
				c = DaoLocator
						.getInstance(getActivity().getApplicationContext())
						.get(SERVICES.TASK)
						.getCursor(
								COL_TASK_STATUS + "<>1" + " AND "
										+ COL_TASK_PERIOD + " = ?",
								new String[] { category });
				break;
			// Tasks overdue
			case 3:
				StringBuilder query = new StringBuilder();
				query.append("date(").append(COL_TASK_DUEDATE).append(")")
						.append("<= date('now') AND ");
				query.append(COL_TASK_DUEDATE).append("<> '' AND ");
				query.append(COL_TASK_STATUS).append("=1");
				query.append(" AND ").append(COL_TASK_PERIOD + " = ?");
				c = DaoLocator
						.getInstance(getActivity().getApplicationContext())
						.get(SERVICES.TASK)
						.getCursor(query.toString(), new String[] { category });
				break;
			// Total list
			default:
			case 0:
				c = DaoLocator
						.getInstance(getActivity().getApplicationContext())
						.get(SERVICES.TASK)
						.getCursor(COL_TASK_PERIOD + "=?",
								new String[] { category }, null);
			}
			// init array of checkbox status
			Mapping bean = null;
			while (c.moveToNext()) {
				bean = new Mapping();
				bean.id = c.getInt(NUM_COL_ID);
				bean.checked = !DatabaseHelper.convertIntToBool(c
						.getInt(NUM_COL_TASK_STATUS));
				mappingList.append(bean.id, bean);
				Log.d(TAG, "Get bean " + bean.id + " - " + bean.checked);
			}
			return c;

		}

		@Override
		protected View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			View view = mInflator.inflate(R.layout.fragment_task_list_item,
					null);
			CheckBox ck = (CheckBox) view.findViewById(R.id.checkboxTaskList);
			ImageView icon = (ImageView) view
					.findViewById(R.id.iconAlarmTaskList);
			TextView txtRemind = (TextView) view
					.findViewById(R.id.textReminderTaskList);
			TextView txtHidden = (TextView) view
					.findViewById(R.id.textDueDateTaskHidden);

			ck.setOnCheckedChangeListener(new OnCheckedChangeListener(view));

			int id = cursor.getInt(NUM_COL_ID);
			String remindDate = cursor.getString(NUM_COL_TASK_REMINDDATE);
			String description = cursor.getString(NUM_COL_TASK_DESC);
			String expiryTxt = (!WeddingPlannerHelper.isEmpty(cursor
					.getString(NUM_COL_TASK_DUEDATE))) ? cursor
					.getString(NUM_COL_TASK_DUEDATE) : "";
			txtHidden.setText(expiryTxt);
			ck.setTag(id);
			ck.setText(description);

			// Set visibility of reminder
			if (!WeddingPlannerHelper.isEmpty(remindDate)) {
				icon.setVisibility(View.VISIBLE);
				txtRemind.setVisibility(View.VISIBLE);
				DateTime parsedDate = DateTime.parse(remindDate);
				txtRemind
						.setText(DateTimeFormat.mediumDate().print(parsedDate));
			} else {
				icon.setVisibility(View.GONE);
				txtRemind.setVisibility(View.GONE);
			}

			Mapping bean = mappingList.get(id);
			ck.setChecked(bean.checked);
			// // Handle check/uncheck mechanism
			displayCheck(ck.isChecked(), view, expiryTxt);

			return view;
		}

		@Override
		protected View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			View mView = mInflator.inflate(R.layout.fragment_task_list_group,
					null);
			String category = cursor.getString(cursor
					.getColumnIndex(COL_TASK_PERIOD));
			TextView tvGrp = (TextView) mView
					.findViewById(R.id.lblTaskListHeader);

			Period eCategory = null;
			try {
				eCategory = Period.valueOf(category);
			} catch (NullPointerException e) {
				eCategory = Period.CUSTOM;
			}
			switch (eCategory) {
			case OVER_YEAR:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[0]);
				break;
			case FOURTH_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[1]);
				break;
			case THIRD_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[2]);
				break;
			case SECOND_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[3]);
				break;
			case FIRST_QUARTER:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[4]);
				break;
			case OVER_WEEK:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[5]);
				break;
			case BEFORE_WEEK:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[6]);
				break;
			case DDAY:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[7]);
				break;
			case CUSTOM:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[8]);
				break;
			default:
				tvGrp.setText(getResources().getStringArray(
						R.array.task_period_list)[8]);
			}
			TextView tvCount = (TextView) mView
					.findViewById(R.id.lblCountTaskListHeader);
			StringBuilder buildQuery = new StringBuilder()
					.append("select count(").append(ConstantesDAO.COL_ID)
					.append(") from ").append(ConstantesDAO.TABLE_TASKS)
					.append(" where ").append(ConstantesDAO.COL_TASK_PERIOD)
					.append(" = ?");
			String[] selectionArgs = new String[] { eCategory.name() };
			Cursor c = DaoLocator.getInstance(getActivity().getApplication())
					.get(SERVICES.TASK)
					.rawQuery(buildQuery.toString(), selectionArgs);
			buildQuery = new StringBuilder().append("select count(")
					.append(ConstantesDAO.COL_ID).append(") from ")
					.append(ConstantesDAO.TABLE_TASKS).append(" where ")
					.append(ConstantesDAO.COL_TASK_PERIOD).append(" = ?")
					.append(" AND ")
					.append(COL_TASK_STATUS).append("<>1");
			Cursor c2 = DaoLocator.getInstance(getActivity().getApplication())
					.get(SERVICES.TASK)
					.rawQuery(buildQuery.toString(), selectionArgs);
			if (c != null && c.getCount() > 0 && c2 != null && c2.getCount() > 0) {
				c.moveToFirst();
				c2.moveToFirst();
				tvCount.setText(String.valueOf(c2.getInt(0)) + "/" + String.valueOf(c.getInt(0)));
			}
			c.close();
			c2.close();

			return mView;
		}

		/**
		 * Manage shape and color of the checkboxes according to its state and
		 * other field values
		 * 
		 * @param check
		 * @param holder
		 */
		private void displayCheck(boolean check, final View view, String dueDate) {

			int color = getResources().getColor(R.color.Black);
			int iconDrawable = R.drawable.ic_alarm_icon;

			CheckBox ck = (CheckBox) view.findViewById(R.id.checkboxTaskList);
			ImageView icon = (ImageView) view
					.findViewById(R.id.iconAlarmTaskList);
			TextView txtRemind = (TextView) view
					.findViewById(R.id.textReminderTaskList);

			if (check) {
				// Strike through text
				ck.setPaintFlags(ck.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				txtRemind.setPaintFlags(txtRemind.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				color = getResources().getColor(R.color.DarkGray);
				iconDrawable = R.drawable.ic_alarm_icon;
				ck.setTextColor(color);
				txtRemind.setTextColor(color);
				icon.setImageResource(iconDrawable);
			}
			// Task not completed => normal behavior
			else {
				ck.setPaintFlags(ck.getPaintFlags()
						& ~Paint.STRIKE_THRU_TEXT_FLAG);
				txtRemind.setPaintFlags(txtRemind.getPaintFlags()
						& ~Paint.STRIKE_THRU_TEXT_FLAG);
				// Reestablish the former view state
				setColorAndIcon(view, dueDate);
			}
		}

		/**
		 * Sets color of a row according to its state
		 * 
		 * @param holder
		 * @param dueDate
		 */
		private void setColorAndIcon(final View view, String dueDate) {
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

			CheckBox ck = (CheckBox) view.findViewById(R.id.checkboxTaskList);
			ImageView icon = (ImageView) view
					.findViewById(R.id.iconAlarmTaskList);
			TextView txtRemind = (TextView) view
					.findViewById(R.id.textReminderTaskList);

			icon.setImageResource(imageRes);
			ck.setTextColor(color);
			txtRemind.setTextColor(color);
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

		// get the listview
		mListView = (ExpandableListView) rootView
				.findViewById(android.R.id.list);
		mListView.setEmptyView(rootView.findViewById(R.id.empty));
		mListView.setOnItemLongClickListener(this);
		// Gets cursor of list of category
		mAdapter = new ExpAdapter(null, getActivity());
		mListView.setAdapter(mAdapter);

		refresh();

		// Load Ad
		adView = new AdView(this.getActivity());
		adView.setAdUnitId(Constantes.AD_ID);
		adView.setAdSize(AdSize.BANNER);
		FrameLayout layoutAd = (FrameLayout) rootView
				.findViewById(R.id.LayoutTaskAd);
		layoutAd.addView(adView);

		// Initiez une demande générique.
		AdRequest adRequest = WeddingPlannerHelper.buildAdvert(
				this.getActivity(), Constantes.DEBUG);

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

		long packedPosition = mListView.getExpandableListPosition(position);
		Log.v(TAG, "Get flat list position " + packedPosition);
		if (ExpandableListView.getPackedPositionType(packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int groupPosition = ExpandableListView
					.getPackedPositionGroup(packedPosition);
			int childPosition = ExpandableListView
					.getPackedPositionChild(packedPosition);

			Log.v(TAG,
					String.format(
							"onItemLongClick - perform long click on item of the list [grouppos=%d, childpos=%d]",
							groupPosition, childPosition));

			if ((null != mListener) && (mActionMode != null)) {
				return false;
			}
			int index = mListView.getFlatListPosition(ExpandableListView
					.getPackedPositionForChild(groupPosition, childPosition));

			// Starts the CAB using the ActionMode.Callbakc defined above
			mActionMode = getActivity().startActionMode(mActionModeCallback);
			mListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
			mListView.setItemChecked(index, true);
			view.setSelected(true);
			// Return true as we are handling the event.
			return true;
		}

		return false;
	}

	/**
	 * Refresh the adapter with an updated cursor
	 */
	@Override
	public void refresh() {
		Log.v(TAG, "Refresh list");
		// refreshes list
		Cursor c = (DaoLocator.getInstance(getActivity().getApplication())
				.<TasksDao> get(SERVICES.TASK)).getCursorPeriod();
		c = sort(c);
		mAdapter.changeCursor(c);
		mAdapter.notifyDataSetChanged();
	}

	private Cursor sort(Cursor c) {
		final class Element {
			private long id;
			private Period period;

			public Element(final long id, final Period period) {
				this.id = id;
				this.period = period;
			}

			@Override
			public String toString() {
				return Objects.toStringHelper(this).omitNullValues()
						.add("id", id).add("period", period.name()).toString();
			}

		}
		// Sort elements of the cursor
		TreeSet<Element> tree = new TreeSet<Element>(new Comparator<Element>() {

			@Override
			public int compare(Element lhs, Element rhs) {
				if (lhs.period.getOrder() < rhs.period.getOrder())
					return -1;
				else if (lhs.period.getOrder() > rhs.period.getOrder())
					return 1;
				else
					return 0;
			}

		});
		Element elt = null;
		c.moveToFirst();
		while (!c.isAfterLast()) {
			elt = new Element(c.getLong(c.getColumnIndex(COL_ID)),
					Period.valueOf(c.getString(c
							.getColumnIndex(COL_TASK_PERIOD))));
			tree.add(elt);
			c.moveToNext();
		}
		// Build cursor
		String[] columnNames = c.getColumnNames();
		MatrixCursor sorted = new MatrixCursor(columnNames, 10);
		for (Element leaf : tree) {
			Log.d(TAG, leaf.toString());
			sorted.addRow(new Object[] { leaf.id, leaf.period.name() });
		}

		// Close the cursor we have copied
		c.close();
		return sorted;
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
		if (adView != null)
			adView.pause();
		Log.d(TAG, "onPause - save all modified items of the list");
		TasksDao service = DaoLocator.getInstance(
				getActivity().getApplicationContext()).get(SERVICES.TASK);
		Task task = null;
		ExpAdapter.Mapping bean = null;
		for (int i = 0; i < mAdapter.mappingList.size(); i++) {
			bean = mAdapter.mappingList.valueAt(i);
			Log.v(TAG, "Item " + bean.id + " is saved with a check "
					+ bean.checked);
			task = service.get(bean.id);
			if (null != task) {
				task.setActive(!bean.checked);
				service.update(bean.id, task);
			}
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adView != null)
			adView.resume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}
}
