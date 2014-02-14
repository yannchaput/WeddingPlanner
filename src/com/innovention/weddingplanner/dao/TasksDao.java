package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.*;
import static com.innovention.weddingplanner.dao.DatabaseHelper.convertIntToBool;
import static com.google.common.base.Preconditions.*;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.innovention.weddingplanner.bean.Task;

public class TasksDao implements IDao<Task> {
	
	private static final String TAG=TasksDao.class.getSimpleName();
	
	private SQLiteDatabase db;

	public TasksDao(final SQLiteDatabase db) {
		this.db = db;
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
		if (null != bean.getDueDate()) values.put(COL_TASK_DUEDATE, bean.getDueDate().toString());
		if (null != bean.getRemindDate()) values.put(COL_TASK_REMINDDATE, bean.getRemindDate().toString());
		if (null != bean.getRemindChoice()) values.put(COL_TASK_REMINDOPTION, bean.getRemindChoice());
		return db.insert(TABLE_TASKS, null, values);
	}

	/**
	 * Updates a record of tasks table
	 * @param id
	 * @param task
	 * @return number of affected rows
	 */
	@Override
	public int update(int id, Task bean) {
		Log.d(TAG, "update - " + "Update table with id " + id + " and bean " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_TASK_STATUS, bean.getActive());
		values.put(COL_TASK_DESC, bean.getDescription());
		if (null != bean.getDueDate()) values.put(COL_TASK_DUEDATE, bean.getDueDate().toString());
		if (null != bean.getRemindDate()) values.put(COL_TASK_REMINDDATE, bean.getRemindDate().toString());
		if (null != bean.getRemindChoice()) values.put(COL_TASK_REMINDOPTION, bean.getRemindChoice());
		int result = db.update(TABLE_TASKS, values, COL_ID + "=?", new String[] {String.valueOf(id)});
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
		return db.delete(TABLE_TASKS, COL_ID + " = " + id, null);
	}

	@Override
	public Task get() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Task get(long id) {
		
		Task entity = null;
		Cursor c = db.query(TABLE_TASKS, new String[] {COL_ID, COL_TASK_STATUS, COL_TASK_DESC,
				COL_TASK_DUEDATE, COL_TASK_REMINDDATE, COL_TASK_REMINDOPTION}
		, COL_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);
		
		if (null == c || c.getCount() == 0){
			Log.d(TAG, "No data available");
			return null;
		}
		
		if (c.moveToFirst()) {
			entity = new Task.Builder().withId(c.getInt(NUM_COL_ID))
			.setActive(convertIntToBool(c.getInt(NUM_COL_TASK_STATUS)))
			.withDesc(c.getString(NUM_COL_TASK_DESC))
			.dueDate(c.getString(NUM_COL_TASK_DUEDATE) != null ? DateTime.parse(c.getString(NUM_COL_TASK_DUEDATE)) : null)
			.remind(c.getString(NUM_COL_TASK_REMINDDATE) != null ? DateTime.parse(c.getString(NUM_COL_TASK_REMINDDATE)): null)
			.remindOption(c.getString(NUM_COL_TASK_REMINDOPTION))
			.build();
		}
		
		c.close();
		
		return entity;
	}

	/**
	 * Returns a cursor with the table content
	 */
	@Override
	public Cursor getCursor() {
		return db.query(TABLE_TASKS, new String[] { COL_ID, COL_TASK_STATUS, COL_TASK_DESC,
				COL_TASK_DUEDATE, COL_TASK_REMINDDATE, COL_TASK_REMINDOPTION }, null, null, null,
				null, null);
	}

	@Override
	public List<Task> getList() {
		throw new UnsupportedOperationException();
	}

}
