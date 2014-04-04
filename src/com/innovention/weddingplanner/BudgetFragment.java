/**
 * 
 */
package com.innovention.weddingplanner;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.contentprovider.VendorContentProvider.Vendors;
import com.innovention.weddingplanner.dao.ConstantesDAO;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;

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
	private CursorAdapter adapter;

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
				// TODO Auto-generated method stub
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

		return view;
	}

}
