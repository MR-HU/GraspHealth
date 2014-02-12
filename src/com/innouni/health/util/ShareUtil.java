package com.innouni.health.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
* SharedPreferences工具类 <br>
 * 保存和读取基本配置参数
 * 
 * @author HuGuojun
 * @date 2014-1-23 下午7:17:53
 * @modify
 * @version 1.0.0
 */
public class ShareUtil {
	
	private static SharedPreferences sharedPreferences;
	
	static ShareUtil shareUtil = new ShareUtil();
	
	public static ShareUtil getInstance(Context context){
		if(sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences("healthShareInfo", Context.MODE_PRIVATE);
		}
		if(shareUtil == null){
			return new ShareUtil();
		}
		return shareUtil;
	}
	
	public Editor setStringValues(String key,String value){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
		return editor;
	}
	
	public Editor setBooleanValues(String key,Boolean value){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
		return editor;
	}
	
	public Editor setFloatValues(String key, float value){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
		return editor;
	}
	
	public Editor setIntValues(String key, int value){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
		return editor;
	}
	
	public Editor setLongValues(String key, long value){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
		return editor;
	}
	
	public String getStringValues(String key){
		return sharedPreferences.getString(key, "");
	}
	
	public boolean getBooleanValues(String key){
		return sharedPreferences.getBoolean(key, false);
	}
	
	public float getFloatValues(String key){
		return sharedPreferences.getFloat(key, -1);
	}
	
	public int getIntValues(String key){
		return sharedPreferences.getInt(key, -1);
	}
	
	public Long getLongValues(String key){
		return sharedPreferences.getLong(key, -1);
	}
	
	public boolean removeShareValues(String key){
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		return editor.commit();
	}
}
