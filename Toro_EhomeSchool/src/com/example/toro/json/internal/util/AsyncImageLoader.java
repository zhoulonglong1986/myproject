package com.example.toro.json.internal.util;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.example.toro.sample.Application;
import com.example.toro_ehomeschool.MainActivity;
import com.example.toroapi.AppConstants;
import com.example.toroapi.HttpUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;



	/**
	 * ʵ��������ͼƬ���غͻ���
	 * 
	 * @author pjy
	 * 
	 */
	public class AsyncImageLoader {
		private Handler handler;
		private Context context;

		public AsyncImageLoader(Context context, final Callback callback) {
			this.context = context;
			this.handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// ��ȡ������ɵ�ͼƬ��������
					ImageLoadTask task = (ImageLoadTask) msg.obj;
					// �ص�
					callback.imageLoaded(task.path, task.bitmap);
				}
			};

		}

		private File getFileInfo(String path) {
		
			File file = new File(path);
			
			return file;
		}

		/**
		 * ����ָ��·����ͼƬ
		 * type =0 stu  type=1 sch
		 * @param path
		 * @return
		 */
		public Bitmap loadBitmap(String path,int type) {
			String fileName = path.substring(path.lastIndexOf("/") + 1);
			String filepath;
			if(type==0){
				filepath= Application.stuPath + fileName;
			}else{
				filepath= Application.schPath + fileName;
			}
			
			
			Bitmap bm = null;
			// �ж��ļ�����
			//bm = BitmapUtils.loadBitmap(getFileInfo(filepath).getAbsolutePath());
			bm=BitmapFactory.decodeFile(filepath);
			if (bm != null) {
				// �����ļ�����

				return bm;
			}
			// ����ͼƬ����������ӵ����񼯺ϲ������߳�
			if(AppConstants.netConnect){
				final ImageLoadTask task = new ImageLoadTask();
				task.path = path;
				
				new Thread(){
					
					public void run() {
						
						try {
							// ִ�м�������
							HttpEntity entity = HttpUtils.getEntity(task.path, null,
									HttpUtils.METHOD_GET);
							byte[] data = EntityUtils.toByteArray(entity);
							task.bitmap = BitmapUtils.loadBitmap(data, 64, 64);
							// ������Ϣ��ת�߳�
							Message msg = Message.obtain(handler, 0, task);
							msg.sendToTarget();
							
							String fileName = task.path.substring(task.path.lastIndexOf("/") + 1);
							String filepath = Application.schPath + fileName;

							File file = getFileInfo(filepath);
							BitmapUtils.save(task.bitmap, file);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					};
				}.start();
			
			}
			
			return null;
		}

		private class ImageLoadTask {
			private String path;
			private Bitmap bitmap;
	
		}

		public interface Callback {
			void imageLoaded(String path, Bitmap bitmap);
		}
	}
