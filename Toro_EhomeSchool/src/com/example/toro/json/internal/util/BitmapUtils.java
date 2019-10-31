package com.example.toro.json.internal.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;


public class BitmapUtils {
		/**
		 * 加载本地的图片文件 到内存
		 * 
		 * @param path
		 *            图片文件的路径
		 * @return
		 */
		public static Bitmap loadBitmap(String path) {
			Bitmap bm = null;
			bm = BitmapFactory.decodeFile(path);
			return bm;
		}

		/**
		 * 按指定宽高 加载字节数组中的图片到内存
		 * 
		 * @param data
		 * @param width
		 * @param height
		 * @return
		 */
		public static Bitmap loadBitmap(byte[] data, int width, int height) {
			Bitmap bm = null;
			// 加载图片的边界信息
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			// 计算收缩比例
			int xSacle = opts.outWidth / width;
			int yScale = opts.outHeight / height;

			// 设置收缩比例
			opts.inSampleSize = xSacle > yScale ? xSacle : yScale;
			// 加载图片
			opts.inJustDecodeBounds = false;
			//Bitmap bitMap = BitmapFactory.decodeByteArray(data, 0, length);不收缩方式
			bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			
			return bm;
		}

		/**
		 * 保存内存中的图片到指定文件
		 * 
		 * @param bm
		 * @param file
		 * @throws IOException
		 */
		public static void save(Bitmap bm, File file) throws IOException {
			if (bm != null && file != null) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream stream = new FileOutputStream(file);

				bm.compress(CompressFormat.JPEG, 100, stream);
				
			}
		}
	}