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

public class SessionVTReaderCtrlFrg extends SessionSettingsFrgBase {
    private final int REQ_ENABLE = 0;
    private final int REQ_DISABLE = 1;

    private Preference mPrefEnableCmd = null;
    private Preference mPrefDisableCmd = null;
    private TESwitchPreference mPrefSoundFile = null;
    private ListPreference mLstVBDur = null;

	public SessionVTReaderCtrlFrg() {
    }

    private void syncSettingToCmdPref(String cmd, Preference prefCmd) {
        prefCmd.setSummary(cmd);
    }

    private void syncSettingToSoundPref(String fullPath) {
        File file = new File(fullPath);
        mPrefSoundFile.setSummary(file.getName());
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
        addPreferencesFromResource(R.xml.pref_vt_reader_control);

        mPrefEnableCmd = findPreference(getResources().getString(R.string.fb_enable_reader_key));
        mPrefEnableCmd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActForResult(mSetting.g_ReaderParam.mScannerEnableCmd, REQ_ENABLE);
                return true;
            }
        });
        mPrefDisableCmd = findPreference(getResources().getString(R.string.fb_disable_reader_key));
        mPrefDisableCmd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActForResult(mSetting.g_ReaderParam.mScannerDisableCmd, REQ_DISABLE);
                return true;
            }
        });
        mPrefSoundFile = (TESwitchPreference) findPreference(getResources().getString(R.string.fb_reader_sound_key));
        mPrefSoundFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(mPrefSoundFile.isChecked() == false) {
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
                                mSetting.g_ReaderParam.mScannerSoundFile = chosenDir;
                                syncSettingToSoundPref(mSetting.g_ReaderParam.mScannerSoundFile);
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

                FileOpenDialog.chooseFile_or_Dir(mSetting.g_ReaderParam.mScannerSoundFile);
                return true;
            }
        });
        mLstVBDur = (ListPreference) findPreference(getResources().getString(R.string.reader_vib_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        syncSettingToCmdPref(CipherlabSymbol.TransformMulit(mSetting.g_ReaderParam.mScannerEnableCmd), mPrefEnableCmd);
        syncSettingToCmdPref(CipherlabSymbol.TransformMulit(mSetting.g_ReaderParam.mScannerDisableCmd), mPrefDisableCmd);
        syncSettingToSoundPref(mSetting.g_ReaderParam.mScannerSoundFile);
        mPrefSoundFile.setChecked(mSetting.g_ReaderParam.mbUseScannerSoundFile);
        mLstVBDur.setValue(String.valueOf(mSetting.g_ReaderParam.mScannerVBIndex));
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.reader_vib_key)) == 0) {
            String selVBIdx = mLstVBDur.getValue();
            mSetting.g_ReaderParam.mScannerVBIndex = Integer.valueOf(selVBIdx);
        } else if(key.compareTo(getResources().getString(R.string.fb_reader_sound_key)) == 0) {
            mSetting.g_ReaderParam.mbUseScannerSoundFile = mPrefSoundFile.isChecked();
        }
        ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if(requestCode == REQ_ENABLE) {
                    Bundle bundle = data.getExtras();
                    mSetting.g_ReaderParam.mScannerEnableCmd = bundle.getString("data");
                    syncSettingToCmdPref(CipherlabSymbol.TransformMulit(mSetting.g_ReaderParam.mScannerEnableCmd), mPrefEnableCmd);
                } else if(requestCode == REQ_DISABLE) {
                    Bundle bundle = data.getExtras();
                    mSetting.g_ReaderParam.mScannerDisableCmd = bundle.getString("data");
                    syncSettingToCmdPref(CipherlabSymbol.TransformMulit(mSetting.g_ReaderParam.mScannerDisableCmd), mPrefDisableCmd);
                }
                ((Session3rdSettings)getActivity()).gIsAlarmModified = true;
            break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
