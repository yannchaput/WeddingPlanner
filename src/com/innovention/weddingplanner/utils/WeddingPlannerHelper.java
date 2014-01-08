package com.innovention.weddingplanner.utils;


import static com.innovention.weddingplanner.Constantes.ILLEGAL_EMAIL_MSG;
import static com.innovention.weddingplanner.Constantes.ILLEGAL_PHONE_MSG;
import static com.innovention.weddingplanner.Constantes.MISSING_MANADTORY_FIELD_MSG;
import static com.innovention.weddingplanner.Constantes.FragmentTags;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Patterns;
import android.view.View;

import com.innovention.weddingplanner.AlertDialogFragment;
import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

public class WeddingPlannerHelper {
	
	private static final String TAG = WeddingPlannerHelper.class.getSimpleName();
	
	private WeddingPlannerHelper() {
		
	}

	/**
	 * Compute the remaining days between now and the wedding
	 * @param year
	 * @param month (joda time format)
	 * @param day
	 * @return
	 */
	public static final int computeDate(final int year, final int month, final int day) {
		DateTime now = DateTime.now();
		DateTime weddingDate = new DateTime(year, month, day, 0, 0);
		return Days.daysBetween(now.toDateMidnight(), weddingDate.toDateMidnight()).getDays();
	}
	
	/**
	 * Return the view linked to the component
	 * @param fgt
	 * @param id
	 * @return
	 */
	public static final View findView(final Fragment fgt, int id ) {
		return fgt.getView().findViewById(id);
		
	}
	
	// TODO make a generic validator
	/**
	 * Validate a mandatory field is not empty and not null
	 * @param field
	 * @return boolean
	 */
	public static <T> void validateMandatory(T field) throws MissingMandatoryFieldException {

		if ( (field == null) || (field.equals("")) )
			throw new MissingMandatoryFieldException(MISSING_MANADTORY_FIELD_MSG);
	}
	
	/**
	 * Validate a email is  well formated (non mandatory)
	 * @param field
	 * @throws IncorrectMailException
	 */
	public static void validateEmail(final String field) throws IncorrectMailException {
		if (field!=null && !field.equals("") && !Patterns.EMAIL_ADDRESS.matcher(field).matches())
			throw new IncorrectMailException(ILLEGAL_EMAIL_MSG);
	}
	
	/**
	 * Validate a phone number (non mandatory)
	 * @param field
	 * @throws IncorrectTelephoneException
	 */
	public static void validateTelephone(final String field) throws IncorrectTelephoneException {
		if (field!=null && !field.equals("") && !Patterns.PHONE.matcher(field).matches())
			throw new IncorrectTelephoneException(ILLEGAL_PHONE_MSG);
	}
	
	/**
	 * Show an alert dialog
	 * @param titleResId : res id of title
	 * @param content : content to show
	 * @param fgtMgr : fragment manager
	 */
	public static void showAlert(int titleResId, int content, final FragmentManager fgtMgr) {
		DialogFragment newDlg = AlertDialogFragment.newInstance(titleResId, content);
		newDlg.show(fgtMgr,FragmentTags.TAG_FGT_GUESTALERT.getValue());
	}
	
	/**
	 * Get a registered font from assets
	 * @param context
	 * @param fontName
	 * @return
	 */
	public static Typeface getFont(Context context, String fontName) {
		return Typeface.createFromAsset(context.getAssets(), fontName);
	}
}
