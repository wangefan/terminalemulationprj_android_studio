package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

public class SessionHostProfileFrg extends SessionSettingsFrgBase {

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
    private CheckBoxPreference mChkAutoConn = null;
    private CheckBoxPreference mChkAutoSign = null;
    private EditTextPreference mPrefLoginName = null;
    private EditTextPreference mPrefLoginPwd = null;
    private EditTextPreference mPrefLoginNameProm = null;
    private EditTextPreference mPrefLoginPwdProm = null;
    private ListPreference mPrefLoginTerm = null;

    public SessionHostProfileFrg() {
    }

    @Override
    protected void updatePrefSummary(Preference p) {
        super.updatePrefSummary(p);
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getKey().compareTo((getResources().getString(R.string.host_auto_sign_pwd_key))) == 0) {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
    }

    private void updatePreferenceForVT() {
        boolean bEnable = false;
        if(mChkAutoConn.isChecked() == false) {
            bEnable = false;
        } else if(mChkAutoSign.isChecked() == false ) {
            bEnable = false;
        } else {
            bEnable = mSetting.mIsTN != 1;
        }
        mPrefLoginNameProm.setEnabled(bEnable);
        mPrefLoginPwdProm.setEnabled(bEnable);
        mPrefLoginTerm.setEnabled(bEnable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TE", "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_host_profile_more);

        mTN5250HostTypeName = getResources().getString(R.string.IBM5250Val);
        mTN3270HostTypeName = getResources().getString(R.string.IBM3270Val);
        mVT100HostTypeName = getResources().getString(R.string.VT100Val);
        mVT102HostTypeName = getResources().getString(R.string.VT102Val);
        mVT220HostTypeName = getResources().getString(R.string.VT220Val);
        mVTAnsiHostTypeName = getResources().getString(R.string.ANSIVal);

        //UI
        mLstServerType = (ListPreference) findPreference(getResources().getString(R.string.host_type_key));
        mLstServerIp = (MyIPPreference) findPreference(getResources().getString(R.string.host_ip_key));
        mPrefPort = (EditTextPreference) findPreference(getResources().getString(R.string.host_port_key));
        mChkAutoConn = (CheckBoxPreference) findPreference(getResources().getString(R.string.host_auto_conn_key));
        mChkAutoSign = (CheckBoxPreference) findPreference(getResources().getString(R.string.host_auto_sign_key));
        mPrefLoginName = (EditTextPreference) findPreference(getResources().getString(R.string.host_auto_sign_name_key));
        mPrefLoginPwd = (EditTextPreference) findPreference(getResources().getString(R.string.host_auto_sign_pwd_key));
        mPrefLoginNameProm = (EditTextPreference) findPreference(getResources().getString(R.string.host_auto_sign_name_prom_key));
        mPrefLoginPwdProm = (EditTextPreference) findPreference(getResources().getString(R.string.host_auto_sign_pwd_prom_key));
        mPrefLoginTerm = (ListPreference) findPreference(getResources().getString(R.string.host_auto_sign_term_prom_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        //Sync settings to UI
        if(mSetting.mIsTN == 0) {
            mLstServerType.setValue(mSetting.mTermName);
        } else {
            mLstServerType.setValue(mSetting.mTermNameTN);
        }
        mLstServerIp.setAddress(mSetting.mHostIP);
        mPrefPort.setText(mSetting.getHostPort());
        mChkAutoConn.setChecked(mSetting.mBAutoConnect);
        mChkAutoSign.setChecked(mSetting.mBAutoSignOn);
        mPrefLoginName.setText(mSetting.mLoginName);
        mPrefLoginPwd.setText(mSetting.mLoginPassword);
        mPrefLoginNameProm.setText(mSetting.mNamePrompt);
        mPrefLoginPwdProm.setText(mSetting.mPassPrompt);
        mPrefLoginTerm.setValue(Integer.toString(mSetting.mTermLogin));
        updatePreferenceForVT();
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
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
            updatePreferenceForVT();
        } else if(key.compareTo(getResources().getString(R.string.host_ip_key)) == 0) {
            mSetting.mHostIP = mLstServerIp.getAddress();
        } else if(key.compareTo(getResources().getString(R.string.host_port_key)) == 0) {
            mSetting.setHostPort(mPrefPort.getText());
        } else if(key.compareTo(getResources().getString(R.string.host_auto_conn_key)) == 0) {
            mSetting.mBAutoConnect = mChkAutoConn.isChecked();
            updatePreferenceForVT();
        } else if(key.compareTo(getResources().getString(R.string.host_auto_sign_key)) == 0) {
            mSetting.mBAutoSignOn = mChkAutoSign.isChecked();
            updatePreferenceForVT();
        } else if(key.compareTo(getResources().getString(R.string.host_auto_sign_name_key)) == 0) {
            mSetting.mLoginName = mPrefLoginName.getText();
        } else if(key.compareTo(getResources().getString(R.string.host_auto_sign_pwd_key)) == 0) {
            mSetting.mLoginPassword = mPrefLoginPwd.getText();
        } else if(key.compareTo(getResources().getString(R.string.host_auto_sign_name_prom_key)) == 0) {
            mSetting.mNamePrompt = mPrefLoginNameProm.getText();
        } else if(key.compareTo(getResources().getString(R.string.host_auto_sign_pwd_prom_key)) == 0) {
            mSetting.mPassPrompt = mPrefLoginPwdProm.getText();
        } else if(key.compareTo(getResources().getString(R.string.host_auto_sign_term_prom_key)) == 0) {
            mSetting.mTermLogin = Integer.valueOf(mPrefLoginTerm.getValue());
        }
    }
}
