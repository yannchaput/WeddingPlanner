package com.innovention.weddingplanner.dao;


import java.util.List;

import android.database.Cursor;

import com.innovention.weddingplanner.bean.IDtoBean;


public interface IDao<T extends IDtoBean> {
	

	/**
	 * Insert info into database
	 * @param info
	 * @return
	 */
	long insert(T bean);

	/**
	 * Update info into table
	 * @param id
	 * @param info
	 * @return
	 */
	int update(int id, T bean);

	/**
	 * Remove an info item (should be only one)
	 * @param id
	 * @return
	 */
	int removeWithId(int id);

	/**
	 * Get an item
	 * @return
	 */
	T get();
	
	/**
	 * Get an item from id column
	 * @param id
	 * @return T
	 */
	T get(long id);
	
	/**
	 * Gets a cursor of the specified dataset
	 * @return cursor
	 */
	Cursor getCursor();
	
	/**
	 * Gets a cursor with a where clause statement
	 * @param selectionClause
	 * @param selectionArgs
	 * @return cursor
	 */
	Cursor getCursor(String selectionClause, String[] selectionArgs);
	
	/**
	 * Gets a cursor with a where clause statement
	 * @param selectionClause
	 * @param selectionArgs
	 * @param orderBy
	 * @return cursor
	 */
	Cursor getCursor(String selectionClause, String[] selectionArgs, String orderBy);
	
	/**
	 * Returns the list of beans stored in db
	 * @return List of <T>
	 */
	List<T> getList();
	
	/**
	 * Returns the list of beans stored in db
	 * @param selectionClause
	 * @param selectionArgs
	 * @return list of tasks
	 */
	List<T> getList(String selectionClause, String[] selectionArgs);
	
	/**
	 * Executes a raw sql query
	 * @param selectionClause
	 * @param selectionArgs
	 * @return
	 */
	Cursor rawQuery(String selectionClause, String[] selectionArgs);

}