package com.innovention.weddingplanner.dao;

import com.innovention.weddingplanner.bean.IDtoBean;


public interface IDao<T extends IDtoBean> {

	/**
	 * Insert info into database
	 * @param info
	 * @return
	 */
	public abstract long insert(T bean);

	/**
	 * Update info into table
	 * @param id
	 * @param info
	 * @return
	 */
	public abstract int update(int id, T bean);

	/**
	 * Remove an info item (should be only one)
	 * @param id
	 * @return
	 */
	public abstract int removeWithId(int id);

	/**
	 * Get an item of info
	 * @return
	 */
	public abstract T get();

}