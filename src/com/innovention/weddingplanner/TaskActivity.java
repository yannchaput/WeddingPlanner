package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.TaskFragment.OnValidateTask;
import com.innovention.weddingplanner.TaskListFragment.OnTaskSelectedListener;
import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.TasksDao;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
								getString(R.string.title_section3), }), this);
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
		args.putInt(TaskListFragment.ARG_SECTION_NUMBER, position + 1);
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
		
		TasksDao service = (TasksDao) DaoLocator.getInstance(getApplicationContext()).get(SERVICES.TASK);
		
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
	public void onValidateTask(final View v, FragmentTags tag) {
		Log.d(TAG, "validateTask - click on validate task button");

		// Hide virtual keyboard if opened
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(
				getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		// Retrieve field content
		String description = ((EditText) findViewById(R.id.taskEditDescription))
				.getText().toString();
		String dueDateTxt = ((EditText) findViewById(R.id.taskEditDateEcheance)).getText().toString();
		String remindDateTxt = (String) ((Spinner) findViewById(R.id.taskSpinnerRemindDate)).getSelectedItem();
		DateTime dueDate = null;
		DateTime remindDate = null;
		
		try {
			if (!dueDateTxt.isEmpty()) {
				dueDate = DateTimeFormat.shortDate().parseDateTime(dueDateTxt);
				remindDate = calculateReminder(dueDate, remindDateTxt);
			}
			// Build task bean
			Task task = new Task.Builder().withDesc(description)
					.dueDate(dueDate)
					.remind(remindDate)
					.remindOption(remindDateTxt)
					.build();
			// Validate task
			task.validate();
			Log.v(TAG, "saveTask - build task bean: " + task);
			TasksDao dao = (TasksDao) DaoLocator.getInstance(getApplication())
					.get(SERVICES.TASK);
			dao.insert(task);
			replaceFragment(this, FragmentTags.TAG_FGT_TASKLIST);
		} catch (MissingMandatoryFieldException e) {
			showAlert(R.string.task_alert_dialog_title,
					R.string.task_mandatory_validator_message,
					getFragmentManager());
		}

	}
	
	/**
	 * Calculate reminder date from due date
	 * @param dueDate
	 * @param option
	 * @return reminder date
	 */
	private DateTime calculateReminder(DateTime dueDate, String option) {

		DateTime remindDate = dueDate;
		
		// Hard set time to midday
		remindDate = remindDate.withHourOfDay(12);

		// Default case
		if ((null == remindDate) || (option.equals(getResources().getString(R.string.task_spinner_item1)))) {
			remindDate = null;
		}
		// 1 month
		else if (option
				.equals(getResources().getString(R.string.task_spinner_item2))) {
			remindDate = remindDate.minusMonths(1);
		}
		// 2 weeks
		else if (option.equals(getResources().getString(
				R.string.task_spinner_item3))) {
			remindDate = remindDate.minusWeeks(2);
		}
		// 1 week
		else if (option.equals(getResources().getString(
				R.string.task_spinner_item4))) {
			remindDate = remindDate.minusWeeks(1);
		}
		// 3 days
		else if (option.equals(getResources().getString(
				R.string.task_spinner_item5))) {
			remindDate = remindDate.minusDays(3);
		}
		// 1 day
		else if (option.equals(getResources().getString(
				R.string.task_spinner_item6))) {
			remindDate = remindDate.minusDays(1);
		}
		// 6 hours
		else if (option.equals(getResources().getString(
				R.string.task_spinner_item7))) {
			remindDate = remindDate.minusHours(6);
		}
		// 2 hours
		else if (option.equals(getResources().getString(
				R.string.task_spinner_item8))) {
			remindDate = remindDate.minusHours(2);
		}
		// 1 hour
		else if (option.equals(getResources().getString(
				R.string.task_spinner_item9))) {
			remindDate = remindDate.minusHours(1);
		}

		return remindDate;
	}
}
