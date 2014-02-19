package com.innovention.weddingplanner.exception;

public class MissingMandatoryFieldException extends WeddingPlannerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2083604296719760649L;

	public MissingMandatoryFieldException() {
		// TODO Auto-generated constructor stub
	}

	public MissingMandatoryFieldException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public MissingMandatoryFieldException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public MissingMandatoryFieldException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
