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
	 * 实现批量的图片加载和缓存
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
					// 获取加载完成的图片加载任务
					ImageLoadTask task = (ImageLoadTask) msg.obj;
					// 回调
					callback.imageLoaded(task.path, task.bitmap);
				}
			};

		}

		private File getFileInfo(String path) {
		
			File file = new File(path);
			
			return file;
		}

		/**
		 * 加载指定路径的图片
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
			// 判断文件缓存
			//bm = BitmapUtils.loadBitmap(getFileInfo(filepath).getAbsolutePath());
			bm=BitmapFactory.decodeFile(filepath);
			if (bm != null) {
				// 存在文件缓存

				return bm;
			}
			// 创建图片加载任务，添加到任务集合并唤醒线程
			if(AppConstants.netConnect){
				final ImageLoadTask task = new ImageLoadTask();
				task.path = path;
				
				new Thread(){
					
					public void run() {
						
						try {
							// 执行加载任务
							HttpEntity entity = HttpUtils.getEntity(task.path, null,
									HttpUtils.METHOD_GET);
							byte[] data = EntityUtils.toByteArray(entity);
							task.bitmap = BitmapUtils.loadBitmap(data, 64, 64);
							// 发送消息到转线程
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
