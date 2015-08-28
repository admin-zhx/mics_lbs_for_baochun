package com.baochun.utils;

public class Constant {

	 /** 
     * 间隔时间15分钟 
     */  
     public static final int DELAY_TIME = 10*60*1000;
     
//	public static final String BASEURL = "http://192.168.1.13:8080/mics/app";
	public static final String BASEURL = "http://112.126.82.31:10086/mics/app";
	public static final String URL_UPLOAD_LOACTION = BASEURL+"/lbs/add.json?list=";
	public static final String URL_INDEX = BASEURL+"/index.html";
	public static final String URL_UPDATE = BASEURL+"/xml/ver_apk.xml";  
	public static final String URL_DOWNLOAD = BASEURL+"/download/apk/";
	
	public static final int CHECK_NETWORK_CONNECT_FLAG = 0;
	public static final int LOCATION_NETWORK_CONNECT_FAIL = 68;
	public static final int UPLOAD_LOACTION_FAIL = -1;
	public static final int UPLOAD_LOACTION_SUCCESS = 1;
	public static final int GET_LOCATION_SUCCESS = 2;
	public static final int GET_LOCATION_FAILED = 63;
	public static final int RESTART_SERVICE = 3;
	
	 /** 
     * Service action. 
     */  
    public static final String ACTION_MOBILE_LOCATOR_SERVICE = "com.baochun.service.MobileLocatorService";  
  
}
