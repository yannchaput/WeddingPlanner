package com.innovention.weddingplanner.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.innovention.weddingplanner.Constantes.ILLEGAL_EMAIL_EX;
import static com.innovention.weddingplanner.Constantes.ILLEGAL_PHONE_EX;
import static com.innovention.weddingplanner.Constantes.MISSING_MANADTORY_FIELD_EX;

import java.text.NumberFormat;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.AdRequest;
import com.innovention.weddingplanner.AlertDialogFragment;
import com.innovention.weddingplanner.BudgetFragment;
import com.innovention.weddingplanner.BudgetListFragment;
import com.innovention.weddingplanner.BudgetPieFragment;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.ContactFragment;
import com.innovention.weddingplanner.ContactListFragment;
import com.innovention.weddingplanner.GuestListFragment;
import com.innovention.weddingplanner.GuestPagerFragment;
import com.innovention.weddingplanner.R;
import com.innovention.weddingplanner.TaskFragment;
import com.innovention.weddingplanner.TaskListFragment;
import com.innovention.weddingplanner.VendorFragment;
import com.innovention.weddingplanner.VendorListFragment;
import com.innovention.weddingplanner.bean.Budget;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.bean.Vendor;
import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

public final class WeddingPlannerHelper {

	private static final String TAG = WeddingPlannerHelper.class
			.getSimpleName();

	private WeddingPlannerHelper() {

	}

	/**
	 * Compute the remaining days between now and the wedding
	 * 
	 * @param year
	 * @param month
	 *            (joda time format)
	 * @param day
	 * @return
	 */
	public static final int computeDate(final int year, final int month,
			final int day) {
		DateTime now = DateTime.now();
		DateTime weddingDate = new DateTime(year, month, day, 0, 0);
		return Days.daysBetween(now.toDateMidnight(),
				weddingDate.toDateMidnight()).getDays();
	}

	/**
	 * Return the view linked to the component
	 * 
	 * @param fgt
	 * @param id
	 * @return
	 */
	public static final View findView(final Fragment fgt, int id) {
		return fgt.getView().findViewById(id);

	}

	/**
	 * Get a string typed resource from the context
	 * 
	 * @param ctx
	 * @param id
	 * @return the string
	 */
	public static final String getStringResource(final Context ctx, int id) {
		return ctx.getResources().getString(id);
	}

	/**
	 * String isEmpty utility
	 * 
	 * @param str
	 * @return true if string is empty or null
	 */
	public static boolean isEmpty(final String str) {
		// return ((null == str) || (str.isEmpty()));
		return TextUtils.isEmpty(str);
	}

	/**
	 * Validate a mandatory field is not empty and not null
	 * 
	 * @param field
	 * @return boolean
	 */
	public static <T extends CharSequence> void validateMandatory(T field)
			throws MissingMandatoryFieldException {

		if ((field == null) || (field.equals("")))
			throw new MissingMandatoryFieldException(MISSING_MANADTORY_FIELD_EX);
	}

	/**
	 * Validate a email is well formated (non mandatory)
	 * 
	 * @param field
	 * @throws IncorrectMailException
	 */
	public static void validateEmail(final String field)
			throws IncorrectMailException {
		if (field != null && !field.equals("")
				&& !Patterns.EMAIL_ADDRESS.matcher(field).matches())
			throw new IncorrectMailException(ILLEGAL_EMAIL_EX);
	}

	/**
	 * Validate a phone number (non mandatory)
	 * 
	 * @param field
	 * @throws IncorrectTelephoneException
	 */
	public static void validateTelephone(final String field)
			throws IncorrectTelephoneException {
		if (field != null && !field.equals("")
				&& !Patterns.PHONE.matcher(field).matches())
			throw new IncorrectTelephoneException(ILLEGAL_PHONE_EX);
	}

	/**
	 * Show an alert dialog
	 * 
	 * @param titleResId
	 *            : res id of title
	 * @param content
	 *            : content to show
	 * @param fgtMgr
	 *            : fragment manager
	 */
	public static void showAlert(int titleResId, int content,
			final FragmentManager fgtMgr) {
		DialogFragment newDlg = AlertDialogFragment.newInstance(titleResId,
				content);
		newDlg.show(fgtMgr, FragmentTags.TAG_FGT_GUESTALERT.getValue());
	}

	/**
	 * Get a registered font from assets
	 * 
	 * @param context
	 * @param fontName
	 * @return
	 */
	public static Typeface getFont(Context context, String fontName) {
		return Typeface.createFromAsset(context.getAssets(), fontName);
	}

	/**
	 * Replace the layout of the activity with the fragment specified
	 * 
	 * @param tag
	 *            tag of the fragment to display
	 */
	public static void replaceFragment(FragmentActivity parent, FragmentTags tag,
			final IDtoBean... params) {
		FragmentTransaction tx = parent.getSupportFragmentManager().beginTransaction();
		Fragment fgt = null;
		int resId;

		// TODO use tag native field to store the enum instead of private
		// attribute in every fragment
		switch (tag) {
		case TAG_FGT_GUESTLIST:
			fgt = parent.getSupportFragmentManager().findFragmentByTag(tag.getValue());
			if (null == fgt) {
				Log.v(TAG, "Create new GuestListFragment instance");
				fgt = GuestListFragment.newInstance();
			}
			else {
				Log.v(TAG, "Reuse existing GuestListFragment instance");
				((GuestListFragment) fgt).refresh();
			}
			resId = R.id.LayoutGuest;
			break;
		case TAG_FGT_GUESTPAGER:
			fgt = parent.getSupportFragmentManager().findFragmentByTag(tag.getValue());
			if (null == fgt) {
				Log.v(TAG, "Create new GuestPagerFragment instance");
				fgt = GuestPagerFragment.newInstance();
			}
			resId = R.id.LayoutGuest;
			break;
		case TAG_FGT_CREATECONTACT:
			fgt = ContactFragment.newInstance();
			resId = R.id.LayoutGuest;
			break;
		case TAG_FGT_UPDATECONTACT:
			checkNotNull(params,
					"The contact passed as parameter should not be null");
			checkArgument(params.length == 1,
					"There should be exactly 1 contact to update");
			fgt = ContactFragment.newInstance(
					FragmentTags.TAG_FGT_UPDATECONTACT, (Contact) params[0]);
			resId = R.id.LayoutGuest;
			break;
		case TAG_FGT_IMPORTCONTACT:
			//fgt = parent.getFragmentManager().findFragmentByTag(tag.getValue());
			//if (null == fgt) fgt = ContactListFragment.newInstance();
			// We recreate a new one in order not to keep old imported contact checks
			fgt = ContactListFragment.newInstance();
			resId = R.id.LayoutGuest;
			break;
		case TAG_FGT_CREATETASK:
			fgt = TaskFragment.newInstance();
			resId = R.id.layoutTask;
			break;
		case TAG_FGT_TASKLIST:
			fgt = TaskListFragment.newInstance();
			resId = R.id.layoutTask;
			break;
		case TAG_FGT_UPDATETASK:
			checkNotNull(params,
					"The task passed in parameter should not be null");
			checkArgument(params.length == 1,
					"There should be exactly one task to update");
			fgt = TaskFragment.newInstance(tag, (Task) params[0]);
			resId = R.id.layoutTask;
			break;
		case TAG_FGT_CREATE_VENDOR:
			fgt = VendorFragment.newInstance();
			resId = R.id.LayoutVendor;
			break;
		case TAG_FGT_UPDATE_VENDOR:
			checkNotNull(params,
					"The vendor object passed in parameter should not be null");
			checkArgument(params.length == 1,
					"There should be exactly one vendor to update");
			fgt = VendorFragment.newInstance(tag, (Vendor) params[0]);
			resId = R.id.LayoutVendor;
			break;
		case TAG_FGT_VENDORLIST:
			// Reuse existing fragment if possible
			fgt = parent.getSupportFragmentManager().findFragmentByTag(tag.getValue());
			if (null == fgt) {
				Log.v(TAG, "Create new VendorListFragment instance");
				fgt = VendorListFragment.newInstance();
			} else {
				Log.v(TAG, "Reuse existing VendorListFragment instance");
				((VendorListFragment) fgt).refresh();
			}
			resId = R.id.LayoutVendor;
			break;
		case TAG_FGT_BUDGET_LIST:
			fgt = parent.getSupportFragmentManager().findFragmentByTag(tag.getValue());
			if (null == fgt) {
				fgt = BudgetListFragment.newInstance();
			} else {
				((BudgetListFragment) fgt).refresh();
			}
			resId = R.id.LayoutBudget;
			break;
		case TAG_FGT_BUDGET_PIE:
			fgt = parent.getSupportFragmentManager().findFragmentByTag(tag.getValue());
			if (null == fgt) {
				fgt = BudgetPieFragment.newInstance();
			} 
			resId = R.id.LayoutBudget;
			break;
		case TAG_FGT_CREATE_BUDGET:
			Log.v(TAG, "Create new BudgetFragment instance");
			fgt = BudgetFragment.newInstance();
			resId = R.id.LayoutBudget;
			break;
		case TAG_FGT_UPDATE_BUDGET:
			checkNotNull(params,
					"The budget object passed in parameter should not be null");
			checkArgument(params.length == 1,
					"There should be exactly one budget to update");
			fgt = BudgetFragment.newInstance(tag, (Budget) params[0]);
			resId = R.id.LayoutBudget;
			break;
		default:
			return;
		}
		tx.replace(resId, fgt, tag.getValue());
		tx.addToBackStack(tag.getValue());
		tx.commit();
	}

	/**
	 * Shows a dialog
	 * 
	 * @param parent
	 *            parent activity
	 * @param dialog
	 *            dialog window
	 * @param tag
	 *            tag of the fragment to display
	 */
	public static void showFragmentDialog(FragmentActivity parent,
			DialogFragment dialog, FragmentTags tag) {
		FragmentTransaction ft = parent.getSupportFragmentManager().beginTransaction();
		ft.addToBackStack(tag.getValue());
		dialog.show(ft, tag.getValue());
	}

	/**
	 * Hide the virtual keyboard if still open
	 * 
	 * @param activity
	 */
	public static void hideKeyboard(final Activity activity) {
		// Hide virtual keyboard if opened
		InputMethodManager inputManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * Gets default regional parameter
	 * 
	 * @return default Locale instance
	 */
	public static Locale getDefaultLocale() {
		return Locale.getDefault();
	}

	/**
	 * Format a number in the specified local currency
	 * 
	 * @param amount
	 * @param region
	 * @return
	 */
	public static String formatCurrency(final double amount, final Locale region) {
		NumberFormat fmt = NumberFormat.getCurrencyInstance(region);
		return fmt.format(amount);
	}

	/**
	 * Buil the Admob widget according to a debug parameter
	 * 
	 * @param debug
	 * @return
	 */
	public static AdRequest buildAdvert(final Context ctxt, boolean debug) {

		AdRequest.Builder builder = new AdRequest.Builder();
		if (debug) {
			builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(
					"4947D53B14805BCA0640BC7C7457AA07");
		}

		Bundle extras = new Bundle();
		// extras.putString("color_bg", "AAAAFF");
		// extras.putString("color_bg_top", "FFFFFF");
		extras.putString("color_border", "B7C3D0");
		// extras.putString("color_link", "000080");
		// extras.putString("color_text", "808080");
		// extras.putString("color_url", "008000");

		return builder
				.addKeyword(getStringResource(ctxt, R.string.ad_keyword))
				.setGender(AdRequest.GENDER_FEMALE)
				.addNetworkExtrasBundle(
						com.google.ads.mediation.admob.AdMobAdapter.class,
						extras).build();
	}
}
