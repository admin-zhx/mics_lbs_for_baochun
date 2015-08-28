package com.baochun.bean;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.baochun.utils.Constant;

import android.util.Xml;

public class Update  {
	public final static String UTF8 = "UTF-8";
	private int versionCode;
	private String versionName;
	private String appName;
	private String downloadUrl;
	private String updateLog;

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}

	public static Update parse(InputStream inputStream) throws IOException,XmlPullParserException,
			Exception {
		Update update = null;
		// 获得XmlPullParser解析器
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(inputStream, UTF8);
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = xmlParser.getEventType();
			// 一直循环，直到文档结束
			while (evtType != XmlPullParser.END_DOCUMENT) {

				switch (evtType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tag = xmlParser.getName();
//					LogUtil.e("开始解析xml :"+"evtType==" + evtType + "  tag===" + tag);
					// 通知信息
					if (tag.equalsIgnoreCase("apk")) {
						update = new Update();
					} else if (update != null)
					{
						if (tag.equalsIgnoreCase("versionCode")) {
							update.setVersionCode(1);
							String text = xmlParser.nextText();
							if (text != null
									&& !text.isEmpty())
								update.setVersionCode(Integer
										.parseInt(text));

						} else if (tag.equalsIgnoreCase("versionName")) {
							String text = xmlParser.nextText();
							if (text != null && text != "") {
								update.setVersionName(text);
							}
						} else if (tag.equalsIgnoreCase("name")) {
							String text = xmlParser.nextText();
							if (text!= null
									&& !text.isEmpty()) {
								update.setAppName(text);
								update.setDownloadUrl(Constant.URL_DOWNLOAD
										+ update.getAppName() + ".apk");
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evtType = xmlParser.next();
			}
		} catch (XmlPullParserException e) {

		} finally {
			inputStream.close();
		}
		return update;
	}

	@Override
	public String toString() {
		return "Update [versionCode=" + versionCode + ", versionName="
				+ versionName + ", appName=" + appName + ", downloadUrl="
				+ downloadUrl + ", updateLog=" + updateLog + "]";
	}

}
