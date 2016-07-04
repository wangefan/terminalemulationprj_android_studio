package com.te.UI;

import android.app.Activity;
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

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class UIUtility {
	public interface OnEditMessageBoxListener {
		void onResult(String result);
		void onCancel();
	}
	public interface OnListMessageBoxListener {
		void onSelResult(String result);
	}
	public interface OnDetectOFRListener {
		void onResult(boolean bHasNetwork);
	}
	static private ProgressDialog mPDialog = null;
	static private ProgressDialog mProgNetwork = null;
	static private Context mContext;
	static private Point mPrgDlgSize = new Point();
	static private boolean mBShow = false;
	static private Handler mUIHandler = null;
	static private TourGuide mTourGuideHandler;
	
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

	public static void messageBox(int nStringID, DialogInterface.OnClickListener positiveClkListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(nStringID).setPositiveButton(R.string.STR_OK, positiveClkListener);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void messageBox(String message, DialogInterface.OnClickListener positiveClkListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setMessage(message).setPositiveButton(R.string.STR_OK, positiveClkListener);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void messageBoxFromWorkerThread(final String message, final DialogInterface.OnClickListener positiveClkListener) {
		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				messageBox(message, positiveClkListener);
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

	public static void listMessageBox(int nTitleStringID, final int nStingArrayID, int nCheckedItem, final Context context, final OnListMessageBoxListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(nTitleStringID);
		builder.setSingleChoiceItems(nStingArrayID, nCheckedItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String [] items = context.getResources().getStringArray(nStingArrayID);
				if(items != null) {
					String result = items[which];
					listener.onSelResult(result);
					dialog.dismiss();
				}
			}
		});
		builder.create().show();
	}

	public static void showTourEditProfile(View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(mContext.getString(R.string.msg_tour_edit_profile_title)).
				setDescription(mContext.getString(R.string.msg_tour_edit_profile)).
				setBackgroundColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) mContext)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void showAddSession(View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(mContext.getString(R.string.msg_tour_add_session_title)).
				setDescription(mContext.getString(R.string.msg_tour_add_session)).
				setBackgroundColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) mContext)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void showDelSession(View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(mContext.getString(R.string.msg_tour_del_session_title)).
				setDescription(mContext.getString(R.string.msg_tour_del_session)).
				setBackgroundColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) mContext)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void showResetFullScreen(View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(mContext.getString(R.string.msg_tour_screen_title)).
				setDescription(mContext.getString(R.string.msg_tour_screen)).
				setBackgroundColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(mContext.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) mContext)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void detectNetworkOutRange(final OnDetectOFRListener listener) {
		if(CipherUtility.hasNetwork() == true) {
			listener.onResult(true);
			return;
		}

		final Runnable checkNetwork = new Runnable() {
			@Override
			public void run() {
				if(CipherUtility.hasNetwork() == true) {
					mProgNetwork.dismiss();
					listener.onResult(true);
				} else {
					mUIHandler.postDelayed(this, 2000);
				}
			}
		};
		mProgNetwork = new ProgressDialog(mContext);
		mProgNetwork.setMessage(mContext.getResources().getString(R.string.str_detect_out_title));
		mProgNetwork.setCancelable(false);
		mProgNetwork.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.STR_Cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				listener.onResult(false);
				mUIHandler.removeCallbacks(checkNetwork);
			}
		});
		mProgNetwork.show();
		mUIHandler.post(checkNetwork);
	}

	public static void cancelDetectNetworkOutRange() {
		if(mProgNetwork != null && mProgNetwork.isShowing()) {
			mProgNetwork.dismiss();
			mUIHandler.removeCallbacksAndMessages(null);
		}
	}
}
