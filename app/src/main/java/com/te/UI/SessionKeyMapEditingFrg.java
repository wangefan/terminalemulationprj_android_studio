package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cipherlab.terminalemulation.R;

import Terminals.TESettings;

public class SessionKeyMapEditingFrg extends Fragment {

    //Data members
    protected TESettings.SessionSetting mSetting = null;

	public SessionKeyMapEditingFrg() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View keyMappingEdtView = inflater.inflate(R.layout.pref_key_mapping_editing, container, false);
        return keyMappingEdtView;
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }
}
