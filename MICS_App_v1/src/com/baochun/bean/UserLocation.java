package com.baochun.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserLocation {
	private int userid;
	private String username;
	private String time;
	private double latitude;
	private double longitude;
	private int locType;
	
	public UserLocation(){
		
	}
	public UserLocation(int mUserid,String mTime,double mlatitude,double mLongitude){
		this.userid = mUserid;
		this.time = mTime;
		this.latitude = mlatitude;
		this.longitude = mLongitude;
	}
	public UserLocation(String mTime,double mlatitude,double mLongitude){
		this.time = mTime;
		this.latitude = mlatitude;
		this.longitude = mLongitude;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getlocType() {
		return locType;
	}
	public void setlocType(int locType) {
		this.locType = locType;
	}
	
	/**
	 * 字符串转换成json
	 * @return
	 */
	public static String toJson(ArrayList<UserLocation> userBeans) {  //把一个集合转换成json格式的字符串
		JSONObject object = null;  //JSONObject对象，处理一个一个的对象
		JSONArray jsonArray = null;//JSONObject对象，处理一个一个集合或者数组
		String jsonString = null;  //保存带集合的json字符串
		jsonArray = new JSONArray();
		for (int i = 0; i < userBeans.size(); i++) {  //遍历上面初始化的集合数据，把数据加入JSONObject里面
			object = new JSONObject();//一个user对象，使用一个JSONObject对象来装
			try {
				//从集合取出数据，放入JSONObject里面 JSONObject对象和map差不多用法,以键和值形式存储数据
//				object.put("userName", userBeans.get(i).getUsername());
				object.put("userId", userBeans.get(i).getUserid());
				object.put("ltime", userBeans.get(i).getTime());
				object.put("latitude", userBeans.get(i).getLatitude());
				object.put("longitude", userBeans.get(i).getLongitude());
				jsonArray.put(object); //把JSONObject对象装入jsonArray数组里面
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		jsonString = jsonArray.toString(); //把JSONObject转换成json格式的字符串
		return jsonString;
	}
	
}
