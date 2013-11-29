package com.innovention.weddingplanner.dao;

public interface ConstantesDAO {

	// Table name
	String TABLE_WEDDINGINFO = "table_weddingInfo";
	String TABLE_GUESTS = "table_guests";
	
	// Columns wedding info
	String COL_ID ="id";
	byte NUM_COL_ID = 0;
	String COL_WEDDATE = "weddate";
	byte NUM_COL_WEDDATE = 1;
	
	// Columns guest table
	String COL_SURNAME="surname";
	String COL_NAME="name";
	String COL_TEL="telephone";
	String COL_EMAIL="email";
	String COL_ADDRESS="address";
	
	// General
	final int VERSION_BDD = 2;
	final String NOM_BDD = "wedplanner.db";
	
	final String CREATE_BDD = "CREATE TABLE " + TABLE_WEDDINGINFO + " ("
			+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_WEDDATE + " TEXT NOT NULL"
			+ ");";
	
	final String DROP_DB = "DROP TABLE " + TABLE_WEDDINGINFO + ";";
}
