package com.baochun.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetWorkUtil {
	
//	private static NetWorkUtil netWork;
	private Context mContext;
	 /** 
     * 检测网络的次数（5分钟一次，检测三次） 
     */  
    private int checkNetworkNumber = 0;  
  
    /** 
     * @desc 定时器 
     */  
    private Timer mTimer;  
    
    public NetWorkUtil(Context context){
    	this.mContext = context;
    }
//	public static NetWorkUtil getInstanse(){
//		if(netWork == null){
//			netWork = new NetWorkUtil();
//		}
//		return netWork;
//	}
	/** 
     * 当前网络是否可用的标志 
     */  
    private boolean isNetwork = false;
	
	public boolean isOpenNetwork() {
		return isNetwork;
	}

	public  void setOpenNetwork(boolean isOpenNetwork) {
		isNetwork = isOpenNetwork;
	}
	/**
	 * 网络是否可用
	 */
	public static boolean isNetworkAvailable(Context mContext) {
		ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if(info!=null && info.isAvailable()){
			return true;
		}
		return false;
	}
	// Wifi是否可用    
    public static boolean isWifiEnable(Context mContext) {    
        WifiManager wifiManager = (WifiManager) mContext    
                .getSystemService(Context.WIFI_SERVICE);    
        return wifiManager.isWifiEnabled();    
    }
    /**
     * 注册网络监听广播
     * @param mContext
     */
    public  void registNetReceiver(Context mContext){
    	if(mReceiver != null){
    		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    		mContext.registerReceiver(mReceiver, filter);
    	}
    }
    /**
     * 取消注册网络监听广播
     * @param mContext
     */
    public void unRegistNetReceiver(Context mContext){
    	if(mReceiver != null){
    		mContext.unregisterReceiver(mReceiver);
    	}
    	
    }
    
    /**
     * 监听网络状态
     * @author Administrator
     *
     */
    private static BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				if(!NetWorkUtil.isNetworkAvailable(context)){
//					setOpenNetwork(false);
					
				}
			}
		}
    	
    };
    /**
     * 检测网络，对未连接网络进行三次提醒
     * @param
     */
    public  void isNetStates(){
    	// 第一检测网络，直接过了。（已打开）  
        boolean isRun = ServiceUtil.isServiceRun(mContext, "com.baidu.location.f");
         final boolean isOpen = isOpenNetwork();
        if (isOpen && isRun) {  
            LogUtil.i("--------------网络（已打开）----------------");  
            return;  
        }  

        mTimer = new Timer();  
        mTimer.schedule(new TimerTask() {  

            @Override  
            public void run() {  
                checkNetworkNumber++;  
                LogUtil.i("Timer checkNetworkNumber = " + checkNetworkNumber);  

                boolean isRun = ServiceUtil.isServiceRun(mContext, "com.baidu.location.f");  
                if (isOpen  && isRun) {  
//                    mNotificationUtil.cancelNotification(Constant.NOTIFICATION_NETWORK_NOT_OPEN);  
                    mTimer.cancel();  
                    return;  
                } else {  
                    if (checkNetworkNumber == 3) {  

//                        LogUtil.e("--------------第三次检测网络，还未开启，直接退出应用---------");  

                        // 检查网络，提醒了用户三次依然未开，退出应用。  
//                        mNotificationUtil.cancelNotification(Constant.NOTIFICATION_NETWORK_NOT_OPEN);  
//                        mNotificationUtil.cancelNotification(Constant.NOTIFICATIO_GPS_NOT_OPEN);  
                        mTimer.cancel();  

                        // System.gc();  
//                        System.exit(0);  
                    }  
                }  
            }  
        }, 0, 5*60*1000);  

    }
}
