package com.guohua.north_bulb.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

/**
 * 日志记录
 * 
 */
public class LogUtil {
	private static final String TAG = "LogUtil";
	/**
	 * 开发阶段
	 */
	private static final int DEVELOP = 0;
	/**
	 * 内部测试阶段
	 */
	private static final int DEBUG = 1;
	/**
	 * 公开测试
	 */
	private static final int BATE = 2;
	/**
	 * 正式版
	 */
	private static final int RELEASE = 3;

	/**
	 * 当前阶段标示
	 */
	private static int currentStage = DEVELOP;

	private static String path;
	private static File file;
	private static FileOutputStream outputStream;
	private static String pattern = "yyyy-MM-dd HH:mm:ss";

	/*static {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File externalStorageDirectory = Environment.getExternalStorageDirectory();
			path = externalStorageDirectory.getAbsolutePath() + "/HuiCaiSmart/";
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			file = new File(new File(path), "log.txt");
			android.util.Log.i("SDCAEDTAG", path);
			try {
				outputStream = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {

			}
		} else {

		}
	}*/

	public static void info(String msg) {
		info(TAG, msg);
	}
	
	
	public static void info(String tag, String msg) {
		if(currentStage == DEVELOP) {
			Log.i(tag, msg);
		} else if(currentStage == DEBUG) {
			
		} else if(currentStage == BATE) {
			writeMsgToSDCard(tag, msg);
		} else {
			
		}
	}
	
	/**
	 * 将日志记录写到SDCard中
	 * @param clazz
	 * @param msg
	 */
	private static void writeMsgToSDCard(String tag, String msg)  {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File externalStorageDirectory = Environment.getExternalStorageDirectory();
			path = externalStorageDirectory.getAbsolutePath() + "/HuiCaiSmart/";
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			file = new File(new File(path), "log.txt");
			Log.i("SDCAEDTAG", path);
			try {
				outputStream = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {

			}
		}
		
		// 写日志到sdcard
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		
		String time = format.format(date);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if (outputStream != null) {
				try {
					outputStream.write(time.getBytes());
					String className = "";
					if (tag != null) {
						className = "LogUtil";
					}
					outputStream.write(("    " + className + "\r\n").getBytes());

					outputStream.write(msg.getBytes());
					outputStream.write("\r\n".getBytes());
					outputStream.flush();
				} catch (IOException e) {

				}
			} else {
				Log.i("SDCAEDTAG", "file is null");
			}
		}
	}
}
