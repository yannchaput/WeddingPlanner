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

	@Override
	public long insert(Task bean) {
		checkNotNull(bean,"Task bean to save can not be null");
		Log.d(TAG, "insert - " + bean.toString());
		ContentValues values = new ContentValues();
		values.put(COL_TASK_DESC, bean.getDescription());
		return db.insert(TABLE_TASKS, null, values);
	}

	@Override
	public int update(int id, Task bean) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeWithId(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Task get() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor getCursor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> getList() {
		// TODO Auto-generated method stub
		return null;
	}

}
