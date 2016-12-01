package Terminals;

import android.content.Context;

import com.cipherlab.terminalemulation.R;

public class CipherReaderControl {
	public interface OnReaderControlListener {
		void onData(String data);
		void onReaderServiceConnected();
	}

	private static ReaderObjBase mReaderObj = null;
	public static void InitReader(Context context, OnReaderControlListener listener) {
		String appId = context.getResources().getString(R.string.application_Id);
		if(appId.compareTo("com.densowave.terminalemulation") == 0) {
			mReaderObj = new DensowaveReaderObj(context, listener);
		} else {
			mReaderObj = new CipherLabReaderObj(context, listener);
		}
	}
	 
	public static void ReleaseReader(Context context) {
		if (mReaderObj != null) {
			mReaderObj.releaseReader(context);
		}
	}

	public static void SetActived(Boolean Enable) {
		if (mReaderObj != null) {
			mReaderObj.setActived(Enable);
		}
	}
}
