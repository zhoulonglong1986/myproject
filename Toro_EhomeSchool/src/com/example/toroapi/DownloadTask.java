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
    private String downloadUrl;// �������ӵ�ַ  
    private int threadNum;// �������߳���  
    private String filePath;// �����ļ�·����ַ  
    private int blockSize;// ÿһ���̵߳�������  
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
            // ��ȡ�����ļ��ܴ�С  
            int fileSize = conn.getContentLength();  
            if (fileSize <= 0) {  
                System.out.println("��ȡ�ļ�ʧ��");  
                return;  
            }  
            // ����ProgressBar���ĳ���Ϊ�ļ�Size  

            // ����ÿ���߳����ص����ݳ���  
            blockSize = (fileSize % threadNum) == 0 ? fileSize / threadNum  
                    : fileSize / threadNum + 1;  

      

            File file = new File(filePath);  
            for (int i = 0; i < threads.length; i++) {  
                // �����̣߳��ֱ�����ÿ���߳���Ҫ���صĲ���  
                threads[i] = new FileDownloadThread(url, file, blockSize,  
                        (i + 1));  
                threads[i].setName("Thread:" + i);  
                threads[i].start();  
            }  

            boolean isfinished = false;  
            int downloadedAllSize = 0;  
            while (!isfinished) {  
                isfinished = true;  
                // ��ǰ�����߳���������  
                downloadedAllSize = 0;  
                for (int i = 0; i < threads.length; i++) {  
                    downloadedAllSize += threads[i].getDownloadLength();  
                    if (!threads[i].isCompleted()) {  
                        isfinished = false;  
                    }  
                }  
               
            }  
            
            /**
             * ������н�ѹ�� 
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