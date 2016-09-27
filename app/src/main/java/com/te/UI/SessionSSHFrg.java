package com.te.UI;

import android.os.Bundle;

import com.cipherlab.terminalemulation.R;

public class SessionSSHFrg extends SessionSettingsFrgBase {

    public SessionSSHFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh);
    }

    @Override
    protected void syncPrefUIFromTESettings() {

    }

    @Override
    protected void commitPrefUIToTESettings(String key) {

    }
}
