/**
 * 
 */
package com.innovention.weddingplanner;

import com.innovention.weddingplanner.Constantes.FragmentTags;

import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.replaceFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Window dialog with 2 choices: import or create contact
 * 
 * @author ychaput
 * 
 */
public class ContactDialogFragment extends DialogFragment {

	/**
	 * Factory method
	 * 
	 * @return an instance of ContactDialogFragment
	 */
	public static ContactDialogFragment newInstance() {
		return new ContactDialogFragment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.contact_title_choice_dialog).setItems(R.array.contact_choice_dialog_entries,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					dismiss();
					// Create contact action
					replaceFragment(getActivity(), FragmentTags.TAG_FGT_CREATECONTACT);
					}
				});
		return builder.create();
	}

}
