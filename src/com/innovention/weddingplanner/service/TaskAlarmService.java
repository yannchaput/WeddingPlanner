/**
 * 
 */
package com.innovention.weddingplanner.service;

import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_REMINDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_STATUS;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.innovention.weddingplanner.Constantes;
import com.innovention.weddingplanner.R;
import com.innovention.weddingplanner.bean.Task;
import com.innovention.weddingplanner.dao.DaoLocator;
import com.innovention.weddingplanner.dao.DaoLocator.SERVICES;
import com.innovention.weddingplanner.dao.TasksDao;

/**
 * TaskAlarmService aims at loading all ongoing tasks and schedule them inside the 
 * AlarmManager
 * @author YCH
 *
 */
public class TaskAlarmService extends Service {
	
	private static final String TAG = TaskAlarmService.class.getSimpleName();

	/**
	 * 
	 */
	public TaskAlarmService() {
		super();
	}

	
	/**
	 * Executed when service is requested for scheduling tasks or delete them
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d(TAG, "Start alert manager service");
		
		TasksDao dao = DaoLocator.getInstance(this).get(
				SERVICES.TASK);

		// Schedules tasks
		if (Constantes.TASK_SCHEDULE_NOTIF_ACTION.equals(intent.getAction())) {
			
			Log.d(TAG, "Schedule tasks");

			AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent receiverIntent = null;
			PendingIntent alarmIntent = null;

			String whereClause = new StringBuilder().append(COL_TASK_STATUS)
					.append("=1").append(" AND ").append(COL_TASK_REMINDDATE)
					.append(" <> ''").toString();
			
			List<Task> taskList = dao.getList(whereClause, null);

			for (Task task : taskList) {
				Log.v(TAG, "Set an alarm for reminder " + task.getId());
				receiverIntent = new Intent(this, TaskAlarmReceiver.class);
				receiverIntent.putExtra(Task.KEY_VALUE, task);
				alarmIntent = PendingIntent.getBroadcast(this, task.getId(),
						receiverIntent, 0);
				alarmMgr.set(AlarmManager.RTC,
						task.getRemindDate().getMillis(), alarmIntent);
			}
		}
		// Deschedule task upon notification tap
		else if (Constantes.TASK_REMOVE_NOTIF_ACTION.equals(intent.getAction())) {
			Log.d(TAG, "Remove reminder upon notification delete");
			Task task = intent.getExtras().getParcelable(Task.KEY_VALUE);
			// Reset reminder field so as to not firing up another alarm next time at reboot
			task.setRemindChoice(getResources().getString(R.string.task_spinner_item1));
			task.setRemindDate(null);
			dao.update(task.getId(), task);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
