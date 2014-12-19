package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showAlert;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showFragmentDialog;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.ContactFragment.OnValidateContactListener;
import com.innovention.weddingplanner.GuestListFragment.OnGuestSelectedListener;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.bean.WeddingInfo;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.GuestsDao;
import com.innovention.weddingplanner.dao.WeddingInfoDao;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

public class GuestActivity extends FragmentActivity implements
		OnGuestSelectedListener, OnValidateContactListener {

	private final static String TAG = GuestActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		setContentView(R.layout.activity_guest);
		Fragment fgt = ObjectUtils.defaultIfNull(getSupportFragmentManager().findFragmentByTag(FragmentTags.TAG_FGT_GUESTPAGER.getValue()),GuestPagerFragment.newInstance());
		if (!fgt.isAdded()) {
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.LayoutGuest, fgt,
						FragmentTags.TAG_FGT_GUESTPAGER.getValue()).commit();
		}
		// Show the Up button in the action bar.

	}

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
			Log.d(TAG, "onOptionsItemSelected - Back to home page");
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_add_contact:

			Log.d(TAG,
					"User action : create new contact -> open the option list");
			showFragmentDialog(this, ContactDialogFragment.newInstance(),
					FragmentTags.TAG_FGT_GUEST_ADD_ACTION);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called when an item is clicked on the fragment list
	 * 
	 * @param id
	 *            db id of the selected item
	 * @param action
	 *            action (update/delete)
	 */
	@Override
	public void onSelectGuest(long id, final FragmentTags action) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSelectGuest - Selected item with id:" + id);
		GuestsDao dao = DaoLocator.getInstance(getApplication()).get(
				SERVICES.GUEST);

		if (FragmentTags.TAG_FGT_UPDATECONTACT.equals(action)) {
			Contact selectedGuest = dao.get(id);
			Log.v(TAG, "onSelectGuest - Guest to update is " + selectedGuest);
			// replaceFragment(this, FragmentTags.TAG_FGT_UPDATECONTACT,
			// selectedGuest);
			Intent intent = new Intent(this, ContactFormContainerActivity.class);
			intent.setAction(FragmentTags.TAG_FGT_UPDATECONTACT.getValue());
			intent.putExtra(Contact.KEY_INTENT_SELECTED_GUEST, selectedGuest);
			startActivity(intent);
		} else if (FragmentTags.TAG_FGT_DELETECONTACT.equals(action)) {
			Log.v(TAG, "onSelectGuest - Suppress guest with id " + id);
			int count = dao.removeWithId((int) id);
			if (count > 1)
				showAlert(R.string.delete_guest_alert_dialog_title,
						R.string.delete_guest_multiple_alert_message,
						getSupportFragmentManager());
			else if (count == 0) {
				showAlert(R.string.delete_guest_alert_dialog_title,
						R.string.delete_guest_0_alert_message,
						getSupportFragmentManager());
			} else {
				showAlert(R.string.delete_guest_OK_alert_dialog_title,
						R.string.delete_guest_OK_alert_message,
						getSupportFragmentManager());
			}
		}
	}

	/**
	 * Performs a phone call to the selected guest
	 */
	@Override
	public void onCallGuest(long id) {

		Log.d(TAG, "onCallGuest - Call contact with id:" + id);
		GuestsDao dao = DaoLocator.getInstance(getApplication()).get(
				SERVICES.GUEST);
		Contact selectedGuest = dao.get(id);
		String phone = selectedGuest.getTelephone();
		Log.v(TAG, "Call contact with phone number " + phone);
		if (!WeddingPlannerHelper.isEmpty(phone)) {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + phone));
			startActivity(callIntent);
		} else
			Toast.makeText(this, R.string.call_guest_alert_message,
					Toast.LENGTH_SHORT).show();
	}

	/**
	 * Sends a mail to the selected guest
	 * 
	 * @param id
	 *            id of the contact guest
	 */
	@Override
	public void onMailGuest(long id) {
		Log.d(TAG, "onMailGuest - Mail contact with id:" + id);
		GuestsDao dao = DaoLocator.getInstance(getApplication()).get(
				SERVICES.GUEST);
		WeddingInfoDao infoDao = DaoLocator.getInstance(getApplication()).get(
				SERVICES.INFO);
		Contact selectedGuest = dao.get(id);
		WeddingInfo info = infoDao.get();
		DateTimeFormatter fmt = DateTimeFormat.fullDate();
		String weddingDate = (info != null ? info.getWeddingDate()
				.toString(fmt) : "XXX");
		String mail = selectedGuest.getMail();
		Log.v(TAG, "Send mail to contact with email " + mail);
		if (!WeddingPlannerHelper.isEmpty(mail)) {
			Intent email = new Intent(Intent.ACTION_SEND);
			email.putExtra(Intent.EXTRA_EMAIL, new String[] { mail });
			email.putExtra(Intent.EXTRA_SUBJECT, WeddingPlannerHelper
					.getStringResource(this, R.string.contact_mail_subject));
			email.putExtra(Intent.EXTRA_TEXT,
					this.getString(R.string.contact_mail_message, weddingDate));
			email.setType("message/rfc822");
			startActivity(Intent.createChooser(email, WeddingPlannerHelper
					.getStringResource(this, R.string.contact_mail_provider)));
		} else
			Toast.makeText(this, R.string.mail_guest_alert_message,
					Toast.LENGTH_SHORT).show();

	}

	/**
	 * Triggered when a contact was validated from ContactFragment and needs
	 * saving
	 */
	@Override
	public void onValidateContact(IDtoBean bean, String tag) {
		// TODO Auto-generated method stub

		// Called from ContactListFragment during import
		Preconditions.checkNotNull(bean,
				"Contact bean can't be null on validation");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(tag),
				"Fragment tag passed is empty which is incorrect");

		GuestsDao dao = DaoLocator.getInstance(getApplication()).get(
				SERVICES.GUEST);

		Log.d(TAG, "Save contact in creation mode: " + bean);
		dao.insert((Contact) bean);

		Log.d(TAG, "Contact saved: " + bean);
	}

}
