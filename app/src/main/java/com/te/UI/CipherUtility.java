package com.te.UI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.terminalemulation.BuildConfig;

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
}