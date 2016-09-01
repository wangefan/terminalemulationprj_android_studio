package com.te.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.cipherlab.terminalemulation.R;

import java.util.HashMap;

import Terminals.KeyMapItem;
import Terminals.TESettings;

public class SessionKeyMapEditingFrg extends Fragment {
    final int PHY_CATE_UNDEFINED = -1;
    final int PHY_CATE_ALPHA = 0;
    final int PHY_CATE_NUM = 1;
    final int PHY_CATE_PUN = 2;
    final int PHY_CATE_FUNC = 3;
    final int PHY_CATE_NAVG = 4;
    final int PHY_CATE_EDIT = 5;

    //Data members
    protected TESettings.SessionSetting mSetting = null;
    final static private HashMap<Integer, Integer> mKeyCodeCategryMap = new HashMap<>();//Key: Key code   Val: Category
    private int mEditServerKeyCode = 0;
    private TextView mtvServerKeyText = null;
    private Spinner mPhyCategory = null;
    private Spinner mPhyKeys = null;

	public SessionKeyMapEditingFrg() {
        mKeyCodeCategryMap.clear();
        mKeyCodeCategryMap.put(KeyMapItem.UNDEFINE_PHY, PHY_CATE_UNDEFINED);
        //Alphabets
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_A, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_B, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_C, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_D, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_E, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_G, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_H, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_I, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_J, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_K, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_L, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_M, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_N, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_O, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_P, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_Q, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_R, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_S, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_T, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_U, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_V, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_W, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_X, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_Y, PHY_CATE_ALPHA);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_Z, PHY_CATE_ALPHA);
        //Numbers
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_0, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_1, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_2, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_3, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_4, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_5, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_6, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_7, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_8, PHY_CATE_NUM);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_9, PHY_CATE_NUM);
        //Punctuations
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, PHY_CATE_PUN);	// ( + )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, PHY_CATE_PUN);	// ( -  )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, PHY_CATE_PUN);	// ( * )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_SLASH, PHY_CATE_PUN);	// ( / )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_BACKSLASH, PHY_CATE_PUN);	// ( \ )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_NUMPAD_EQUALS, PHY_CATE_PUN);	// ( = )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_SEMICOLON, PHY_CATE_PUN);	// ( ; )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_COMMA, PHY_CATE_PUN);	// ( , )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, PHY_CATE_PUN);	// ( } )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, PHY_CATE_PUN);	// ( { )
        //mKeyCodeCategryMap.put(KeyEvent.KEYCODE_HYPHEN, PHY_CATE_PUN);	// ( _)
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_PERIOD, PHY_CATE_PUN);	// ( . )
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_APOSTROPHE, PHY_CATE_PUN);	// ( ' )
        //mKeyCodeCategryMap.put(KeyEvent.KEYCODE_SEPARATOR, PHY_CATE_PUN);
        //mKeyCodeCategryMap.put(KeyEvent.KEYCODE_DECIDECIMAL, PHY_CATE_PUN);
        //mKeyCodeCategryMap.put(KeyEvent.KEYCODE_DIVIDE, PHY_CATE_PUN);
        //mKeyCodeCategryMap.put(KeyEvent.KEYCODE_BACKQUOTE, PHY_CATE_PUN);
        //Function Keys
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F1, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F2, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F3, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F4, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F5, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F6, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F7, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F8, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F9, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F10, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F11, PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F12, PHY_CATE_FUNC);
        /*mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F13,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F14,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F15,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F16,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F17,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F18,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F19,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F20,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F21,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F22,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F23,PHY_CATE_FUNC);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_F24,PHY_CATE_FUNC);*/
        //Navigation keys
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_LEFT, PHY_CATE_NAVG);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_UP, PHY_CATE_NAVG);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, PHY_CATE_NAVG);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_DOWN, PHY_CATE_NAVG);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_HOME, PHY_CATE_NAVG);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_MOVE_END, PHY_CATE_NAVG);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_NAVIGATE_PREVIOUS, PHY_CATE_NAVG);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_NAVIGATE_NEXT, PHY_CATE_NAVG);
        //Editing Keys
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_TAB, PHY_CATE_EDIT);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_BACK, PHY_CATE_EDIT);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_SPACE, PHY_CATE_EDIT);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_INSERT, PHY_CATE_EDIT);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_DEL, PHY_CATE_EDIT);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_ENTER, PHY_CATE_EDIT);
        mKeyCodeCategryMap.put(KeyEvent.KEYCODE_ESCAPE, PHY_CATE_EDIT);
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    public void setServerKeyCode(int nEditServerKeyCode) {
        mEditServerKeyCode = nEditServerKeyCode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View keyMappingEdtView = inflater.inflate(R.layout.pref_key_mapping_editing, container, false);
        mtvServerKeyText = (TextView) keyMappingEdtView.findViewById(R.id.server_keycode_text);
        mtvServerKeyText.setText(mSetting.getKeyMapList().getServerKeyTextByKeycode(mEditServerKeyCode));
        mPhyCategory = (Spinner) keyMappingEdtView.findViewById(R.id.spinner_phy_category);
        String [] phyCategory = getResources().getStringArray(R.array.phykey_cate_array);
        ArrayAdapter<String> phyCategoryAdpr  = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, phyCategory);
        mPhyCategory.setAdapter(phyCategoryAdpr);

        mPhyKeys = (Spinner) keyMappingEdtView.findViewById(R.id.spinner_phy);
        return keyMappingEdtView;
    }
}
