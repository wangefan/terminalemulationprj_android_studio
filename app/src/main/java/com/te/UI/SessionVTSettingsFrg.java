package com.te.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.Log;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

import Terminals.TESettings;

public class SessionVTSettingsFrg extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Data members
    private CheckBoxPreference mChkUpperCase = null;
    private CheckBoxPreference mChkLineBuffer = null;
    private CheckBoxPreference mChkEcho = null;
    private TESettings.SessionSetting mSetting = null;

    public SessionVTSettingsFrg() {
    }

    public void setSessionSeting(TESettings.SessionSetting setting) {
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

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        } else if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getKey().compareTo((getResources().getString(R.string.host_auto_sign_pwd_key))) == 0) {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        } else if (p instanceof MyIPPreference) {
            MyIPPreference ipPref = (MyIPPreference) p;
            ipPref.setSummary(ipPref.getIp());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TE", "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_vt_settings);

        //UI
        mChkUpperCase = (CheckBoxPreference) findPreference(getResources().getString(R.string.data_upper_case_key));
        mChkLineBuffer = (CheckBoxPreference) findPreference(getResources().getString(R.string.vt_linebuffer_key));
        mChkEcho = (CheckBoxPreference) findPreference(getResources().getString(R.string.vt_echo_key));
    }

    @Override
    public void onResume() {
        super.onResume();
        //Sync settings to UI
        mChkUpperCase.setChecked(mSetting.mBUpperCase);
        mChkLineBuffer.setChecked(mSetting.mLineBuffer == 1);
        mChkEcho.setChecked(mSetting.mBEcho);

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
        if(key.compareTo(getResources().getString(R.string.data_upper_case_key)) == 0) {
            mSetting.mBUpperCase = mChkUpperCase.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.vt_linebuffer_key)) == 0) {
            mSetting.mLineBuffer = mChkLineBuffer.isChecked() ? 1 : 0;
        } else if(key.compareTo(getResources().getString(R.string.vt_echo_key)) == 0) {
            mSetting.mBEcho = mChkEcho.isChecked();
        }
    }
}
