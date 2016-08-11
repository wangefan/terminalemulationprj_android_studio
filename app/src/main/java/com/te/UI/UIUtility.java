package com.te.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.terminalemulation.R;

import java.io.UnsupportedEncodingException;

import Terminals.TESettingsInfo;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class UIUtility {
	public interface OnLoadSettingProcListener {
		void onLoadResult(boolean bSuccess);
	}
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
	public interface OnAccessCtrlChkListener {
		void onValid();
	}
	public interface OnActivationListener {
		void onResult(boolean bActivate);
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

	public static void doActivationDialog(final OnActivationListener listener) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View activationView = inflater.inflate(R.layout.activation, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.str_activation_key);
		builder.setView(activationView);
		builder.setPositiveButton(R.string.STR_Activate, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText editKey = (EditText) (activationView.findViewById(R.id.ed_key));
				String inputKey = editKey.getText().toString();
				boolean bAct = ActivateKeyUtility.getInstance().verifyKey(inputKey);
				if(listener != null) {
					listener.onResult(bAct);
				}
			}
		});
		builder.setNegativeButton(R.string.STR_Cancel, null);
		builder.create().show();
	}

	public static void doAccessCtrlDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View accessCtrlDialog = inflater.inflate(R.layout.access_control, null);
		final EditText edPwd1 = (EditText) (accessCtrlDialog.findViewById(R.id.ed_pwd1));
		final EditText edPwd2 = (EditText) (accessCtrlDialog.findViewById(R.id.ed_pwd2));
		final CheckBox ckIsSettingsProct = (CheckBox) accessCtrlDialog.findViewById(R.id.id_protect_item_settings);
		ckIsSettingsProct.setChecked(TESettingsInfo.getIsSettingsProtect());
		accessCtrlDialog.findViewById(R.id.id_lay_item_settings).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ckIsSettingsProct.setChecked(!ckIsSettingsProct.isChecked());
			}
		});
		final CheckBox ckIsExitProct = (CheckBox) accessCtrlDialog.findViewById(R.id.id_protect_item_exit);
		ckIsExitProct.setChecked(TESettingsInfo.getIsExitProtect());
		accessCtrlDialog.findViewById(R.id.id_lay_item_exit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ckIsExitProct.setChecked(!ckIsExitProct.isChecked());
			}
		});
		final CheckBox ckIsExitFullProct = (CheckBox) accessCtrlDialog.findViewById(R.id.id_protect_item_exit_full_screen);
		ckIsExitFullProct.setChecked(TESettingsInfo.getIsExitFullScreenProtect());
		accessCtrlDialog.findViewById(R.id.id_lay_item_exit_full_screen).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ckIsExitFullProct.setChecked(!ckIsExitFullProct.isChecked());
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.str_access_ctrl);
		builder.setView(accessCtrlDialog);
		builder.setPositiveButton(R.string.STR_Confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TESettingsInfo.setAccessCtrlProtect(true);
				TESettingsInfo.setSettingsProtect(ckIsSettingsProct.isChecked());
				TESettingsInfo.setExitProtect(ckIsExitProct.isChecked());
				TESettingsInfo.setExitFullScreenProtect(ckIsExitFullProct.isChecked());
				TESettingsInfo.setAccessCtrlProtectedPassword(edPwd2.getText().toString());
			}
		});
		builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		final Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		btnPositive.setEnabled(false);
		edPwd1.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String confirm = edPwd2.getText().toString();
				if(confirm.length() > 0 && confirm.compareTo(s.toString()) == 0) {
					btnPositive.setEnabled(true);
				} else {
					btnPositive.setEnabled(false);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		edPwd2.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String ori = edPwd1.getText().toString();
				if(ori.length() > 0 && ori.compareTo(s.toString()) == 0) {
					btnPositive.setEnabled(true);
				} else {
					btnPositive.setEnabled(false);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	public static void doCheckAccessCtrlDialog(final OnAccessCtrlChkListener listener) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View accessChkCtrlDialog = inflater.inflate(R.layout.access_control_check, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.str_access_ctrl_check);
		builder.setView(accessChkCtrlDialog);
		builder.setPositiveButton(R.string.STR_pass, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onValid();
			}
		});
		builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		final Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		btnPositive.setEnabled(false);
		final EditText check_pwd = (EditText) (accessChkCtrlDialog.findViewById(R.id.check_pwd));
		check_pwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String password = TESettingsInfo.getAccessCtrlProtectedPassword();
				if(password.length() > 0 && password.compareTo(s.toString()) == 0) {
					btnPositive.setEnabled(true);
				} else {
					btnPositive.setEnabled(false);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
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

	public static void doLoadSettingProc(final Context context, final OnLoadSettingProcListener listener) {
		//ask user to try to clear setting, or exit app
		if(TESettingsInfo.loadSessionSettings(context) == false) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							TESettingsInfo.deleteJsonFile();
							listener.onLoadResult(TESettingsInfo.loadSessionSettings(context));
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							listener.onLoadResult(false);
							break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(R.string.MSG_clear_setting).setPositiveButton(R.string.str_positive, dialogClickListener)
					.setNegativeButton(R.string.str_negative, dialogClickListener);
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			listener.onLoadResult(true);
		}
	}
}
