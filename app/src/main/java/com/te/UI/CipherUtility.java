package com.te.UI;

import android.util.Log;

import com.example.terminalemulation.BuildConfig;

public class CipherUtility {
	public static void Log_d(String tag, String msg) {
		if(BuildConfig.DEBUG_MODE)
			Log.d("TE:", "[" + tag + "]:" + msg);
    }
}
