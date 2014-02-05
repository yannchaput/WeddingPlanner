package com.innovention.weddingplanner.dao;

import static com.innovention.weddingplanner.dao.ConstantesDAO.*;
import static com.google.common.base.Preconditions.*;
import java.util.List;

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
		values.put(COL_TASK_DESC, bean.getDescription());
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
		values.put(COL_TASK_DESC, bean.getDescription());
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
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a cursor with the table content
	 */
	@Override
	public Cursor getCursor() {
		return db.query(TABLE_TASKS, new String[] {COL_ID, COL_TASK_DESC}
		, null, null, null, null, null);
	}

	@Override
	public List<Task> getList() {
		throw new UnsupportedOperationException();
	}

}
