package com.te.UI;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.cipherlab.terminalemulation.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SessionTNAlarmFrg extends SessionSettingsFrgBase {
    private Preference mPrefMsgAlarmSound = null;
    private ListPreference mListMsgVB = null;
    private Preference mPrefErrAlarmSound = null;
    private ListPreference mListErrVB = null;
	public SessionTNAlarmFrg() {
    }

    private void syncSettingToSoundPref(String fullPath, Preference pref) {
        File file = new File(fullPath);
        pref.setSummary(file.getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_tn_alarm);
        mPrefMsgAlarmSound = findPreference(getResources().getString(R.string.fb_tn_msg_sound_key));
        mPrefMsgAlarmSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SimpleFileDialog FileOpenDialog = new SimpleFileDialog(getActivity(),
                        getResources().getString(R.string.STR_Choose_Audio),
                        new ArrayList<>(Arrays.asList(getResources().getString(R.string.STR_ExtWav),
                                getResources().getString(R.string.STR_ExtMp3),
                                getResources().getString(R.string.STR_ExtOgg))),
                        SimpleFileDialog.Type.FILE_CHOOSE,
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onFilePath(String chosenDir) {
                                mSetting.g_ReaderParam.mGoodSoundFile = chosenDir;
                                syncSettingToSoundPref(mSetting.g_ReaderParam.mGoodSoundFile, mPrefMsgAlarmSound);
                            }

                            @Override
                            public void onFileSel(String path) {
                                CipherUtility.playSound(path);
                            }

                            @Override
                            public void onFileSelNext(String path) {
                            }
                        });

                FileOpenDialog.chooseFile_or_Dir(mSetting.g_ReaderParam.mGoodSoundFile);
                return true;
            }
        });
        mListMsgVB = (ListPreference) findPreference(getResources().getString(R.string.fb_tn_msg_vib_key));
        mPrefErrAlarmSound = findPreference(getResources().getString(R.string.fb_tn_err_sound_key));
        mPrefErrAlarmSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SimpleFileDialog FileOpenDialog = new SimpleFileDialog(getActivity(),
                        getResources().getString(R.string.STR_Choose_Audio),
                        new ArrayList<>(Arrays.asList(getResources().getString(R.string.STR_ExtWav),
                                getResources().getString(R.string.STR_ExtMp3),
                                getResources().getString(R.string.STR_ExtOgg))),
                        SimpleFileDialog.Type.FILE_CHOOSE,

                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onFilePath(String chosenDir) {
                                mSetting.g_ReaderParam.mErrorSoundFile = chosenDir;
                                syncSettingToSoundPref(mSetting.g_ReaderParam.mErrorSoundFile, mPrefErrAlarmSound);
                            }

                            @Override
                            public void onFileSel(String path) {
                                CipherUtility.playSound(path);
                            }

                            @Override
                            public void onFileSelNext(String path) {
                            }
                        });

                FileOpenDialog.chooseFile_or_Dir(mSetting.g_ReaderParam.mErrorSoundFile);
                return true;
            }
        });
        mListErrVB = (ListPreference) findPreference(getResources().getString(R.string.fb_tn_err_vib_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        syncSettingToSoundPref(mSetting.g_ReaderParam.mGoodSoundFile, mPrefMsgAlarmSound);
        mListMsgVB.setValue(String.valueOf(mSetting.g_ReaderParam.mGoodVBIndex));
        syncSettingToSoundPref(mSetting.g_ReaderParam.mErrorSoundFile, mPrefErrAlarmSound);
        mListErrVB.setValue(String.valueOf(mSetting.g_ReaderParam.mErrorVBIndex));
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.fb_tn_msg_vib_key)) == 0) {
            String selMsgVBIdx = mListMsgVB.getValue();
            mSetting.g_ReaderParam.mGoodVBIndex = Integer.valueOf(selMsgVBIdx);
        } else if(key.compareTo(getResources().getString(R.string.fb_tn_err_vib_key)) == 0) {
            String selErrVBIdx = mListErrVB.getValue();
            mSetting.g_ReaderParam.mErrorVBIndex = Integer.valueOf(selErrVBIdx);
        }
    }
}
