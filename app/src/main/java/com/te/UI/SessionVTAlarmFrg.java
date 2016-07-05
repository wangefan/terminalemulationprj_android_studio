package com.te.UI;

import android.os.Bundle;

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
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mSwchCtrlReader.setChecked(mSetting.mIsScanControl);
        mSwchFeedback.setChecked(mSetting.g_ReaderParam.mIsFeedbackControlByCmd > 0);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.vt_control_reader_key)) == 0) {
            mSetting.mIsScanControl = mSwchCtrlReader.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.vt_feedback_key)) == 0) {
            if(mSwchFeedback.isChecked()) {
                mSetting.g_ReaderParam.mIsFeedbackControlByCmd = 1;
            } else {
                mSetting.g_ReaderParam.mIsFeedbackControlByCmd = 0;
            }
        }
    }
}
