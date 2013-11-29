package com.innovention.weddingplanner;


import com.innovention.weddingplanner.ContactFragment.OnValidateContactListener;
import com.innovention.weddingplanner.GuestListFragment.OnGuestSelectedListener;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.IDtoBean;

import static com.innovention.weddingplanner.Constantes.*;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class GuestActivity extends Activity implements OnGuestSelectedListener, OnValidateContactListener {
	
	private final static String TAG = ContactFragment.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guest);
		// Show the Up button in the action bar.
		setupActionBar();
		// Add list guest fragment
		getFragmentManager().beginTransaction().add(R.id.LayoutGuest, GuestListFragment.newInstance(), TAG_FGT_GUESTLIST).commit();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guest, menu);
		return true;
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_add_contact:
			
			if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.d(TAG, "User action : create new contact -> open the mask");
			// Create contact action
			Fragment contactFragment = ContactFragment.newInstance();
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			tx.replace(R.id.LayoutGuest, contactFragment, TAG_FGT_CREATECONTACT);
			tx.addToBackStack(null);
			tx.commit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSelectGuest(String id) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Triggered when a contact was validated from ContactFragment
	 */
	@Override
	public void onValidateContact(IDtoBean bean) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Contact about to save: " + bean);
	}

}
