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
    private EditTextPreference medtServerEnvir = null;
    private EditTextPreference medtServerCmds = null;
    private EditTextPreference medtServerTTY = null;
    private EditTextPreference medtProxyType = null;
    private EditTextPreference medtProxyHost = null;
    private EditTextPreference medtProxyPort = null;
    private EditTextPreference medtProxyUser = null;
    private EditTextPreference medtProxyPwd = null;

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

        medtServerEnvir = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_envir_key));
        medtServerCmds = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_cmd_key));
        medtServerTTY = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_tty_key));
        medtProxyType = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_proxy_type_key));
        medtProxyHost = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_proxy_host_key));
        medtProxyPort = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_proxy_port_key));
        medtProxyUser = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_proxy_user_key));
        medtProxyPwd = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_proxy_pwd_key));
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        medtSSHName.setText(mSetting.mSSHName);
        mchkSSHTcpNoDelay.setChecked(mSetting.mSSHTcpNoDelay);
        mchkSSHTcpNoPseudoKey.setChecked(mSetting.mSSHNoPseudoTer);
        mchkSSHTcpNoHostShellKey.setChecked(mSetting.mSSHNoHostShell);
        mchkSSHReKey60.setChecked(mSetting.mSSHReKey60min);
        mchkSSHReKey1G.setChecked(mSetting.mSSHReKey1G);
        mchkSSHOverwriteLog.setChecked(mSetting.mSSHLogOverwrite);
        medtServerEnvir.setText(mSetting.mSSHServerEnv);
        medtServerCmds.setText(mSetting.mSSHServerCommand);
        medtServerTTY.setText(mSetting.mSSHServerTTY);
        medtProxyType.setText(mSetting.mSSHProxyType);
        medtProxyHost.setText(mSetting.mSSHProxyHost);
        medtProxyPort.setText(mSetting.mSSHProxyPort);
        medtProxyUser.setText(mSetting.mSSHProxyUser);
        medtProxyPwd.setText(mSetting.mSSHProxyPassword);
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
        } else if(key.compareTo(getResources().getString(R.string.ssh_envir_key)) == 0) {
            mSetting.mSSHServerEnv = medtServerEnvir.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_cmd_key)) == 0) {
            mSetting.mSSHServerCommand = medtServerCmds.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_tty_key)) == 0) {
            mSetting.mSSHServerTTY = medtServerTTY.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_proxy_type_key)) == 0) {
            mSetting.mSSHProxyType = medtProxyType.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_proxy_host_key)) == 0) {
            mSetting.mSSHProxyHost = medtProxyHost.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_proxy_port_key)) == 0) {
            mSetting.mSSHProxyPort = medtProxyPort.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_proxy_user_key)) == 0) {
            mSetting.mSSHProxyUser = medtProxyUser.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_proxy_pwd_key)) == 0) {
            mSetting.mSSHProxyPassword = medtProxyPwd.getText();
        }
    }
}
