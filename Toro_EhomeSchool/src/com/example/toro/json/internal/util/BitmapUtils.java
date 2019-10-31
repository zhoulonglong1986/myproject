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
		 * ���ر��ص�ͼƬ�ļ� ���ڴ�
		 * 
		 * @param path
		 *            ͼƬ�ļ���·��
		 * @return
		 */
		public static Bitmap loadBitmap(String path) {
			Bitmap bm = null;
			bm = BitmapFactory.decodeFile(path);
			return bm;
		}

		/**
		 * ��ָ����� �����ֽ������е�ͼƬ���ڴ�
		 * 
		 * @param data
		 * @param width
		 * @param height
		 * @return
		 */
		public static Bitmap loadBitmap(byte[] data, int width, int height) {
			Bitmap bm = null;
			// ����ͼƬ�ı߽���Ϣ
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			// ������������
			int xSacle = opts.outWidth / width;
			int yScale = opts.outHeight / height;

			// ������������
			opts.inSampleSize = xSacle > yScale ? xSacle : yScale;
			// ����ͼƬ
			opts.inJustDecodeBounds = false;
			//Bitmap bitMap = BitmapFactory.decodeByteArray(data, 0, length);��������ʽ
			bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			
			return bm;
		}

		/**
		 * �����ڴ��е�ͼƬ��ָ���ļ�
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