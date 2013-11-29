package com.innovention.weddingplanner.bean;


import org.joda.time.DateTime;

import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;




public class WeddingInfo implements IDtoBean {

	private int id;
	private DateTime weddingDate = null;
	
	public WeddingInfo() {
		super();
	}
	
	public WeddingInfo(int year, int month, int day) {
		setWeddingDate(new DateTime(year, month, day, 0, 0));
	}

	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDtoBean#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.dao.IDtoBean#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the weddingDate
	 */
	public DateTime getWeddingDate() {
		return weddingDate;
	}

	/**
	 * @param weddingDate the weddingDate to set
	 */
	public void setWeddingDate(DateTime weddingDate) {
		this.weddingDate = weddingDate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		StringBuilder buffer = new StringBuilder().append("WeddingInfo").append("[")
				.append(getId()).append(",").append(getWeddingDate().toString());
		
		return buffer.toString();
	}

	@Override
	public void validate() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	
	
}
