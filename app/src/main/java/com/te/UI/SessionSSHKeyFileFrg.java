package com.te.UI;

import android.os.Bundle;
import android.preference.EditTextPreference;

import com.cipherlab.terminalemulation.R;

public class SessionSSHKeyFileFrg extends SessionSettingsFrgBase {
    public SessionSSHKeyFileFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh_key_file);
    }

    @Override
    protected void syncPrefUIFromTESettings() {

    }

    @Override
    protected void commitPrefUIToTESettings(String key) {

    }
}
