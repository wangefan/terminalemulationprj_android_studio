package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cipherlab.terminalemulation.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
        mlstKeyFilesView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SimpleFileDialog FileOpenDialog = new SimpleFileDialog(getActivity(),
                        getResources().getString(R.string.ssh_add_key_files_title),
                        new ArrayList<>(Arrays.asList(getResources().getString(R.string.STR_ExtPpk),
                                getResources().getString(R.string.STR_ExtPem))),
                        SimpleFileDialog.Type.FILE_WIZAERD,
                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onFilePath(String chosenDir) {
                            }

                            @Override
                            public void onFileSel(String path) {

                            }

                            @Override
                            public void onFileSelNext(String path) {
                                File f = new File(path);
                                if(f.isFile() && f.exists()) {
                                    TESettingsInfo.setSSHKeyPath(path);
                                }
                            }
                        });

                FileOpenDialog.chooseFile_or_Dir(TESettingsInfo.getSSHKeyPath());
            }
        });
        return mgrKeyFilesView;
    }
}
