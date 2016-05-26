package com.te.UI;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.terminalemulation.R;

public class Session3rdSettings extends SessionSettingsBase {
    public static final String ACTION_COLOR = "com.te.UI.Session3rdSettings.ACTION_COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_3rd_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);

        //To determine action bar.
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         // Display the fragment as the main content.
        String action = getIntent().getAction();
        if(action.compareTo(ACTION_COLOR) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.screen_color));
            SessionColorSettingsFrg settingsFrg = new SessionColorSettingsFrg();
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, settingsFrg)
                    .commit();
        }
    }
}
