package com.innovention.weddingplanner.bean;

import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

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
	 * @throws IncorrectMailException 
	 * @throws IncorrectTelephoneException 
	 */
	void validate() throws UnsupportedOperationException, IncorrectMailException, MissingMandatoryFieldException, IncorrectTelephoneException;

}