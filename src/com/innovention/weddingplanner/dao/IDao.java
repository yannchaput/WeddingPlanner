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
	 * @return
	 */
	T get(long id);
	
	/**
	 * Gets a cursor of the specified dataset
	 * @return
	 */
	Cursor getCursor();
	
	/**
	 * Returns the list of beans stored in db
	 * @return
	 */
	List<T> getList();

}