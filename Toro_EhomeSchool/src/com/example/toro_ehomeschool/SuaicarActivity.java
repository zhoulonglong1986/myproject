package com.example.toro_ehomeschool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.toro.bo.EhomeBo;
import com.example.toro.doman.Member;
import com.example.toro.doman.SchNotice;
import com.example.toro.doman.Student;
import com.example.toro.doman.SuaiCar;
import com.example.toro.json.internal.util.AsyncImageLoader;
import com.example.toro.json.internal.util.NetUtils;
import com.example.toro.json.internal.util.AsyncImageLoader.Callback;
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
import com.example.toroapi.PreferencesService;
import com.example.toroapi.SchThread;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraContainer.TakePictureListener;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SuaicarActivity extends Activity implements View.OnClickListener,
		TakePictureListener {

	private ImageView iv_camera, iv_bg, iv_tanqin,iv_logo;// 相片 ，学生大图,天气
	// ImageView
	private TextView tv_name, tv_ban, tv_week, tv_wendu;// 学生名字，班级，星期（天气），温度
	// TextView

	private LinearLayout ll_wifi;
	private WatercAdapter qinsuAdapters;
	private DbHelper db;// 数据库操作对象


	private HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();// 用于模拟测试

	private getStuInformationResponse stuInformationres = new getStuInformationResponse();// 请求学生数据
	private String WCid;// 当前刷卡人的cid

	private CameraContainer mContainer;// 照相机 控件
	private SubmitCardataResponse submitCardataResponse; // 提交刷卡数据 Response
	private String carnum = "";// 刷卡卡号
	private Long sucartime = 0L;
	private SchView[] schViews = new SchView[5];
	private int interval=0;
	private TextView tv_sch06_title,tv_sch06_body,tv_sch06_time;//公告小图文字

	private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

	/**
	 * 语音播报
	 */
	private SpeechSynthesizer mTts;// 讯飞语音播报 接口

	/**
	 * 常量
	 */
	public final static int MSG_SUKA = 0;// 刷卡
	public final static int SCH_Time = 3;// 长时间没刷卡监听
	public final static int GET_STU = 4;// 获取学生数据
	public final static int GET_STU_OFFNET = 6;// 获取学生数据 断网状态
	public final static int GET_STU_SQL = 5;// 获取学生数据

	private SchThread suaicarThread = null;// 监听长时间没刷卡后跳回 公告页面

	private int suaicarTime = 0;// 刷 卡间隔 单位秒


	/**
	 * 刷卡数据上传 线程池
	 */
	private ExecutorService exe = Executors.newFixedThreadPool(5);

	/**
	 * 网络监听广播
	 */

	private BroadcastReceiver networkBroadcast = null;
	private IntentFilter filter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_suaicar);

			setupView();// 设置控件
			init(); // 初始化 变量

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 设置控件
	 */
	private void setupView() {

		iv_logo=(ImageView)this.findViewById(R.id.iv_logo);
		iv_logo.setOnClickListener(this);
		iv_bg = (ImageView) findViewById(R.id.iv_bg); // 学生ImageView
		tv_name = (TextView) findViewById(R.id.tv_name); // 名字TextView
		tv_ban = (TextView) findViewById(R.id.tv_ban); // 班级 TextView
		tv_week = (TextView) findViewById(R.id.tv_week); // 星期 TextView
		tv_wendu = (TextView) findViewById(R.id.tv_wendu); // 温度 TextView
		iv_tanqin = (ImageView) findViewById(R.id.iv_tanqin); // 天气 TextView
		ll_wifi = (LinearLayout) findViewById(R.id.ll_wifi);

		// 照相机 控件
		mContainer = (CameraContainer) findViewById(R.id.container);
		iv_camera=(ImageView)this.findViewById(R.id.iv_camera);//相片 ImageView

		/**
		 * 文本公告
		 */

		//文本公告TextView控件
		tv_sch06_title=(TextView)this.findViewById(R.id.tv_sch06_title);
		tv_sch06_body=(TextView)this.findViewById(R.id.tv_sch06_body);
		tv_sch06_time=(TextView)this.findViewById(R.id.tv_sch06_time);

		if(Application.schTxtList.size()>0){
			setTextSch(Application.schTxtList.get(0));
		}



	}

	/**
	 * 初始化 变量
	 */
	private void init() {

		//setLogo();

		// 获取传过来的 cid


		try {

			carnum = getIntent().getStringExtra("cid");
			db = DbHelper.getInstance(this);// 初始化数据库对象


			interval=Application.interval;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


		/**
		 * 设置温度和天气
		 */
		tv_week.setText(AppUtils.StringData());
		if (!"".equals(Application.wendu)) {
			tv_wendu.setVisibility(View.VISIBLE);
			tv_wendu.setText(Application.wendu);
			iv_tanqin.setVisibility(View.VISIBLE);
			iv_tanqin.setImageResource(Application.tanqin);
		}

		/**
		 * 设置 讯飞语音 播放
		 */
		SpeechUtility.createUtility(SuaicarActivity.this, "appid=56aad6cb");
		mTts = SpeechSynthesizer.createSynthesizer(this, null);
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		mTts.setParameter(SpeechConstant.SPEED, "50");
		mTts.setParameter(SpeechConstant.VOLUME, "80");
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);

		if (AppConstants.isTest) {
			// textModel();
		}

		/**
		 * 获取公告数据，开始公告轮播
		 */

		wifiConnect(true);



		/**
		 * 注册网络监听广播 和刷卡广播
		 */

		GridView lv_qinsus = (GridView) this.findViewById(R.id.lv_qinsus);// 流水
		// gridViewy
		qinsuAdapters = new WatercAdapter(this, bitmapList, lv_qinsus);
		lv_qinsus.setAdapter(qinsuAdapters);

		/**
		 * 注册网络监听广播 和刷卡广播
		 */
		regNetReceiver();

	}

	private void setLogo(){

		AsyncImageLoader asyncImageLoader=asyncImageLoader=new AsyncImageLoader(SuaicarActivity.this, new Callback() {
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

	}

	/**
	 * 注册网络监听广播 和刷卡广播
	 */
	private void regNetReceiver() {
		AppConstants.netConnect = NetUtils.isNetworkConnected(SuaicarActivity.this);
		filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(Application.SERIAL_PORT);
		networkBroadcast = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();

				if (Application.SERIAL_PORT.equals(action)) {
					String cid = intent.getStringExtra("cid");
					carnum = cid;
					readyCamera(cid); // 拍照
				} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
					boolean isConnected = NetUtils.isNetworkConnected(context);
					if (isConnected) {
						wifiConnect(false);
						AppConstants.netConnect = true;
						// 有网络后，自动上传没有传完的数据。
						// queryScdata();
					} else {
						wifiConnect(true);// 没网络显示 网络断开连接图标
						AppConstants.netConnect = false;
					}
				}
			}

		};

		registerReceiver(networkBroadcast, filter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (AppConstants.isbDebug) {
			System.out.println("====================SuaicarActivity onResume()");
		}

		// 如果 是由公告页面进入刷卡界面( 延时2秒 拍照)
		new Thread() {
			public void run() {

				try {
					Thread.sleep(2000);// 延时2秒 拍照

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				readyCamera(carnum);
			};
		}.start();

		registerReceiver(networkBroadcast, filter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		try {
			unregisterReceiver(networkBroadcast);

			if (null != bitmapList) {
				for (Bitmap map : bitmapList) {
					if (!map.isRecycled()) {
						map.isRecycled();

					}
				}
			}
			bitmapList = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 设置纯文字公告
	 */
	private void setTextSch(SchNotice txtSch){

		if(null!=txtSch){
			tv_sch06_title.setText(txtSch.getTitle());
			tv_sch06_body.setText("　　"+txtSch.getTxt());
			tv_sch06_time.setText(AppUtils.getYearMonthDayHourMinuteSecond(txtSch.getTime(),1)+"");//要转换成时间
		}
	}

	/**
	 * 获取视频的缩略图
	 *
	 */

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	private Bitmap getVideoThumbnail(String videoPath, int width, int height,
									 int kind) {
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// 获取视频的缩略图

		System.out.println("w" + bitmap.getWidth());
		System.out.println("h" + bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 *
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	private Bitmap getImageThumbnail(String imagePath, int width, int height) {
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
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 测试模式数据
	 */
	private void textModel() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("http://e5ex.com/man02.jpg");
		list.add("http://e5ex.com/man03.jpg");
		list.add("http://e5ex.com/man04.jpg");
		list.add("http://e5ex.com/man05.jpg");

		map.put("0000005618b4171d", list);

		ArrayList<String> ists = new ArrayList<String>();
		ists.add("http://e5ex.com/man11.jpg");
		ists.add("http://e5ex.com/man12.jpg");
		ists.add("http://e5ex.com/man13.jpg");
		ists.add("http://e5ex.com/man14.jpg");

		map.put("00000000fd3e9dc1", ists);

	}

	/**
	 * 设置语音播放监听接口
	 */
	private SynthesizerListener mSynListener = new SynthesizerListener() {

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



	private Bitmap showImage(String path) {
		Bitmap bm = null;

		try {
			String fileName = path.substring(path.lastIndexOf("/") + 1);

			String filepath = Application.stuPath + fileName;

			BitmapFactory.Options bfOptions = new BitmapFactory.Options();
			bfOptions.inDither = false; // Disable Dithering mode
			bfOptions.inPurgeable = true; // Tell to gc that whether it needs
			// free memory, the Bitmap can be
			// cleared
			bfOptions.inInputShareable = true; // Which kind of reference will
			// be used to recover the Bitmap
			// data after being clear, when
			// it will be used in the future
			bfOptions.inTempStorage = new byte[32 * 1024];

			File file = new File(filepath);
			FileInputStream fs = null;
			try {
				fs = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO do something intelligent
				e.printStackTrace();
			}

			try {
				if (fs != null)
					bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
							bfOptions);
			} catch (IOException e) {
				// TODO do something intelligent
				e.printStackTrace();
			} finally {
				if (fs != null) {
					try {
						fs.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// bm=BitmapFactory.decodeFile(path, bfOptions); This one causes
			// error: java.lang.OutOfMemoryError: bitmap size exceeds VM budget

			// im.setImageBitmap(bm);
			// bm.recycle();
			// bm=null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return bm;

	}

	/**
	 * grivView 适配器
	 *
	 */
	class QinsuAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<String> qinsuList;

		AsyncImageLoader loader;
		Context contexts;
		GridView gridView;

		public QinsuAdapter(Context context, ArrayList<String> list,
							GridView gridview) {
			if (null != list) {
				qinsuList = list;
			} else {
				qinsuList = new ArrayList<String>();
			}

			this.contexts = context;
			this.gridView = gridview;
			this.inflater = LayoutInflater.from(context);

			this.loader = new AsyncImageLoader(context, new Callback() {
				@Override
				public void imageLoaded(String path, Bitmap bitmap) {
					// 根据路径为tag 查找imageView
					ImageView iv = (ImageView) gridView.findViewWithTag(path);
					// 显示图片
					if (iv != null) {
						if (bitmap != null) {
							Drawable drawable = new BitmapDrawable(bitmap);
							iv.setBackground(drawable);
						} else {
							// iv.setBackground(null);
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
			// Log.i("info", "Updatemenu.count=" + Updatemenu.size());
		}

		public void setQinsuList(ArrayList<String> qinsuList) {
			if (null != qinsuList) {
				this.qinsuList = qinsuList;
			} else {
				this.qinsuList = new ArrayList<String>();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;
			try {
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.relatives_item,
							null);
					viewHolder = new ViewHolder();
					viewHolder.iv_item = (ImageView) convertView
							.findViewById(R.id.iv_item);

					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}

				String PhotoUrl = qinsuList.get(position);
				if ("1".equals(PhotoUrl)) {

				} else if (!"".equals(PhotoUrl)) {
					viewHolder.iv_item.setTag(PhotoUrl);

					Bitmap bm = showImage(PhotoUrl);
					if (null != bm) {
						Drawable drawable = new BitmapDrawable(bm);
						viewHolder.iv_item.setBackground(drawable);
					} else {
						bm = loader.loadBitmap(PhotoUrl, 2);
						if (bm != null) {
							Drawable drawable = new BitmapDrawable(bm);
							viewHolder.iv_item.setBackground(drawable);

						} else {
							// viewHolder.iv_item.setBackground(null);
						}
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return convertView;
		}

		class ViewHolder {
			ImageView iv_item;
		}

	}

	/**
	 * grivView 适配器
	 *
	 */
	class WatercAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<Bitmap> qinsuList;

		AsyncImageLoader loader;
		Context contexts;
		GridView gridView;

		public WatercAdapter(Context context, ArrayList<Bitmap> list,
							 GridView gridview) {
			try {

				if (null != list) {
					qinsuList = list;
				} else {
					qinsuList = new ArrayList<Bitmap>();
				}

				this.contexts = context;
				this.gridView = gridview;
				this.inflater = LayoutInflater.from(context);

				this.loader = new AsyncImageLoader(context, new Callback() {
					@Override
					public void imageLoaded(String path, Bitmap bitmap) {
						// 根据路径为tag 查找imageView
						ImageView iv = (ImageView) gridView
								.findViewWithTag(path);
						// 显示图片
						if (iv != null) {
							if (bitmap != null) {
								Drawable drawable = new BitmapDrawable(bitmap);
								iv.setBackground(drawable);
							} else {
								// iv.setBackground(null);
							}
						}
					}
				});

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
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
		public void changeData(ArrayList<Bitmap> list) {
			this.setQinsuList(list);// 变更数据集
			this.notifyDataSetChanged();// 刷新listview
			// Log.i("info", "Updatemenu.count=" + Updatemenu.size());
		}

		public void setQinsuList(ArrayList<Bitmap> qinsuList) {
			if (null != qinsuList) {
				this.qinsuList = qinsuList;
			} else {
				this.qinsuList = new ArrayList<Bitmap>();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			try {
				if (AppConstants.isbDebug) {
					System.out
							.println("==============================getView()");
				}

				ViewHolder viewHolder;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.watercourse_item,
							null);
					viewHolder = new ViewHolder();
					viewHolder.iv_item = (ImageView) convertView
							.findViewById(R.id.iv_item);

					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}

				Bitmap bm = qinsuList.get(qinsuList.size() - (position + 1));

				Drawable drawable = new BitmapDrawable(bm);
				viewHolder.iv_item.setBackground(drawable);

				/*
				 * if(!"".equals(PhotoUrl)){
				 * viewHolder.iv_item.setTag(PhotoUrl);
				 *
				 * // 加载图片 Bitmap bm = loader.loadBitmap(PhotoUrl,0);
				 *
				 * if (bm != null) { Drawable drawable =new BitmapDrawable(bm);
				 * viewHolder.iv_item.setBackground(drawable); } else { //
				 * viewHolder.iv_item.setBackground(null); } }
				 */

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			return convertView;
		}

		class ViewHolder {
			ImageView iv_item;
		}

	}

	/**
	 * 刷 卡监听
	 */

	/**
	 * 全局 Handler 处理
	 */
	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			try {

				switch (msg.what) {
					case MSG_SUKA:
						String carnum = msg.obj.toString();
						if (null != carnum && !"".equals(carnum)) {
							readyCamera(carnum); // 拍照
							// requestStu(carnum);//请求学生数据
						}

						break;

					case GET_STU:

						String cid = msg.obj.toString();
						if (WCid.equals(cid) && null != stuInformationres	&& stuInformationres.isSuccess()) {


							if (null != stuInformationres.getRid()&& !"".equals(stuInformationres.getRid())) {
								Student stu = new Student();
								stu.setId(stuInformationres.getRid());
								stu.setCids(stuInformationres.getCids());
								stu.setMembers(stuInformationres.getMembers());

								ArrayList<Member> memebers = stu.getMembers();

								Member me = new Member();
								ArrayList<String> list = new ArrayList<String>() {};
								for (Member member : memebers) {
									if ("".equals(member.getRelatship())||"0".equals(member.getRelatship())) {
										me = member;
										refreshView(member);
									} else {
										if (!"".equals(member.getPhoto())) {
											list.add(member.getPhoto());
										}
									}
								}
								//refreshView(me);


								db.insertOrStu(stu.getId(), stu);// 将获取的学生数据 再插入数据库
							} else {

								Member me = new Member();
								me.setPhoto("1");
								refreshView(me);
								ArrayList<String> list = new ArrayList<String>() {
								};
								list.add("1");
								list.add("1");
								list.add("1");
								list.add("1");

								mTts.startSpeaking("无效卡", mSynListener);// 语音播报
							}

						}else{

							Member me = new Member();
							me.setPhoto("1");
							refreshView(me);
							ArrayList<String> list = new ArrayList<String>() {
							};
							list.add("1");
							list.add("1");
							list.add("1");
							list.add("1");

							mTts.startSpeaking("无效卡", mSynListener);// 语音播报
						}

						break;

					case GET_STU_SQL:

						String cids = msg.obj.toString();

						if (WCid.equals(cids)) {
							if (AppConstants.isbDebug) {
								System.out.println("==================GET_STU_SQL更新学生数据时间"+ System.currentTimeMillis());
							}
							refreshView(mes);

							if (AppConstants.isbDebug) {
								System.out.println("==================GET_STU_SQL更新view时间"+ System.currentTimeMillis());
							}

						}

						break;

					case SCH_Time:

						// suaicarThread.play=false;
						Intent intent = new Intent(SuaicarActivity.this,
								MainActivity.class);

						startActivity(intent);
						SuaicarActivity.this.finish();
						break;

					default:
						break;
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		};
	};

	/**
	 * 拍照
	 */
	private void readyCamera(final String cid) {

		try {

			suaicarTime = 0;

			if (null == suaicarThread) {
				suaicarThread = new SchThread() {

					// 逻辑代码体
					@Override
					protected void runPersonelLogic() {

						try {
							if (suaicarTime > interval) {

								this.play = false;
								Message msg = new Message();
								msg.what = SCH_Time;
								handler.sendMessage(msg);

								return;
							} else {
								Thread.sleep(2000);
								suaicarTime++;
							}

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};

				suaicarThread.setUncaughtExceptionHandler(CrashHandler
						.getInstance());
				suaicarThread.start();
			}

			sucartime = System.currentTimeMillis();

			mContainer.takePicture(SuaicarActivity.this, cid, sucartime);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private Member mes = new Member();
	private ArrayList<String> listsss = new ArrayList<String>();

	/**
	 * 请求学生数据 线程
	 *
	 * @param cid
	 */
	private void queryStu(final String cid) {
		new Thread() {
			@Override
			public void run() {
				super.run();

				try {

					WCid = cid;

					// 是否是模拟测试
					if (AppConstants.isTest) {
						Message msg = new Message();
						msg.obj = cid;
						msg.what = GET_STU_SQL;
						if (AppConstants.isbDebug) {
							System.out.println("==================GET_STU_SQL获取学生数据时间"+ System.currentTimeMillis());
						}

						if (cid.equals("00803c167265f704")) {
							listsss = map.get("00000000fd3e9dc1");
							mes.setPhoto("http://e5ex.com/left01.jpg");
							mes.setName("刘小宝");
							mes.setRelatship("幼儿大一班");
						} else {
							listsss = map.get("0000005618b4171d");
							mes.setPhoto("http://e5ex.com/left02.jpg");
							mes.setName("周小宝");
							mes.setRelatship("幼儿大二班");
						}
						if (AppConstants.isbDebug) {
							System.out
									.println("==================GET_STU_SQL获取到学生数据时间"
											+ System.currentTimeMillis());
						}

						handler.sendMessage(msg);

					} else {
						// 根据刷卡的卡号从 存储中 提取数据 然后刷新界面

						Message msgss = new Message();
						msgss.obj = cid;
						msgss.what = GET_STU_SQL;
						Student stu = db.queryStus(cid);
						listsss = new ArrayList<String>();
						if (!"".equals(stu.getId())) {
							ArrayList<Member> memebers = stu.getMembers();

							for (Member member : memebers) {
								if ("0".equals(member.getRelatship())
										|| "".equals(member.getRelatship())) {
									mes = member;

								} else {
									if (!"".equals(member.getPhoto())) {
										listsss.add(member.getPhoto());
									}
								}
							}

							handler.sendMessage(msgss);

						} else {// 如果 数据库中没有，则请求网络
							// 如果 有网络则请求学生数据//没网络不处理
							Message msgs = new Message();
							msgs.obj = cid;
							msgs.what = GET_STU;
							if (AppConstants.netConnect) {
								// 向服务器请求学生列表

								stuInformationres = EhomeBo.getStuInformation(AppConstants.Mid, cid, 0 + "");
								handler.sendMessage(msgs);

							}else{

								stuInformationres =null;
								handler.sendMessage(msgs);

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
	 * 刷新学生个人信息
	 */
	private void refreshView(Member member) {

		try {

			if (null != member) {

				if ("1".equals(member.getPhoto())) {

					Drawable drawable = getResources().getDrawable(
							R.drawable.no);
					iv_bg.setBackground(drawable);

				} else if (!"".equals(member.getPhoto())) {

					iv_bg.setTag(member.getPhoto());

					Bitmap bitmap = showImage(member.getPhoto());

					if (null != bitmap) {
						Drawable drawable = new BitmapDrawable(bitmap);
						iv_bg.setBackground(drawable);
					} else {
						AsyncImageLoader asyncImageLoade = new AsyncImageLoader(
								this, new Callback() {
							@Override
							public void imageLoaded(String path,
													Bitmap bitmap) {
								// TODO Auto-generated method stub

								if (null != bitmap
										&& iv_bg.getTag().toString()
										.equals(path)) {
									Drawable drawable = new BitmapDrawable(
											bitmap);
									iv_bg.setBackground(drawable);
								} else {
									// iv_bg.setBackground(null);
								}

							}
						});

						bitmap = asyncImageLoade.loadBitmap(member.getPhoto(),
								0);

						if (null != bitmap) {
							Drawable drawable = new BitmapDrawable(bitmap);
							iv_bg.setBackground(drawable);

						} else {
							// iv_bg.setBackground(null);
						}

					}

				}
			} else {
				member = new Member();
				// iv_bg.setBackground(null);
			}

			tv_name.setText(member.getName());// 名字
			tv_ban.setText(member.getClazz());// 班级
			mTts.startSpeaking("欢迎" + member.getName() + "同学", mSynListener);// 语音播报

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 *
	 * 解析天气
	 */
	private void todayParse(String weather) {

		try {

			String temp = weather.replace("'", "");
			String[] tempArr = temp.split(",");
			String wd = "";
			String tq = "";
			String fx = "";
			if (tempArr.length > 0) {
				for (int i = 0; i < tempArr.length; i++) {
					if (tempArr[i].indexOf("t1:") != -1) {
						wd = tempArr[i].substring(3, tempArr[i].length()) + "℃";
					} else if (tempArr[i].indexOf("t2:") != -1) {
						wd = wd + "~"
								+ tempArr[i].substring(3, tempArr[i].length())
								+ "℃";
					}
				}

				tv_wendu.setVisibility(View.VISIBLE);
				tv_wendu.setText(wd);
				iv_tanqin.setVisibility(View.VISIBLE);
				iv_tanqin.setImageResource(imageResoId(tq));
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 是否有网络 f:true =有网络 false 无网络
	 */

	private void wifiConnect(boolean f) {
		if (f) {
			ll_wifi.setVisibility(View.VISIBLE);
		} else {
			ll_wifi.setVisibility(View.GONE);
		}
	}

	/**
	 * 天气图片设置
	 */
	private int imageResoId(String weather) {
		int resoid = R.drawable.s_2;
		try {
			if (weather.indexOf("多云") != -1 || weather.indexOf("晴") != -1) {// 多云转晴，以下类同
				// indexOf:包含字串
				resoid = R.drawable.s_1;
			} else if (weather.indexOf("多云") != -1
					&& weather.indexOf("阴") != -1) {
				resoid = R.drawable.s_2;
			} else if (weather.indexOf("阴") != -1 && weather.indexOf("雨") != -1) {
				resoid = R.drawable.s_3;
			} else if (weather.indexOf("晴") != -1 && weather.indexOf("雨") != -1) {
				resoid = R.drawable.s_12;
			} else if (weather.indexOf("晴") != -1 && weather.indexOf("雾") != -1) {
				resoid = R.drawable.s_12;
			} else if (weather.indexOf("晴") != -1) {
				resoid = R.drawable.s_13;
			} else if (weather.indexOf("多云") != -1) {
				resoid = R.drawable.s_2;
			} else if (weather.indexOf("阵雨") != -1) {
				resoid = R.drawable.s_3;
			} else if (weather.indexOf("小雨") != -1) {
				resoid = R.drawable.s_3;
			} else if (weather.indexOf("中雨") != -1) {
				resoid = R.drawable.s_4;
			} else if (weather.indexOf("大雨") != -1) {
				resoid = R.drawable.s_5;
			} else if (weather.indexOf("暴雨") != -1) {
				resoid = R.drawable.s_5;
			} else if (weather.indexOf("冰雹") != -1) {
				resoid = R.drawable.s_6;
			} else if (weather.indexOf("雷阵雨") != -1) {
				resoid = R.drawable.s_7;
			} else if (weather.indexOf("小雪") != -1) {
				resoid = R.drawable.s_8;
			} else if (weather.indexOf("中雪") != -1) {
				resoid = R.drawable.s_9;
			} else if (weather.indexOf("大雪") != -1) {
				resoid = R.drawable.s_10;
			} else if (weather.indexOf("暴雪") != -1) {
				resoid = R.drawable.s_10;
			} else if (weather.indexOf("扬沙") != -1) {
				resoid = R.drawable.s_11;
			} else if (weather.indexOf("沙尘") != -1) {
				resoid = R.drawable.s_11;
			} else if (weather.indexOf("雾") != -1) {
				resoid = R.drawable.s_12;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resoid;
	}

	/**
	 *
	 * @param cardType
	 *            刷卡 校验（校验 异或 运算）
	 */
	public boolean xor(int cardType, int[] cardNumber, int check) {

		for (int i = 0; i < cardNumber.length; i++) {
			cardType = cardType ^ cardNumber[i];
		}

		if (check == cardType) {
			return true;
		}
		return false;
	}

	public int[] bytetoInteger(byte[] src) {
		int[] ints = new int[src.length];
		if (src == null || src.length <= 0) {
			return null;
		}

		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;

			ints[i] = v;
		}

		return ints;
	}

	/**
	 * 刷卡数据解析
	 *
	 * @param src
	 * @return
	 */
	public String bytesToHexString(byte[] src) {
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

	/**
	 * 有网的时候 将刷卡 本地数据 传到网络上
	 */
	private void queryScdata() {

		try {

			ArrayList<SuaiCar> suaicarList = db.querySuaiCar();

			for (SuaiCar cuaicar : suaicarList) {
				// 如果没有传过就传
				if (cuaicar.getState() == 0) {
					exe.execute(new CuaiCarThread(cuaicar.getCid(), cuaicar
							.getTime()));// 用线程池 队列提交刷卡数据
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 将刷卡数据保存在本地
	 */
	private void saveScdata(String cid, String time) {

		try {
			db.insertOrSuaiCar(new SuaiCar(cid, cid, time, 0));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 提交 刷卡数据线程
	 */
	class CuaiCarThread extends Thread {
		String cid;
		String time;

		public CuaiCarThread(String cids, String times) {
			this.cid = cids;
			this.time = times;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			try {
				File file = getStuImage(cid);
				// 向服务器请求学生列表

				submitCardataResponse = EhomeBo.submitgCard(cid, file, time);

				if (submitCardataResponse == null) {
					if (AppConstants.isbDebug) {
						System.out
								.println("===================submitCardataRespons===null");
					}

				} else if (!submitCardataResponse.isSuccess()) {
					if (AppConstants.isbDebug) {
						System.out
								.println("===================submitCardataRespons===false");
					}

				} else {
					if (AppConstants.isbDebug) {
						// 提交刷卡数据成功
						System.out
								.println("===================submitCardataRespons===true");

					}

				}

			} catch (Exception e) {
				e.printStackTrace();

				if (AppConstants.isbDebug) {
					System.out
							.println("===================submitCardataRespons===error");
				}

			}
		}
	}

	/**
	 * 根据 卡 ID 去 存储中 找图片
	 *
	 * @param cid
	 * @return
	 */
	private File getStuImage(String cid) {
		String path = Application.suaicar + cid + ".jpg";
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		return file;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

		switch (arg0.getId()) {
			case R.id.iv_logo:

				Intent intent=new Intent(SuaicarActivity.this,SettingActivity.class);

				startActivity(intent);

				this.finish();

				break;

			default:
				break;
		}

	}

	@Override
	protected void onDestroy() {

		try {

			// TODO Auto-generated method stu

			if (AppConstants.isbDebug) {
				System.out
						.println("====================SuaicarActivity onDestroy()");
			}

			super.onDestroy();
			// Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音

			if (null != mTts) {
				mTts.stopSpeaking();
				// 退出时释放连接
				mTts.destroy();
			}

			/**
			 * 当主界面停止时 发广播重启
			 */
			/**
			 * Intent intent = new Intent("android.intent.action.RSTART");
			 * sendBroadcast(intent);
			 */

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 异步请求天气预报
	 *
	 * @author Administrator
	 *
	 */
	private class QueryAsyncTask extends AsyncTask {
		@Override
		protected void onPostExecute(Object result) {
			try {

				// progressDialog.dismiss();
				if (result != null) {
					String weatherResult = (String) result;
					if (weatherResult.split(";").length > 1) {
						String a = weatherResult.split(";")[1];
						if (a.split("=").length > 1) {
							String b = a.split("=")[1];
							String c = b.substring(1, b.length() - 1);
							String[] resultArr = c.split("\\}");
							if (resultArr.length > 0) {
								todayParse(resultArr[0]);// 解析当天天气
							}

						} else {
							if (AppConstants.isbDebug) {
								System.out.println("查无天气信息");
							}

						}
					} else {
						if (AppConstants.isbDebug) {
							System.out.println("查无天气信息");
						}
					}
				} else {
					if (AppConstants.isbDebug) {
						System.out.println("查无天气信息");
					}
				}

				/**
				 * 设置日期(星期几)
				 */

				tv_week.setText(AppUtils.StringData());
				super.onPostExecute(result);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			return HttpService.getWeather((String) params[0]);
		}
	}

	/**
	 * 图片水平翻转
	 *
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

	@Override
	public void onTakePictureEnd(Bitmap bm) {
		// TODO Auto-generated method stub

	}

	/**
	 * 照相返回监听
	 */
	@Override
	public void onAnimtionEnd(Bitmap bm, boolean isVideo) {

		try {

			// TODO Auto-generated method stub

			if (bm != null) {
				// 生成缩略图

				if(iv_camera.getTag()!=null&&iv_camera.getTag().equals("true")){
					bitmapList.add(bm);

					if (bitmapList.size() > 8) {
						bitmapList.remove(0);
					}

					qinsuAdapters.changeData(bitmapList);
				}

				if (AppConstants.isbDebug) {
					System.out.println("========================生成图片时间"
							+ System.currentTimeMillis());
				}

				Drawable drawable = new BitmapDrawable(bm);
				iv_camera.setBackground(drawable);

				iv_camera.setTag("true");
				if (AppConstants.isbDebug) {
					System.out.println("==================设置图片时间"
							+ System.currentTimeMillis());
				}

				System.out.println("===============carnum=" + carnum);
				queryStu(carnum);

				/**
				 * 发送刷卡数据处理广播
				 */
				Intent intent = new Intent();
				intent.setAction(Application.SUACARS);
				intent.putExtra("cid", carnum);
				intent.putExtra("time", sucartime + "");
				SuaicarActivity.this.sendBroadcast(intent);

			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 公告View
	 *
	 * @author Administrator
	 *
	 */
	class SchView {
		TextView tv_body;
		TextView tv_time;

		public SchView(TextView tv_bodys, TextView tv_times) {
			this.tv_body = tv_bodys;
			this.tv_time = tv_times;
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

}