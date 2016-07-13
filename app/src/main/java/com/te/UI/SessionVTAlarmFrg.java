package com.te.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

import com.example.terminalemulation.R;

public class SessionVTAlarmFrg extends SessionSettingsFrgBase {
    private TESwitchPreference mSwchCtrlReader = null;
    private TESwitchPreference mSwchFeedback = null;
	public SessionVTAlarmFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_vt_alarm);

        mSwchCtrlReader = (TESwitchPreference) findPreference(getResources().getString(R.string.vt_control_reader_key));
        mSwchFeedback = (TESwitchPreference) findPreference(getResources().getString(R.string.vt_feedback_key));
        mSwchFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent feedbackAct = new Intent(getActivity(), Session3rdSettings.class);
                feedbackAct.setAction(Session3rdSettings.ACTION_FEEDBACK);
                startActivityForResult(feedbackAct, 0);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if(Session3rdSettings.gIsModified)
                    mSetting.mIsFeedbackControlByCmd = true;
                //onResume will update UI
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mSwchCtrlReader.setChecked(mSetting.mIsScanControl);
        mSwchFeedback.setChecked(mSetting.mIsFeedbackControlByCmd);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.vt_control_reader_key)) == 0) {
            mSetting.mIsScanControl = mSwchCtrlReader.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.vt_feedback_key)) == 0) {
            mSetting.mIsFeedbackControlByCmd = mSwchFeedback.isChecked();
        }
    }
}
