package com.innovention.weddingplanner;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Task fragment
 * 
 * @author YCH
 * 
 */
public class TaskFragment extends Fragment implements OnDateSetListener {

	private static final String TAG = TaskFragment.class.getSimpleName();

	private EditText dueDateTxt;

	/**
	 * Internal date picker dialog for task purpose
	 * 
	 * @author Yann
	 * 
	 */
	public static class DatePickerFragment extends DialogFragment implements
			OnDateSetListener {

		private OnDateSetListener listener = null;

		public static DatePickerFragment newInstance(
				final OnDateSetListener callback) {
			DatePickerFragment instance = new DatePickerFragment();
			instance.listener = callback;
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

			DateTime now = DateTime.now();

			// deduct 1 month from the months to add as beacuase referential is
			// not the same between Joda and regular JDK
			DatePickerDialog pickDlg = new DatePickerDialog(getActivity(),
					this, now.getYear(), now.getMonthOfYear() - 1,
					now.getDayOfMonth());
			pickDlg.getDatePicker().setMinDate(now.getMillis());

			Log.d(TAG, "Set date to " + now);

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
		dueDateTxt = (EditText) view.findViewById(R.id.taskEditDateEcheance);
		dueDateTxt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);

			}
		});
		return view;
	}

	/**
	 * Callback called when one clicked on the wedding date
	 * 
	 * @param v
	 *            view
	 */
	private void showDatePickerDialog(View v) {

		Log.d(TAG, "showDatePickerDialog - " + "show date picker fragment");

		DialogFragment datePicker = DatePickerFragment.newInstance(this);
		WeddingPlannerHelper.showFragmentDialog(getActivity(), datePicker,
				FragmentTags.TAG_FGT_TASKDATEPICKER);
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
				.withMonthOfYear(monthOfYear).withYear(monthOfYear);
		dueDateTxt.setText(DateTimeFormat.shortDate().print(setDate));
	}

}
