package com.baochun.db;

import java.util.ArrayList;
import java.util.List;

import com.baochun.bean.UserLocation;
import com.baochun.utils.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocationDBManager {
	private LocationDBHelper dbHelper;

	public LocationDBManager(Context context) {
		dbHelper = new LocationDBHelper(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化dbHelperManager的步骤放在Activity的onCreate里
		// dbHelper = helper.getWritableDatabase();
	}

	/**
	 * 通过 beginTransaction 事务插入数据
	 * 
	 * @param persons
	 */
	public void add(List<UserLocation> locations) {
		SQLiteDatabase sd = dbHelper.getWritableDatabase();
		sd.beginTransaction(); // 开始事务
		String sql = "INSERT INTO  "+LocationDBHelper.TABLE_NAME_LOCATION+"  VALUES(null, ?, ?, ?, ?)";
		try {
			if(locations!=null&&!locations.isEmpty()){
				
				for (UserLocation person : locations) {
					sd.execSQL(sql,
							new Object[] { person.getUserid(), person.getTime(),
							person.getLatitude(), person.getLongitude() });
				}
				sd.setTransactionSuccessful(); // 设置事务成功完成
			}
		} finally {
			sd.endTransaction(); // 结束事务
			sd.close();
		}
	}

	/**
	 * 查询所有记录
	 * 
	 * @param Cursor
	 *            android.database.sqlite.SQLiteDatabase.query(String table,
	 *            String[] columns , String selection, String[] selectionArgs,
	 *            String groupBy, String having, String orderBy) //参数1：表名
	 *            //参数2：要想显示的列 //参数3：where子句 //参数4：where子句对应的条件值 //参数5：分组方式
	 *            //参数6：having条件 //参数7：排序方式
	 * @return
	 */
	public ArrayList<UserLocation> queryAll() {
		ArrayList<UserLocation> beans =null;
		UserLocation userLocation = null;
		// 得到一个可写的数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(LocationDBHelper.TABLE_NAME_LOCATION,
				new String[] { LocationDBHelper.TABLE_KEY_ITME,
						LocationDBHelper.TABLE_KEY_LATITUDE,
						LocationDBHelper.TABLE_KEY_LONTITUDE }, null, null,
				null, null, null);
		LogUtil.e("cursor=="+(cursor!=null)+" getCount=="+cursor.getCount());
		if(cursor!=null && cursor.getCount()!=0){
			beans = new ArrayList<UserLocation>();
			while (cursor.moveToNext()) {
				String time = cursor.getString(cursor
						.getColumnIndex(LocationDBHelper.TABLE_KEY_ITME));
				double latitude = cursor.getDouble(cursor
						.getColumnIndex(LocationDBHelper.TABLE_KEY_LATITUDE));
				double lontitude = cursor.getDouble(cursor
						.getColumnIndex(LocationDBHelper.TABLE_KEY_LONTITUDE));
				
				userLocation = new UserLocation(time,latitude,lontitude);
				if(userLocation.getUserid()==0){
					try {
						userLocation.setUserid(getId());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				beans.add(userLocation);
			}
		}
		
		// 关闭数据库
		cursor.close();
		db.close();
		return beans;
	}

	/**
	 * 获取用户id
	 * 
	 * @return
	 */
	public int getId() throws Exception{
		SQLiteDatabase sd = dbHelper.getReadableDatabase();
		Cursor c = null;
		int id = 0;
		c = sd.query(LocationDBHelper.TABLE_NAME_USER, null, null, null, null,
				null, null);
		if (c.moveToFirst()) {
			id = c.getInt(c.getColumnIndex(LocationDBHelper.TABLE_KEY_USERID));
		}
		c.close();
		sd.close();
		return id;
	}

	/**
	 * 设置用户id
	 * 
	 * @param userid
	 */
	public void setId(int userid) {
		SQLiteDatabase sd = dbHelper.getWritableDatabase();
		sd.delete(LocationDBHelper.TABLE_NAME_USER, null, null);
		// String sql =
		// "INSERT INTO  "+LocationDBHelper.TABLE_NAME_USER+"  VALUES(null, ?)";
		ContentValues values = new ContentValues();
		try {
			values.put(LocationDBHelper.TABLE_KEY_USERID, userid);
			sd.insert(LocationDBHelper.TABLE_NAME_USER, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sd.close();
		}

	}

	/**
	 * 更新用户id
	 * 
	 * @param userid
	 */
	public void updateId(int userid) {
		SQLiteDatabase sd = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(LocationDBHelper.TABLE_KEY_USERID, userid);
		sd.update(LocationDBHelper.TABLE_NAME_USER, values, null, null);
		sd.close();
	}

	/**
	 * 删除表
	 */
	public void deleteTab_Loc() {
		SQLiteDatabase sd = dbHelper.getWritableDatabase();
		sd.delete(LocationDBHelper.TABLE_NAME_LOCATION, null, null);
		sd.close();
		LogUtil.i("删除表数据");
	}

	public void deleteTab_User() {
		SQLiteDatabase sd = dbHelper.getWritableDatabase();
		sd.delete(LocationDBHelper.TABLE_NAME_USER, null, null);
		sd.close();
	}

}
