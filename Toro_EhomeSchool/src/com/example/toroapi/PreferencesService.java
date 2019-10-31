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
	 * ���������
	 * 
	 */
	public void saveMid(String mid) {
		// ���SharedPreferences����
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("mid", mid);
		editor.commit();
	}
	

	/**
	 * ����ˢ�����
	 * 
	 */
	public void saveCreditinterval(String inter) {
		// ���SharedPreferences����
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("inter", inter);
		editor.commit();
	}
	
	
	/**
	 * ����������ڳ���
	 */
	public void saveCity(String city){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("city", city);
		editor.commit();
	}
	

	/**
	 * ��ȡ������
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
	 * ��ȡˢ�����
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
	 * ��ȡ�������ڳ���
	 * @return
	 */
	public String getCity(){
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		String city = preferences.getString("city", "");

		return city;
	}
	
	
	/**
	 * ���濪��ʱ��
	 * 
	 */
	public void savestartime(String inter) {
		// ���SharedPreferences����
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("start", inter);
		editor.commit();
	}
	
	/**
	 * ��ȡ����ʱ��
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
	 * ����ػ�ʱ��
	 * 
	 */
	public void savendtime(String inter) {
		// ���SharedPreferences����
		SharedPreferences preferences = context.getSharedPreferences(
				"preferences", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("end", inter);
		editor.commit();
	}
	
	/**
	 * ��ȡ�ػ�ʱ��
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
