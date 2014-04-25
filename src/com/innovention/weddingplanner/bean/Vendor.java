/**
 * 
 */
package com.innovention.weddingplanner.bean;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.validateEmail;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.validateMandatory;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.validateTelephone;
import android.content.Context;
import android.database.Cursor;

import com.google.common.base.Objects;
import com.innovention.weddingplanner.dao.ConstantesDAO;
import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;

/**
 * Vendor bean class
 * @author YCH
 *
 */
public class Vendor implements IDtoBean {

	private int id;
	private String companyName;
	private String contactDetails;
	private String address;
	private String telephone;
	private String mail;
	private String category;
	private String note;
	
	/**
	 * Transformer/Factory class. Creates a vendor object from a cursor
	 * @author YCH
	 *
	 */
	public static class Transformer {
		
		/**
		 * Constructor not visible
		 */
		private Transformer() {
			
		}
		
		/**
		 * Transform a cursor into a vendor object
		 * @param c
		 * @return
		 */
		public static Vendor transform(final Cursor c) {
			checkNotNull(c, "Cursor is null");
			checkArgument(c.getCount() > 0, "Cursor can not be empty");
			c.moveToFirst();
			Vendor transformee = new Builder()
				.addId(c.getInt(ConstantesDAO.NUM_COL_ID))
				.addCompany(c.getString(ConstantesDAO.NUM_COL_VENDOR_COMPANY))
				.addContact(c.getString(ConstantesDAO.NUM_COL_VENDOR_CONTACT))
				.addAddress(c.getString(ConstantesDAO.NUM_COL_VENDOR_ADDRESS))
				.addMail(c.getString(ConstantesDAO.NUM_COL_VENDOR_MAIL))
				.addTelephone(c.getString(ConstantesDAO.NUM_COL_VENDOR_PHONE))
				.addCategory(c.getString(ConstantesDAO.NUM_COL_VENDOR_CATEGORY))
				.addNote(c.getString(ConstantesDAO.NUM_COL_VENDOR_NOTE))
				.build();
			
			return transformee;
		}
	}
	
	/**
	 * Builder utility class
	 * @author YCH
	 *
	 */
	public static class Builder implements org.apache.commons.lang3.builder.Builder<Vendor> {
		
		private Vendor instance;

		public Builder() {
			instance = new Vendor();
		}
		
		public Builder addId(final int id) {
			instance.id = id;
			return this;
		}
		
		public Builder addCompany(String name) {
			instance.companyName = name;
			return this;
		}
		
		public Builder addContact(String details) {
			instance.contactDetails = details;
			return this;
		}
		
		public Builder addAddress(String address) {
			instance.address = address;
			return this;
		}
		
		public Builder addTelephone(String tel) {
			instance.telephone = tel;
			return this;
		}
		
		public Builder addMail(String mail) {
			instance.mail = mail;
			return this;
		}
		
		public Builder addCategory(String category) {
			instance.category = category;
			return this;
		}
		
		public Builder addNote(String note) {
			instance.note = note;
			return this;
		}
		
		@Override
		public Vendor build() {
			return instance;
		}
		
	}
	
	/**
	 * 
	 */
	public Vendor() {
		// TODO Auto-generated constructor stub
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
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the contactDetails
	 */
	public String getContactDetails() {
		return contactDetails;
	}

	/**
	 * @param contactDetails the contactDetails to set
	 */
	public void setContactDetails(String contactDetails) {
		this.contactDetails = contactDetails;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.innovention.weddingplanner.bean.IDtoBean#validate(android.content
	 * .Context)
	 */
	@Override
	public void validate(Context ctx) throws MissingMandatoryFieldException,
			IncorrectTelephoneException, IncorrectMailException {
		checkNotNull(ctx, "Context can not be null");
		validateMandatory(getCompanyName());
		validateEmail(getMail());
		validateTelephone(getTelephone());

	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this).omitNullValues()
				.add("id", getId())
				.add("company", getCompanyName())
				.add("contact", getContactDetails())
				.add("address", getAddress())
				.add("telephone", getTelephone())
				.add("mail", getMail())
				.add("category", getCategory())
				.add("note", getNote())
				.toString();
	}


}
