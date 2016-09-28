package com.te.UI;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.cipherlab.terminalemulation.R;

public class Session3rdSettings extends SessionSettingsBase {
    public static final String ACTION_COLOR = "com.te.UI.Session3rdSettings.ACTION_COLOR";
    public static final String ACTION_READER_CTRL = "com.te.UI.Session3rdSettings.ACTION_READER_CTRL";
    public static final String ACTION_FEEDBACK = "com.te.UI.Session3rdSettings.ACTION_FEEDBACK";
    public static final String ACTION_KEYMAP_EDIT = "com.te.UI.Session3rdSettings.ACTION_KEYMAP_EDIT";
    public static final String ACTION_KEYMAP_EDIT_SERVER_KEYCODE = "com.te.UI.Session3rdSettings.ACTION_KEYMAP_EDIT_SERVER_KEYCODE";
    public static final String ACTION_SSH_NAME_PWD = "com.te.UI.Session3rdSettings.ACTION_SSH_NAME_PWD";
    public static final String ACTION_SSH_KEY_FILE = "com.te.UI.Session3rdSettings.ACTION_SSH_KEY_FILE";
    public static boolean gIsAlarmModified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_3rd_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);

        //To determine action bar.
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         // Display the fragment as the main content.
        String action = getIntent().getAction();
        if(action.compareTo(ACTION_COLOR) == 0) {
            getSupportActionBar().setTitle(getResources().getString(R.string.screen_color));
            SessionColorSettingsFrg settingsFrg = getFragment(SessionColorSettingsFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        } else if(action.compareTo(ACTION_READER_CTRL) == 0) {
            gIsAlarmModified = false;
            getSupportActionBar().setTitle(getResources().getString(R.string.vt_control_reader));
            SessionVTReaderCtrlFrg settingsFrg = getFragment(SessionVTReaderCtrlFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        } else if(action.compareTo(ACTION_FEEDBACK) == 0) {
            gIsAlarmModified = false;
            getSupportActionBar().setTitle(getResources().getString(R.string.vt_feedback));
            SessionVTFeedbackFrg settingsFrg = getFragment(SessionVTFeedbackFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        } else if(action.compareTo(ACTION_KEYMAP_EDIT) == 0) {
            int nEditServerKeycode = getIntent().getIntExtra(ACTION_KEYMAP_EDIT_SERVER_KEYCODE, 0);
            getSupportActionBar().setTitle(getResources().getString(R.string.keymap_edit));
            SessionKeyMapEditingFrg settingsFrg = getFragment(SessionKeyMapEditingFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            settingsFrg.setServerKeyCode(nEditServerKeycode);
            commitFrgToActivity(settingsFrg);
        } else if(action.compareTo(ACTION_SSH_NAME_PWD) == 0) {
            gIsAlarmModified = false;
            getSupportActionBar().setTitle(getResources().getString(R.string.ssh_set_name_pwd));
            SessionSSHNamePwdFrg settingsFrg = getFragment(SessionSSHNamePwdFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        } else if(action.compareTo(ACTION_SSH_KEY_FILE) == 0) {
            gIsAlarmModified = false;
            getSupportActionBar().setTitle(getResources().getString(R.string.ssh_set_key_file));
            SessionSSHKeyFileFrg settingsFrg = getFragment(SessionSSHKeyFileFrg.class);
            settingsFrg.setSessionSetting(SessionSettings.gEditSessionSetting);
            commitFrgToActivity(settingsFrg);
        }
    }
}
