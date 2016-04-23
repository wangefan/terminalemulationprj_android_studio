package com.te.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.Log;
import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;
import Terminals.TESettings;

public class SessionSettingsFrg extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    //constanct
    final String IP_SEP = "\\.";
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
    private CheckBoxPreference mCkNetworkAlive = null;
    private CheckBoxPreference mCkDetectOut = null;
    private CheckBoxPreference mCkGenLog = null;
    private TESettings.SessionSetting mSetting = null;

    public SessionSettingsFrg() {
    }

    public void setSessionSeting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        } else if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        } else if (p instanceof MyIPPreference) {
            MyIPPreference ipPref = (MyIPPreference) p;
            ipPref.setSummary(ipPref.getIp());
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

        //Server type UI
        mLstServerType = (ListPreference) findPreference(getResources().getString(R.string.host_type_key));
        if(mSetting.mIsTN == 0) {
            mLstServerType.setValue(mSetting.mTermName);
        } else {
            mLstServerType.setValue(mSetting.mTermNameTN);
        }

        mLstServerIp = (MyIPPreference) findPreference(getResources().getString(R.string.host_ip_key));
        mLstServerIp.setIp(mSetting.mHostIP);

        mPrefPort = (EditTextPreference) findPreference(getResources().getString(R.string.host_port_key));
        mPrefPort.setText(mSetting.getHostPort());

        mCkNetworkAlive = (CheckBoxPreference) findPreference(getResources().getString(R.string.keep_alive_key));
        mCkNetworkAlive.setChecked(mSetting.mNetKeepAlive);

        mCkDetectOut = (CheckBoxPreference) findPreference(getResources().getString(R.string.out_range_key));
        mCkDetectOut.setChecked(mSetting.mIsDetectOutRange);

        mCkGenLog = (CheckBoxPreference) findPreference(getResources().getString(R.string.log_key));
        mCkGenLog.setChecked(mSetting.mIsSaveLog);

        initSummary(getPreferenceScreen());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
        if(key.compareTo(getResources().getString(R.string.host_type_key)) == 0) {
            String selHostTypeName = mLstServerType.getValue();
            if(selHostTypeName.compareToIgnoreCase(mTN5250HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mTN3270HostTypeName) == 0) {
                mSetting.mIsTN = 1;
            } else if(selHostTypeName.compareToIgnoreCase(mVT100HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVT102HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVT220HostTypeName) == 0 ||
                    selHostTypeName.compareToIgnoreCase(mVTAnsiHostTypeName) == 0){
                mSetting.mIsTN = 0;
            }

            if(mSetting.mIsTN == 0) {
                mSetting.mTermName = selHostTypeName;
            } else {
                mSetting.mTermNameTN = selHostTypeName;
            }
        } else if(key.compareTo(getResources().getString(R.string.host_ip_key)) == 0) {
            mSetting.mHostIP = mLstServerIp.getIp();
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
}
