package com.innovention.weddingplanner;

import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.exception.TechnicalException;
import com.innovention.weddingplanner.utils.AppRater;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private final static String TAG = SettingsActivity.class.getSimpleName();

	public static class SettingsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

			Preference button = (Preference) findPreference("recreateDB");

			final DaoLocator locator = DaoLocator.getInstance(getActivity());
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference arg0) {
					try {
						Log.d(TAG, "Settings - rebuild db command");
						locator.getDbHelper().recreateDb(
								locator.getDbHelper().getWritableDatabase());
						Toast.makeText(getActivity(),
								R.string.item_recreateDb_toast,
								Toast.LENGTH_LONG).show();
					} catch (TechnicalException e) {
						Toast.makeText(getActivity(),
								R.string.item_recreateDb_failed_toast,
								Toast.LENGTH_LONG).show();
					}
					return true;
				}
			});
			button = (Preference) findPreference("resetDb");
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference arg0) {
					try {
						locator.getDbHelper().resetDb(
								locator.getDbHelper().getWritableDatabase());
						Toast.makeText(getActivity(),
								R.string.item_recreateDb_toast,
								Toast.LENGTH_LONG).show();
					} catch (TechnicalException e) {
						Toast.makeText(getActivity(),
								R.string.item_recreateDb_failed_toast,
								Toast.LENGTH_LONG).show();
					}
					return true;
				}
			});
			button = (Preference) findPreference("rateApp");
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference arg0) {
					AppRater.showRateDialog(getActivity(), null);
					return true;
				}
			});
		}
	}

	public SettingsActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}
}
