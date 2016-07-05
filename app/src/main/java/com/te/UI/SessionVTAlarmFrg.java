package com.te.UI;

import android.os.Bundle;

import com.example.terminalemulation.R;

public class SessionVTAlarmFrg extends SessionSettingsFrgBase {
	public SessionVTAlarmFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_vt_alarm);
    }

    @Override
    protected void syncPrefUIFromTESettings() {

    }

    @Override
    protected void commitPrefUIToTESettings(String key) {

    }
}
