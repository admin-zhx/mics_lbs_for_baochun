package com.baochun.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDBHelper extends SQLiteOpenHelper {
	
	 private static final String DATABASE_NAME = "location_lbs.db";  
	 private static final int DATABASE_VERSION = 1;
	 public static final String TABLE_NAME_LOCATION = "mylocation";
	 public static final String TABLE_NAME_USER = "user";
	 
		public static final String TABLE_KEY_USERID = "userid";
//		private static final String TABLE_KEY_USERNAME = "username";
		public static final String TABLE_KEY_ITME = "time";
		public static final String TABLE_KEY_LATITUDE = "latitude";
		public static final String TABLE_KEY_LONTITUDE = "lontitude";
//		private static final String TABLE_KEY_CODE = "result_code";
	 
	//CursorFactory设置为null,使用默认值
	 public LocationDBHelper(Context context) {
		 super(context, DATABASE_NAME, null, DATABASE_VERSION);
		 // TODO Auto-generated constructor stub
	 }
	public LocationDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	//数据库第一次被创建时onCreate会被调用 
	@Override
	public void onCreate(SQLiteDatabase db) {
//		String sql = "CREATE TABLE IF NOT EXISTS getdates(userid Integer primary key autoincrement,date_filed varchar )";
//		db.execSQL(sql);
		String locationSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_LOCATION
				+ "(_id Integer primary key autoincrement,"
				+ "userid  Integer," 
//				+ "username  varchar," 
//				+ "result_code  Integer,"											
				+ "time  varchar,"
				 + "latitude  Double,"
				 + "lontitude  Double)";
		String userSQL = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME_USER+ "("
//				+"_id Integer primary key autoincrement,"
				+"_id Integer primary key,"
				+ "userid  Integer)" ;
		db.execSQL(locationSQL);
		db.execSQL(userSQL);
		
	}
	
	 //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 db.execSQL("ALTER TABLE  "+TABLE_NAME_LOCATION+"  ADD COLUMN other STRING");
	}

}
