package com.te.UI;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;

import Terminals.TESettings;

public abstract class SessionSettingsFrgBase extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Data members

    //Notice that could not use in onCreate because may not ready
    protected TESettings.SessionSetting mSetting = null;
    public SessionSettingsFrgBase() {
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
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

    protected void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        } else if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        } else if (p instanceof MyIPPreference) {
            MyIPPreference ipPref = (MyIPPreference) p;
            ipPref.setSummary(ipPref.getAddress());
        }
    }

    protected abstract void syncPrefUIFromTESettings();
    protected abstract void commitPrefUIToTESettings(String key);

    @Override
    public void onResume() {
        super.onResume();
        syncPrefUIFromTESettings();
        initSummary(getPreferenceScreen());
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
        commitPrefUIToTESettings(key);
    }
}
