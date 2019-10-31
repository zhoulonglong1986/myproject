package com.example.toroapi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.example.toro_ehomeschool.MainActivity;
import com.linj.FileOperateUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.gsm.SmsManager;
import android.text.ClipboardManager;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class AppUtils {

	public static String writePhotoToSd(Context content, Bitmap bitmap,
			String name) {
		if (bitmap == null) {
			return null;
		}

		String path = FileOperateUtil.getFolderPath(content,
				FileOperateUtil.TYPE_IMAGE, "toro");
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		String fileName = path + File.separator + name;
		FileOutputStream b = null;
		try {
			b = new FileOutputStream(fileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}
	
	
	 public static String StringData(){  
	        final Calendar c = Calendar.getInstance();  
	        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));  
	      
	        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  
	        if("1".equals(mWay)){  
	            mWay ="天";  
	        }else if("2".equals(mWay)){  
	            mWay ="一";  
	        }else if("3".equals(mWay)){  
	            mWay ="二";  
	        }else if("4".equals(mWay)){  
	            mWay ="三";  
	        }else if("5".equals(mWay)){  
	            mWay ="四";  
	        }else if("6".equals(mWay)){  
	            mWay ="五";  
	        }else if("7".equals(mWay)){  
	            mWay ="六";  
	        }  
	        return "星期"+mWay;  
	    }  
	 
	 public static String getYearMonthDayHourMinuteSecond(long timeMillis,int type) {
	      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));  
	      calendar.setTimeInMillis(timeMillis);
	      int year=calendar.get(Calendar.YEAR);
	      
	      int month=calendar.get(Calendar.MONTH) + 1;
	      String mToMonth=null;
	      if (String.valueOf(month).length()==1) {
	      	mToMonth="0"+month;
	      } else {
	      	mToMonth=String.valueOf(month);
	      }
	      
	      int day=calendar.get(Calendar.DAY_OF_MONTH);
	      String dToDay=null;
	      if (String.valueOf(day).length()==1) {
	      	dToDay="0"+day;
	      } else {
	      	dToDay=String.valueOf(day);
	      }
	      
	      int hour=calendar.get(Calendar.HOUR_OF_DAY);
	      String hToHour=null;
	      if (String.valueOf(hour).length()==1) {
	      	hToHour="0"+hour;
	      } else {
	      	hToHour=String.valueOf(hour);
	      }
	      
	      int minute=calendar.get(Calendar.MINUTE);
	      String mToMinute=null;
	      if (String.valueOf(minute).length()==1) {
	      	mToMinute="0"+minute;
	      } else {
	      	mToMinute=String.valueOf(minute);
	      }
	      
	      int second=calendar.get(Calendar.SECOND);
	      String sToSecond=null;
	      if (String.valueOf(second).length()==1) {
	      	sToSecond="0"+second;
	      } else {
	      	sToSecond=String.valueOf(second);
	      }
	      if(type==1){
	    	  return  mToMonth+ "月-" +dToDay+"日"; 
	      }else{
	    	  return  mToMonth+ "/" +dToDay; 
	      }
	    		
	  }


}
