/**
 * 
 */
package com.innovention.weddingplanner;

import org.apache.commons.lang3.math.NumberUtils;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.VendorFragment.OnValidateVendor;
import com.innovention.weddingplanner.contentprovider.DBContentProvider.Budget;
import com.innovention.weddingplanner.contentprovider.DBContentProvider.Vendors;
import com.innovention.weddingplanner.dao.ConstantesDAO;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.Spinner;
import android.widget.TextView;

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

	// AutoCompleteTextView adapter
	private SimpleCursorAdapter adapter;
	
	// Widgets
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
		AutoCompleteTextView textView = (AutoCompleteTextView) view
				.findViewById(R.id.budgetMultiAutoCompleteContact);
		textView.setAdapter(adapter);

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
		
		vendorTxt = (AutoCompleteTextView) view.findViewById(R.id.budgetMultiAutoCompleteContact);
		categorySpinner = (Spinner) view.findViewById(R.id.budgetSpinnerCategory);
		totalTxt = (EditText) view.findViewById(R.id.budgetEditTotalAmount);
		paidTxt = (EditText) view.findViewById(R.id.budgetEditPaidAmount);
		noteTxt = (EditText) view.findViewById(R.id.budgetEditNote);
		
		Button validateBtn = (Button) view.findViewById(R.id.budgetButtonSave);
		validateBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "validateBtn - onClick : save new budget item");
				ContentValues values = new ContentValues();
				values.put (ConstantesDAO.COL_BUDGET_VENDOR, vendorTxt.getText().toString());
				values.put(ConstantesDAO.COL_BUDGET_CATEGORY, (String) categorySpinner.getSelectedItem());
				values.put(ConstantesDAO.COL_BUDGET_TOTAL_AMOUNT, NumberUtils.toDouble(totalTxt.getText().toString()));
				values.put(ConstantesDAO.COL_BUDGET_PAID_AMOUNT, NumberUtils.toDouble(paidTxt.getText().toString()));
				values.put(ConstantesDAO.COL_BUDGET_NOTE, noteTxt.getText().toString());
				validateListener.onValidateBudget(values, BudgetFragment.this.mode);
			}
		});

		return view;
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
