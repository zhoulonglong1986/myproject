package com.example.toro.sample;

import java.lang.Thread.UncaughtExceptionHandler;

import com.example.toro_ehomeschool.R;
import com.example.toroapi.AppUtils;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {
	
	private static final String TAG = "CrashHandler";

	//程序的Context对象    
    private Context mContext;
    
    //系统默认的UncaughtException处理类   
    private Thread.UncaughtExceptionHandler mDefaultHandler;  

    //CrashHandler实例
    private static CrashHandler instance = null;   
    
    public static CrashHandler getInstance() {
    	if (instance == null) {
    		instance = new CrashHandler();
    	}
    	return instance;
    }
    
    public void init(Context context) {
    	mContext = context;
    	
    	//获取系统默认的UncaughtException处理器  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        //设置该CrashHandler为程序的默认处理器  
        Thread.setDefaultUncaughtExceptionHandler(this);  

    }
    
    //用来存储设备信息和异常信息    
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
	
		 if (!handleException(ex) && mDefaultHandler != null) {  
	            //如果用户没有处理则让系统默认的异常处理器来处理  
	            mDefaultHandler.uncaughtException(thread, ex);  
	        } else {  
	        	 android.os.Process.killProcess(android.os.Process.myPid());  
		            System.exit(0);  

	        }  
	}
	
	/** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  else {
        	//使用Toast来显示异常信息  
            new Thread() {  
                @Override  
                public void run() {  
                    Looper.prepare();  
                   Toast.makeText(mContext, "出现异常XXXX", Toast.LENGTH_LONG).show();
                   // showCustomToast();
                    Looper.loop();

                }  
            }.start();  
            //收集设备参数信息   
            return true;  
        }
    }

    public void showCustomToast() {
        //LayoutInflater这个类用来实例化XML文件到其相应的视图对象的布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //通过制定XML文件及布局ID来填充一个视图对象
        View layout = inflater.inflate(R.layout.custom_toast,null);

        TextView tv_bug=(TextView)layout.findViewById(R.id.tv_bug);

        Toast toast= new Toast(mContext.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();


    }


}
