package com.baochun.utils;

import android.content.Context;
import android.widget.Toast;

public class NotificationUtil {

	private Context mContext;
	
    
	public NotificationUtil(Context context) {
		this.mContext = context;
	}

	public void sendNetworkNotification() {
		Toast.makeText(mContext, "网络不可用或未连接!", Toast.LENGTH_SHORT).show();
	}
	public void sendNotification(String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}
	public static void sendNotification(Context context,String text){ 
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
