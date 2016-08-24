package com.te.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import com.cipherlab.terminalemulation.R;

public class SessionVTAlarmFrg extends SessionSettingsFrgBase {
    private int REQ_READER_CONTROL = 0;
    private int REQ_FEEDBACK = 1;
    private TESwitchPreference mSwchCtrlReader = null;
    private TESwitchPreference mSwchFeedback = null;
	public SessionVTAlarmFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_vt_alarm);

        mSwchCtrlReader = (TESwitchPreference) findPreference(getResources().getString(R.string.vt_control_reader_key));
        mSwchCtrlReader.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent feedbackAct = new Intent(getActivity(), Session3rdSettings.class);
                feedbackAct.setAction(Session3rdSettings.ACTION_READER_CTRL);
                startActivityForResult(feedbackAct, REQ_READER_CONTROL);
                return true;
            }
        });
        mSwchFeedback = (TESwitchPreference) findPreference(getResources().getString(R.string.vt_feedback_key));
        mSwchFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent feedbackAct = new Intent(getActivity(), Session3rdSettings.class);
                feedbackAct.setAction(Session3rdSettings.ACTION_FEEDBACK);
                startActivityForResult(feedbackAct, REQ_FEEDBACK);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if(requestCode == REQ_READER_CONTROL) {
                    if(Session3rdSettings.gIsModified)
                        mSetting.mIsScanControl = true;
                } else if(requestCode == REQ_FEEDBACK) {
                    if(Session3rdSettings.gIsModified)
                        mSetting.mIsFeedbackControlByCmd = true;
                }
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
