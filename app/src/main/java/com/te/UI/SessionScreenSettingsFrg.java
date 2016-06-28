package com.te.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

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

    private String getLockedLocString(int nCursorLockRow, int nCursorLockCol) {
        return String.format(getString(R.string.LockedLocFormat), nCursorLockRow, nCursorLockCol);
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
        mSwchShowWiFiAlert.setSummaryOn(String.valueOf(mSetting.mNShowWifiAlertLevel));
        mSwchShowBattrryAlert.setChecked(mSetting.mIsShowBatteryAlert);
        mSwchShowBattrryAlert.setSummaryOn(String.valueOf(mSetting.mNShowBatteryAlertLevel));
        mChkAcitvateMacro.setChecked(mSetting.mIsActMacro);
        mlstCursorType.setValue(String.valueOf(mSetting.mNCursorType));
        mSwchAutoTracking.setChecked(mSetting.mIsCursorTracking);
        mSwchAutoTracking.setSummaryOn(getAutoTrackString(mSetting.getAutoTrackType()));
        updateLockPrefUIDependOnAutoTrackUI(mSetting.getAutoTrackType());
        mPrefLockedLoc.setSummary(getLockedLocString(mSetting.mNCursorLockRow, mSetting.mNCursorLockCol));
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
        } else if(key.compareTo(getResources().getString(R.string.screen_battery_alert_key)) == 0) {
            mSetting.mIsShowBatteryAlert = mSwchShowBattrryAlert.isChecked();
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
            String[] fontDims = mlstFontSize.getValue().toString().split(split);
            if(fontDims != null) {
                mSetting.mNFontWidth = Integer.valueOf(fontDims[0].trim());
                mSetting.mNFontHeight = Integer.valueOf(fontDims[1].trim());
            }
        } else if(key.compareTo(getResources().getString(R.string.screen_auto_full_on_conn_key)) == 0) {
            mSetting.mIsAutoFullscreenOnConn = mChkAutoFullScreenOnConn.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_show_taskbar_key)) == 0) {
            mSetting.mIsShowTaskbarOnConn = mChkShowTaskbarOnFullScreen.isChecked();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_screen_settings);

        //UI
        mChkShowSessionNumber = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_session_number_key));
        mChkShowSessionStatus = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_session_status_key));
        mSwchShowWiFiAlert = (TESwitchPreference) findPreference(getResources().getString(R.string.screen_wifi_alert_key));
        mSwchShowWiFiAlert.setOnTESwitchListener(new TESwitchPreference.OnTESwitchListener() {
            @Override
            public void onClick() {
                int nSelItem = 0;
                String[] alertItems = getResources().getStringArray(R.array.alert_array);
                for (int idxAlert = 0; idxAlert < alertItems.length; idxAlert++) {
                    if(alertItems[idxAlert].compareTo(String.valueOf(mSetting.mNShowWifiAlertLevel)) == 0) {
                        nSelItem = idxAlert;
                    }
                }
                UIUtility.listMessageBox(R.string.screen_wifi_alert,
                        R.array.alert_array,
                        nSelItem,
                        getActivity(),
                        new UIUtility.OnListMessageBoxListener() {
                            @Override
                            public void onSelResult(String result) {
                                mSetting.mNShowWifiAlertLevel = Integer.valueOf(result);
                                mSwchShowWiFiAlert.setSummaryOn(result);
                                mSwchShowWiFiAlert.setChecked(true);
                            }
                        });
            }

            @Override
            public void onChecked(boolean isChecked) {

            }
        });
        mSwchShowBattrryAlert = (TESwitchPreference) findPreference(getResources().getString(R.string.screen_battery_alert_key));
        mSwchShowBattrryAlert.setOnTESwitchListener(new TESwitchPreference.OnTESwitchListener() {
            @Override
            public void onClick() {
                int nSelItem = 0;
                String[] alertItems = getResources().getStringArray(R.array.alert_array);
                for (int idxAlert = 0; idxAlert < alertItems.length; idxAlert++) {
                    if(alertItems[idxAlert].compareTo(String.valueOf(mSetting.mNShowBatteryAlertLevel)) == 0) {
                        nSelItem = idxAlert;
                    }
                }
                UIUtility.listMessageBox(R.string.screen_battery_alert,
                        R.array.alert_array,
                        nSelItem,
                        getActivity(),
                        new UIUtility.OnListMessageBoxListener() {
                            @Override
                            public void onSelResult(String result) {
                                mSetting.mNShowBatteryAlertLevel = Integer.valueOf(result);
                                mSwchShowBattrryAlert.setSummaryOn(result);
                                mSwchShowBattrryAlert.setChecked(true);
                            }
                        });
            }

            @Override
            public void onChecked(boolean isChecked) {

            }
        });

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
        mPrefLockedLoc.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Pop-up Row-Column dialog.
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View rolColView = inflater.inflate(R.layout.row_col_dialog, null);
                final NumberPicker npRow = (NumberPicker) rolColView.findViewById(R.id.row_picker);
                final NumberPicker npCol = (NumberPicker) rolColView.findViewById(R.id.col_picker);
                npRow.setMinValue(0); npRow.setMaxValue(23);
                npCol.setMinValue(0); npCol.setMaxValue(80);
                npRow.setValue(mSetting.mNCursorLockRow);
                npCol.setValue(mSetting.mNCursorLockCol);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.row_col_dialog);
                builder.setView(rolColView);
                builder.setPositiveButton(R.string.STR_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSetting.mNCursorLockRow = npRow.getValue();
                        mSetting.mNCursorLockCol = npCol.getValue();
                        mPrefLockedLoc.setSummary(getLockedLocString(mSetting.mNCursorLockRow, mSetting.mNCursorLockCol));
                    }
                });
                builder.setNegativeButton(R.string.STR_Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                return false;
            }
        });
        mlstFont = (ListPreference) findPreference(getResources().getString(R.string.screen_font_key));
        mlstFontSize = (ListPreference) findPreference(getResources().getString(R.string.screen_font_size_key));
        //Todo: mPrefFontColor
        //Todo: mPrefBGColor
        mChkAutoFullScreenOnConn = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_auto_full_on_conn_key));
        mChkShowTaskbarOnFullScreen = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_show_taskbar_key));
    }
}
