package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_D;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_M;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_Y;

import org.joda.time.DateTime;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class TaskFragment extends Fragment {
	
	static final String TAG = TaskFragment.class.getSimpleName();

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
		DateTime now = new DateTime();
		Bundle parameters = new Bundle();
		parameters.putInt(KEY_DTPICKER_Y, now.year().get());
		parameters.putInt(KEY_DTPICKER_M, now.monthOfYear().get());
		parameters.putInt(KEY_DTPICKER_D, now.dayOfMonth().get());
		datePicker.setArguments(parameters);
		WeddingPlannerHelper.showFragmentDialog(getActivity(), datePicker, FragmentTags.TAG_FGT_DATEPICKER);
	}

}
