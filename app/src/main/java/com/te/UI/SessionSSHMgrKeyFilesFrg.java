package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cipherlab.terminalemulation.R;

import java.util.ArrayList;

import Terminals.TESettings;
import Terminals.TESettingsInfo;

public class SessionSSHMgrKeyFilesFrg extends Fragment {
    //Data members
    protected TESettings.SessionSetting mSetting = null;
    private ListView mlstKeyFilesView = null;
    private ArrayList<String> mlstKeyFiles = new ArrayList<>();

    public SessionSSHMgrKeyFilesFrg() {

    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mgrKeyFilesView = inflater.inflate(R.layout.ssh_mgr_key_files, container, false);
        LinearLayout laySelectKey = (LinearLayout) mgrKeyFilesView.findViewById(R.id.id_lay_select_key);
        laySelectKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo:select key from list
            }
        });
        //Fill Key Files
        ArrayList<TESettings.TECommonSetting.CSsh_Keys> sshList = TESettingsInfo.getCommonSSHKeys();
        for (TESettings.TECommonSetting.CSsh_Keys ssh : sshList) {
            mlstKeyFiles.add(ssh.mPath);
        }
        mlstKeyFiles.add("add");//dummy item for "add" icon
        mlstKeyFilesView = (ListView) mgrKeyFilesView.findViewById(R.id.id_key_files_list);
        mlstKeyFilesView.setAdapter(new KeyFilesListAdapter(getActivity(), mlstKeyFiles));
        return mgrKeyFilesView;
    }
}
