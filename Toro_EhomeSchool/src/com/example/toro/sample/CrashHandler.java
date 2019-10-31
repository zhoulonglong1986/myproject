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

	//�����Context����    
    private Context mContext;
    
    //ϵͳĬ�ϵ�UncaughtException������   
    private Thread.UncaughtExceptionHandler mDefaultHandler;  

    //CrashHandlerʵ��
    private static CrashHandler instance = null;   
    
    public static CrashHandler getInstance() {
    	if (instance == null) {
    		instance = new CrashHandler();
    	}
    	return instance;
    }
    
    public void init(Context context) {
    	mContext = context;
    	
    	//��ȡϵͳĬ�ϵ�UncaughtException������  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        //���ø�CrashHandlerΪ�����Ĭ�ϴ�����  
        Thread.setDefaultUncaughtExceptionHandler(this);  

    }
    
    //�����洢�豸��Ϣ���쳣��Ϣ    
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
	
		 if (!handleException(ex) && mDefaultHandler != null) {  
	            //����û�û�д�������ϵͳĬ�ϵ��쳣������������  
	            mDefaultHandler.uncaughtException(thread, ex);  
	        } else {  
	        	 android.os.Process.killProcess(android.os.Process.myPid());  
		            System.exit(0);  

	        }  
	}
	
	/** 
     * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����. 
     *  
     * @param ex 
     * @return true:��������˸��쳣��Ϣ;���򷵻�false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  else {
        	//ʹ��Toast����ʾ�쳣��Ϣ  
            new Thread() {  
                @Override  
                public void run() {  
                    Looper.prepare();  
                   Toast.makeText(mContext, "�����쳣XXXX", Toast.LENGTH_LONG).show();
                   // showCustomToast();
                    Looper.loop();

                }  
            }.start();  
            //�ռ��豸������Ϣ   
            return true;  
        }
    }

    public void showCustomToast() {
        //LayoutInflater���������ʵ����XML�ļ�������Ӧ����ͼ����Ĳ���
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //ͨ���ƶ�XML�ļ�������ID�����һ����ͼ����
        View layout = inflater.inflate(R.layout.custom_toast,null);

        TextView tv_bug=(TextView)layout.findViewById(R.id.tv_bug);

        Toast toast= new Toast(mContext.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();


    }


}
