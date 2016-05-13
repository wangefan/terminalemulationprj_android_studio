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

import Terminals.CipherConnectSettingInfo;

public class UIUtility {
	static private ProgressDialog mPDialog = null;
	static private Context mContext;
	static private Point mPrgDlgSize = new Point();
	static private boolean mBShow = false;
	static private Handler mUIHandler = null;
	static private View mEditMessageboxView = null;
	static private String mEditMessageboxResult = null;
	
	//member functions
	static public void init(Context context) {
		mContext = context;
		mUIHandler = new Handler();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mEditMessageboxView = inflater.inflate(R.layout.edit_messagebox, null);
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

	//return null means user cancel.
	public static String editMessageBox(int nTitleStringID, Context context) {
		mEditMessageboxResult = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(nTitleStringID);
		builder.setView(mEditMessageboxView);
		builder.setPositiveButton(R.string.STR_OK, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText editText = (EditText) (mEditMessageboxView.findViewById(R.id.ed_result));
				mEditMessageboxResult = editText.getText().toString();
			}
		});
		builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
		return mEditMessageboxResult;
	}
}
