package com.innovention.weddingplanner;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.internal.bg;
import com.google.common.base.Joiner;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.contentprovider.DBContentProvider;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class BudgetPieFragment extends Fragment {

	// private static final String CHART_URL =
	// "http://chart.apis.google.com/chart?cht=p3&chs=400x200&chd=e:TNTNTNGa&chts=000000,16&chtt=A+Better+Web&chl=Hello|Hi|anas|Explorer&chco=FF5533,237745,9011D3,335423&chdl=Apple|Mozilla|Google|Microsoft";
	private static final String CHART_URL = "http://chart.apis.google.com/chart?cht=p3";

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

		// Build URL
		float pie_width = viewPie.getResources().getDimension(
				R.dimen.budget_pie_chart_width);
		float pie_height = viewPie.getResources().getDimension(
				R.dimen.budget_pie_chart_height);
		String title = viewPie.getResources().getString(
				R.string.budget_pie_chart_title);
		int bgColor = viewPie.getResources().getColor(R.color.Bisque);
		Log.v(TAG, "background color: " + Integer.toHexString(bgColor));
		String[] labels = GetCategoryArray();
		Log.v(TAG, Joiner.on("|").skipNulls().join(labels));

		Uri.Builder builder = new Uri.Builder();
		Uri chartUri = builder
				.scheme("http")
				.authority("chart.apis.google.com")
				.appendPath("chart")
				.appendQueryParameter("cht", "p3")
				.appendQueryParameter(
						"chs",
						new StringBuilder()
								.append(String.valueOf((int) pie_width))
								.append("x")
								.append(String.valueOf((int) pie_height))
								.toString())
				.appendQueryParameter("chd", "t:20,65,15")
				// Data
				.appendQueryParameter("chts", "000000,16")
				// Title color and size
				.appendQueryParameter("chtt", title)
				.appendQueryParameter("chl", Joiner.on("|").skipNulls().join(labels))
				.appendQueryParameter(
						"chf",
						new StringBuilder().append("bg").append(",s")
								.append(",").append(Integer.toHexString(bgColor))
								.toString())
				// .appendQueryParameter("chco", "FF5533,237745,9011D3,335423")
				//.appendQueryParameter("chdl", "Apple|Mozilla|Google|Microsoft")
				.build();
		viewPie.loadUrl(chartUri.toString());

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

	/**
	 * Retrieves list of categories in the shape of an array of strings
	 * @return array of category string
	 */
	private String[] GetCategoryArray() {
		ArrayList<String> categories = new ArrayList<String>();
		Cursor c = getCategoryCursor();

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			categories.add(c.getString(c.getColumnIndex(ConstantesDAO.COL_BUDGET_CATEGORY)));
		}
		c.close();

		return categories.toArray(new String[categories.size()]);
	}

	/**
	 * Retrieve list of available categories from DB
	 * 
	 * @return the cursor
	 */
	private Cursor getCategoryCursor() {
		return this
				.getActivity()
				.getContentResolver()
				.query(Uri.withAppendedPath(
						DBContentProvider.Budget.CONTENT_URI,
						DBContentProvider.Budget.SUFFIX_CATEGORIES), null,
						null, null,
						DBContentProvider.Budget.SORT_ORDER_CATEGORY);
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
