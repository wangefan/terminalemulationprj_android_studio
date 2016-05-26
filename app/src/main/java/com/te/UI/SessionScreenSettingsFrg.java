package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

import Terminals.TESettings;

public class SessionScreenSettingsFrg extends SessionSettingsFrgBase {

    //Data members
    private CheckBoxPreference mChkShowSessionNumber = null;
    private CheckBoxPreference mChkShowSessionStatus = null;
    private TESwitchPreference mSwchShowWiFiAlert = null;
    private TESwitchPreference mSwchShowBattrryAlert = null;
    private CheckBoxPreference mChkAcitvateMacro = null;
    private ListPreference mlstCursorType = null;
    private TESwitchPreference mSwchAutoTracking = null;
    private Preference mPrefLockedLoc = null;
    private ListPreference mlstFont = null;
    private ListPreference mlstFontSize = null;
    private Preference mPrefFontColor = null;
    private Preference mPrefBGColor = null;
    private CheckBoxPreference mChkAutoFullScreenOnConn = null;
    private CheckBoxPreference mChkShowTaskbarOnFullScreen = null;

    public SessionScreenSettingsFrg() {
    }

    private String getAutoTrackString(TESettings.SessionSetting.AutoTrackType autoTrackType) {
        switch (autoTrackType) {
            case AutoTrackType_Visible:
                return getString(R.string.auto_track_visible);
            case AutoTrackType_Center:
                return getString(R.string.auto_track_center);
            case AutoTrackType_Lock:
                return getString(R.string.auto_track_locked);
        }
        return "";
    }

    private TESettings.SessionSetting.AutoTrackType getAutoTrackTypeFromString(String summaryOn) {
        if(summaryOn.compareToIgnoreCase(getString(R.string.auto_track_visible)) == 0) {
            return TESettings.SessionSetting.AutoTrackType.AutoTrackType_Visible;
        } else if(summaryOn.compareToIgnoreCase(getString(R.string.auto_track_center)) == 0) {
            return TESettings.SessionSetting.AutoTrackType.AutoTrackType_Center;
        } else {//(summaryOn.compareToIgnoreCase(getString(R.string.auto_track_locked)) == 0) {
            return TESettings.SessionSetting.AutoTrackType.AutoTrackType_Lock;
        }
    }

    private String getLockedLocString(int nCursorLockCol, int nCursorLockRow) {
        return String.format(getString(R.string.LockedLocFormat), nCursorLockCol, nCursorLockRow);
    }

    public String getFontSizeValFromSetting(int nFontWidth, int nFontHeight) {
        return String.format(getString(R.string.FontSizeFormat), nFontWidth, nFontHeight);
    }

    private void updateLockPrefUIDependOnAutoTrackUI(TESettings.SessionSetting.AutoTrackType trackType) {
        if(mSwchAutoTracking.isChecked() == false || trackType != TESettings.SessionSetting.AutoTrackType.AutoTrackType_Lock) {
            mPrefLockedLoc.setEnabled(false);
        } else {
            mPrefLockedLoc.setEnabled(true);
        }
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mChkShowSessionNumber.setChecked(mSetting.mIsShowSessionNumber);
        mChkShowSessionStatus.setChecked(mSetting.mIsShowSessionStatus);
        mSwchShowWiFiAlert.setChecked(mSetting.mIsShowWifiAlert);
        //Todo: level
        mSwchShowBattrryAlert.setChecked(mSetting.mIsShowBatteryAlert);
        //Todo: level
        mChkAcitvateMacro.setChecked(mSetting.mIsActMacro);
        mlstCursorType.setValue(String.valueOf(mSetting.mNCursorType));
        mSwchAutoTracking.setChecked(mSetting.mIsCursorTracking);
        mSwchAutoTracking.setSummaryOn(getAutoTrackString(mSetting.getAutoTrackType()));
        updateLockPrefUIDependOnAutoTrackUI(mSetting.getAutoTrackType());
        mPrefLockedLoc.setSummary(getLockedLocString(mSetting.mNCursorLockCol, mSetting.mNCursorLockRow));
        mlstFont.setValue(String.valueOf(mSetting.mNFontType));
        mlstFontSize.setValue(getFontSizeValFromSetting(mSetting.mNFontWidth, mSetting.mNFontHeight));
        //Todo: mPrefFontColor
        //Todo: mPrefBGColor
        mChkAutoFullScreenOnConn.setChecked(mSetting.mIsAutoFullscreenOnConn);
        mChkShowTaskbarOnFullScreen.setChecked(mSetting.mIsShowTaskbarOnConn);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.screen_session_number_key)) == 0) {
            mSetting.mIsShowSessionNumber = mChkShowSessionNumber.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_session_status_key)) == 0) {
            mSetting.mIsShowSessionStatus = mChkShowSessionStatus.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_wifi_alert_key)) == 0) {
            mSetting.mIsShowWifiAlert = mSwchShowWiFiAlert.isChecked();
            //Todo: level
        } else if(key.compareTo(getResources().getString(R.string.screen_battery_alert_key)) == 0) {
            mSetting.mIsShowBatteryAlert = mSwchShowBattrryAlert.isChecked();
            //Todo: level
        } else if(key.compareTo(getResources().getString(R.string.screen_act_macro_key)) == 0) {
            mSetting.mIsActMacro = mChkAcitvateMacro.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_cursor_type_key)) == 0) {
            mSetting.mNCursorType = Integer.valueOf(mlstCursorType.getValue());
        } else if(key.compareTo(getResources().getString(R.string.screen_auto_scroll_key)) == 0) {
            mSetting.mIsCursorTracking = mSwchAutoTracking.isChecked();
            mSetting.setAutoTrackType(getAutoTrackTypeFromString(String.valueOf(mSwchAutoTracking.getSummaryOn())));
        } else if(key.compareTo(getResources().getString(R.string.screen_font_key)) == 0) {
            mSetting.mNFontType = Integer.valueOf(mlstFont.getValue());
        } else if(key.compareTo(getResources().getString(R.string.screen_font_size_key)) == 0) {
            final String split = "x";
            String[] fontDims = mlstFontSize.getTitle().toString().split(split);
            if(fontDims != null) {
                mSetting.mNFontWidth = Integer.valueOf(fontDims[0]);
                mSetting.mNFontHeight = Integer.valueOf(fontDims[1]);
            }
        } else if(key.compareTo(getResources().getString(R.string.screen_font_color_key)) == 0) {
            //Todo:
        } else if(key.compareTo(getResources().getString(R.string.screen_bg_color_key)) == 0) {
            //Todo:
        } else if(key.compareTo(getResources().getString(R.string.screen_auto_full_on_conn_key)) == 0) {
            mSetting.mIsAutoFullscreenOnConn = mChkAutoFullScreenOnConn.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_show_taskbar_key)) == 0) {
            mSetting.mIsShowTaskbarOnConn = mChkShowTaskbarOnFullScreen.isChecked();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG) Log.d("TE", "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_screen_settings);

        //UI
        mChkShowSessionNumber = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_session_number_key));
        mChkShowSessionStatus = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_session_status_key));
        mSwchShowWiFiAlert = (TESwitchPreference) findPreference(getResources().getString(R.string.screen_wifi_alert_key));
        mSwchShowBattrryAlert = (TESwitchPreference) findPreference(getResources().getString(R.string.screen_battery_alert_key));
        mChkAcitvateMacro = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_act_macro_key));
        mlstCursorType = (ListPreference) findPreference(getResources().getString(R.string.screen_cursor_type_key));
        mSwchAutoTracking = (TESwitchPreference) findPreference(getResources().getString(R.string.screen_auto_scroll_key));
        mSwchAutoTracking.setOnTESwitchListener(new TESwitchPreference.OnTESwitchListener() {
            @Override
            public void onClick() {
                int nSelItem = 0;
                String[] items = getResources().getStringArray(R.array.auto_track_array);
                for (int idxAutoTk = 0; idxAutoTk < items.length; idxAutoTk++) {
                    if(items[idxAutoTk].compareTo(getAutoTrackString(mSetting.getAutoTrackType())) == 0) {
                        nSelItem = idxAutoTk;
                    }
                }
                UIUtility.listMessageBox(R.string.screen_auto_track,
                        R.array.auto_track_array,
                        nSelItem,
                        getActivity(),
                         new UIUtility.OnListMessageBoxListener() {
                    @Override
                    public void onSelResult(String result) {
                        TESettings.SessionSetting.AutoTrackType trackType = getAutoTrackTypeFromString(result);
                        mSetting.setAutoTrackType(trackType);
                        mSwchAutoTracking.setSummaryOn(getAutoTrackString(mSetting.getAutoTrackType()));
                        mSwchAutoTracking.setChecked(true);
                        updateLockPrefUIDependOnAutoTrackUI(trackType);
                    }
                });
            }

            @Override
            public void onChecked(boolean isChecked) {
                updateLockPrefUIDependOnAutoTrackUI(mSetting.getAutoTrackType());
            }
        });
        mPrefLockedLoc = findPreference(getResources().getString(R.string.screen_track_lock_key));
        mlstFont = (ListPreference) findPreference(getResources().getString(R.string.screen_font_key));
        mlstFontSize = (ListPreference) findPreference(getResources().getString(R.string.screen_font_size_key));
        //Todo: mPrefFontColor
        //Todo: mPrefBGColor
        mChkAutoFullScreenOnConn = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_auto_full_on_conn_key));
        mChkShowTaskbarOnFullScreen = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_show_taskbar_key));
    }
}
