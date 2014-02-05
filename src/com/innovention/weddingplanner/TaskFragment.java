package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_D;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_M;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_Y;

import org.joda.time.DateTime;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Task fragment
 * @author YCH
 *
 */
public class TaskFragment extends Fragment implements OnDateSetListener {
	
	static final String TAG = TaskFragment.class.getSimpleName();
	
	/**
	 * Internal date picker dialog for task purpose
	 * @author Yann
	 *
	 */
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		/* (non-Javadoc)
		 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			Log.d(TAG, "onCreateDialog - " + "Create dialog fragment for the first time");
			
			DateTime now = DateTime.now();
			
			// deduct 1 month from the months to add as beacuase referential is not the same between Joda and regular JDK
			DatePickerDialog pickDlg = new DatePickerDialog(getActivity(), this, now.getYear(), now.getMonthOfYear()-1, now.getDayOfMonth());
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
		
			Log.d(TAG, "onDateSet - " + "Set date " + dayOfMonth + "/" + monthOfYear + "/" + year);
			
			
		}
	}


	public TaskFragment() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Factory method
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
		//Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_task_form, container, false);
		EditText dateInput = (EditText) view.findViewById(R.id.taskDateEcheance);
		dateInput.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);
				
			}
		});
		return view;
	}
	
	/**
	 * Callback called when one clicked on the wedding date
	 * @param v view
	 */
	private void showDatePickerDialog(View v) {
		
		// Todo change for a new implementation of date picker dialog for onset method
		Log.d(TAG, "showDatePickerDialog - " + "show date picker fragment");
		
		DialogFragment datePicker = new DatePickerFragment();
		WeddingPlannerHelper.showFragmentDialog(getActivity(), datePicker, FragmentTags.TAG_FGT_TASKDATEPICKER);
	}

	/**
	 * Listens to date change event
	 * @param view
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		
	}

}
