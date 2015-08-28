package com.baochun.mics_app;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.baochun.utils.Constant;
import com.baochun.utils.LogUtil;
import com.baochun.utils.ServiceUtil;

public class MyApplication extends Application {
	private Timer mTimer;
	private  Context mContext;
	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		startMyServer();
		super.onCreate();
	}
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			boolean isRun = ServiceUtil.isServiceRun(mContext,
					"com.baochun.service.MobileLocatorService");
			if (!isRun) {
				LogUtil.i("--isRunService1111=" + isRun);
				if(mTimer == null){
	        		mTimer = new Timer();  
	        	}
	               mTimer.schedule(new TimerTask() {  
	                   @Override  
	                   public void run() { 
	                	   startLocatorService();
	                	   }  
	                   },2000, 5*60*1000
	               );
			}else{
				LogUtil.i("--isRunService2222=" + isRun);
				if(mTimer!=null){
					mTimer.cancel();
				}
			}
			
			super.handleMessage(msg);
		}
	};
	public void startMyServer(){
		// 启动定位服务
				new Thread(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(1);
					}
				}).start();
	}
	/**
	 * 启动服务
	 */
	private void startLocatorService() {
			Intent service = new Intent(Constant.ACTION_MOBILE_LOCATOR_SERVICE);
			service.setPackage(getPackageName());
			startService(service);
	}
}
