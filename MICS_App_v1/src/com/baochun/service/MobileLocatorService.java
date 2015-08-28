package com.baochun.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baochun.bean.UserLocation;
import com.baochun.db.LocationDBManager;
import com.baochun.mics_app.MyApplication;
import com.baochun.utils.Constant;
import com.baochun.utils.GPSUtil;
import com.baochun.utils.HttpRequester;
import com.baochun.utils.LogUtil;
import com.baochun.utils.NetWorkUtil;
import com.baochun.utils.NotificationUtil;

/** 
 * 类名：MobileLocatorService 
 * 功能描述：定位服务类。 
 */  
public class MobileLocatorService extends Service {  
  
  
    /** 
     * 启动10s后开始检测网络 
     *	private static final int CHECK_NETWORK_DELAY_TIME = 5 * 1000;
     */  

	private static final String BDLOCATION_GCJ02_TO_BD09LL = BDLocation.BDLOCATION_GCJ02_TO_BD09LL;  
  
    private Context mContext;  
    private MyApplication mContexts;
  
    /** 
     * 定位SDK的核心类 
     */  
    private LocationClient mLocationClient;  
  
    /** 
     * 定位结果处理器 # class MyLocationListener implements BDLocationListener{} 
     */  
    private MyLocationListener mLocationListener;  
  
    /** 
     * 通知工具类 
     */  
    private NotificationUtil mNotificationUtil;  
  
    /** 
     * 当前网络是否可用的标志 
     */  
    private boolean isOpenNetwork = false;  
    /**
     * 定时器
     */
    private Timer mTimer;
    /**
     * 数据库是否为空标识
     */
    private boolean isNullDB=false;
  
   private LocationDBManager dbManager = null; 
   private  ArrayList<UserLocation> userLocationList;
   private int userId = 0;
    
    @Override  
    public void onCreate() {  
        LogUtil.i("--------------MobileLocatorService onCreate()----------------");  
  
//        mContext = MobileLocatorService.this;  
        mContext = getApplicationContext();  
        mContexts = (MyApplication) getApplication();
        initLocationService();  
        
        mNotificationUtil = new NotificationUtil(mContext);  
        dbManager = new LocationDBManager(mContext);
        
        // 设置为前台进程，尽量避免被系统干掉。  
        // MobileLocatorService.this.setForeground(true);  
    }  
    /** 
     * 初始化定位服务，配置相应参数 
     */  
    private void initLocationService() { 
    	LogUtil.i("-------------初始化定位服务------------");
        mLocationClient = new LocationClient(mContext);  
        mLocationListener = new MyLocationListener();  
        mLocationClient.registerLocationListener(mLocationListener);  
  
        LocationClientOption locationOption = new LocationClientOption(); 
        locationOption.setLocationMode(LocationMode.Hight_Accuracy);
        locationOption.setOpenGps(true);  
        locationOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setCoorType(BDLOCATION_GCJ02_TO_BD09LL);  
        locationOption.disableCache(true);  
        locationOption.setPriority(LocationClientOption.GpsFirst);  
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(false);
        locationOption.setScanSpan(Constant.DELAY_TIME);
//        locationOption.setProdName(this.getString(R.string.loaction_prod_name));  
        locationOption.setProdName("location_prod_name");  
  
        mLocationClient.setLocOption(locationOption);  
    } 
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        LogUtil.i("--------------MobileLocatorService onStartCommand()----------------");  
        if (intent != null) {  
                mHandler.post(new Runnable() {  
                    @Override  
                    public void run() {  
                    	checkNetwork();
                    	if(!isOpenNetwork){
                    		 Message msg = Message.obtain();  
                        	  msg.what = Constant.CHECK_NETWORK_CONNECT_FLAG;  
                        	  mHandler.sendMessage(msg);  
                    	}
                    	 
                    }  
                });  
        }  
        return Service.START_REDELIVER_INTENT;  
    }  
  
    /** 
     * 检测网络是否可用 
     */  
    private void checkNetwork() {  
        // 如果网络不可用，开启GPS就没有意义  
        if (NetWorkUtil.isNetworkAvailable(mContext)) {  
            isOpenNetwork = true;  
  
            if (GPSUtil.isOPen(mContext) == false) { 
            	GPSUtil.openGPS(mContext);
            }  
            boolean isRun = mLocationClient.isStarted();
            LogUtil.i("--定位服务是否开启=="+isRun);
            if (!isRun) {  
            	LogUtil.i("----------------开启定位服务-----------");
            	mLocationClient.start(); 
            }
  
        } else {  
        	// 网络不可用  
        	isOpenNetwork = false;
        }  
    }  
   
    Handler mHandler = new Handler() {  
  
        @Override  
        public void handleMessage(Message msg) {  
            int result = msg.what;  
            int locType = msg.arg1;
            switch (result) {  
            case Constant.CHECK_NETWORK_CONNECT_FLAG:
            	if(mTimer == null){
            		mTimer = new Timer();  
            	}
                   mTimer.schedule(new TimerTask() {  
                       @Override  
                       public void run() { 
                    	   checkNetwork();
                    	   if(isOpenNetwork==true){
                    		   mTimer.cancel();
                    	   }
                    	   }  
                       },2000, 60*1000
                   );
                break;  
  
            case Constant.UPLOAD_LOACTION_SUCCESS:  
                LogUtil.i("您当前的位置上传服务器成功！");
                if(!isNullDB){
                	dbManager.deleteTab_Loc();
                }
                break; 
            //  GPS定位结果、网络定位结果  
            case Constant.GET_LOCATION_SUCCESS:
            	LogUtil.i("GET_LOCATION_SUCCESS---locType =="+locType);
            	if(locType==66){
            		LogUtil.i("刚才使用了离线定位");
            	}else
           	 if( userId > 0 && isOpenNetwork){
           		 LogUtil.i("--启动线程发送数据--="+userId+" "+isOpenNetwork);
           		 new Thread(runable).start();
                }else{
               	 LogUtil.i("--未登录--保存到数据库->"+userId+" "+isOpenNetwork);
               	//向数据库添加数据
                	dbManager.add(userLocationList);
                }
           	break;
           	//  网络异常，没有成功向服务器发起请求。
            case Constant.GET_LOCATION_FAILED :  
            	LogUtil.e("GET_LOCATION_FAILED=="+locType);
            	if(locType==62){
            		LogUtil.e("GET_LOCATION_FAILED=gps=问题");
            	}else if(locType==167){
					LogUtil.e("GET_LOCATION_FAILED=== 服务端定位失败，" +
							"请您检查是否禁用获取位置信息权限，尝试重新请求定位");
            	}
                break;  
            case Constant.UPLOAD_LOACTION_FAIL:  
                LogUtil.e("--上传位置信息失败！--"+"UPLOAD_LOACTION_FAIL=="+msg.obj); 
                dbManager.add(userLocationList);
                break;
             // LogUtil.e("网络连接失败，请将网络关闭再重新打开试试！");  
            case Constant.LOCATION_NETWORK_CONNECT_FAIL:  
              mNotificationUtil.sendNetworkNotification();  
              break;  
            case Constant.RESTART_SERVICE :
            if (mLocationClient != null && mLocationClient.isStarted()) {  
                mLocationClient.stop();  
                if (mLocationListener != null) {  
                    mLocationClient.unRegisterLocationListener(mLocationListener);  
                }  
            }  
            // 销毁时重新启动Service
            mContexts.startMyServer();
            break;
            default:  
                break;  
            }  
        }  
    };  
   /**
    *  location.getLocType()的返回值含义： 
               61 ： GPS定位结果 ok
               62 ： 扫描整合定位依据失败。此时定位结果无效。 
               63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。 
               65 ： 定位缓存的结果。 
               66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果 
               67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果 
               68 ： 网络连接失败时，查找本地离线定位时对应的返回结果 
               161： 表示网络定位结果 ok
               162~167： 服务端定位失败。  
    * @author 
    *
    */
    class MyLocationListener implements BDLocationListener {  
        
        @Override  
        public void onReceiveLocation(BDLocation location) {  
            if (location == null) {  
                return;  
            } 
            
            int locType = location.getLocType(); 
            double latitude = location.getLatitude();
            double lontitude = location.getLongitude();
            String desc = location.getDistrict();
//            String time = location.getTime();
            String time = (new java.text.SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss",Locale.getDefault())).format(System.currentTimeMillis());
         // TODO 调试使用  
//               StringBuffer sb = new StringBuffer(256);  
//               sb.append(" time : ");  
//               sb.append(time);  
//               sb.append("\t  error code : ");  
//               sb.append(locType);  
//               sb.append("\t  latitude : ");  
//               sb.append(lontitude);  
//               sb.append("\t  longitude : ");  
//               sb.append(latitude);  
//               LogUtil.i("BDLocationListene== " +userId +"  "+ sb.toString()); 
            try {
				userId = dbManager.getId();
			} catch (Exception e) {
				LogUtil.e("查询用户id异常=="+userId);
			}
            userLocationList = new ArrayList<UserLocation>();
            UserLocation locationDate = new UserLocation();
            locationDate.setUserid(userId);
            locationDate.setTime(time);
         	locationDate.setLatitude(latitude);
         	locationDate.setLongitude(lontitude);
         	locationDate.setlocType(locType);
         	userLocationList.add(locationDate);
           
         	Message msg = Message.obtain();
			if (locType == BDLocation.TypeGpsLocation
					|| locType == BDLocation.TypeNetWorkLocation
					|| locType == BDLocation.TypeOffLineLocation) {
				msg.what = Constant.GET_LOCATION_SUCCESS;
				msg.arg1 = locType;
				mHandler.sendMessage(msg);
			} else {
				msg.arg1 = locType;
				msg.what = Constant.GET_LOCATION_FAILED;
				mHandler.sendMessage(msg);
			}
        }  
    }
    
    //发送数据线程
    private Runnable runable =new Runnable() {
    	String params = null;
        @Override  
        public void run() {  
        	
 			//调用位置信息方法
         		ArrayList<UserLocation> locationDates = dbManager.queryAll();
         		if(locationDates!=null && !locationDates.isEmpty()){
         			isNullDB = false;
         			params = UserLocation.toJson(locationDates);
         			LogUtil.i("---数据库----"+"-------"+params);
         		}else{
         			isNullDB = true;
         			params = UserLocation.toJson(userLocationList);
         			LogUtil.i("---直接发送----"+"-------"+params);
         		}
        	 Message msg = Message.obtain();
            try {
            	InputStream stream =  HttpRequester.sendRequestGet(Constant.URL_UPLOAD_LOACTION,params);
           		int result = HttpRequester.getResult(stream);
				 msg.what = result;  
                
			} catch (MalformedURLException e) {
				msg.obj = e.getMessage();
				msg.what = Constant.UPLOAD_LOACTION_FAIL;
			} catch (IOException e) {  
				msg.obj = e.getMessage();
				msg.what = Constant.UPLOAD_LOACTION_FAIL;
	        }catch (JSONException e) {
	        	msg.obj = e.getMessage();
				msg.what = Constant.UPLOAD_LOACTION_FAIL;
			}  
            mHandler.sendMessage(msg);
        } 
    };
  
    //服务销毁
    @Override  
    public void onDestroy() {  
        LogUtil.e("---------------MobileLocatorService onDestroy()----------------");  
        mHandler.sendEmptyMessage(Constant.RESTART_SERVICE);
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}  
}  