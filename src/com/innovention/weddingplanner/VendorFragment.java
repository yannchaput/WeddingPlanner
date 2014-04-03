/**
 * 
 */
package com.innovention.weddingplanner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showAlert;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.Vendor;
import com.innovention.weddingplanner.bean.Vendor.Builder;
import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

/**
 * Template for a new vendor
 * 
 * @author YCH
 * 
 */
public class VendorFragment extends Fragment {

	private static final String TAG = VendorFragment.class.getSimpleName();
	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_CREATE_VENDOR;
	// Parameter bean
	private Vendor bean;

	// Fields
	private Button validateBtn;
	private EditText companyTxt;
	private EditText contactTxt;
	private EditText addressTxt;
	private EditText phoneTxt;
	private EditText mailTxt;
	private Spinner spinner;
	private EditText noteTxt;

	// Listeners
	private OnValidateVendor validateListener;

	interface OnValidateVendor {
		void onValidateVendor(final Vendor vendor, FragmentTags source);
	}

	/**
	 * Default constructor
	 */
	public VendorFragment() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Plain factory
	 * 
	 * @return a vendor fragment instance
	 */
	public static VendorFragment newInstance() {
		Log.v(TAG, "newInstance");
		VendorFragment fgt = new VendorFragment();
		fgt.mode = FragmentTags.TAG_FGT_CREATE_VENDOR;
		return fgt;
	}

	/**
	 * Parameterized factory
	 * 
	 * @return
	 */
	public static VendorFragment newInstance(FragmentTags tag, final Vendor bean) {
		checkArgument(FragmentTags.TAG_FGT_UPDATE_VENDOR.equals(tag),
				"This constructor requires the tag update vendor");
		checkNotNull(bean,
				"This constructore requires a non null object in parameter");
		checkArgument(bean.getId() > 0, "No id specified");
		VendorFragment fgt = newInstance();
		fgt.mode = tag;
		fgt.bean = bean;
		return fgt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_vendor_form, container,
				false);
		validateBtn = (Button) view.findViewById(R.id.vendorButtonSave);
		companyTxt = (EditText) view.findViewById(R.id.vendorEditCompanyName);
		contactTxt = (EditText) view.findViewById(R.id.vendorEditContactName);
		addressTxt = (EditText) view
				.findViewById(R.id.vendorEditCompanyAddress);
		phoneTxt = (EditText) view.findViewById(R.id.vendorEditCompanyPhone);
		mailTxt = (EditText) view.findViewById(R.id.vendorEditCompanyMail);
		spinner = (Spinner) view.findViewById(R.id.vendorSpinnerCategory);
		noteTxt = (EditText) view.findViewById(R.id.vendorEditCompanyNote);

		if (FragmentTags.TAG_FGT_UPDATE_VENDOR.equals(mode) && bean != null) {
			updateFields(bean);
		}

		validateBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Button save - OnClickListener");

				Builder builder = new Vendor.Builder()
						.addCompany(companyTxt.getText().toString())
						.addContact(contactTxt.getText().toString())
						.addAddress(addressTxt.getText().toString())
						.addTelephone(phoneTxt.getText().toString())
						.addMail(mailTxt.getText().toString())
						.addNote(noteTxt.getText().toString())
						.addCategory((String) spinner.getSelectedItem());
				
				if (FragmentTags.TAG_FGT_UPDATE_VENDOR.equals(mode)) {
				// Set id to update the right record
					builder.addId(bean!=null ? bean.getId() : -1);
				}
				
				Vendor bean = builder.build();

				try {
					bean.validate(VendorFragment.this.getActivity());
					validateListener.onValidateVendor(bean,
							VendorFragment.this.mode);
				} catch (MissingMandatoryFieldException e) {
					showAlert(R.string.vendor_alert_dialog_title,
							R.string.vendor_mandatory_validator_message,
							getFragmentManager());
				} catch (IncorrectTelephoneException e) {
					showAlert(R.string.vendor_alert_dialog_title,
							R.string.phone_validator_message,
							getFragmentManager());
				} catch (IncorrectMailException e) {
					showAlert(R.string.vendor_alert_dialog_title,
							R.string.email_validator_message,
							getFragmentManager());
				}

			}
		});
		return view;
	}

	/**
	 * Retrieve the values of the vendor object and update fields accordingly
	 * 
	 * @param id
	 *            object db id
	 */
	@SuppressWarnings("unchecked")
	private void updateFields(final Vendor bean) {
		companyTxt.setText(bean.getCompanyName());
		contactTxt.setText(bean.getContactDetails());
		addressTxt.setText(bean.getAddress());
		phoneTxt.setText(bean.getTelephone());
		mailTxt.setText(bean.getMail());
		noteTxt.setText(bean.getNote());
		spinner.setSelection(((ArrayAdapter<String>) spinner.getAdapter()).getPosition(bean.getCategory()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			validateListener = (OnValidateVendor) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnValidateVendor");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		validateListener = null;
		super.onDetach();
	}

}
