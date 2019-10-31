/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.toro.sample;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Random;

import com.example.toro.doman.SchNotice;
import com.example.toro_ehomeschool.LoadingActivity;
import com.example.toroapi.AppConstants;
import com.linj.FileOperateUtil;
import com.tencent.bugly.crashreport.CrashReport;


import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

public class Application extends android.app.Application {

	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;
	public static String schPath="",stuPath="",suaicar="",apkpath="";
	public static final String SERIAL_PORT="serialport";
	public static final String SUACARS="suacar";
	public static final String HUOTAN_DATA="huotadata";
	public static final String HUOTAN_DATA_STOP="huotadatastop";
	public static boolean ismain=true;
	public static String wendu="";
	public static int tanqin=0;
	public static int  tlb=10;
	public static int interval=60;
	public static boolean iswhetherStart=false;
	public static ArrayList<SchNotice> schTxtList=new ArrayList<SchNotice>();




	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CrashReport.initCrashReport(getApplicationContext(), "900019291", false);
		CrashReport.putUserData(getApplicationContext(), "Mid",AppConstants.Mid+"");

		// 异常处理
		// CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(getApplicationContext());
		try {
			tlb=this.getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;

			System.out.println("=================="+tlb);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		schPath=FileOperateUtil.getFolderPath(getApplicationContext(), FileOperateUtil.TYPE_SCH);
		stuPath=FileOperateUtil.getFolderPath(getApplicationContext(), FileOperateUtil.TYPE_STU);
		suaicar=FileOperateUtil.getFolderPath(getApplicationContext(), FileOperateUtil.TYPE_SUAICAR);
		apkpath=FileOperateUtil.getFolderPath(getApplicationContext(), FileOperateUtil.TYPE_APK);

		File schfile = new File(schPath);
		// 如果SD卡目录不存在创建
		if (!schfile.exists()) {  schfile.mkdir();   }

		File stufile = new File(stuPath);
		// 如果SD卡目录不存在创建
		if (!stufile.exists()) {  stufile.mkdir();  }
		File suaicarfile = new File(suaicar);
		// 如果SD卡目录不存在创建
		if (!suaicarfile.exists()) {  suaicarfile.mkdir(); }

		File sapkfile = new File(apkpath);
		// 如果SD卡目录不存在创建
		if (!suaicarfile.exists()) {  suaicarfile.mkdir(); }

		//CrashReport.testJavaCrash();
	}
	public  SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
			SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			//String path = "/dev/ttyS4";
			String path = "/dev/ttyS3";//21寸
			int baudrate = 9600;

			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
