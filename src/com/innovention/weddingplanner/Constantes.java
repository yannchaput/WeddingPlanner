package com.innovention.weddingplanner;

public interface Constantes {
	
	Boolean DEBUG = Boolean.TRUE;
	
	/**
	 * List of tags to identify fragments. Also serves to identify the mode
	 * of the mask, i.e. create/edit
	 * @author YCH
	 *
	 */
	enum FragmentTags {
		TAG_FGT_CREATECONTACT("createContactFgt"),
		TAG_FGT_UPDATECONTACT("updateContactFgt"),
		TAG_FGT_DELETECONTACT("deleteContactFgt"),
		TAG_FGT_GUESTLIST("guestListFgt"),
		TAG_FGT_GUESTALERT("guestAlertFgt"),
		TAG_FGT_MAINDATEPICKER("mainDatePickerDlgFgt"),
		TAG_FGT_TASKDATEPICKER("taskDatePickerDlgFgt"),
		TAG_FGT_TASKDATEPICKER_UPDATE("taskDatePickerUpdateDlgFgt"),
		TAG_FGT_CREATETASK("createTaskFgt"),
		TAG_FGT_UPDATETASK("updateTaskFgt"),
		TAG_FGT_DELETETASK("deleteTaskFgt"),
		TAG_FGT_TASKLIST("taskListFgt"),
		TAG_SERV_TASKALARM("taskAlarmService"),
		TAG_FGT_CREATE_VENDOR("createVendorFgt"),
		TAG_FGT_UPDATE_VENDOR("updateVendorFgt"),
		TAG_FGT_VENDORLIST("vendorListFgt"),
		TAG_FGT_BUDGET_LIST("budgetListFgt"),
		TAG_FGT_CREATE_BUDGET("createBudgetFgt"),
		TAG_FGT_UPDATE_BUDGET("updateBudgetFgt");
		
		private final String tag;
		
		private FragmentTags(final String tagValue) {
			this.tag = tagValue;
		}
		
		public String getValue() {
			return tag;
		}
	}
	
	// AdMob Ids
	String AD_ID = "ca-app-pub-8815791754201326/7427489893";
	
	// Ids
	int DEFAULT_MONTHS_INTERVAL = 9;
	String KEY_DTPICKER_Y = "Year";
	String KEY_DTPICKER_M = "Month";
	String KEY_DTPICKER_D = "Day";
	String KEY_ALERT_TITLE="alert_title";
	String KEY_MSG="validator_message";
	
	// Values
	int TASK_DEFAULT_REMIND_HOUR = 12;
	
	// Intents
	String TASK_SCHEDULE_NOTIF_ACTION = "scheduleNotifTask"; 
	String TASK_REMOVE_NOTIF_ACTION = "RemoveNotifTask";
	
	// Ressources
	String FONT_CURVED = "GreatVibes-Regular.otf";

	// Exception messages
	String MISSING_MANADTORY_FIELD_EX="Champs obligatoire manquant";
	String ILLEGAL_EMAIL_EX="Adresse mail incorrecte";
	String ILLEGAL_PHONE_EX="Numéro de téléphone incorrect";
	String INCORRECT_NUMBER_ARGS_EX="Incorrect number of arguments";
	String INCONSISTENT_FIELD_EX = "Inconsistent combination of fields";
	String ILLEGAL_URI = "Unsupported URI";
	String BACKUP_ERROR = "Unable to backup database. An error occurred during backup process. All data probably lost.";
	String BACKUP_ERROR_FILENOTFOUND = "Unable to backup database because one of the two files were not available.";
	
}
