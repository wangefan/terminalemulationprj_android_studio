package com.te.UI;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.chilkatsoft.CkSshKey;
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

    private boolean checkSSHKey(String keyPah, String passphrase) {
        System.loadLibrary("chilkat");
        CkSshKey key = new CkSshKey();
        String keyConent = key.loadText(keyPah);
        if(keyConent == null) {
            String str = key.lastErrorText();
            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
            return false;
        }
        if(passphrase.length() > 0) {
            key.put_Password(passphrase);
        }

        //0: open(pem) 1:putty(ppk)
        int nKeyFrom = getKeyType(new File(keyPah));
        boolean bResult = false;
        if(nKeyFrom == 0) {
            bResult = key.FromOpenSshPrivateKey(keyConent);
        } else if(nKeyFrom == 1) {
            bResult = key.FromPuttyPrivateKey(keyConent);
        }
        if(!bResult) {
            String str = key.lastErrorText();
            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
        }
        return bResult;
    }

    private void procImportSSH(File srcSSH, File destSSH, String passphrase) {
        CipherUtility.copyFile(srcSSH, destSSH);
        mlstKeyFiles.add(0, destSSH.getAbsolutePath());
        ((KeyFilesListAdapter)(mlstKeyFilesView.getAdapter())).notifyDataSetChanged();
        //commit to setting
        ArrayList<TESettings.CSsh_Keys> sshList = TESettingsInfo.getCommonSSHKeys();
        int nKeyFrom = getKeyType(destSSH);//0: open(pem) 1:putty(ppk)
        sshList.add(new TESettings.CSsh_Keys(destSSH.getName(), destSSH.getAbsolutePath(), passphrase, nKeyFrom));
        TESettingsInfo.setCommonSSHKeys(sshList);
        Toast.makeText(getActivity(), R.string.MSG_Import_ssh_ok, Toast.LENGTH_LONG).show();
        TESettingsInfo.setSSHKeyPath(srcSSH.getAbsolutePath());
    }

    //0: open(pem) 1:putty(ppk)
    private int getKeyType(File sshFile) {
        String extPpk = String.format(".%s", getActivity().getResources().getString(R.string.STR_ExtPpk));
        String extPem = String.format(".%s", getActivity().getResources().getString(R.string.STR_ExtPem));
        int nKeyFrom = 0;//0: open(pem) 1:putty(ppk)
        if(CipherUtility.isFileByExt(sshFile, extPem)) {
            nKeyFrom = 0;
        } else if(CipherUtility.isFileByExt(sshFile, extPpk)) {
            nKeyFrom = 1;
        }
        return nKeyFrom;
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
        ArrayList<TESettings.CSsh_Keys> sshList = TESettingsInfo.getCommonSSHKeys();
        for (TESettings.CSsh_Keys ssh : sshList) {
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
                                final String pathTemp = path;
                                UIUtility.doSSHConfirmPassphrase(getActivity(), new UIUtility.OnSSHEditPassphraseListener() {
                                    @Override
                                    public void onDone(String pass1, String pass2) {
                                        final File sshFile = new File(pathTemp);
                                        if(sshFile.exists() == false || sshFile.isFile() == false) {
                                            return;
                                        }
                                        if(pass1.compareTo(pass2) != 0) {
                                            //Todo: popup msg
                                            return;
                                        }
                                        final String pass = pass1;
                                        boolean bValidSSH = checkSSHKey(pathTemp, pass);
                                        if(bValidSSH == false) {
                                            //Todo: popup msg
                                            return;
                                        }
                                        String sshPathRoot = CipherUtility.getTESSHPath(getActivity());
                                        File f = new File(sshPathRoot);
                                        if(f.exists() == false) {
                                            f.mkdirs();
                                        }
                                        final String dstSshPath = sshPathRoot + sshFile.getName();
                                        final File destSSH = new File(dstSshPath);
                                        if(destSSH.exists()) {
                                            String strMsg = getResources().getString(R.string.Msg_file_exist);
                                            UIUtility.doYesNoDialog(getActivity(), strMsg, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            destSSH.delete();
                                                            procImportSSH(sshFile, destSSH, pass);
                                                            break;
                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            break;
                                                    }
                                                }
                                            });
                                        } else {
                                            procImportSSH(sshFile, destSSH, pass);
                                        }

                                        TESettingsInfo.setSSHKeyPath(pathTemp);
                                    }
                                });
                            }
                        });

                FileOpenDialog.chooseFile_or_Dir(TESettingsInfo.getSSHKeyPath());
            }
        });
        return mgrKeyFilesView;
    }
}
