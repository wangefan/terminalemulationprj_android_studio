package com.te.UI;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
    private TextView mtvSelectKey = null;
    private ListView mlstKeyFilesView = null;
    private ArrayList<String> mlstKeyFiles = new ArrayList<>();

    public SessionSSHMgrKeyFilesFrg() {
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    private boolean checkSSHKey(String keyPah, String passphrase, StringBuilder sbMsg) {
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
            sbMsg.append(str);
        }
        return bResult;
    }

    private void procImportSSH(File srcSSH, File destSSH, String passphrase) {
        CipherUtility.copyFile(srcSSH, destSSH);
        mlstKeyFiles.add(0, destSSH.getAbsolutePath());
        ((KeyFilesListAdapter)(mlstKeyFilesView.getAdapter())).notifyDataSetChanged();
        //commit to setting
        ArrayList<TESettings.CSsh_Key> sshList = TESettingsInfo.getCommonSSHKeys();
        int nKeyFrom = getKeyType(destSSH);//0: open(pem) 1:putty(ppk)
        sshList.add(new TESettings.CSsh_Key(destSSH.getName(), destSSH.getAbsolutePath(), passphrase, nKeyFrom));
        TESettingsInfo.setCommonSSHKeys(sshList);
        Toast.makeText(getActivity(), R.string.MSG_Import_ssh_ok, Toast.LENGTH_LONG).show();
        TESettingsInfo.setSSHKeyPath(srcSSH.getAbsolutePath());
    }

    private void setSelectKeyText(String keyPath) {
        String selPath = getActivity().getResources().getString(R.string.screen_show_wf_bt_status_off); //default none
        if(keyPath.length() > 0) {
            ArrayList<TESettings.CSsh_Key> sshList = TESettingsInfo.getCommonSSHKeys();
            for (int idxSSHList = 0; idxSSHList < sshList.size(); idxSSHList++) {
                String sshPath = sshList.get(idxSSHList).mSSHPath;
                if (sshPath.compareTo(keyPath) == 0) {
                    selPath = keyPath;
                    break;
                }
            }
        }
        mtvSelectKey.setText(selPath);
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
        mtvSelectKey = (TextView) mgrKeyFilesView.findViewById(R.id.ssh_select_key_file);
        setSelectKeyText(mSetting.mSSHKeyPath);
        laySelectKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fill key files to chosen list
                int nCheckedIdx = 0; //default "none"
                final int nDefaultCount = 1; //for "none"
                final String noneSel = getActivity().getResources().getString(R.string.screen_show_wf_bt_status_off);
                ArrayList<TESettings.CSsh_Key> sshList = TESettingsInfo.getCommonSSHKeys();
                final String [] keyFiles = new String [nDefaultCount + sshList.size()];
                keyFiles[0] = noneSel;
                for (int idxSSHList = 0; idxSSHList < sshList.size(); idxSSHList++) {
                    TESettings.CSsh_Key itrSSHKey = sshList.get(idxSSHList);
                    keyFiles[idxSSHList + nDefaultCount] = itrSSHKey.mSSHFileName;
                    if (mSetting.mSSHKeyPath.compareTo(itrSSHKey.mSSHPath) == 0) {
                        nCheckedIdx = idxSSHList + nDefaultCount;
                    }
                }
                UIUtility.listMessageBox(R.string.ssh_select_key_file_title, keyFiles, nCheckedIdx, getActivity(), new UIUtility.OnListMessageBoxListener() {
                    @Override
                    public void onSelResult(String result, int selIndex) {
                        if(selIndex == 0) {//select "none"
                            mSetting.mSSHKeyPath = "";
                            setSelectKeyText("");
                            return;
                        }
                        TESettings.CSsh_Key selKey = TESettingsInfo.getCommonSSHKeys().get(selIndex - 1);   //"none" is the first item so selIndex need - 1
                        mSetting.mSSHKeyPath = selKey.mSSHPath;
                        setSelectKeyText(mSetting.mSSHKeyPath);
                    }
                });
            }
        });
        //Fill Key Files
        ArrayList<TESettings.CSsh_Key> sshList = TESettingsInfo.getCommonSSHKeys();
        for (TESettings.CSsh_Key ssh : sshList) {
            mlstKeyFiles.add(ssh.mSSHPath);
        }
        mlstKeyFiles.add("add");//dummy item for "add" icon
        mlstKeyFilesView = (ListView) mgrKeyFilesView.findViewById(R.id.id_key_files_list);
        KeyFilesListAdapter adapter = new KeyFilesListAdapter(getActivity(), mlstKeyFiles);
        adapter.setClickAddListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                            UIUtility.messageBox(getActivity(), R.string.msg_pwd_not_match, null);
                                            return;
                                        }
                                        final String pass = pass1;
                                        StringBuilder sbMsg = new StringBuilder();
                                        boolean bValidSSH = checkSSHKey(pathTemp, pass, sbMsg);
                                        if(bValidSSH == false) {
                                            UIUtility.messageBox(getActivity(), sbMsg.toString(), null);
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
        adapter.setClickDelListener(new KeyFilesListAdapter.OnDelKeyListener() {
            @Override
            public void onDelKey(int position) {
                String keyPath = mlstKeyFiles.get(position);
                File keyFile = new File(keyPath);
                if(keyFile.exists()) {
                    keyFile.delete();
                }
                mlstKeyFiles.remove(position);
                ((KeyFilesListAdapter)(mlstKeyFilesView.getAdapter())).notifyDataSetChanged();
                //commit to setting
                ArrayList<TESettings.CSsh_Key> sshList = TESettingsInfo.getCommonSSHKeys();
                sshList.remove(position);
                TESettingsInfo.setCommonSSHKeys(sshList);
                //Remove all used in each session
                for (int idxSession = 0; idxSession < TESettingsInfo.getSessionCount(); idxSession++) {
                    TESettings.SessionSetting setting = TESettingsInfo.getSessionSetting(idxSession);
                    if(setting.mSSHKeyPath.compareTo(keyPath) == 0) {
                        setting.mSSHKeyPath = "";
                    }
                }
                setSelectKeyText(mSetting.mSSHKeyPath);
            }
        });
        mlstKeyFilesView.setAdapter(adapter);
        return mgrKeyFilesView;
    }
}
