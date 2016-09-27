package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;

import com.cipherlab.terminalemulation.R;

public class SessionSSHFrg extends SessionSettingsFrgBase {
    private TESwitchPreference mSSH = null;
    private PreferenceCategory mAuthCate = null;
    private CheckBoxPreference mSSHLog = null;

    public SessionSSHFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh);

        mSSH = (TESwitchPreference) findPreference(getResources().getString(R.string.ssh_key));
        mAuthCate = (PreferenceCategory) findPreference(getResources().getString(R.string.ssh_auth_cate_key));
        mSSHLog = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_log_key));
        mSSHLog.setEnabled(mSetting.mUseSSH);
        mAuthCate.setEnabled(mSetting.mUseSSH);
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mSSH.setChecked(mSetting.mUseSSH);
        mSSHLog.setChecked(mSetting.mSaveSSHLog);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.ssh_key)) == 0) {
            mSetting.mUseSSH = mSSH.isChecked();
            mSSHLog.setEnabled(mSetting.mUseSSH);
            mAuthCate.setEnabled(mSetting.mUseSSH);
        } else if(key.compareTo(getResources().getString(R.string.ssh_log_key)) == 0) {
            mSetting.mSaveSSHLog = mSSHLog.isChecked();
        }
    }
}
