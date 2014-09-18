package com.innovention.weddingplanner;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class BudgetPieFragment extends Fragment {
	
	// Log
	private static final String TAG = BudgetPieFragment.class.getSimpleName();
	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_BUDGET_PIE;
	
	// Fields
	private ImageButton backListBtn;

	public BudgetPieFragment() {
		mode = FragmentTags.TAG_FGT_BUDGET_PIE;
	}

	/**
	 * Default factory
	 */
	public static Fragment newInstance() {
		BudgetPieFragment instance = new BudgetPieFragment();
		instance.mode = FragmentTags.TAG_FGT_BUDGET_PIE;
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_budget_pie,
				container, false);
		backListBtn = (ImageButton) rootView.findViewById(R.id.imageButtonReturnBudget);
		backListBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WeddingPlannerHelper.replaceFragment(getActivity(), FragmentTags.TAG_FGT_BUDGET_LIST);
				
			}
		});
		return rootView;
	}
	
	
}
