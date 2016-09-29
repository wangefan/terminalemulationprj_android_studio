package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;

import com.cipherlab.terminalemulation.R;

public class SessionSSHKeyFileFrg extends SessionSettingsFrgBase {
    private EditTextPreference medtSSHName = null;
    private CheckBoxPreference mchkSSHTcpNoDelay = null;
    private CheckBoxPreference mchkSSHTcpNoPseudoKey = null;
    private CheckBoxPreference mchkSSHTcpNoHostShellKey = null;
    private CheckBoxPreference mchkSSHReKey60 = null;
    private CheckBoxPreference mchkSSHReKey1G = null;
    private CheckBoxPreference mchkSSHOverwriteLog = null;

    public SessionSSHKeyFileFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh_key_file);

        medtSSHName = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_name_key));
        mchkSSHTcpNoDelay = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_use_tcp_delay_key));
        mchkSSHTcpNoPseudoKey = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_no_pseudo_key));
        mchkSSHTcpNoHostShellKey = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_no_host_shell_key));
        mchkSSHReKey60 = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_re_key_60_key));
        mchkSSHReKey1G = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_re_key_1g_key));
        mchkSSHOverwriteLog = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_write_log_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        medtSSHName.setSummary(mSetting.mSSHName);
        mchkSSHTcpNoDelay.setChecked(mSetting.mSSHTcpNoDelay);
        mchkSSHTcpNoPseudoKey.setChecked(mSetting.mSSHNoPseudoTer);
        mchkSSHTcpNoHostShellKey.setChecked(mSetting.mSSHNoHostShell);
        mchkSSHReKey60.setChecked(mSetting.mSSHReKey60min);
        mchkSSHReKey1G.setChecked(mSetting.mSSHReKey1G);
        mchkSSHOverwriteLog.setChecked(mSetting.mSSHLogOverwrite);
    }

    @Override
    protected void commitPrefUIToTESettings(String key) {
        if(key.compareTo(getResources().getString(R.string.ssh_name_key)) == 0) {
            mSetting.mSSHName = medtSSHName.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_use_tcp_delay_key)) == 0) {
            mSetting.mSSHTcpNoDelay = mchkSSHTcpNoDelay.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_no_pseudo_key)) == 0) {
            mSetting.mSSHNoPseudoTer = mchkSSHTcpNoPseudoKey.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_no_host_shell_key)) == 0) {
            mSetting.mSSHNoHostShell = mchkSSHTcpNoHostShellKey.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_re_key_60_key)) == 0) {
            mSetting.mSSHReKey60min = mchkSSHReKey60.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_re_key_1g_key)) == 0) {
            mSetting.mSSHReKey1G = mchkSSHReKey1G.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_write_log_key)) == 0) {
            mSetting.mSSHLogOverwrite = mchkSSHOverwriteLog.isChecked();
        }
    }
}
