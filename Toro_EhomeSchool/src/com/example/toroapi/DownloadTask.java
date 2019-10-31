package com.example.toroapi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


import com.example.toro.sample.Application;

import android.os.Handler;
import android.os.Message;


public class DownloadTask extends Thread {  
    private String downloadUrl;// 下载链接地址  
    private int threadNum;// 开启的线程数  
    private String filePath;// 保存文件路径地址  
    private int blockSize;// 每一个线程的下载量  
    private Handler mHandler;

    public DownloadTask(String downloadUrl, int threadNum, String fileptah,Handler handler) {  
        this.downloadUrl = downloadUrl;  
        this.threadNum = threadNum;  
        this.filePath = fileptah;  
        this.mHandler=handler;
    }  

    @Override  
    public void run() {  

        FileDownloadThread[] threads = new FileDownloadThread[threadNum];  
        Message msg=new Message();
        msg.what=5; 
        try {  
            URL url = new URL(downloadUrl);  
            
            URLConnection conn = url.openConnection();  
            // 读取下载文件总大小  
            int fileSize = conn.getContentLength();  
            if (fileSize <= 0) {  
                System.out.println("读取文件失败");  
                return;  
            }  
            // 设置ProgressBar最大的长度为文件Size  

            // 计算每条线程下载的数据长度  
            blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum  
                    : fileSize / threadNum + 1;  

      

            File file = new File(filePath);  
            for (int i = 0; i < threads.length; i++) {  
                // 启动线程，分别下载每个线程需要下载的部分  
                threads[i] = new FileDownloadThread(url, file, blockSize,  
                        (i + 1));  
                threads[i].setName("Thread:" + i);  
                threads[i].start();  
            }  

            boolean isfinished = false;  
            int downloadedAllSize = 0;  
            while (!isfinished) {  
                isfinished = true;  
                // 当前所有线程下载总量  
                downloadedAllSize = 0;  
                for (int i = 0; i < threads.length; i++) {  
                    downloadedAllSize += threads[i].getDownloadLength();  
                    if (!threads[i].isCompleted()) {  
                        isfinished = false;  
                    }  
                }  
               
            }  
            
            /**
             * 这里进行解压缩 
             */
            /*ZipFileThread zft = new ZipFileThread();
			zft.setFile(file);
			zft.setHandlePath(Application.schPath  + zipFileName);
			zft.setHandler(zipHandler);
			zft.setZip(false);
			zft.start();*/
            

        } catch (Exception e) {  
        	msg.obj=e.getMessage();
            e.printStackTrace();  
        } finally{
        	msg.obj="";
        	mHandler.sendMessage(msg);
        }

    }  
}  