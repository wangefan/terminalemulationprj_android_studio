package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettings;

public class SessionSSHMgrKeyFilesFrg extends Fragment {
    //Data members
    protected TESettings.SessionSetting mSetting = null;

    public SessionSSHMgrKeyFilesFrg() {

    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mgrKeyFilesView = inflater.inflate(R.layout.ssh_mgr_key_files, container, false);
        return mgrKeyFilesView;
    }
}
