package com.example.toro_ehomeschool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.example.toro.bo.EhomeBo;
import com.example.toro.doman.Card;
import com.example.toro.doman.Member;
import com.example.toro.doman.SchNotice;
import com.example.toro.doman.Softupdate;
import com.example.toro.doman.Student;
import com.example.toro.json.internal.util.NetThread;
import com.example.toro.json.internal.util.NetThread.Callback;
import com.example.toro.json.internal.util.NetUtils;
import com.example.toro.response.getSchNoticeResponse;
import com.example.toro.response.getStuInformationResponse;
import com.example.toro.response.getStuListResponse;
import com.example.toro.sample.Application;
import com.example.toro.sample.db.DbHelper;
import com.example.toroapi.AppConstants;
import com.example.toroapi.AppUtils;
import com.example.toroapi.DownloadTask;
import com.example.toroapi.Downloader;
import com.example.toroapi.FileDownloadThread;
import com.example.toroapi.HttpService;
import com.example.toroapi.HttpUtils;
import com.example.toroapi.PreferencesService;
import com.example.toroapi.scan.CaptureActivity;

import com.linj.FileOperateUtil;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 优家校同步更新页面
 * @author zhoulonglong 2016/4/5
 * 功能：1、联网读取公告和学生列表数据
 *      2、获取天气预报数据
 *      3、升级APP
 *
 */
public class LoadingActivity extends Activity {

	private TextView tv_update,tv_downloadname;//更新状态显示 TextView
	private String fileUrl="";	//当前下载公告文件url
	private int progress=1;// 记录进度条数量
	private ProgressBar pb_download;//下载文件进度条
	private PreferencesService pre;//偏好数据保存 (机器码保存读取)
	private DbHelper db;//数据库操作 工具
	private getSchNoticeResponse schnoticeres;//返回公告数据
	private getStuListResponse stulistres;//返回学生列表
	private getStuInformationResponse stuInformationres;//返回学生信息列表

	/**
	 * 断网提示dialog
	 */
	private AlertDialog dialogs=null;
	private AlertDialog.Builder dialogBuilder=null;


	/**
	 * 更新APP 对话框
	 */
	private Dialog downloadDialog = null;
	private File appFile;
	private ProgressBar bar = null;//更新app 进度条
	private TextView proText = null;//更新app 百度比 textView

	/**
	 * 常量
	 */
	private final int GET_SCH_OVER = 0;//获取公告列表成功
	private final int GET_SCH_UPDATE = 1;//升级APP
	private final int GET_SCH_EORR = 2;//获取公告列表异常
	private final int GET_STU_LIST = 3;//获取学生列表成功
	private final int GET_STU_EORR = 4;//获取学生列表异常
	private final int START_MAIN=5;	//跳转到主界面
	private final int UPDATE_PB = 6;//更新进度条
	private final int INIT_PB=7;//初始化进度
	private final int DOWNLOAD_APP_UPDATE=8;//下载APP 更新

	/**
	 * 全局处理Handler
	 */
	private Handler handler = new Handler() {
				@Override
				public void handleMessage(android.os.Message msg) {
					super.handleMessage(msg);

					try {
						switch (msg.what) {
							case GET_SCH_OVER:

								String city=schnoticeres.getCity();
								if("".equals(city)){
									city=pre.getCity();
								}else{
									pre.saveCity(city);
								}
								//获取公告完成,异步请求天气预报
								System.out.println("======================city"+city);

								if(null!=city&&!"".equals(city)){
									getWeather(city);
								}else{
									getStuList();
								}

								break;
							case GET_SCH_UPDATE:

								//升级 APP
								Softupdate softupdate=schnoticeres.getSoftupdate();
								Dialog dialog = startDownloadApp(softupdate.getAppUrl(),softupdate.getReleaseLog());
								dialog.setCancelable(false);
								break;
							case GET_SCH_EORR:

								if(AppConstants.isbDebug){
									//获取公告异常（null,失败，异常）
									if(null==schnoticeres){
										Toast.makeText(LoadingActivity.this,"获取公告为空", Toast.LENGTH_SHORT).show();
									}else{
										if(!schnoticeres.isSuccess()){

											try {

												System.out.println("===msg"+schnoticeres.getMsg().toString().trim());
												if(schnoticeres.getMsg().toString().trim().equals("未知的考勤机")){

													Toast.makeText(LoadingActivity.this,"未知的考勤机", Toast.LENGTH_LONG).show();
													LoadingActivity.this.finish();

													return;

												}else{
													Toast.makeText(LoadingActivity.this,"获取公告失败", Toast.LENGTH_SHORT).show();
												}
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}

										}
									}

								}

								String citys=schnoticeres.getCity();
								if("".equals(citys)){
									citys=pre.getCity();
								}else{
									pre.saveCity(citys);
								}

								if(null!=citys&&!"".equals(citys)){
									getWeather(citys);
								}else{
									getStuList();
								}

								//不管有没有获取到公告 ,都去获取天气预报
								getWeather(citys);
								break;
							case GET_STU_LIST:

								//获取学生列表成功跳转到主界面
								startMain();
								break;

							case GET_STU_EORR:

								if(AppConstants.isbDebug){

									if(null==stulistres){
										Toast.makeText(LoadingActivity.this,"获取学生列表null", Toast.LENGTH_SHORT).show();
									}else if(stulistres.isSuccess()){
										Toast.makeText(LoadingActivity.this,"解释学生失败", Toast.LENGTH_SHORT).show();
									}else{
										Toast.makeText(LoadingActivity.this,"解释学生XML 出现异常", Toast.LENGTH_SHORT).show();
									}
								}

								//获取学生列表出现异常跳转到主界面(不管有什么异常都跳转到主界面)
								startMain();
								break;
							case START_MAIN:

								if(null!=dialogs){
									dialogs.dismiss();
								}

								//跳转到主界面
								startMain();
								break;
							case UPDATE_PB:

								//更新进度条
								if(progress<=100){
									//pb_download.setProgress(progress);
								}

								//tv_downloadname.setText(fileUrl);
								break;
							case INIT_PB:

								//初始化进度条
								progress=1;
								//tv_update.setText("正在同步学生列表");
								//pb_download.setProgress(progress);
								break;

							case DOWNLOAD_APP_UPDATE:
								//更新下载进度
								int progres = msg.arg1;
								if (progres == -1) {
									downloadDialog.dismiss();
								} else {
									proText.setText(progres + "%");
									bar.setProgress(progres);

									if (progres >= 100) {
										downloadDialog.dismiss();
										if (appFile != null) {
											install(appFile.getAbsolutePath());
										}
									}
								}

								break;

							default:
								break;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}


				}
			};

	/**
	 * 异步请求天气
	 */
	private void getWeather(String city){

		try {
			if(null!=city&&!"".equals(city)){
				QueryAsyncTask asyncTask = new QueryAsyncTask();
				asyncTask.execute(city);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);


		try {


			setupView();//初始化控件

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		try {

			init();//初始化变量
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loading, menu);
		return true;

	}

	/**
	 * 初始化控件
	 */
	private void setupView(){
		//tv_update=(TextView)this.findViewById(R.id.tv_update);
		//tv_downloadname=(TextView)this.findViewById(R.id.tv_name);

		pb_download=(ProgressBar) this.findViewById(R.id.pb_load);
		pb_download.setIndeterminate(true);
		//pb_download.setMax(100);
		//pb_download.setProgress(progress);



	}

	/**
	 * 初始化变量
	 */
	private void init() {

		pre = new PreferencesService(this);
		try {
			String inter=pre.getCreditinterval();

			if("".equals(inter)){
				Application.interval=60;
				pre.saveCreditinterval(60+"");
			}else{
				Application.interval=Integer.parseInt(inter);
			}


		} catch (Exception e) {
			// TODO: handle exception
		}

		db = DbHelper.getInstance(this);
		// 先从数据偏好中 获取机器码
		String mid = pre.getMid();
		//mid="20161108";//测试用 05/5
		if ("".equals(mid)) {
			// 如果没有则从数据库中去取
			mid = db.queryMid();
			if ("".equals(mid)) {
				// 开启扫描机器码 页面
				Intent intent = new Intent(this, MidSetActivity.class);
				startActivityForResult(intent, 10);
				return;
			}
		}
		AppConstants.Mid = mid;
		//初始化百度云，检查网络获取公告
		//initPush(mid);
		checkNetSch();
	}

	/**
	 * 初始化百度云推送
	 */
	private void initPush(String mid){
		PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY,"y5n65z5pGOs2hsog37blCKTb");
		ArrayList<String> tags=new ArrayList<String>();
		tags.add(mid);
		PushManager.setTags(getApplicationContext(), tags);
	}


	/**
	 * activity 回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		try {

			switch (requestCode) {
				case 10: {// 时间段设置
					if (resultCode == RESULT_OK) {
						String mid= data.getStringExtra("mid");
						//保存机器码
						pre.saveMid(mid);
						db.insertOrtMid(mid);
						AppConstants.Mid = mid;
						//初始化百度云，检查网络获取公告
						initPush(mid);
						checkNetSch();
					}
					break;
				}

			}
			super.onActivityResult(requestCode, resultCode, data);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	/**
	 * 检查网络状态
	 */
	private void checkNetSch() {

		boolean isConnected = NetUtils.isNetworkConnected(LoadingActivity.this);

		boolean wifi=NetUtils.isWifiConnected(LoadingActivity.this);

		System.out.println("=============wifi==="+wifi);
		if(!isConnected){
			//断开网络（等待网络连接）
			new NetThread(LoadingActivity.this, new Callback() {
				@Override
				public void connect(int i) {
					// TODO Auto-generated method stub
					if(i==1){//连接网络成功
						getSchThread();
					}else if(i==5){

						Toast.makeText(LoadingActivity.this, "当前设备无网络，正在尝试重新连接！！", Toast.LENGTH_LONG).show();

					}else if(i==10){//重复连接网络10秒失败
						deleteMemberDialog("     网络连接失败，当前设备无网络！系统进入本地刷卡状态！！无网络有会造成数据传输不及时！请及时排查网络！");
						new Thread(){
							public void run() {

								try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//跳转到主页面
								handler.sendEmptyMessage(START_MAIN);
							};

						}.start();

					}
				}
			});

		}else{
			initPush(AppConstants.Mid);
			//没有断开网络则 发送请求（请求公告数据）
			getSchThread();
		}

	}

	/**
	 * 显示断网提示框
	 * @param txt
	 */
	private void deleteMemberDialog(String txt) {

		try {
			dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setMessage(txt);
			dialogBuilder.setTitle("无网络提示");
			dialogBuilder.setIcon(R.drawable.warning);
			dialogs=dialogBuilder.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取公告 线程
	 */
	private void getSchThread(){
		new Thread() {
			@Override
			public void run() {
				super.run();

				try {

					// 向服务器请求公告列表
					schnoticeres = EhomeBo.getSchNotice(AppConstants.Mid);
					//schnoticeres = EhomeBo.getSchNotice("20161108");


					System.out.println("===============r============="+schnoticeres.getResult());
					System.out.println("===============r=========="+schnoticeres.getBody());
					System.out.println("===============r========"+schnoticeres.getMsg());
					System.out.println("===============r======schname=="+schnoticeres.getSchname());
					System.out.println("===============r======update=="+schnoticeres.getSoftupdate());
					System.out.println("===============r======city=="+schnoticeres.getCity());
					System.out.println("===============r==url===="+schnoticeres.getLogourl());

					if (schnoticeres == null) {//null 异常

						handler.sendEmptyMessage(GET_SCH_EORR);

					} else if (!schnoticeres.isSuccess()) {


						if(schnoticeres.getResult()==101&&null!=schnoticeres.getSoftupdate()){
							//更新APP
							handler.sendEmptyMessage(GET_SCH_UPDATE);
						}else{
							//获取数据失败 异常
							handler.sendEmptyMessage(GET_SCH_EORR);
						}
					} else {
						if(AppConstants.isbDebug){
							System.out.println("================获取公告数据成功");
						}

						try {
							String logourl = schnoticeres.getLogourl();
							if (null != logourl && !"".equals(logourl)) {
								downlogo(schnoticeres.getLogourl(),"logo.png");
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						ArrayList<SchNotice> schList = schnoticeres.getSchNotices();
						int size=schList.size();
						if(size>0){

							for(int i=0;i<schList.size();i++){
								SchNotice sch=schList.get(i);
								System.out.println("=================="+sch.getPhoto());
							}
							db.insertOrSchnotice(schList);

							for (int i=0;i<size;i++) {
								SchNotice sch=schList.get(i);
								if (sch.getType() == 1) {

									downSch(sch.getPhoto());

								} else if (sch.getType() == 2) {

									downSch(sch.getVideo());
								}

							}
						}else{
							db.updateSchnotice(true,null);
						}
						//所 有的公告下载完成 ，继续请求学生列表
						handler.sendEmptyMessage(GET_SCH_OVER);
					}
				} catch (Exception e) {
					e.printStackTrace();
					//请求公告出现异常
					handler.sendEmptyMessage(GET_SCH_EORR);

				}
			}
		}.start();
	}



	/**
	 * 下载logo
	 * @param downurl
	 */
	private void downlogo(String downurl,String fileName){

		String filepath = Application.schPath + fileName;

		fileUrl=filepath;
		progress+=10;
		handler.sendEmptyMessage(UPDATE_PB);

		BufferedInputStream bis = null;
		RandomAccessFile raf = null;

		File file = new File(filepath);

		if(!file.exists()){
			try {



				URL url = new URL(downurl);
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
		}


	}


	/**
	 * 下载公告中的图片和视频
	 * @param downurl
	 */
	private void downSch(String downurl){
		String fileName = downurl.substring(downurl.lastIndexOf("/") + 1);

		String filepath = Application.schPath + fileName.trim();

		fileUrl=filepath;
		progress+=10;
		handler.sendEmptyMessage(UPDATE_PB);

		BufferedInputStream bis = null;
		RandomAccessFile raf = null;

		File file = new File(filepath);

		if(!file.exists()){
			try {


				URL url = new URL(downurl);
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
		}


	}


	/**
	 * 跳转到主页面
	 */


	private void startMain(){

		if(!Application.iswhetherStart){
			Application.iswhetherStart=true;
			Intent intent=new Intent(LoadingActivity.this,MainActivity.class);
			startActivity(intent);
			LoadingActivity.this.finish();
		}

	}


	/**
	 * 显示下载dialog
	 *
	 * @author bob
	 */
	private Dialog startDownloadApp(final String downloadUrl, String updateLog) {

		Dialog dialog = showDownLoadDialog(updateLog);
		String sdpath = Environment.getExternalStorageDirectory() + "/";
		String mSavePath = sdpath + "download";


		final String filepath=mSavePath+"/sch.apk";
		new Thread() {

			@Override
			public void run() {

				super.run();
				File saveFile = new File(filepath);
				Downloader downloader = new Downloader(downloadUrl, saveFile, new DownloadListener());
				try {
					appFile = downloader.download();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
		return dialog;
	}

	/**
	 * 下载监听
	 *
	 * @author bob
	 */
	class DownloadListener extends Downloader.ProgressListener {

		@Override
		public void onProgressChanged(int percentage) {

			Message msg = new Message();
			msg.arg1 = percentage;
			msg.what=DOWNLOAD_APP_UPDATE;
			handler.sendMessage(msg);
		}

	}

	/**
	 * 下载Dialog
	 *
	 * @author bob
	 */
	private Dialog showDownLoadDialog(String log) {
		downloadDialog = new Dialog(this);
		downloadDialog.setTitle(R.string.soft_update_tip_updating);
		View view = LayoutInflater.from(this).inflate(R.layout.downloadview, null);
		downloadDialog.setContentView(view);
		TextView txtView = (TextView) view.findViewById(R.id.msg);
		txtView.setText(log.replace("|", "\n"));
		bar = (ProgressBar) view.findViewById(R.id.download_progress);
		proText = (TextView) view.findViewById(R.id.textShow_Progress);
		downloadDialog.show();

		downloadDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {

				LoadingActivity.this.finish();
			}
		});
		return downloadDialog;
	}

	/**
	 * 下载之后安装
	 *
	 * @author bob
	 */
	private void install(String filePath) {
		try {
			Uri uri = Uri.fromFile(new File(filePath));
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

			pending.send(this, 100, intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}


	/**
	 * 异步请求天气预报
	 * @author Administrator
	 *
	 */
	private class QueryAsyncTask extends AsyncTask {
		@Override
		protected void onPostExecute(Object result) {
			try {

				if(result!=null){
					String weatherResult = (String)result;
					if(weatherResult.split(";").length>1){
						String a  = weatherResult.split(";")[1];
						if(a.split("=").length>1){
							String b = a.split("=")[1];
							String c = b.substring(1,b.length()-1);
							String[] resultArr = c.split("\\}");
							if(resultArr.length>0){
								//解析当天天气
								todayParse(resultArr[0]);
							}

						}
					}
				}
				super.onPostExecute(result);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();

			}finally{
				//不管有没有获取到天气都 请求学生列表
				getStuList();
			}

		}

		@Override
		protected Object doInBackground(Object... params) {
			return HttpService.getWeather(params[0].toString());
		}
	}

	/**
	 * 网络获取学生列表
	 */
	private void getStuList() {

		new Thread() {
			@Override
			public void run() {
				super.run();

				try {
					// 向服务器请求学生列表
					stulistres = EhomeBo.getStuList(AppConstants.Mid);
					String xml=stulistres.getStu();
					System.out.println("==============="+xml);
					if (stulistres == null) {
						handler.sendEmptyMessage(GET_STU_EORR);
					} else if (!stulistres.isSuccess()) {

						handler.sendEmptyMessage(GET_STU_EORR);
					} else {
						// 获取学生xml 地址
						String stusUrl = stulistres.getStu();
						if("".equals(stusUrl)){
							handler.sendEmptyMessage(GET_STU_LIST);
						}else{
							try {
								//发送更新进度条指今
								handler.sendEmptyMessage(INIT_PB);
								//联网解析学生列表xml
								getStuXml(stusUrl);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								handler.sendEmptyMessage(GET_STU_EORR);
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(GET_STU_EORR);
				}
			}
		}.start();
	}
	/**
	 * 网络解析 xml 实时解析
	 * @param url
	 * @throws Exception
	 */
	private void getStuXml(String url) throws Exception {



		try {
			ExecutorService exe=Executors.newFixedThreadPool(5);
			HttpEntity entity = HttpUtils
					.getEntity(url, null, HttpUtils.METHOD_GET);
			InputStream in = HttpUtils.getStream(entity);
			Student stu=new Student();
			ArrayList<Card> cids=new ArrayList<Card>();
			Card card=new Card();
			ArrayList<Member> members=new ArrayList<Member>();
			Member member=new Member();

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				String tag = parser.getName();
				switch (type) {
					case XmlPullParser.START_TAG:
						if (tag.equals("stu")) {
							stu=new Student();
							stu.setId(parser.getAttributeValue(0));
						} else if (tag.equals("cids")) {
							cids=new ArrayList<Card>();
						} else if (tag.equals("cid")) {
							card=new Card();
							card.setCid(parser.nextText());
							cids.add(card);
						} else if (tag.equals("members")) {
							members=new ArrayList<Member>();
						} else if (tag.equals("member")) {
							member=new Member();
						} else if (tag.equals("name")) {
							member.setName(parser.nextText());
						} else if (tag.equals("relatship")) {
							member.setRelatship(parser.nextText());
						} else if (tag.equals("clazz")) {
							member.setClazz(parser.nextText());
						} else if (tag.equals("photo")) {
							String photo=parser.nextText();
							if(!"".equals(photo)){
								member.setPhoto(photo);
								//这里加入线程池下载图片

								String fileName =photo.substring(photo.lastIndexOf("/") + 1);

								String filepath = Application.stuPath + fileName.trim();
								File file=new File(filepath);

								if(!file.exists()){
									exe.execute(new StuImageThread(member.getPhoto()));
								}

							}

						}
						break;

					case XmlPullParser.END_TAG:
						if (tag.equals("stu")) {
							//这里将数据插入数据库

							db.insertOrStu(stu.getId(), stu);
						} else if (tag.equals("cids")) {
							stu.setCids(cids);
						} else if (tag.equals("members")) {
							stu.setMembers(members);
						} else if (tag.equals("member")) {
							members.add(member);
						}
						break;
					default:
						break;
				}
				type = parser.next();
			}


			exe.shutdown();
			while(true){
				if(exe.isTerminated()){
					//图片下载完成了
					handler.sendEmptyMessage(GET_STU_LIST);
					break;
				}
				//每隔3秒钟 检查一下 图片线程队列 有没有下载完成
				Thread.sleep(3000);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 下载图片 线程
	 * @author Administrator
	 *
	 */
	class StuImageThread extends Thread {

		String imageurl;


		public StuImageThread(String imageurll) {
			imageurl = imageurll;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String fileName = imageurl.substring(imageurl.lastIndexOf("/") + 1);

			String filepath = Application.stuPath + fileName.trim();

			BufferedInputStream bis = null;
			RandomAccessFile raf = null;

			try {

				URL url = new URL(imageurl);
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


			} catch (Exception e) {
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

				fileUrl=filepath;
				progress++;
				handler.sendEmptyMessage(UPDATE_PB);
			}

		}

	}


	/**
	 *
	 * 解析天气
	 */
	private void todayParse(String weather) throws Exception{
		String temp = weather.replace("'", "");
		String[] tempArr = temp.split(",");
		String wd="";
		String tq="";
		String fx="";
		if(tempArr.length>0){
			for(int i=0;i<tempArr.length;i++){
				if(tempArr[i].indexOf("t1:")!=-1){
					wd=tempArr[i].substring(3,tempArr[i].length())+"℃";
				}else if(tempArr[i].indexOf("t2:")!=-1){
					wd=wd+"~"+tempArr[i].substring(3,tempArr[i].length())+"℃";
				}
			}

			/**
			 * 设置日期(星期几)
			 */
			Application.wendu=wd;
			Application.tanqin=imageResoId(tq);

		}

	}

	/**
	 * 天气图片设置
	 */
	private int imageResoId(String weather) throws Exception{
		int resoid=R.drawable.s_2;
		if(weather.indexOf("多云")!=-1||weather.indexOf("晴")!=-1){//多云转晴，以下类同 indexOf:包含字串
			resoid=R.drawable.s_1;}
		else if(weather.indexOf("多云")!=-1&&weather.indexOf("阴")!=-1){
			resoid=R.drawable.s_2;}
		else if(weather.indexOf("阴")!=-1&&weather.indexOf("雨")!=-1){
			resoid=R.drawable.s_3;}
		else if(weather.indexOf("晴")!=-1&&weather.indexOf("雨")!=-1){
			resoid=R.drawable.s_12;}
		else if(weather.indexOf("晴")!=-1&&weather.indexOf("雾")!=-1){
			resoid=R.drawable.s_12;}
		else if(weather.indexOf("晴")!=-1){resoid=R.drawable.s_13;}
		else if(weather.indexOf("多云")!=-1){resoid=R.drawable.s_2;}
		else if(weather.indexOf("阵雨")!=-1){resoid=R.drawable.s_3;}
		else if(weather.indexOf("小雨")!=-1){resoid=R.drawable.s_3;}
		else if(weather.indexOf("中雨")!=-1){resoid=R.drawable.s_4;}
		else if(weather.indexOf("大雨")!=-1){resoid=R.drawable.s_5;}
		else if(weather.indexOf("暴雨")!=-1){resoid=R.drawable.s_5;}
		else if(weather.indexOf("冰雹")!=-1){resoid=R.drawable.s_6;}
		else if(weather.indexOf("雷阵雨")!=-1){resoid=R.drawable.s_7;}
		else if(weather.indexOf("小雪")!=-1){resoid=R.drawable.s_8;}
		else if(weather.indexOf("中雪")!=-1){resoid=R.drawable.s_9;}
		else if(weather.indexOf("大雪")!=-1){resoid=R.drawable.s_10;}
		else if(weather.indexOf("暴雪")!=-1){resoid=R.drawable.s_10;}
		else if(weather.indexOf("扬沙")!=-1){resoid=R.drawable.s_11;}
		else if(weather.indexOf("沙尘")!=-1){resoid=R.drawable.s_11;}
		else if(weather.indexOf("雾")!=-1){resoid=R.drawable.s_12;}
		return resoid;
	}

}
