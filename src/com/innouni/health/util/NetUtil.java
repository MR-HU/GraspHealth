package com.innouni.health.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * {@code NetUtils}网络工具类<br>
 * <li></li>判断网络状态与模式<br>
 * <li></li>对外方法： {@link #isAirplaneMode}, {@link #isNetworkAvailable},
 * {@link #isURLAvailable}
 * 
 * @author HuGuojun
 * @date 2013-12-30 上午9:51:54
 * @modify
 * @version 1.0.0
 */
public class NetUtil {

	/**
	 * 判断飞行模式;
	 * 
	 * @param context
	 * @return true飞行模式 false非飞行模式
	 * @category 静态公用方法
	 */
	public static boolean isAirplaneMode(Context context) {
		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) == 1;
	}

	/**
	 * 判断网络是否可用;
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null)
			return false;
		return networkinfo.isAvailable() && networkinfo.isConnected();
	}

	/**
	 * 判断URL是否可用;
	 */
	public static boolean isURLAvailable(Context context, String strUrl) {
		boolean result = false;
		if (isNetworkAvailable(context)) {
			try {
				URL url = new URL(strUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(3 * 1000);
				if (conn.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
					result = true;
				}
			} catch (IOException e) {
				result = false;
			}
		}
		return result;
	}
}
