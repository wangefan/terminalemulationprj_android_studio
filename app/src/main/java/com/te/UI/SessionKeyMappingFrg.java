package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cipherlab.terminalemulation.R;

import Terminals.KeyMapList;
import Terminals.KeyMapListAdapter;
import Terminals.TESettings;
import Terminals.TESettingsInfo;
import Terminals.stdActivityRef;

public class SessionKeyMappingFrg extends Fragment {
    //Data members
    protected TESettings.SessionSetting mSetting = null;

    private ListView mKeyMapListView = null;

    public SessionKeyMappingFrg() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View keyMappingView = inflater.inflate(R.layout.pref_key_mapping, container, false);
        mKeyMapListView = (ListView) keyMappingView.findViewById(R.id.key_list);
        KeyMapList curKeyList = TESettingsInfo.getKeyMapListByIndex(TESettingsInfo.getSessionIndex());
        KeyMapListAdapter adapter = new KeyMapListAdapter(stdActivityRef.getCurrActivity(), curKeyList);
        mKeyMapListView.setAdapter(adapter);
        return keyMappingView;
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

}
