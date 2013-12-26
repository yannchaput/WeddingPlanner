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

import com.innovention.weddingplanner.bean.Contact;
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
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	private final static String TAG = ContactFragment.class.getSimpleName();

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	// Subscribe to validate button event
	private OnValidateContactListener mListener;
	private OnClickListener validateBtnListener = new OnClickListener() {
		public void onClick(View v) {
			Log.v(TAG, "Click on validate button");

			// Create a "contact" bean object
			Contact bean = new Contact.ContactBuilder()
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
				mListener.onValidateContact(bean);
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
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment ContactFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static ContactFragment newInstance(String param1, String param2) {
		ContactFragment fragment = new ContactFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public static ContactFragment newInstance() {
		ContactFragment fragment = new ContactFragment();
		return fragment;
	}

	public ContactFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_contact, container, false);
		Button validateBtn = (Button) v.findViewById(R.id.contactButtonSave);
		validateBtn.setOnClickListener(validateBtnListener);
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
		void onValidateContact(IDtoBean bean);
	}

}
