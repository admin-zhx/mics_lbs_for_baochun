package com.baochun.utils;

import android.util.Log;

public class LogUtil {
	private static final String TAG = "TAG";
	public static void e(String msg){
		Log.e(TAG, msg);
	}
	public static void i(String msg){
		Log.i(TAG, msg);
	}
	public static void w(String msg){
		Log.w(TAG, msg);
	}
}
