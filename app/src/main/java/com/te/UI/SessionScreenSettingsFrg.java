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

import com.cipherlab.terminalemulation.R;

import Terminals.TESettings;
import Terminals.stdActivityRef;

public class SessionScreenSettingsFrg extends SessionSettingsFrgBase {

    private static final int STRIDX_SHOW_STATUSBAR = 0;
    private static final int STRIDX_SHOW_WIFI = 1;
    private static final int STRIDX_SHOW_BATT = 2;
    private static final int STRIDX_SHOW_WIFI_BATT = 3;

    //Data members
    private CheckBoxPreference mChkShowSessionNumber = null;
    private CheckBoxPreference mChkShowSessionStatus = null;
    private CheckBoxPreference mChkScreenPanning = null;
    private CheckBoxPreference mChkAutoPopSIPOnConn = null;
    private TESwitchPreference mSwchShowWiFiAlert = null;
    private TESwitchPreference mSwchShowBattrryAlert = null;
    private CheckBoxPreference mChkAcitvateMacro = null;
    private ListPreference mlstCursorType = null;
    private TESwitchPreference mSwchAutoTracking = null;
    private Preference mPrefLockedLoc = null;
    private ListPreference mlstFont = null;
    private ListPreference mlstFontSize = null;
    private Preference mPrefColorSettings = null;
    private CheckBoxPreference mChkAutoFullScreenOnConn = null;
    private CheckBoxPreference mChkShowNavibarOnFullScreen = null;
    private TESwitchPreference mSwhShowWFBTOnFullScreen = null;
    private ListPreference mlstUpdateIconInterval = null;

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

    private void updateIntervalUIFromSetting() {
        if(mSetting.mIsUpdateWiFiIconOnFull == false) {
            mlstUpdateIconInterval.setValueIndex(0);
        } else {
            mlstUpdateIconInterval.setValueIndex(mSetting.mWiFiIntervalIndex + 1);
        }
        if(mSetting.mIsShowWifiIconOnFull || mSetting.mIsShowBatteryIconOnFull) {
            mlstUpdateIconInterval.setEnabled(true);
        } else {
            mlstUpdateIconInterval.setEnabled(false);
        }
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mChkShowSessionNumber.setChecked(mSetting.mIsShowSessionNumber);
        mChkShowSessionStatus.setChecked(mSetting.mIsShowSessionStatus);
        mChkScreenPanning.setChecked(mSetting.mIsScreenPanning);
        mChkAutoPopSIPOnConn.setChecked(mSetting.mIsAutoPopSIPOnConn);
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
        mChkAutoFullScreenOnConn.setChecked(mSetting.mIsAutoFullscreenOnConn);
        if(mSetting.mIsShowStatusbarOnFull ||
                mSetting.mIsShowWifiIconOnFull ||
                mSetting.mIsShowBatteryIconOnFull) {
            mSwhShowWFBTOnFullScreen.setChecked(true);
            mSwhShowWFBTOnFullScreen.setSummaryOn(
                    getShowWFBTOnFullScreenSummary(
                            mSetting.mIsShowStatusbarOnFull,
                            mSetting.mIsShowWifiIconOnFull,
                            mSetting.mIsShowBatteryIconOnFull));
        } else {
            mSwhShowWFBTOnFullScreen.setChecked(false);
        }
        updateIntervalUIFromSetting();
    }

    private String getShowWFBTOnFullScreenSummary(boolean isShowTaskbarOnFull, boolean isShowWifiIconOnFull, boolean isShowBatteryIconOnFull) {
        String [] wf_batt_sum_array = getResources().getStringArray(R.array.show_wf_batt_info_array);
        if(wf_batt_sum_array.length <= 0) {
            return "";
        }

        if(isShowWifiIconOnFull && isShowBatteryIconOnFull) {
            return wf_batt_sum_array[STRIDX_SHOW_WIFI_BATT];
        } else if(isShowTaskbarOnFull) {
            return wf_batt_sum_array[STRIDX_SHOW_STATUSBAR];
        } else if(isShowWifiIconOnFull) {
            return wf_batt_sum_array[STRIDX_SHOW_WIFI];
        } else if(isShowBatteryIconOnFull){
            return wf_batt_sum_array[STRIDX_SHOW_BATT];
        }
        return "";
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.screen_session_number_key)) == 0) {
            mSetting.mIsShowSessionNumber = mChkShowSessionNumber.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_session_status_key)) == 0) {
            mSetting.mIsShowSessionStatus = mChkShowSessionStatus.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_screen_panning_key)) == 0) {
            mSetting.mIsScreenPanning = mChkScreenPanning.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_autopop_sip_conn_key)) == 0) {
            mSetting.mIsAutoPopSIPOnConn = mChkAutoPopSIPOnConn.isChecked();
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
        } else if(key.compareTo(getResources().getString(R.string.screen_show_navi_bar_key)) == 0) {
            mSetting.mIsShowNavibarOnFullScreen = mChkShowNavibarOnFullScreen.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.screen_show_wf_batt_key)) == 0) {
            if(mSwhShowWFBTOnFullScreen.isChecked() == false) {
                mSetting.mIsShowStatusbarOnFull = false;
                mSetting.mIsShowWifiIconOnFull = false;
                mSetting.mIsShowBatteryIconOnFull = false;
            } else {
                String summOn = mSwhShowWFBTOnFullScreen.getSummaryOn().toString();
                String statusBarOnFull = getResources().getStringArray(R.array.show_wf_batt_info_array)[0];
                String showWiFiOnFull = getResources().getStringArray(R.array.show_wf_batt_info_array)[1];
                String showBattOnFull = getResources().getStringArray(R.array.show_wf_batt_info_array)[2];
                String showWiFiAndBattOnFull = getResources().getStringArray(R.array.show_wf_batt_info_array)[3];
                if(summOn.compareTo(statusBarOnFull) == 0) {
                    mSetting.mIsShowStatusbarOnFull = true;
                    mSetting.mIsShowWifiIconOnFull = false;
                    mSetting.mIsShowBatteryIconOnFull = false;
                } else if(summOn.compareTo(showWiFiOnFull) == 0) {
                    mSetting.mIsShowStatusbarOnFull = false;
                    mSetting.mIsShowWifiIconOnFull = true;
                    mSetting.mIsShowBatteryIconOnFull = false;
                } else if(summOn.compareTo(showBattOnFull) == 0) {
                    mSetting.mIsShowStatusbarOnFull = false;
                    mSetting.mIsShowWifiIconOnFull = false;
                    mSetting.mIsShowBatteryIconOnFull = true;
                } else if(summOn.compareTo(showWiFiAndBattOnFull) == 0) {
                    mSetting.mIsShowStatusbarOnFull = false;
                    mSetting.mIsShowWifiIconOnFull = true;
                    mSetting.mIsShowBatteryIconOnFull = true;
                }
            }
            updateIntervalUIFromSetting();
        } else if(key.compareTo(getResources().getString(R.string.screen_icon_update_interval_key)) == 0) {
            if(mlstUpdateIconInterval.getValue().compareTo("0") == 0) {
                mSetting.mIsUpdateWiFiIconOnFull = false;
                mSetting.mIsShowBatteryIconOnFull = false;
            } else {
                mSetting.mIsUpdateWiFiIconOnFull = true;
                mSetting.mIsUpdateBatteryIconOnFull = true;
                for(int idxInterval = 0; idxInterval < mlstUpdateIconInterval.getEntryValues().length; ++idxInterval) {
                    String itrInterval = mlstUpdateIconInterval.getEntryValues()[idxInterval].toString();
                    if(itrInterval.compareTo(mlstUpdateIconInterval.getValue().toString()) == 0) {
                        mSetting.mWiFiIntervalIndex = idxInterval - 1;
                        mSetting.mWiFiIntervalValue = Integer.valueOf(itrInterval);
                        mSetting.mBatteryIntervalIndex = idxInterval - 1;
                        mSetting.mBatteryIntervalValue = Integer.valueOf(itrInterval);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_screen_settings);

        //UI
        mChkShowSessionNumber = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_session_number_key));
        mChkShowSessionNumber.setEnabled(stdActivityRef.gIsActivate);
        mChkShowSessionStatus = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_session_status_key));
        mChkScreenPanning = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_screen_panning_key));
        mChkAutoPopSIPOnConn = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_autopop_sip_conn_key));
        mSwchShowWiFiAlert = (TESwitchPreference) findPreference(getResources().getString(R.string.screen_wifi_alert_key));
        mSwchShowWiFiAlert.setOnTESwitchListener(new TESwitchPreference.OnTESwitchListener() {
            @Override
            public void onClick() {
                int nSelItem = mSetting.mNShowWifiAlertLevelIndex;
                UIUtility.listMessageBox(R.string.screen_wifi_alert,
                        R.array.alert_array,
                        nSelItem,
                        getActivity(),
                        new UIUtility.OnListMessageBoxListener() {
                            @Override
                            public void onSelResult(String result, int nSelIdx) {
                                mSetting.mNShowWifiAlertLevel = Integer.valueOf(result);
                                mSetting.mNShowWifiAlertLevelIndex = nSelIdx;
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
                int nSelItem = mSetting.mNShowBatteryAlertLevelIndex;
                UIUtility.listMessageBox(R.string.screen_battery_alert,
                        R.array.alert_array,
                        nSelItem,
                        getActivity(),
                        new UIUtility.OnListMessageBoxListener() {
                            @Override
                            public void onSelResult(String result, int nSelIdx) {
                                mSetting.mNShowBatteryAlertLevelIndex = nSelIdx;
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
        mChkAcitvateMacro.setEnabled(stdActivityRef.gIsActivate);
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
                    public void onSelResult(String result, int nSelIdx) {
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
        mPrefColorSettings = findPreference(getResources().getString(R.string.screen_color_key));
        mPrefColorSettings.setEnabled(stdActivityRef.gIsActivate);
        mChkAutoFullScreenOnConn = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_auto_full_on_conn_key));
        mChkShowNavibarOnFullScreen = (CheckBoxPreference) findPreference(getResources().getString(R.string.screen_show_navi_bar_key));
        mSwhShowWFBTOnFullScreen = (TESwitchPreference) findPreference(getResources().getString(R.string.screen_show_wf_batt_key));
        mSwhShowWFBTOnFullScreen.setSummaryOn(getResources().getStringArray(R.array.show_wf_batt_info_array)[0]);
        mSwhShowWFBTOnFullScreen.setOnTESwitchListener(new TESwitchPreference.OnTESwitchListener() {
            @Override
            public void onClick() {
                int nSelItem = -1;
                if(mSetting.mIsShowBatteryIconOnFull && mSetting.mIsShowWifiIconOnFull) {
                    nSelItem = STRIDX_SHOW_WIFI_BATT;
                } else if(mSetting.mIsShowBatteryIconOnFull) {
                    nSelItem = STRIDX_SHOW_BATT;
                } else if(mSetting.mIsShowWifiIconOnFull) {
                    nSelItem = STRIDX_SHOW_WIFI;
                } else if(mSetting.mIsShowStatusbarOnFull){
                    nSelItem = STRIDX_SHOW_STATUSBAR;
                }

                UIUtility.listMessageBox(R.string.show_wf_batt_info_title,
                        R.array.show_wf_batt_info_array,
                        nSelItem,
                        getActivity(),
                        new UIUtility.OnListMessageBoxListener() {
                            @Override
                            public void onSelResult(String result, int selIndex) {
                                switch (selIndex) {
                                    case STRIDX_SHOW_STATUSBAR:
                                    default:
                                        mSetting.mIsShowStatusbarOnFull = true;
                                        mSetting.mIsShowWifiIconOnFull = false;
                                        mSetting.mIsShowBatteryIconOnFull = false;
                                        break;
                                    case STRIDX_SHOW_WIFI:
                                        mSetting.mIsShowStatusbarOnFull = false;
                                        mSetting.mIsShowWifiIconOnFull = true;
                                        mSetting.mIsShowBatteryIconOnFull = false;
                                        break;
                                    case STRIDX_SHOW_BATT:
                                        mSetting.mIsShowStatusbarOnFull = false;
                                        mSetting.mIsShowWifiIconOnFull = false;
                                        mSetting.mIsShowBatteryIconOnFull = true;
                                        break;
                                    case STRIDX_SHOW_WIFI_BATT:
                                        mSetting.mIsShowStatusbarOnFull = false;
                                        mSetting.mIsShowWifiIconOnFull = true;
                                        mSetting.mIsShowBatteryIconOnFull = true;
                                        break;
                                }
                                mSwhShowWFBTOnFullScreen.setSummaryOn(result);
                                mSwhShowWFBTOnFullScreen.setChecked(true);
                                updateIntervalUIFromSetting();
                            }
                        });
            }

            @Override
            public void onChecked(boolean isChecked) {

            }
        });
        mlstUpdateIconInterval = (ListPreference) findPreference(getResources().getString(R.string.screen_icon_update_interval_key));
    }
}
