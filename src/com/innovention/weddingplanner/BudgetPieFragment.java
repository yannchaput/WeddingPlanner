package com.innovention.weddingplanner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class BudgetPieFragment extends Fragment {

	private static final String CHART_URL = "http://chart.apis.google.com/chart?cht=p3&chs=400x200&chd=e:TNTNTNGa&chts=000000,16&chtt=A+Better+Web&chl=Hello|Hi|anas|Explorer&chco=FF5533,237745,9011D3,335423&chdl=Apple|Mozilla|Google|Microsoft";

	// Log
	private static final String TAG = BudgetPieFragment.class.getSimpleName();
	// Edit mode
	private FragmentTags mode = FragmentTags.TAG_FGT_BUDGET_PIE;

	// Ad banner
	private AdView adView;

	// Fields
	private WebView viewPie;
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

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_budget_pie,
				container, false);
		backListBtn = (ImageButton) rootView
				.findViewById(R.id.imageButtonReturnBudget);
		backListBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WeddingPlannerHelper.replaceFragment(getActivity(),
						FragmentTags.TAG_FGT_BUDGET_LIST);

			}
		});

		viewPie = (WebView) rootView.findViewById(R.id.webViewPie);
		viewPie.getSettings().setJavaScriptEnabled(true);
		viewPie.loadUrl(CHART_URL);

		// Load Ad
		adView = new AdView(this.getActivity());
		adView.setAdUnitId(Constantes.AD_ID);
		adView.setAdSize(AdSize.BANNER);
		FrameLayout layoutAd = (FrameLayout) rootView
				.findViewById(R.id.LayoutBudgetPieAd);
		layoutAd.addView(adView);

		// Initiez une demande générique.
		AdRequest adRequest = WeddingPlannerHelper.buildAdvert(
				this.getActivity(), Constantes.DEBUG);

		// Chargez l'objet adView avec la demande d'annonce.
		adView.loadAd(adRequest);

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adView != null)
			adView.resume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (adView != null)
			adView.pause();
	}

	@Override
	public void onDestroy() {
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}

}
