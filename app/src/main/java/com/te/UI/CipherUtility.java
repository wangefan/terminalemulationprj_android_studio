package com.te.UI;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.terminalemulation.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import Terminals.stdActivityRef;

public class CipherUtility {
	static Context mContext = null;
	static public void init(Context context) {
		mContext = context;
	}

	static public void Log_d(String tag, String msg) {
		if (BuildConfig.DEBUG_MODE)
			Log.d("TE:", "[" + tag + "]:" + msg);
	}

	static public int getWiFiStrength() {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		int wifiStrength = wifi.calculateSignalLevel(wifi.getConnectionInfo().getRssi(), 100);
		return wifiStrength;
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
}