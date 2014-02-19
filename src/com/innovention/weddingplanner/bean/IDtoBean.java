package com.innovention.weddingplanner.bean;

import android.content.Context;

import com.innovention.weddingplanner.exception.WeddingPlannerException;

public interface IDtoBean {

	/**
	 * @return the id
	 */
	int getId();

	/**
	 * @param id the id to set
	 */
	void setId(int id);
	
	/**
	 * Validate the bean inputs
	 * @throws UnsupportedOperationException if not implemented
	 * @throws WeddingPlannerException 
	 */
	void validate(Context ctx) throws UnsupportedOperationException, WeddingPlannerException;

}