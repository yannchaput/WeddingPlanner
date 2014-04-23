/**
 * 
 */
package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_EXPENSE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_CATEGORY;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_VENDOR;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_TOTAL_AMOUNT;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_BUDGET_PAID_AMOUNT;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.formatCurrency;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.getDefaultLocale;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.contentprovider.DBContentProvider;
import com.innovention.weddingplanner.contentprovider.DBContentProvider.Budget;
import com.innovention.weddingplanner.contentprovider.DBContentProvider.Vendors;
import com.innovention.weddingplanner.dao.ConstantesDAO;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

/**
 * A fragment that displays a list of the current expenses grouped by vendor categories
 * The footer display the balance, the outstanding balance and the paid amount
 * @author YCH
 *
 */
public final class BudgetListFragment extends Fragment implements LoaderCallbacks<Cursor>, Refreshable {
	
	// Log
	private static final String TAG =  BudgetListFragment.class.getSimpleName();
	
	// LoaderManager id
	private static final byte LOADER_ID = 0;
	
	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_BUDGET_LIST;
	
	// Adapter
	private ExpAdapter expListAdapter;

	// List view
	private ExpandableListView expListView;
	
	/**
	 * Adapter for Expandable list
	 * 
	 * @author YCH
	 * 
	 */
	private final class ExpAdapter extends CursorTreeAdapter {
		private LayoutInflater mInflator;

		public ExpAdapter(Cursor cursor, Context context) {
			super(cursor, context);
			mInflator = LayoutInflater.from(context);
		}

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {
			TextView tvChild = (TextView) view
					.findViewById(R.id.lblBudgetListItem);
			tvChild.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_BUDGET_EXPENSE)));
			TextView tvAmount = (TextView) view.findViewById(R.id.lblBudgetListAmount);
			tvAmount.setText(formatCurrency(cursor.getDouble(cursor.getColumnIndex(ConstantesDAO.COL_BUDGET_TOTAL_AMOUNT)), getDefaultLocale()));
			TextView tvPaid = (TextView) view.findViewById(R.id.lblBudgetListPaid);
			tvPaid.setText(formatCurrency(cursor.getDouble(cursor.getColumnIndex(ConstantesDAO.COL_BUDGET_PAID_AMOUNT)), getDefaultLocale()));
		}

		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			TextView tvGrp = (TextView) view
					.findViewById(R.id.lblBudgetListHeader);
			tvGrp.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_BUDGET_CATEGORY)));
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			String category = groupCursor.getString(groupCursor
					.getColumnIndex(COL_BUDGET_CATEGORY));
			String[] projection = { COL_ID, COL_BUDGET_EXPENSE, COL_BUDGET_VENDOR, COL_BUDGET_TOTAL_AMOUNT,
					COL_BUDGET_PAID_AMOUNT };
			Cursor c = getActivity().getContentResolver().query(
					DBContentProvider.Budget.CONTENT_URI, projection,
					COL_BUDGET_CATEGORY + "=?", new String[] { category },
					DBContentProvider.Budget.SORT_ORDER_DEFAULT);
			return c;
		}

		@Override
		protected View newChildView(Context context, Cursor cursor,
				boolean isLastChild, ViewGroup parent) {
			View mView = mInflator.inflate(R.layout.fragment_budget_list_item,
					null);
			TextView tvChild = (TextView) mView
					.findViewById(R.id.lblBudgetListItem);
			tvChild.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_BUDGET_EXPENSE)));
			TextView tvAmount = (TextView) mView.findViewById(R.id.lblBudgetListAmount);
			tvAmount.setText(formatCurrency(cursor.getDouble(cursor.getColumnIndex(COL_BUDGET_TOTAL_AMOUNT)), getDefaultLocale()));
			TextView tvPaid = (TextView) mView.findViewById(R.id.lblBudgetListPaid);
			tvPaid.setText(formatCurrency(cursor.getDouble(cursor.getColumnIndex(COL_BUDGET_PAID_AMOUNT)), getDefaultLocale()));
			return mView;
		}

		@Override
		protected View newGroupView(Context context, Cursor cursor,
				boolean isExpanded, ViewGroup parent) {
			View mView = mInflator.inflate(R.layout.fragment_budget_list_group,
					null);
			TextView tvGrp = (TextView) mView
					.findViewById(R.id.lblBudgetListHeader);
			tvGrp.setText(cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_BUDGET_CATEGORY)));
			return mView;
		}

	}

	/**
	 * Default constructor
	 */
	public BudgetListFragment() {
		mode = FragmentTags.TAG_FGT_BUDGET_LIST;
	}
	
	/**
	 * Default factory
	 */
	public static Fragment newInstance() {
		BudgetListFragment instance = new BudgetListFragment();
		instance.mode = FragmentTags.TAG_FGT_BUDGET_LIST;
		return instance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_budget_list,
				container, false);
		
		// get the listview
		expListView = (ExpandableListView) rootView
				.findViewById(R.id.expandableListBudget);
		expListView.setEmptyView(rootView.findViewById(R.id.empty));
		
		fillContent();
		
		setHasOptionsMenu(true);
		return rootView;
	}
	
	/**
	 * Fill content of the list view
	 */
	private void fillContent() {

		getLoaderManager().initLoader(LOADER_ID, null, this);
		expListAdapter = new ExpAdapter(null, getActivity());
		expListView.setAdapter(expListAdapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.budget, menu);
	}

	@Override
	public void refresh() {
		getLoaderManager().restartLoader(LOADER_ID, null, this);
	}
	
	// LoaderCallbacks interface implementation
	// -----------------------------------------

	// TODO: implement categories query on content provider
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { COL_ID, COL_BUDGET_CATEGORY };
		CursorLoader loader = new CursorLoader(getActivity(),
				Uri.withAppendedPath(Budget.CONTENT_URI,
						Budget.SUFFIX_CATEGORIES), projection, null, null,
				DBContentProvider.Budget.SORT_ORDER_CATEGORY);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		expListAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data are not available any more, delete reference
		expListAdapter.changeCursor(null);
	}
}
