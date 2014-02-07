package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_DESC;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_REMINDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DESC;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_REMINDDATE;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.TasksDao;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

public class TaskListFragment extends Fragment {

	
	private static final String TAG = TaskListFragment.class.getSimpleName();
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * The list adapter
	 */
	private TaskCursorAdapter mAdapter;

	/**
	 * The fragment's ListView/GridView.
	 */
	private AbsListView mListView;

	/**
	 * Override SimpleCursorAdapter in order to have a stronger control on this
	 * display to row
	 * 
	 * @author YCH
	 * 
	 */
	private class TaskCursorAdapter extends SimpleCursorAdapter {

		private Context context;
		
		private class ViewHolder {
			private CheckBox check;
			private ImageView alarmIcon;
			private TextView remindDateTxt;
		}

		public TaskCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);
			this.context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ViewHolder holder;
			
			// If view does not already exists we create it
			if (row == null) {
				row = View.inflate(context, R.layout.fragment_task_adapter, null);
				holder = new ViewHolder();
				holder.check = (CheckBox) row.findViewById(R.id.checkboxTaskList);
				// Listener on check box
				holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							// Strike through text
							buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
						}
						else {
							// Normal text
							buttonView.setPaintFlags(buttonView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
						}
						buttonView.setText(buttonView.getText());		
					}
				});
				holder.alarmIcon = (ImageView) row.findViewById(R.id.iconAlarmTaskList);
				holder.remindDateTxt = (TextView) row.findViewById(R.id.textReminderTaskList);
				row.setTag(holder);
			}
			
			holder = (ViewHolder) row.getTag();
			Cursor c = getCursor();
			c.moveToPosition(position);
			
			CheckBox check = holder.check;
			ImageView icon = holder.alarmIcon;
			TextView dateTxt = holder.remindDateTxt;
			String remindDate = c.getString(NUM_COL_TASK_REMINDDATE);
			
			check.setText(c.getString(NUM_COL_TASK_DESC));
			
			// Set visibility of reminder
			if (null != remindDate && !remindDate.isEmpty()) {
				icon.setVisibility(View.VISIBLE);
				dateTxt.setVisibility(View.VISIBLE);
				DateTime parsedDate = DateTime.parse(remindDate);
				dateTxt.setText(DateTimeFormat.mediumDate().print(parsedDate));
			}
			else{
				icon.setVisibility(View.GONE);
				dateTxt.setVisibility(View.GONE);
			}
			
			// Set color according to expiration date
			String expiryTxt = c.getString(NUM_COL_TASK_DUEDATE);
			if (null != expiryTxt && !expiryTxt.isEmpty()) {
				DateTime today = DateTime.now();
				DateTime expiry = DateTime.parse(expiryTxt);
				
				if (expiry.compareTo(today) < 0) {
					int color = getResources().getColor(R.color.Red);
					check.setTextColor(color);
					dateTxt.setTextColor(color);
				}
			}
			
			return row;
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

		// Gets cursor from db
		Cursor c = ((TasksDao) DaoLocator.getInstance(
				getActivity().getApplicationContext()).get(SERVICES.TASK))
				.getCursor();
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

		// Set the adapter
		mListView = (AbsListView) rootView.findViewById(android.R.id.list);
		((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		// super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.task, menu);
	}
}
