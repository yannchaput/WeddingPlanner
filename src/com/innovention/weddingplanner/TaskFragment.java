package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.Constantes.TASK_DEFAULT_REMIND_HOUR;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.isEmpty;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showAlert;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showFragmentDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.bean.Task.Period;
import com.innovention.weddingplanner.exception.InconsistentFieldException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

/**
 * Task fragment
 * 
 * @author YCH
 * 
 */
public class TaskFragment extends Fragment implements OnDateSetListener {

	private static final String TAG = TaskFragment.class.getSimpleName();
	
	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_CREATETASK;
	// Parameter bean
	private Task bean;

	// Description field
	private EditText descriptionTxt;
	// Due date field
	private EditText dueDateTxt;
	// Reminder field
	private Spinner remindSpinner;
	// Validate button
	private Button validateBtn;
	// listener on validate button
	private OnValidateTask mListener;
	
	interface OnValidateTask {
		void onValidateTask(final IDtoBean bean, FragmentTags tag);  
	}

	/**
	 * Internal date picker dialog for task purpose
	 * 
	 * @author Yann
	 * 
	 */
	public static class DatePickerFragment extends DialogFragment implements
			OnDateSetListener {

		private OnDateSetListener listener = null;
		private DateTime initialDate;
		private FragmentTags mode = FragmentTags.TAG_FGT_TASKDATEPICKER;

		static DatePickerFragment newInstance(
				final OnDateSetListener callback, 
				final FragmentTags mode,
				final DateTime setupDate) {
			DatePickerFragment instance = new DatePickerFragment();
			instance.listener = callback;
			instance.mode = mode;
			instance.initialDate = (FragmentTags.TAG_FGT_TASKDATEPICKER_UPDATE.equals(mode) && null != setupDate) ?  setupDate : DateTime.now();
			return instance;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			Log.d(TAG, "onCreateDialog - "
					+ "Create dialog fragment for the first time");

			// deduct 1 month from the months to add as beacuase referential is
			// not the same between Joda and regular JDK
			DatePickerDialog pickDlg = new DatePickerDialog(getActivity(),
					this, initialDate.getYear(), initialDate.getMonthOfYear() - 1,
					initialDate.getDayOfMonth());
			// substract 1 second from now so as to min date < displayed date
			// otherwise we have an exception
			pickDlg.getDatePicker().setMinDate(DateTime.now().getMillis()-1000);

			Log.d(TAG, "Set date to " + initialDate);

			return pickDlg;
		}

		/**
		 * Callback when wedding date is set through date picker
		 */
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			Log.d(TAG, "onDateSet - " + "Set date " + dayOfMonth + "/"
					+ monthOfYear + "/" + year);
			// Add 1 to month to translate from joda referential to java date
			// one
			listener.onDateSet(view, year, monthOfYear + 1, dayOfMonth);

		}
	}

	public TaskFragment() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Factory method
	 * 
	 * @return an instance of TaskFragment
	 */
	public static TaskFragment newInstance() {
		TaskFragment fgt = new TaskFragment();
		fgt.mode = FragmentTags.TAG_FGT_CREATETASK;
		fgt.bean = null;
		return fgt;
	}
	
	/**
	 * Factory method with parameters
	 * @param action
	 * @param bean
	 * @return
	 */
	public static TaskFragment newInstance(FragmentTags action, final Task bean) {
		Log.v(TAG, String.format("newInstance - create new instance in mode %s and with bean %s", action.toString(), bean.toString()));
		TaskFragment fgt = newInstance();
		fgt.mode = action;
		fgt.bean = bean;
		return fgt;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_task_form, container,
				false);
		
		// Customize action bar for task form template
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		validateBtn = (Button) view.findViewById(R.id.taskButtonSave);
		dueDateTxt = (EditText) view.findViewById(R.id.taskEditDateEcheance);
		descriptionTxt = (EditText) view.findViewById(R.id.taskEditDescription);
		remindSpinner = (Spinner) view.findViewById(R.id.taskSpinnerRemindDate);
		
		// Case update task
		if (mode.equals(FragmentTags.TAG_FGT_UPDATETASK) && null != bean) {
			descriptionTxt.setText(bean.getDescription());
			if (bean.getDueDate() != null) 
				dueDateTxt.setText(DateTimeFormat.shortDate().print(bean.getDueDate()));
			ArrayAdapter<String> spinAdapter = (ArrayAdapter<String>) remindSpinner.getAdapter();
			int spinnerPos = spinAdapter.getPosition(bean.getRemindChoice());
			remindSpinner.setSelection(spinnerPos);
		}
		
		validateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					DateTime dueDate = null;
					DateTime remindDate = null;
					
					if ( !isEmpty(dueDateTxt.getText().toString()) ) {
						dueDate = DateTimeFormat.shortDate().parseDateTime(dueDateTxt.getText().toString());
						remindDate = calculateReminder(dueDate, (String) remindSpinner.getSelectedItem());
						Log.v(TAG, "Due date = " + dueDate);
						Log.v(TAG, "remindDate = " + remindDate);
					}
					// Build task bean
					Task task = new Task.Builder()
							.withId(FragmentTags.TAG_FGT_UPDATETASK.equals(mode) ? bean.getId() : -1)
							.withDesc(descriptionTxt.getText().toString())
							.dueDate(dueDate)
							.remind(remindDate)
							.remindOption((String) remindSpinner.getSelectedItem())
							.setPlanning(Period.CUSTOM)
							.build();
					// Validate task
					try {
						task.validate(TaskFragment.this.getActivity());
						mListener.onValidateTask(task, TaskFragment.this.mode);
					} catch (MissingMandatoryFieldException e) {
						showAlert(R.string.task_alert_dialog_title,
								R.string.task_mandatory_validator_message,
								getFragmentManager());
					} catch (InconsistentFieldException e) {
						showAlert(R.string.task_alert_dialog_title,
								R.string.task_inconsistent_validator_message,
								getFragmentManager());
					}
				}
				
			}
		});
		dueDateTxt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (FragmentTags.TAG_FGT_UPDATETASK.equals(TaskFragment.this.mode)) {
					showDatePickerDialog(FragmentTags.TAG_FGT_TASKDATEPICKER_UPDATE, bean.getDueDate());
				}
				else {
					showDatePickerDialog(FragmentTags.TAG_FGT_TASKDATEPICKER);
				}
			}
		});
		
		return view;
	}
	
	/**
	 * Callback called when one clicked on the due date field in create mode
	 * 
	 * @param v
	 *            view
	 */
	private void showDatePickerDialog(final FragmentTags mode) {

		showDatePickerDialog(mode, null);
	}

	/**
	 * Callback called when one clicked on the due date field in update mode
	 * 
	 * @param v
	 *            view
	 */
	private void showDatePickerDialog(final FragmentTags mode, final DateTime dueDate) {

		Log.d(TAG, "showDatePickerDialog - " + "show date picker fragment");

		DialogFragment datePicker = DatePickerFragment.newInstance(this, mode, dueDate);
		showFragmentDialog(getActivity(), datePicker,
				mode);
	}

	/**
	 * Listens to date change event
	 * 
	 * @param view
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		DateTime setDate = new DateTime().withDayOfMonth(dayOfMonth)
				.withMonthOfYear(monthOfYear).withYear(year);
		dueDateTxt.setText(DateTimeFormat.shortDate().print(setDate));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnValidateTask) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		dueDateTxt = null;
		mode = null;
		bean = null;
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
		remindDate = remindDate.withHourOfDay(TASK_DEFAULT_REMIND_HOUR);

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
