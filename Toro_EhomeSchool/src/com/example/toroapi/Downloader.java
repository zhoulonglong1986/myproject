package com.example.toroapi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Ŀǰֻ֧��httpЭ��Ķϵ��������߳�����<br >
 * ֧�����ؽ��ȼ���
 *
 * @author rocky
 */
public class Downloader {

    File saveFile;//�������ļ����ر���·��
    String remoteUrl;//�������ļ�Զ��http��ַ
    ProgressListener progressListener;//���ؽ��ȼ���
    int progress;//��ǰ���ؽ���

    /**
     * ���ع�����
     *
     * @param remoteUrl �������ļ�Զ��http��ַ
     * @param saveFile �������ļ����ر���·��
     */
    public Downloader(String remoteUrl, File saveFile) {
        this.saveFile = saveFile;
        this.remoteUrl = remoteUrl;
    }

    /**
     * �����ȼ����Ĺ�����
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
     * ִ�����ز���
     *
     * @return ���ر�����ļ�
     */
    public File download() throws IOException {

        HttpURLConnection httpURLConnection = null;
        URL url;
        BufferedInputStream bis = null;

        // ��鱾���ļ�
        RandomAccessFile rndFile = null;
        File file = saveFile;
        long remoteFileSize = 0;
        long nPos = 0;
        long localFileSize = 0;
        try {
            if (file.exists()) {
                localFileSize = file.length();
                remoteFileSize = getRemoteFileSize(remoteUrl);//��ȡԶ���ļ���С
                // System.out.println("--remoteFileSize=" + remoteFileSize);
                if (localFileSize < remoteFileSize) {
                    // System.out.println("�ļ�����...");
                    nPos = localFileSize;
                } else {
                    // System.out.println("�ļ����ڣ���������...");
                    file.delete();
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        throw new IOException("�޷������ļ�");
                    }
                    localFileSize = 0;
                }

            } else {
                // �����ļ�
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    throw new IOException("�޷������ļ�");
                }
            }

            // �����ļ�
            url = new URL(remoteUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // ����User-Agent
            httpURLConnection.setRequestProperty("User-Agent", "Android APP");
            // ����������ʼ
            httpURLConnection.setRequestProperty("Range", "bytes=" + nPos + "-");
            // ��ȡ������
            bis = new BufferedInputStream(httpURLConnection.getInputStream());
            //��ȡԶ���ļ���С
            remoteFileSize = remoteFileSize == 0 ? httpURLConnection.getContentLength() : remoteFileSize;
            //���ӱ����ļ�
            rndFile = new RandomAccessFile(file, "rw");
            //�����ϵ�
            rndFile.seek(nPos);
            byte[] buf = new byte[8*1024];
            int size;
            int _progress = 0;//���ڱȽ�, ÿ����ָ���������Żᴥ��һ��onProgressChanged�¼�
            while ((size = bis.read(buf)) != -1) {
                rndFile.write(buf, 0, size);
                localFileSize += size;//�����ļ���С�ı�
                progress = (int) (localFileSize * 100 / remoteFileSize);
                if (progressListener != null
                        && (progress - _progress >= progressListener.getPer() || progress == 100)) {
                    progressListener.onProgressChanged(progress);//
                    _progress = progress;
                }
            }

            /**
             * �����ļ��ߴ����0����Զ���ļ��ߴ�һ�²ŷ���
             */
            if (localFileSize == 0 || localFileSize != remoteFileSize) {
                file = null;
            }

        } catch (Exception e) {
            if (localFileSize == 0 && file.exists()) {
                file = null;
                throw new IOException("�ļ�����ʧ��");
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
            	progressListener.onProgressChanged(-1);//���ؽ���
            }
        }

        return file;
    }

    /**
     * ��ȡԶ���ļ���С
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
     * ���ؽ��ȼ���, ʵ��onProgressChanged ��������
     */
    public abstract static class ProgressListener {

        int per = 1;

        public ProgressListener() {
        }

        /**
         * �ٷֱ�ÿ����per%, �Żᴥ��һ��onProgressChanged, ���ٴ�������
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
         * ʵ�ִ˷���
         *
         * @param percentage 0��100������
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
