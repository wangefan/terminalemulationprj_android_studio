package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import com.cipherlab.terminalemulation.R;

public class SessionSSHFrg extends SessionSettingsFrgBase {
    private TESwitchPreference mSSH = null;
    private PreferenceCategory mAuthCate = null;
    private CheckBoxPreference mSSHLog = null;
    private Preference mAuthSettings = null;
    private ListPreference mAuthType = null;

    public SessionSSHFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh);

        mSSH = (TESwitchPreference) findPreference(getResources().getString(R.string.ssh_key));
        mSSHLog = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_log_key));
        mAuthCate = (PreferenceCategory) findPreference(getResources().getString(R.string.ssh_auth_cate_key));
        mAuthType = (ListPreference) findPreference(getResources().getString(R.string.ssh_auth_type_key));
        mAuthSettings = findPreference(getResources().getString(R.string.ssh_auth_setting_key));
        mSSHLog.setEnabled(mSetting.mUseSSH);
        mAuthCate.setEnabled(mSetting.mUseSSH);
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mSSH.setChecked(mSetting.mUseSSH);
        mSSHLog.setChecked(mSetting.mSaveSSHLog);
        if(mSetting.mAuType) {
            mAuthType.setValue(String.valueOf(1));
        } else {
            mAuthType.setValue(String.valueOf(0));
        }
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.ssh_key)) == 0) {
            mSetting.mUseSSH = mSSH.isChecked();
            mSSHLog.setEnabled(mSetting.mUseSSH);
            mAuthCate.setEnabled(mSetting.mUseSSH);
        } else if(key.compareTo(getResources().getString(R.string.ssh_log_key)) == 0) {
            mSetting.mSaveSSHLog = mSSHLog.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_auth_type_key)) == 0) {
            int nAuthType = Integer.valueOf(mAuthType.getValue());
            if(nAuthType == 0) { //File
                mSetting.mAuType = false;
            } else {
                mSetting.mAuType = true;
            }
        }
    }
}
