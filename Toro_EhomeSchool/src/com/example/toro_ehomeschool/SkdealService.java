package com.example.toro_ehomeschool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.example.toro.bo.EhomeBo;
import com.example.toro.doman.Student;
import com.example.toro.json.internal.util.NetUtils;
import com.example.toro.response.SubmitDStatusResponse;
import com.example.toro.sample.Application;
import com.example.toro.sample.Nfc;
import com.example.toro.sample.SerialPort;
import com.example.toro.sample.SerialPortActivity;
import com.example.toroapi.AppConstants;
import com.example.toroapi.PreferencesService;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * 刷卡监听
 * @author zhoulonglong
 * 通过后台service 监听刷卡
 */
public class SkdealService extends Service{

	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;

	private NetThreadPing netThreadPing;


	private PreferencesService pre=null;//偏好数据保存 (机器码保存读取)

	private int count=1;



	//监听网络中断线程
	private class NetThreadPing extends Thread{

		private Context contexts;
		public NetThreadPing(Context context){
			this.contexts=context;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			while(true){

				try {
					Thread.sleep(2*60*1000);

					boolean isConnected = NetUtils.isNetworkConnected(contexts);

					if(isConnected){

						submitDstatus(getSdcardSize(contexts));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}




	//监听刷卡数据线程
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			//如果该线程已经中断，则返回 true；否则返回 false
			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null) return;
					size = mInputStream.read(buffer);

					if (size > 0) {
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		//解析刷卡数据
		protected void onDataReceived(final byte[] buffer, final int size) {


			try {
				int cardtype,check;
				int[] cardnumber;


				System.out.println("===============================卡数据"+bytesToHexString(buffer));

				Nfc nfc=new Nfc();
				byte[] frameHeader=new byte[2];
				System.arraycopy(buffer, 0, frameHeader, 0, 2);
				nfc.setFrameHeader(bytesToHexString(frameHeader));

				/*byte[] CardTypes=new byte[1];
				System.arraycopy(buffer, 2, CardTypes, 0, 1);
				cardtype=CardTypes[0] & 0xFF;
				nfc.setCardType(bytesToHexString(CardTypes));*/

				byte[] CardNumbers=new byte[size-3];
				System.arraycopy(buffer, 2, CardNumbers, 0, size-3);
				cardnumber=bytetoInteger(CardNumbers);
				//nfc.setCardNumber("00"+bytesToHexString(CardNumbers));
				nfc.setCardNumber(bytesToHexString(CardNumbers));

				Long carnum=Long.parseLong(nfc.getCardNumber(),16);


				byte[] checkStr=new byte[1];
				System.arraycopy(buffer, size-1, checkStr, 0, 1);
				check=checkStr[0] & 0xFF;
				nfc.setCheckStr(bytesToHexString(checkStr));

				//boolean f=xor(cardtype,cardnumber,check);


				//	if(AppConstants.isbDebug){
				// final String bb="帧头："+nfc.getFrameHeader()+"卡类型："+nfc.getCardType()+"卡号："+nfc.getCardNumber()+"校验字节"+nfc.getCheckStr()+"是否校验成功:"+true;
				//System.out.println("===============================卡号========"+bb);
				//}

				final String bb="帧头："+nfc.getFrameHeader()+"卡类型："+nfc.getCardType()+"卡号："+nfc.getCardNumber()+"校验字节"+nfc.getCheckStr()+"是否校验成功:"+true;
				System.out.println("===============================卡号========"+bb);

				//发送刷卡广播
				Intent intent=new Intent();
				intent.putExtra("cid", carnum+"");
				intent.setAction(Application.SERIAL_PORT);
				SkdealService.this.sendBroadcast(intent);


			} catch (Exception e) {
				e.printStackTrace();

			}finally{
				//handler.sendMessage(msg);
			}

		}


		/**
		 *
		 * @param cardType 刷卡 校验（校验 异或 运算）
		 */
		public boolean xor(int cardType,int[] cardNumber,int check){

			for(int i=0;i<cardNumber.length;i++){
				cardType=cardType^cardNumber[i];
			}

			if(check==cardType){
				return true;
			}
			return false;
		}

		public int[] bytetoInteger(byte[] src){
			int[] ints=new int[src.length];
			if (src == null || src.length <= 0) {
				return null;
			}

			for (int i = 0; i < src.length; i++) {
				int v = src[i] & 0xFF;

				ints[i]=v;
			}

			return ints;
		}

		/**
		 * 刷卡数据解析
		 * @param src
		 * @return
		 */
		public  String bytesToHexString(byte[] src){
			StringBuilder stringBuilder = new StringBuilder();
			if (src == null || src.length <= 0) {
				return null;
			}
			for (int i = 0; i < src.length; i++) {
				int v = src[i] & 0xFF;
				String hv = Integer.toHexString(v);
				if (hv.length() < 2) {
					stringBuilder.append(0);
				}
				stringBuilder.append(hv);
			}
			return stringBuilder.toString();
		}

	}



	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mApplication = (Application) this.getApplication();
		try {


			if(pre==null){
				pre = new PreferencesService(SkdealService.this);
			}

			mSerialPort = mApplication.getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();


			netThreadPing=new NetThreadPing(this);
			netThreadPing.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(AppConstants.isbDebug){
			System.out.println("========================onstart");
		}

		return super.onStartCommand(intent, flags, startId);


	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(AppConstants.isbDebug){
			System.out.println("========================onDestroy");
		}

		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}



	private String getSdcardSize(Context context){
		long blockSize;
		long totalBlocks;
		long availableBlocks;
		String sdsurp="";
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		blockSize = stat.getBlockSize();
		totalBlocks = stat.getBlockCount();
		availableBlocks = stat.getAvailableBlocks();

		//内部存储空间总大小
		//	String totalText = formatSize(context,blockSize * totalBlocks);
		//剩余存储空间大小
		sdsurp = formatSize(context,blockSize * availableBlocks);


		return sdsurp;

	}

	private String formatSize(Context context,long size)
	{
		return Formatter.formatFileSize(context, size);
	}




	/**
	 * 提交机器状态
	 */
	@SuppressWarnings("finally")
	private boolean submitDstatus(final String sdsurp) {


		try {

			Map<String,String> map = new HashMap<String, String>();
			map.put("mid",AppConstants.Mid);
			map.put("time", System.currentTimeMillis()+"");
			map.put("sdsurplus", sdsurp);
			// 向服务器请求公告列表
			SubmitDStatusResponse sbstatusResponse = EhomeBo.submitDstatus(new Gson().toJson(map));

			if (sbstatusResponse == null||!sbstatusResponse.isSuccess()) {

				return false;
			} else{

				try {

					//System.out.println("===============tt"+sbstatusResponse.getT()+"==="+sbstatusResponse.getUptime()+"==="+sbstatusResponse.getDowntime());
					if(sbstatusResponse.getT().equals("4")){
						//重启命令

						Intent intent = new Intent("android.intent.action.pubds_reboot");
						SkdealService.this.sendBroadcast(intent);//重启系统指今

					}else if(sbstatusResponse.getT().equals("5")){
						//关机命令

						Intent PowerOffintent = new Intent("android.intent.action.shutdown");

						SkdealService.this.sendBroadcast(PowerOffintent);

					}else if(sbstatusResponse.getT().equals("6")){



						if(null!=sbstatusResponse.getUptime()&&null!=sbstatusResponse.getDowntime()){

							String s=pre.getstartime();
							String s1=sbstatusResponse.getUptime();
							String e=pre.getendtime();
							String e1=sbstatusResponse.getDowntime();


							if(s.equals(s1)&&e.equals(e1)&&count>=3){

							}else{

								//设置开关机时间
								int[] startime=new int[5];
								startime[0]=Integer.parseInt(sbstatusResponse.getUptime().substring(0, 2));
								startime[1]=Integer.parseInt(sbstatusResponse.getUptime().substring(2, 4));


								int[] endtime=new int[5];

								endtime[0]=Integer.parseInt(sbstatusResponse.getDowntime().substring(0, 2));
								endtime[1]=Integer.parseInt(sbstatusResponse.getDowntime().substring(2, 4));
								new BootThread(SkdealService.this, startime, endtime).start();
							}

						}

					}
				} catch (Exception e) {
					// TODO: handle exception
					//e.printStackTrace();
				}finally{
					return true;
				}
			}
		} catch (Exception e) {


			return false;
		}
	}



	private int[] parseInputTime(String tmpContent)
	{

		int[] intResultArray = new int[5];

		try {
			if(tmpContent == null ||
					tmpContent.trim().length() <= 3){

				return null;
			}



			intResultArray[0] = Integer.parseInt(tmpContent.substring(0, 2));

			if(intResultArray[0] < 0 || intResultArray[0] >= 24){
				return null;
			}



			intResultArray[1] = Integer.parseInt(tmpContent.substring(3, tmpContent.length()));
			if(intResultArray[1] <0 || intResultArray[1] >= 60){
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


		return intResultArray;
	}



	class BootThread extends Thread{

		private Context mcontext;
		private int[] startime,endtime;

		public BootThread(Context context,int[] startm,int[] endtm){
			this.mcontext=context;
			this.startime=startm;
			this.endtime=endtm;
		}

		public BootThread(){}

		@Override
		public void run() {
			try {
				// TODO Auto-generated method stub
				super.run();

					/*Intent cancelbootintent = new Intent("com.example.jt.boottime");
					cancelbootintent.putExtra("message", "0,0,0,0,0,0,0,0");
					mcontext.sendBroadcast(cancelbootintent);//取消定时开机

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Intent canceldownintent = new Intent("com.example.jt.shutdowntime");
					canceldownintent.putExtra("message", "cancel");
					mcontext.sendBroadcast(canceldownintent);//取消定时关机

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/


				String[] timestart =new String[2];

				if(startime[0]<10){
					timestart[0]="0"+startime[0];
				}else{
					timestart[0]=startime[0]+"";
				}

				if(startime[1]<10){
					timestart[1]="0"+startime[1];
				}else{
					timestart[1]=startime[1]+"";
				}

				String boottime = "1,0,0," + timestart[0] + "," + timestart[1]+ "," + startime[2] + "," + startime[3] + "," + startime[4];
				System.out.println("=========okok=="+boottime);
				Intent bootintent = new Intent("com.example.jt.boottime");
				bootintent.putExtra("message", boottime);
				mcontext.sendBroadcast(bootintent);


				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				String[] timend =new String[2];

				if(endtime[0]<10){
					timend[0]="0"+endtime[0];
				}else{
					timend[0]=""+endtime[0];
				}

				if(endtime[1]<10){
					timend[1]="0"+endtime[1];
				}else{
					timend[1]=""+endtime[1];
				}

				String shutdownTime = "1,0,0," + timend[0] + "," + timend[1]+ "," + endtime[2] + "," + endtime[3] + "," + endtime[4];
				Intent shutdownintent = new Intent("com.example.jt.shutdowntime");
				shutdownintent.putExtra("message", shutdownTime);

				pre.savestartime(timestart[0]+""+timestart[1]);
				pre.savendtime(timend[0]+""+timend[1]);

				System.out.println("=====okok定时关机 时间发送+=="+shutdownTime);
				mcontext.sendBroadcast(shutdownintent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}




}
