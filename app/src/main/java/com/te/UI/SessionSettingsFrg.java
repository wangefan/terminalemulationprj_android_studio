package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.cipherlab.terminalemulation.R;

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
    private NumberPickerPreference mPrefPort = null;
    private Preference mServerSetting = null;
    private Preference mSSH = null;
    private CheckBoxPreference mCkNetworkAlive = null;
    private CheckBoxPreference mCkDetectOut = null;
    private Preference mReaderconfig = null;
    private CheckBoxPreference mCkGenLog = null;

    public SessionSettingsFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        CipherUtility.Log_d("SessionSettingsFrg.onCreate", "call CipherUtility.isReaderConfigAvable()");
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
        mPrefPort = (NumberPickerPreference) findPreference(getResources().getString(R.string.host_port_key));
        mServerSetting = findPreference(getResources().getString(R.string.server_setting_key));
        mSSH = findPreference(getResources().getString(R.string.ssh_key));
        mCkNetworkAlive = (CheckBoxPreference) findPreference(getResources().getString(R.string.keep_alive_key));
        mCkDetectOut = (CheckBoxPreference) findPreference(getResources().getString(R.string.out_range_key));
        mReaderconfig = findPreference(getResources().getString(R.string.reader_config_key));
        mReaderconfig.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CipherUtility.showReaderConfig(getActivity());
                return true;
            }
        });
        mReaderconfig.setEnabled(CipherUtility.isReaderConfigAvable(getActivity()));
        mCkGenLog = (CheckBoxPreference) findPreference(getResources().getString(R.string.log_key));
    }

    @Override
    public void syncPrefUIFromTESettings() {
        if(mSetting.mIsTN == 0) {
            mLstServerType.setValue(mSetting.mTermName);
            mServerSetting.setTitle(R.string.vt_setting);
            mSSH.setEnabled(true);
        } else {
            mLstServerType.setValue(mSetting.mTermNameTN);
            mServerSetting.setTitle(R.string.tn_setting);
            mSSH.setEnabled(false);
        }
        mLstServerIp.setAddress(mSetting.mHostIP);
        mPrefPort.setPort(mSetting.getHostPort());
        mPrefPort.setSummary(mPrefPort.getPort());
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
                mSSH.setEnabled(false);
            } else if(selHostTypeName.compareToIgnoreCase(mVT100HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVT102HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVT220HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVTAnsiHostTypeName) == 0){
                mSetting.mIsTN = 0;
                mServerSetting.setTitle(R.string.vt_setting);
                mSSH.setEnabled(true);
            }

            if(mSetting.mIsTN == 0) {
                mSetting.mTermName = selHostTypeName;
            } else {
                mSetting.mTermNameTN = selHostTypeName;
            }
        } else if(key.compareTo(getResources().getString(R.string.host_ip_key)) == 0) {
            mSetting.mHostIP = mLstServerIp.getAddress();
        } else if(key.compareTo(getResources().getString(R.string.host_port_key)) == 0) {
            mSetting.setHostPort(String.valueOf(mPrefPort.getPort()));
            mPrefPort.setSummary(mPrefPort.getPort());
        } else if(key.compareTo(getResources().getString(R.string.keep_alive_key)) == 0) {
            mSetting.mNetKeepAlive = mCkNetworkAlive.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.out_range_key)) == 0) {
            mSetting.mIsDetectOutRange = mCkDetectOut.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.log_key)) == 0) {
            mSetting.mIsSaveLog = mCkGenLog.isChecked();
        }
    }
}
