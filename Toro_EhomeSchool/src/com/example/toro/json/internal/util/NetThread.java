package com.example.toro.json.internal.util;

import com.example.toro_ehomeschool.LoadingActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class NetThread {

	Callback callback;
	Context context;
	Handler handler;
	public NetThread(Context contexts,Callback callbacks){
		this.callback=callbacks;
		this.context=contexts;
		this.handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				callback.connect(Integer.parseInt(msg.obj.toString()));
			}
		};
		
		runs();
	}
	
	public void runs(){
		new Thread(){
			public void run() {
				
				for(int i=0;i<=10;i++){
					//等待10秒，如果还没连上网络，则提示
					try {
						Thread.sleep(1000);
						boolean isConnected = NetUtils.isNetworkConnected(context);
						
						if(isConnected){
							Message msg=new Message();
							msg.obj=1;
							handler.sendMessage(msg);
							
							break;
						}else if(i==5){
							Message msg=new Message();
							msg.obj=5;
							handler.sendMessage(msg);
							
						}else if(i==10){
							Message msg=new Message();
							msg.obj=10;
							handler.sendMessage(msg);
							
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}.start();
	}
	
	
	public interface Callback {
		 void connect(int i);
	}
}
