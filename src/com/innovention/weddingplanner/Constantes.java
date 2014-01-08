package com.innovention.weddingplanner;

public interface Constantes {
	
	/**
	 * List of tags to identify fragments
	 * @author YCH
	 *
	 */
	enum FragmentTags {
		TAG_FGT_CREATECONTACT("createContactFgt"),
		TAG_FGT_UPDATECONTACT("updateContactFgt"),
		TAG_FGT_DELETECONTACT("deleteContactFgt"),
		TAG_FGT_GUESTLIST("guestListFgt"),
		TAG_FGT_GUESTALERT("guestAlertFgt"),
		TAG_FGT_DATEPICKER("datePickerDlgFgt");
		
		private final String tag;
		
		private FragmentTags(final String tagValue) {
			this.tag = tagValue;
		}
		
		public String getValue() {
			return tag;
		}
	}
	
	// Ids
	int DEFAULT_MONTHS_INTERVAL = 9;
	String KEY_DTPICKER_Y = "Year";
	String KEY_DTPICKER_M = "Month";
	String KEY_DTPICKER_D = "Day";
	String KEY_ALERT_TITLE="alert_title";
	String KEY_MSG="validator_message";
	
	// Ressources
	String FONT_CURVED = "GreatVibes-Regular.otf";

	// Exception messages
	String MISSING_MANADTORY_FIELD_MSG="Champs obligatoire manquant";
	String ILLEGAL_EMAIL_MSG="Adresse mail incorrecte";
	String ILLEGAL_PHONE_MSG="Numéro de téléphone incorrect";
	String INCORRECT_NUMBER_ARGS="Incorrect number of arguments";
	
}
