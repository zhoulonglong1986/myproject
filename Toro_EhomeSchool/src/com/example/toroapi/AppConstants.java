package com.example.toroapi;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Environment;

import com.example.toroapi.DefaultTogetherClient;

public class AppConstants {
	/** �Ƿ�Ϊ����ģʽ */
	public static boolean isTest= false;
	public static boolean isbDebug=true;
	
	
	
	public static String Mid = "";
	
	public static int projectId = 100;
	
	public static  boolean netConnect=true;//����״̬��־

	// public static int projectId = PROJECT.YOUJIAXIAO;

	public static void setProjectId(int projectId) {
		AppConstants.projectId = projectId;
	}

	public static final String pv = "2";
	public static final String pv3 = "3";

	public static final String rootPath = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	

	public static final String KEY = "54b4c01f-dce0-102a-a4e0-462c07a00c5e";
	public static String Security_Key = "yvAfFVpX0xoTeAVT6AsCHycxZeWOvvB3";// �ӿڰ�ȫ��Կ

	
	public static final boolean Is_encrypted = false; // �Ƿ���� true ���ܣ� false ������


}
