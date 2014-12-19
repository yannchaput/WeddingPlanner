package com.innovention.weddingplanner;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class GuestPagerFragment extends Fragment {

	private final static String TAG = GuestPagerFragment.class.getSimpleName();

	private GuestPageAdapter mPageAdapter;
	private ViewPager mPager;

	/**
	 * PageAdapter to enable swipe between fragments
	 * 
	 * @author Yann
	 * 
	 */
	private class GuestPageAdapter extends FragmentPagerAdapter {

		public GuestPageAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fgt = null;
			if (position == 0) {
				fgt = GuestStatsFragment.newInstance();
			} else {
				fgt = GuestListFragment.newInstance();
			}
			return fgt;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String title = "";
			if (position == 0)
				title = getResources().getString(R.string.contact_tab_statistics);
			else
				title = getResources().getString(R.string.contact_tab_liste);
			return title;
		}

	}

	public GuestPagerFragment() {
		Log.d(TAG, "default constructor");
	}

	public static GuestPagerFragment newInstance() {
		Log.d(TAG, "newInstance");
		return new GuestPagerFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_guest_pager, container,
				false);
		setHasOptionsMenu(true);

		mPager = (ViewPager) v.findViewById(R.id.pager);
		mPageAdapter = new GuestPageAdapter(getFragmentManager());
		mPager.setAdapter(mPageAdapter);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between pages, select the
				// corresponding tab.
				getActivity().getActionBar()
						.setSelectedNavigationItem(position);
			}
		});
		
		setupActionBar();

		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.guest, menu);
		return;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		Log.d(TAG, "setupActionBar");

		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction arg1) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(tab.getPosition());

			}

			@Override
			public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
				// TODO Auto-generated method stub

			}

		};

		actionBar.addTab(actionBar.newTab().setText(getResources().getString(R.string.contact_tab_statistics))
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(getResources().getString(R.string.contact_tab_liste))
				.setTabListener(tabListener));


	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}
}
