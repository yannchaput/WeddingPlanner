/**
 * 
 */
package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.showAlert;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.validateMandatory;

import org.apache.commons.lang3.math.NumberUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Spinner;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.bean.Budget;
import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.contentprovider.DBContentProvider.Vendors;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.exception.InconsistentFieldException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

/**
 * A fragment that displays a form to create or update a new budget item
 * 
 * @author YCH
 * 
 */
public final class BudgetFragment extends Fragment {

	// Log
	private static final String TAG = BudgetFragment.class.getSimpleName();

	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_CREATE_BUDGET;
	
	private Budget bean;

	// AutoCompleteTextView adapter
	private SimpleCursorAdapter adapter;
	
	// Widgets
	private EditText captionTxt;
	private AutoCompleteTextView vendorTxt;
	private Spinner categorySpinner;
	private EditText totalTxt;
	private EditText paidTxt;
	private EditText noteTxt;
	
	private OnValidateBudget validateListener;
	
	interface OnValidateBudget {
		void onValidateBudget(ContentValues values, FragmentTags source);
	}

	/**
	 * Default constructor
	 */
	public BudgetFragment() {
	}

	/**
	 * Default factory
	 * 
	 * @return a new instance of the fragment
	 */
	public static BudgetFragment newInstance() {
		return new BudgetFragment();
	}
	
	public static BudgetFragment newInstance(FragmentTags mode, final Budget bean) {
		BudgetFragment instance = BudgetFragment.newInstance();
		instance.bean = bean;
		instance.mode = mode;
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_budget_form, container,
				false);
		
		// Set up AutoCompleteView
		Cursor c = getActivity().getContentResolver().query(
				Vendors.CONTENT_URI,
				new String[] { ConstantesDAO.COL_ID,
						ConstantesDAO.COL_VENDOR_COMPANY }, null, null, null);

		adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_dropdown_item_1line, null,
				new String[] { ConstantesDAO.COL_VENDOR_COMPANY },
				new int[] { android.R.id.text1 }, 0);
		vendorTxt = (AutoCompleteTextView) view
				.findViewById(R.id.budgetMultiAutoCompleteContact);
		vendorTxt.setAdapter(adapter);

		// Triggered every time a change occured in the text view
		adapter.setFilterQueryProvider(new FilterQueryProvider() {

			@Override
			public Cursor runQuery(CharSequence constraint) {
				return getCursor(constraint);
			}

			private Cursor getCursor(CharSequence str) {
				StringBuilder select = new StringBuilder().append(
						ConstantesDAO.COL_VENDOR_COMPANY).append(" LIKE ?");
				String[] selectArgs = { "%" + str + "%" };
				Cursor c = getActivity().getContentResolver().query(
						Vendors.CONTENT_URI,
						new String[] { ConstantesDAO.COL_ID,
								ConstantesDAO.COL_VENDOR_COMPANY },
						select.toString(), selectArgs, null);
				return c;
			}
		});
		
		// Set a converter from cursor item to String
		adapter.setCursorToStringConverter(new CursorToStringConverter() {
			
			@Override
			public CharSequence convertToString(Cursor cursor) {
				int index = cursor.getColumnIndex(ConstantesDAO.COL_VENDOR_COMPANY);
				return cursor.getString(index);
			}
		});
		
		captionTxt = (EditText) view.findViewById(R.id.budgetEditCaption);
		categorySpinner = (Spinner) view.findViewById(R.id.budgetSpinnerCategory);
		totalTxt = (EditText) view.findViewById(R.id.budgetEditTotalAmount);
		paidTxt = (EditText) view.findViewById(R.id.budgetEditPaidAmount);
		noteTxt = (EditText) view.findViewById(R.id.budgetEditNote);
		
		Button validateBtn = (Button) view.findViewById(R.id.budgetButtonSave);
		validateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "validateBtn - onClick : save new budget item");
				String caption = captionTxt.getText().toString();
				String vendor = vendorTxt.getText().toString();
				String total = totalTxt.getText().toString();
				String paid = paidTxt.getText().toString();
				double dTotal = NumberUtils.toDouble(total);
				double dPaid = NumberUtils.toDouble(paid);
				try {
					validateMandatory(caption);
					validateMandatory(vendor);
					validateMandatory(total);
					
					if (dPaid > dTotal) {
						throw new InconsistentFieldException(Constantes.INCONSISTENT_FIELD_EX);
					}

					ContentValues values = new ContentValues();
					
					// Pass the id to activity to identify the record to update
					if (FragmentTags.TAG_FGT_UPDATE_BUDGET.equals(mode)) {
						values.put(ConstantesDAO.COL_ID, bean.getId());
					}
					
					values.put(ConstantesDAO.COL_BUDGET_EXPENSE, caption);
					values.put(ConstantesDAO.COL_BUDGET_VENDOR, vendor);
					values.put(ConstantesDAO.COL_BUDGET_CATEGORY,
							(String) categorySpinner.getSelectedItem());
					values.put(ConstantesDAO.COL_BUDGET_TOTAL_AMOUNT,
							dTotal);
					values.put(ConstantesDAO.COL_BUDGET_PAID_AMOUNT,
							dPaid);
					values.put(ConstantesDAO.COL_BUDGET_NOTE, noteTxt.getText()
							.toString());
					validateListener.onValidateBudget(values,
							BudgetFragment.this.mode);
				} catch (MissingMandatoryFieldException e) {
					showAlert(R.string.budget_alert_dialog_title,
							R.string.budget_mandatory_validator_message,
							getFragmentManager());
				} catch (InconsistentFieldException e) {
					showAlert(R.string.budget_alert_dialog_title,
							R.string.inconsistent_paid_amount,
							getFragmentManager());
				}
			}
		});
		
		if (FragmentTags.TAG_FGT_UPDATE_BUDGET.equals(mode)) {
			updateFields(this.bean);
		}

		return view;
	}
	
	@SuppressWarnings("unchecked")
	private void updateFields(final Budget budget) {
		captionTxt.setText(budget.getDescription());
		vendorTxt.setText(budget.getVendor());
		categorySpinner.setSelection(((ArrayAdapter<String>) categorySpinner.getAdapter()).getPosition(budget.getCategory()));
		totalTxt.setText(String.valueOf(budget.getAmount()));
		paidTxt.setText(String.valueOf(budget.getPaid()));
		noteTxt.setText(budget.getNote());
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
			validateListener = (OnValidateBudget) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnValidateBudget");
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
