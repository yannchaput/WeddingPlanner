/**
 * 
 */
package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.replaceFragment;

import com.innovention.weddingplanner.Constantes.FragmentTags;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment that displays a list of the current expenses grouped by vendor categories
 * The footer display the balance, the outstanding balance and the paid amount
 * @author YCH
 *
 */
public final class BudgetListFragment extends Fragment {
	
	private FragmentTags mode = FragmentTags.TAG_FGT_BUDGET_LIST;

	/**
	 * Default constructor
	 */
	public BudgetListFragment() {
	}
	
	/**
	 * Default factory
	 */
	public static Fragment newInstance() {
		BudgetListFragment instance = new BudgetListFragment();
		return instance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_budget_list,
				container, false);
		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.budget, menu);
	}
}
