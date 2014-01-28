package com.innovention.weddingplanner;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.*;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Checkable;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.innovention.weddingplanner.ContactFragment.OnValidateContactListener;
import com.innovention.weddingplanner.GuestListFragment.OnGuestSelectedListener;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.GuestsDao;
import com.innovention.weddingplanner.dao.IDao;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;

public class GuestActivity extends Activity implements OnGuestSelectedListener, OnValidateContactListener {
	
	private final static String TAG = ContactFragment.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest);
		// Show the Up button in the action bar.
		setupActionBar();
		// Add list guest fragment
		getFragmentManager().beginTransaction().add(R.id.LayoutGuest, GuestListFragment.newInstance(), FragmentTags.TAG_FGT_GUESTLIST.getValue()).commit();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.guest, menu);
//		return true;
//	}

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
			
			if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.d(TAG, "User action : create new contact -> open the mask");
			// Create contact action
			replaceFragment(this,FragmentTags.TAG_FGT_CREATECONTACT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called when an item is clicked on the fragment list
	 * @param id db id of the selected item
	 * @param action action (update/delete)
	 */
	@Override
	public void onSelectGuest(long id, final FragmentTags action) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onSelectGuest - Selected item with id:" + id);
		GuestsDao dao = (GuestsDao) DaoLocator.getInstance(getApplication())
				.get(SERVICES.GUEST);
		
		if (FragmentTags.TAG_FGT_UPDATECONTACT.equals(action)) {
			Contact selectedGuest = dao.get(id);
			Log.v(TAG, "onSelectGuest - Guest to update is " + selectedGuest);
			replaceFragment(this,FragmentTags.TAG_FGT_UPDATECONTACT, selectedGuest);
		}
		else if (FragmentTags.TAG_FGT_DELETECONTACT.equals(action)) {
			Log.v(TAG, "onSelectGuest - Suppress guest with id " + id);
			int count = dao.removeWithId((int) id);
			if (count > 1)
				showAlert(R.string.delete_guest_alert_dialog_title,
						R.string.delete_guest_multiple_alert_message,
						getFragmentManager());
			else if (count == 0){
				showAlert(R.string.delete_guest_alert_dialog_title,
						R.string.delete_guest_0_alert_message,
						getFragmentManager());
			}
			else {
				showAlert(R.string.delete_guest_alert_dialog_title,
						R.string.delete_guest_OK_alert_message,
						getFragmentManager());
			}
		}
	}

	/**
	 * Triggered when a contact was validated from ContactFragment
	 * and needs saving
	 */
	@Override
	public void onValidateContact(IDtoBean bean, String tag) {
		// TODO Auto-generated method stub
		
		Preconditions.checkNotNull(bean, "Contact bean can't be null on validation");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(tag), "Fragment tag passed is empty which is incorrect");
		
		GuestsDao dao = (GuestsDao) DaoLocator
				.getInstance(getApplication()).get(SERVICES.GUEST);
		
		if (FragmentTags.TAG_FGT_CREATECONTACT.getValue().equals(tag)) {
			Log.d(TAG, "Save contact in creation mode: " + bean);
			dao.insert((Contact) bean);		
		}
		else if (FragmentTags.TAG_FGT_UPDATECONTACT.getValue().equals(tag)) {
			Log.d(TAG, "Save contact in update mode: " + bean);
			Preconditions.checkArgument(bean.getId() > 0, "Invalid db id for contact");
			dao.update(bean.getId(), (Contact) bean); 
		}
		// Replace add contact fragment by list fragment
		replaceFragment(this,FragmentTags.TAG_FGT_GUESTLIST);
		Log.d(TAG, "Contact saved: " + bean);
	}

}
