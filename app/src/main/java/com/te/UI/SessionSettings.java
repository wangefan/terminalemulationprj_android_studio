package com.te.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.terminalemulation.R;
import Terminals.CipherConnectSettingInfo;
import Terminals.CipherReaderControl;
import Terminals.TESettings;

public class SessionSettings extends AppCompatActivity {
    public static final int REQ_EDIT = 1;
    public static final int REQ_ADD = 2;
    public static final String ACT_SETTING = "ACT_SETTING";
    public static final String ACT_SETTING_EDIT = "ACT_SETTING_EDIT";
    public static final String ACT_SETTING_EDIT_GET_SESSION_IDX = "ACT_SETTING_EDIT_GET_SESSION_IDX";
    public static final String ACT_SETTING_ADD = "ACT_SETTING_ADD";

    public static TESettings.SessionSetting gEditSessionSetting = null;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);
        RelativeLayout layBack = (RelativeLayout) toolbar.findViewById(R.id.setting_back);
        RelativeLayout layCancel = (RelativeLayout) toolbar.findViewById(R.id.setting_cancel);
        RelativeLayout layOK = (RelativeLayout) toolbar.findViewById(R.id.setting_ok);
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.setting_title);

        //To determine action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        String act = intent.getStringExtra(ACT_SETTING);

        if(act.compareToIgnoreCase(ACT_SETTING_EDIT) == 0) {
            int nEditSessionIdx = intent.getIntExtra(ACT_SETTING_EDIT_GET_SESSION_IDX, 0);
            String strTitle = String.format(getResources().getString(R.string.setting_title), nEditSessionIdx);
            tvTitle.setText(strTitle);
            gEditSessionSetting = CipherConnectSettingInfo.getSessionSetting(nEditSessionIdx);
            layBack.setVisibility(View.VISIBLE);
            layCancel.setVisibility(View.GONE);
            layOK.setVisibility(View.GONE);
            layBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else if(act.compareToIgnoreCase(ACT_SETTING_ADD) == 0) {
            String strTitle = getResources().getString(R.string.new_session);
            tvTitle.setText(strTitle);
            gEditSessionSetting = new TESettings.SessionSetting();
            layBack.setVisibility(View.GONE);
            layCancel.setVisibility(View.VISIBLE);
            layOK.setVisibility(View.VISIBLE);
            layCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            layOK.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     setResult(RESULT_OK);
                     finish();
                 }
            });
        }
         // Display the fragment as the main content.
        SessionSettingsFrg settingsFrg = new SessionSettingsFrg();
        settingsFrg.setSessionSeting(gEditSessionSetting);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, settingsFrg)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CipherConnectSettingInfo.SessionSettingSave();
    }
}
