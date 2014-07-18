package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.hideKeyboard;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.replaceFragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.VendorFragment.OnValidateVendor;
import com.innovention.weddingplanner.bean.Vendor;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.VendorDao;

public class VendorActivity extends Activity implements OnValidateVendor {
	
	private static final String TAG = VendorActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.activity_vendor);
		// At start, display list fragment
		getFragmentManager().beginTransaction().add(R.id.LayoutVendor, VendorListFragment.newInstance(), FragmentTags.TAG_FGT_VENDORLIST.getValue()).commit();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.vendor, menu);
//		return true;
//	}

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
		case R.id.action_add_vendor:
			replaceFragment(this, FragmentTags.TAG_FGT_CREATE_VENDOR);
			return true;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onValidateVendor(Vendor vendor, FragmentTags source) {
		Log.d(TAG, "onValidateVendor - click on validate vendor button");
		
		// Hide virtual keyboard if opened
		hideKeyboard(this);
		
		VendorDao dao = DaoLocator.getInstance(getApplicationContext()).get(SERVICES.VENDOR);
		Log.v(TAG, "Save vendor: " + vendor + " in mode " + source.toString());
		// Create vendor
		if (FragmentTags.TAG_FGT_CREATE_VENDOR.equals(source)) {
			// TODO use content provider
			dao.insert(vendor);
		}
		else if (FragmentTags.TAG_FGT_UPDATE_VENDOR.equals(source)) {
			// TODO use content provider
			dao.update(vendor.getId(), vendor);
		}
		
		replaceFragment(this, FragmentTags.TAG_FGT_VENDORLIST);
	}

	

}
