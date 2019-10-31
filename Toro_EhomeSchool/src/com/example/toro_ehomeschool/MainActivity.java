package com.example.toro_ehomeschool;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.a.b.c;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.example.toro.bo.EhomeBo;
import com.example.toro.doman.Member;
import com.example.toro.doman.SchNotice;
import com.example.toro.doman.Student;
import com.example.toro.doman.SuaiCar;
import com.example.toro.json.internal.util.AsyncImageLoader;
import com.example.toro.json.internal.util.AsyncImageLoader.Callback;
import com.example.toro.json.internal.util.NetUtils;
import com.example.toro.response.SubmitCardataResponse;
import com.example.toro.response.getStuInformationResponse;
import com.example.toro.sample.Application;
import com.example.toro.sample.CrashHandler;
import com.example.toro.sample.Nfc;
import com.example.toro.sample.SerialPortActivity;
import com.example.toro.sample.db.DbHelper;
import com.example.toroapi.AppConstants;
import com.example.toroapi.AppUtils;
import com.example.toroapi.HttpService;
import com.example.toroapi.SchThread;
import com.example.toroapi.TogetherResponse;
import com.example.toroapi.model.Device;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.linj.FileOperateUtil;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraContainer.TakePictureListener;


import android.location.Location;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 公告轮播界面
 * @author Administrator
 *
 */
@SuppressLint("NewApi")
public class MainActivity  extends Activity implements SurfaceHolder.Callback,View.OnClickListener {

	private ImageView iv_tanqin,iv_logo;//wifi
	private TextView tv_week,tv_wendu;//学生名字，班级，星期（天气），温度 TextView

	private RelativeLayout rl_big;//大图 所属 的控件
	private LinearLayout ll_wifi;
	private TextView tv_sch06_title,tv_sch06_body,tv_sch06_time;//公告小图文字

	private DbHelper db;//数据库操作对象

	private String WCid;//当前刷卡人的cid

	private CameraContainer mContainer;//照相机 控件
	private SubmitCardataResponse submitCardataResponse; //提交刷卡数据 Response
	private String carnum="";//刷卡卡号

	/**
	 * 视频播放
	 */
	private SurfaceView surfaceView;//视频播放 控件
	private AudioManager audioManager;//视频声音设置
	private MediaPlayer player;//视频播放 控件
	private SurfaceHolder surfaceHolder;//视频播放 控件




	/**
	 * 常量
	 */
	public final static int MSG_SUKA = 0;//刷卡

	public final static int SCH_PLAY= 2;//公告轮播



	/**
	 * 公告轮播 线程
	 */
	private SchThread schThread = null;
	private SchThread suaicarThread = null;//监听长时间没刷卡后跳回 公告页面
	private int schId=-1;//公告轮播标识
	private int suaicarTime=0;//刷 卡间隔 单位秒
	private ArrayList<SchNotice> schList=new ArrayList<SchNotice>();//图文公告数据
	private ArrayList<SchNotice> schTxtList=new ArrayList<SchNotice>();//文字公告数据
	private SchView[] schViewList=new SchView[6];//公告控件

	/**
	 * 刷卡数据上传 线程
	 */

	/**
	 * 网络监听广播
	 */
	private IntentFilter filter;
	private BroadcastReceiver networkBroadcast=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(AppConstants.isbDebug){
			System.out.println("==========================MainActivity_onCreate()");
		}
		try {
			setupView();//设置控件

			startService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 设置控件
	 */
	private void setupView(){

		iv_logo=(ImageView)this.findViewById(R.id.iv_logo);
		iv_logo.setOnClickListener(this);
		SchView schview;
		rl_big=(RelativeLayout)this.findViewById(R.id.rl_big);//大图容器
		ImageView iv_big=(ImageView)this.findViewById(R.id.iv_big);//大图控件
		TextView tv_big=(TextView)this.findViewById(R.id.tv_big);//大图文字
		TextView tv_big_time=(TextView)this.findViewById(R.id.tv_big_time);//大图时间控件
		schview=new SchView(iv_big,tv_big,tv_big_time);
		schViewList[0]=schview;

		ImageView iv_sch01=(ImageView)this.findViewById(R.id.iv_sch01);
		TextView tv_sch01=(TextView)this.findViewById(R.id.tv_sch01);
		TextView tv_sch01_time=(TextView)this.findViewById(R.id.tv_sch01_time);
		schview=new SchView(iv_sch01,tv_sch01,tv_sch01_time);
		schViewList[1]=schview;

		ImageView iv_sch02=(ImageView)this.findViewById(R.id.iv_sch02);
		TextView tv_sch02=(TextView)this.findViewById(R.id.tv_sch02);
		TextView tv_sch02_time=(TextView)this.findViewById(R.id.tv_sch02_time);
		schview=new SchView(iv_sch02,tv_sch02,tv_sch02_time);
		schViewList[2]=schview;

		ImageView iv_sch03=(ImageView)this.findViewById(R.id.iv_sch03);
		TextView tv_sch03=(TextView)this.findViewById(R.id.tv_sch03);
		TextView tv_sch03_time=(TextView)this.findViewById(R.id.tv_sch03_time);
		schview=new SchView(iv_sch03,tv_sch03,tv_sch03_time);
		schViewList[3]=schview;

		ImageView iv_sch04=(ImageView)this.findViewById(R.id.iv_sch04);
		TextView tv_sch04=(TextView)this.findViewById(R.id.tv_sch04);
		TextView tv_sch04_time=(TextView)this.findViewById(R.id.tv_sch04_time);
		schview=new SchView(iv_sch04,tv_sch04,tv_sch04_time);
		schViewList[4]=schview;

		ImageView iv_sch05=(ImageView)this.findViewById(R.id.iv_sch05);
		TextView tv_sch05=(TextView)this.findViewById(R.id.tv_sch05);
		TextView tv_sch05_time=(TextView)this.findViewById(R.id.tv_sch05_time);
		schview=new SchView(iv_sch05,tv_sch05,tv_sch05_time);
		schViewList[5]=schview;

		//文本公告TextView控件
		tv_sch06_title=(TextView)this.findViewById(R.id.tv_sch06_title);
		tv_sch06_body=(TextView)this.findViewById(R.id.tv_sch06_body);
		tv_sch06_time=(TextView)this.findViewById(R.id.tv_sch06_time);


		tv_week=(TextView)findViewById(R.id.tv_week); //星期 TextView
		tv_wendu=(TextView)findViewById(R.id.tv_wendu); //温度 TextView

		iv_tanqin=(ImageView)findViewById(R.id.iv_tanqin); //天气 TextView

		ll_wifi=(LinearLayout)findViewById(R.id.ll_wifi);


		//视频播放 控件
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

	}

	/**
	 * 初始化 变量
	 */
	private void init(){


		try {

			//setLogo();

			db = DbHelper.getInstance(this);//初始化数据库对象

			/**
			 * 设置温度和天气
			 */
			tv_week.setText(AppUtils.StringData());
			if(!"".equals(Application.wendu)){
				tv_wendu.setVisibility(View.VISIBLE);
				tv_wendu.setText(Application.wendu);
				iv_tanqin.setVisibility(View.VISIBLE);
				iv_tanqin.setImageResource(Application.tanqin);
			}


			/**
			 * 初始化视频播放控件
			 */

			surfaceHolder=surfaceView.getHolder();//SurfaceHolder是SurfaceView的控制接口
			surfaceHolder.addCallback(this); //因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
			surfaceHolder.setFixedSize(320, 220);//显示的分辨率,不设置为视频默认
			surfaceHolder.setKeepScreenOn(true);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			player=new MediaPlayer();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					//播放结束
					schThread.setSuspend(false);
				}
			});

			/**
			 * 获取公告数据，开始公告轮播
			 */

			schInit();

			//初始化的时候设置有网状态
			wifiConnect(true);

			/**
			 * 启动 刷卡监听 Service 和监听数据处理 后台服务
			 */



			/**
			 * 初始化网络监听广播变量 和刷卡监听广播
			 */
			initReceiver();

		} catch (Exception e) {
			// TODO: handle exception
		}



	}

	private void setLogo(){

		try {

			AsyncImageLoader asyncImageLoader=asyncImageLoader=new AsyncImageLoader(MainActivity.this, new Callback() {
				@Override
				public void imageLoaded(String path, Bitmap bitmap) {
					// 根据路径为tag 查找imageView

					Drawable drawable =new BitmapDrawable(bitmap);
					iv_logo.setBackground(drawable);

				}
			});

			Bitmap bitmap=asyncImageLoader.loadBitmap("logo.png",1);

			if(null==bitmap){
				//schview.getIv().setBackground(null);
			}else{
				Drawable drawable =new BitmapDrawable(bitmap);
				iv_logo.setBackground(drawable);

			};

		} catch (Exception e) {
			// TODO: handle exception
		}



	}
	/**
	 * 启动 Service 后台服务
	 */
	private void startService(){
		//启动刷卡监听 服务
		Intent suicar=new Intent(MainActivity.this,SkdealService.class);
		startService(suicar);

		//启动刷卡数据处理 服务
		Intent suicarData=new Intent(MainActivity.this,SkDataService.class);
		startService(suicarData);
	}


	/**
	 * 控制大图 是显示图片 和是视频f=true 显示 大图，f=false 显示视频
	 */
	public void isVisibility(boolean f){
		if(f){
			rl_big.setVisibility(View.VISIBLE);

		}else{
			rl_big.setVisibility(View.GONE);

		}
	}

	/**
	 * 公告View
	 * @author Administrator
	 *
	 */
	class SchView{
		ImageView iv;
		TextView tv_body;
		TextView tv_time;
		public SchView(ImageView ivs,TextView tv_bodys,TextView tv_times){
			this.iv=ivs;
			this.tv_body=tv_bodys;
			this.tv_time=tv_times;
		}
		public ImageView getIv() {
			return iv;
		}
		public void setIv(ImageView iv) {
			this.iv = iv;
		}
		public TextView getTv_body() {
			return tv_body;
		}
		public void setTv_body(TextView tv_body) {
			this.tv_body = tv_body;
		}
		public TextView getTv_time() {
			return tv_time;
		}
		public void setTv_time(TextView tv_time) {
			this.tv_time = tv_time;
		}
	}



	/**
	 * 初始化广播网络监听广播 和刷卡监听广播
	 */
	private void initReceiver(){
		//初始化网络状态
		AppConstants.netConnect=NetUtils.isNetworkConnected(MainActivity.this);
		filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(Application.SERIAL_PORT);
		networkBroadcast = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();
				//处理刷卡 监听广播 的数据 并 跳转到 刷卡界面
				if(Application.SERIAL_PORT.equals(action)){
					if(schThread!=null){
						schThread.play=false;
					}

					String cid=intent.getStringExtra("cid");
					startScActivity(cid);

				}else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
					boolean isConnected = NetUtils.isNetworkConnected(context);
					if (isConnected) {
						wifiConnect(false);
						AppConstants.netConnect=true;

					} else {
						wifiConnect(true);//没网络显示 网络断开连接图标
						AppConstants.netConnect=false;

					}
				}
			}

		};


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		try {

			init(); //初始化 变量

			if(AppConstants.isbDebug){
				System.out.println("==========================MainActivity_onResume()");
			}

			/**
			 * 发送后台传数据广播
			 */
			new Thread(){

				public void run() {

					try {
						Thread.sleep(3000);

						Intent intent=new Intent();

						intent.setAction(Application.HUOTAN_DATA);

						MainActivity.this.sendBroadcast(intent);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				};

			}.start();

			/**
			 * 注册网络监听广播 和 刷卡监听广播
			 */
			registerReceiver(networkBroadcast, filter);


		} catch (Exception e) {
			// TODO: handle exception
		}


	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		Intent intent=new Intent();

		intent.setAction(Application.HUOTAN_DATA_STOP);

		MainActivity.this.sendBroadcast(intent);
		/**
		 * 注销网络监听广播和 刷卡监听广播
		 */
		unregisterReceiver(networkBroadcast);

		//Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
		if(null!=player&&player.isPlaying()){
			player.stop();
			player.release();
			player=null;
			schThread.setSuspend(false);
		}


	}


	/**
	 * 初始化公告数据，开始轮播
	 */
	private void schInit(){
		try {
			getSchdata();
			if(null==schThread){
				schThread = new SchThread() {

					//逻辑代码体
					@Override
					protected void runPersonelLogic() {
						Message msg = new Message();
						msg.what = SCH_PLAY;
						handler.sendMessage(msg);

						try {
							Thread.sleep(12000);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};

				schThread.setUncaughtExceptionHandler(CrashHandler.getInstance());
				schThread.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * 公告轮播
	 */
	private void SchPlay(ArrayList<SchNotice> schlists){

		try {
			AsyncImageLoader asyncImageLoader;
			for(int i=0;i<schlists.size();i++){
				SchNotice schNotice=schlists.get(i);
				final SchView schview=schViewList[i];
				if(i==0){
					//i==0 为 大图
					if(schNotice.getType()==2){//2 为视频
						//显示 隐藏相关控件
						isVisibility(false);

						String fileName = schNotice.getVideo().substring(schNotice.getVideo().lastIndexOf("/") + 1);

						String filepath = Application.schPath + fileName;
						schThread.setSuspend(true);
						if(!"".equals(filepath)){
							play(filepath);
						}

						continue;//设置完视频后 跳出去（视频没有 文本 和时间 和图片不用设置）
					}else{
						//显示 隐藏相关控件
						isVisibility(true);
					}

				}

				if(schNotice.getType()==2){
					//如果小图 中 要显示的是视频 则显示视频 缩略图 文字 和时间 隐藏

					String fileNames = schNotice.getVideo().substring(schNotice.getVideo().lastIndexOf("/") + 1);
					String filepaths = Application.schPath + fileNames;

					if(!"".equals(filepaths)){
						Bitmap bitmap=null;
						try {
							bitmap=getVideoThumbnail(filepaths,440,285, MediaStore.Images.Thumbnails.MINI_KIND);
						} catch (Exception e) {
							// TODO Auto-generated catch block

							e.printStackTrace();
						}
						Drawable drawable =new BitmapDrawable(bitmap);
						schview.getIv().setBackground(drawable);//设置缩略图
					}
					if("".equals(schNotice.getTitle())){
						schview.getTv_body().setText("宣传片");
					}else{
						schview.getTv_body().setText(schNotice.getTitle());
					}
					if(schNotice.getTime()!=0){
						schview.getTv_time().setText(AppUtils.getYearMonthDayHourMinuteSecond(schNotice.getTime(),1)+"");
					}

				}else{
					schview.getIv().setTag(schNotice.getPhoto());

					if("toro".equals(schNotice.getPhoto())){

						schview.getIv().setBackground(null);

					}else if(!"".equals(schNotice.getPhoto())){

						Bitmap bitmap=showImage(schNotice.getPhoto());

						if(null!=bitmap){
							Drawable drawable =new BitmapDrawable(bitmap);
							schview.getIv().setBackground(drawable);
						}else{
							asyncImageLoader=new AsyncImageLoader(MainActivity.this, new Callback() {
								@Override
								public void imageLoaded(String path, Bitmap bitmap) {
									// 根据路径为tag 查找imageView

									if (bitmap != null&&path.equals(schview.getIv().getTag()+"")) {

										Drawable drawable =new BitmapDrawable(bitmap);
										schview.getIv().setBackground(drawable);

									} else {
										//schview.getIv().setBackground(null);
									}

								}
							});
							bitmap=asyncImageLoader.loadBitmap(schNotice.getPhoto(),1);

							if(null==bitmap){
								//schview.getIv().setBackground(null);
							}else{
								Drawable drawable =new BitmapDrawable(bitmap);

								schview.getIv().setBackground(drawable);

							}
						}



					}


					schview.getTv_body().setText(schNotice.getTitle());
					if(schNotice.getPhoto().equals("toro")){
						schview.getTv_time().setText("");
					}else{
						schview.getTv_time().setText(AppUtils.getYearMonthDayHourMinuteSecond(schNotice.getTime(),1)+"");
					}

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}




	/**
	 * 获取视频的缩略图
	 *
	 */


	private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap  bitmap=null;
		try {

			bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
			// 获取视频的缩略图

			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  ThumbnailUtils.OPTIONS_RECYCLE_INPUT);


		} catch (Exception e) {

			// TODO: handle exception
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图
	 * 此方法有两点好处：
	 *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
	 *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
	 *        用这个工具生成的图像不会被拉伸。
	 * @param imagePath 图像的路径
	 * @param width 指定输出图像的宽度
	 * @param height 指定输出图像的高度
	 * @return 生成的缩略图
	 */
	private Bitmap getImageThumbnail(String imagePath, int width, int height)  {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,   ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}


	/**
	 * 初始化公告模拟数据
	 * @return
	 */
	private ArrayList<SchNotice> getSchTestModel(){
		ArrayList<SchNotice> schLists=new ArrayList<SchNotice>();

		SchNotice  sch=new SchNotice();
		sch.setId("sch1001");
		sch.setTime(1368176599982L);
		sch.setType(0);
		sch.setTitle("关于元旦放假通知");
		sch.setTxt("根据国家关于2016年元旦节假日时间的具体安排，公司对元旦放假时间及注意事项通知如下：　1、2016年元旦放假时间为：2016年1月1日、1月2日、1月3日是，共放假三天。2、元旦放假期间，如需要正常上班的部门，请部门负责人于12月31日之前将值班表报办公室备案。3、元旦放假期间全体员工及领导必须保证手机24小时开机状态,确保联系畅通;4、元旦放假期间，未经批准，非值班人员(除部门负责人外)一律不得进入公司，如有特殊情况须请示部门负责人批准，经同意方可进入公司;5、元旦放假期间回家或外出旅游的员工，应事先知会部门负责人，并协调处理好放假期间的本职工作;6、元旦放假期间监控中心对公司财产的安全负全责,请做好防火、防盗工作。监控中心值班长并负责关好办公区域的门、窗等;7、元旦放假期间在公司值班人员一律禁止使用电脑以外的所有电器及办公设备。8、回家或外出旅游的员工应注意安全。祝全体员工：节日愉快，身体健康!");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch1002");
		sch.setTime(1368176089982L);
		sch.setType(0);
		sch.setTitle("关于元旦放假");
		sch.setTxt("根据国家关于2016年元旦节假日时间的具体安排，公司对元旦放假时间及注意事项通知如下：　1、2016年元旦放假时间为：2016年1月1日、1月2日、1月3日是，共放假三天。2、元旦放假期间，如需要正常上班的部门，请部门负责人于12月31日之前将值班表报办公室备案。3、元旦放假期间全体员工及领导必须保证手机24小时开机状态,确保联系畅通;4、元旦放假期间，未经批准，非值班人员(除部门负责人外)一律不得进入公司，如有特殊情况须请示部门负责人批准，经同意方可进入公司;5、元旦放假期间回家或外出旅游的员工，应事先知会部门负责人，并协调处理好放假期间的本职工作;6、元旦放假期间监控中心对公司财产的安全负全责,请做好防火、防盗工作。监控中心值班长并负责关好办公区域的门、窗等;7、元旦放假期间在公司值班人员一律禁止使用电脑以外的所有电器及办公设备。8、回家或外出旅游的员工应注意安全。祝全体员工：节日愉快，身体健康!");
		schLists.add(sch);


		sch=new SchNotice();
		sch.setId("sch1003");
		sch.setTime(1368176599989L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img01.png");
		sch.setTxt("本学期画画比赛获奖小朋友！");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch1004");
		sch.setTime(1368176599988L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img02.png");
		sch.setTxt("~恭喜 大二班三位小朋友获得 “好孩子” 奖状！~");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch1005");
		sch.setTime(1368176599987L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img03.png");
		sch.setTxt("~元旦晚会精彩表演~");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch1006");
		sch.setTime(1368176599986L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img04.png");
		sch.setTxt("~欢乐课间操~");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch10030");
		sch.setTime(1368176599985L);
		sch.setType(2);
		sch.setTitle("");
		sch.setTxt("");
		sch.setVideo("http://e5ex.com/video1.mp4");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch1007");
		sch.setTime(1368176599984L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img05.png");
		sch.setTxt("~小朋友在用午餐~");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch1008");
		sch.setTime(1368176599983L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img06.png");
		sch.setTxt("~晚会前准备~");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("sch1009");
		sch.setTime(1368176599982L);
		sch.setType(1);
		sch.setTitle("");
		sch.setPhoto("http://e5ex.com/img07.png");
		sch.setTxt("~欢乐课间游戏~");
		schLists.add(sch);


		return schLists;
	}


	private ArrayList<SchNotice> addSch(ArrayList<SchNotice> schs){
		ArrayList<SchNotice> schLists=new ArrayList<SchNotice>();

		SchNotice  sch=new SchNotice();

		sch.setId("toro02");
		sch.setTime(0L);
		sch.setType(1);
		sch.setTitle("公告展示位1");
		sch.setPhoto("toro");
		sch.setTxt("公告展示位1");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("toro03");
		sch.setTime(0L);
		sch.setType(1);
		sch.setTitle("公告展示位2");
		sch.setPhoto("toro");
		sch.setTxt("公告展示位2");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("toro04");
		sch.setTime(0L);
		sch.setType(1);
		sch.setTitle("公告展示位3");
		sch.setPhoto("toro");
		sch.setTxt("公告展示位3");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("toro05");
		sch.setTime(0L);
		sch.setType(1);
		sch.setTitle("公告展示位4");
		sch.setPhoto("toro");
		sch.setTxt("公告展示位4");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("toro06");
		sch.setTime(0L);
		sch.setType(1);
		sch.setTitle("公告展示位5");
		sch.setPhoto("toro");
		sch.setTxt("公告展示位5");
		schLists.add(sch);

		sch=new SchNotice();
		sch.setId("toro07");
		sch.setTime(0L);
		sch.setType(1);
		sch.setTitle("公告展示位6");
		sch.setPhoto("toro");
		sch.setTxt("公告展示位6");
		schLists.add(sch);
		int size=6-schs.size();
		for(int i=0;i<size;i++){
			schs.add(schLists.get(i));
		}


		return schLists;
	}
	/**
	 * 1、获取公告数据 2、拆分公告数据
	 */
	private void getSchdata(){
		ArrayList<SchNotice> schLists=new ArrayList<SchNotice>();

		if(AppConstants.isTest){//是否是测试模式
			schLists=getSchTestModel(); //获取 公告模拟数据

		}else{
			ArrayList<SchNotice> photolist=db.querySchnoticevideoandphoto();

			for(SchNotice sch:photolist){
				schLists.add(sch);
			}

			ArrayList<SchNotice> txtlist=db.querySchnotice("0","1");

			for(SchNotice sch:txtlist){
				schLists.add(sch);
			}


			for(SchNotice sch:schLists){
				System.out.println("==============select"+sch.getPhoto()+"====type"+sch.getType());
			}

			System.out.println("=================dbsize"+schLists.size());
			//如果里面没有6个公告则增加默认的公告
			if(schLists.size()<6){
				addSch(schLists);
			}

			for(int i=0;i<schLists.size();i++){

				System.out.println("==============title"+schLists.get(i).getTitle());
				System.out.println("==============txt"+schLists.get(i).getTxt());

			}
		}


		for(SchNotice sch:schLists){
			if(sch.getType()==0){
				schTxtList.add(sch);
			}else if(sch.getType()==1){
				schList.add(sch);
			}
		}

		for(SchNotice sch:schLists){
			if(sch.getType()==2){
				schList.add(sch);
			}
		}

		if(schTxtList.size()==0){
			SchNotice  sch=new SchNotice();
			sch.setId("toro01");
			sch.setTime(1368176599982L);
			sch.setType(0);
			sch.setTitle("文字公告展示位");
			sch.setTxt("");
			schTxtList.add(sch);
		}

		//将文本公告加入Application 刷卡页面要用
		Application.schTxtList=schTxtList;


		//按时间对公告进行排序（发布时间最近的按前面）
		//Collections.sort(schList, new SortByTime());
		Collections.sort(schTxtList, new SortByTime());

		if(schTxtList.size()>0){
			setTextSch(schTxtList.get(0));
		}
	}

	/**
	 * 设置纯文字公告
	 */
	private void setTextSch(SchNotice txtSch){

		if(null!=txtSch){
			tv_sch06_title.setText(txtSch.getTitle());
			tv_sch06_body.setText("　　"+txtSch.getTxt());
			if(txtSch.getTxt().equals("")){
				tv_sch06_time.setText("");//要转换成时间
			}else{
				tv_sch06_time.setText(AppUtils.getYearMonthDayHourMinuteSecond(txtSch.getTime(),1)+"");//要转换成时间
			}

		}
	}

	/**
	 * 公告按时间排序
	 */

	class SortByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			SchNotice s1 = (SchNotice) o1;
			SchNotice s2 = (SchNotice) o2;
			if (s1.getTime() > s2.getTime()){
				return -1;
			}else if(s1.getTime()<s2.getTime()){
				return 1;
			}
			return 0;
		}
	}



	/**
	 * 设置语音播放监听接口
	 */
	private SynthesizerListener mSynListener=new SynthesizerListener(){

		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompleted(SpeechError arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSpeakBegin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSpeakPaused() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSpeakResumed() {
			// TODO Auto-generated method stub

		}


	};




	/**
	 *  grivView 适配器
	 *
	 */
	class QinsuAdapter extends BaseAdapter{

		LayoutInflater inflater;
		ArrayList<String> qinsuList;


		AsyncImageLoader loader;
		Context contexts;
		GridView gridView;

		public QinsuAdapter(Context context,ArrayList<String> list,GridView gridview){
			if(null!=list){
				qinsuList=list;
			}else{
				qinsuList=new ArrayList<String>();
			}

			this.contexts=context;
			this.gridView=gridview;
			this.inflater= LayoutInflater.from(context);

			this.loader = new AsyncImageLoader(context, new Callback() {
				@Override
				public void imageLoaded(String path, Bitmap bitmap) {
					// 根据路径为tag 查找imageView
					ImageView iv = (ImageView) gridView.findViewWithTag(path);
					// 显示图片
					if (iv != null) {
						if (bitmap != null) {
							Drawable drawable =new BitmapDrawable(bitmap);
							iv.setBackground(drawable);
						} else {
							//iv.setBackground(null);
						}
					}
				}
			});
		}

		@Override
		public int getCount() {
			return qinsuList.size();
		}

		@Override
		public Object getItem(int position) {
			return qinsuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * 更新listview中显示的数据集
		 *
		 * @param musics
		 */
		public void changeData(ArrayList<String> list) {
			this.setQinsuList(list);// 变更数据集
			this.notifyDataSetChanged();// 刷新listview
			//Log.i("info", "Updatemenu.count=" + Updatemenu.size());
		}

		public void setQinsuList(ArrayList<String> qinsuList) {
			if(null!=qinsuList){
				this.qinsuList = qinsuList;
			}else{
				this.qinsuList=new ArrayList<String>();
			}
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;
			if(convertView==null){
				convertView=inflater.inflate(R.layout.relatives_item,null);
				viewHolder=new ViewHolder();
				viewHolder.iv_item=(ImageView)convertView.findViewById(R.id.iv_item);

				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder)convertView.getTag();
			}

			String PhotoUrl=qinsuList.get(position);

			if(!"".equals(PhotoUrl)){
				viewHolder.iv_item.setTag(PhotoUrl);

				// 加载图片
				Bitmap bm = loader.loadBitmap(PhotoUrl,0);

				if (bm != null) {
					Drawable drawable =new BitmapDrawable(bm);
					viewHolder.iv_item.setBackground(drawable);
				} else {
					//  viewHolder.iv_item.setBackground(null);
				}
			}


			return convertView;
		}

		class ViewHolder{
			ImageView iv_item;
		}


	}



	private void startScActivity(String cid){

		Intent intent=new Intent(MainActivity.this,SuaicarActivity.class);
		intent.putExtra("cid", cid);
		startActivity(intent);
		this.finish();
	}


	/**
	 * 全局 Handler 处理
	 */
	private Handler handler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			try {
				switch (msg.what) {
					case MSG_SUKA:
						String carnum=msg.obj.toString();
						if(null!=carnum&&!"".equals(carnum)){
							//readyCamera(carnum);	//拍照
							//requestStu(carnum);//请求学生数据
						}

						break;



					case SCH_PLAY:
						if(schId>=schList.size()-1){
							schId=0;
						}else{
							schId++;
						}
						ArrayList<SchNotice> sch=null;
						int size=schList.size()-6;
						if(size==0){
							sch=new ArrayList<SchNotice>(schList.subList(schId, schList.size()));
							for(int i=0;i<schId;i++){
								sch.add(schList.get(i));
							}
						}else if(size>0){

							int sizes= schList.size()-schId;
							if(sizes>=6){
								sch=new ArrayList<SchNotice>(schList.subList(schId, schId+6));
							}else{
								sch=new ArrayList<SchNotice>(schList.subList(schId, schList.size()));

								for(int i=0;i<6-sizes;i++){
									sch.add(schList.get(i));
								}
							}

						}

						SchPlay(sch);
						break;


					default:
						break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	};





	/**
	 * 是否有网络
	 * f:true =有网络 false 无网络
	 */

	private void wifiConnect(boolean f){
		try {
			if(f){
				ll_wifi.setVisibility(View.VISIBLE);
			}else{
				ll_wifi.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.iv_logo:

				Intent intent=new Intent(MainActivity.this,SettingActivity.class);

				startActivity(intent);
				this.finish();
				break;

			default:
				break;
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 播放控件 监听 接口
	 */
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

		//设置显示视频显示在SurfaceView上
	         /*   try {
	                player.setDataSource("/sdcard/Music/video1.mp4");//播放视频地址
	                player.prepare();
	                player.start();
	                play("");
	            } catch (Exception e) {
	                e.printStackTrace();
	            }

	           */
	}
	/**
	 * 设置mediaPlayer播放视频
	 * @param position
	 */
	private void play(String path)
	{
		try
		{

			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			if(null!=player){
				player.reset();
				player.setDataSource(fis.getFD());//设置播放源
				player.setDisplay(surfaceHolder);
				player.prepare();//缓冲
				player.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {

						player.start();


					}
				});
			}
		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
	}



	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stu

		super.onDestroy();

		if(AppConstants.isbDebug){
			System.out.println("====================MainActivity onDestroy()");
		}

		/**
		 * 当主界面停止时 发广播重启
		 */
		/**Intent intent = new Intent("android.intent.action.RSTART");
		 sendBroadcast(intent);  */

	}


	/**
	 * 图片水平翻转
	 * @param bmp
	 * @return
	 */
	public Bitmap convertBmp(Bitmap bmp) {
		int w = bmp.getWidth();
		int h = bmp.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale(-1, 1); // 镜像水平翻转
		Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

		return convertBmp;
	}


	private Bitmap showImage(String path)   {

		String fileName = path.substring(path.lastIndexOf("/") + 1);

		String filepath= Application.schPath + fileName;

		Bitmap bm=null;
		BitmapFactory.Options bfOptions=new BitmapFactory.Options();
		bfOptions.inDither=false;                     //Disable Dithering mode
		bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
		bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
		bfOptions.inTempStorage=new byte[32 * 1024];


		File file=new File(filepath);
		FileInputStream fs=null;
		try {
			fs = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			//TODO do something intelligent
			e.printStackTrace();
		}



		try {
			if(fs!=null) bm=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
		} catch (IOException e) {
			//TODO do something intelligent
			e.printStackTrace();
		} finally{
			if(fs!=null) {
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//bm=BitmapFactory.decodeFile(path, bfOptions); This one causes error: java.lang.OutOfMemoryError: bitmap size exceeds VM budget

		// im.setImageBitmap(bm);
		//bm.recycle();
		// bm=null;

		return bm;

	}




}