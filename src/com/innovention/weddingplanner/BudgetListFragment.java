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
import com.innovention.weddingplanner.bean.Budget;
import com.innovention.weddingplanner.contentprovider.DBContentProvider;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

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
	
	// Context menu
	private ActionMode mActionMode = null;
	
	// Action mode callback
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;

		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.budget_context_menu, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			Log.v(TAG, "ActionMode.Callback - onActionItemClicked");

			boolean returnCode = false;
			// Compute position in the expandable list
			// Flat position
			int flatPosition = expListView.getCheckedItemPosition();
			// Compute packed position
			long packedPosition = expListView
					.getExpandableListPosition(flatPosition);
			int packedGroupPos = ExpandableListView
					.getPackedPositionGroup(packedPosition);
			int packedChildPos = ExpandableListView
					.getPackedPositionChild(packedPosition);
			Log.v(TAG, String.format("Decoding position [group=%d,  child=%d]",
					packedGroupPos, packedChildPos));

			long id = expListAdapter.getChildId(packedGroupPos, packedChildPos);
			Log.v(TAG, "Get child id " + id);
			Uri uri = ContentUris.withAppendedId(DBContentProvider.Budget.CONTENT_URI, id);

			// Handle action
			switch (item.getItemId()) {
			case R.id.action_delete_budget:
				getActivity().getContentResolver().delete(uri, null, null);
				BudgetListFragment.this.refresh();
				Toast.makeText(getActivity(),
						R.string.delete_budget_OK_alert_message,
						Toast.LENGTH_SHORT).show();
				returnCode = true;
				break;
			case R.id.action_update_budget:
				Cursor c = getActivity().getContentResolver().query(uri,
						DBContentProvider.Budget.PROJECTION_ALL, null, null, null);
				Budget budget = Budget.Transformer.transform(c);
				c.close();
				WeddingPlannerHelper.replaceFragment(getActivity(),
						FragmentTags.TAG_FGT_UPDATE_BUDGET, budget);
				returnCode = true;
				break;
			default:
			}
			mode.finish();
			return returnCode;
		}
	};
	
	// Fields
	private TextView totalAmount;
	private TextView paidAmount;
	private TextView outstandingAmount;

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
			super(cursor, context,true);
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
			String category = cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_BUDGET_CATEGORY));
			TextView tvGrp = (TextView) view
					.findViewById(R.id.lblBudgetListHeader);
			tvGrp.setText(category);
			TextView tvTotal = (TextView) view.findViewById(R.id.lblBudgetListHeaderAmount);
			TextView tvPaid = (TextView) view.findViewById(R.id.lblBudgetListHeaderPaid);
			tvTotal.setText(formatCurrency(computeTotalAmount(category), getDefaultLocale()));
			tvPaid.setText(formatCurrency(computePaidAmount(category), getDefaultLocale()));
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
			String category = cursor.getString(cursor
					.getColumnIndex(ConstantesDAO.COL_BUDGET_CATEGORY));
			TextView tvGrp = (TextView) mView
					.findViewById(R.id.lblBudgetListHeader);
			tvGrp.setText(category);
			TextView tvTotal = (TextView) mView.findViewById(R.id.lblBudgetListHeaderAmount);
			TextView tvPaid = (TextView) mView.findViewById(R.id.lblBudgetListHeaderPaid);
			tvTotal.setText(formatCurrency(computeTotalAmount(category), getDefaultLocale()));
			tvPaid.setText(formatCurrency(computePaidAmount(category), getDefaultLocale()));
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
		expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Log.v(TAG,
						String.format(
								"onChildClick - perform a click on child of expandable list [id=%d, groupPos=%d, childPos=%d",
								id, groupPosition, childPosition));
				if (null == mActionMode) {
					mActionMode = getActivity().startActionMode(
							mActionModeCallback);
					expListView
							.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
					int index = parent
							.getFlatListPosition(ExpandableListView
									.getPackedPositionForChild(
											groupPosition,
											childPosition));
					expListView.setItemChecked(index, true);
				}
				return true;
			}
		});
		
		// Set the fields
		View includeView = rootView.findViewById(R.id.includeBudgetTopPanel);
		totalAmount = (TextView) includeView.findViewById(R.id.lblCalculatedTotal);
		paidAmount = (TextView) includeView.findViewById(R.id.lblCalculatedPaid);
		outstandingAmount = (TextView) includeView.findViewById(R.id.lblCalculatedOutstanding);
		
		fillContent();
		
		setHasOptionsMenu(true);
		return rootView;
	}
	
	/**
	 * Fill content of the list view the first time
	 */
	private void fillContent() {

		getLoaderManager().initLoader(LOADER_ID, null, this);
		expListAdapter = new ExpAdapter(null, getActivity());
		expListView.setAdapter(expListAdapter);
		
		updateTopContent();
		
		
	}
	
	/**
	 * Update content of top panel
	 */
	private void updateTopContent() {
		double total = computeTotalAmount(null);
		double paid = computePaidAmount(null);
		double outstanding = total - paid;
		
		totalAmount.setText(formatCurrency(total, getDefaultLocale()));
		paidAmount.setText(formatCurrency(paid, getDefaultLocale()));
		outstandingAmount.setText(formatCurrency(outstanding, getDefaultLocale()));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.budget, menu);
	}

	@Override
	public void refresh() {
		//getLoaderManager().restartLoader(LOADER_ID, null, this);
		// No need to refresh the loader as content provider ensures
		// the listeners such as adpaters are notified and updated
		
		// refresh top panel
		updateTopContent();
	}
	
	/**
	 * Compute total amount of a category
	 * @param category
	 * @return the total
	 */
	private double computeTotalAmount(String category) {
		
		double result = 0.0d;
		
		Cursor c = null;
		
		if (null != category) {
			c = getActivity().getContentResolver().query(Uri.withAppendedPath(DBContentProvider.Budget.CONTENT_URI, DBContentProvider.Budget.SUFFIX_SUM_TOTAL_AMOUNT),
					null,
					null,
					new String[] {category},
					null);
		}
		else {
			c = getActivity().getContentResolver().query(Uri.withAppendedPath(DBContentProvider.Budget.CONTENT_URI, DBContentProvider.Budget.SUFFIX_SUM_TOTAL_AMOUNT),
					null,
					null,
					null,
					null);
		}
		
		result = compute(c);
		
		c.close();
		
		return result;
	}
	
	/**
	 * Compute paid amount of a category
	 * @param category
	 * @return the total
	 */
	private double computePaidAmount(String category) {
		
		double result = 0.0d;
		
		Cursor c = null;
		
		if (null != category) {
			c = getActivity().getContentResolver().query(
					Uri.withAppendedPath(DBContentProvider.Budget.CONTENT_URI,
							DBContentProvider.Budget.SUFFIX_SUM_PAID_AMOUNT), null, null,
					new String[] { category }, null);
		}
		else {
			c = getActivity().getContentResolver().query(
					Uri.withAppendedPath(DBContentProvider.Budget.CONTENT_URI,
							DBContentProvider.Budget.SUFFIX_SUM_PAID_AMOUNT), null, null,
					null, null);
		}
		
		result = compute(c);
		
		c.close();
		
		return result;
	}
	
	/**
	 * Embeds logic to calculate sum of a column
	 * @param c
	 * @return result
	 */
	private double compute(final Cursor c) {
		double result = 0.0d;
		
		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				result += c.getDouble(0);
			} while (!c.isLast());
		}
		
		return result;
	}
	
	// LoaderCallbacks interface implementation
	// -----------------------------------------

	// TODO: implement categories query on content provider
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { COL_ID, COL_BUDGET_CATEGORY };
		CursorLoader loader = new CursorLoader(getActivity(),
				Uri.withAppendedPath(DBContentProvider.Budget.CONTENT_URI,
						DBContentProvider.Budget.SUFFIX_CATEGORIES), projection, null, null,
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
