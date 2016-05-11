package com.te.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.WindowManager;

import com.example.terminalemulation.R;

import Terminals.CipherConnectSettingInfo;

public class UIUtility {
	static private ProgressDialog mPDialog = null;
	static private Context mContext;
	static private Point mPrgDlgSize = new Point();
	static private boolean mBShow = false;
	
	//member functions
	static public void init(Context context) {
		mContext = context;
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
}
