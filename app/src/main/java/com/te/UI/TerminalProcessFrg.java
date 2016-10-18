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
    private static final String TE_SETTINGS = "TE_SETTINGS";
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
        if(savedInstanceState != null) {
            CipherUtility.Log_d(TAG, "savedInstanceState != null");
            // Restore state members from saved instance
            boolean bRestore = false;
            bRestore = savedInstanceState.getBoolean(TE_SETTINGS);
            if(bRestore) {
                CipherUtility.Log_d(TAG, "Restore TE settings");
                TESettingsInfo.loadSessionSettings(getContext());
                syncSessionsFromSettings();
            }
        }
    }

    @Override
    public void onDestroy() {
        CipherUtility.Log_d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        CipherUtility.Log_d(TAG, "onSaveInstanceState");
        // Save the user's current game state
        TESettingsInfo.saveSessionSettings(getContext());
        savedInstanceState.putBoolean(TE_SETTINGS, true);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
