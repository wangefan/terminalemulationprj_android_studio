package com.te.UI;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cipherlab.terminalemulation.R;

import TelnetIBM.IBMHost5250;
import TelnetVT.CVT100;
import Terminals.KeyMapItem;
import Terminals.KeyMapList;
import Terminals.KeyMapListAdapter;
import Terminals.TESettings;
import Terminals.TN3270KeyMapList;
import Terminals.TN5250KeyMapList;
import Terminals.VT100_102KeyMapList;
import Terminals.VT220KeyMapList;
import Terminals.stdActivityRef;

public class SessionKeyMappingFrg extends Fragment {
    final int [] VT220SERVER_KEY_SEQUENCE = {
        CVT100.VTKEY_ENTER,
        CVT100.VTKEY_BS	,
        CVT100.VTKEY_DEL	,
        CVT100.VTKEY_TAB	,
        CVT100.VTKEY_LEFT	,
        CVT100.VTKEY_RIGHT	,
        CVT100.VTKEY_UP	,
        CVT100.VTKEY_DW	,
        CVT100.VTKEY_ESC	,
        CVT100.VTKEY_LF	,
        CVT100.VTKEY_FIND	,
        CVT100.VTKEY_SELECT	,
        CVT100.VTKEY_INS	,
        CVT100.VTKEY_REMOVE	,
        CVT100.VTKEY_PREV	,
        CVT100.VTKEY_NEXT,
// 	CVT100.VTKEY_PGUP	,   temporary remove
// 	CVT100.VTKEY_PGDW	,   temporary remove
        CVT100.VTKEY_HOME	,
        CVT100.VTKEY_END	,
        CVT100.VTKEY_F1	,
        CVT100.VTKEY_F2	,
        CVT100.VTKEY_F3	,
        CVT100.VTKEY_F4	,
        CVT100.VTKEY_F5	,
        CVT100.VTKEY_F6	,
        CVT100.VTKEY_F7	,
        CVT100.VTKEY_F8	,
        CVT100.VTKEY_F9	,
        CVT100.VTKEY_F10	,
        CVT100.VTKEY_F11	,
        CVT100.VTKEY_F12	,
        CVT100.VTKEY_F13	,
        CVT100.VTKEY_F14	,
        CVT100.VTKEY_F15	,
        CVT100.VTKEY_F16	,
        CVT100.VTKEY_F17	,
        CVT100.VTKEY_F18	,
        CVT100.VTKEY_F19	,
        CVT100.VTKEY_F20
    };

    final int [] VT100_102SERVER_KEY_SEQUENCE = {
            CVT100.VTKEY_ENTER,
            CVT100.VTKEY_BS	,
            CVT100.VTKEY_DEL	,
            CVT100.VTKEY_TAB	,
            CVT100.VTKEY_LEFT	,
            CVT100.VTKEY_RIGHT	,
            CVT100.VTKEY_UP	,
            CVT100.VTKEY_DW	,
            CVT100.VTKEY_ESC	,
            CVT100.VTKEY_LF	,
            CVT100.VTKEY_FIND	,
            CVT100.VTKEY_SELECT	,
            CVT100.VTKEY_INS	,
            CVT100.VTKEY_REMOVE	,
            CVT100.VTKEY_PREV	,
            CVT100.VTKEY_NEXT,
// 	CVT100.VTKEY_PGUP	,   temporary remove
// 	CVT100.VTKEY_PGDW	,   temporary remove
            CVT100.VTKEY_HOME	,
            CVT100.VTKEY_END	,
            CVT100.VTKEY_F1	,
            CVT100.VTKEY_F2	,
            CVT100.VTKEY_F3	,
            CVT100.VTKEY_F4	,
            CVT100.VTKEY_F5
    };

    final int [] TN5250SERVER_KEY_SEQUENCE = {
            IBMHost5250.IBMKEY_ATTN,
            IBMHost5250.IBMKEY_LEFTDELETE,
            IBMHost5250.IBMKEY_PREV        ,
            IBMHost5250.IBMKEY_CLR           ,
            IBMHost5250.IBMKEY_CLREOF        ,
            IBMHost5250.IBMKEY_DEL         ,
            IBMHost5250.IBMKEY_DUP             ,
            IBMHost5250.IBMKEY_ENTER           ,
            IBMHost5250.IBMKEY_ERINPUT      ,
            IBMHost5250.IBMKEY_FBEGIN      ,
            IBMHost5250.IBMKEY_FEND        ,
            IBMHost5250.IBMKEY_PRINT               ,
            IBMHost5250.IBMKEY_FEXIT       ,
            IBMHost5250.IBMKEY_FPLUS        ,
            IBMHost5250.IBMKEY_FMINUS      ,
            IBMHost5250.IBMKEY_FMARK        ,
            IBMHost5250.IBMKEY_LAST            ,
            IBMHost5250.IBMKEY_HELP            ,
            IBMHost5250.IBMKEY_HOME      ,
            IBMHost5250.IBMKEY_INS          ,
            IBMHost5250.IBMKEY_NEWLINE        ,
            IBMHost5250.IBMKEY_RESET           ,
            IBMHost5250.IBMKEY_ROLUP            ,
            IBMHost5250.IBMKEY_ROLDN            ,
            IBMHost5250.IBMKEY_SYSRQ          ,
            IBMHost5250.IBMKEY_NEXT            ,
            IBMHost5250.IBMKEY_LEFT            ,
            IBMHost5250.IBMKEY_RIGHT			,
            IBMHost5250.IBMKEY_UP            ,
            IBMHost5250.IBMKEY_DOWN            ,
            IBMHost5250.IBMKEY_RECORD          ,
            IBMHost5250.IBMKEY_F1,
            IBMHost5250.IBMKEY_F2             ,
            IBMHost5250.IBMKEY_F3             ,
            IBMHost5250.IBMKEY_F4             ,
            IBMHost5250.IBMKEY_F5             ,
            IBMHost5250.IBMKEY_F6             ,
            IBMHost5250.IBMKEY_F7             ,
            IBMHost5250.IBMKEY_F8             ,
            IBMHost5250.IBMKEY_F9             ,
            IBMHost5250.IBMKEY_F10            ,
            IBMHost5250.IBMKEY_F11            ,
            IBMHost5250.IBMKEY_F12            ,
            IBMHost5250.IBMKEY_F13            ,
            IBMHost5250.IBMKEY_F14            ,
            IBMHost5250.IBMKEY_F15            ,
            IBMHost5250.IBMKEY_F16            ,
            IBMHost5250.IBMKEY_F17            ,
            IBMHost5250.IBMKEY_F18            ,
            IBMHost5250.IBMKEY_F19            ,
            IBMHost5250.IBMKEY_F20            ,
            IBMHost5250.IBMKEY_F21            ,
            IBMHost5250.IBMKEY_F22            ,
            IBMHost5250.IBMKEY_F23            ,
            IBMHost5250.IBMKEY_F24
    };

    final int [] TN3270SERVER_KEY_SEQUENCE = {
            IBMHost5250.IBMKEY_ATTN            ,
            IBMHost5250.IBMKEY_LEFTDELETE      ,
            IBMHost5250.IBMKEY_PREV        ,
            IBMHost5250.IBMKEY_CLR           ,
            IBMHost5250.IBMKEY_CLREOF        ,
            IBMHost5250.IBMKEY_DEL         ,
            IBMHost5250.IBMKEY_DUP             ,
            IBMHost5250.IBMKEY_ENTER           ,
            IBMHost5250.IBMKEY_ERINPUT      ,
            IBMHost5250.IBMKEY_FBEGIN      ,
            IBMHost5250.IBMKEY_FEND        ,
            IBMHost5250.IBMKEY_FMARK        ,
            IBMHost5250.IBMKEY_LAST            ,
            IBMHost5250.IBMKEY_HOME      ,
            IBMHost5250.IBMKEY_INS          ,
            IBMHost5250.IBMKEY_NEWLINE        ,
            IBMHost5250.IBMKEY_RESET           ,
            IBMHost5250.IBMKEY_ROLUP            ,
            IBMHost5250.IBMKEY_ROLDN            ,
            IBMHost5250.IBMKEY_SYSRQ          ,
            IBMHost5250.IBMKEY_NEXT            ,
            IBMHost5250.IBMKEY_LEFT            ,
            IBMHost5250.IBMKEY_RIGHT			,
            IBMHost5250.IBMKEY_UP            ,
            IBMHost5250.IBMKEY_DOWN            ,
            IBMHost5250.IBMKEY_PA1		,
            IBMHost5250.IBMKEY_PA2		,
            IBMHost5250.IBMKEY_PA3		,
            IBMHost5250.IBMKEY_F1             ,
            IBMHost5250.IBMKEY_F2             ,
            IBMHost5250.IBMKEY_F3             ,
            IBMHost5250.IBMKEY_F4             ,
            IBMHost5250.IBMKEY_F5             ,
            IBMHost5250.IBMKEY_F6             ,
            IBMHost5250.IBMKEY_F7             ,
            IBMHost5250.IBMKEY_F8             ,
            IBMHost5250.IBMKEY_F9             ,
            IBMHost5250.IBMKEY_F10            ,
            IBMHost5250.IBMKEY_F11            ,
            IBMHost5250.IBMKEY_F12            ,
            IBMHost5250.IBMKEY_F13            ,
            IBMHost5250.IBMKEY_F14            ,
            IBMHost5250.IBMKEY_F15            ,
            IBMHost5250.IBMKEY_F16            ,
            IBMHost5250.IBMKEY_F17            ,
            IBMHost5250.IBMKEY_F18            ,
            IBMHost5250.IBMKEY_F19            ,
            IBMHost5250.IBMKEY_F20            ,
            IBMHost5250.IBMKEY_F21            ,
            IBMHost5250.IBMKEY_F22            ,
            IBMHost5250.IBMKEY_F23            ,
            IBMHost5250.IBMKEY_F24
    };
    //Data members
    protected TESettings.SessionSetting mSetting = null;

    private ListView mKeyMapListView = null;

    public SessionKeyMappingFrg() {

    }

    @Override
    public void onStart() {
        updateKeyListItems();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View keyMappingView = inflater.inflate(R.layout.pref_key_mapping, container, false);
        mKeyMapListView = (ListView) keyMappingView.findViewById(R.id.key_list);
        mKeyMapListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(stdActivityRef.gIsActivate) {
                    KeyMapItem keyItem = (KeyMapItem) mKeyMapListView.getItemAtPosition(position);
                    Intent intent = new Intent(getActivity(), Session3rdSettings.class);
                    intent.setAction(Session3rdSettings.ACTION_KEYMAP_EDIT);
                    intent.putExtra(Session3rdSettings.ACTION_KEYMAP_EDIT_SERVER_KEYCODE, keyItem.mServerKeycode);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.MSG_KeyMappingItem_not_accept), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return keyMappingView;
    }

    public void updateKeyListItems() {
        KeyMapList keyListInSettings = mSetting.getKeyMapList();
        KeyMapList keyListReSeq = null;
        int [] keySequenceBase = null;
        if(keyListInSettings instanceof VT100_102KeyMapList) {
            keyListReSeq = new VT100_102KeyMapList();
            keySequenceBase = VT100_102SERVER_KEY_SEQUENCE;
        } else if(keyListInSettings instanceof VT220KeyMapList) {
            keyListReSeq = new VT220KeyMapList();
            keySequenceBase = VT220SERVER_KEY_SEQUENCE;
        } else if(keyListInSettings instanceof TN5250KeyMapList) {
            keyListReSeq = new TN5250KeyMapList();
            keySequenceBase = TN5250SERVER_KEY_SEQUENCE;
        } else if(keyListInSettings instanceof TN3270KeyMapList) {
            keyListReSeq = new VT220KeyMapList();
            keySequenceBase = TN3270SERVER_KEY_SEQUENCE;
        }

        //Re sequence list by SEQUENCE_LIST
        if(keySequenceBase != null && keyListReSeq != null) {
            for(int idxKey = 0; idxKey < keySequenceBase.length; ++idxKey) {
                KeyMapItem newKeyItem = new KeyMapItem(keySequenceBase[idxKey], KeyMapItem.UNDEFINE_PHY);
                for (int idxCurList = 0; idxCurList < keyListInSettings.size(); idxCurList++) {
                    KeyMapItem curKeyItem = keyListInSettings.get(idxCurList);
                    if(keySequenceBase[idxKey] == curKeyItem.mServerKeycode) {
                        newKeyItem.mPhysicalKeycode = curKeyItem.mPhysicalKeycode;
                        break;
                    }
                }
                keyListReSeq.add(newKeyItem);
            }
            final KeyMapListAdapter adapter = new KeyMapListAdapter(getActivity(), keyListReSeq);
            mKeyMapListView.setAdapter(adapter);
        }
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

}
