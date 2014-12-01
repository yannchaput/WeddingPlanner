package com.innovention.weddingplanner.dao;

public interface ConstantesDAO {

	// Table name
	String TABLE_WEDDINGINFO = "table_weddingInfo";
	String TABLE_GUESTS = "table_guests";
	String TABLE_TASKS = "table_tasks";
	String TABLE_VENDORS = "table_vendors";
	String TABLE_BUDGET = "table_budget";
	
	// General
	String COL_ID ="_id";
	byte NUM_COL_ID = 0;
	
	// Columns wedding info
	String COL_WEDDATE = "weddate";
	byte NUM_COL_WEDDATE = 1;
	
	// Columns guest table
	String COL_SURNAME="surname";
	byte NUM_COL_SURNAME = 1;
	String COL_NAME="name";
	byte NUM_COL_NAME = 2;
	String COL_TEL="telephone";
	byte NUM_COL_TEL = 3;
	String COL_EMAIL="email";
	byte NUM_COL_MAIL = 4;
	String COL_ADDRESS="address";
	byte NUM_COL_ADDRESS = 5;
	String COL_INVITATION="invitation";
	byte NUM_COL_INVITATION = 6;
	String COL_CHURCH="church";
	byte NUM_COL_CHURCH = 7;
	String COL_TOWNHALL="townhall";
	byte NUM_COL_TOWNHALL = 8;
	String COL_COCKTAIL="cocktail";
	byte NUM_COL_COCKTAIL = 9;
	String COL_PARTY="party";
	byte NUM_COL_PARTY = 10;
	String COL_RSVP="rsvp";
	byte NUM_COL_RSVP = 11;
	String COL_FAMILY="family";
	byte NUM_COL_FAMILY = 12;
	String COL_FRIEND="friend";
	byte NUM_COL_FRIEND = 13;
	String COL_COLLEGUE="collegue";
	byte NUM_COL_COLLEGUE = 14;
	String COL_OTHER="other";
	byte NUM_COL_OTHER = 15;
	
	// Columns tasks table
	String COL_TASK_STATUS = "active";
	byte NUM_COL_TASK_STATUS = NUM_COL_ID + 1; 
	String COL_TASK_DESC = "description";
	byte NUM_COL_TASK_DESC = NUM_COL_TASK_STATUS + 1; 
	String COL_TASK_DUEDATE = "duedate";
	byte NUM_COL_TASK_DUEDATE = NUM_COL_TASK_DESC + 1;
	String COL_TASK_REMINDDATE = "reminddate";
	byte NUM_COL_TASK_REMINDDATE = NUM_COL_TASK_DUEDATE + 1;
	String COL_TASK_REMINDOPTION = "remindoption";
	byte NUM_COL_TASK_REMINDOPTION = NUM_COL_TASK_REMINDDATE + 1;
	
	// Vendor table
	String COL_VENDOR_COMPANY = "company";
	byte NUM_COL_VENDOR_COMPANY = NUM_COL_ID + 1;
	String COL_VENDOR_CONTACT = "contact";
	byte NUM_COL_VENDOR_CONTACT = NUM_COL_VENDOR_COMPANY + 1;
	String COL_VENDOR_ADDRESS = "address";
	byte NUM_COL_VENDOR_ADDRESS = NUM_COL_VENDOR_CONTACT + 1;
	String COL_VENDOR_PHONE = "telephone";
	byte NUM_COL_VENDOR_PHONE = NUM_COL_VENDOR_ADDRESS + 1;
	String COL_VENDOR_MAIL = "email";
	byte NUM_COL_VENDOR_MAIL = NUM_COL_VENDOR_PHONE + 1;
	String COL_VENDOR_CATEGORY = "category";
	byte NUM_COL_VENDOR_CATEGORY = NUM_COL_VENDOR_MAIL + 1;
	String COL_VENDOR_NOTE = "note";
	byte NUM_COL_VENDOR_NOTE = NUM_COL_VENDOR_CATEGORY + 1;
	
	// Budget table
	String COL_BUDGET_EXPENSE="expense";
	byte NUM_COL_BUDGET_EXPENSE = NUM_COL_ID +1;
	String COL_BUDGET_VENDOR = "vendor";
	byte NUM_COL_BUDGET_VENDOR = NUM_COL_BUDGET_EXPENSE + 1;
	String COL_BUDGET_CATEGORY = "category";
	byte NUM_COL_BUDGET_CATEGORY = NUM_COL_BUDGET_VENDOR + 1;
	String COL_BUDGET_TOTAL_AMOUNT = "total_amount";
	byte NUM_COL_BUDGET_TOTAL_AMOUNT = NUM_COL_BUDGET_CATEGORY + 1;
	String COL_BUDGET_PAID_AMOUNT = "paid_amount";
	byte NUM_COL_BUDGET_PAID_AMOUNT = NUM_COL_BUDGET_TOTAL_AMOUNT + 1;
	String COL_BUDGET_NOTE = "note";
	byte NUM_COL_BUDGET_NOTE = NUM_COL_BUDGET_PAID_AMOUNT + 1;
	
	// General
	int VERSION_BDD = 10;
	String NOM_BDD = "wedplanner.db";
	String BDD_BACKUP_NOM = "wedplanner_backup.db";
	//String BDD_PATH = "//data//data//com.innovention.weddingplanner//databases";
	String BDD_PATH = "databases";
	
	String CREATE_WEDDINGINFO_TABLE= new StringBuilder()
	.append("CREATE TABLE IF NOT EXISTS ")
	.append(TABLE_WEDDINGINFO)
	.append(" (")
	.append(COL_ID)
	.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
	.append(COL_WEDDATE)
	.append(" TEXT NOT NULL")
	.append(");")
	.toString();
	
	String CREATE_GUEST_TABLE=new StringBuilder()
	.append("CREATE TABLE IF NOT EXISTS ")
	.append(TABLE_GUESTS)
	.append(" (")
	.append(COL_ID)
	.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
	.append(COL_SURNAME)
	.append(" TEXT NOT NULL, ")
	.append(COL_NAME)
	.append(" TEXT NOT NULL, ")
	.append(COL_TEL)
	.append(" TEXT, ")
	.append(COL_EMAIL)
	.append(" TEXT, ")
	.append(COL_ADDRESS)
	.append(" TEXT, ")
	.append(COL_INVITATION)
	.append(" INTEGER, ")
	.append(COL_CHURCH)
	.append(" INTEGER, ")
	.append(COL_TOWNHALL)
	.append(" INTEGER, ")
	.append(COL_COCKTAIL)
	.append(" INTEGER, ")
	.append(COL_PARTY)
	.append(" INTEGER, ")
	.append(COL_RSVP)
	.append(" TEXT NOT NULL, ")
	.append(COL_FAMILY)
	.append(" INTEGER, ")
	.append(COL_FRIEND)
	.append(" INTEGER, ")
	.append(COL_COLLEGUE)
	.append(" INTEGER, ")
	.append(COL_OTHER)
	.append(" INTEGER ")
	.append(");")
	.toString();
	
	String CREATE_TASK_TABLE = new StringBuilder()
	.append("CREATE TABLE IF NOT EXISTS ")
	.append(TABLE_TASKS)
	.append(" (")
	.append(COL_ID)
	.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
	.append(COL_TASK_STATUS)
	.append(" INTEGER NOT NULL, ")
	.append(COL_TASK_DESC)
	.append(" TEXT NOT NULL, ")
	.append(COL_TASK_DUEDATE)
	.append(" TEXT, ")
	.append(COL_TASK_REMINDDATE)
	.append(" TEXT, ")
	.append(COL_TASK_REMINDOPTION)
	.append(" TEXT ")
	.append(");")
	.toString();
	
	String CREATE_VENDOR_TABLE = new StringBuilder()
	.append("CREATE TABLE IF NOT EXISTS ")
	.append(TABLE_VENDORS)
	.append(" (")
	.append(COL_ID)
	.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
	.append(COL_VENDOR_COMPANY)
	.append(" TEXT NOT NULL, ")
	.append(COL_VENDOR_CONTACT)
	.append(" TEXT, ")
	.append(COL_VENDOR_ADDRESS)
	.append(" TEXT, ")
	.append(COL_VENDOR_PHONE)
	.append( " TEXT, ")
	.append(COL_VENDOR_MAIL)
	.append(" TEXT, ")
	.append(COL_VENDOR_CATEGORY)
	.append(" TEXT, ")
	.append(COL_VENDOR_NOTE)
	.append(" TEXT")
	.append(");")
	.toString();
	
	String CREATE_BUDGET_TABLE = new StringBuilder()
	.append("CREATE TABLE IF NOT EXISTS ")
	.append(TABLE_BUDGET)
	.append(" ( ")
	.append(COL_ID)
	.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
	.append(COL_BUDGET_EXPENSE)
	.append(" TEXT NOT NULL, ")
	.append(COL_BUDGET_VENDOR)
	.append(" TEXT NOT NULL, ")
	.append(COL_BUDGET_CATEGORY)
	.append(" TEXT NOT NULL, ")
	.append(COL_BUDGET_TOTAL_AMOUNT)
	.append(" REAL NOT NULL, ")
	.append(COL_BUDGET_PAID_AMOUNT)
	.append(" REAL, ")
	.append(COL_BUDGET_NOTE)
	.append(" TEXT")
	.append(" );")
	.toString();
	
	
	String DROP_WEDDINGINFO_TABLE = new StringBuilder()
	.append("DROP TABLE IF EXISTS ")
	.append(TABLE_WEDDINGINFO)
	.append(";")
	.toString();
	
	String DROP_GUEST_TABLE = new StringBuilder()
	.append("DROP TABLE IF EXISTS ")
	.append(TABLE_GUESTS)
	.append(";")
	.toString();
	
	String DROP_TASK_TABLE = new StringBuilder()
	.append("DROP TABLE IF EXISTS ")
	.append(TABLE_TASKS)
	.append(";")
	.toString();
	
	String DROP_VENDOR_TABLE = new StringBuilder()
	.append("DROP TABLE IF EXISTS ")
	.append(TABLE_VENDORS)
	.append(";")
	.toString();
	
	String DROP_BUDGET_TABLE = new StringBuilder()
	.append("DROP TABLE IF EXISTS ")
	.append(TABLE_BUDGET)
	.append(";")
	.toString();
}
