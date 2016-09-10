package com.te.UI;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;

import com.cipherlab.terminalemulation.R;

public class Session4thSettings extends SessionSettingsBase {
    public static final String ACTION_KEY_TRAP = "com.te.UI.Session4thSettings.ACTION_KEY_TRAP";

    private SessionKeyMappingTrapFrg mKeyMappingTrapFrg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_4th_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);

        //To determine action bar.
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         // Display the fragment as the main content.
        String action = getIntent().getAction();
        if(action.compareTo(ACTION_KEY_TRAP) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.detect_key_title));
            mKeyMappingTrapFrg = getFragment(SessionKeyMappingTrapFrg.class);
            commitFrgToActivity(mKeyMappingTrapFrg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mKeyMappingTrapFrg != null){
            mKeyMappingTrapFrg.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
