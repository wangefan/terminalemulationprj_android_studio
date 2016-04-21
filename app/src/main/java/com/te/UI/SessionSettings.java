package com.te.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;
import java.util.List; 

import com.example.terminalemulation.R;

import Terminals.CipherConnectSettingInfo;
import Terminals.TESettings;

import static Terminals.TESettings.*;

public class SessionSettings extends AppCompatActivity {
    public static final int REQ_EDIT = 1;
    public static final int REQ_ADD = 2;
    public static final String ACT_SETTING = "ACT_SETTING";
    public static final String ACT_SETTING_EDIT = "ACT_SETTING_EDIT";
    public static final String ACT_SETTING_ADD = "ACT_SETTING_ADD";
    public static TESettings.SessionSetting gEditSessionSetting = null;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);
        ImageButton btnCancel = (ImageButton) toolbar.findViewById(R.id.idcancel);
        ImageButton btnOK = (ImageButton) toolbar.findViewById(R.id.idok);

        //To determine action bar.
        Intent intent = getIntent();
        String act = intent.getStringExtra(ACT_SETTING);
        if(act.compareToIgnoreCase(ACT_SETTING_EDIT) == 0) {
            gEditSessionSetting = CipherConnectSettingInfo.getSessionSetting(CipherConnectSettingInfo.GetSessionIndex());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            btnCancel.setVisibility(View.GONE);
            btnOK.setVisibility(View.GONE);
        } else if(act.compareToIgnoreCase(ACT_SETTING_ADD) == 0) {
            gEditSessionSetting = new TESettings.SessionSetting();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            btnCancel.setVisibility(View.VISIBLE);
            btnOK.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     setResult(RESULT_OK);
                     finish();
                 }
            });
        }
         // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SessionSettingsFrg(gEditSessionSetting))
                .commit();
    }
}
