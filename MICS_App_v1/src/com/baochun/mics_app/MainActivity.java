package com.baochun.mics_app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.baochun.db.LocationDBManager;
import com.baochun.utils.Constant;
import com.baochun.utils.DeskShortCutUtil;
import com.baochun.utils.HttpRequester;
import com.baochun.utils.LogUtil;
import com.baochun.utils.NotificationUtil;
import com.baochun.utils.UpdateManager;

public class MainActivity extends Activity {

	private WebView webView;

	private static final int WHAT_NOTCONNECT = -1;
	private static final int WHAT_OK = 200;
	private static final int WHAT_SHOWPROGRESS = 100;

	private String mCameraFilePath = null;
	private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
	private final static int FILECHOOSER_RESULTCODE = 123;// 表单的结果回调</span>

	private int requestCode = -1;
	private LocationDBManager dbManager;
	private Context appContext;
	private LinearLayout layout;
	// private TextView msg_tv;
	private ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		appContext = getApplicationContext();
		// 实例化数据库， 并设置userid值
		dbManager = new LocationDBManager(getApplicationContext());
		dbManager.setId(0);
		layout = (LinearLayout) findViewById(R.id.ll_netstatserror);
		webView = (WebView) findViewById(R.id.webView1);
		// msg_tv = (TextView) layout.findViewById(R.id.errorTv);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		initWebViewSet();
		init();
	}

	/**
	 * 设置webview参数
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebViewSet() {
		webView.setSaveEnabled(false);
		webView.requestFocusFromTouch();

		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setSaveFormData(false);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setGeolocationEnabled(true);
		// webView.addJavascriptInterface(script, "JS");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);

	}
	/**
	 * 验证地址
	 */
	private void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sendMsgToHandler(WHAT_SHOWPROGRESS, 0, "正在加载");
					requestCode = HttpRequester
							.getRespStatus(Constant.URL_INDEX);
				} catch (IOException e) {
					sendMsgToHandler(WHAT_NOTCONNECT, requestCode,
							"HttpRequester=IOException= " + e.getMessage());
					sendMsgToHandler(WHAT_SHOWPROGRESS, 100, "加载完成");
					return;
				}
				// 此处判断主页是否存在，因为主页是通过loadUrl加载的，
				// 此时不会执行shouldOverrideUrlLoading进行页面是否存在的判断
				// 进入主页后，点主页里面的链接，链接到其他页面就一定会执行shouldOverrideUrlLoading方法了
				if (requestCode == 200) {
					sendMsgToHandler(WHAT_OK, requestCode, "正常加载");
				} else {
					sendMsgToHandler(WHAT_NOTCONNECT, requestCode, "其它错误= "+requestCode);
				}
			}
		}).start();
		
		// 创建快捷方式和更新提示
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				DeskShortCutUtil.createShortCut(appContext);
				UpdateManager.getUpdateManager().checkAppUpdate(
						MainActivity.this, false);
			}
		});
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			switch (what) {
			case WHAT_NOTCONNECT:
				showErrorView();
				if(msg.arg1 == -1){
					NotificationUtil.sendNotification(appContext, "连接服务器超时,请检查网络");
				}
				LogUtil.e(msg.toString());
				break;
			case WHAT_OK:
				loadWeb();
				break;
			case WHAT_SHOWPROGRESS:
				if(pb!=null){
					if(pb.getVisibility()==View.INVISIBLE || pb.getVisibility() == View.GONE){
						pb.setVisibility(View.VISIBLE);
						pb.setProgress(msg.arg1);
					}
					if (msg.arg1 == 100) {
						pb.setProgress(0);
						pb.setVisibility(View.GONE);
					}
				}
				break;
			default:
				break;
			}

		}

	};

	/**
	 * 刷新按钮点击事件
	 * 
	 * @param view
	 */
	public void reLoad(View view) {
//		webView.reload();
		mHandler.sendEmptyMessage(WHAT_OK);
	}

	/**
	 * 对加载的网页处理
	 */
	private void loadWeb() {
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.i("--shouldOverrideUrlLoading--"+url);
				if (url.startsWith("http://") && requestCode != 200) {
					view.stopLoading();
					sendMsgToHandler(WHAT_NOTCONNECT, requestCode, url);
				} else {
					view.loadUrl(url);
				}
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				LogUtil.i("--onPageStarted--"+url);
				sendMsgToHandler(WHAT_SHOWPROGRESS, 20, "onPageStarted");
				if (url.startsWith("http://") && requestCode != 200) {
					view.stopLoading();
					sendMsgToHandler(WHAT_NOTCONNECT, requestCode, "onPageStarted==");
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				sendMsgToHandler(WHAT_NOTCONNECT, errorCode, "onReceivedError=="+description);
			};

			@Override
			public void onPageFinished(WebView view, String url) {
				LogUtil.i("--onPageFinished--"+url);
				sendMsgToHandler(WHAT_SHOWPROGRESS, 100, "onPageStarted");
				// 获取网页携带的cookies
				getcookies(url);
				super.onPageFinished(view, url);
			}
			@Override
			public void doUpdateVisitedHistory(WebView view, String url,
					boolean isReload) {
				LogUtil.i("onLoadResource----"+url);
				super.doUpdateVisitedHistory(view, url, isReload);
			}

		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					android.webkit.GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				sendMsgToHandler(WHAT_SHOWPROGRESS, newProgress, "onProgressChanged==");
				super.onProgressChanged(view, newProgress);
			}

			// js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
			// Android > 4.1.1 调用这个方法
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {
				
				mUploadMessage = uploadMsg;
				startActivityForResult(Intent.createChooser(
						createCameraIntent(), "Image Browser"),
						FILECHOOSER_RESULTCODE);
			}

			// 3.0 + 调用这个方法
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {
				openFileChooser(uploadMsg,"","");
			}

			// Android < 3.0 调用这个方法
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				openFileChooser(uploadMsg, "");
			}

		});
		showNormalView();
	}

	/**
	 * 拍照或相册选择
	 * 
	 * @return
	 */
	private Intent createCameraIntent() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 拍照
		File externalDataDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		LogUtil.i("externalDataDir:" + externalDataDir);
		File cameraDataDir = new File(externalDataDir.getAbsolutePath()
				+ File.separator + "BC_upload");
		cameraDataDir.mkdirs();
		mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator
				+ System.currentTimeMillis() + ".jpg";
		LogUtil.i("mcamerafilepath:" + mCameraFilePath);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(mCameraFilePath)));
		// =======================================================
		Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);// 选择图片文件
		// =======================================================
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");
		
		Intent chooser = createChooserIntent(cameraIntent, imageIntent);
		chooser.putExtra(Intent.EXTRA_INTENT, i);
		return chooser;
	}

	private Intent createChooserIntent(Intent... intents) {
		Intent chooser = new Intent(Intent.ACTION_CHOOSER);
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
		chooser.putExtra(Intent.EXTRA_TITLE, "选择图片");
		return chooser;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		LogUtil.e("requestCode====" + requestCode + " resultCode=="
				+ resultCode + " intent==" + intent);
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
					: intent.getData();

			if (result == null && intent == null
					&& resultCode == Activity.RESULT_OK) {
				File cameraFile = new File(mCameraFilePath);

				if (cameraFile.exists()) {
					result = Uri.fromFile(cameraFile);
					LogUtil.e("file====" + result);
					// result = Uri.fromFile(handleFile(cameraFile));
					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
				}
			}

			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	/**
	 * 发送Handler信息
	 * 
	 * @param errorCode
	 * @param description
	 */

	private void sendMsgToHandler(int what, int arg1, String description) {
		Message msg = Message.obtain();
		msg.what = what;
		msg.arg1 = arg1;
		msg.obj = description;
		mHandler.sendMessage(msg);
	}

	/**
	 * 显示错误页
	 */
	private void showErrorView() {
		if (layout.getVisibility() == View.GONE
				|| layout.getVisibility() == View.INVISIBLE) {
			layout.setVisibility(View.VISIBLE);
			webView.setVisibility(View.INVISIBLE);
			// return;
		}
	}

	/**
	 * 显示正常页
	 */
	private void showNormalView() {
		if (webView.getVisibility() == View.INVISIBLE
				|| webView.getVisibility() == View.GONE) {
			webView.setVisibility(View.VISIBLE);
			layout.setVisibility(View.GONE);
		}
		webView.loadUrl(Constant.URL_INDEX);
	}

	/**
	 * 获取cookie信息
	 * 
	 * @param url
	 */
	private void getcookies(String url) {
		CookieManager cookieManager = CookieManager.getInstance();
		String cookieStr = cookieManager.getCookie(url);
		Map<String, String> valueMap = new HashMap<String, String>();
		String userid = "0";
		if (cookieStr != null && !cookieStr.isEmpty()) {
			String[] strs = cookieStr.split(";");
			for (String str : strs) {
				String[] keyValue = str.split("=");
				valueMap.put(keyValue[0].trim(), keyValue[1].trim());
			}
			userid = valueMap.get("userId");
			try {
				if (userid != null && !userid.equals("")) {
					int id = Integer.parseInt(userid);
					if (id == dbManager.getId())
						return;
					dbManager.updateId(id);
					LogUtil.e("Cookies_id= " + id + " dbManager.getId= "
							+ dbManager.getId());
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if(webView !=null){
				 String url = webView.getUrl();
				 webView.clearHistory();
				 if(url !=null && !url.isEmpty()){
					 if(!url.equalsIgnoreCase(Constant.URL_INDEX)&&!url.contains("login.html")){
						 webView.loadUrl(Constant.URL_INDEX);
						 LogUtil.i("histtory===="+url);
						 return true;
					 }else {
							showExitDialog();
						}
				 }else {
						showExitDialog();
					}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showExitDialog() {
//		long exitTime = 0;
//		if ((System.currentTimeMillis() - exitTime) > 2000) {
//			Toast.makeText(getApplicationContext(), "再按一次退出程序",
//					Toast.LENGTH_SHORT).show();
//			exitTime = System.currentTimeMillis();
//			LogUtil.i("exit==="+exitTime);
//		} else {
//			finish();
//			LogUtil.i("exit=1111=="+exitTime);
//		}
		AlertDialog.Builder ad = new Builder(this);
		ad.setTitle("退出");
		ad.setMessage("确定要退出？");
		ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		ad.setNegativeButton("取消", null);
		ad.create().show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webView != null) {
			
			webView.clearCache(true);
			webView.clearHistory();
			webView.clearFormData();
		}
		finish();
//		System.exit(0);// 退出程序
		LogUtil.e("--------activity is onDestroy----------");
	}

}
