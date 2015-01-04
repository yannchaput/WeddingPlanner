package com.innovention.weddingplanner;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.bean.Task.Period;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.TasksDao;
import com.innovention.weddingplanner.exception.TechnicalException;
import com.innovention.weddingplanner.utils.AppRater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private final static String TAG = SettingsActivity.class.getSimpleName();

	public static class SettingsFragment extends PreferenceFragment {

		public static class PopulatePlanningRunnable implements Runnable {

			private final Context ctxt;
			private final Handler handler;
			private final Runnable postAction;

			PopulatePlanningRunnable(Context ctx, Handler handler, Runnable action) {
				this.ctxt = ctx;
				this.handler = handler;
				this.postAction = action;
			}

			@Override
			public void run() {
				
				final class Element {
					Period period;
					String[] listTasks;
					
					Element(Period period, String[] listTasks) {
						this.period = period;
						this.listTasks = listTasks;
					}
				}
				
				final DaoLocator locator = DaoLocator.getInstance(ctxt);
				List<Element> list = new LinkedList<Element>();
				String[] listOfTasksOverOneYear = ctxt.getResources()
						.getStringArray(R.array.planning_over_one_year);
				String[] listOfTasksFourthQuarter = ctxt.getResources()
						.getStringArray(R.array.planning_fourth_quarter);
				String[] listOfTasksThirdQuarter = ctxt.getResources()
						.getStringArray(R.array.planning_third_quarter);
				String[] listOfTasksSecondQuarter = ctxt.getResources()
						.getStringArray(R.array.planning_second_quarter);
				String[] listOfTasksFirstQuarter = ctxt.getResources()
						.getStringArray(R.array.planning_first_quarter);
				String[] listOfTasksAfterOneWeek = ctxt.getResources()
						.getStringArray(R.array.planning_after_one_week);
				String[] listOfTasksBeforeOneWeek = ctxt.getResources()
						.getStringArray(R.array.planning_before_one_week);
				String[] listOfTasksDday = ctxt.getResources()
						.getStringArray(R.array.planning_on_dday);
				
				list.add(new Element(Period.OVER_YEAR,listOfTasksOverOneYear));
				list.add(new Element(Period.FOURTH_QUARTER,listOfTasksFourthQuarter));
				list.add(new Element(Period.THIRD_QUARTER,listOfTasksThirdQuarter));
				list.add(new Element(Period.SECOND_QUARTER,listOfTasksSecondQuarter));
				list.add(new Element(Period.FIRST_QUARTER,listOfTasksFirstQuarter));
				list.add(new Element(Period.OVER_WEEK,listOfTasksAfterOneWeek));
				list.add(new Element(Period.BEFORE_WEEK,listOfTasksBeforeOneWeek));
				list.add(new Element(Period.DDAY,listOfTasksDday));
				
				TasksDao dao = locator. get(SERVICES.TASK);
				
				for (Element elt : list) {
					for (String item : elt.listTasks) {
						Task task = new Task.Builder().withDesc(item)
								.setActive(true).setPlanning(elt.period)
								.build();
						Log.d(TAG, "Process task " + task);
						dao.insert(task);
					}
				}
				
				
				if (null != handler) {
					handler.post(postAction);
				}

			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

			Preference button = (Preference) findPreference("recreateDB");

			final SharedPreferences prefs = this.getPreferenceManager()
					.getSharedPreferences();

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
			CheckBoxPreference planningCheck = (CheckBoxPreference) findPreference("planning");
			planningCheck
					.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {

							Runnable runnable = new PopulatePlanningRunnable(
									getActivity(), null, null);
							if (prefs.getBoolean("planning", false)) {
								Log.d(TAG, "Import predefined planning");
								Executors.newSingleThreadExecutor().execute(
										runnable);
							} else {
								TasksDao dao = locator.get(SERVICES.TASK);
								List<Task> list = dao.getList();
								for (Task t : list) {
									if (!Period.CUSTOM.equals(t.getPeriod()))
										dao.removeWithId(t.getId());
								}
							}
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
