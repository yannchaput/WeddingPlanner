package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.hideKeyboard;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.replaceFragment;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showAlert;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.TaskFragment.OnValidateTask;
import com.innovention.weddingplanner.TaskListFragment.OnTaskSelectedListener;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.TasksDao;

public class TaskActivity extends Activity implements
		ActionBar.OnNavigationListener, OnValidateTask, OnTaskSelectedListener {

	private static final String TAG = TaskActivity.class.getSimpleName();

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2),
								getString(R.string.title_section3), 
								getString(R.string.title_section4)}), this);
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.task, menu);
	// return true;
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_add_task:
			replaceFragment(this, FragmentTags.TAG_FGT_CREATETASK);
			return true;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		android.app.Fragment fragment = new TaskListFragment();
		Bundle args = new Bundle();
		args.putInt(TaskListFragment.ARG_SECTION_NUMBER, position);
		fragment.setArguments(args);
		getFragmentManager().beginTransaction()
				.replace(R.id.layoutTask, fragment).commit();
		return true;
	}
	
	/**
	 * Triggered by the CAB on the list
	 * @param id
	 * @param action
	 */
	@Override
	public void onSelectTask(long id, FragmentTags action) {
		Log.d(TAG, String.format("onSelectTask - task with id %d was selected for %s", id, action.getValue()));
		
		TasksDao service = DaoLocator.getInstance(getApplicationContext()).get(SERVICES.TASK);
		
		switch(action) {
			case TAG_FGT_UPDATETASK:
				Task task = service.get(id);
				Log.v(TAG, "Update task " + task);
				if (null != task) {
					replaceFragment(this, FragmentTags.TAG_FGT_UPDATETASK, task);
				}
				else {
					showAlert(R.string.update_task_alert_dialog_title, R.string.update_task_0_alert_message, getFragmentManager());
				}
				break;
			case TAG_FGT_DELETETASK:
				Log.v(TAG, "Delete task with id " + id);
				int count = service.removeWithId((int) id);
				if (count > 1)
					showAlert(R.string.delete_task_alert_dialog_title,
							R.string.delete_guest_multiple_alert_message,
							getFragmentManager());
				else if (count == 0){
					showAlert(R.string.delete_task_alert_dialog_title,
							R.string.delete_guest_0_alert_message,
							getFragmentManager());
				}
				else {
					showAlert(R.string.delete_task_OK_alert_dialog_title,
							R.string.delete_guest_OK_alert_message,
							getFragmentManager());
				}
				break;
			default:
				return;
		}
	}

	/**
	 * Triggered on "Validate" button click
	 * @param v
	 * @param tag
	 */
	public void onValidateTask(final IDtoBean bean, FragmentTags tag) {
		Log.d(TAG, "validateTask - click on validate task button");

		// Hide virtual keyboard if opened
		hideKeyboard(this);

			Log.v(TAG, "saveTask - build task bean: " + bean);
			TasksDao dao = DaoLocator.getInstance(getApplication())
					.get(SERVICES.TASK);
			if (FragmentTags.TAG_FGT_CREATETASK.equals(tag)) {
				Log.d(TAG, "Save task in creation mode : " + bean);
				dao.insert( (Task) bean);
			}
			else if (FragmentTags.TAG_FGT_UPDATETASK.equals(tag)) {
				Log.d(TAG, "Save task in update mode : " + bean);
				dao.update(bean.getId(), (Task) bean);
			}
			
			replaceFragment(this, FragmentTags.TAG_FGT_TASKLIST);
	}
	
}
