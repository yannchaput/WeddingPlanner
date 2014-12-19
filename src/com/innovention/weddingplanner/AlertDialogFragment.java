package com.innovention.weddingplanner;

import static com.innovention.weddingplanner.Constantes.KEY_ALERT_TITLE;
import static com.innovention.weddingplanner.Constantes.KEY_MSG;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Custom alert dialog window
 * 
 * @author YCH
 * 
 */
public class AlertDialogFragment extends DialogFragment {

	/**
	 * Singleton
	 * 
	 * @param title
	 * @return fragment
	 */
	public static AlertDialogFragment newInstance(int title, int msg) {
		AlertDialogFragment fgt = new AlertDialogFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_ALERT_TITLE, title);
		args.putInt(KEY_MSG, msg);
		fgt.setArguments(args);
		return fgt;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int title = getArguments().getInt(KEY_ALERT_TITLE);
		int msg = getArguments().getInt(KEY_MSG);
		return new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(R.string.alert_dialog_validate,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create();
	}

}
