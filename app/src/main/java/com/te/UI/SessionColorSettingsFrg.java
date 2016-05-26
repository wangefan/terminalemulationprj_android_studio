package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.terminalemulation.R;

import Terminals.TESettings;

public class SessionColorSettingsFrg extends Fragment {

    //Data members
    protected TESettings.SessionSetting mSetting = null;

    public SessionColorSettingsFrg() {
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View colorSettingView = inflater.inflate(R.layout.color_setting_fragment, container, false);

        return colorSettingView;
    }
}
