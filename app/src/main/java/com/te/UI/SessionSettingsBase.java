package com.te.UI;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettings;
import Terminals.TESettingsInfo;

public class SessionSettingsBase extends AppCompatActivity {
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
    protected void onDestroy() {
        super.onDestroy();
        TESettingsInfo.saveSessionSettings();
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
