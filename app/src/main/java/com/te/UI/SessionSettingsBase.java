package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettings;
import Terminals.TESettingsInfo;
import Terminals.stdActivityRef;

public class SessionSettingsBase extends AppCompatActivity {
    private static final String TE_APP_STATUS_SETTINGS = "TE_APP_STATUS_SETTINGS";
    private static final String TE_APP_STATUS_LICENSE = "TE_APP_STATUS_LICENSE";
    private static final String TE_APP_STATUS_CUR_EDITING_IDX = "TE_APP_STATUS_CUR_EDITING_IDX";
    private static final String TE_APP_STATUS_IS_53Key = "TE_APP_STATUS_IS_53Key";

    private static TESettings.SessionSetting mAddedSession = null;

    protected <TypeName extends Fragment> TypeName getFragment(Class<TypeName> classObj) {
        TypeName settingsFrg = (TypeName) getFragmentManager().findFragmentByTag(classObj.getName());
        if(settingsFrg == null) {
            try {
                settingsFrg = classObj.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return settingsFrg;
    }

    protected void commitFrgToActivity(Fragment frg) {
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, frg, frg.getClass().getName())
                .commit();
    }

    protected static TESettings.SessionSetting getCurrentEditSession() {
        TESettings.SessionSetting setting = null;
        if(stdActivityRef.gCurrentEditSessionIndex >= 0) { //Edit session
            setting = TESettingsInfo.getSessionSetting(stdActivityRef.gCurrentEditSessionIndex);
        } else {
            setting = mAddedSession;
        }
        assert (setting != null);
        return setting;
    }

    public static TESettings.SessionSetting getAddedSession() {
        return mAddedSession;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CipherUtility.Log_d("SessionSettingsBase", String.format("onCreate"));
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            // Restore state members from saved instance
            boolean bRestore = false;
            bRestore = savedInstanceState.getBoolean(TE_APP_STATUS_SETTINGS);
            if(bRestore) {
                CipherUtility.Log_d("SessionSettingsBase", String.format("onCreate, Restore state to restore TE settings"));
                TESettingsInfo.loadSessionSettings(getApplication());
            }
            stdActivityRef.gIsActivate = savedInstanceState.getBoolean(TE_APP_STATUS_LICENSE);
            stdActivityRef.gIs53Keys = savedInstanceState.getBoolean(TE_APP_STATUS_IS_53Key);
            stdActivityRef.gCurrentEditSessionIndex = savedInstanceState.getInt(TE_APP_STATUS_CUR_EDITING_IDX);
            if(stdActivityRef.gCurrentEditSessionIndex == -1) {
                TESettingsInfo.restoreAddedSetting(mAddedSession);
            }
        } else {
            if(stdActivityRef.gCurrentEditSessionIndex == -1) {
                mAddedSession = TESettingsInfo.createNewDefaultSessionSetting(this);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        CipherUtility.Log_d("SessionSettingsBase", String.format("onSaveInstanceState"));
        // Save the user's current game state
        TESettingsInfo.saveSessionSettings(this);
        savedInstanceState.putBoolean(TE_APP_STATUS_SETTINGS, true);
        savedInstanceState.putBoolean(TE_APP_STATUS_LICENSE, stdActivityRef.gIsActivate);
        savedInstanceState.putBoolean(TE_APP_STATUS_IS_53Key, stdActivityRef.gIs53Keys);
        savedInstanceState.putInt(TE_APP_STATUS_CUR_EDITING_IDX, stdActivityRef.gCurrentEditSessionIndex);
        if(stdActivityRef.gCurrentEditSessionIndex == -1) { //add session
            TESettingsInfo.saveAddedSetting(mAddedSession);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
