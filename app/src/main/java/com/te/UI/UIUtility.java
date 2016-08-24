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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cipherlab.terminalemulation.R;

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
		void onSelResult(String result, int selIndex);
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

	//Access control dialog begin
	static View mAccessCtrlDialog = null;
	static LinearLayout mLayPassword = null;
	static Switch mSwSetPwd = null;
	static EditText mEdPwd1 = null;
	static EditText mEdPwd2 = null;
	static TextView mTVNotMatch = null;
	static LinearLayout mLayAllSettings = null;
	static CheckBox mCkIsSettingsProct = null;
	static CheckBox mCkIsExitProct = null;
	static CheckBox mCkIsExitFullProct = null;
	static Button mBtnPositive = null;
	static boolean mIsDirty = false;
	//Access control dialog end
	
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
					listener.onSelResult(result, which);
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

	private static void updateAllPwdsEditUI(boolean bEnable) {
		if(bEnable) {
			CipherUtility.enableAllChild(mLayPassword, true);
			if(mEdPwd1.getText().length() <= 0) {
				mEdPwd2.setEnabled(false);
			}
		} else {
			CipherUtility.enableAllChild(mLayPassword, false);
		}
	}

	private static void updatePostiveBtn() {
		boolean bEnable = false;
		if(mIsDirty) {
			if(mSwSetPwd.isChecked() == true) {
				String strPwd1 = mEdPwd1.getText().toString();
				String strPwd2 = mEdPwd2.getText().toString();
				if(strPwd1.length() > 0 &&
						strPwd2.length() > 0 &&
						strPwd1.compareTo(strPwd2) == 0) {
					bEnable = true;
				}
			} else {
				bEnable = true;
			}
		}
		mBtnPositive.setEnabled(bEnable);
	}

	public static void doAccessCtrlDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mAccessCtrlDialog = inflater.inflate(R.layout.access_control, null);
		mLayPassword = (LinearLayout) (mAccessCtrlDialog.findViewById(R.id.password_lay));
		mSwSetPwd = (Switch) (mAccessCtrlDialog.findViewById(R.id.mod_pwd_switch));
		mAccessCtrlDialog.findViewById(R.id.set_password).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsDirty = true;
				mSwSetPwd.setChecked(!mSwSetPwd.isChecked());
				updateAllPwdsEditUI(mSwSetPwd.isChecked());
				CipherUtility.enableAllChild(mLayAllSettings, mSwSetPwd.isChecked());
				updatePostiveBtn();
			}
		});
		mEdPwd1 = (EditText) mAccessCtrlDialog.findViewById(R.id.ed_pwd1);
		mEdPwd1.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mIsDirty = true;
				updateAllPwdsEditUI(true);
				updatePostiveBtn();
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mEdPwd1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					mEdPwd1.selectAll();
				}
			}
		});
		mEdPwd2 = (EditText) mAccessCtrlDialog.findViewById(R.id.ed_pwd2);
		mEdPwd2.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mIsDirty = true;
				if(s.length() > mEdPwd1.length()) {
					mTVNotMatch.setVisibility(View.VISIBLE);
				} else
				if(s.length() == mEdPwd1.length() && mEdPwd1.getText().toString().compareTo(s.toString()) != 0) {
					mTVNotMatch.setVisibility(View.VISIBLE);
				} else {
					mTVNotMatch.setVisibility(View.GONE);
				}
				updatePostiveBtn();
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		mTVNotMatch = (TextView) mAccessCtrlDialog.findViewById(R.id.id_msg_not_match);
		mTVNotMatch.setVisibility(View.GONE);
		mLayAllSettings = (LinearLayout) mAccessCtrlDialog.findViewById(R.id.all_settings_lay);
		mCkIsSettingsProct = (CheckBox) mAccessCtrlDialog.findViewById(R.id.id_protect_item_settings);
		mCkIsSettingsProct.setClickable(false);
		mAccessCtrlDialog.findViewById(R.id.id_lay_item_settings).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsDirty = true;
				mCkIsSettingsProct.setChecked(!mCkIsSettingsProct.isChecked());
				updatePostiveBtn();
			}
		});
		mCkIsExitProct = (CheckBox) mAccessCtrlDialog.findViewById(R.id.id_protect_item_exit);
		mCkIsExitProct.setClickable(false);
		mAccessCtrlDialog.findViewById(R.id.id_lay_item_exit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsDirty = true;
				mCkIsExitProct.setChecked(!mCkIsExitProct.isChecked());
				updatePostiveBtn();
			}
		});
		mCkIsExitFullProct = (CheckBox) mAccessCtrlDialog.findViewById(R.id.id_protect_item_exit_full_screen);
		mCkIsExitFullProct.setClickable(false);
		mAccessCtrlDialog.findViewById(R.id.id_lay_item_exit_full_screen).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsDirty = true;
				mCkIsExitFullProct.setChecked(!mCkIsExitFullProct.isChecked());
				updatePostiveBtn();
			}
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.str_access_ctrl);
		builder.setView(mAccessCtrlDialog);
		builder.setPositiveButton(R.string.STR_Confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TESettingsInfo.setAccessCtrlProtect(mSwSetPwd.isChecked());
				TESettingsInfo.setSettingsProtect(mCkIsSettingsProct.isChecked());
				TESettingsInfo.setExitProtect(mCkIsExitProct.isChecked());
				TESettingsInfo.setExitFullScreenProtect(mCkIsExitFullProct.isChecked());
				TESettingsInfo.setAccessCtrlProtectedPassword(mEdPwd2.getText().toString());
				mIsDirty = false;
			}
		});
		builder.setNeutralButton(R.string.STR_Reset, null);

		builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		mBtnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Button btnReset = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
		if(btnReset != null) {
			btnReset.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mIsDirty = true;
					mSwSetPwd.setChecked(false);
					mEdPwd1.setText("");
					mEdPwd2.setText("");
					CipherUtility.enableAllChild(mLayPassword, false);
					mCkIsSettingsProct.setChecked(false);
					mCkIsExitProct.setChecked(false);
					mCkIsExitFullProct.setChecked(false);
					CipherUtility.enableAllChild(mLayAllSettings, false);
					updatePostiveBtn();
				}
			});
		}

		mSwSetPwd.setChecked(TESettingsInfo.getIsAccessCtrlProtected());
		mEdPwd1.setText(TESettingsInfo.getAccessCtrlProtectedPassword());
		mEdPwd2.setText(TESettingsInfo.getAccessCtrlProtectedPassword());
		updateAllPwdsEditUI(mSwSetPwd.isChecked());

		mCkIsSettingsProct.setChecked(TESettingsInfo.getIsSettingsProtect());
		mCkIsExitProct.setChecked(TESettingsInfo.getIsExitProtect());
		mCkIsExitFullProct.setChecked(TESettingsInfo.getIsExitFullScreenProtect());
		CipherUtility.enableAllChild(mLayAllSettings, mSwSetPwd.isChecked());
		mIsDirty = false;
		updatePostiveBtn();
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
