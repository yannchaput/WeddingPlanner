package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.Constantes.DEFAULT_MONTHS_INTERVAL;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_D;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_M;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_Y;

import org.joda.time.DateTime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

/**
 * DatePicker dialog fragment
 * @author Yann
 *
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	private static final String TAG =  DatePickerFragment.class.getSimpleName();

	/* (non-Javadoc)
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		Log.d(TAG, "onCreateDialog - " + "Create dialog fragment for the first time");
		
		DateTime prospectiveDate = null;
		
		DateTime now = DateTime.now();
		if (getArguments() != null) {
			int year = getArguments().getInt(KEY_DTPICKER_Y);
			Log.d(TAG, "Year passed as parameter : " + year);
			int month = getArguments().getInt(KEY_DTPICKER_M);
			Log.d(TAG, "Month passed as parameter : " + month);
			int day = getArguments().getInt(KEY_DTPICKER_D);
			Log.d(TAG, "Day passed as parameter : " + day);
			prospectiveDate = new DateTime(year, month, day, 0, 0);
			Log.d(TAG, "Parameter date set to display : " + prospectiveDate);
		}
		else prospectiveDate = now.plusMonths(DEFAULT_MONTHS_INTERVAL);
		
		// deduct 1 month from the months to add as beacuase referential is not the same between Joda and regular JDK
		DatePickerDialog pickDlg = new DatePickerDialog(getActivity(), this, prospectiveDate.getYear(), prospectiveDate.getMonthOfYear()-1, prospectiveDate.getDayOfMonth());
		pickDlg.getDatePicker().setMinDate(now.getMillis());
		
		Log.d(TAG, "Set date to " + prospectiveDate);
		
		return pickDlg;
	}

	/**
	 * Callback when wedding date is set through date picker
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
	
		Log.d(TAG, "onDateSet - " + "Set date " + dayOfMonth + "/" + monthOfYear + "/" + year);
		// TODO: ajouter une validation pour les dates n�gatives
		// Add 1 to month because calendar format is 0 to 11
		((MainActivity) getActivity()).updateWeddingDate(year, monthOfYear+1, dayOfMonth);
		
	}
	
	

}
