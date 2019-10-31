package com.example.toro_ehomeschool;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.toro.bo.EhomeBo;
import com.example.toro.doman.SuaiCar;
import com.example.toro.json.internal.util.NetUtils;
import com.example.toro.response.SubmitCardataResponse;
import com.example.toro.sample.Application;
import com.example.toro.sample.db.DbHelper;
import com.example.toroapi.AppConstants;
import com.linj.FileOperateUtil;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.Message;
/**
 *  刷卡数据后台处理service
 * @author Administrator
 *
 */
public class SkDataService extends Service{

	/**
	 * 网络监听广播
	 */
	private IntentFilter filter;
	private BroadcastReceiver sucarBroadcast=null;
	private boolean backoff=false;

	/**
	 * 刷卡数据上传 线程池
	 */
	private ExecutorService exe=Executors.newFixedThreadPool(5);

	private DbHelper db;//数据库操作对象

	/**
	 * 后台传数据，一旦不刷卡，进入公告界面，就在后台传数据。
	 */
	private BgThread  bgThread=null;




	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initReceiver();
		db = DbHelper.getInstance(this);//初始化数据库对象
		registerReceiver(sucarBroadcast, filter);//注册刷卡数据处理广播

	}

	/**
	 * 初始化广播
	 */
	private void initReceiver(){

		filter = new IntentFilter();
		//刷卡传数据指今
		filter.addAction(Application.SUACARS);
		//后台传传数据指今
		filter.addAction(Application.HUOTAN_DATA);
		//停止后台传数据
		filter.addAction(Application.HUOTAN_DATA_STOP);
		sucarBroadcast = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();
				//如果接收到刷卡广播则 新开一个刷卡数据上传线程，加入到线程池中，
				if(Application.SUACARS.equals(action)){

					String cid=intent.getStringExtra("cid");
					String time=intent.getStringExtra("time");
					exe.execute(new CuaiCarThread(cid,time));
					//如果接收到 后台 传送刷卡数据指令，则开启后台 刷卡数据上传线程，扫描数据库中有没有 没上传的刷卡数据，有则上传
				}else if(Application.HUOTAN_DATA.equals(action)&&AppConstants.netConnect){

					if(null==bgThread||backoff){
						bgThread=new BgThread();
						bgThread.start();
					}else{
						bgThread.setSuspend(false);
					}
					//当，前台进入刷卡界面 ，则停止 后台传数据线程
				}else if(Application.HUOTAN_DATA_STOP.equals(action)){
					if(null!=bgThread){
						bgThread.setSuspend(true);
					}
				}
			}

		};
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);


	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		//销毁刷卡数据 监听广播
		unregisterReceiver(sucarBroadcast);
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}


	/**
	 * 提交 刷卡数据线程
	 */
	class CuaiCarThread extends Thread{
		String cid;
		String time;
		String photoFile;
		SubmitCardataResponse submitCardataResponse; //提交刷卡数据 Response
		public CuaiCarThread(String cids,String times){
			this.cid=cids;
			this.time=times;
			photoFile=cids+"_"+time;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			if(AppConstants.netConnect){
				try {
					File file=getStuImage(photoFile);
					// 向服务器请求学生列表

					submitCardataResponse = EhomeBo.submitgCard(cid,file,time);

					if (submitCardataResponse == null) {
						//Toast.makeText(SuaicarActivity.this,R.string.refresh_failed, Toast.LENGTH_SHORT).show();
						db.insertOrSuaiCar(new SuaiCar(cid,photoFile,time,0));
					} else if (!submitCardataResponse.isSuccess()) {
						//Toast.makeText(SuaicarActivity.this,submitCardataResponse.getMsg(), Toast.LENGTH_SHORT).show();
						db.insertOrSuaiCar(new SuaiCar(cid,photoFile,time,0));
					} else {
						//提交刷卡数据成功（删除图片）
						System.out.println("=========================成功");
						String imgName=FileOperateUtil.createFileNmae(photoFile,".jpg");
						String imagePath=Application.suaicar+imgName;
						File File=new File(imagePath);
						if (File.exists()) { // 文件存在则删除
							File.delete();
						}


					}

				} catch (Exception e) {
					e.printStackTrace();
					//调试用，正常后注释


					if(null==submitCardataResponse||!submitCardataResponse.isSuccess()){
						db.insertOrSuaiCar(new SuaiCar(cid,photoFile,time,0));
					}
					if(AppConstants.isbDebug){
						System.out.println("===================submitCardataRespons===error");
					}

					//Toast.makeText(SuaicarActivity.this, "获取学生列表出现数据异常",Toast.LENGTH_SHORT).show();
				}
			}else{
				//如果没有网络则保存在本地
				db.insertOrSuaiCar(new SuaiCar(cid,photoFile,time,0));
			}


		}

		/**
		 * 根据 卡 ID 去 存储中 找图片
		 * @param cid
		 * @return
		 */
		private File getStuImage(String filename){
			String path=Application.suaicar+filename+".jpg";
			File file=new File(path);
			if(!file.exists()){
				return null;
			}
			return file;
		}

	}


	class BgThread extends Thread{

		boolean suspend = false;//是否暂停

		String control = "";

		SubmitCardataResponse submitCardataResponsess; //提交刷卡数据 Response


		public void setSuspend(boolean suspend) {
			if (!suspend) {
				synchronized (control) {
					control.notifyAll();
				}
			}
			this.suspend = suspend;
		}

		public boolean isSuspend() {
			return this.suspend;
		}

		/**
		 * 根据 卡 ID 去 存储中 找图片
		 * @param cid
		 * @return
		 */
		private File getStuImage(String filename){
			String path=Application.suaicar+filename+".jpg";
			File file=new File(path);
			if(!file.exists()){
				return null;
			}
			return file;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				ArrayList<SuaiCar> suaicarList=db.queryScfeng();
				if(null!=suaicarList&&suaicarList.size()>0){
					for(SuaiCar cuaicar:suaicarList){

						synchronized (control) {
							if (suspend) {
								try {
									control.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						//如果没有传过就传
						if(cuaicar.getState()==0&&AppConstants.netConnect){

							try {
								File file=getStuImage(cuaicar.getPhoto());
								// 向服务器请求学生列表

								submitCardataResponsess = EhomeBo.submitgCard(cuaicar.getCid(),file,cuaicar.getTime());

								if (submitCardataResponsess == null) {
									if(AppConstants.isbDebug){
										System.out.println("===================submitCardataRespons===null");
									}

								} else if (!submitCardataResponsess.isSuccess()) {
									if(AppConstants.isbDebug){
										System.out.println("===================submitCardataRespons===false");
									}


									String imgName=FileOperateUtil.createFileNmae(cuaicar.getPhoto(),".jpg");
									String imagePath=Application.suaicar+imgName;
									File File=new File(imagePath);
									if (File.exists()) { // 文件存在则删除
										File.delete();
									}

									db.deleteSuaiCar(cuaicar.getPhoto());

								} else {
									if(AppConstants.isbDebug){
										//提交刷卡数据成功
										System.out.println("===================submitCardataRespons===true");
									}

									//成功则将数据库 里的那一条删除掉，找图片也删除掉

									String imgName=FileOperateUtil.createFileNmae(cuaicar.getPhoto(),".jpg");
									String imagePath=Application.suaicar+imgName;
									File File=new File(imagePath);
									if (File.exists()) { // 文件存在则删除
										File.delete();
									}

									db.deleteSuaiCar(cuaicar.getPhoto());

								}

							} catch (Exception e) {
								e.printStackTrace();
								//调试用，正常后注释
								if(AppConstants.isbDebug){
									System.out.println("===================submitCardataRespons===error");
								}

							}finally{
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}



					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}else{
					if(AppConstants.isbDebug){
						System.out.println("===========backoff");
					}

					try {
						//每隔10分钟扫描 一次数据库
						Thread.sleep(10*60*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					backoff=true;
					break ;
				}

			}
		}

	}




}
