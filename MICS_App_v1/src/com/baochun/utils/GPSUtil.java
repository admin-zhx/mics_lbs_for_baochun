package com.baochun.utils;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

public class GPSUtil {
	/**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */ 
    public static  boolean isOPen(Context context) { 
        LocationManager locationManager  
                                 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快） 
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位） 
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); 
        if (gps || network) { 
            return true; 
        } 
   
        return false; 
    }
    /**
     * 强制帮用户打开GPS
     * @param context
     */ 
    public static  void openGPS(Context context) { 
        Intent gpsIntent = new Intent(); 
        gpsIntent.setClassName("com.android.settings", 
                "com.android.settings.widget.SettingsAppWidgetProvider"); 
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE"); 
        gpsIntent.setData(Uri.parse("custom:3")); 
        try { 
            PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send(); 
        } catch (CanceledException e) { 
            e.printStackTrace(); 
        } 
//        LocationManager locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
//    	locationManager.setTestProviderEnabled("gps", true);
    }
}
