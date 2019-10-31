package com.example.toroapi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 目前只支持http协议的断点续传单线程下载<br >
 * 支持下载进度监听
 *
 * @author rocky
 */
public class Downloader {

    File saveFile;//待下载文件本地保存路径
    String remoteUrl;//待下载文件远程http地址
    ProgressListener progressListener;//下载进度监听
    int progress;//当前下载进度

    /**
     * 下载构造器
     *
     * @param remoteUrl 待下载文件远程http地址
     * @param saveFile 待下载文件本地保存路径
     */
    public Downloader(String remoteUrl, File saveFile) {
        this.saveFile = saveFile;
        this.remoteUrl = remoteUrl;
    }

    /**
     * 带进度监听的构造器
     *
     * @param remoteUrl
     * @param saveFile
     * @param progressListener
     */
    public Downloader(String remoteUrl, File saveFile, ProgressListener progressListener) {
        this.saveFile = saveFile;
        this.remoteUrl = remoteUrl;
        this.progressListener = progressListener;
    }

    /**
     * 执行下载操作
     *
     * @return 返回保存的文件
     */
    public File download() throws IOException {

        HttpURLConnection httpURLConnection = null;
        URL url;
        BufferedInputStream bis = null;

        // 检查本地文件
        RandomAccessFile rndFile = null;
        File file = saveFile;
        long remoteFileSize = 0;
        long nPos = 0;
        long localFileSize = 0;
        try {
            if (file.exists()) {
                localFileSize = file.length();
                remoteFileSize = getRemoteFileSize(remoteUrl);//获取远程文件大小
                // System.out.println("--remoteFileSize=" + remoteFileSize);
                if (localFileSize < remoteFileSize) {
                    // System.out.println("文件续传...");
                    nPos = localFileSize;
                } else {
                    // System.out.println("文件存在，重新下载...");
                    file.delete();
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        throw new IOException("无法创建文件");
                    }
                    localFileSize = 0;
                }

            } else {
                // 建立文件
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    throw new IOException("无法创建文件");
                }
            }

            // 下载文件
            url = new URL(remoteUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置User-Agent
            httpURLConnection.setRequestProperty("User-Agent", "Android APP");
            // 设置续传开始
            httpURLConnection.setRequestProperty("Range", "bytes=" + nPos + "-");
            // 获取输入流
            bis = new BufferedInputStream(httpURLConnection.getInputStream());
            //获取远程文件大小
            remoteFileSize = remoteFileSize == 0 ? httpURLConnection.getContentLength() : remoteFileSize;
            //链接本地文件
            rndFile = new RandomAccessFile(file, "rw");
            //跳到断点
            rndFile.seek(nPos);
            byte[] buf = new byte[8*1024];
            int size;
            int _progress = 0;//用于比较, 每增加指定的数量才会触发一次onProgressChanged事件
            while ((size = bis.read(buf)) != -1) {
                rndFile.write(buf, 0, size);
                localFileSize += size;//本地文件大小改变
                progress = (int) (localFileSize * 100 / remoteFileSize);
                if (progressListener != null
                        && (progress - _progress >= progressListener.getPer() || progress == 100)) {
                    progressListener.onProgressChanged(progress);//
                    _progress = progress;
                }
            }

            /**
             * 本地文件尺寸大于0且与远程文件尺寸一致才返回
             */
            if (localFileSize == 0 || localFileSize != remoteFileSize) {
                file = null;
            }

        } catch (Exception e) {
            if (localFileSize == 0 && file.exists()) {
                file = null;
                throw new IOException("文件下载失败");
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (rndFile != null) {
                rndFile.close();
            }
            if(file==null && progressListener!=null){
            	progressListener.onProgressChanged(-1);//下载结束
            }
        }

        return file;
    }

    /**
     * 获取远程文件大小
     *
     * @param url
     * @return
     */
    public static long getRemoteFileSize(String url) {
        long size = 0;
        try {
            HttpURLConnection httpUrl = (HttpURLConnection) (new URL(url)).openConnection();
            size = httpUrl.getContentLength();
            httpUrl.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 下载进度监听, 实现onProgressChanged 方法即可
     */
    public abstract static class ProgressListener {

        int per = 1;

        public ProgressListener() {
        }

        /**
         * 百分比每增加per%, 才会触发一次onProgressChanged, 减少触发次数
         *
         * @param per
         */
        public ProgressListener(int per) {
            this.per = per;
        }

        public int getPer() {
            return per;
        }

        /**
         * 实现此方法
         *
         * @param percentage 0至100的整数
         */
        public abstract void onProgressChanged(int percentage);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        String url = "http://e5ex.com/app/android";
        File f = new File("e:\\aa.apk");

        Downloader dl = new Downloader(url, f, new ProgressListener(10) {
            @Override
            public void onProgressChanged(int percentage) {
                // System.out.println("--progress:" + percentage + "%");
            }
        });
        try {
            dl.download();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
