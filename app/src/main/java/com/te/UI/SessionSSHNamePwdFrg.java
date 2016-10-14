package com.te.UI;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;

import com.cipherlab.terminalemulation.R;

public class SessionSSHNamePwdFrg extends SessionSettingsFrgBase {
    private EditTextPreference medtSSHName = null;
    private EditTextPreference medtSSHPwd = null;
    private CheckBoxPreference mchkSSHTcpNoDelay = null;
    private CheckBoxPreference mchkSSHTcpNoPseudoKey = null;
    private CheckBoxPreference mchkSSHTcpNoHostShellKey = null;
    private CheckBoxPreference mchkSSHOverwriteLog = null;
    private EditTextPreference medtServerEnvir = null;
    private EditTextPreference medtServerCmds = null;
    private EditTextPreference medtServerTTY = null;
    private EditTextPreference medtProxyType = null;
    private EditTextPreference medtProxyHost = null;
    private EditTextPreference medtProxyPort = null;
    private EditTextPreference medtProxyUser = null;
    private EditTextPreference medtProxyPwd = null;
    public SessionSSHNamePwdFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_ssh_name_pwd);

        medtSSHName = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_name_key));
        medtSSHPwd = (EditTextPreference) findPreference(getResources().getString(R.string.ssh_pwd_key));

        mchkSSHTcpNoDelay = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_use_tcp_delay_key));
        mchkSSHTcpNoPseudoKey = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_no_pseudo_key));
        mchkSSHTcpNoHostShellKey = (CheckBoxPreference) findPreference(getResources().getString(R.string.ssh_no_host_shell_key));
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
    protected void updatePrefSummary(Preference p) {
        super.updatePrefSummary(p);
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getKey().compareTo((getResources().getString(R.string.ssh_pwd_key))) == 0) {
                p.setSummary(CipherUtility.getNumStar(editTextPref.getText()));
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
    }

    @Override
    protected void syncPrefUIFromTESettings() {
        medtSSHName.setText(mSetting.mSSHName);
        medtSSHPwd.setText(mSetting.mSSHPassword);

        mchkSSHTcpNoDelay.setChecked(mSetting.mSSHTcpNoDelay);
        mchkSSHTcpNoPseudoKey.setChecked(mSetting.mSSHNoPseudoTer);
        mchkSSHTcpNoHostShellKey.setChecked(mSetting.mSSHNoHostShell);
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
        } else if(key.compareTo(getResources().getString(R.string.ssh_pwd_key)) == 0) {
            mSetting.mSSHPassword = medtSSHPwd.getText();
        } else if(key.compareTo(getResources().getString(R.string.ssh_use_tcp_delay_key)) == 0) {
            mSetting.mSSHTcpNoDelay = mchkSSHTcpNoDelay.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_no_pseudo_key)) == 0) {
            mSetting.mSSHNoPseudoTer = mchkSSHTcpNoPseudoKey.isChecked();
        } else if(key.compareTo(getResources().getString(R.string.ssh_no_host_shell_key)) == 0) {
            mSetting.mSSHNoHostShell = mchkSSHTcpNoHostShellKey.isChecked();
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
