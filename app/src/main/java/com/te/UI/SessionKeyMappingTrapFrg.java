package com.te.UI;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cipherlab.terminalemulation.R;

import Terminals.KeyMapList;

public class SessionKeyMappingTrapFrg extends Fragment {
    public static final String KEY_COMBI_RESULT = "KEY_COMBI_RESULT";
    private View mContentView = null;

    private boolean isCtrlPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_CTRL_MASK) != 0;
    }

    private boolean isShiftPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_SHIFT_MASK) != 0;
    }

    private boolean isAltPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_ALT_MASK) != 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        mContentView = inflater.inflate(R.layout.key_mapping_trap, container, false);
        return mContentView;
    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            getActivity().onBackPressed();
            return;
        }
        if(SessionKeyMapEditingFrg.gKeyCodeCategryMap.containsKey(keyCode) == false) {
            return;
        }
        int nEncodedKeycode = KeyMapList.encodePhyKeyCode(keyCode, isCtrlPressed(event), isShiftPressed(event), isAltPressed(event));
        Intent intentResult = new Intent();
        Bundle b = new Bundle();
        b.putInt(KEY_COMBI_RESULT, nEncodedKeycode);
        intentResult.putExtras(b);
        getActivity().setResult(Activity.RESULT_OK, intentResult);
        getActivity().onBackPressed();
    }
}
