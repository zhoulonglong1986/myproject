package com.example.toro_ehomeschool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.example.toro.bo.EhomeBo;
import com.example.toro.doman.Member;
import com.example.toro.doman.Push;
import com.example.toro.doman.SchNotice;
import com.example.toro.doman.Student;
import com.example.toro.response.SubmitDStatusResponse;
import com.example.toro.response.getSchNoticeResponse;
import com.example.toro.response.getStuInformationResponse;
import com.example.toro.sample.Application;
import com.example.toro.sample.db.DbHelper;
import com.example.toroapi.AppConstants;
import com.example.toroapi.FileDownloadThread;
import com.example.toroapi.PreferencesService;
import com.google.gson.Gson;
import android.text.format.Formatter;
/**
 * 百度云推送广播
 * @author zhoulonglong
 *
 */
public class PushchReceiver extends PushMessageReceiver{
	
	private DbHelper db=null;
	private String sdsurp;//剩余内存空间
	private PreferencesService pre=null;//偏好数据保存 (机器码保存读取)
	

	@Override
	public void onBind(Context arg0, int arg1, String arg2, String arg3,
			String arg4, String arg5) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onDelTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListTags(Context arg0, int arg1, List<String> arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(Context context, String message, String customContentString) {
		// TODO Auto-generated method stub
		
		
		if(pre==null){
			pre = new PreferencesService(context);
		}
		
		
		try {
			if(null==db){
				db=DbHelper.getInstance(context);
			} 
			if(AppConstants.isbDebug){
				System.out.println("==================Message"+message);
			}
			
			Gson gson=new Gson();
			Push push=gson.fromJson(message, Push.class);
			if(AppConstants.isbDebug)
			System.out.println("==================Message"+push.getT());
			//学校公告
			if(push.getT()==1){
				switch (push.getType()) {
				case 1://删除
					db.deleteSch(push.getId());
					break;
				case 0://增加
				case 2://修改
					getSchNotice();
					break;
							

				default:
					break;
				}
				
			 //学生信息
			}else if(push.getT()==2){
				switch (push.getType()) {
				
				case 1://刪除
					
					db.deleteStu(push.getId());
					break;
				case 0://增加
				case 2://修改
					getStuInfo(push.getId());
					break;

				default:
					break;
				}
			 //发送机器状态信息
			}else if(push.getT()==3){
			    
				getSdcardSize(context);
				submitDstatus();
			}else if(push.getT()==4){
				//重启命令
				
				Intent intent = new Intent("android.intent.action.pubds_reboot");
				context.sendBroadcast(intent);//重启系统指今
				
			}else if(push.getT()==5){
				//关机命令
				
				Intent PowerOffintent = new Intent("android.intent.action.shutdown");
				   // updateintent.putExtra("path", updatepath);
				context.sendBroadcast(PowerOffintent);
				
			}else if(push.getT()==6){
				//设置开关机时间
				int[] startime=parseInputTime(push.getUptime());
				int[] endtime=parseInputTime(push.getDowntime());
				
				
				if(null!=startime&&null!=endtime){
					
					new BootThread(context, startime, endtime).start();
					
				}
				
				
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNotificationClicked(Context arg0, String arg1, String arg2,
			String arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnbind(Context arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	
    
    private void getSdcardSize(Context context){
    	long blockSize;
        long totalBlocks;
        long availableBlocks;
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());	
    	blockSize = stat.getBlockSize();
    	totalBlocks = stat.getBlockCount();
    	availableBlocks = stat.getAvailableBlocks();

    	//内部存储空间总大小
       //	String totalText = formatSize(context,blockSize * totalBlocks);
    	//剩余存储空间大小
    	sdsurp = formatSize(context,blockSize * availableBlocks);

    	
    }
    
    private String formatSize(Context context,long size)
    {
        return Formatter.formatFileSize(context, size);
    }
	
	/**
	 * 提交机器状态
	 */
	private void submitDstatus() {
       
		
		new Thread() {
			@Override
			public void run() {
				super.run();

				try {

					  Map<String,String> map = new HashMap<String, String>();
		                map.put("mid",AppConstants.Mid);
		                map.put("time", System.currentTimeMillis()+"");
		                map.put("sdsurplus", sdsurp);
					// 向服务器请求公告列表
					SubmitDStatusResponse sbstatusResponse = EhomeBo.submitDstatus(new Gson().toJson(map));

					if (sbstatusResponse == null) {
						if(AppConstants.isbDebug){
							System.out.println("===============push3null");
						}
					} else if (!sbstatusResponse.isSuccess()) {
						if(AppConstants.isbDebug){
							System.out.println("===============push3no_success");
						}
					} else {
						if(AppConstants.isbDebug){
							System.out.println("===============push3ok");
						}
			
					}
				} catch (Exception e) {
					e.printStackTrace();
					if(AppConstants.isbDebug){
						System.out.println("===============push3error");
					}
				}
			}
		}.start();
	}
	
	
	/**
	 * 获取学校公告
	 */
	private void getSchNotice() {

		
		new Thread() {
			@Override
			public void run() {
				super.run();

				try {

					// 向服务器请求公告列表
					getSchNoticeResponse schnoticeres = EhomeBo.getSchNotice("");

					if (schnoticeres == null) {
						
					} else if (!schnoticeres.isSuccess()) {
						
					} else {
						ArrayList<SchNotice> schList = schnoticeres.getSchNotices();
						db.insertOrSchnotice(schList);
						for (SchNotice sch : schList) {
							if (sch.getType() == 1) {
								downSch(sch.getPhoto());
							} else if (sch.getType() == 2) {
								downSch(sch.getVideo());
							}
						}
					
					}
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}
		}.start();
	}
	
	/**
	 * 下载公告中的图片和视频
	 * @param downurl
	 */
	private void downSch(String downurl){
		String fileName = downurl.substring(downurl.lastIndexOf("/") + 1);
        
        String filepath = Application.schPath + fileName.trim();
        
     
		BufferedInputStream bis = null;
		RandomAccessFile raf = null;

		try {

			URL url = new URL(downurl);
			URLConnection conn = url.openConnection();
			conn.setAllowUserInteraction(true);

			byte[] buffer = new byte[1024];
			bis = new BufferedInputStream(conn.getInputStream());

			File file = new File(filepath);
			raf = new RandomAccessFile(file, "rwd");

			int len;
			while ((len = bis.read(buffer, 0, 1024)) != -1) {
				raf.write(buffer, 0, len);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		}
	}

	
	/**
	 * 获取学生信息
	 */
	private void getStuInfo(final String id) {

		
		new Thread() {
			@Override
			public void run() {
				super.run();

				try {

					// 向服务器请求公告列表
					getStuInformationResponse stuResponse = EhomeBo.getStuInformation(AppConstants.Mid,id,"1");

					if (stuResponse == null) {
						
					} else if (!stuResponse.isSuccess()) {
						
					} else {
						Student stu=new Student();
						stu.setId(stuResponse.getRid());
						stu.setCids(stuResponse.getCids());
						stu.setMembers(stuResponse.getMembers());
						db.insertOrStu(stuResponse.getRid(), stu);
						
						for(Member men:stu.getMembers()){
							downloadimg(men.getPhoto());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					//resHandler.sendEmptyMessage(REQUEST_SCH_EORR);
				}
			}
		}.start();
	}
	
	
	private void downloadimg(String imageurl){
		try {
			// TODO Auto-generated method stub
			String fileName = imageurl.substring(imageurl.lastIndexOf("/") + 1);

			String filepath = Application.stuPath + fileName;
			
			File file=new File(filepath);
			if(file.exists()){
				return;
			}

			BufferedInputStream bis = null;
			RandomAccessFile raf = null;

			try {

				URL url = new URL(imageurl);
				URLConnection conn = url.openConnection();
				conn.setAllowUserInteraction(true);

				byte[] buffer = new byte[1024];
				bis = new BufferedInputStream(conn.getInputStream());

				
				raf = new RandomAccessFile(file, "rwd");

				int len;
				while ((len = bis.read(buffer, 0, 1024)) != -1) {
					raf.write(buffer, 0, len);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (raf != null) {
					try {
						raf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
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
