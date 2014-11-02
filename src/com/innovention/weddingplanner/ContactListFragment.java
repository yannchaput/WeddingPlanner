/**
 * 
 */
package com.innovention.weddingplanner;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.innovention.weddingplanner.Constantes.FragmentTags;
import com.innovention.weddingplanner.ContactFragment.OnValidateContactListener;
import com.innovention.weddingplanner.bean.Contact;
import com.innovention.weddingplanner.exception.WeddingPlannerException;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

/**
 * Fragment which displays a list of contacts stored in the cloud/phone so as to
 * select them
 * 
 * @author ychaput
 * 
 */
public class ContactListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	// LOG
	private static final String TAG = ContactListFragment.class.getSimpleName();
	// LoaderCallback ids
	private static final int QUERY_LIST_ID = 1;
	private static final int QUERY_DETAIL_NAMES_ID = 2;

	/**
	 * Activity listens to create contact events
	 */
	private OnValidateContactListener mListener;

	/**
	 * Progress bar
	 */
	private ProgressBar bar;

	/**
	 * Bouton fin de l'import
	 */
	private Button okButton;

	/**
	 * ListView cursorAdapter
	 */
	private CursorAdapter mAdapter;

	/**
	 * List of db ids whose checkbox was checked Use a sparsearray for better
	 * performance while walking through the screen
	 */
	private SparseArray<Mapping> mListContacts;

	// columns requested from the database for query list
	private static final String[] PROJECTION_QUERY_LIST = {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.LOOKUP_KEY,
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY };

	// columns requested from the database for query contact details
	private static final String[] PROJECTION_QUERY_DETAIL_NAMES = {
			StructuredName.LOOKUP_KEY, StructuredName.GIVEN_NAME,
			StructuredName.FAMILY_NAME };
	private static final String[] PROJECTION_QUERY_DETAIL_EMAIL = { Email.ADDRESS };
	private static final String[] PROJECTION_QUERY_DETAIL_PHONE = { Phone.NUMBER };
	private static final String[] PROJECTION_QUERY_DETAIL_ADDRESS = { StructuredPostal.FORMATTED_ADDRESS };

	/**
	 * Where clause for cursor loader queries
	 */
	private String mSelectionDetail;
	/**
	 * Where clause arguments for contact details
	 */
	private String[] mSelectionDetailArgs;

	/**
	 * Utility class for ContactListCursorAdapter. Represents the mapping
	 * between one row of the list and what is saved in cache
	 * 
	 * @author ychaput
	 * 
	 */
	private class Mapping {
		private long _id = -1;
		private String _lookupKey;
		private boolean _checked = false;

		private Mapping() {
		}

		private Mapping(long id, String lookupKey) {
			_id = id;
			_lookupKey = lookupKey;
		}

		private Mapping(long id, String lookupKey, boolean checked) {
			this(id, lookupKey);
			_checked = checked;
		}
	}

	/**
	 * ContactListCursorAdapter is the adapter layout for a list of contact from
	 * the android device
	 * 
	 * @author ychaput
	 * 
	 */
	private class ContactListCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		public ContactListCursorAdapter(final Context ctx, final Cursor c,
				int flags) {
			super(ctx, c, flags);
			mInflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.CursorAdapter#newView(android.content.Context,
		 * android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.fragment_contact_adapter,
					parent, false);
			CheckBox ck = (CheckBox) view
					.findViewById(R.id.checkboxContactList);
			ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Mapping bean = (Mapping) buttonView.getTag();
					Log.d(TAG, "Click on checkbox item whose id is " + bean._id);
					bean._checked = isChecked;
					// Map the value of the check to this id value in db
					// Will replace the previous entry if exists
					mListContacts.put((int) bean._id, bean);
				}
			});

			return view;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.CursorAdapter#bindView(android.view.View,
		 * android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			CheckBox ckView = (CheckBox) view
					.findViewById(R.id.checkboxContactList);

			String name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
			// Save db id as a tag of the checkbox
			long id = cursor.getLong(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String lookupKey = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
			Mapping cachedBean = mListContacts.get((int) id);
			if (cachedBean == null) {
				cachedBean = new Mapping(id, lookupKey, false);
			}
			// Set checkbox caption
			ckView.setText(name);
			// We store the cache object temporarily as a tag
			// in order to retrieve it and save in the cache is selected
			ckView.setTag(cachedBean);
			// Set the check mark
			ckView.setChecked(cachedBean._checked);
		}

	}

	public ContactListFragment() {
		mListContacts = new SparseArray<Mapping>(30);
	}

	/**
	 * Factory method
	 * 
	 * @return
	 */
	public static ContactListFragment newInstance() {
		return new ContactListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create adapter once
		Context context = getActivity();
		Cursor c = null; // there is no cursor yet
		int flags = 0; // no auto-requery! Loader requeries.
		mAdapter = new ContactListCursorAdapter(context, c, flags);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// each time we are started use our listadapter
		setListAdapter(mAdapter);
		// and tell loader manager to start loading
		getLoaderManager().initLoader(QUERY_LIST_ID, null, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_contact_list, container,
				false);
		// Necessary to set the menu visible for fragment
		setHasOptionsMenu(true);

		okButton = (Button) v.findViewById(R.id.contactImportButtonOK);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				WeddingPlannerHelper.replaceFragment(
						ContactListFragment.this.getActivity(),
						FragmentTags.TAG_FGT_GUESTLIST);

			}
		});
		okButton.setEnabled(false);

		bar = (ProgressBar) v.findViewById(R.id.contactImportProgress);
		bar.setVisibility(View.GONE);

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
		inflater.inflate(R.menu.import_contact_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_validate_import_contact:
			Log.d(TAG, "Click on validate import button");
			startImportContacts();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mListener = (OnValidateContactListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnValidateContactListener");
		}
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		mListener = null;
	}

	/**
	 * Import contacts checked in the ListView and transforms them into Contacts
	 * in a view to persisting them in db
	 */
	private void startImportContacts() {

		Log.i(TAG, "Import and save contacts from address book");

		Mapping bean = null;
		ArrayList<String> listWhereArgs = new ArrayList<String>();
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(Data.LOOKUP_KEY).append(" IN (");
		int i = 0;
		for (; i < mListContacts.size(); i++) {
			bean = mListContacts.valueAt(i);
			if (bean != null && bean._checked && bean._id != -1
					&& !StringUtils.isEmpty(bean._lookupKey)) {
				whereClause.append("?,");
				listWhereArgs.add(bean._lookupKey);
			}
		}
		whereClause.deleteCharAt(whereClause.length() - 1);
		whereClause.append(")");
		whereClause.append(" AND ").append(" ( ").append(" ( ")
				.append(Data.MIMETYPE).append(" = ?").append(" ) ")
				.append(" OR ").append(" ( ").append(Data.MIMETYPE)
				.append(" = ?").append(" ) ").append(" ) ");
		mSelectionDetail = whereClause.toString();
		listWhereArgs.add(StructuredName.CONTENT_ITEM_TYPE);
		listWhereArgs.add(Email.CONTENT_ITEM_TYPE);
		mSelectionDetailArgs = listWhereArgs.toArray(new String[listWhereArgs
				.size()]);
		Log.v(TAG, "Where clause = " + mSelectionDetail);
		Log.v(TAG, "Lookup keys: " + ArrayUtils.toString(mSelectionDetailArgs));

		// Show progress bar
		bar.setVisibility(View.VISIBLE);
		// Then load the contacts in background
		getLoaderManager().restartLoader(QUERY_DETAIL_NAMES_ID, null, this);
		// Results are received in onLoadReset method
	}

	/**
	 * Process retrieved contacts and store them in db as guests
	 * 
	 * @param data
	 *            cursor of contacts
	 */
	private void processContacts(final Cursor data) {

		final Handler handler = new Handler();

		// Do the process in another thread so that the gui queue is not impeded
		Runnable taskDesc = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Contact bean = null;
				final String msg;
				boolean hasErrors = false;
				short count = 0;
				Log.v(TAG,
						"Get a cursor of contacts with size " + data.getCount());
				for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
					String lookupKey = "";
					String name = "";
					String surname = "";
					String eMail = "";
					String phone = "";
					String address = "";
					Log.i(TAG, "Process contact:");
					lookupKey = data.getString(data
							.getColumnIndex(StructuredName.LOOKUP_KEY));
					Log.i(TAG, "Lookup key : " + lookupKey);
					name = data.getString(data
							.getColumnIndex(StructuredName.GIVEN_NAME));
					Log.i(TAG, "Name : " + name);
					surname = data.getString(data
							.getColumnIndex(StructuredName.FAMILY_NAME));
					Log.i(TAG, "Surname : " + surname);
					// Get email
					StringBuilder selection = new StringBuilder()
							.append(Data.LOOKUP_KEY).append(" = ?")
							.append(" AND ").append(Data.MIMETYPE)
							.append(" = ?");
					String[] selArgs = { lookupKey, Email.CONTENT_ITEM_TYPE };
					Cursor c = getActivity().getContentResolver().query(
							Data.CONTENT_URI, PROJECTION_QUERY_DETAIL_EMAIL,
							selection.toString(), selArgs, null);
					Log.v(TAG, "Size of cursor of mail: " + c.getCount());
					// We only take one mail the first one
					if (c.getCount() > 0) {
						c.moveToFirst();
						eMail = c.getString(c.getColumnIndex(Email.ADDRESS));
						Log.i(TAG, "EMail : " + eMail);
					}
					// Get phone
					selection = new StringBuilder().append(Data.LOOKUP_KEY)
							.append(" = ?").append(" AND ")
							.append(Data.MIMETYPE).append(" = ?");
					selArgs = new String[] { lookupKey, Phone.CONTENT_ITEM_TYPE };
					c = getActivity().getContentResolver().query(
							Data.CONTENT_URI, PROJECTION_QUERY_DETAIL_PHONE,
							selection.toString(), selArgs, null);
					Log.v(TAG, "Size of cursor of phone: " + c.getCount());
					// We only take one mail the first one
					if (c.getCount() > 0) {
						c.moveToFirst();
						phone = c.getString(c.getColumnIndex(Phone.NUMBER));
						Log.i(TAG, "Phone : " + phone);
					}
					// Get address (if available)
					selection = new StringBuilder().append(Data.LOOKUP_KEY)
							.append(" = ?").append(" AND ")
							.append(Data.MIMETYPE).append(" = ?");
					selArgs = new String[] { lookupKey,
							StructuredPostal.CONTENT_ITEM_TYPE };
					c = getActivity().getContentResolver().query(
							Data.CONTENT_URI, PROJECTION_QUERY_DETAIL_ADDRESS,
							selection.toString(), selArgs, null);
					Log.v(TAG, "Size of cursor of address: " + c.getCount());
					// We only take one mail the first one
					if (c.getCount() > 0) {
						c.moveToFirst();
						address = c
								.getString(c
										.getColumnIndex(StructuredPostal.FORMATTED_ADDRESS));
						Log.i(TAG, "Address : " + address);
					}

					// Save contact into db
					bean = new Contact.ContactBuilder().name(name)
							.surname(surname).mail(eMail).telephone(phone)
							.address(address).build();
					try {
						bean.validate(ContactListFragment.this.getActivity());
					} catch (WeddingPlannerException e) {
						hasErrors = true;
						continue;
					}
					mListener.onValidateContact(bean,
							ContactListFragment.this.getTag());
					++count;
				}

				// Show notification
				if (!hasErrors) {
					msg = getString(
							R.string.contact_import_notification_message, count);

				} else {
					msg = getString(R.string.import_contact_validator_message);
				}

				handler.post(new Runnable() {

					@Override
					public void run() {
						bar.setVisibility(View.GONE);
						Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG)
								.show();
						okButton.setEnabled(true);

					}
				});
				data.close();
			}
		};

		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(taskDesc);
		

	}

	// -----------------------------------------------------------------------------------------
	// -- Impl�mentation LoaderCallbacks
	// -----------------------------------------------------------------------------------------

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		// load from the "Contacts table"
		Uri contentUri = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		switch (id) {
		case QUERY_LIST_ID:
		default:
			contentUri = ContactsContract.Contacts.CONTENT_URI;
			projection = PROJECTION_QUERY_LIST;
			sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
			break;
		case QUERY_DETAIL_NAMES_ID:
			contentUri = Data.CONTENT_URI;
			projection = PROJECTION_QUERY_DETAIL_NAMES;
			selection = mSelectionDetail;
			selectionArgs = mSelectionDetailArgs;
		}

		// no sub-selection, no sort order, simply every row
		// projection says we want just the _id and the name column
		return new CursorLoader(getActivity(), contentUri, projection,
				selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case QUERY_LIST_ID:
		default:
			// Once cursor is loaded, give it to adapter
			mAdapter.swapCursor(data);
			break;
		case QUERY_DETAIL_NAMES_ID:
			Log.v(TAG, "Get loader result for query contact details");
			processContacts(data);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()) {
		case QUERY_LIST_ID:
		default:
			// on reset take any old cursor away
			mAdapter.swapCursor(null);
			break;
		case QUERY_DETAIL_NAMES_ID:
			break;
		}
	}

}
