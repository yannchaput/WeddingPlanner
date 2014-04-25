package com.innovention.weddingplanner.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_DESC;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_REMINDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_REMINDOPTION;
import static com.innovention.weddingplanner.dao.ConstantesDAO.COL_TASK_STATUS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_ID;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DESC;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_DUEDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_REMINDDATE;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_REMINDOPTION;
import static com.innovention.weddingplanner.dao.ConstantesDAO.NUM_COL_TASK_STATUS;
import static com.innovention.weddingplanner.dao.ConstantesDAO.TABLE_TASKS;
import static com.innovention.weddingplanner.dao.DatabaseHelper.convertIntToBool;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.innovention.weddingplanner.bean.Task;

public class TasksDao implements IDao<Task> {
	
	private static final String TAG=TasksDao.class.getSimpleName();
	
	private Context context;

	public TasksDao(final Context ctxt) {
		this.context = ctxt;
	}

	/**
	 * Inserts an entity task into table
	 * @param task
	 * @return id
	 */
	@Override
	public long insert(Task bean) {
		checkNotNull(bean,"Task bean to save can not be null");
		Log.d(TAG, "insert - " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_TASK_STATUS, bean.getActive());
		values.put(COL_TASK_DESC, bean.getDescription());
		values.put(COL_TASK_DUEDATE, (null != bean.getDueDate()) ? bean.getDueDate().toString() : "");
		values.put(COL_TASK_REMINDDATE, (null != bean.getRemindDate()) ? bean.getRemindDate().toString() : "");
		values.put(COL_TASK_REMINDOPTION, (null != bean.getRemindChoice()) ? bean.getRemindChoice() : "");
		return DaoLocator.getInstance(context).getWritableDatabase()
				.insert(TABLE_TASKS, null, values);
	}

	/**
	 * Updates a record of tasks table
	 * @param id
	 * @param task
	 * @return number of affected rows
	 */
	@Override
	public int update(int id, Task bean) {
		checkNotNull(bean,"Task bean to save can not be null");
		checkArgument(id > 0, "An id can not be negative");
		Log.d(TAG, "update - " + "Update table with id " + id + " and bean " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_TASK_STATUS, bean.getActive());
		values.put(COL_TASK_DESC, bean.getDescription());
		values.put(COL_TASK_DUEDATE, (null != bean.getDueDate()) ? bean.getDueDate().toString() : "");
		values.put(COL_TASK_REMINDDATE, (null != bean.getRemindDate()) ? bean.getRemindDate().toString() : "");
		values.put(COL_TASK_REMINDOPTION, (null != bean.getRemindChoice()) ? bean.getRemindChoice() : "");
		int result = DaoLocator.getInstance(context).getWritableDatabase()
				.update(TABLE_TASKS, values, COL_ID + "=?", new String[] {String.valueOf(id)});
		Log.d(TAG, "" + result + "rows affected");
		return result;
	}

	/**
	 * Delete the record with specified id
	 * @param id
	 * @return number of affected rows
	 */
	@Override
	public int removeWithId(int id) {
		return DaoLocator.getInstance(context).getWritableDatabase()
				.delete(TABLE_TASKS, COL_ID + " = " + id, null);
	}

	@Override
	public Task get() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Task get(long id) {
		
		Task entity = null;
		Cursor c = DaoLocator.getInstance(context).getReadDatabase()
				.query(TABLE_TASKS, new String[] {COL_ID, COL_TASK_STATUS, COL_TASK_DESC,
				COL_TASK_DUEDATE, COL_TASK_REMINDDATE, COL_TASK_REMINDOPTION}
		, COL_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
		
		if (null == c || c.getCount() == 0){
			Log.d(TAG, "No data available");
			return null;
		}
		
		if (c.moveToFirst()) {
			String dueDateTxt = c.getString(NUM_COL_TASK_DUEDATE);
			String remindTxt = c.getString(NUM_COL_TASK_REMINDDATE);
			String remindOptionTxt = c.getString(NUM_COL_TASK_REMINDOPTION);
			entity = new Task.Builder().withId(c.getInt(NUM_COL_ID))
			.setActive(convertIntToBool(c.getInt(NUM_COL_TASK_STATUS)))
			.withDesc(c.getString(NUM_COL_TASK_DESC))
			.dueDate( !isEmpty(dueDateTxt) ? DateTime.parse(c.getString(NUM_COL_TASK_DUEDATE)) : null)
			.remind( !isEmpty(remindTxt) ? DateTime.parse(c.getString(NUM_COL_TASK_REMINDDATE)): null)
			.remindOption(remindOptionTxt)
			.build();
		}
		
		c.close();
		
		return entity;
	}

	/**
	 * Returns a cursor with the table content
	 * Take care of closing it
	 * @return the cursor
	 */
	@Override
	public Cursor getCursor() {
		return DaoLocator.getInstance(context).getReadDatabase()
				.query(TABLE_TASKS, new String[] { COL_ID, COL_TASK_STATUS, COL_TASK_DESC,
				COL_TASK_DUEDATE, COL_TASK_REMINDDATE, COL_TASK_REMINDOPTION }, null, null, null,
				null, null);
	}
	
	@Override
	public Cursor getCursor(String selectionClause, String[] selectionArgs) {
		return DaoLocator.getInstance(context).getReadDatabase()
				.query(TABLE_TASKS, new String[] { COL_ID, COL_TASK_STATUS, COL_TASK_DESC,
				COL_TASK_DUEDATE, COL_TASK_REMINDDATE, COL_TASK_REMINDOPTION }, selectionClause, selectionArgs, null,
				null, null);
	}

	@Override
	public List<Task> getList() {
		return getList(null, null);
	}

	/**
	 * Returns a list of tasks
	 * @param the where clause
	 * @param the optional argument widlcards
	 */
	@Override
	public List<Task> getList(String selectionClause, String[] selectionArgs) {
		
		Cursor c = getCursor(selectionClause, selectionArgs);
		ArrayList<Task> list = new ArrayList<Task>();
		Task current = null;
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			current = new Task.Builder().withId(c.getInt(NUM_COL_ID))
					.withDesc(c.getString(NUM_COL_TASK_DESC))
					.dueDate(DateTime.parse(c.getString(NUM_COL_TASK_DUEDATE)))
					.remind(DateTime.parse(c.getString(NUM_COL_TASK_REMINDDATE)))
					.remindOption(c.getString(NUM_COL_TASK_REMINDDATE))
					.setActive(convertIntToBool(c.getInt(NUM_COL_TASK_STATUS)))
					.build();
			list.add(current);
		}
		c.close();
		
		return list;
	}

}
