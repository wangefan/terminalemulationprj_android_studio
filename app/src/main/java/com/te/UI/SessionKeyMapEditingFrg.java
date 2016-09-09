package com.te.UI;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cipherlab.terminalemulation.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import Terminals.KeyMapItem;
import Terminals.KeyMapList;
import Terminals.TESettings;

public class SessionKeyMapEditingFrg extends Fragment {
    final int PHY_CATE_ALPHA = 0;
    final int PHY_CATE_NUM = 1;
    final int PHY_CATE_PUN = 2;
    final int PHY_CATE_FUNC = 3;
    final int PHY_CATE_NAVG = 4;
    final int PHY_CATE_EDIT = 5;

    //Data members
    protected TESettings.SessionSetting mSetting = null;
    final static public LinkedHashMap<Integer, Integer> gKeyCodeCategryMap = new LinkedHashMap<>();//Key: Key code   Val: Category
    private int mEditServerKeyCode = 0;
    private int mEditEncodedPhyKeyCode = KeyMapItem.UNDEFINE_PHY;
    private TextView mtvServerKeyText = null;
    private Spinner mPhyCategory = null;
    private Spinner mPhyKeys = null;
    private RelativeLayout mlayShift = null;
    private CheckBox mchkShift = null;
    private RelativeLayout mlayCtrl = null;
    private CheckBox mchkCtrl = null;
    private RelativeLayout mlayAlt = null;
    private CheckBox mchkAlt = null;

    public SessionKeyMapEditingFrg() {
        gKeyCodeCategryMap.clear();
        //Alphabets
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_A, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_B, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_C, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_D, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_E, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_G, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_H, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_I, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_J, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_K, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_L, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_M, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_N, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_O, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_P, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_Q, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_R, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_S, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_T, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_U, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_V, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_W, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_X, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_Y, PHY_CATE_ALPHA);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_Z, PHY_CATE_ALPHA);
        //Numbers
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_0, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_1, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_2, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_3, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_4, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_5, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_6, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_7, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_8, PHY_CATE_NUM);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_9, PHY_CATE_NUM);
        //Punctuations
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, PHY_CATE_PUN);	// ( + )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_MINUS, PHY_CATE_PUN);	// ( -  )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, PHY_CATE_PUN);	// ( * )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_SLASH, PHY_CATE_PUN);	// ( / )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_BACKSLASH, PHY_CATE_PUN);	// ( \ )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_NUMPAD_EQUALS, PHY_CATE_PUN);	// ( = )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_SEMICOLON, PHY_CATE_PUN);	// ( ; )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_COMMA, PHY_CATE_PUN);	// ( , )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, PHY_CATE_PUN);	// ( } )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, PHY_CATE_PUN);	// ( { )
        //gKeyCodeCategryMap.put(KeyEvent.KEYCODE_HYPHEN, PHY_CATE_PUN);	// ( _)
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_PERIOD, PHY_CATE_PUN);	// ( . )
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_GRAVE, PHY_CATE_PUN);	// ( ' )
        //gKeyCodeCategryMap.put(KeyEvent.KEYCODE_SEPARATOR, PHY_CATE_PUN);
        //gKeyCodeCategryMap.put(KeyEvent.KEYCODE_DECIDECIMAL, PHY_CATE_PUN);
        //gKeyCodeCategryMap.put(KeyEvent.KEYCODE_DIVIDE, PHY_CATE_PUN);
        //gKeyCodeCategryMap.put(KeyEvent.KEYCODE_BACKQUOTE, PHY_CATE_PUN);
        //Function Keys
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F1, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F2, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F3, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F4, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F5, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F6, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F7, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F8, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F9, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F10, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F11, PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F12, PHY_CATE_FUNC);
        /*gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F13,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F14,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F15,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F16,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F17,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F18,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F19,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F20,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F21,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F22,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F23,PHY_CATE_FUNC);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_F24,PHY_CATE_FUNC);*/
        //Navigation keys
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_LEFT, PHY_CATE_NAVG);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_UP, PHY_CATE_NAVG);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, PHY_CATE_NAVG);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_DPAD_DOWN, PHY_CATE_NAVG);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_HOME, PHY_CATE_NAVG);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_MOVE_END, PHY_CATE_NAVG);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_NAVIGATE_PREVIOUS, PHY_CATE_NAVG);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_PAGE_DOWN, PHY_CATE_NAVG);
        //Editing Keys
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_TAB, PHY_CATE_EDIT);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_BACK, PHY_CATE_EDIT);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_SPACE, PHY_CATE_EDIT);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_INSERT, PHY_CATE_EDIT);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_DEL, PHY_CATE_EDIT);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_ENTER, PHY_CATE_EDIT);
        gKeyCodeCategryMap.put(KeyEvent.KEYCODE_ESCAPE, PHY_CATE_EDIT);
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    public void setServerKeyCode(int nEditServerKeyCode) {
        mEditServerKeyCode = nEditServerKeyCode;
        //To find mapped physical key
        mEditEncodedPhyKeyCode = KeyMapItem.UNDEFINE_PHY;
        for(KeyMapItem keyItem :  mSetting.getKeyMapList()) {
            if(mEditServerKeyCode == keyItem.mServerKeycode) {
                mEditEncodedPhyKeyCode = keyItem.mPhysicalKeycode;
                break;
            }
        }
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
        phyCategoryAdpr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPhyCategory.setAdapter(phyCategoryAdpr);
        mPhyCategory.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer pos = (Integer) mPhyCategory.getTag();
                if(pos == null || pos == position)
                    return;

                mPhyKeys.setSelection(0);
                mPhyKeys.setTag(0);
                commitToTESettings(KeyMapList.encodePhyKeyCode(KeyMapItem.UNDEFINE_PHY, false, false, false));
                updateUIByPhyCode(mEditEncodedPhyKeyCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mPhyKeys = (Spinner) keyMappingEdtView.findViewById(R.id.spinner_phy);
        mPhyKeys.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer pos = (Integer) mPhyKeys.getTag();
                if(pos == null || pos == position)
                    return;
                PhyKeyItem phyKeyItem = (PhyKeyItem) mPhyKeys.getItemAtPosition(position);
                final int nDecodedPhyCode = phyKeyItem.mPhyKeycode;
                if(nDecodedPhyCode == KeyMapItem.UNDEFINE_PHY) {
                    commitToTESettings(KeyMapList.encodePhyKeyCode(KeyMapItem.UNDEFINE_PHY, false, false, false));
                    updateUIByPhyCode(mEditEncodedPhyKeyCode);
                } else {
                    checkAndProcEncodedPhy(KeyMapList.encodePhyKeyCode(nDecodedPhyCode, mchkCtrl.isChecked(), mchkShift.isChecked(), mchkAlt.isChecked()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mchkShift = (CheckBox) keyMappingEdtView.findViewById(R.id.id_shift);
        mchkShift.setClickable(false);
        mlayShift = (RelativeLayout) keyMappingEdtView.findViewById(R.id.id_lay_shift);
        mlayShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mchkShift.setChecked(!mchkShift.isChecked());
                int nDecodeKeycode = KeyMapList.decodePhyCodeRetunHelpKey(mEditEncodedPhyKeyCode, null, null, null);
                checkAndProcEncodedPhy(KeyMapList.encodePhyKeyCode(nDecodeKeycode, mchkCtrl.isChecked(), mchkShift.isChecked(), mchkAlt.isChecked()));
            }
        });
        mchkCtrl = (CheckBox) keyMappingEdtView.findViewById(R.id.id_ctrl);
        mchkCtrl.setClickable(false);
        mlayCtrl = (RelativeLayout) keyMappingEdtView.findViewById(R.id.id_lay_ctrl);
        mlayCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mchkCtrl.setChecked(!mchkCtrl.isChecked());
                int nDecodeKeycode = KeyMapList.decodePhyCodeRetunHelpKey(mEditEncodedPhyKeyCode, null, null, null);
                checkAndProcEncodedPhy(KeyMapList.encodePhyKeyCode(nDecodeKeycode, mchkCtrl.isChecked(), mchkShift.isChecked(), mchkAlt.isChecked()));
            }
        });
        mchkAlt = (CheckBox) keyMappingEdtView.findViewById(R.id.id_alt);
        mchkAlt.setClickable(false);
        mlayAlt = (RelativeLayout) keyMappingEdtView.findViewById(R.id.id_lay_alt);
        mlayAlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mchkAlt.setChecked(!mchkAlt.isChecked());
                int nDecodeKeycode = KeyMapList.decodePhyCodeRetunHelpKey(mEditEncodedPhyKeyCode, null, null, null);
                checkAndProcEncodedPhy(KeyMapList.encodePhyKeyCode(nDecodeKeycode, mchkCtrl.isChecked(), mchkShift.isChecked(), mchkAlt.isChecked()));
            }
        });
        RelativeLayout layClear = (RelativeLayout) keyMappingEdtView.findViewById(R.id.id_lay_clear_key);
        layClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhyCategory.setSelection(0);
                mPhyKeys.setSelection(0);
                mPhyKeys.setSelection(0);
                mPhyKeys.setTag(0);
                commitToTESettings(KeyMapList.encodePhyKeyCode(KeyMapItem.UNDEFINE_PHY, false, false, false));
                updateUIByPhyCode(mEditEncodedPhyKeyCode);
            }
        });
        RelativeLayout layTrap = (RelativeLayout) keyMappingEdtView.findViewById(R.id.id_lay_trap);
        layTrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Session4thSettings.class);
                intent.setAction(Session4thSettings.ACTION_KEY_TRAP);
                startActivityForResult(intent, 0);
            }
        });
        return keyMappingEdtView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                if(data != null) {
                    Bundle bundle = data.getExtras();
                    int nKeyCombiResult = bundle.getInt(SessionKeyMappingTrapFrg.KEY_COMBI_RESULT);
                    checkAndProcEncodedPhy(nKeyCombiResult);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkAndProcEncodedPhy(final int nEncodedPhyCode) {
        final AtomicInteger mappedServerKeyCode = new AtomicInteger(0);
        if(checkIfMapped(nEncodedPhyCode, mappedServerKeyCode) && mappedServerKeyCode.get() != mEditServerKeyCode) {
            String serverKeyText = mSetting.getKeyMapList().getServerKeyTextByKeycode(mappedServerKeyCode.get());
            String strMsg = String.format(getResources().getString(R.string.Msg_dup_key_mapped), serverKeyText);
            UIUtility.doYesNoDialog(getActivity(), strMsg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            replaceMappedPhyKeycode(mappedServerKeyCode.get(), KeyMapItem.UNDEFINE_PHY);
                            commitToTESettings(nEncodedPhyCode);
                            updateUIByPhyCode(mEditEncodedPhyKeyCode);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            updateUIByPhyCode(mEditEncodedPhyKeyCode);
                            break;
                    }
                }
            });
        } else {
            commitToTESettings(nEncodedPhyCode);
            updateUIByPhyCode(mEditEncodedPhyKeyCode);
        }
    }

    private void commitToTESettings(int nEncodedPhyCode) {
        mEditEncodedPhyKeyCode = nEncodedPhyCode;
        replaceMappedPhyKeycode(mEditServerKeyCode, mEditEncodedPhyKeyCode);
    }

    private void replaceMappedPhyKeycode(int nServerKeyCode, int nEncodedPhyKeyCode) {
        for(KeyMapItem keyItem :  mSetting.getKeyMapList()) {
            if(nServerKeyCode == keyItem.mServerKeycode) {
                keyItem.mPhysicalKeycode = nEncodedPhyKeyCode;
                break;
            }
        }
    }

    private boolean checkIfMapped(int nEncodePhyKeycode, AtomicInteger mappedServerKeyCode) {
        for(KeyMapItem keyItem :  mSetting.getKeyMapList()) {
            if(nEncodePhyKeycode == keyItem.mPhysicalKeycode) {
                if(mappedServerKeyCode != null) {
                    mappedServerKeyCode.set(keyItem.mServerKeycode);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUIByPhyCode(mEditEncodedPhyKeyCode);
    }

    private void updateUIByPhyCode(int nEncodedPhyKeyCode) {
        AtomicBoolean bCtrl = new AtomicBoolean(false);
        AtomicBoolean bShift = new AtomicBoolean(false);
        AtomicBoolean bAlt = new AtomicBoolean(false);
        int nDecodePhyCode = KeyMapList.decodePhyCodeRetunHelpKey(nEncodedPhyKeyCode, bCtrl, bShift, bAlt);
        if(nDecodePhyCode != KeyMapItem.UNDEFINE_PHY) {
            int nPhyCate = gKeyCodeCategryMap.get(nDecodePhyCode);
            mPhyCategory.setSelection(nPhyCate);
            mPhyCategory.setTag(nPhyCate);
            ArrayList<PhyKeyItem> phyKeyInCategory = new ArrayList<>();
            phyKeyInCategory.add(new PhyKeyItem(KeyMapItem.UNDEFINE_PHY));
            int nCurrentPhyKeyCodeIdx = getPhyKeycodesByCategory(nDecodePhyCode, nPhyCate, phyKeyInCategory);
            ArrayAdapter<PhyKeyItem> phyKeycodesAdptr = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, phyKeyInCategory);
            phyKeycodesAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mPhyKeys.setAdapter(phyKeycodesAdptr);
            mPhyKeys.setSelection(nCurrentPhyKeyCodeIdx);
            mPhyKeys.setTag(nCurrentPhyKeyCodeIdx);
            mchkShift.setChecked(bShift.get());
            mchkCtrl.setChecked(bCtrl.get());
            mchkAlt.setChecked(bAlt.get());
            CipherUtility.enableAllChild(mlayShift, true);
            CipherUtility.enableAllChild(mlayCtrl, true);
            CipherUtility.enableAllChild(mlayAlt, true);
        } else {
            int nPhyCate = mPhyCategory.getSelectedItemPosition();
            mPhyCategory.setSelection(nPhyCate);
            mPhyCategory.setTag(nPhyCate);
            ArrayList<PhyKeyItem> phyKeyInCategory = new ArrayList<>();
            phyKeyInCategory.add(new PhyKeyItem(KeyMapItem.UNDEFINE_PHY));
            getPhyKeycodesByCategory(nDecodePhyCode, nPhyCate, phyKeyInCategory);
            ArrayAdapter<PhyKeyItem> phyKeycodesAdptr = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, phyKeyInCategory);
            phyKeycodesAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mPhyKeys.setAdapter(phyKeycodesAdptr);
            mPhyKeys.setSelection(0);
            mPhyKeys.setTag(0);
            mchkShift.setChecked(false);
            mchkCtrl.setChecked(false);
            mchkAlt.setChecked(false);
            CipherUtility.enableAllChild(mlayShift, false);
            CipherUtility.enableAllChild(mlayCtrl, false);
            CipherUtility.enableAllChild(mlayAlt, false);
        }
    }

    private int getPhyKeycodesByCategory(int nCurDecodePhyCode, int nPhyCate, ArrayList<PhyKeyItem> phyKeyInCategory) {
        Iterator entries = gKeyCodeCategryMap.entrySet().iterator();
        int nCurrentPhyKeyCodeIdx = 0;
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Integer phyKeycode = (Integer) entry.getKey();
            Integer phyCate = (Integer) entry.getValue();
            if(phyCate == nPhyCate) {
                phyKeyInCategory.add(new PhyKeyItem(phyKeycode));
                if(nCurDecodePhyCode == phyKeycode) {
                    nCurrentPhyKeyCodeIdx = phyKeyInCategory.size() - 1;
                }
            }
        }
        return nCurrentPhyKeyCodeIdx;
    }

    class PhyKeyItem {
        int mPhyKeycode = KeyMapItem.UNDEFINE_PHY;
        String mPhyKeyText = getResources().getString(R.string.undefinedPhy);

        @Override
        public String toString() {
            return mPhyKeyText;
        }

        PhyKeyItem(int nPhyKeycode) {
            mPhyKeycode = nPhyKeycode;
            if(mPhyKeycode != KeyMapItem.UNDEFINE_PHY) {
                mPhyKeyText = KeyMapList.getPhyKeycodeTextByKeycode(mPhyKeycode);
            }
        }
    }
}
