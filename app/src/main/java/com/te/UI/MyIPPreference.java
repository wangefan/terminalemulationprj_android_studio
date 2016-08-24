package com.te.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.cipherlab.terminalemulation.R;
import Terminals.TESettingsInfo;

/**
 * Created by wange on 23/4/2016.
 */
public class MyIPPreference extends DialogPreference {
    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private String mStrAddress = "";
    private RelativeLayout mIPCateGory = null;
    private RelativeLayout mHostNameCateGory = null;
    private EditText[] mEdIp = new EditText[4];
    private EditText mEdHostName = null;
    private RadioButton mBtnIP = null;
    private RadioButton mBtnHost = null;
    private Handler mMainThrdHandler = new Handler();

    public MyIPPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_my_ip);
    }

    public void setAddress(String strIp) {
        mStrAddress = strIp;
    }

    public String getAddress() {
        return mStrAddress;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mIPCateGory = (RelativeLayout) view.findViewById(R.id.ip_category);
        mHostNameCateGory = (RelativeLayout) view.findViewById(R.id.host_name_category);
        mBtnIP = (RadioButton) view.findViewById(R.id.ip_radio);
        mBtnIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIPAddressType(true);
                setOKBtnEnabled(true);
                setIPAddressToEditTextControls();
            }
        });
        mEdIp[0] = (EditText) view.findViewById(R.id.ip_1);
        mEdIp[1] = (EditText) view.findViewById(R.id.ip_2);
        mEdIp[2] = (EditText) view.findViewById(R.id.ip_3);
        mEdIp[3] = (EditText) view.findViewById(R.id.ip_4);

        mBtnHost = (RadioButton) view.findViewById(R.id.hostname_radio);
        mBtnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIPAddressType(false);
                setOKBtnEnabled(mEdHostName.getText().length() > 0);
            }
        });
        mEdHostName = (EditText) view.findViewById(R.id.host_name);
        mEdHostName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setOKBtnEnabled(count > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mMainThrdHandler.post(new Runnable() {
            @Override
            public void run() {
                if(isIPFormat(mStrAddress) == false) {
                    setOKBtnEnabled(mEdHostName.length() > 0);
                }
            }
        });

        if(isIPFormat(mStrAddress)) {
            setIPAddressType(true);
            setIPAddressToEditTextControls();
        } else {
            mEdHostName.setText(mStrAddress);
            setIPAddressType(false);
        }
    }

    private void setIPAddressToEditTextControls() {
        final String IP_SEP = "\\.";
        String [] strip = mStrAddress.split(IP_SEP);
        if(strip.length < 4) {
            strip = TESettingsInfo.getDefaultIP().split(IP_SEP);
        }
        IpTextWatcher [] ipWatchers = new IpTextWatcher[4];
        for(int idxIp = 0; idxIp <strip.length; ++idxIp) {
            ipWatchers[idxIp] = new IpTextWatcher(mEdIp[idxIp], mEdIp);
            mEdIp[idxIp].setText(strip[idxIp]);
            mEdIp[idxIp].addTextChangedListener(ipWatchers[idxIp]);
        }
    }

    private boolean isIPFormat(String strAddress) {
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(strAddress);
        return matcher.matches();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if ( which == DialogInterface.BUTTON_POSITIVE ) {
            if(mBtnIP.isChecked()) {
                mStrAddress = String.format("%s.%s.%s.%s", mEdIp[0].getText(), mEdIp[1].getText(), mEdIp[2].getText(), mEdIp[3].getText());
            } else {
                mStrAddress = String.valueOf(mEdHostName.getText());
            }
            persistBoolean(!getPersistedBoolean(true));//here to trigger SharedPreferences.OnSharedPreferenceChangeListener
            callChangeListener(mStrAddress);
        }
    }

    public void setIPAddressType(boolean bIPAddressType) {
        if(bIPAddressType) {
            mBtnIP.setChecked(true);
            CipherUtility.enableAllChild(mIPCateGory, true);
            mBtnHost.setChecked(false);
            CipherUtility.enableAllChild(mHostNameCateGory, false);
        } else {
            mBtnIP.setChecked(false);
            CipherUtility.enableAllChild(mIPCateGory, false);
            mBtnHost.setChecked(true);
            CipherUtility.enableAllChild(mHostNameCateGory, true);
        }
    }

    public void setOKBtnEnabled(boolean bEnabled) {
        AlertDialog dialog = (AlertDialog) getDialog();
        if(dialog == null) {
            return;
        }
        Button btnOK = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if(btnOK == null) {
            return;
        }
        btnOK.setEnabled(bEnabled);
    }
}
