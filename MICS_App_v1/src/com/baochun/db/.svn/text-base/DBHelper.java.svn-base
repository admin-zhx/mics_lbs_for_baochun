package com.anjoyo.jd.db;

import java.util.ArrayList;
import java.util.List;

import com.anjoyo.jd.bean.GoodsBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {
	private DB db;
	public DBHelper(Context context) {
		db = new DB(context, "jd.db", null, 1);
	}

	public void insertGoods(ArrayList<GoodsBean> beans) {
		
		SQLiteDatabase sd = db.getWritableDatabase();
		sd.delete("goods", null, null);
		ContentValues values=null;
		for (GoodsBean goodsBean : beans) {
			values=new ContentValues();
			values.put("goods_id", goodsBean.getGoods_id());
			values.put("goods_images", goodsBean.getGoods_image());
			values.put("goods_name", goodsBean.getGoods_name());
			values.put("new_price", goodsBean.getNew_price());
			values.put("old_price", goodsBean.getOld_price());
			values.put("praise_scale", goodsBean.getPraise_scale());
			values.put("scales_volume", goodsBean.getScales_volume());
			values.put("goods_location", goodsBean.getGoods_location());
			values.put("goods_promotion", goodsBean.getGoods_promotion());
			sd.insert("goods", null, values);		
		}
		sd.close();
	}
	
	public ArrayList<GoodsBean> queryGoods(String...str) {
		SQLiteDatabase sd = db.getReadableDatabase();
		String orderBy = str[0];
		if (str.length>1&&"asc".endsWith(str[1])) {
			orderBy+=" asc";
		}
		else
			orderBy+=" desc";
		Cursor c = sd.query("goods", null, null, null, null, null, orderBy);
		ArrayList<GoodsBean> beans = new ArrayList<GoodsBean>();
		GoodsBean goodsBean=null;
		while (c.moveToNext()) {
			goodsBean=new GoodsBean(c.getInt(1), c.getString(2), c.getString(3), c.getDouble(4), c.getDouble(5), c.getDouble(6), c.getInt(7), c.getString(8), c.getString(9), c.getString(10));
			beans.add(goodsBean);
		}
		c.close();
		sd.close();
		return beans;
	}
	public void insert(String name) {
		ArrayList<String> al=find();
		for (String string : al) {
			if (string.equals(name)) {
				return;
			}
		}
		SQLiteDatabase sd = db.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("search_filed", name);
		sd.insert("search", null, values);
		sd.close();
	}

	public ArrayList<String> find() {
		SQLiteDatabase sd = db.getReadableDatabase();
		Cursor c = sd.query("search", null, null, null, null, null, null);
		ArrayList<String> list = new ArrayList<String>();
		while (c.moveToNext()) {
			list.add(c.getString(c.getColumnIndex("search_filed")));
		}
		c.close();
		sd.close();
		return list;
	}

	public void delete() {
		SQLiteDatabase sd = db.getWritableDatabase();
		sd.delete("search", null, null);
		sd.close();
	}
}
