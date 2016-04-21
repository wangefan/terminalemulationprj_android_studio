package com.te.UI;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;
import Terminals.CipherConnectSettingInfo;
import Terminals.TESettings;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SessionSettingsFrg extends PreferenceFragment {
    //constanct
    final String IP_SEP = "\\.";
    //Data members
    private String mTN5250HostTypeName = "";
    private String mTN3270HostTypeName = "";
    private String mVT100HostTypeName = "";
    private String mVT102HostTypeName = "";
    private String mVT220HostTypeName = "";
    private String mVTAnsiHostTypeName = "";
    private TESettings.SessionSetting mSetting = null;
    private EditText [] mEdIp = new EditText[4];
    private EditText mEdPort;
    private CheckBox mCkNetworkAlive = null;
    private CheckBox mCkUpperCase = null;
    private CheckBox mCkNetwork = null;
    private CheckBox mCkAutoFullScreen = null;
    private CheckBox mCkDetectOut = null;
    private CheckBox mCkAutoConnect = null;
    private LinearLayout mAutoSignOnLayout = null;
    private CheckBox mCkAutoSignOn = null;
    private LinearLayout mAutoSignOnNameLayout = null;
    private EditText mEdAutoSignOnName = null;
    private LinearLayout mAutoSignOnPwdLayout = null;
    private EditText mEdAutoSignOnPwd = null;
    private CheckBox mCkGenLog = null;
    
	public SessionSettingsFrg(TESettings.SessionSetting setting) {
        mSetting = setting;
    }
	
	private void setEnableAll(ViewGroup group, boolean bEnable) {
	    for (int idxChild = 0; idxChild < group.getChildCount(); idxChild++) {
	        View child = group.getChildAt(idxChild);
	        child.setEnabled(bEnable);
	    }
	    group.setEnabled(bEnable);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TE", "onCreate");
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.pref_general);
        
        mTN5250HostTypeName = getResources().getString(R.string.IBM5250Val);
        mTN3270HostTypeName = getResources().getString(R.string.IBM3270Val);
        mVT100HostTypeName = getResources().getString(R.string.VT100Val);
        mVT102HostTypeName = getResources().getString(R.string.VT102Val);
        mVT220HostTypeName = getResources().getString(R.string.VT220Val);
        mVTAnsiHostTypeName = getResources().getString(R.string.ANSIVal);
    }

    @Override
    public void onDestroy() {
        if(BuildConfig.DEBUG) Log.d("TE", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if(BuildConfig.DEBUG) Log.d("TE", "onDestroyView");
        super.onDestroyView();
    }
}
