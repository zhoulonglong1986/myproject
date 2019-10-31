package com.example.toroapi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import android.os.Environment;
import android.util.Log;

public class Logger {
	public final static boolean printLog = true;

	public static void e(String key, Object value) {
		if (printLog) {
			Log.e(key, "LM Together : " + String.valueOf(value));
			// saveData("Key: " + key + "     Value: " + value);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (printLog) {
			Log.e(tag, msg, tr);
		}
	}

	public static void i(String key, String value) {
		if (printLog) {
			Log.i(key, "result:" + value);
		}
	}

	public static void w(String key, String value) {
		if (printLog) {
			Log.w(key, "result:" + value);
		}
	}

	public static void d(String key, String value) {
		if (printLog) {
			Log.d(key, "result:" + value);
		}
	}

	private static void saveData(String value) {
		String fileUrl = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/TestTogether/";
		try {

			File f = new File(fileUrl);
			if (!f.exists()) {
				f.mkdirs();
			}

			fileUrl = fileUrl + "/" + "logger" + ".txt";

			File file = new File(fileUrl);
			if (!file.exists()) {
				file.createNewFile();
			}

			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(fileUrl, true));
			BufferedWriter bw = new BufferedWriter(osw);

			bw.write(value);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
