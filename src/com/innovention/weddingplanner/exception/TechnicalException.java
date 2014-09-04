/**
 * 
 */
package com.innovention.weddingplanner.exception;

/**
 * @author Yann
 *
 */
public class TechnicalException extends WeddingPlannerException {

	/**
	 * 
	 */
	public TechnicalException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 */
	public TechnicalException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param throwable
	 */
	public TechnicalException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public TechnicalException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
