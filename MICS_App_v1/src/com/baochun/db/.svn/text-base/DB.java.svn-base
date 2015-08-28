package com.anjoyo.jd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {

	public DB(Context context, String name, CursorFactory factory, int version) {
		super(context, "jd.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "create table search(_id Integer primary key autoincrement,search_filed varchar )";
		db.execSQL(sql);
		// private int goods_id;
		// private int scales_volume;
		// private String goods_images;
		// private String goods_name;
		// private String goods_location;
		// private String s_sub_category;
		// private String sub_category;
		// private double new_price;
		// private double praise_scale;
		String goodsSQL = "create table goods("
				+ "_id Integer primary key autoincrement,"
				+ "goods_id Integer," 
				+ "goods_images varchar," 
				+ "goods_name varchar,"
				 + " new_price Double,"
				 + " old_price Double,"
				 + "praise_scale Double," 
				 + "scales_volume Integer,"											
				+ "goods_location varchar,"
				+"goods_promotion varchar,"
				+ "s_sub_category varchar,"
				+"sub_category varchar," 
				+"category varchar)";

		db.execSQL(goodsSQL);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
