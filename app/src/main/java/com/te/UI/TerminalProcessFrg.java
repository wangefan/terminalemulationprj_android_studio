package com.te.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import SessionProcess.TerminalProcess;
import Terminals.TESettingsInfo;

public class TerminalProcessFrg extends Fragment {
    private static final String TAG = TerminalProcessFrg.class.getSimpleName();
    List<TerminalProcess> mCollSessions = new ArrayList<TerminalProcess>();

    public void syncSessionsFromSettings() {
        CipherUtility.Log_d("TerminalProcessFrg.syncSessionsFromSettings", "call mCollSessions.clear()");
        mCollSessions.clear();
        for (int idxSession = 0; idxSession < TESettingsInfo.getSessionCount(); ++idxSession) {
            TerminalProcess Process = new TerminalProcess();
            Process.setMacroList(TESettingsInfo.getHostMacroListByIndex(idxSession));
            mCollSessions.add(Process);
        }
    }

    public TerminalProcessFrg() {
        super();
        CipherUtility.Log_d("TerminalProcessFrg.TerminalProcessFrg", "constructor called");
    }

    @Override
    public void onAttach(Activity activity) {
        CipherUtility.Log_d(TAG, "onAttach(Activity)");
        super.onAttach(activity);
    }

    /**
     * This method is called once when the Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        CipherUtility.Log_d(TAG, "onCreate(Bundle savedInstanceState)");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        CipherUtility.Log_d(TAG, "onDestroy");
        super.onDestroy();
    }

    public TerminalProcess getTerminalProc(int sessionIndex) {
        return mCollSessions.get(sessionIndex);
    }

    public void addTerminalProcess(TerminalProcess process) {
        mCollSessions.add(process);
    }

    public void removeTerminalProc(int position) {
        mCollSessions.remove(position);
    }

    public void resetCurSessionKeyList() {
        TerminalProcess terminal = mCollSessions.get(TESettingsInfo.getSessionIndex());
        terminal.setKeyMapList(TESettingsInfo.getKeyMapListByIndex(TESettingsInfo.getSessionIndex()));
    }
}
