package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.replaceFragment;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.hideKeyboard;

import com.innovention.weddingplanner.BudgetFragment.OnValidateBudget;
import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.contentprovider.DBContentProvider;
import com.innovention.weddingplanner.contentprovider.DBContentProvider.Budget;
import com.innovention.weddingplanner.dao.ConstantesDAO;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Displays a budget review screen
 * 
 * @author YCH
 * 
 */
public final class BudgetActivity extends FragmentActivity implements OnValidateBudget {

	private static final String TAG = BudgetActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_budget);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.LayoutBudget, BudgetListFragment.newInstance())
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_add_budget_item:
			replaceFragment(this, FragmentTags.TAG_FGT_CREATE_BUDGET);
			return true;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onValidateBudget(ContentValues values, FragmentTags source) {
		Uri uri = null;

		hideKeyboard(this);
		if (FragmentTags.TAG_FGT_CREATE_BUDGET.equals(source)) {
			uri = getContentResolver().insert(Budget.CONTENT_URI, values);
		} else {
			uri = ContentUris.withAppendedId(
					DBContentProvider.Budget.CONTENT_URI,
					values.getAsLong(ConstantesDAO.COL_ID));
			values.remove(ConstantesDAO.COL_ID);
			getContentResolver().update(uri, values, null, null);
		}

		replaceFragment(this, FragmentTags.TAG_FGT_BUDGET_LIST);
	}

	/**
	 * Compute total or paid amount of a category depends on the uri hit
	 * 
	 * @param category
	 * @return the total
	 */
	double computeAmount(final Uri uri, String category) {

		double result = 0.0d;

		Cursor c = null;

		if (null != category) {
			c = getContentResolver().query(uri, null, null,
					new String[] { category }, null);
		} else {
			c = getContentResolver().query(uri, null, null, null,
					null);
		}

		result = compute(c);

		c.close();

		return result;
	}

	/**
	 * Embeds logic to calculate sum of a column
	 * 
	 * @param c
	 * @return result
	 */
	double compute(final Cursor c) {
		double result = 0.0d;

		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				result += c.getDouble(0);
			} while (!c.isLast());
		}

		return result;
	}

}
