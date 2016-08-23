package com.te.UI;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.terminalemulation.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import Terminals.stdActivityRef;

public class CipherUtility {
	static final String READER_CONFIG_PKG_NAME = "cipherlab.sw.readerconfig";
	static Context mContext = null;
	static WindowManager mWMgr = null;
	static public void init(Context context) {
		mContext = context;
		mWMgr = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
	}

	static public void Log_d(String tag, String msg) {
		if (BuildConfig.DEBUG_MODE)
			Log.d("TE:", "[" + tag + "]:" + msg);
	}

	static public void outputHex(String tag, char[] charArray) {
		if (BuildConfig.DEBUG_MODE) {
			String strHex = "";
			for (char C : charArray) {
				strHex += String.format("%02x ", (byte) C);
			}
			CipherUtility.Log_d("TE:", "[" + tag + "]:" + strHex);
		}
	}

	static public void outputHex(String tag, byte[] byteArray) {
		if (BuildConfig.DEBUG_MODE) {
			String strHex = "";
			for (byte by : byteArray) {
				strHex += String.format("%02x ", by);
			}
			CipherUtility.Log_d("TE:", "[" + tag + "]:" + strHex);
		}
	}

	// 0 ~ 100
	static public int getWiFiStrength() {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		int wifiStrength = wifi.calculateSignalLevel(wifi.getConnectionInfo().getRssi(), 100);
		return wifiStrength;
	}

	// 0 ~ 100
	static public int getBatteryPct() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = mContext.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float) scale * 100;
		return (int) batteryPct;
	}

	static public boolean hasNetwork() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	static public void playSound(String file) {
		SoundPool soundPool;
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(file, 1);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		soundPool.play(1, 1, 1, 0, 0, 1);
	}

	static void vibration(long milliseconds) {
		stdActivityRef.ApplicationVibration(milliseconds);
	}

	public static void copyFile(InputStream inputStream, OutputStream fileOutputStream) throws IOException {
		byte[] buffer = new byte[100];
		int read;
		while ((read = inputStream.read(buffer)) != -1) {
			fileOutputStream.write(buffer, 0, read);
		}
	}

	public static void copyFile(File source, File dest) {
		try {
			InputStream input = new FileInputStream(source);
			OutputStream output = new FileOutputStream(dest);
			copyFile(input, output);
		} catch (Exception e) {

		}
	}

	public static int getScreenWidth() {
		final DisplayMetrics metrics = new DisplayMetrics();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mWMgr.getDefaultDisplay().getRealMetrics(metrics);
			return metrics.widthPixels;
		} else {
			try {
				Method GetRawW = Display.class.getMethod("getRawWidth");
				return (Integer) GetRawW.invoke(mWMgr.getDefaultDisplay());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return 0;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return 0;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	public static int getScreenHeight() {
		final DisplayMetrics metrics = new DisplayMetrics();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mWMgr.getDefaultDisplay().getRealMetrics(metrics);
			return metrics.heightPixels;
		} else {
			try {
				Method GetRawW = Display.class.getMethod("getRawHeight");
				return (Integer) GetRawW.invoke(mWMgr.getDefaultDisplay());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return 0;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return 0;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	public static void enableAllChild(ViewGroup layout, boolean bEnabled) {
		layout.setEnabled(bEnabled);
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			if (child instanceof ViewGroup) {
				enableAllChild((ViewGroup) child, bEnabled);
			} else if (child instanceof View == true){
				child.setEnabled(bEnabled);
			}
		}
	}

	public static boolean isReaderConfigAvable() {
		PackageManager manager = mContext.getPackageManager();
		Intent intent = manager.getLaunchIntentForPackage(READER_CONFIG_PKG_NAME);
		if (intent == null) {
			return false;
		}
		List activities = manager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		if (activities == null) {
			return false;
		}
		return true;
	}

	public static void showReaderConfig() {
		if(isReaderConfigAvable()) {
			Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(READER_CONFIG_PKG_NAME);
			mContext.startActivity(intent);
		}
	}
}