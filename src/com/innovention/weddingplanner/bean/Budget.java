
package com.innovention.weddingplanner.bean;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import android.content.Context;
import android.database.Cursor;

import com.innovention.weddingplanner.bean.Vendor.Builder;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.exception.WeddingPlannerException;

/**
 * Budget bean
 * @author YCH
 *
 */
public class Budget implements IDtoBean {
	
	private int id;
	private String description;
	private String vendor;
	private String category;
	private double amount;
	private double paid;
	private String note;
	
	/**
	 * Transformer class utility
	 * @author YCH
	 *
	 */
	public static class Transformer {
		
		private Transformer() {
		}
		
		public static Budget transform(final Cursor c) {
			checkNotNull(c, "Cursor is null");
			checkArgument(c.getCount() > 0, "Cursor can not be empty");
			c.moveToFirst();
			Budget transformee = new Budget();
			transformee.setId(c.getInt(ConstantesDAO.NUM_COL_ID));
			transformee.setDescription(c.getString(ConstantesDAO.NUM_COL_BUDGET_EXPENSE));
			transformee.setVendor(c.getString(ConstantesDAO.NUM_COL_BUDGET_VENDOR));
			transformee.setCategory(c.getString(ConstantesDAO.NUM_COL_BUDGET_CATEGORY));
			transformee.setAmount(c.getDouble(ConstantesDAO.NUM_COL_BUDGET_TOTAL_AMOUNT));
			transformee.setPaid(c.getDouble(ConstantesDAO.NUM_COL_BUDGET_PAID_AMOUNT));
			transformee.setNote(c.getString(ConstantesDAO.NUM_COL_BUDGET_NOTE));
			
			return transformee;
		}
	}

	/**
	 * Default constructor
	 */
	public Budget() {
	}

	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.bean.IDtoBean#getId()
	 */
	@Override
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.bean.IDtoBean#setId(int)
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the vendor
	 */
	public String getVendor() {
		return vendor;
	}

	/**
	 * @param vendor the vendor to set
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the paid
	 */
	public double getPaid() {
		return paid;
	}

	/**
	 * @param paid the paid to set
	 */
	public void setPaid(double paid) {
		this.paid = paid;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/* (non-Javadoc)
	 * @see com.innovention.weddingplanner.bean.IDtoBean#validate(android.content.Context)
	 */
	@Override
	public void validate(Context ctx) throws WeddingPlannerException {
		throw new UnsupportedOperationException("No method validate implemented for Budget class");
	}

}
