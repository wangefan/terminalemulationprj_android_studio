package com.te.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.example.terminalemulation.R;

import Terminals.CipherConnectSettingInfo;

public class SessionSecondSettings extends AppCompatActivity {
    public static final String ACTION_HOST_PROFILE = "com.te.UI.SessionSecondSettings.ACTION_HOST_PROFILE";
    public static final String ACTION_SERVER_SETTING = "com.te.UI.SessionSecondSettings.ACTION_SERVER_SETTING";
    public static final String ACTION_SCREEN_SETTING = "com.te.UI.SessionSecondSettings.ACTION_SCREEN_SETTING";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_second_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);

        //To determine action bar.
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         // Display the fragment as the main content.
        String action = getIntent().getAction();
        if(action.compareTo(ACTION_HOST_PROFILE) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.host_profile));
            SessionHostProfileFrg settingsFrg = new SessionHostProfileFrg();
            settingsFrg.setSessionSeting(SessionSettings.gEditSessionSetting);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, settingsFrg)
                    .commit();
        } else if(action.compareTo(ACTION_SERVER_SETTING) == 0) {
            if(CipherConnectSettingInfo.getIsHostTNByIndex(CipherConnectSettingInfo.GetSessionIndex()) == true) {
                getSupportActionBar().setTitle(getResources().getString(R.string.tn_setting));
                SessionTNSettingsFrg settingsFrg = new SessionTNSettingsFrg();
                settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, settingsFrg)
                        .commit();
            } else {//VT settings
                getSupportActionBar().setTitle(getResources().getString(R.string.vt_setting));
                SessionVTSettingsFrg settingsFrg = new SessionVTSettingsFrg();
                settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, settingsFrg)
                        .commit();
            }
        } else if(action.compareTo(ACTION_SCREEN_SETTING) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.screen_setting));
            SessionScreenSettingsFrg settingsFrg = new SessionScreenSettingsFrg();
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, settingsFrg)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
