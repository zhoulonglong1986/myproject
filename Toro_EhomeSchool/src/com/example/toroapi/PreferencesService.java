package com.example.toroapi;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesService {

	private Context context;

	public PreferencesService(Context context) {
		this.context = context;
	}

	/**
	 * 保存机器码
	 * 
	 */
	public void saveMid(String mid) {
		// 获得SharedPreferences对象
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("mid", mid);
		editor.commit();
	}
	

	/**
	 * 保存刷卡间隔
	 * 
	 */
	public void saveCreditinterval(String inter) {
		// 获得SharedPreferences对象
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("inter", inter);
		editor.commit();
	}
	
	
	/**
	 * 保存机器所在城市
	 */
	public void saveCity(String city){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("city", city);
		editor.commit();
	}
	

	/**
	 * 获取机器码
	 * 
	 * @return
	 */
	public String getMid(){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		String mid = preferences.getString("mid", "");

		return mid;
	}
	
	/**
	 * 获取刷卡间隔
	 * 
	 * @return
	 */
	public String getCreditinterval(){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		String mid = preferences.getString("inter", "");

		return mid;
	}
	/**
	 * 获取机器所在城市
	 * @return
	 */
	public String getCity(){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		String city = preferences.getString("city", "");

		return city;
	}
	
	
	/**
	 * 保存开机时间
	 * 
	 */
	public void savestartime(String inter) {
		// 获得SharedPreferences对象
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("start", inter);
		editor.commit();
	}
	
	/**
	 * 获取开机时间
	 * 
	 * @return
	 */
	public String getstartime(){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		String mid = preferences.getString("start", "");

		return mid;
	}
	
	/**
	 * 保存关机时间
	 * 
	 */
	public void savendtime(String inter) {
		// 获得SharedPreferences对象
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("end", inter);
		editor.commit();
	}
	
	/**
	 * 获取关机时间
	 * 
	 * @return
	 */
	public String getendtime(){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		String mid = preferences.getString("end", "");

		return mid;
	}

}
