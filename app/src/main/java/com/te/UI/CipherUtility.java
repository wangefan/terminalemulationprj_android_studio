package com.te.UI;

import android.util.Log;

import com.example.terminalemulation.BuildConfig;

public class CipherUtility
{
	public static void Log_d(String tag, String msg) {
		if(BuildConfig.DEBUG_MODE)
			Log.d("TE:", "[" + tag + "]:" + msg);
    }

	public static void d(String tag, String msg, Throwable tr) {
		if(BuildConfig.DEBUG_MODE)
			Log.d("TE:", "[" + tag + "]:" + msg, tr);
    }

	public static void e(String tag, String msg) {
		if(BuildConfig.DEBUG_MODE)
			Log.e("TE:", "[" + tag + "]:" + msg);
    }

	public static void e(String tag, String msg, Throwable tr) {
		if(BuildConfig.DEBUG_MODE)
			Log.e("TE:", "[" + tag + "]:" + msg, tr);
    }
}
