package com.example.toro_ehomeschool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 监听开机广播
 * @author Administrator
 * 功能：开机自动启动APP
 */
public class BootReceiver extends BroadcastReceiver {  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent start_load = new Intent(context, LoadingActivity.class);  
            start_load.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            context.startActivity(start_load);  
        }  
    }  
}  