package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.util.Log;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

public class SessionTNSettingsFrg extends SessionSettingsFrgBase {

    //Data members
    private CheckBoxPreference mChkUpperCase = null;
    private ListPreference mCodePage = null;
    private CheckBoxPreference mChkAutoReset = null;
    private ListPreference mChkFLIfExceed = null;
    private TESwitchPreference mSwchDevName = null;
    private EditTextPreference mEdtPopErrorRow = null;
    private CheckBoxPreference mChkPopupWindow = null;

    public SessionTNSettingsFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_tn_settings);

        //UI
        mChkUpperCase = (CheckBoxPreference) findPreference(getResources().getString(R.string.data_upper_case_key));
        mCodePage = (ListPreference) findPreference(getResources().getString(R.string.tn_codepage_key));
        mChkAutoReset = (CheckBoxPreference) findPreference(getResources().getString(R.string.tn_auto_reset_key));
        mChkFLIfExceed = (ListPreference) findPreference(getResources().getString(R.string.tn_field_length_if_exceed_key));
        mSwchDevName = (TESwitchPreference) findPreference(getResources().getString(R.string.tn_devname_key));
        mSwchDevName.setOnTESwitchListener(new TESwitchPreference.OnTESwitchListener() {
            @Override
            public void onClick() {
                UIUtility.editMessageBox(R.string.MSG_get_cust_dev_name, getActivity(), new UIUtility.OnEditMessageBoxListener() {
                    @Override
                    public void onResult(String result) {
                        mSetting.setUseDefaultDevName(false);
                        mSetting.mDevName = result;
                        mSwchDevName.setSummaryOn(mSetting.mDevName);
                        mSwchDevName.setChecked(true);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }

            @Override
            public void onChecked(boolean isChecked) {

            }
        });
        mEdtPopErrorRow = (EditTextPreference) findPreference(getResources().getString(R.string.tn_popup_row_key));
        mChkPopupWindow = (CheckBoxPreference) findPreference(getResources().getString(R.string.tn_popup_window_key));
    }

    @Override
    public void syncPrefUIFromTESettings() {
        mChkUpperCase.setChecked(mSetting.mBUpperCase);
        mCodePage.setValue(String.valueOf(mSetting.mTELanguage));
        mChkAutoReset.setChecked(mSetting.mBIBMAutoReset);
        mChkFLIfExceed.setValue(String.valueOf(mSetting.mCheckFieldLength));
        mSwchDevName.setChecked(!mSetting.isUseDefaultDevName());
        mSwchDevName.setSummaryOn(mSetting.mDevName);
        mEdtPopErrorRow.setText(String.valueOf(mSetting.mNErrorRowIndexg));
        mChkPopupWindow.setChecked(mSetting.misPopUpErrorDialog);
    }

    @Override
    public void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.data_upper_case_key)) == 0) {
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
