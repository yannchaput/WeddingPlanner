package com.innovention.weddingplanner;


import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_D;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_M;
import static com.innovention.weddingplanner.Constantes.KEY_DTPICKER_Y;
import static com.innovention.weddingplanner.Constantes.TAG_FGT_DATEPICKER;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NOM_BDD;
import static com.innovention.weddingplanner.dao.ConstantesDAO.VERSION_BDD;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.innovention.weddingplanner.bean.IDtoBean;
import com.innovention.weddingplanner.bean.WeddingInfo;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DatabaseHelper;
import com.innovention.weddingplanner.dao.GuestsDao;
import com.innovention.weddingplanner.dao.WeddingInfoDao;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

public class MainActivity extends Activity {
	
	private final static String TAG = MainActivity.class.getSimpleName();
	
	private ImageButton taskBtn;
	private ImageButton inviteeBtn;
	private ImageButton budgetBtn;
	private ImageButton vendorBtn;
	
	private TextView days2WeddingTxt;
	private ViewGroup days2WeddingLayout;
	
	private DaoLocator locator = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate - " + "Create main activity");
		
		setContentView(R.layout.activity_main);
		
		taskBtn = (ImageButton) findViewById(R.id.imageButton1);
		inviteeBtn = (ImageButton) findViewById(R.id.imageButton2);
		budgetBtn = (ImageButton) findViewById(R.id.imageButton3);
		vendorBtn = (ImageButton) findViewById(R.id.imageButton4);
		
		// Enregistre la police du texte en bas
		// TODO : voir si on peut pas faire la m�me chose par les styles
		days2WeddingTxt = (TextView) findViewById(R.id.days2WeddingText);
		Typeface font = Typeface.createFromAsset(getAssets(), "GreatVibes-Regular.otf");
		days2WeddingTxt.setTypeface(font);
		
		// Init services and DAO
		initServices();
		
		// Update bottomPanel view
		updateWeddingDate();
		
	}
	
	/*
	 * Creates all services
	 */
	private void initServices() {
		// Create main DAO object and open db
		locator = DaoLocator.getInstance(getApplication());
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// Close all open dbs
		Log.d(TAG, "onDestroy - " + "Destroy db");
		locator.getDbHelper().close();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_recreateDb: 
			// TODO call db connector recreate db
			locator.getDbHelper().recreateDb(locator.getDbHelper().getWritableDatabase());
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Callback called when one clicked on the wedding date
	 * @param v view
	 */
	public void showDatePickerDialog(View v) {
		
		Log.d(TAG, "showDatePickerDialog - " + "show date picker fragment");
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag(TAG_FGT_DATEPICKER);
		if (prev != null)
			ft.remove(prev);
		ft.addToBackStack(null);
		
		DialogFragment datePicker = new DatePickerFragment();
		WeddingInfo info = null;
		WeddingInfoDao infoDao = (WeddingInfoDao) DaoLocator.getInstance(getApplication()).get(SERVICES.INFO);
		if ( (info = (WeddingInfo) infoDao.get()) != null ) {
			Log.d(TAG, "showDatePickerDialog - " + "Fetch info : " + info);
			Bundle parameters = new Bundle();
			parameters.putInt(KEY_DTPICKER_Y, info.getWeddingDate().year().get());
			parameters.putInt(KEY_DTPICKER_M, info.getWeddingDate().monthOfYear().get());
			parameters.putInt(KEY_DTPICKER_D, info.getWeddingDate().dayOfMonth().get());
			datePicker.setArguments(parameters);
		}
		datePicker.show(ft, TAG_FGT_DATEPICKER);
	}
	
	public void showGuestActivity(View v) {
		
		Log.d(TAG, "Start Guest invitation activity");
		//Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, GuestActivity.class);
		startActivity(intent);
	}
	
	
	/**
	 * Update bottomPanel with date saved in db if so
	 */
	private void updateWeddingDate() {

		Log.d(TAG, "updateWeddingDate - "
				+ "Get & display wedding date at start time");

		WeddingInfo info = null;

		WeddingInfoDao infoDao = (WeddingInfoDao) DaoLocator.getInstance(getApplication()).get(SERVICES.INFO);
		if ((info = (WeddingInfo) infoDao.get()) != null) {
			Log.d(TAG, "Date get from db " + info.getWeddingDate());
			updateGUIWeddingDate(WeddingPlannerHelper.computeDate(info
					.getWeddingDate().year().get(), info.getWeddingDate()
					.monthOfYear().get(), info.getWeddingDate().dayOfMonth()
					.get()));
		}
	}
	
	/**
	 * Update Wedding info in bdd and in the bottom panel
	 * 
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 */
	public void updateWeddingDate(final int year, final int monthOfYear,
			final int dayOfMonth) {

		Log.d(TAG, "updateWeddingDate - " + "Update wedding date in db");
		IDtoBean info = null;
		WeddingInfoDao infoDao = (WeddingInfoDao) DaoLocator.getInstance(getApplication()).get(SERVICES.INFO);
		
		if ((info = infoDao.get()) == null) {
			Log.d(TAG, "Set first time date to " + dayOfMonth + "/"
					+ monthOfYear + "/" + year);
			infoDao.insert(new WeddingInfo(year, monthOfYear, dayOfMonth));
		} else {
			Log.d(TAG, "Update date to " + dayOfMonth + "/" + monthOfYear + "/"
					+ year);
			infoDao.update(info.getId(), new WeddingInfo(year, monthOfYear,
					dayOfMonth));
		}
		updateGUIWeddingDate(WeddingPlannerHelper.computeDate(year,
				monthOfYear, dayOfMonth));

	}
	
	/**
	 * Sets the text of bottom panel (days to the wedding)
	 * @param daysToCome
	 */
	public void updateGUIWeddingDate(final int daysToCome) {
		
		Resources res = getResources();
		days2WeddingTxt.setText(daysToCome + " " + res.getString(R.string.days_to_wedding));
	}
	
	

}
