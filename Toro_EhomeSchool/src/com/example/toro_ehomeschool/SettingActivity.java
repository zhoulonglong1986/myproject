package com.example.toro_ehomeschool;

import com.example.toro.sample.Application;
import com.example.toro_ehomeschool.PushchReceiver.BootThread;
import com.example.toroapi.PreferencesService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener{
	
	private EditText et_startime,et_endtime,et_inter;
	
	private String startm,endtm,inter;
	private Button bt_save;
	private PreferencesService pre;//偏好数据保存 (机器码保存读取)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		et_startime=(EditText)this.findViewById(R.id.et_startime);
		et_endtime=(EditText)this.findViewById(R.id.et_endtime);
		et_inter=(EditText)this.findViewById(R.id.et_inter);
		bt_save=(Button)this.findViewById(R.id.bt_save);
		
		bt_save.setOnClickListener(this);
		
		
		init();
	}
	
	private void init(){
		pre = new PreferencesService(this);
		
		startm=pre.getstartime()+"";
		et_startime.setText(startm);
		
		endtm=pre.getendtime()+"";
		et_endtime.setText(endtm);
		
		inter=pre.getCreditinterval();
		int inte=Integer.parseInt(inter)/30;
		et_inter.setText(inte+"");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		switch (arg0.getId()) {
		case R.id.bt_save:
			
			
			
			boolean f=false;
			int t=Integer.parseInt(et_inter.getText().toString().trim())*30;
			if(!inter.equals(t+"")){
				Application.interval=t;
				pre.saveCreditinterval(t+"");
				f=true;
			}
			
			
			if((!startm.equals(et_startime.getText().toString().trim()))||(!endtm.equals(et_endtime.getText().toString().trim()))){
				
				
				String start=et_startime.getText().toString().trim();
				String end=et_endtime.getText().toString().trim();
				
				
				//设置开关机时间
				int[] startime=new int[5];
				startime[0]=Integer.parseInt(start.substring(0, 2));
				startime[1]=Integer.parseInt(start.substring(2, 4));
				
				
				int[] endtime=new int[5];
				
				endtime[0]=Integer.parseInt(end.substring(0, 2));
				endtime[1]=Integer.parseInt(end.substring(2, 4));
				
				
				
				
				
				if(null!=startime&&null!=endtime){
					
					pre.savestartime(et_startime.getText().toString().trim());
					pre.savendtime(et_endtime.getText().toString().trim());
					
					new BootThread(SettingActivity.this, startime, endtime).start();
					
				}else{
					Toast.makeText(SettingActivity.this, "日期格式不正确", Toast.LENGTH_LONG).show();
				}
				
			}else{
				if(f){
					//重启
					
					Intent intent = new Intent("android.intent.action.pubds_reboot");
					SettingActivity.this.sendBroadcast(intent);//重启系统指今
				}
			}
			

			
			break;

		default:
			break;
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
			

			
			intResultArray[1] = Integer.parseInt(tmpContent.substring(2, tmpContent.length()));
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
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent canceldownintent = new Intent("com.example.jt.shutdowntime");
				canceldownintent.putExtra("message", "cancel");
				mcontext.sendBroadcast(canceldownintent);//取消定时关机 
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				  String[] timestart =new String[2];
				
				if(startime[0]<10){
					timestart[0]="0"+startime[0];
				}else{
					timestart[0]=""+startime[0];
				}
				
				if(startime[1]<10){
					timestart[1]="0"+startime[1];
				}else{
					timestart[1]=""+startime[1];
				}
				
				String boottime = "1,0,0," + timestart[0] + "," + timestart[1]+ "," + startime[2] + "," + startime[3] + "," + startime[4];
				System.out.println("=========okok=="+boottime);
				Intent bootintent = new Intent("com.example.jt.boottime");
				bootintent.putExtra("message", boottime);
				mcontext.sendBroadcast(bootintent);
				
				
				
				
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

				System.out.println("=====okok定时关机 时间发送+=="+shutdownTime);
				mcontext.sendBroadcast(shutdownintent);
				
				
				pre.savestartime(timestart[0]+""+timestart[1]);
				pre.savendtime(timend[0]+""+timend[1]);
				
				SettingActivity.this.finish();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

		}
		
	}
	


}
