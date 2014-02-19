package com.innovention.weddingplanner.bean;


import android.content.Context;

import com.google.common.base.Objects;
import com.innovention.weddingplanner.ContactFragment;
import com.innovention.weddingplanner.exception.IncorrectMailException;
import com.innovention.weddingplanner.exception.IncorrectTelephoneException;
import com.innovention.weddingplanner.exception.MissingMandatoryFieldException;
import static com.innovention.weddingplanner.utils.WeddingPlannerHelper.*;

/**
 * Contact bean
 * @author YCH
 *
 */
public class Contact implements IDtoBean {
	
	private final static String TAG = Contact.class.getSimpleName();
	
	private int id;
	
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
	
	private ResponseType response;
	
	public enum ResponseType {
		PENDING, ATTEND, NOTATTEND;
		
		public static ResponseType toEnum(String value) {
			return ResponseType.valueOf(value);
		}
		
	}
	
	/**
	 * Builder utility to create instance of "Contact" bean
	 * @author YCH
	 *
	 */
	public static class ContactBuilder {
		
		private int id = -1; // Only for dao purpose
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
		
		public ContactBuilder withId(final int id) {
			this.id = id;
			return this;
		}
		
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
		
		private ResponseType getEnumValue() {
			if (attend == null) return null;
			else if(attend.booleanValue()) return ResponseType.ATTEND;
			else if (notAttend.booleanValue()) return ResponseType.NOTATTEND;
			else return ResponseType.PENDING;
		}
		
	}
	
	/**
	 * Constructor (from a builder object)
	 * @param builder: builder attached to this object
	 */
	private Contact(final ContactBuilder builder) {
		this.id = builder.id;
		this.surname = builder.surname;
		this.name = builder.name;
		this.telephone = builder.telephone;
		this.mail = builder.mail;
		this.address = builder.address;
		this.inviteSent = builder.inviteSent;
		this.church = builder.church;
		this.cocktail = builder.cocktail;
		this.townHall = builder.townHall;
		this.party = builder.party;
		this.response = builder.getEnumValue();
	}
	/**
	 * Validate contact bean
	 * @return 
	 */
	public void validate(Context ctx) throws IncorrectMailException, MissingMandatoryFieldException, IncorrectTelephoneException {
		
		validateMandatory(this.getSurname());
		validateMandatory(this.getName());
		validateEmail(this.getMail());
		validateTelephone(this.getTelephone());
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
				.add("id", getId())
				.add("surname", this.getSurname())
				.add("name", this.getName())
				.add("telephone", this.getTelephone())
				.add("mail", this.getMail())
				.add("address", this.getAddress())
				.add("inviteSent", this.getInviteSent())
				.add("church", this.getChurch())
				.add("cocktail", this.getCocktail())
				.add("townhall", this.getTownHall())
				.add("party", this.getParty())
				.add("response", this.getResponse())
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
		return townHall;
	}
	public void setTownHall(Boolean townHall) {
		this.townHall = townHall;
	}
	public Boolean getCocktail() {
		return cocktail;
	}
	public void setCocktail(Boolean cocktail) {
		this.cocktail = cocktail;
	}
	public Boolean getParty() {
		return party;
	}
	public void setParty(Boolean party) {
		this.party = party;
	}
	public ResponseType getResponse() {
		return response;
	}
	public void setResponse(ResponseType response) {
		this.response = response;
	}


	
}
