package com.innovention.weddingplanner.dao;

public interface ConstantesDAO {

	// Table name
	String TABLE_WEDDINGINFO = "table_weddingInfo";
	String TABLE_GUESTS = "table_guests";
	
	// Columns wedding info
	String COL_ID ="_id";
	byte NUM_COL_ID = 0;
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
	
	// General
	int VERSION_BDD = 2;
	String NOM_BDD = "wedplanner.db";
	
	String CREATE_WEDDINGINFO_TABLE= new StringBuilder()
	.append("CREATE TABLE ")
	.append(TABLE_WEDDINGINFO)
	.append(" (")
	.append(COL_ID)
	.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
	.append(COL_WEDDATE)
	.append(" TEXT NOT NULL")
	.append(");")
	.toString();
	
	String CREATE_GUEST_TABLE=new StringBuilder()
	.append("CREATE TABLE ")
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
	.append(" TEXT NOT NULL ")
	.append(");")
	.toString();
	
	String DROP_WEDDINGINFO_TABLE = new StringBuilder()
	.append("DROP TABLE ")
	.append(TABLE_WEDDINGINFO)
	.append(";")
	.toString();
	
	String DROP_GUEST_TABLE = new StringBuilder()
	.append("DROP TABLE ")
	.append(TABLE_GUESTS)
	.append(";")
	.toString();
}
