package com.te.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import com.cipherlab.terminalemulation.R;

public class SessionVTSettingsFrg extends SessionSettingsFrgBase {

    //Data members
    private CheckBoxPreference mChkUpperCase = null;
    private CheckBoxPreference mChkLineBuffer = null;
    private CheckBoxPreference mChkEcho = null;
    private Preference mPrefSendString = null;
    private ListPreference mlstPrefCharSet = null;
    private TESwitchPreference mReplaceCharIfUnconvert = null;

    public SessionVTSettingsFrg() {
    }

    private void syncSettingToSendingStringPref() {
        mPrefSendString.setSummary(CipherlabSymbol.TransformMulit(mSetting.mVTSendtoHost));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        mChkUpperCase.setChecked(mSetting.mBUpperCase);
        mChkLineBuffer.setChecked(mSetting.mLineBuffer == 1);
        mChkEcho.setChecked(mSetting.mBEcho);
        mlstPrefCharSet.setValue(String.valueOf(mSetting.mNCharSet));
        mReplaceCharIfUnconvert.setEnabled(mSetting.mNCharSet == 1);
        mReplaceCharIfUnconvert.setChecked(mSetting.mBReplaceIfUnconvert);
        mReplaceCharIfUnconvert.setSummaryOn(String.valueOf(mSetting.mReplaceChar));
        syncSettingToSendingStringPref();
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.data_upper_case_key)) == 0) {
            mSetting.mBUpperCase = mChkUpperCase.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.vt_linebuffer_key)) == 0) {
            mSetting.mLineBuffer = mChkLineBuffer.isChecked() ? 1 : 0;
        } else if(key.compareTo(getResources().getString(R.string.vt_echo_key)) == 0) {
            mSetting.mBEcho = mChkEcho.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.vt_char_set_key)) == 0) {
            mSetting.mNCharSet = Integer.valueOf(mlstPrefCharSet.getValue());
            mReplaceCharIfUnconvert.setEnabled(mSetting.mNCharSet == 1);
        } else if(key.compareTo(getResources().getString(R.string.vt_replace_if_unconvert_key)) == 0) {
            mSetting.mBReplaceIfUnconvert = mReplaceCharIfUnconvert.isChecked();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_vt_settings);

        //UI
        mChkUpperCase = (CheckBoxPreference) findPreference(getResources().getString(R.string.data_upper_case_key));
        mChkLineBuffer = (CheckBoxPreference) findPreference(getResources().getString(R.string.vt_linebuffer_key));
        mChkEcho = (CheckBoxPreference) findPreference(getResources().getString(R.string.vt_echo_key));
        mPrefSendString = findPreference(getResources().getString(R.string.vt_send_string_key));
        mPrefSendString.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent screen = new Intent(getActivity(), SymbolActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("data", mSetting.mVTSendtoHost);
                bundle.putString("Encode", "windows-1252");
                bundle.putInt("limit", 10);
                bundle.putInt("Select", 2);
                screen.putExtras(bundle);
                startActivityForResult(screen, 0);
                return true;
            }
        });
        mlstPrefCharSet = (ListPreference) findPreference(getResources().getString(R.string.vt_char_set_key));
        mReplaceCharIfUnconvert = (TESwitchPreference) findPreference(getResources().getString(R.string.vt_replace_if_unconvert_key));
        mReplaceCharIfUnconvert.setOnTESwitchListener(new TESwitchPreference.OnTESwitchListener() {
            @Override
            public void onClick() {
                int nSelItem = 0;
                String[] items = getResources().getStringArray(R.array.replace_char_arr);
                for (int idxChar = 0; idxChar < items.length; idxChar++) {
                    if(items[idxChar].compareTo(mSetting.mReplaceChar) == 0) {
                        nSelItem = idxChar;
                    }
                }
                UIUtility.listMessageBox(R.string.vt_replace_if_unconvert,
                        R.array.replace_char_arr,
                        nSelItem,
                        getActivity(),
                        new UIUtility.OnListMessageBoxListener() {
                            @Override
                            public void onSelResult(String result, int nSelIdx) {
                                mSetting.mReplaceChar = result;
                                mSetting.mBReplaceIfUnconvert = true;
                                mReplaceCharIfUnconvert.setSummaryOn(result);
                                mReplaceCharIfUnconvert.setChecked(true);
                            }
                        });
            }

            @Override
            public void onChecked(boolean isChecked) {
                mSetting.mBReplaceIfUnconvert = isChecked;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                Bundle bundle = data.getExtras();
                mSetting.mVTSendtoHost = bundle.getString("data");
                syncSettingToSendingStringPref();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
