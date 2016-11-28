package com.te.UI;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;

import Terminals.TESettingsInfo;

public class SetOrientationActivity extends AppCompatActivity {
    protected void procScreenOrientation() {
        int nScreenOritMode = TESettingsInfo.getScreenOrientation();
        switch (nScreenOritMode) {
            case 0:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                break;
            case 1:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case 2:
                setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        procScreenOrientation();
    }
}
