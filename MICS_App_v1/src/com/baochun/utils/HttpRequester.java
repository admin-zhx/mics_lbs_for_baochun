package com.baochun.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


public class HttpRequester {

	private static final String ENCODING_UTF_8 = "UTF-8";

	public static InputStream sendRequestGet(String urlPath,String params) throws IOException,MalformedURLException {
		params = URLEncoder.encode(params,ENCODING_UTF_8);
		//将参数拼接在URl地址后面
		URL url = new URL(urlPath + params);
		//通过url地址打开连接
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//设置超时时间
		conn.setConnectTimeout(3000);
		conn.setDefaultUseCaches(false);
		conn.setUseCaches(false);
		//设置请求方式
		conn.setRequestMethod("GET");
		//如果返回的状态码是200，则一切Ok，连接成功。
		if(conn.getResponseCode() == 200){
			return conn.getInputStream();
		}
		LogUtil.e("responseCode==="+conn.getResponseCode()+"  "+conn.getErrorStream());
		return conn.getErrorStream();
		}
	/**
	 * 向服务器端发送当前位置的经纬度
	 * 
	 * @param usetId
	 *            用户ID
	 * @param longitude
	 *            经度值
	 * @param latitude
	 *            纬度值
	 * @throws JSONException 
	 * @throws Exception 
	 */
	public static int getResult(InputStream inputStream) throws IOException, JSONException{
		int resultCode = -1;
		if (inputStream != null) {
			String result = read(inputStream);
			if (parseResult(result)) {
				resultCode = Constant.UPLOAD_LOACTION_SUCCESS;
			} else if (!parseResult(result)) {
				resultCode = Constant.UPLOAD_LOACTION_FAIL;
			}
		} else {
			LogUtil.e("网络请求成功，但是返回的数据流为NULL");
		}

		return resultCode;
	}
	private static  String read(InputStream inputStream) throws IOException{
		String result = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
			while ((len=inputStream.read(data))!=-1) {
				os.write(data,0,len);
			}
		result = new String(os.toByteArray());
		return result;
	}
	/**
	 * 对结果进行解析
	 * @param json
	 * @return
	 */
	private static boolean parseResult(String json) throws JSONException{
		String result= null;
		boolean isok = false;
			JSONObject jsonObject = new JSONObject(json);
			result = (String) jsonObject.get("result");
			if(result!=null && result.equals("success")){
				isok = true;
			}
		return isok;
	}
	/**
	 * 获取升级信息
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static InputStream http_get(String url) throws ClientProtocolException, IOException{
		HttpGet httpGet = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpGet);
		HttpEntity entity = response.getEntity();
		String result = null;
		int status = response.getStatusLine().getStatusCode();
		if(status==HttpStatus.SC_OK){
			result = EntityUtils.toString(entity);
		}
		return new ByteArrayInputStream(result.getBytes());
	}
	
	/**
	 * 获取请求头
	 * @param url
	 * @return
	 */
	 public static int getRespStatus(String url) throws IOException,ClientProtocolException,IllegalArgumentException { 
		 HttpParams params = new BasicHttpParams();
		//超时设置  
         /*从连接池中取连接的超时时间*/  
         ConnManagerParams.setTimeout(params, 5000);  
         /*连接超时*/  
         HttpConnectionParams.setConnectionTimeout(params, 5000);  
         /*请求超时*/  
         HttpConnectionParams.setSoTimeout(params, 5000);  
         HttpClient client = new DefaultHttpClient(params);
		 HttpHead head = new HttpHead(url);
         HttpResponse resp = client.execute(head); 
         return resp.getStatusLine().getStatusCode(); 
 } 

}
