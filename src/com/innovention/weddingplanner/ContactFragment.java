package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.findView;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showAlert;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.bean.Contact.ResponseType;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link ContactFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class ContactFragment extends Fragment {

	// Bundle constantes key
	private static final String ARG_ID = "id";
	private static final String ARG_SURNAME = "surname";
	private static final String ARG_NAME = "name";
	private static final String ARG_TEL = "telephone";
	private static final String ARG_EMAIL = "email";
	private static final String ARG_ADDRESS = "address";
	private static final String ARG_INVITATION = "invitation";
	private static final String ARG_CHURCH = "church";
	private static final String ARG_TOWNHALL = "townhall";
	private static final String ARG_COCKTAIL = "cocktail";
	private static final String ARG_PARTY = "party";
	private static final String ARG_RSVP_ATTEND = "rsvp_attend";
	private static final String ARG_RSVP_NOTATTEND = "rsvp_NotAttend";
	private static final String ARG_RSVP_PENDING = "rsvp_pending";

	private final static String TAG = ContactFragment.class.getSimpleName();

	// Bundle parameters
	private int mId = -1;
	private String mSurname;
	private String mName;
	private String mTelephone;
	private String mEmail;
	private String mAddress;
	private Boolean mInvitation;
	private Boolean mChurch;
	private Boolean mTownHall;
	private Boolean mCocktail;
	private Boolean mParty;
	private Boolean mRsvpAttend = Boolean.FALSE;
	private Boolean mRsvpNotAttend = Boolean.FALSE;
	private Boolean mRsvpPending = Boolean.FALSE;

	// Display mode
	private FragmentTags mode;

	// Subscribe to validate button event
	private OnValidateContactListener mListener;
	private OnClickListener validateBtnListener = new OnClickListener() {
		public void onClick(View v) {
			Log.v(TAG, "Click on validate button");

			// Create a "contact" bean object
			Contact bean = new Contact.ContactBuilder()
					.withId(mId)
					.surname(
							((EditText) findView(ContactFragment.this,
									R.id.contactEditSurname)).getText()
									.toString())
					.name(((EditText) findView(ContactFragment.this,
							R.id.contactEditName)).getText().toString())
					.telephone(
							((EditText) findView(ContactFragment.this,
									R.id.contactEditPhone)).getText()
									.toString())
					.mail(((EditText) findView(ContactFragment.this,
							R.id.contactEditMail)).getText().toString())
					.address(
							((EditText) findView(ContactFragment.this,
									R.id.contactEditAdress)).getText()
									.toString())
					.inviteSent(
							((CheckBox) findView(ContactFragment.this,
									R.id.contactcheckBoxFairePart)).isChecked())
					.atChurch(
							((CheckBox) findView(ContactFragment.this,
									R.id.contactCheckBoxChurch)).isChecked())
					.atTownHall(
							((CheckBox) findView(ContactFragment.this,
									R.id.contactCheckBoxHall)).isChecked())
					.AtCocktail(
							((CheckBox) findView(ContactFragment.this,
									R.id.contactCheckBoxCocktail)).isChecked())
					.AtParty(
							((CheckBox) findView(ContactFragment.this,
									R.id.contactCheckBoxReception)).isChecked())
					.answerPending(
							((RadioButton) findView(ContactFragment.this,
									R.id.contactRadioNoAnswer)).isChecked())
					.answerAttend(
							((RadioButton) findView(ContactFragment.this,
									R.id.contactRadioAttend)).isChecked())
					.answerNotAttend(
							((RadioButton) findView(ContactFragment.this,
									R.id.contactRadioAbsent)).isChecked())
					.build();
			// TODO Make a helper out of it
			Log.d(TAG, "Build contact object" + bean);
			try {
				bean.validate();
				mListener
						.onValidateContact(bean, ContactFragment.this.getTag());
			} catch (MissingMandatoryFieldException e) {
				showAlert(R.string.contact_alert_dialog_title,
						R.string.mandatory_validator_message,
						getFragmentManager());
			} catch (IncorrectMailException e) {
				showAlert(R.string.contact_alert_dialog_title,
						R.string.email_validator_message, getFragmentManager());
			} catch (IncorrectTelephoneException e) {
				showAlert(R.string.contact_alert_dialog_title,
						R.string.phone_validator_message, getFragmentManager());
			}
		}
	};

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param mode
	 *            edit mode.
	 * @param guest
	 *            guest bean (in edit mode, null otherwise).
	 * @return A new instance of fragment ContactFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ContactFragment newInstance(final FragmentTags mode,
			final Contact guest) {
		ContactFragment fragment = new ContactFragment();

		fragment.mode = mode;

		if ((guest != null)
				&& (FragmentTags.TAG_FGT_UPDATECONTACT.equals(mode))) {
			Log.d(TAG, "newInstance - pass parameter contact : " + guest);

			Bundle args = new Bundle();
			args.putInt(ARG_ID, guest.getId());
			args.putString(ARG_SURNAME, guest.getSurname());
			args.putString(ARG_NAME, guest.getName());
			args.putString(ARG_TEL, guest.getTelephone());
			args.putString(ARG_EMAIL, guest.getMail());
			args.putString(ARG_ADDRESS, guest.getAddress());
			args.putBoolean(ARG_INVITATION, guest.getInviteSent());
			args.putBoolean(ARG_CHURCH, guest.getChurch());
			args.putBoolean(ARG_COCKTAIL, guest.getCocktail());
			args.putBoolean(ARG_TOWNHALL, guest.getTownHall());
			args.putBoolean(ARG_PARTY, guest.getParty());

			ResponseType response = guest.getResponse();
			switch (response) {
			case ATTEND:
				args.putBoolean(ARG_RSVP_ATTEND, Boolean.TRUE);
				args.putBoolean(ARG_RSVP_NOTATTEND, Boolean.FALSE);
				args.putBoolean(ARG_RSVP_PENDING, Boolean.FALSE);
				break;
			case NOTATTEND:
				args.putBoolean(ARG_RSVP_ATTEND, Boolean.FALSE);
				args.putBoolean(ARG_RSVP_NOTATTEND, Boolean.TRUE);
				args.putBoolean(ARG_RSVP_PENDING, Boolean.FALSE);
				break;
			case PENDING:
				args.putBoolean(ARG_RSVP_ATTEND, Boolean.FALSE);
				args.putBoolean(ARG_RSVP_NOTATTEND, Boolean.FALSE);
				args.putBoolean(ARG_RSVP_PENDING, Boolean.TRUE);
				break;
			}

			fragment.setArguments(args);
		}
		return fragment;
	}

	/**
	 * Default factory method mode should always be create in this case
	 * 
	 * @return
	 */
	public static ContactFragment newInstance() {
		ContactFragment fragment = new ContactFragment();
		fragment.mode = FragmentTags.TAG_FGT_CREATECONTACT;
		return fragment;
	}

	public ContactFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set fields from bundle
		if ((getArguments() != null)
				&& (FragmentTags.TAG_FGT_UPDATECONTACT.equals(mode))) {
			Log.d(TAG, "onCreate - restore bundle : " + getArguments());

			mId = getArguments().getInt(ARG_ID);
			mSurname = getArguments().getString(ARG_SURNAME);
			mName = getArguments().getString(ARG_NAME);
			mTelephone = getArguments().getString(ARG_TEL);
			mEmail = getArguments().getString(ARG_EMAIL);
			mAddress = getArguments().getString(ARG_ADDRESS);
			mInvitation = getArguments().getBoolean(ARG_INVITATION);
			mChurch = getArguments().getBoolean(ARG_CHURCH);
			mCocktail = getArguments().getBoolean(ARG_COCKTAIL);
			mTownHall = getArguments().getBoolean(ARG_TOWNHALL);
			mParty = getArguments().getBoolean(ARG_PARTY);
			mRsvpAttend = getArguments().getBoolean(ARG_RSVP_ATTEND);
			mRsvpNotAttend = getArguments().getBoolean(ARG_RSVP_NOTATTEND);
			mRsvpPending = getArguments().getBoolean(ARG_RSVP_PENDING);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_contact, container, false);
		Button validateBtn = (Button) v.findViewById(R.id.contactButtonSave);
		validateBtn.setOnClickListener(validateBtnListener);

		if (FragmentTags.TAG_FGT_UPDATECONTACT.equals(mode)) {
			// Set fields in the form in case of update
			((EditText) v.findViewById(R.id.contactEditSurname))
					.setText(mSurname);
			((EditText) v.findViewById(R.id.contactEditName)).setText(mName);
			((EditText) v.findViewById(R.id.contactEditPhone))
					.setText(mTelephone);
			((EditText) v.findViewById(R.id.contactEditMail)).setText(mEmail);
			((EditText) v.findViewById(R.id.contactEditAdress))
					.setText(mAddress);

			((CheckBox) v.findViewById(R.id.contactcheckBoxFairePart))
					.setChecked(mInvitation.booleanValue());
			((CheckBox) v.findViewById(R.id.contactCheckBoxChurch))
					.setChecked(mChurch.booleanValue());
			((CheckBox) v.findViewById(R.id.contactCheckBoxCocktail))
					.setChecked(mCocktail.booleanValue());
			((CheckBox) v.findViewById(R.id.contactCheckBoxHall))
					.setChecked(mTownHall.booleanValue());
			((CheckBox) v.findViewById(R.id.contactCheckBoxReception))
					.setChecked(mParty.booleanValue());

			((RadioButton) v.findViewById(R.id.contactRadioAbsent))
					.setChecked(mRsvpNotAttend.booleanValue());
			((RadioButton) v.findViewById(R.id.contactRadioAttend))
					.setChecked(mRsvpAttend.booleanValue());
			((RadioButton) v.findViewById(R.id.contactRadioNoAnswer))
					.setChecked(mRsvpPending.booleanValue());
		}

		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnValidateContactListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		validateBtnListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnValidateContactListener {
		// TODO: Update argument type and name
		void onValidateContact(IDtoBean bean, String tag);
	}

}
