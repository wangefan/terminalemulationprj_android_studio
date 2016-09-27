package com.te.UI;

import android.content.Intent;
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
    private ListPreference mAuthType = null;
    private Preference mAuthSettings = null;

    public SessionSSHFrg() {
    }

    private String getNamePwd() {
        String namePwdFormat = getResources().getString(R.string.ssh_auth_setting_format_name_pwd);
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < mSetting.mSSHPassword.length(); idx++) {
            sb.append('*');
        }
        return String.format(namePwdFormat, mSetting.mSSHName, sb.toString());
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
        mAuthSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String str = mAuthType.getValue();
                if(str.compareTo(String.valueOf(0)) == 0) { //File

                } else { //Name/pwd
                    Intent intent = new Intent(getActivity(), Session3rdSettings.class);
                    intent.setAction(Session3rdSettings.ACTION_SSH_NAME_PWD);
                    startActivityForResult(intent, 0);
                }
                return true;
            }
        });
        mSSHLog.setEnabled(mSetting.mUseSSH);
        mAuthCate.setEnabled(mSetting.mUseSSH);
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mSSH.setChecked(mSetting.mUseSSH);
        mSSHLog.setChecked(mSetting.mSaveSSHLog);
        if(mSetting.mAuType) { //Name/pwd
            mAuthType.setValue(String.valueOf(1));
            mAuthSettings.setSummary(getNamePwd());
        } else {    //File
            mAuthType.setValue(String.valueOf(0));
            mAuthSettings.setSummary(mSetting.mSSHKeyPath);
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
                mAuthSettings.setSummary(mSetting.mSSHKeyPath);
            } else {
                mSetting.mAuType = true;
                mAuthSettings.setSummary(getNamePwd());
            }
        }
    }
}
