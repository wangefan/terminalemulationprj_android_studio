package com.te.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.terminalemulation.R;

import java.io.UnsupportedEncodingException;

import Terminals.CipherConnectSettingInfo;

public class UIUtility {
	public interface OnEditMessageBoxListener {
		void onResult(String result);
		void onCancel();
	}
	static private ProgressDialog mPDialog = null;
	static private Context mContext;
	static private Point mPrgDlgSize = new Point();
	static private boolean mBShow = false;
	static private Handler mUIHandler = null;
	
	//member functions
	static public void init(Context context) {
		mContext = context;
		mUIHandler = new Handler();
	}

	public static String hexToString(String txtInHex, String encode) {
		byte[] txtInByte = new byte[txtInHex.length() / 2];
		int j = 0;
		for (int i = 0; i < txtInHex.length(); i += 2) {
			txtInByte[j++] = (byte) Integer.parseInt(
					txtInHex.substring(i, i + 2), 16);
		}
		try {
			return new String(txtInByte, encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(txtInByte);
		}
	}

	static public void showProgressDlg(boolean bShow, int messageID)
    {
    	if(bShow && mBShow == false)
    	{
    		mPDialog = new ProgressDialog(mContext, R.style.MyProgressDlg);
    		mPDialog.setCancelable(false); 
    		mPDialog.setMessage(mContext.getText(messageID));
    		mPDialog.show();
    		mBShow = true;
    		
    		Point wndSize = new Point();
			mPDialog.getWindow().getWindowManager().getDefaultDisplay().getSize(wndSize);
    		mPrgDlgSize.x = 4 * wndSize.x / 5;
    		mPrgDlgSize.y = wndSize.y / 4;
    		WindowManager.LayoutParams lp = mPDialog.getWindow().getAttributes();     
    		lp.alpha = 0.6f;    
    		lp.dimAmount=0.0f;  
    		lp.width = mPrgDlgSize.x;
    		lp.height = mPrgDlgSize.y;
    		mPDialog.getWindow().setAttributes(lp);   
    	}
    	else
    	{
    		if(mPDialog != null)
    		{
    			mPDialog.dismiss();
    			mPDialog = null;
    		}
    		mBShow = false;
    	}
    }

	public static void messageBox(int nStringID) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(nStringID).setPositiveButton(R.string.STR_OK, null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void messageBox(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(message).setPositiveButton(R.string.STR_OK, null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void messageBox(String message, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setPositiveButton(R.string.STR_OK, null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void messageBoxFromWorkerThread(final String message) {
		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				messageBox(message);
			}
		});
	}

	public static void editMessageBox(int nTitleStringID, Context context, final OnEditMessageBoxListener listener) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View editMessageboxView = inflater.inflate(R.layout.edit_messagebox, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(nTitleStringID);
		builder.setView(editMessageboxView);
		builder.setPositiveButton(R.string.STR_OK, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText editText = (EditText) (editMessageboxView.findViewById(R.id.ed_result));
				if(listener != null)
					listener.onResult(editText.getText().toString());
			}
		});
		builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null)
					listener.onCancel();
			}
		});
		builder.create().show();
	}
}
