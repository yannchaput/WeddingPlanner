package com.innovention.weddingplanner.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.innovention.weddingplanner.Constantes;

/**
 * {@code BootstrapReceiver} is loaded upon startup completion so as to be able
 * to trap the boot completed signal
 * @author YCH
 *
 */
public class BootstrapReceiver extends BroadcastReceiver {
	
	private static final String TAG = BootstrapReceiver.class.getSimpleName();

	/**
	 * 
	 */
	public BootstrapReceiver() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG,"Received notification boot has completed");
		
		Intent serviceIntent = new Intent(context, TaskAlarmService.class);
		serviceIntent.setAction(Constantes.TASK_SCHEDULE_NOTIF_ACTION);
		context.startService(serviceIntent);
	}

}
