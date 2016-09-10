package com.te.UI;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettingsInfo;

public class SessionSecondSettings extends SessionSettingsBase {
    public static final String ACTION_HOST_PROFILE = "com.te.UI.SessionSecondSettings.ACTION_HOST_PROFILE";
    public static final String ACTION_SERVER_SETTING = "com.te.UI.SessionSecondSettings.ACTION_SERVER_SETTING";
    public static final String ACTION_SCREEN_SETTING = "com.te.UI.SessionSecondSettings.ACTION_SCREEN_SETTING";
    public static final String ACTION_ALARM_SETTING = "com.te.UI.SessionSecondSettings.ACTION_ALARM_SETTING";
    public static final String ACTION_KEY_MAPPING = "com.te.UI.SessionSecondSettings.ACTION_KEY_MAPPING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_second_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);
        RelativeLayout layReset = (RelativeLayout) toolbar.findViewById(R.id.second_setting_reset);
        layReset.setVisibility(View.GONE);

        //To determine action bar.
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         // Display the fragment as the main content.
        String action = getIntent().getAction();
        if(action.compareTo(ACTION_HOST_PROFILE) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.host_profile));
            SessionHostProfileFrg settingsFrg = getFragment(SessionHostProfileFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        } else if(action.compareTo(ACTION_SERVER_SETTING) == 0) {
            if(TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex()) == true) {
                getSupportActionBar().setTitle(getResources().getString(R.string.tn_setting));
                SessionTNSettingsFrg settingsFrg = getFragment(SessionTNSettingsFrg.class);
                settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
                commitFrgToActivity(settingsFrg);
            } else {//VT settings
                getSupportActionBar().setTitle(getResources().getString(R.string.vt_setting));
                SessionVTSettingsFrg settingsFrg = getFragment(SessionVTSettingsFrg.class);
                settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
                commitFrgToActivity(settingsFrg);
            }
        } else if(action.compareTo(ACTION_SCREEN_SETTING) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.screen_setting));
            SessionScreenSettingsFrg settingsFrg = getFragment(SessionScreenSettingsFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        } else if(action.compareTo(ACTION_ALARM_SETTING) == 0) {
            if(TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex()) == true) {
                getSupportActionBar().setTitle(getResources().getString(R.string.tn_alarm));
                SessionTNAlarmFrg settingsFrg = getFragment(SessionTNAlarmFrg.class);
                settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
                commitFrgToActivity(settingsFrg);
            } else {//VT settings
                getSupportActionBar().setTitle(getResources().getString(R.string.vt_alarm));
                SessionVTAlarmFrg settingsFrg = getFragment(SessionVTAlarmFrg.class);
                settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
                commitFrgToActivity(settingsFrg);
            }
        } else if(action.compareTo(ACTION_KEY_MAPPING) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.key_mapping));
            final SessionKeyMappingFrg settingsFrg = getFragment(SessionKeyMappingFrg.class);
            layReset.setVisibility(View.VISIBLE);
            layReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gEditSessionSetting.resetKeyMapList();
                    settingsFrg.updateKeyListItems();
                }
            });
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        }
    }
}
