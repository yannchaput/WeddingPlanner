package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.innovention.weddingplanner.Constantes.FragmentTags;
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
		ActionBar.OnNavigationListener {

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
	 * Triggered on "Validate" button click
	 * 
	 * @param v
	 *            the originating view
	 */
	public void validateTask(final View v) {
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
		
		DateTime dueDate = DateTimeFormat.shortDate().parseDateTime(dueDateTxt);
		
		//TODO Remplacer le string array par un enum avec les valeurs correspondantes sous forme de Duration
		// Rajouter méthode de calcul de l'échéance
		
		try {
			// Build task bean
			Task task = new Task.Builder().withDesc(description)
					.dueDate(dueDate)
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
}
