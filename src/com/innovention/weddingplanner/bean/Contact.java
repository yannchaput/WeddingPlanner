package com.innovention.weddingplanner.bean;

import com.google.common.base.Optional;


public class Contact implements IDtoBean {
	
	private Optional<String> surname;
	private Optional<String> name;
	private Optional<String> telephone;
	private Optional<String> mail;
	private Optional<String> address;
	
	private Boolean inviteSent;
	private Boolean church;
	private Boolean TownHall;
	private Boolean Cocktail;
	private Boolean Party;
	
	private ResponseType response;
	
	public enum ResponseType {
		PENDING, ATTEND, NOTATTEND
	};
	
	public static class ContactBuilder {
		
		private Optional<String> surname;
		private Optional<String> name;
		private Optional<String> telephone;
		private Optional<String> mail;
		private Optional<String> address;
		
		private Optional<Boolean> inviteSent;
		private Optional<Boolean> church;
		private Optional<Boolean> townHall;
		private Optional<Boolean> cocktail;
		private Optional<Boolean> party;
		
		private Optional<Boolean> pending;
		private Optional<Boolean> attend;
		private Optional<Boolean> notAttend;
		
		public ContactBuilder() {}
		
		public ContactBuilder surname(final String surname) {
			this.surname = Optional.fromNullable(surname);
			return this;
		}
		
		public ContactBuilder name(final String name) {
			this.name = Optional.fromNullable(name);
			return this;
		}
		
		public ContactBuilder telephone(final String telephone) {
			this.telephone = Optional.fromNullable(telephone);
			return this;
		}
		
		public ContactBuilder mail(final String mail) {
			this.mail = Optional.fromNullable(mail);
			return this;
		}
		
		public ContactBuilder address(final String address) {
			this.address = Optional.fromNullable(address);
			return this;
		}
		
		public ContactBuilder inviteSent(final boolean inviteSent) {
			this.inviteSent = Optional.fromNullable(Boolean.valueOf(inviteSent));
			return this;
		}
		
		public ContactBuilder atChurch(final boolean church) {
			this.church = Optional.fromNullable(Boolean.valueOf(church));
			return this;
		}
		
		public ContactBuilder atTownHall(final boolean townHall) {
			this.townHall = Optional.fromNullable(Boolean.valueOf(townHall));
			return this;
		}
		
		public ContactBuilder AtCocktail(final boolean cocktail) {
			this.cocktail = Optional.fromNullable(Boolean.valueOf(cocktail));
			return this;
		}
		
		public ContactBuilder AtParty(final boolean party) {
			this.party = Optional.fromNullable(Boolean.valueOf(party));
			return this;
		}
		
		public ContactBuilder answerPending(final boolean pending) {
			this.pending = Optional.fromNullable(Boolean.valueOf(pending));
			return this;
		}
		
		public ContactBuilder answerAttend(final boolean attend) {
			this.attend = Optional.fromNullable(Boolean.valueOf(attend));
			return this;
		}
		
		public ContactBuilder answerNotAttend(final boolean notAttend) {
			this.notAttend = Optional.fromNullable(Boolean.valueOf(notAttend));
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
	
	public void validate() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Validate method not implemented yet");
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub

	}

}
