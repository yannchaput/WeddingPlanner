package com.innovention.weddingplanner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.innovention.weddingplanner.bean.Contact.ResponseType;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.GuestsDao;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GuestStatsFragment extends Fragment {

	/**
	 * Ad widget
	 */
	private AdView adView;

	public static GuestStatsFragment newInstance() {
		return new GuestStatsFragment();
	}

	public GuestStatsFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_guest_stats, container,
				false);
		setHasOptionsMenu(true);
		
		updateView(v);

		// Load Ad
		adView = new AdView(this.getActivity());
		adView.setAdUnitId(Constantes.AD_ID);
		adView.setAdSize(AdSize.BANNER);
		FrameLayout layoutAd = (FrameLayout) v
				.findViewById(R.id.LayoutGuestStatsAd);
		layoutAd.addView(adView);

		// Initiez une demande générique.
		AdRequest adRequest = WeddingPlannerHelper.buildAdvert(
				this.getActivity(), Constantes.DEBUG);

		// Chargez l'objet adView avec la demande d'annonce.
		adView.loadAd(adRequest);
		return v;
	}
	
	private void updateView(final View v) {
		StringBuilder buildQuery = new StringBuilder().append("select count(")
				.append(ConstantesDAO.COL_ID)
				.append(") from ")
				.append(ConstantesDAO.TABLE_GUESTS);
		updateTextView(v, buildQuery.toString(), null, R.id.lblGuestStatsResult);
		buildQuery = new StringBuilder().append("select count(")
				.append(ConstantesDAO.COL_ID)
				.append(") from ")
				.append(ConstantesDAO.TABLE_GUESTS)
				.append(" where ")
				.append(ConstantesDAO.COL_RSVP)
				.append(" = ?");
		updateTextView(v, buildQuery.toString(), new String[] { ResponseType.ATTEND.toString() }, R.id.lblGuestStatsNbAttendResult);
		updateTextView(v, buildQuery.toString(), new String[] { ResponseType.NOTATTEND.toString() }, R.id.lblGuestStatsNbAbsentResult);
		updateTextView(v, buildQuery.toString(), new String[] { ResponseType.PENDING.toString() }, R.id.lblGuestStatsNbRsvpResult);
	}
	
	private void updateTextView(final View v, String query, String[] queryArgs, int id) {
		GuestsDao dao = DaoLocator.getInstance(getActivity()).get(SERVICES.GUEST);
		Cursor c = dao.rawQuery(query, queryArgs);
		c.moveToFirst();
		int count = c.getInt(0);
		TextView t1 = (TextView) v.findViewById(id);
		t1.setText(String.valueOf(count));
		c.close();
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
		// TODO Auto-generated method stub
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}

}
