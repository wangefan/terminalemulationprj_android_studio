package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;

import com.cipherlab.terminalemulation.R;

public class SessionSSHKeyFileFrg extends SessionSettingsFrgBase {
    private EditTextPreference medtSSHName = null;
    private CheckBoxPreference mchkSSHTcpNoDelay = null;

    public SessionSSHKeyFileFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh_key_file);

        medtSSHName = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_name_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        medtSSHName.setSummary(mSetting.mSSHName);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.ssh_name_key)) == 0) {
            mSetting.mSSHName = medtSSHName.getText();
        }
    }
}
