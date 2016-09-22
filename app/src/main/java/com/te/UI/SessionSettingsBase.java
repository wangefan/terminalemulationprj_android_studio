package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettings;
import Terminals.TESettingsInfo;

public class SessionSettingsBase extends AppCompatActivity {
    private static final String TE_SETTINGS = "TE_SETTINGS";
    protected static TESettings.SessionSetting gEditSessionSetting = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CipherUtility.Log_d("SessionSettingsBase", String.format("onCreate"));
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            // Restore state members from saved instance
            boolean bRestore = false;
            bRestore = savedInstanceState.getBoolean(TE_SETTINGS);
            if(bRestore) {
                CipherUtility.Log_d("SessionSettingsBase", String.format("onCreate, Restore state to restore TE settings"));
                TESettingsInfo.loadSessionSettings(getApplication());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        CipherUtility.Log_d("SessionSettingsBase", String.format("onSaveInstanceState"));
        // Save the user's current game state
        TESettingsInfo.saveSessionSettings(this);
        savedInstanceState.putBoolean(TE_SETTINGS, true);

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
