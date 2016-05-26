package com.te.UI;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import Terminals.CipherConnectSettingInfo;
import Terminals.TESettings;

public class SessionSettingsBase extends AppCompatActivity {
    protected static TESettings.SessionSetting gEditSessionSetting = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CipherConnectSettingInfo.SessionSettingSave();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
