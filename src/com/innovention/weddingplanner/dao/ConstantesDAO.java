package com.innovention.weddingplanner.dao;

public interface ConstantesDAO {

	String TABLE_WEDDINGINFO = "table_weddingInfo";
	String COL_ID ="id";
	byte NUM_COL_ID = 0;
	String COL_WEDDATE = "weddate";
	byte NUM_COL_WEDDATE = 1;
	final int VERSION_BDD = 1;
	final String NOM_BDD = "wedplanner.db";
	
	final String CREATE_BDD = "CREATE TABLE " + TABLE_WEDDINGINFO + " ("
			+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_WEDDATE + " TEXT NOT NULL"
			+ ");";
	
	final String DROP_DB = "DROP TABLE " + TABLE_WEDDINGINFO + ";";
}
