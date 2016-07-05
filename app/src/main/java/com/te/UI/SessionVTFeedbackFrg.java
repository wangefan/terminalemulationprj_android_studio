package com.te.UI;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.example.terminalemulation.R;

public class SessionVTFeedbackFrg extends SessionSettingsFrgBase {

    private ListPreference mLstGoodFBType = null;
    private Preference mPrefGoodFBContent = null;
    private ListPreference mLstErrorFBType = null;
    private Preference mPrefErrorFBContent = null;

	public SessionVTFeedbackFrg() {
    }

    private void syncSettingToGoodFBCmdPref(int nType) {
        if(nType == 0) { //Command
            mPrefGoodFBContent.setTitle(R.string.fb_content_command);
            mPrefGoodFBContent.setSummary(CipherlabSymbol.TransformMulit(mSetting.g_ReaderParam.mGoodFeedBackESC));
        } else {    //Text
            mPrefGoodFBContent.setTitle(R.string.fb_content_text);
            mPrefGoodFBContent.setSummary(mSetting.g_ReaderParam.mGoodFeedBackText);
        }
    }

    private void syncSettingToErrorFBCmdPref(int nType) {
        if(nType == 0) { //Command
            mPrefErrorFBContent.setTitle(R.string.fb_content_command);
            mPrefErrorFBContent.setSummary(CipherlabSymbol.TransformMulit(mSetting.g_ReaderParam.mErrorFeedBackESC));
        } else {    //Text
            mPrefErrorFBContent.setTitle(R.string.fb_content_text);
            mPrefErrorFBContent.setSummary(mSetting.g_ReaderParam.mErrorFeedBackText);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_vt_feedback);

        mLstGoodFBType = (ListPreference) findPreference(getResources().getString(R.string.fb_good_key));
        mPrefGoodFBContent = findPreference(getResources().getString(R.string.fb_good_content_key));
        mLstErrorFBType = (ListPreference) findPreference(getResources().getString(R.string.fb_error_key));
        mPrefErrorFBContent = findPreference(getResources().getString(R.string.fb_error_content_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mLstGoodFBType.setValue(String.valueOf(mSetting.g_ReaderParam.mGoodFBType));
        syncSettingToGoodFBCmdPref(mSetting.g_ReaderParam.mGoodFBType);
        mLstErrorFBType.setValue(String.valueOf(mSetting.g_ReaderParam.mErrorFBType));
        syncSettingToErrorFBCmdPref(mSetting.g_ReaderParam.mErrorFBType);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.fb_good_key)) == 0) {
            String selGoodFBType = mLstGoodFBType.getValue();
            mSetting.g_ReaderParam.mGoodFBType = Integer.valueOf(selGoodFBType);
            syncSettingToGoodFBCmdPref(mSetting.g_ReaderParam.mGoodFBType);
        } else if(key.compareTo(getResources().getString(R.string.fb_error_key)) == 0) {
            String selErrFBType = mLstErrorFBType.getValue();
            mSetting.g_ReaderParam.mErrorFBType = Integer.valueOf(selErrFBType);
            syncSettingToErrorFBCmdPref(mSetting.g_ReaderParam.mErrorFBType);
        }
    }
}
