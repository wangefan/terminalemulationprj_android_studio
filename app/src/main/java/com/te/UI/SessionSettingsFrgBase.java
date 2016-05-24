package com.te.UI;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.Log;
import com.cipherlab.barcode.BuildConfig;

import Terminals.TESettings;

public abstract class SessionSettingsFrgBase extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Data members
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
            ipPref.setSummary(ipPref.getIp());
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
    public void onDestroy() {
        if(BuildConfig.DEBUG) Log.d("TE", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if(BuildConfig.DEBUG) Log.d("TE", "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePrefSummary(findPreference(key));
        commitPrefUIToTESettings(key);
    }
}
