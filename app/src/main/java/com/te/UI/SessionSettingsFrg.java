package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;
import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

public class SessionSettingsFrg extends SessionSettingsFrgBase {
    //Data members
    private String mTN5250HostTypeName = "";
    private String mTN3270HostTypeName = "";
    private String mVT100HostTypeName = "";
    private String mVT102HostTypeName = "";
    private String mVT220HostTypeName = "";
    private String mVTAnsiHostTypeName = "";
    private ListPreference mLstServerType = null;
    private MyIPPreference mLstServerIp = null;
    private EditTextPreference mPrefPort = null;
    private Preference mServerSetting = null;
    private CheckBoxPreference mCkNetworkAlive = null;
    private CheckBoxPreference mCkDetectOut = null;
    private CheckBoxPreference mCkGenLog = null;

    public SessionSettingsFrg() {
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

        //Server type UI
        mLstServerType = (ListPreference) findPreference(getResources().getString(R.string.host_type_key));
        mLstServerIp = (MyIPPreference) findPreference(getResources().getString(R.string.host_ip_key));
        mPrefPort = (EditTextPreference) findPreference(getResources().getString(R.string.host_port_key));
        mServerSetting = findPreference(getResources().getString(R.string.server_setting_key));
        mCkNetworkAlive = (CheckBoxPreference) findPreference(getResources().getString(R.string.keep_alive_key));
        mCkDetectOut = (CheckBoxPreference) findPreference(getResources().getString(R.string.out_range_key));
        mCkGenLog = (CheckBoxPreference) findPreference(getResources().getString(R.string.log_key));
    }

    @Override
    public void syncPrefUIFromTESettings() {
        if(mSetting.mIsTN == 0) {
            mLstServerType.setValue(mSetting.mTermName);
            mServerSetting.setTitle(R.string.vt_setting);
        } else {
            mLstServerType.setValue(mSetting.mTermNameTN);
            mServerSetting.setTitle(R.string.tn_setting);
        }
        mLstServerIp.setAddress(mSetting.mHostIP);
        mPrefPort.setText(mSetting.getHostPort());
        mCkNetworkAlive.setChecked(mSetting.mNetKeepAlive);
        mCkDetectOut.setChecked(mSetting.mIsDetectOutRange);
        mCkGenLog.setChecked(mSetting.mIsSaveLog);
    }

    @Override
    public void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.host_type_key)) == 0) {
            String selHostTypeName = mLstServerType.getValue();
            if(selHostTypeName.compareToIgnoreCase(mTN5250HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mTN3270HostTypeName) == 0) {
                mSetting.mIsTN = 1;
                mServerSetting.setTitle(R.string.tn_setting);
            } else if(selHostTypeName.compareToIgnoreCase(mVT100HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVT102HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVT220HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVTAnsiHostTypeName) == 0){
                mSetting.mIsTN = 0;
                mServerSetting.setTitle(R.string.vt_setting);
            }

            if(mSetting.mIsTN == 0) {
                mSetting.mTermName = selHostTypeName;
            } else {
                mSetting.mTermNameTN = selHostTypeName;
            }
        } else if(key.compareTo(getResources().getString(R.string.host_ip_key)) == 0) {
            mSetting.mHostIP = mLstServerIp.getAddress();
        } else if(key.compareTo(getResources().getString(R.string.host_port_key)) == 0) {
            mSetting.setHostPort(mPrefPort.getText());
        } else if(key.compareTo(getResources().getString(R.string.keep_alive_key)) == 0) {
            mSetting.mNetKeepAlive = mCkNetworkAlive.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.out_range_key)) == 0) {
            mSetting.mIsDetectOutRange = mCkDetectOut.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.log_key)) == 0) {
            mSetting.mIsSaveLog = mCkGenLog.isChecked();
        }
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
