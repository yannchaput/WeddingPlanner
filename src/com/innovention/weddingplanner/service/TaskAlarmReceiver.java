/**
 * 
 */
package com.innovention.weddingplanner.service;

import org.joda.time.format.DateTimeFormat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.innovention.weddingplanner.Constantes;
import com.innovention.weddingplanner.R;
import com.innovention.weddingplanner.TaskActivity;
import com.innovention.weddingplanner.bean.Task;

/**
 * This class handles the alarm received from the Task scheduled in AlarmManager
 * @see TaskAlarmService
 * @author YCH
 *
 */
public class TaskAlarmReceiver extends BroadcastReceiver {
	
	private static final String TAG = TaskAlarmReceiver.class.getSimpleName();
	

	/**
	 * 
	 */
	public TaskAlarmReceiver() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received an alarm for notification");
		createNotification(context, (Task) intent.getParcelableExtra(Task.KEY_VALUE));
	}
	
	private void createNotification(final Context context, final Task task) {
		
		NotificationManager notifMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Build intent to execute on tap
		Intent appIntent = new Intent(context, TaskActivity.class);
		appIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, task.getId(), appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Intent deleteIntent = new Intent(context, TaskAlarmService.class);
		deleteIntent.setAction(Constantes.TASK_REMOVE_NOTIF_ACTION);
		deleteIntent.putExtra(Task.KEY_VALUE, task);
		PendingIntent deletePendingIntent = PendingIntent.getService(context, task.getId(), deleteIntent, PendingIntent.FLAG_ONE_SHOT);
		
		// Notification
		Notification notification = new NotificationCompat.Builder(context)
			.setContentTitle(context.getResources().getString(R.string.task_notification_title))
			.setContentText(task.getDescription())
			.setContentInfo(DateTimeFormat.fullDate().print(task.getDueDate()))
			.setTicker("Rappel pour le mariage")
			.setContentIntent(pendingIntent)
			.setDeleteIntent(deletePendingIntent)
			.setSmallIcon(R.drawable.ic_notification_icon)
			.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_large_icon))
			.setWhen(task.getRemindDate().getMillis())
			.setAutoCancel(true)
			.setDefaults(Notification.DEFAULT_VIBRATE)
			.setLights(context.getResources().getColor(R.color.Yellow), 1000, 5000)
			.build();
		
		notifMgr.notify(task.getId(), notification);
	}

}
