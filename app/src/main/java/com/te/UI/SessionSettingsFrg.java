package com.te.UI;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

import Terminals.CipherConnectSettingInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SessionSettingsFrg extends BaseFragment {
    //constanct
    final String IP_SEP = "\\.";
    //Data members
    private int mNIsTN = 1;
    private String mTN5250HostTypeName = "";
    private String mTN3270HostTypeName = "";
    private String mVT100HostTypeName = "";
    private String mVT102HostTypeName = "";
    private String mVT220HostTypeName = "";
    private String mVTAnsiHostTypeName = "";
    private String mTNHostTypeName = "";
    private String mHostTypeName = "";
    private String mIpAddress = "";
    private EditText [] mEdIp = new EditText[4];
    private EditText mEdPort;
    private String mPort = "";
    private CheckBox mCkNetworkAlive = null;
    private boolean mBNetworkAlive = false;
    private CheckBox mCkUpperCase = null;
    private boolean mBUpperCase = false;
    private CheckBox mCkNetwork = null;
    private boolean mBNetwork = false;
    private CheckBox mCkAutoFullScreen = null;
    private boolean mBAutoFullScreen = false;
    private CheckBox mCkDetectOut = null;
    private boolean mBDetectOut = false;
    private CheckBox mCkAutoConnect = null;
    private boolean mBAutoConnect = false;
    private LinearLayout mAutoSignOnLayout = null;
    private CheckBox mCkAutoSignOn = null;
    private boolean mBAutoSignOn = false;
    private LinearLayout mAutoSignOnNameLayout = null;
    private String mstrAutoSignOnName = "";
    private EditText mEdAutoSignOnName = null;
    private LinearLayout mAutoSignOnPwdLayout = null;
    private String mstrAutoSignOnPwd = "";
    private EditText mEdAutoSignOnPwd = null;
    private CheckBox mCkGenLog = null;
    private boolean mBGenLog = false;
    
	public SessionSettingsFrg() {
        // Required empty public constructor
    }
	
	private void setEnableAll(ViewGroup group, boolean bEnable) {
	    for (int idxChild = 0; idxChild < group.getChildCount(); idxChild++) {
	        View child = group.getChildAt(idxChild);
	        child.setEnabled(bEnable);
	    }
	    group.setEnabled(bEnable);
	}
	
	private void updateAutoSignonUI(boolean bAutoSignon) {
        setEnableAll(mAutoSignOnNameLayout, bAutoSignon);
        setEnableAll(mAutoSignOnPwdLayout, bAutoSignon);
	}
	
	private void updateAutoConnectUI(boolean bAutoConnect) {
        if(bAutoConnect == false) {
            setEnableAll(mAutoSignOnLayout, false);
            updateAutoSignonUI(false);
        } else {
            setEnableAll(mAutoSignOnLayout, true);
            updateAutoSignonUI(mBAutoSignOn);
        }
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
        
        //Initialize settings from json
        mNIsTN = CipherConnectSettingInfo.getHostTypeByIndex(CipherConnectSettingInfo.GetSessionIndex());
        mTNHostTypeName = CipherConnectSettingInfo.getTNHostTypeNameByIndex(CipherConnectSettingInfo.GetSessionIndex());
        mHostTypeName = CipherConnectSettingInfo.getHostTypeNameByIndex(CipherConnectSettingInfo.GetSessionIndex());
        mIpAddress = CipherConnectSettingInfo.getHostAddrByIndex(CipherConnectSettingInfo.GetSessionIndex());
        mPort = CipherConnectSettingInfo.GetHostPortByIndex(CipherConnectSettingInfo.GetSessionIndex());
        //Todo: mBCkNetworkAlive =  from jsaon
        //Todo: mBUpperCase =  from jsaon
        //Todo: mBNetwork =  from jsaon
        //Todo: mBAutoFullScreen =  from jsaon   
        mBAutoConnect = CipherConnectSettingInfo.getHostIsAutoconnectByIndex(CipherConnectSettingInfo.GetSessionIndex());
        mBAutoSignOn = CipherConnectSettingInfo.getHostIsAutoSignByIndex(CipherConnectSettingInfo.GetSessionIndex());
        //Todo:from jsaon  
        mstrAutoSignOnName = "Test Name";
        mstrAutoSignOnPwd = "Test pwd";
        mBGenLog = CipherConnectSettingInfo.getHostIsWriteLogkByIndex(CipherConnectSettingInfo.GetSessionIndex());
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

    @Override
    void commitUpdate() {
        //Server type
        CipherConnectSettingInfo.SetHostTypeByIndex(CipherConnectSettingInfo.GetSessionIndex(), mNIsTN);
        CipherConnectSettingInfo.SetTNHostTypeNameByIndex(CipherConnectSettingInfo.GetSessionIndex(), mTNHostTypeName);
        CipherConnectSettingInfo.SetHostTypeNameByIndex(CipherConnectSettingInfo.GetSessionIndex(), mHostTypeName);
        
        //Ip address
        mIpAddress = String.format("%s.%s.%s.%s", mEdIp[0].getText(), mEdIp[1].getText(), mEdIp[2].getText(), mEdIp[3].getText());
        CipherConnectSettingInfo.SetHostAddrByIndex(CipherConnectSettingInfo.GetSessionIndex(), mIpAddress);
        
        //Port
        mPort = String.valueOf(mEdPort.getText());
        CipherConnectSettingInfo.SetHostPortByIndex(CipherConnectSettingInfo.GetSessionIndex(), mPort);
        
        //NetWork alive
        mBNetworkAlive = mCkNetworkAlive.isChecked();
        //Todo:save to jason
        
        //Data uppercase
        mBUpperCase = mCkUpperCase.isChecked();
        //Todo:save to jason
        
        //Network
        mBNetwork = mCkNetwork.isChecked();
        //Todo:save to jason
        
        //Auto full screen on connection
        mBAutoFullScreen = mCkAutoFullScreen.isChecked();
        //Todo:save to jason
        
        //Detect out of range
        mBDetectOut = mCkDetectOut.isChecked();
        //Todo:save to jason
        
        mBAutoConnect = mCkAutoConnect.isChecked();
        CipherConnectSettingInfo.SetHostIsAutoconnectByIndex(CipherConnectSettingInfo.GetSessionIndex(), mBAutoConnect);
        
        mBAutoSignOn = mCkAutoSignOn.isChecked();
        CipherConnectSettingInfo.SetHostIsAutoSignByIndex(CipherConnectSettingInfo.GetSessionIndex(), mBAutoSignOn);
        
        mstrAutoSignOnName = mEdAutoSignOnName.getText().toString(); 
        //Todo:save to jason
        
        mstrAutoSignOnPwd = mEdAutoSignOnPwd.getText().toString(); 
        //Todo:save to jason
        
        mBGenLog = mCkGenLog.isChecked();
        CipherConnectSettingInfo.SetHostIsWriteLogByIndex(CipherConnectSettingInfo.GetSessionIndex(), mBGenLog);
    }
}
