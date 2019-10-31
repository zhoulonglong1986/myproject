package com.example.toroapi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloadThread extends Thread {  
  
    private static final String TAG = FileDownloadThread.class.getSimpleName();  
  
    /** ��ǰ�����Ƿ���� */  
    private boolean isCompleted = false;  
    /** ��ǰ�����ļ����� */  
    private int downloadLength = 0;  
    /** �ļ�����·�� */  
    private File file;  
    /** �ļ�����·�� */  
    private URL downloadUrl;  
    /** ��ǰ�����߳�ID */  
    private int threadId;  
    /** �߳��������ݳ��� */  
    private int blockSize;  
  
    /** 
     *  
     * @param url:�ļ����ص�ַ 
     * @param file:�ļ�����·�� 
     * @param blocksize:�������ݳ��� 
     * @param threadId:�߳�ID 
     */  
    public FileDownloadThread(URL downloadUrl, File file, int blocksize,  
            int threadId) {  
        this.downloadUrl = downloadUrl;  
        this.file = file;  
        this.threadId = threadId;  
        this.blockSize = blocksize;  
    }  
  
    @Override  
    public void run() {  
  
        BufferedInputStream bis = null;  
        RandomAccessFile raf = null;  
  
        try {  
            URLConnection conn = downloadUrl.openConnection();  
            conn.setAllowUserInteraction(true);  
  
            int startPos = blockSize * (threadId - 1);//��ʼλ��  
            int endPos = blockSize * threadId - 1;//����λ��  
            //���õ�ǰ�߳����ص���㡢�յ�  
            conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);  
            System.out.println(Thread.currentThread().getName() + "  bytes="  
                    + startPos + "-" + endPos);  
  
            byte[] buffer = new byte[1024];  
            bis = new BufferedInputStream(conn.getInputStream());  
  
            raf = new RandomAccessFile(file, "rwd");  
            raf.seek(startPos);  
            int len;  
            while ((len = bis.read(buffer, 0, 1024)) != -1) {  
                raf.write(buffer, 0, len);  
                downloadLength += len;  
            }  
            isCompleted = true;  
           
  
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
  
    /** 
     * �߳��ļ��Ƿ�������� 
     */  
    public boolean isCompleted() {  
        return isCompleted;  
    }  
  
    /** 
     * �߳������ļ����� 
     */  
    public int getDownloadLength() {  
        return downloadLength;  
    }  
  
}  