package com.te.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettingsInfo;
import Terminals.stdActivityRef;

public class SessionSettings extends SessionSettingsBase {
    public static final int REQ_EDIT = 1;
    public static final int REQ_ADD = 2;
    public static final int RESULT_ADD = 3;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);
        RelativeLayout layOK = (RelativeLayout) toolbar.findViewById(R.id.setting_ok);

        //To determine action bar.
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(stdActivityRef.gCurrentEditSessionIndex >= 0) {  //edit session
            int nEditSessionIdx = stdActivityRef.gCurrentEditSessionIndex;
            String strTitle = String.format(getResources().getString(R.string.setting_title), nEditSessionIdx + 1);
            getSupportActionBar().setTitle(strTitle);
            layOK.setVisibility(View.GONE);
        } else if(stdActivityRef.gCurrentEditSessionIndex == -1) {  //add session
            String strTitle = getResources().getString(R.string.new_session);
            getSupportActionBar().setTitle(strTitle);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
            layOK.setVisibility(View.VISIBLE);
            layOK.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     setResult(RESULT_ADD);
                     finish();
                 }
            });
        }
         // Display the fragment as the main content.
        SessionSettingsFrg settingsFrg = getFragment(SessionSettingsFrg.class);
        settingsFrg.setSessionSetting(getCurrentEditSession());
        commitFrgToActivity(settingsFrg);
    }
}
