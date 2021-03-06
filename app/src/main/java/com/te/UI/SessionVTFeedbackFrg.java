package com.te.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

import com.cipherlab.terminalemulation.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SessionVTFeedbackFrg extends SessionSettingsFrgBase {
    private final int REQ_GOOD = 0;
    private final int REQ_ERROR = 1;

    private ListPreference mLstGoodFBType = null;
    private Preference mPrefGoodFBContent = null;
    private TESwitchPreference mPrefGoodSound = null;
    private ListPreference mLstGoodVBDur = null;
    private ListPreference mLstErrorFBType = null;
    private Preference mPrefErrorFBContent = null;
    private TESwitchPreference mPrefErrSound = null;
    private ListPreference mLstErrVBDur = null;

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

    private void syncSettingToGoodSoundPref(String fullPath) {
        File file = new File(fullPath);
        mPrefGoodSound.setSummary(file.getName());
    }

    private void syncSettingToErrSoundPref(String fullPath) {
        File file = new File(fullPath);
        mPrefErrSound.setSummary(file.getName());
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

    private void startActForResult(String data, int nReqCode) {
        Intent screen = new Intent(getActivity(), SymbolActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        bundle.putString("Encode", "windows-1252");
        bundle.putInt("limit", 10);
        bundle.putInt("Select", 2);
        screen.putExtras(bundle);
        startActivityForResult(screen, nReqCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_vt_feedback);

        mLstGoodFBType = (ListPreference) findPreference(getResources().getString(R.string.fb_good_key));
        mPrefGoodFBContent = findPreference(getResources().getString(R.string.fb_good_content_key));
        mPrefGoodFBContent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int nType = Integer.valueOf(mLstGoodFBType.getValue());
                if(nType == 0) { //Command
                    startActForResult(mSetting.g_ReaderParam.mGoodFeedBackESC, REQ_GOOD);
                } else if(nType == 1) { //Text
                    UIUtility.editMessageBox(R.string.fb_good_fb_text_title, mSetting.g_ReaderParam.mGoodFeedBackText, getActivity(), new UIUtility.OnEditMessageBoxListener() {
                        @Override
                        public void onResult(String result) {
                            mSetting.g_ReaderParam.mGoodFeedBackText = result;
                            syncSettingToGoodFBCmdPref(mSetting.g_ReaderParam.mGoodFBType);
                            ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
                return true;
            }
        });
        mPrefGoodSound = (TESwitchPreference) findPreference(getResources().getString(R.string.fb_good_sound_key));
        mPrefGoodSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(mPrefGoodSound.isChecked() == false) {
                    return true;
                }
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
                                syncSettingToGoodSoundPref(mSetting.g_ReaderParam.mGoodSoundFile);
                                ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
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
        mLstGoodVBDur = (ListPreference) findPreference(getResources().getString(R.string.goodvib_key));
        mLstErrorFBType = (ListPreference) findPreference(getResources().getString(R.string.fb_error_key));
        mPrefErrorFBContent = findPreference(getResources().getString(R.string.fb_error_content_key));
        mPrefErrorFBContent.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int nType = Integer.valueOf(mLstErrorFBType.getValue());
                if(nType == 0) { //Command
                    startActForResult(mSetting.g_ReaderParam.mErrorFeedBackESC, REQ_ERROR);
                } else if(nType == 1) { //Text
                    UIUtility.editMessageBox(R.string.fb_error_fb_text_title, mSetting.g_ReaderParam.mErrorFeedBackText, getActivity(), new UIUtility.OnEditMessageBoxListener() {
                        @Override
                        public void onResult(String result) {
                            mSetting.g_ReaderParam.mErrorFeedBackText = result;
                            syncSettingToErrorFBCmdPref(mSetting.g_ReaderParam.mErrorFBType);
                            ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
                return true;
            }
        });
        mPrefErrSound = (TESwitchPreference) findPreference(getResources().getString(R.string.fb_err_sound_key));
        mPrefErrSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(mPrefErrSound.isChecked() == false) {
                    return true;
                }
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
                                syncSettingToErrSoundPref(mSetting.g_ReaderParam.mErrorSoundFile);
                                ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
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
        mLstErrVBDur = (ListPreference) findPreference(getResources().getString(R.string.errvib_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mLstGoodFBType.setValue(String.valueOf(mSetting.g_ReaderParam.mGoodFBType));
        syncSettingToGoodFBCmdPref(mSetting.g_ReaderParam.mGoodFBType);
        syncSettingToGoodSoundPref(mSetting.g_ReaderParam.mGoodSoundFile);
        mPrefGoodSound.setChecked(mSetting.g_ReaderParam.mbUseGoodSoundFile);
        mLstGoodVBDur.setValue(String.valueOf(mSetting.g_ReaderParam.mGoodVBIndex));
        mLstErrorFBType.setValue(String.valueOf(mSetting.g_ReaderParam.mErrorFBType));
        syncSettingToErrorFBCmdPref(mSetting.g_ReaderParam.mErrorFBType);
        syncSettingToErrSoundPref(mSetting.g_ReaderParam.mErrorSoundFile);
        mPrefErrSound.setChecked(mSetting.g_ReaderParam.mbUseErrSoundFile);
        mLstErrVBDur.setValue(String.valueOf(mSetting.g_ReaderParam.mErrorVBIndex));
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.fb_good_key)) == 0) {
            String selGoodFBType = mLstGoodFBType.getValue();
            mSetting.g_ReaderParam.mGoodFBType = Integer.valueOf(selGoodFBType);
            syncSettingToGoodFBCmdPref(mSetting.g_ReaderParam.mGoodFBType);
        } else if(key.compareTo(getResources().getString(R.string.fb_good_sound_key)) == 0) {
            mSetting.g_ReaderParam.mbUseGoodSoundFile = mPrefGoodSound.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.fb_error_key)) == 0) {
            String selErrFBType = mLstErrorFBType.getValue();
            mSetting.g_ReaderParam.mErrorFBType = Integer.valueOf(selErrFBType);
            syncSettingToErrorFBCmdPref(mSetting.g_ReaderParam.mErrorFBType);
        } else if(key.compareTo(getResources().getString(R.string.fb_err_sound_key)) == 0) {
            mSetting.g_ReaderParam.mbUseErrSoundFile = mPrefErrSound.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.goodvib_key)) == 0) {
            String selGoodVBIdx = mLstGoodVBDur.getValue();
            mSetting.g_ReaderParam.mGoodVBIndex = Integer.valueOf(selGoodVBIdx);
        } else if(key.compareTo(getResources().getString(R.string.errvib_key)) == 0) {
            String selErrVBIdx = mLstErrVBDur.getValue();
            mSetting.g_ReaderParam.mErrorVBIndex = Integer.valueOf(selErrVBIdx);
        }
        ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if(requestCode == REQ_GOOD) {
                    Bundle bundle = data.getExtras();
                    mSetting.g_ReaderParam.mGoodFeedBackESC = bundle.getString("data");
                    syncSettingToGoodFBCmdPref(mSetting.g_ReaderParam.mGoodFBType);
                } else if(requestCode == REQ_ERROR) {
                    Bundle bundle = data.getExtras();
                    mSetting.g_ReaderParam.mErrorFeedBackESC = bundle.getString("data");
                    syncSettingToErrorFBCmdPref(mSetting.g_ReaderParam.mErrorFBType);
                }
                ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
            break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
