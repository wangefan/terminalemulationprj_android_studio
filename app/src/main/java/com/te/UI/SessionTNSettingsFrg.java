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

public class SessionTNSettingsFrg extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Data members
    private CheckBoxPreference mChkUpperCase = null;
    private ListPreference mCodePage = null;
    private CheckBoxPreference mChkAutoReset = null;
    private ListPreference mChkFLIfExceed = null;
    private TESwitchPreference mSwchDevName = null;
    private EditTextPreference mEdtPopErrorRow = null;
    private CheckBoxPreference mChkPopupWindow = null;
    private TESettings.SessionSetting mSetting = null;

    public SessionTNSettingsFrg() {
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
        addPreferencesFromResource(R.xml.pref_tn_settings);

        //UI
        mChkUpperCase = (CheckBoxPreference) findPreference(getResources().getString(R.string.tn_data_upper_case_key));
        mCodePage = (ListPreference) findPreference(getResources().getString(R.string.tn_codepage_key));
        mChkAutoReset = (CheckBoxPreference) findPreference(getResources().getString(R.string.tn_auto_reset_key));
        mChkFLIfExceed = (ListPreference) findPreference(getResources().getString(R.string.tn_field_length_if_exceed_key));
        mSwchDevName = (TESwitchPreference) findPreference(getResources().getString(R.string.tn_devname_key));
        mSwchDevName.setListener(new TESwitchPreference.OnListener() {
            @Override
            public void onClick() {
                UIUtility.messageBox("test", getActivity());
                /*String strEditTextResult = UIUtility.editMessageBox(R.string.MSG_get_cust_dev_name, getActivity());
                if(strEditTextResult != null) {
                    mSetting.setUseDefaultDevName(false);
                    mSetting.mDevName = strEditTextResult;
                    mSwchDevName.setSummaryOn(mSetting.mDevName);
                    mSwchDevName.setChecked(true);
                }
                */
            }
        });
        mEdtPopErrorRow = (EditTextPreference) findPreference(getResources().getString(R.string.tn_popup_row_key));
        mChkPopupWindow = (CheckBoxPreference) findPreference(getResources().getString(R.string.tn_popup_window_key));
    }

    @Override
    public void onResume() {
        super.onResume();
        //Sync settings to UI
        mChkUpperCase.setChecked(mSetting.mBUpperCase);
        mCodePage.setValue(String.valueOf(mSetting.mTELanguage));
        mChkAutoReset.setChecked(mSetting.mBIBMAutoReset);
        mChkFLIfExceed.setValue(String.valueOf(mSetting.mCheckFieldLength));
        mSwchDevName.setChecked(mSetting.isUseDefaultDevName());
        mSwchDevName.setSummaryOn(mSetting.mDevName);
        mEdtPopErrorRow.setText(String.valueOf(mSetting.mNErrorRowIndexg));
        mChkPopupWindow.setChecked(mSetting.misPopUpErrorDialog);
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
        if(key.compareTo(getResources().getString(R.string.tn_data_upper_case_key)) == 0) {
            mSetting.mBUpperCase = mChkUpperCase.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.tn_codepage_key)) == 0) {
            mSetting.mTELanguage = Integer.valueOf(mCodePage.getValue());
        } else if(key.compareTo(getResources().getString(R.string.tn_auto_reset_key)) == 0) {
            mSetting.mBIBMAutoReset = mChkAutoReset.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.tn_field_length_if_exceed_key)) == 0) {
            mSetting.mCheckFieldLength = Integer.valueOf(mChkFLIfExceed.getValue());
        } else if(key.compareTo(getResources().getString(R.string.tn_devname_key)) == 0) {
            mSetting.setUseDefaultDevName(mSwchDevName.isChecked() == false);
            mSetting.mDevName = String.valueOf(mSwchDevName.getSummaryOn());
        } else if(key.compareTo(getResources().getString(R.string.tn_popup_row_key)) == 0) {
            mSetting.mNErrorRowIndexg = Integer.valueOf(mEdtPopErrorRow.getText());
        } else if(key.compareTo(getResources().getString(R.string.tn_popup_window_key)) == 0) {
            mSetting.misPopUpErrorDialog = mChkPopupWindow.isChecked();
        }
    }
}
