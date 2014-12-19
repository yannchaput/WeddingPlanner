package com.innovention.weddingplanner;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.hideKeyboard;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.replaceFragment;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.ContactFragment.OnValidateContactListener;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.GuestsDao;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.ContactFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class ContactFormContainerActivity extends FragmentActivity implements OnValidateContactListener{

	private final static String TAG = ContactFragment.class.getSimpleName();
	
	public ContactFormContainerActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_form);
		final Intent from = getIntent();
		if (FragmentTags.TAG_FGT_CREATECONTACT.getValue().equals(from.getAction()))
		 getSupportFragmentManager()
		 .beginTransaction()
		 .add(R.id.LayoutContactFormContainer, ContactFragment.newInstance(),
				 FragmentTags.TAG_FGT_CREATECONTACT.getValue()).commit();
		else if (FragmentTags.TAG_FGT_UPDATECONTACT.getValue().equals(from.getAction())) {
			Contact contact = from.getExtras().getParcelable(Contact.KEY_INTENT_SELECTED_GUEST);
			getSupportFragmentManager()
			 .beginTransaction()
			 .add(R.id.LayoutContactFormContainer, ContactFragment.newInstance(FragmentTags.TAG_FGT_UPDATECONTACT, contact),
					 FragmentTags.TAG_FGT_UPDATECONTACT.getValue()).commit();
		}
		
	}
	
	/**
	 * Triggered when a contact was validated from ContactFragment and needs
	 * saving
	 */
	@Override
	public void onValidateContact(IDtoBean bean, String tag) {
		// TODO Auto-generated method stub

		Preconditions.checkNotNull(bean,
				"Contact bean can't be null on validation");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(tag),
				"Fragment tag passed is empty which is incorrect");

		GuestsDao dao = DaoLocator.getInstance(getApplication()).get(
				SERVICES.GUEST);

		if (FragmentTags.TAG_FGT_CREATECONTACT.getValue().equals(tag)) {
			Log.d(TAG, "Save contact in creation mode: " + bean);
			dao.insert((Contact) bean);
		} else if (FragmentTags.TAG_FGT_UPDATECONTACT.getValue().equals(tag)) {
			Log.d(TAG, "Save contact in update mode: " + bean);
			Preconditions.checkArgument(bean.getId() > 0,
					"Invalid db id for contact");
			dao.update(bean.getId(), (Contact) bean);
		}
		Intent guestActivityIntent = new Intent(this, GuestActivity.class);
		startActivity(guestActivityIntent);
		
		Log.d(TAG, "Contact saved: " + bean);
	}

}
