package com.te.UI;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;

import com.cipherlab.terminalemulation.R;

public class SessionSSHNamePwdFrg extends SessionSettingsFrgBase {
    private EditTextPreference medtSSHName = null;
    private EditTextPreference medtSSHPwd = null;
    public SessionSSHNamePwdFrg() {
    }

    private String getNumStar(String password) {
        if(password != null) {
            StringBuilder sb = new StringBuilder();
            for (int idx = 0; idx < password.length(); idx++) {
                sb.append('*');
            }
            return sb.toString();
        }
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh_name_pwd);

        medtSSHName = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_name_key));
        medtSSHPwd = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_pwd_key));
    }

    @Override
    protected void updatePrefSummary(Preference p) {
        super.updatePrefSummary(p);
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getKey().compareTo((getResources().getString(R.string.ssh_pwd_key))) == 0) {
                p.setSummary(getNumStar(editTextPref.getText()));
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        medtSSHName.setSummary(mSetting.mSSHName);
        medtSSHPwd.setSummary(mSetting.mSSHPassword);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.ssh_name_key)) == 0) {
            mSetting.mSSHName = medtSSHName.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_pwd_key)) == 0) {
            mSetting.mSSHPassword = medtSSHPwd.getText();
        }
    }
}