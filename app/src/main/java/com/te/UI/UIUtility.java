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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cipherlab.terminalemulation.R;

import java.io.UnsupportedEncodingException;

import Terminals.TESettingsInfo;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class UIUtility {
	public static void showSIP(Context context, View view) {
		((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

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
	public interface OnSSHEditPassphraseListener {
		void onDone(final String pass1, final String pass2);
	}

	//nResult: 0 => portrait, 1 => landscape, 2=> user define
	public interface OnScreenOrientationListener {
		void onResult(int nResult);
	}
	static private ProgressDialog mPDialog = null;
	static private ProgressDialog mProgNetwork = null;
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
	static CheckBox mCkIsSetScreenOritProct = null;
	static CheckBox mCkIsSettingsProct = null;
	static CheckBox mCkIsExitProct = null;
	static CheckBox mCkIsExitFullProct = null;
	static Button mBtnPositive = null;
	static boolean mIsDirty = false;
	//Access control dialog end
	
	//member functions
	static public void init() {
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

	static public void showProgressDlg(Context context, boolean bShow, int messageID)
    {
    	if(bShow && mBShow == false)
    	{
    		mPDialog = new ProgressDialog(context, R.style.MyProgressDlg);
    		mPDialog.setCancelable(false); 
    		mPDialog.setMessage(context.getText(messageID));
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

	public static void messageBox(Context context, int nStringID, DialogInterface.OnClickListener positiveClkListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(nStringID).setPositiveButton(R.string.STR_OK, positiveClkListener);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void messageBox(Context context, String message, DialogInterface.OnClickListener positiveClkListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setPositiveButton(R.string.STR_OK, positiveClkListener);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void doYesNoDialog(Context context, String strMsg, DialogInterface.OnClickListener dialogClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(strMsg).setPositiveButton(R.string.STR_OK, dialogClickListener)
		.setNegativeButton(R.string.STR_Cancel, dialogClickListener);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void messageBoxFromWorkerThread(final Context context, final String message, final DialogInterface.OnClickListener positiveClkListener) {
		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				messageBox(context, message, positiveClkListener);
			}
		});
	}

	public static void editMessageBox(int nTitleStringID, String content, Context context, final OnEditMessageBoxListener listener) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View editMessageboxView = inflater.inflate(R.layout.edit_messagebox, null);
		final EditText etContent = (EditText) editMessageboxView.findViewById(R.id.ed_result);
		etContent.setText(content);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(nTitleStringID);
		builder.setView(editMessageboxView);
		builder.setPositiveButton(R.string.STR_OK, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(listener != null)
					listener.onResult(etContent.getText().toString());
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

	public static void listMessageBox(int nTitleStringID, final String [] items, int nCheckedItem, final Context context, final OnListMessageBoxListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(nTitleStringID);
		builder.setSingleChoiceItems(items, nCheckedItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(items != null) {
					String result = items[which];
					listener.onSelResult(result, which);
					dialog.dismiss();
				}
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

	public static void doActivationDialog(Context context, final OnActivationListener listener) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View activationView = inflater.inflate(R.layout.activation, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

	//nCurMode: 0 => portrait, 1 => landscape, 2=> user define
	public static void doScreenOrientationDlg(final Context context, int nCurMode, final OnScreenOrientationListener listener) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View screenOrientationDlg = inflater.inflate(R.layout.screen_orientation, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.screen_orientation_title);
		builder.setView(screenOrientationDlg);
		final ImageView ivScreenMoode = (ImageView) screenOrientationDlg.findViewById(R.id.id_screen_orientation);
		final TextView tvScreenMoode = (TextView) screenOrientationDlg.findViewById(R.id.id_screen_orientation_text);
		final RelativeLayout layScreenOrit = (RelativeLayout) screenOrientationDlg.findViewById(R.id.id_lay_screen_orit);
		layScreenOrit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String strCurScreenMode = tvScreenMoode.getText().toString();
				if(strCurScreenMode.compareTo(context.getString(R.string.screen_orientation_lk_portrait)) == 0) {
					ivScreenMoode.setImageResource(R.drawable.lock_landscape);
					tvScreenMoode.setText(R.string.screen_orientation_lk_landscape);
				} else if(strCurScreenMode.compareTo(context.getString(R.string.screen_orientation_lk_landscape)) == 0) {
					ivScreenMoode.setImageResource(R.drawable.sc_user);
					tvScreenMoode.setText(R.string.screen_orientation_user);
				} else if(strCurScreenMode.compareTo(context.getString(R.string.screen_orientation_user)) == 0) {
					ivScreenMoode.setImageResource(R.drawable.lock_portrait);
					tvScreenMoode.setText(R.string.screen_orientation_lk_portrait);
				}
			}
		});
		switch (nCurMode) {
			case 0://portrait
			default:
				ivScreenMoode.setImageResource(R.drawable.lock_portrait);
				tvScreenMoode.setText(R.string.screen_orientation_lk_portrait);
				break;
			case 1://landscape
				ivScreenMoode.setImageResource(R.drawable.lock_landscape);
				tvScreenMoode.setText(R.string.screen_orientation_lk_landscape);
				break;
			case 2://user defined
				ivScreenMoode.setImageResource(R.drawable.sc_user);
				tvScreenMoode.setText(R.string.screen_orientation_user);
				break;
		}

		builder.setPositiveButton(R.string.strScreenOrientation_apply, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String selMode = tvScreenMoode.getText().toString();
				if(selMode.compareTo(context.getString(R.string.screen_orientation_lk_portrait)) == 0) {
					listener.onResult(0);
				} else if(selMode.compareTo(context.getString(R.string.screen_orientation_lk_landscape)) == 0) {
					listener.onResult(1);
				} else if(selMode.compareTo(context.getString(R.string.screen_orientation_user)) == 0) {
					listener.onResult(2);
				}
			}
		});
		builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void doSSHConfirmPassphrase(Context context, final OnSSHEditPassphraseListener listener) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View sshPassphraseDialog = inflater.inflate(R.layout.ssh_edit_key_passphrase_, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.ssh_add_key_files_edit_pha_title);
		builder.setView(sshPassphraseDialog);
		final EditText txPass1 = (EditText) sshPassphraseDialog.findViewById(R.id.id_ssh_add_key_files_edit_pha_1);
		final EditText txPass2 = (EditText) sshPassphraseDialog.findViewById(R.id.id_ssh_add_key_files_edit_pha_2);
		builder.setPositiveButton(R.string.ssh_add_key_files_done, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onDone(txPass1.getText().toString(), txPass2.getText().toString());
			}
		});
		builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void doAccessCtrlDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
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

		mCkIsSetScreenOritProct = (CheckBox) mAccessCtrlDialog.findViewById(R.id.id_protect_item_set_screen);
		mCkIsSetScreenOritProct.setClickable(false);
		mAccessCtrlDialog.findViewById(R.id.id_lay_item_set_screen).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsDirty = true;
				mCkIsSetScreenOritProct.setChecked(!mCkIsSetScreenOritProct.isChecked());
				updatePostiveBtn();
			}
		});

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
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.str_access_ctrl);
		builder.setView(mAccessCtrlDialog);
		builder.setPositiveButton(R.string.STR_Confirm, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TESettingsInfo.setAccessCtrlProtect(mSwSetPwd.isChecked());
				TESettingsInfo.setScreenOritProtect(mCkIsSetScreenOritProct.isChecked());
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
					mCkIsSetScreenOritProct.setChecked(false);
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

		mCkIsSetScreenOritProct.setChecked(TESettingsInfo.getScreenOritProtect());
		mCkIsSettingsProct.setChecked(TESettingsInfo.getIsSettingsProtect());
		mCkIsExitProct.setChecked(TESettingsInfo.getIsExitProtect());
		mCkIsExitFullProct.setChecked(TESettingsInfo.getIsExitFullScreenProtect());
		CipherUtility.enableAllChild(mLayAllSettings, mSwSetPwd.isChecked());
		mIsDirty = false;
		updatePostiveBtn();
	}

	public static void doCheckAccessCtrlDialog(Context context, final OnAccessCtrlChkListener listener) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View accessChkCtrlDialog = inflater.inflate(R.layout.access_control_check, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

	public static void showTourEditProfile(Context context, View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(context.getString(R.string.msg_tour_edit_profile_title)).
				setDescription(context.getString(R.string.msg_tour_edit_profile)).
				setBackgroundColor(context.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(context.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) context)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void showAddSession(Context context, View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(context.getString(R.string.msg_tour_add_session_title)).
				setDescription(context.getString(R.string.msg_tour_add_session)).
				setBackgroundColor(context.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(context.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) context)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void showDelSession(Context context, View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(context.getString(R.string.msg_tour_del_session_title)).
				setDescription(context.getString(R.string.msg_tour_del_session)).
				setBackgroundColor(context.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(context.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) context)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void showResetFullScreen(Context context, View targetView) {
		ToolTip toolTip = new ToolTip().
				setTitle(context.getString(R.string.msg_tour_screen_title)).
				setDescription(context.getString(R.string.msg_tour_screen)).
				setBackgroundColor(context.getResources().getColor(R.color.tooltipsColor)).
				setTextColor(context.getResources().getColor(R.color.tooltipsColor)).
				setShadow(true).
				setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTourGuideHandler.cleanUp();
					}
				});
		mTourGuideHandler = TourGuide.init((Activity) context)
				.setToolTip(toolTip)
				.setOverlay(new Overlay().disableClickThroughHole(true))
				.playOn(targetView);
	}

	public static void detectNetworkOutRange(final Context context, final OnDetectOFRListener listener) {
		if(CipherUtility.hasNetwork(context) == true) {
			listener.onResult(true);
			return;
		}

		final Runnable checkNetwork = new Runnable() {
			@Override
			public void run() {
				if(CipherUtility.hasNetwork(context) == true) {
					mProgNetwork.dismiss();
					listener.onResult(true);
				} else {
					mUIHandler.postDelayed(this, 2000);
				}
			}
		};
		mProgNetwork = new ProgressDialog(context);
		mProgNetwork.setMessage(context.getResources().getString(R.string.str_detect_out_title));
		mProgNetwork.setCancelable(false);
		mProgNetwork.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.STR_Cancel), new DialogInterface.OnClickListener() {
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
		String result = TESettingsInfo.loadSessionSettings(context);
		if(result.length() > 0) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							TESettingsInfo.deleteJsonFile(context);
							listener.onLoadResult(TESettingsInfo.loadSessionSettings(context).isEmpty());
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							listener.onLoadResult(false);
							break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(String.format(context.getString(R.string.MSG_clear_setting), result)).setPositiveButton(R.string.str_positive, dialogClickListener)
					.setNegativeButton(R.string.str_negative, dialogClickListener);
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			listener.onLoadResult(true);
		}
	}
}
