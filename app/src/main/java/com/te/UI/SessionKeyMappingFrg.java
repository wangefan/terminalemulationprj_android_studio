package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettings;

public class SessionKeyMappingFrg extends Fragment {
    //Data members
    protected TESettings.SessionSetting mSetting = null;

    public SessionKeyMappingFrg() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View keyMappingView = inflater.inflate(R.layout.pref_key_mapping, container, false);


        return keyMappingView;
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

}
