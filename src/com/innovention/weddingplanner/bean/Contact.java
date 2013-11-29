package com.innovention.weddingplanner.bean;


import org.joda.time.DateTime;

import android.util.Log;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.innovention.weddingplanner.ContactFragment;
import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;
import com.innovention.weddingplanner.utils.WeddingPlannerHelper;

/**
 * Contact bean
 * @author YCH
 *
 */
public class Contact implements IDtoBean {
	
	private final static String TAG = ContactFragment.class.getSimpleName();
	
	private int id;
	
	private String surname;
	private String name;
	private String telephone;
	private String mail;
	private String address;
	
	private Boolean inviteSent;
	private Boolean church;
	private Boolean TownHall;
	private Boolean Cocktail;
	private Boolean Party;
	
	private ResponseType response;
	
	public enum ResponseType {
		PENDING, ATTEND, NOTATTEND
	};
	
	/**
	 * Builder utility to create instance of "Contact" bean
	 * @author YCH
	 *
	 */
	public static class ContactBuilder {
		
		private String surname;
		private String name;
		private String telephone;
		private String mail;
		private String address;
		
		private Boolean inviteSent;
		private Boolean church;
		private Boolean townHall;
		private Boolean cocktail;
		private Boolean party;
		
		private Boolean pending;
		private Boolean attend;
		private Boolean notAttend;
		
		public ContactBuilder() {}
		
		public ContactBuilder surname(final String surname) {
			this.surname = surname;
			return this;
		}
		
		public ContactBuilder name(final String name) {
			this.name = name;
			return this;
		}
		
		public ContactBuilder telephone(final String telephone) {
			this.telephone = telephone;
			return this;
		}
		
		public ContactBuilder mail(final String mail) {
			this.mail = mail;
			return this;
		}
		
		public ContactBuilder address(final String address) {
			this.address = address;
			return this;
		}
		
		public ContactBuilder inviteSent(final boolean inviteSent) {
			this.inviteSent = Boolean.valueOf(inviteSent);
			return this;
		}
		
		public ContactBuilder atChurch(final boolean church) {
			this.church = Boolean.valueOf(church);
			return this;
		}
		
		public ContactBuilder atTownHall(final boolean townHall) {
			this.townHall = Boolean.valueOf(townHall);
			return this;
		}
		
		public ContactBuilder AtCocktail(final boolean cocktail) {
			this.cocktail = Boolean.valueOf(cocktail);
			return this;
		}
		
		public ContactBuilder AtParty(final boolean party) {
			this.party = Boolean.valueOf(party);
			return this;
		}
		
		public ContactBuilder answerPending(final boolean pending) {
			this.pending = Boolean.valueOf(pending);
			return this;
		}
		
		public ContactBuilder answerAttend(final boolean attend) {
			this.attend = Boolean.valueOf(attend);
			return this;
		}
		
		public ContactBuilder answerNotAttend(final boolean notAttend) {
			this.notAttend = Boolean.valueOf(notAttend);
			return this;
		}
		
		public Contact build() {
			return new Contact(this);
		}
	}
	
	private Contact(final ContactBuilder builder) {
		this.surname = builder.surname;
		this.name = builder.name;
		this.telephone = builder.telephone;
		this.mail = builder.mail;
		this.address = builder.address;
	}
	/**
	 * Validate contact bean
	 * @return 
	 */
	public void validate() throws IncorrectMailException, MissingMandatoryFieldException, IncorrectTelephoneException {
		
		WeddingPlannerHelper.validateMandatory(this.getSurname());
		WeddingPlannerHelper.validateMandatory(this.getName());
		WeddingPlannerHelper.validateEmail(this.getMail());
		WeddingPlannerHelper.validateTelephone(this.getTelephone());
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;

	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).omitNullValues()
				.add("surname", this.getSurname())
				.add("name", this.getName())
				.add("telephone", this.getTelephone())
				.add("mail", this.getMail())
				.add("address", this.getAddress())
				.toString();
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Boolean getInviteSent() {
		return inviteSent;
	}
	public void setInviteSent(Boolean inviteSent) {
		this.inviteSent = inviteSent;
	}
	public Boolean getChurch() {
		return church;
	}
	public void setChurch(Boolean church) {
		this.church = church;
	}
	public Boolean getTownHall() {
		return TownHall;
	}
	public void setTownHall(Boolean townHall) {
		TownHall = townHall;
	}
	public Boolean getCocktail() {
		return Cocktail;
	}
	public void setCocktail(Boolean cocktail) {
		Cocktail = cocktail;
	}
	public Boolean getParty() {
		return Party;
	}
	public void setParty(Boolean party) {
		Party = party;
	}
	public ResponseType getResponse() {
		return response;
	}
	public void setResponse(ResponseType response) {
		this.response = response;
	}


	
}
