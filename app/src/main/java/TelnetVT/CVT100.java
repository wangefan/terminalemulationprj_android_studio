package TelnetVT;

import android.graphics.Color;
import android.view.KeyEvent;

import com.cipherlab.terminalemulation.BuildConfig;
import com.cipherlab.terminalemulation.R;
import com.te.UI.CipherUtility;
import com.te.UI.ServerKeyEvent;

import java.util.Arrays;
import java.util.HashMap;

import Terminals.CipherReaderControl;
import Terminals.KeyMapItem;
import Terminals.KeyMapList;
import Terminals.TESettingsInfo;
import Terminals.stdActivityRef;

//import TelnetIBM.IBMHost5250.IBmAID;
//import TelnetIBM.IBMHost5250.IBmActions;

/**
 * Created by Franco.Liu on 2014/1/7.
 */
//region Description

//sample

//endregion

public class CVT100 extends CVT100Enum {
    public static final int VTKEY_NONE = -1;
    public static final int VTKEY_PGUP = ServerKeyEvent.VT_KEYCODE_PGUP;
    public static final int VTKEY_PGDW = ServerKeyEvent.VT_KEYCODE_PGDW;
    public static final int VTKEY_HOME = ServerKeyEvent.VT_KEYCODE_HOME;
    public static final int VTKEY_END = ServerKeyEvent.VT_KEYCODE_END;
    public static final int VTKEY_INS = ServerKeyEvent.VT_KEYCODE_INS;
    public static final int VTKEY_BS = ServerKeyEvent.VT_KEYCODE_BS;
    public static final int VTKEY_TAB = ServerKeyEvent.VT_KEYCODE_TAB;
    public static final int VTKEY_LEFT = ServerKeyEvent.VT_KEYCODE_LEFT;
    public static final int VTKEY_ENTER = ServerKeyEvent.VT_KEYCODE_ENTER;
    public static final int VTKEY_DEL = ServerKeyEvent.VT_KEYCODE_DEL;
    public static final int VTKEY_UP = ServerKeyEvent.VT_KEYCODE_UP;
    public static final int VTKEY_DW = ServerKeyEvent.VT_KEYCODE_DW;
    public static final int VTKEY_RIGHT = ServerKeyEvent.VT_KEYCODE_RIGHT;
    public static final int VTKEY_ESC = ServerKeyEvent.VT_KEYCODE_ESC;
    public static final int VTKEY_LF = ServerKeyEvent.VT_KEYCODE_LF;
    public static final int VTKEY_FIND = ServerKeyEvent.VT_KEYCODE_FIND;
    public static final int VTKEY_SELECT = ServerKeyEvent.VT_KEYCODE_SELECT;
    public static final int VTKEY_REMOVE = ServerKeyEvent.VT_KEYCODE_REMOVE;
    public static final int VTKEY_PREV = ServerKeyEvent.VT_KEYCODE_PRESCREEN;
    public static final int VTKEY_NEXT = ServerKeyEvent.VT_KEYCODE_NEXTSCREEN;
    public static final int VTKEY_F1 = ServerKeyEvent.FUN_KEYCODE_F1;
    public static final int VTKEY_F2 = ServerKeyEvent.FUN_KEYCODE_F2;
    public static final int VTKEY_F3 = ServerKeyEvent.FUN_KEYCODE_F3;
    public static final int VTKEY_F4 = ServerKeyEvent.FUN_KEYCODE_F4;
    public static final int VTKEY_F5 = ServerKeyEvent.FUN_KEYCODE_F5;
    public static final int VTKEY_F6 = ServerKeyEvent.FUN_KEYCODE_F6;
    public static final int VTKEY_F7 = ServerKeyEvent.FUN_KEYCODE_F7;
    public static final int VTKEY_F8 = ServerKeyEvent.FUN_KEYCODE_F8;
    public static final int VTKEY_F9 = ServerKeyEvent.FUN_KEYCODE_F9;
    public static final int VTKEY_F10 = ServerKeyEvent.FUN_KEYCODE_F10;
    public static final int VTKEY_F11 = ServerKeyEvent.FUN_KEYCODE_F11;
    public static final int VTKEY_F12 = ServerKeyEvent.FUN_KEYCODE_F12;
    public static final int VTKEY_F13 = ServerKeyEvent.FUN_KEYCODE_F13;
    public static final int VTKEY_F14 = ServerKeyEvent.FUN_KEYCODE_F14;
    public static final int VTKEY_F15 = ServerKeyEvent.FUN_KEYCODE_F15;
    public static final int VTKEY_F16 = ServerKeyEvent.FUN_KEYCODE_F16;
    public static final int VTKEY_F17 = ServerKeyEvent.FUN_KEYCODE_F17;
    public static final int VTKEY_F18 = ServerKeyEvent.FUN_KEYCODE_F18;
    public static final int VTKEY_F19 = ServerKeyEvent.FUN_KEYCODE_F19;
    public static final int VTKEY_F20 = ServerKeyEvent.FUN_KEYCODE_F20;
    static java.util.Map<Integer, String> mVTKeyCodeText = new java.util.HashMap<>();

    public static java.util.Map<Integer, Integer> gDefaultVT220KeyCodeMap_Taurus = new java.util.HashMap<>();
    public static java.util.Map<Integer, Integer> gDefaultVT100_102KeyCodeMap_Taurus = new java.util.HashMap<>();
    public static java.util.Map<Integer, Integer> gDefaultVT220KeyCodeMap = new java.util.HashMap<>();
    public static java.util.Map<Integer, Integer> gDefaultVT100_102KeyCodeMap = new java.util.HashMap<>();
    VtParserEvent vtParserEvent = new VtParserEvent();
    private uc_Parser Parser = null;
    private int TopMargin;
    private int BottomMargin;
    private java.util.ArrayList<uc_CaretAttribs> SavedCarets = new java.util.ArrayList<uc_CaretAttribs>();
    private java.util.ArrayList<KeyEventVal> LineBufferList = new java.util.ArrayList<KeyEventVal>();
    private java.util.Map<Integer, Integer> mVTKeyCodeMap = null;
    private uc_TabStops TabStops = null;  //defined inbase
    private CharAttribStruct[][] AttribGrid = null;//defined inbase
    private CharAttribStruct CharAttribs = new CharAttribStruct();
    private uc_Caret Caret;
    private uc_Chars G0;
    //endregion
    //region  Tab
    private uc_Chars G1;
    private uc_Chars G2;
    private uc_Chars G3;
    private uc_Chars EmUc_Chars;
    private uc_Mode Modes;
    //endregion
    //region CVT100 Member
    private StringBuilder mCurrentGoodFBText = new StringBuilder();
    private StringBuilder mCurrentErrorFBText = new StringBuilder();


    public CVT100() {
        this.Parser = new uc_Parser();
        this.Parser.UcParserEvent = vtParserEvent;

        this.G0 = new uc_Chars(Sets.ASCII);
        this.G1 = new uc_Chars(Sets.ASCII);
        this.G2 = new uc_Chars(Sets.DECSG);
        this.G3 = new uc_Chars(Sets.DECSG);
        this.EmUc_Chars = new uc_Chars(Sets.ASCII);

        this.CharAttribs.GL = this.G0;
        this.CharAttribs.GR = this.G2;

        this.SetSize(25, 80);

        //Todo: break CVT100 into CVT100_102 and CVT220
        mVTKeyCodeMap = new HashMap<Integer, Integer>(gDefaultVT220KeyCodeMap_Taurus);
        this.Caret = new uc_Caret();
        this.Modes = new uc_Mode();
        this.TabStops = new uc_TabStops();
        this.LineBufferList.clear();
    }

    @Override
    public void setKeyMapList(KeyMapList keyMapList) {
        if(keyMapList != null) { //Load from settings
            mVTKeyCodeMap = new HashMap<>();
            for (int idxKeyMapList = 0; idxKeyMapList < keyMapList.size(); idxKeyMapList++) {
                KeyMapItem keyMapItem = keyMapList.get(idxKeyMapList);
                mVTKeyCodeMap.put(keyMapItem.mPhysicalKeycode, keyMapItem.mServerKeycode);
            }
        }
    }

    public static void initKeyCodeMap() {
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_ENTER, VTKEY_ENTER);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DEL, VTKEY_BS);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_MOVE_END, VTKEY_DEL);  //End or Blue + Backspace, need confirm.
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_PAGE_DOWN, VTKEY_TAB);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_UP, VTKEY_UP);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_DOWN, VTKEY_DW);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_LEFT, VTKEY_LEFT);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_RIGHT, VTKEY_RIGHT);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_ESCAPE, VTKEY_ESC);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_N, true, false, false), VTKEY_LF);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_COMMA, false, true, false), VTKEY_FIND);//Shift + [Comma or Blue + A]
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_APOSTROPHE, false, true, false), VTKEY_SELECT);//Shift + [KEYCODE_APOSTROPHE or Blue + C]
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_TAB, VTKEY_INS);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_PERIOD, false, true, false), VTKEY_REMOVE);//Shift + [PERIOD or Blue + B]
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_LEFT_BRACKET, false, true, false), VTKEY_PREV);//Shift + [LEFT_BRACKET or Blue + E]
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_RIGHT_BRACKET, false, true, false), VTKEY_NEXT);//Shift + [RIGHT_BRACKET or Blue + F]
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F1, VTKEY_F1);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F2, VTKEY_F2);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F3, VTKEY_F3);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F4, VTKEY_F4);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F5, VTKEY_F5);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F6, VTKEY_F6);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F7, VTKEY_F7);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F8, VTKEY_F8);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F9, VTKEY_F9);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F10, VTKEY_F10);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_1, false, true, false), VTKEY_F11);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_2, false, true, false), VTKEY_F12);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_3, false, true, false), VTKEY_F13);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_4, false, true, false), VTKEY_F14);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_5, false, true, false), VTKEY_F15);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_6, false, true, false), VTKEY_F16);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_7, false, true, false), VTKEY_F17);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_8, false, true, false), VTKEY_F18);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_9, false, true, false), VTKEY_F19);
        gDefaultVT220KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_0, false, true, false), VTKEY_F20);

        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_ENTER, VTKEY_ENTER);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_DEL, VTKEY_BS);
        //gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_MOVE_END, VTKEY_DEL);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_TAB, VTKEY_TAB);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_UP, VTKEY_UP);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_DOWN, VTKEY_DW);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_LEFT, VTKEY_LEFT);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, VTKEY_RIGHT);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_ESCAPE, VTKEY_ESC);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F1, VTKEY_F1);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F2, VTKEY_F2);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F3, VTKEY_F3);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F4, VTKEY_F4);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F5, VTKEY_F5);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F6, VTKEY_F6);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F7, VTKEY_F7);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F8, VTKEY_F8);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F9, VTKEY_F9);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F10, VTKEY_F10);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F11, VTKEY_F11);
        gDefaultVT220KeyCodeMap.put(KeyEvent.KEYCODE_F12, VTKEY_F12);

        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_ENTER, VTKEY_ENTER);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DEL, VTKEY_BS);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_MOVE_END, VTKEY_DEL);  //End or Blue + Backspace, need confirm.
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_PAGE_DOWN, VTKEY_TAB);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_UP, VTKEY_UP);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_DOWN, VTKEY_DW);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_LEFT, VTKEY_LEFT);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_DPAD_RIGHT, VTKEY_RIGHT);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_ESCAPE, VTKEY_ESC);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_N, true, false, false), VTKEY_LF);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_COMMA, false, true, false), VTKEY_FIND);//Shift + [Comma or Blue + A]
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_APOSTROPHE, false, true, false), VTKEY_SELECT);//Shift + [KEYCODE_APOSTROPHE or Blue + C]
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_TAB, VTKEY_INS);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_PERIOD, false, true, false), VTKEY_REMOVE);//Shift + [PERIOD or Blue + B]
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_LEFT_BRACKET, false, true, false), VTKEY_PREV);//Shift + [LEFT_BRACKET or Blue + E]
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyMapList.encodePhyKeyCode(KeyEvent.KEYCODE_RIGHT_BRACKET, false, true, false), VTKEY_NEXT);//Shift + [RIGHT_BRACKET or Blue + F]
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F1, VTKEY_F1);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F2, VTKEY_F2);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F3, VTKEY_F3);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F4, VTKEY_F4);
        gDefaultVT100_102KeyCodeMap_Taurus.put(KeyEvent.KEYCODE_F5, VTKEY_F5);

        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_ENTER, VTKEY_ENTER);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_DEL, VTKEY_BS);
        //gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_MOVE_END, VTKEY_DEL);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_TAB, VTKEY_TAB);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_UP, VTKEY_UP);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_DOWN, VTKEY_DW);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_LEFT, VTKEY_LEFT);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, VTKEY_RIGHT);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_ESCAPE, VTKEY_ESC);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_F1, VTKEY_F1);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_F2, VTKEY_F2);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_F3, VTKEY_F3);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_F4, VTKEY_F4);
        gDefaultVT100_102KeyCodeMap.put(KeyEvent.KEYCODE_F5, VTKEY_F5);

        mVTKeyCodeText.put(VTKEY_TAB, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_TAB));
        mVTKeyCodeText.put(VTKEY_ENTER, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_ENTER));
        mVTKeyCodeText.put(VTKEY_LEFT, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_LEFT));
        mVTKeyCodeText.put(VTKEY_RIGHT, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_RIGHT));
        mVTKeyCodeText.put(VTKEY_UP, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_UP));
        mVTKeyCodeText.put(VTKEY_DW, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_DW));
        mVTKeyCodeText.put(VTKEY_BS, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_BS));
        mVTKeyCodeText.put(VTKEY_PGUP, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_PGUP));
        mVTKeyCodeText.put(VTKEY_PGDW, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_PGDW));
        mVTKeyCodeText.put(VTKEY_HOME, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_HOME));
        mVTKeyCodeText.put(VTKEY_END, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_END));
        mVTKeyCodeText.put(VTKEY_INS, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_INS));
        mVTKeyCodeText.put(VTKEY_DEL, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_DEL));
        mVTKeyCodeText.put(VTKEY_ESC, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_ESC));
        mVTKeyCodeText.put(VTKEY_LF, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_LF));
        mVTKeyCodeText.put(VTKEY_FIND, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_FIND));
        mVTKeyCodeText.put(VTKEY_SELECT, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_SELECT));
        mVTKeyCodeText.put(VTKEY_REMOVE, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_REMOVE));
        mVTKeyCodeText.put(VTKEY_PREV, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_PREV));
        mVTKeyCodeText.put(VTKEY_NEXT, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_NEXT));
        mVTKeyCodeText.put(VTKEY_F1, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F1));
        mVTKeyCodeText.put(VTKEY_F2, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F2));
        mVTKeyCodeText.put(VTKEY_F3, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F3));
        mVTKeyCodeText.put(VTKEY_F4, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F4));
        mVTKeyCodeText.put(VTKEY_F5, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F5));
        mVTKeyCodeText.put(VTKEY_F6, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F6));
        mVTKeyCodeText.put(VTKEY_F7, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F7));
        mVTKeyCodeText.put(VTKEY_F8, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F8));
        mVTKeyCodeText.put(VTKEY_F9, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F9));
        mVTKeyCodeText.put(VTKEY_F10, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F10));
        mVTKeyCodeText.put(VTKEY_F11, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F11));
        mVTKeyCodeText.put(VTKEY_F12, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F12));
        mVTKeyCodeText.put(VTKEY_F13, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F13));
        mVTKeyCodeText.put(VTKEY_F14, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F14));
        mVTKeyCodeText.put(VTKEY_F15, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F15));
        mVTKeyCodeText.put(VTKEY_F16, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F16));
        mVTKeyCodeText.put(VTKEY_F17, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F17));
        mVTKeyCodeText.put(VTKEY_F18, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F18));
        mVTKeyCodeText.put(VTKEY_F19, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F19));
        mVTKeyCodeText.put(VTKEY_F20, stdActivityRef.getCurrActivity().getResources().getString(R.string.VTKEY_F20));
    }

    public static String getServerKeyText(int nKeyCode) {
        return mVTKeyCodeText.get(nKeyCode);
    }
    public static void clearKeyCodeMap() {
        gDefaultVT220KeyCodeMap_Taurus.clear();
        gDefaultVT100_102KeyCodeMap_Taurus.clear();
        gDefaultVT220KeyCodeMap.clear();
        gDefaultVT100_102KeyCodeMap.clear();
    }

    public String GetLogTitle() {
        return "VT";
    }

    public String GetTerminalTypeName() {
        return "vt100";
    }

    @Override
    public void processChar(char ch) {
        Parser.processChar(ch);
    }

    @Override
    protected boolean autoLogin() {
        String loginNameProm = TESettingsInfo.getHostLoginPromtByIndex(TESettingsInfo.getSessionIndex());
        String pwdProm = TESettingsInfo.getHostPassWordPromtByIndex(TESettingsInfo.getSessionIndex());
        boolean bHasNameProm = false, bHasPwdProm = false;
        //Parse content to see if loginNameProm and pwdProm exist or not
        for (int idxRow = 0; idxRow < CharGrid.length; idxRow++) {
            String strRow = String.valueOf(CharGrid[idxRow]);
            if (strRow.toLowerCase().contains(loginNameProm.toLowerCase()))
                bHasNameProm = true;
            if (strRow.toLowerCase().contains(pwdProm.toLowerCase()))
                bHasPwdProm = true;
        }
        if (bHasNameProm == false && bHasPwdProm == false)
            return false;

        int nTerm = TESettingsInfo.getHostTermLoginByIndex(TESettingsInfo.getSessionIndex());
        String sendString = "";
        if (bHasNameProm) {
            String loginName = TESettingsInfo.getHostUserNameByIndex(TESettingsInfo.getSessionIndex());
            if (nTerm == TESettingsInfo.TERM_TAB) {
                sendString = loginName + "\t";
            } else {
                sendString = loginName + "\r\n";
            }
        }
        if (bHasPwdProm) {
            String loginPwd = TESettingsInfo.getHostPassWordByIndex(TESettingsInfo.getSessionIndex());
            sendString = sendString + loginPwd + "\r\n";
        }

        if (sendString.length() > 0)
            DispatchMessage(this, sendString);
        return true;
    }

    @Override
    public Point getCursorGridPos() {
        return Caret.Pos;
    }

    @Override
    protected int getServerKeyCode(int keyCode) {
        Integer nVTKeyCode = mVTKeyCodeMap.get(keyCode);
        if (nVTKeyCode != null) {
            CipherUtility.Log_d("CVT100", "Keycode mapped, Keyevent = %d[%s], VT Keycode = %d[%s]", keyCode, KeyMapList.getPhysicalKeyTextByEncode(keyCode), nVTKeyCode, getServerKeyText(nVTKeyCode));
            return nVTKeyCode;
        }
        CipherUtility.Log_d("CVT100", "No Keycode mapped!, Keyevent = %d[%s]", keyCode, KeyMapList.getPhysicalKeyTextByEncode(keyCode));
        return VTKEY_NONE;
    }

    //endregion
    //region  TelnetInterpreter
    @Override
    public void ParseEnd() {

    }

    public void drawAll() {
        for (int idxRow = this.TopMargin; idxRow < this.BottomMargin; idxRow++) {
            for (int idxCol = 0; idxCol < this._cols; ++idxCol) {
                if (this.AttribGrid[idxRow][idxCol] == null) {
                    DrawChar(this.CharGrid[idxRow][idxCol], idxCol, idxRow, false, false, false);
                } else {
                    DrawChar(this.CharGrid[idxRow][idxCol], idxCol, idxRow, this.AttribGrid[idxRow][idxCol].IsBold, this.AttribGrid[idxRow][idxCol].IsUnderscored, false);
                }
            }
        }
    }

    private void SetSize(int Rows, int Columns) {
        this._rows = Rows;
        this._cols = Columns;

        this.TopMargin = 0;
        this.BottomMargin = Rows - 1;

        this.CharGrid = new char[Rows][];

        for (int i = 0; i < this.CharGrid.length; i++) {
            this.CharGrid[i] = new char[Columns];
        }

        this.AttribGrid = new CharAttribStruct[Rows][];

        for (int i = 0; i < this.AttribGrid.length; i++) {
            this.AttribGrid[i] = new CharAttribStruct[Columns];
            for (int y = 0; y < Columns; y++) {
                this.AttribGrid[i][y] = new CharAttribStruct();

            }

        }
    }

    private void checkCustomCommand(ParserEventArgs e) {
        String StrCmd = GetActionString(e);
        if(TESettingsInfo.getHostIsFeedbackByTextCmdByIndex(TESettingsInfo.getSessionIndex()) == true) {
            if(TESettingsInfo.getHostIsGoodFeedbackByCmdByIndex(TESettingsInfo.getSessionIndex()) == true) {
                String strGood = TESettingsInfo.getHostGoodFeedbackCmdByIndex(TESettingsInfo.getSessionIndex());
                if (strGood != null && strGood.isEmpty() == false) {
                    if (StrCmd.compareTo(strGood) == 0) {
                        String strGoodSoundFile = TESettingsInfo.getHostGoodFeedbackSoundByIndex(TESettingsInfo.getSessionIndex());
                        CipherUtility.playSound(strGoodSoundFile);
                        stdActivityRef.ApplicationVibration(TESettingsInfo.getHostGoodFBVBByIndex(TESettingsInfo.getSessionIndex()));
                    }
                }
            }

            if(TESettingsInfo.getHostIsErrorFeedbackByCmdByIndex(TESettingsInfo.getSessionIndex()) == true) {
                String strErr = TESettingsInfo.getHostErrorFeedbackCmdByIndex(TESettingsInfo.getSessionIndex());if (strErr != null && strErr.isEmpty() == false) {
                    if (StrCmd.compareTo(strErr) == 0) {
                        String strErrSoundFile = TESettingsInfo.getHostErrorFeedbackSoundByIndex(TESettingsInfo.getSessionIndex());
                        CipherUtility.playSound(strErrSoundFile);
                        stdActivityRef.ApplicationVibration(TESettingsInfo.getHostErrorFBVBByIndex(TESettingsInfo.getSessionIndex()));
                    }
                }
            }
        }

        if(TESettingsInfo.getHostIsReaderControlByIndex(TESettingsInfo.getSessionIndex()) == true) {
            String strReader = TESettingsInfo.getHostEnableReaderCmdByIndex(TESettingsInfo.getSessionIndex());
            if (strReader != null && strReader.isEmpty() == false) {
                if (StrCmd.compareTo(strReader) == 0) {
                    String strReaderSoundFile = TESettingsInfo.getHostReaderSoundByIndex(TESettingsInfo.getSessionIndex());
                    CipherUtility.playSound(strReaderSoundFile);
                    stdActivityRef.ApplicationVibration(TESettingsInfo.getHostReaderVBByIndex(TESettingsInfo.getSessionIndex()));
                    CipherReaderControl.SetActived(true);
                }
            }

            String strDisableReader = TESettingsInfo.getHostDisableReaderCmdByIndex(TESettingsInfo.getSessionIndex());
            if (strDisableReader != null && strDisableReader.isEmpty() == false) {
                if (StrCmd.compareTo(strDisableReader) == 0) {
                    String strReaderSoundFile = TESettingsInfo.getHostReaderSoundByIndex(TESettingsInfo.getSessionIndex());
                    CipherUtility.playSound(strReaderSoundFile);
                    stdActivityRef.ApplicationVibration(TESettingsInfo.getHostReaderVBByIndex(TESettingsInfo.getSessionIndex()));
                    CipherReaderControl.SetActived(false);
                }
            }
        }
    }

    private String GetActionString(ParserEventArgs e)//not finish
    {
        String Sequence = "";
        String strParm = "";


        for (int i = 0; i < e.CurParams.Elements.size(); i++) {
            strParm += e.CurParams.Elements.get(i);
        }

        if (strParm.length() > 0) {
            int IntParm = Integer.parseInt(strParm);
            strParm = String.valueOf(IntParm);
        }

        if (e.CurSequence.startsWith("\u001b[")) // CUD
        {
            if (e.CurSequence.length() == 3)
                Sequence = "\u001b[" + strParm + e.CurSequence.charAt(2);
        } else if (e.CurSequence.startsWith("\u001b")) {
            if (e.CurSequence.length() == 2)
                Sequence = "\u001b" + strParm + e.CurSequence.charAt(1);
        }

        return Sequence;

    }

    private void CommandRouter(Object Sender, ParserEventArgs e)//not finish
    {
        Character MultiChar;
        switch (e.Action) {
            case Print:

                MultiChar = TryGetMultiChar();

                if (MultiChar != null)
                    this.PrintChar(MultiChar, true);
                else
                    this.PrintChar(e.CurChar, false);

                break;

            case Execute:
                this.ExecuteChar(e.CurChar);
                break;

            case Dispatch:
                break;

            default:
                break;
        }

        int Param = 0;

        int Inc = 1; // increment

        if (BuildConfig.DEBUG_MODE) {
            String strSeq = getCurSeq(e.CurSequence, e.CurParams.Elements);
            CipherUtility.Log_d("CVT100", "[VT Host][sequence %s, hex:%s]", strSeq, getHex(strSeq));
        }

        if (e.CurSequence.equals("")) {
        } else if (e.CurSequence.equals("\u001b" + "7"))//DECSC Save Cursor position and attributes//DECSC Save Cursor position and attributes
        {
            this.SavedCarets.add(new uc_CaretAttribs(this.Caret.Pos, this.G0.Set.Get(), this.G1.Set.Get(), this.G2.Set.Get(), this.G3.Set.Get(), this.CharAttribs));
        } else if (e.CurSequence.equals("\u001b" + "8")) //DECRC Restore Cursor position and attributes
        {
            this.Caret.Pos = ((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).Pos;
            this.CharAttribs = ((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).Attribs;

            this.G0.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G0Set);
            this.G1.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G1Set);
            this.G2.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G2Set);
            this.G3.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G3Set);

            this.SavedCarets.remove(this.SavedCarets.size() - 1);

        } else if (e.CurSequence.equals("\u001b~")) //LS1R Locking Shift G1 -> GR
        {
            this.CharAttribs.GR = G1;
        } else if (e.CurSequence.equals("\u001bn")) //LS2 Locking Shift G2 -> GL
        {
            this.CharAttribs.GL = G2;
        } else if (e.CurSequence.equals("\u001b}")) //LS2R Locking Shift G2 -> GR
        {
            this.CharAttribs.GR = G2;
        } else if (e.CurSequence.equals("\u001bo")) //LS3 Locking Shift G3 -> GL
        {
            this.CharAttribs.GL = G3;
        } else if (e.CurSequence.equals("\u001b|")) //LS3R Locking Shift G3 -> GR
        {
            this.CharAttribs.GR = G3;
        } else if (e.CurSequence.equals("\u001b#8")) //DECALN
        {
            e.CurParams.Elements.add("1");
            e.CurParams.Elements.add(String.valueOf(this._rows));


            // put E's on the entire screen
            for (int y = 0; y < this._rows; y++) {
                this.CaretToAbs(y, 0);

                for (int x = 0; x < this._cols; x++) {
                    this.PrintChar('E', false);
                }
            }
        } else if (e.CurSequence.equals("\u001b=")) // Keypad to Application mode
        {
            this.Modes.Flags = this.Modes.Flags | Modes.KeypadAppln;
        } else if (e.CurSequence.equals("\u001b>")) // Keypad to Numeric mode
        {
            this.Modes.Flags = this.Modes.Flags ^ Modes.KeypadAppln;
        } else if (e.CurSequence.equals("\u001b[B")) // CUD
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);

            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y + Inc, this.Caret.Pos.X);
        } else if (e.CurSequence.equals("\u001b[A")) // CUU
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);
            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y - Inc, this.Caret.Pos.X);
        } else if (e.CurSequence.equals("\u001b[C")) // CUF
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);
            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y, this.Caret.Pos.X + Inc);
        } else if (e.CurSequence.equals("\u001b[D")) // CUB
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);
            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y, this.Caret.Pos.X - Inc);
        } else if (e.CurSequence.equals("\u001b[H") || e.CurSequence.equals("\u001b[f")) // CUP
        {

            int X = 0;
            int Y = 0;

            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Y = Integer.valueOf(str) - 1;

            }

            if (e.CurParams.Count() > 1) {
                String str = (String) e.CurParams.Elements.get(1);
                X = Integer.valueOf(str) - 1;

            }

            if (Y > 20)
                this.CaretToRel(Y, X);
            else
                this.CaretToRel(Y, X);
        } else if (e.CurSequence.equals("\u001b[J")) {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            this.ClearDown(Param);

        } else if (e.CurSequence.equals("\u001b[K")) {

            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
            }

            this.ClearRight(Param);
        } else if (e.CurSequence.equals("\u001b[L"))  // INSERT LINE
        {
            this.InsertLine(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[M")) // DELETE LINE
        {
            this.DeleteLine(e.CurParams);
        } else if (e.CurSequence.equals("\u001bN")) // SS2 Single Shift (G2 -> GL)
        {
            this.CharAttribs.GS = this.G2;
        } else if (e.CurSequence.equals("\u001bO")) // SS3 Single Shift (G3 -> GL)
        {
            this.CharAttribs.GS = this.G3;
            //System.Console.WriteLine ("SS3: GS = {0}", this.CharAttribs.GS);
        } else if (e.CurSequence.equals("\u001b[m")) {
            this.SetCharAttribs(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[?h")) {
            //this.SetqmhMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[?l")) {
            setQmlMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[c")) // DA Device Attributes
        {
            //                    this.DispatchMessage (this, "\x1b[?64;1;2;6;7;8;9c");
            this.DispatchMessage(this, "\u001b[?6c");
        } else if (e.CurSequence.equals("\u001b[g")) {
            this.ClearTabs(e.CurParams);
        } else if (e.CurSequence.equals("\u001B[h")) {
            //this.SethMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[l")) {
            // this.SetlMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[r"))// DECSTBM Set Top and Bottom Margins
        {
            //this.SetScrollRegion(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[t")) // DECSLPP Set Lines Per Page
        {

            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            if (Param > 0) {
                this.SetSize(Param, this._cols);
            }

        } else if (e.CurSequence.equals("\u001b" + "D")) // IND
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            this.Index(Param);
        } else if (e.CurSequence.equals("\u001b" + "E")) // NEL
        {
            this.LineFeed();
            this.CarriageReturn();
        } else if (e.CurSequence.equals("\u001bH")) // HTS
        {
            this.TabSet();
        } else if (e.CurSequence.equals("\u001bM"))// RI
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            this.ReverseIndex(Param);
        }

        checkCustomCommand(e);
        //endregion

        if (e.CurSequence.startsWith("\u001b(")) {
            this.SelectCharSet(this.G0.Set, e.CurSequence.substring(2));
        } else if (e.CurSequence.startsWith("\u001b-") || e.CurSequence.startsWith("\u001b)")) {
            this.SelectCharSet(this.G1.Set, e.CurSequence.substring(2));
        } else if (e.CurSequence.startsWith("\u001b.") || e.CurSequence.startsWith("\u001b*")) {
            this.SelectCharSet(this.G2.Set, e.CurSequence.substring(2));
        } else if (e.CurSequence.startsWith("\u001b/") || e.CurSequence.startsWith("\u001b+")) {
            this.SelectCharSet(this.G3.Set, e.CurSequence.substring(2));
        }

    }

    private String getHex(String str) {
        String strHex = "";
        byte[] bData = str.getBytes();
        for (int idxBy = 0; idxBy < bData.length; idxBy++) {
            strHex += String.format("%02x ", bData[idxBy] & 0xFF);
        }
        return strHex;
    }

    private String getCurSeq(String sequence, java.util.ArrayList<String> params) {
        StringBuilder sbResult = new StringBuilder();
        if (sequence.startsWith("\u001b")) {
            sbResult.append(sequence.substring(0, sequence.length() - 1));
            for (int idxParams = 0; idxParams < params.size(); ++idxParams) {
                String param = params.get(idxParams);
                if (idxParams > 0) {
                    sbResult.append(";");
                }
                String oriVal = String.valueOf(Integer.valueOf(param));
                sbResult.append(oriVal);
            }

            sbResult.append(sequence.charAt(sequence.length() - 1));
        }
        return sbResult.toString();
    }

    private void ClearCharAttribs() {
        this.CharAttribs.IsBold = false;
        this.CharAttribs.IsDim = false;
        this.CharAttribs.IsUnderscored = false;
        this.CharAttribs.IsBlinking = false;
        this.CharAttribs.IsInverse = false;
        this.CharAttribs.IsPrimaryFont = false;
        this.CharAttribs.IsAlternateFont = false;
        this.CharAttribs.UseAltBGColor = false;
        this.CharAttribs.UseAltColor = false;
        this.CharAttribs.AltColor = Color.WHITE;
        this.CharAttribs.AltBGColor = Color.BLACK;
    }

    private void SetCharAttribs(uc_Params CurParams) {
        if (CurParams.Count() < 1) {
            this.ClearCharAttribs();
            return;
        }

        for (int i = 0; i < CurParams.Count(); i++) {
            String str = (String) CurParams.Elements.get(i);
            int c = Integer.valueOf(str);
            switch (c) {
                case 0:
                    this.ClearCharAttribs();
                    break;

                case 1:
                    this.CharAttribs.IsBold = true;
                    break;

                case 4:
                    this.CharAttribs.IsUnderscored = true;
                    break;

                case 5:
                    this.CharAttribs.IsBlinking = true;
                    break;

                case 7:
                    this.CharAttribs.IsInverse = true;
                    break;

                case 22:
                    this.CharAttribs.IsBold = false;
                    break;

                case 24:
                    this.CharAttribs.IsUnderscored = false;
                    break;

                case 25:
                    this.CharAttribs.IsBlinking = false;
                    break;

                case 27:
                    this.CharAttribs.IsInverse = false;
                    break;

                case 30:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.BLACK;
                    break;

                case 31:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.RED;
                    break;

                case 32:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.GREEN;
                    break;

                case 33:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.YELLOW;
                    break;

                case 34:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.BLUE;
                    break;

                case 35:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.MAGENTA;
                    break;

                case 36:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.CYAN;
                    break;

                case 37:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.WHITE;
                    break;
                case 40:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.BLACK;
                    break;

                case 41:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.RED;
                    break;

                case 42:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.GREEN;
                    break;

                case 43:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.YELLOW;
                    break;

                case 44:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.BLUE;
                    break;

                case 45:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.MAGENTA;
                    break;

                case 46:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.CYAN;
                    break;

                case 47:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.WHITE;
                    break;

                default:
                    break;
            }
        }
    }

    private void ExecuteChar(char CurChar) {
        switch (CurChar) {
            case '\u0005':
                this.DispatchMessage(this, "vt220");
                break;

            case '\u0007':

                break;

            case '\u0008':
                this.CaretLeft();
                break;

            case '\u0009':
                this.Tab();
                break;

            case 10:// 0A
            case '\u000B':
            case '\u000C':
            case '\u0084':
                this.LineFeed();
                break;

            case 13:
                this.CarriageReturn();
                break;

            case '\u000E':
                this.CharAttribs.GL = this.G1;
                break;

            case '\u000F':
                this.CharAttribs.GL = this.G0;
                break;

            case '\u0011':
                this.DispatchMessage(this, "");
                break;

            case '\u0013':
                break;

            case '\u0085':
                this.LineFeed();
                this.CaretToAbs(this.Caret.Pos.Y, 0);
                break;

            case '\u0088':
                this.TabSet();
                break;

            case '\u008D':
                this.ReverseLineFeed();
                break;

            case '\u008E':
                this.CharAttribs.GS = this.G2;
                break;

            case '\u008F':
                this.CharAttribs.GS = this.G3;
                break;

            default:
                break;
        }
    }

    private void SelectCharSet(SetsVal CurTarget, String CurString) {
        if (CurString.equals("B"))
            CurTarget.Set(Sets.ASCII);
        else if (CurString.equals("%5"))
            CurTarget.Set(Sets.DECS);
        else if (CurString.equals("0"))
            CurTarget.Set(Sets.DECSG);
        else if (CurString.equals(">"))
            CurTarget.Set(Sets.DECTECH);
        else if (CurString.equals("<"))
            CurTarget.Set(Sets.DECSG);
        else if (CurString.equals("A")) {
            if ((this.Modes.Flags & Modes.National) == 0) {
                CurTarget.Set(Sets.ISOLatin1S);
            } else {
                CurTarget.Set(Sets.NRCUK);
            }
        } else if (CurString.equals("4") || CurString.equals("5"))
            CurTarget.Set(Sets.NRCFinnish);
        else if (CurString.equals("R"))
            CurTarget.Set(Sets.NRCFrench);
        else if (CurString.equals("9"))
            CurTarget.Set(Sets.NRCFrenchCanadian);
        else if (CurString.equals("K"))
            CurTarget.Set(Sets.NRCGerman);
        else if (CurString.equals("Y"))
            CurTarget.Set(Sets.NRCItalian);
        else if (CurString.equals("6"))
            CurTarget.Set(Sets.NRCNorDanish);
        else if (CurString.equals("'"))
            CurTarget.Set(Sets.NRCNorDanish);
        else if (CurString.equals("%6"))
            CurTarget.Set(Sets.NRCPortuguese);
        else if (CurString.equals("Z"))
            CurTarget.Set(Sets.NRCSpanish);
        else if (CurString.equals("7"))
            CurTarget.Set(Sets.NRCSwedish);
        else if (CurString.equals("="))
            CurTarget.Set(Sets.NRCSwiss);
    }

    //region  PrintToBmp
    private void PrintChar(char CurChar, boolean bMultiByte) { //not finish
        if (this.Caret.EOL == true) {
            //if ((this.Modes.Flags & Modes.AutoWrap) == Modes.AutoWrap)
            {
                this.LineFeed();
                this.CarriageReturn();

            }
        }

        int X = this.Caret.Pos.X;
        int Y = this.Caret.Pos.Y;

        this.AttribGrid[Y][X] = this.CharAttribs.clone();

        if (this.CharAttribs.GS != null) {
            CurChar = EmUc_Chars.Get(CurChar, this.AttribGrid[Y][X].GS.Set.Get(), this.AttribGrid[Y][X].GR.Set.Get());

            if (this.CharAttribs.GS.Set.Get() == Sets.DECSG) {
                this.AttribGrid[Y][X].IsDECSG = true;
            }

            this.CharAttribs.GS = null;
        } else {
            CurChar = EmUc_Chars.Get(CurChar, this.AttribGrid[Y][X].GL.Set.Get(), this.AttribGrid[Y][X].GR.Set.Get());

            if (this.CharAttribs.GL.Set.Get() == Sets.DECSG) {
                this.AttribGrid[Y][X].IsDECSG = true;
            }
        }

        this.CharGrid[Y][X] = CurChar;
        if(bMultiByte) {
            CharGrid[Y][X+1] = CurChar;
        }
        if (CurChar != 0x20 && CurChar != 0) {
            int po = 0;
        }

        DrawChar(CurChar, X, Y, this.CharAttribs.IsBold, this.CharAttribs.IsUnderscored, bMultiByte);
        this.CaretRight();
        if(bMultiByte) {
            this.CaretRight();
        }
        //Handle alarm by text
        if(TESettingsInfo.getHostIsFeedbackByTextCmdByIndex(TESettingsInfo.getSessionIndex()) == true) {
            if(TESettingsInfo.getHostIsGoodFeedbackByTextByIndex(TESettingsInfo.getSessionIndex()) == true) {
                String destText = TESettingsInfo.getHostGoodFeedbackTextByIndex(TESettingsInfo.getSessionIndex());
                if(destText.length() > 0) {
                    mCurrentGoodFBText.append(CurChar);
                    String curText = mCurrentGoodFBText.toString();
                    if(destText.contains(curText)) {
                        if(destText.length() == curText.length()) {
                            CipherUtility.playSound(TESettingsInfo.getHostGoodFeedbackSoundByIndex(TESettingsInfo.getSessionIndex()));
                            mCurrentGoodFBText = new StringBuilder();
                        }
                    } else {
                        mCurrentGoodFBText = new StringBuilder();
                        mCurrentGoodFBText.append(CurChar);
                    }
                }
            } else {
                mCurrentGoodFBText = new StringBuilder();
            }

            if(TESettingsInfo.getHostIsErrorFeedbackByTextByIndex(TESettingsInfo.getSessionIndex()) == true) {
                String destText = TESettingsInfo.getHostErrorFeedbackTextByIndex(TESettingsInfo.getSessionIndex());
                if(destText.length() > 0) {
                    mCurrentErrorFBText.append(CurChar);
                    String curText = mCurrentErrorFBText.toString();
                    if(destText.contains(curText)) {
                        if(destText.length() == curText.length()) {
                            CipherUtility.playSound(TESettingsInfo.getHostErrorFeedbackSoundByIndex(TESettingsInfo.getSessionIndex()));
                            mCurrentErrorFBText = new StringBuilder();
                        }
                    } else {
                        mCurrentErrorFBText = new StringBuilder();
                        mCurrentErrorFBText.append(CurChar);
                    }
                }
            } else {
                mCurrentErrorFBText = new StringBuilder();
            }
        }
    }

    private void Tab() {
        for (int i = 0; i < this.TabStops.Columns.length; i++) {
            if (i > this.Caret.Pos.X && this.TabStops.Columns[i] == true) {
                this.CaretToAbs(this.Caret.Pos.Y, i);
                return;
            }
        }

        this.CaretToAbs(this.Caret.Pos.Y, this._cols - 1);
        return;
    }

    private void TabSet() {
        this.TabStops.Columns[this.Caret.Pos.X] = true;
    }

    //endregion
    //region  Clear
    private void ClearRight(int Param) {
        int X = this.Caret.Pos.X;
        int Y = this.Caret.Pos.Y;

        switch (Param) {
            case 0: // from cursor to end of line inclusive

                //Arrays.fill(this.CharGrid[Y], X, this.CharGrid[Y].length - X,0);
                Arrays.fill(this.CharGrid[Y], X, this.AttribGrid[Y].length - X, (char) 0);
                for (int i = 0; i < this.AttribGrid[Y].length - X; i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][X + i] = Obj;
                }
                //Arrays.fill(this.AttribGrid[Y], X, this.AttribGrid[Y].length - X,Obj);
                break;

            case 1: // from beginning to cursor inclusive

                Arrays.fill(this.CharGrid[Y], 0, X + 1, (char) 0);

                for (int i = 0; i < (X + 1); i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][i] = Obj;
                }
                //Arrays.fill(this.AttribGrid[Y], 0, X + 1,0);

                break;

            case 2: // entire line

                //System.Array.Clear(this.CharGrid[Y], 0, this.CharGrid[Y].getLength());
                Arrays.fill(this.CharGrid[Y], (char) 0);
                for (int i = 0; i < (this.AttribGrid[Y].length); i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][i] = Obj;
                }
                Arrays.fill(this.AttribGrid[Y], 0, this.AttribGrid[Y].length, 0);
                break;

            default:
                break;
        }
    }

    private void ClearDown(int Param) {
        int X = this.Caret.Pos.X;
        int Y = this.Caret.Pos.Y;

        switch (Param) {
            case 0: // from cursor to bottom inclusive

                Arrays.fill(this.CharGrid[Y], X, this.CharGrid[Y].length, (char) 0);
                //Arrays.fill(this.AttribGrid[Y], X, this.AttribGrid[Y].length - X,0);
                for (int i = 0; i < this.AttribGrid[Y].length - X; i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][X + i] = Obj;
                }


                ViewDrawSpace(X, Y, this.CharGrid[Y].length - X);
                for (int i = Y + 1; i < this._rows; i++) {
                    //Arrays.asList(this.CharGrid[i]).clear();
                    Arrays.fill(this.CharGrid[i], (char) 0);
                    //Arrays.fill(this.AttribGrid[i], 0, this.AttribGrid[i].length,0);
                    ViewDrawSpace(0, i, this.CharGrid[i].length);
                }
                break;

            case 1: // from top to cursor inclusive

                //Arrays.asList(this.CharGrid[Y]).subList(0, X + 1).clear();
                Arrays.fill(this.CharGrid[Y], 0, X + 1, (char) 0);
                // Arrays.fill(this.AttribGrid[Y], 0, X + 1,0);
                for (int i = 0; i < (X + 1); i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][i] = Obj;
                }
                ViewDrawSpace(0, Y, X + 1);
                for (int i = 0; i < Y; i++) {
                    //Arrays.asList(this.CharGrid[i]).clear();
                    Arrays.fill(this.CharGrid[i], (char) 0);
                    //Arrays.fill(this.AttribGrid[i], 0, this.AttribGrid[i].length,0);
                    ViewDrawSpace(0, i, this.CharGrid[i].length);
                }
                break;

            case 2: // entire screen

                for (int i = 0; i < this._rows; i++) {
                    Arrays.fill(this.CharGrid[i], (char) 0);
                }

                ViewClear();
                break;

            default:
                break;
        }
    }

    private void ClearTabs(uc_Params CurParams) // TBC
    {
        int Param = 0;

        if (CurParams.Count() > 0) {
            String str = (String) CurParams.Elements.get(0);
            Param = Integer.valueOf(str);

        }

        switch (Param) {
            case 0: // Current Position
                this.TabStops.Columns[this.Caret.Pos.X] = false;
                break;

            case 3: // All Tabs
                for (int i = 0; i < this.TabStops.Columns.length; i++) {
                    this.TabStops.Columns[i] = false;
                }
                break;

            default:
                break;
        }
    }

    //endregion
    //region  Lines
    private void ReverseLineFeed() {

        // if we're at the top of the scroll region (top margin)
        if (this.Caret.Pos.Y == this.TopMargin) {
            // we need to add a new line at the top of the screen margin
            // so shift all the rows in the scroll region down in the array and
            // insert a new row at the top

            int i;


            for (i = this.BottomMargin; i > this.TopMargin; i--) {
                this.CharGrid[i] = this.CharGrid[i - 1];
                this.AttribGrid[i] = this.AttribGrid[i - 1];
            }

            this.CharGrid[this.TopMargin] = new char[this._cols];

            this.AttribGrid[this.TopMargin] = new CharAttribStruct[this._cols];
        }

        this.CaretUp();
    }

    private void InsertLine(uc_Params CurParams) {

        // if we're not in the scroll region then bail
        if (this.Caret.Pos.Y < this.TopMargin || this.Caret.Pos.Y > this.BottomMargin) {
            return;
        }

        int NbrOff = 1;

        if (CurParams.Count() > 0) {
            String str = (String) CurParams.Elements.get(0);
            NbrOff = Integer.valueOf(str);
        }

        while (NbrOff > 0) {

            // Shift all the rows from the current row to the bottom margin down one place
            for (int i = this.BottomMargin; i > this.Caret.Pos.Y; i--) {
                this.CharGrid[i] = this.CharGrid[i - 1];
                this.AttribGrid[i] = this.AttribGrid[i - 1];
            }


            this.CharGrid[this.Caret.Pos.Y] = new char[this._cols];
            this.AttribGrid[this.Caret.Pos.Y] = new CharAttribStruct[this._cols];

            NbrOff--;
        }

    } //need paint

    private void DeleteLine(uc_Params CurParams) {
        // if we're not in the scroll region then bail
        if (this.Caret.Pos.Y < this.TopMargin || this.Caret.Pos.Y > this.BottomMargin) {
            return;
        }


        int NbrOff = 1;

        if (CurParams.Count() > 0) {
            String str = (String) CurParams.Elements.get(0);
            NbrOff = Integer.valueOf(str);
        }

        while (NbrOff > 0) {

            // Shift all the rows from below the current row to the bottom margin up one place
            for (int i = this.Caret.Pos.Y; i < this.BottomMargin; i++) {
                this.CharGrid[i] = this.CharGrid[i + 1];
                this.AttribGrid[i] = this.AttribGrid[i + 1];
            }

            this.CharGrid[this.BottomMargin] = new char[this._cols];
            this.AttribGrid[this.BottomMargin] = new CharAttribStruct[this._cols];

            NbrOff--;
        }
    } //need paint

    private void LineFeed() {

        String s = "";
        for (int x = 0; x < this._cols; x++) {
            char CurChar = this.CharGrid[this.Caret.Pos.Y][x];

            if (CurChar == '\0') {
                continue;
            }
            s = s + String.valueOf(CurChar);
        }

        if (this.Caret.Pos.Y == this.BottomMargin || this.Caret.Pos.Y == this._rows - 1) {
            // we need to add a new line so shift all the rows up in the array and
            // insert a new row at the bottom

            int i;
            this.AttribGrid[this.BottomMargin - 1] = this.AttribGrid[0];

            for (i = this.TopMargin; i < this.BottomMargin; i++) {
                this.CharGrid[i] = this.CharGrid[i + 1];
                this.AttribGrid[i] = this.AttribGrid[i + 1];

                for (int xcol = 0; xcol < this._cols; ++xcol) {
                    if (this.AttribGrid[i][xcol] == null) {
                        DrawChar(this.CharGrid[i][xcol], xcol, i, false, false, false);
                    } else {
                        DrawChar(this.CharGrid[i][xcol], xcol, i, this.AttribGrid[i][xcol].IsBold, this.AttribGrid[i][xcol].IsUnderscored, false);
                    }
                }

            }

            this.CharGrid[i] = new char[this._cols];
            Arrays.fill(CharGrid[i], (char) 0);

            //this.AttribGrid[i] = new CharAttribStruct[this._cols];

            //for (int y = 0; y < this._cols; y++)
            // {
            //this.AttribGrid[i][ y] = new CharAttribStruct();

            // }


        }

        this.CaretDown();


    }

    private void Index(int Param) {

        if (Param == 0) {
            Param = 1;
        }

        for (int i = 0; i < Param; i++) {
            this.LineFeed();
        }
    }

    @Override
    public void handleBarcodeFire(String Code) {
        if (TESettingsInfo.getUpperCaseByIndex(TESettingsInfo.getSessionIndex()) == true)
            Code = Code.toUpperCase();
        DispatchMessage(this, Code);
        ViewPostInvalidate();
    }

    @Override
    public void OnScreenBufferPos(int x, int y) {

    }

    @Override
    public void OnConnected() {
        byte[] SendData = TESettingsInfo.getVTHostSendToHostByIndex(TESettingsInfo.getSessionIndex());
        if (SendData != null && SendData.length > 0) {
            DispatchMessageRaw(this, SendData, SendData.length);
        }
        if (mTerminalListener != null) {
            mTerminalListener.onConnected();
        }
    }

    @Override
    public void handleKeyDown(int keyCode, KeyEvent event) {
        Boolean IsLineBuffer = TESettingsInfo.getHostIsLineBufferByIndex(TESettingsInfo.getSessionIndex());

        if (IsLineBuffer) {
            KeyEventVal Key = new KeyEventVal();
            Key.Set(event, keyCode);
            LineBufferList.add(Key);

            if (Key.GetEventKeycode() == KeyEvent.KEYCODE_TAB || Key.GetEventKeycode() == KeyEvent.KEYCODE_ENTER) {
                for (int i = 0; i < LineBufferList.size(); i++) {
                    KeyEventVal KeyVal = (KeyEventVal) LineBufferList.get(i);
                    KeyInput(KeyVal.GetEventKeycode(), KeyVal.GetEvent());
                }

                LineBufferList.clear();
                return;
            } else {
                Boolean IsEcho = TESettingsInfo.getHostIsLocalEchoByIndex(TESettingsInfo.getSessionIndex());
                if (IsEcho)
                    LineBufferInput(keyCode, event);
            }
        } else
            KeyInput(keyCode, event);
    }

    public void LineBufferInput(int keyCode, KeyEvent event) {
        int nVTKeyCode = VTKEY_NONE;
        if (event instanceof ServerKeyEvent) {
            nVTKeyCode = keyCode;
        } else {
            nVTKeyCode = getServerKeyCode(keyCode);
        }

        if (nVTKeyCode == VTKEY_NONE) {
            char pressedKey = (char) event.getUnicodeChar();
            if (pressedKey == 0)
                return;
            this.PrintChar(pressedKey, false);
        }
        ViewPostInvalidate();
    }

    public void KeyInput(int keyCode, KeyEvent event) {
        int nVTKeyCode = VTKEY_NONE;
        if (event instanceof ServerKeyEvent) {
            nVTKeyCode = keyCode;
            CipherUtility.Log_d("CVT100", "VT Keycode = %d[%s]", nVTKeyCode, getServerKeyText(nVTKeyCode));
        } else {
            int nEncodePhyKeycode = KeyMapList.getEncodePhyKeyCode(event);
            nVTKeyCode = getServerKeyCode(nEncodePhyKeycode);
        }

        if (nVTKeyCode != VTKEY_NONE) {
            byte SendData[] = new byte[30];
            int SendLenth = 0;
            switch (nVTKeyCode) {
                case VTKEY_PGUP:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = 'V';
                    break;
                case VTKEY_PGDW:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = 'U';
                    break;
                case VTKEY_HOME:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = 'H';
                    break;
                case VTKEY_END:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = 'K';
                    break;
                case VTKEY_INS:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_BS:
                    SendData[SendLenth++] = 0x08;
                    break;
                case VTKEY_TAB:
                    SendData[SendLenth++] = 0x09;
                    break;
                case VTKEY_LEFT:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'D';
                    break;
                case VTKEY_RIGHT:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'C';
                    break;
                case VTKEY_UP:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'A';
                    break;
                case VTKEY_DW:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'B';
                    break;
                case VTKEY_ENTER:
                    SendData[SendLenth++] = 0x0d;
                    break;
                case VTKEY_DEL:
                    SendData[SendLenth++] = 0x7f;
                    break;
                case VTKEY_ESC:
                    SendData[SendLenth++] = 0x1b;
                    break;
                case VTKEY_LF:
                    SendData[SendLenth++] = 0x0a;
                    break;
                case VTKEY_FIND:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '1';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_SELECT:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '4';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_REMOVE:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '3';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_PREV:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '5';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_NEXT:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '6';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F1:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'P';
                    break;

                case VTKEY_F2:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'Q';
                    break;
                case VTKEY_F3:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'R';
                    break;
                case VTKEY_F4:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = 'O';
                    SendData[SendLenth++] = 'S';
                    break;
                case VTKEY_F5:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = 'M';
                    break;
                case VTKEY_F6:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '1';
                    SendData[SendLenth++] = '7';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F7:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '1';
                    SendData[SendLenth++] = '8';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F8:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '1';
                    SendData[SendLenth++] = '9';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F9:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '0';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F10:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '1';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F11:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '3';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F12:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '4';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F13:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '5';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F14:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '6';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F15:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '8';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F16:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '9';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F17:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '3';
                    SendData[SendLenth++] = '1';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F18:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '3';
                    SendData[SendLenth++] = '2';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F19:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '3';
                    SendData[SendLenth++] = '3';
                    SendData[SendLenth++] = '~';
                    break;
                case VTKEY_F20:
                    SendData[SendLenth++] = 27;
                    SendData[SendLenth++] = '[';
                    SendData[SendLenth++] = '3';
                    SendData[SendLenth++] = '4';
                    SendData[SendLenth++] = '~';
                    break;
            }
            DispatchMessageRaw(this, SendData, SendLenth);
        } else {
            char pressedKey = (char) event.getUnicodeChar();
            if (pressedKey == 0)
                return;
            PutAsciiKey(pressedKey);
        }
        ViewPostInvalidate();
    }

    private void PutAsciiKey(int KeyCode) {
        if (Character.isLetter((char) KeyCode) && TESettingsInfo.getUpperCaseByIndex(TESettingsInfo.getSessionIndex()) == true) {
            KeyCode = Character.toUpperCase((char) KeyCode);
        }

        byte[] OutData = {0};
        OutData[0] = (byte) KeyCode;
        DispatchMessageRaw(this, OutData, OutData.length);
    }

    private void ReverseIndex(int Param) {

        if (Param == 0) {
            Param = 1;
        }

        for (int i = 0; i < Param; i++) {
            this.ReverseLineFeed();
        }
    }

    //endregion
    //region Caret
    private void CaretOn() {
        if (this.Caret.IsOff == false) {
            return;
        }

        this.Caret.IsOff = false;

    }

    private void CaretOff() {
        if (this.Caret.IsOff == true) {
            return;
        }

        this.Caret.IsOff = true;
    }

    private void CaretDown() {
        this.Caret.EOL = false;

        if ((this.Caret.Pos.Y < this._rows - 1 && (this.Modes.Flags & Modes.OriginRelative) == 0) || (this.Caret.Pos.Y < this.BottomMargin && (this.Modes.Flags & Modes.OriginRelative) > 0)) {
            this.Caret.Pos.Y += 1;
        }
    }

    private void CaretUp() {
        this.Caret.EOL = false;

        if ((this.Caret.Pos.Y > 0 && (this.Modes.Flags & Modes.OriginRelative) == 0) || (this.Caret.Pos.Y > this.TopMargin && (this.Modes.Flags & Modes.OriginRelative) > 0)) {
            this.Caret.Pos.Y -= 1;
        }
    }

    private void CaretRight() {
        if (this.Caret.Pos.X < this._cols - 1) {
            this.Caret.Pos.X += 1;
            this.Caret.EOL = false;
        } else {
            this.Caret.EOL = true;
        }
    }

    private void CaretLeft() {
        this.Caret.EOL = false;

        if (this.Caret.Pos.X > 0) {
            this.Caret.Pos.X -= 1;
        }
    }

    private void CaretToRel(int Y, int X) {

        this.Caret.EOL = false;
            /* This code is used when we get a cursor position command from
                   the host. Even if we're not in relative mode we use this as this will
			       sort that out for us. The ToAbs code is used internally by this prog
			       but is smart enough to stay within the margins if the originrelative
			       flagis set. */

        if ((this.Modes.Flags & Modes.OriginRelative) == 0) {
            this.CaretToAbs(Y, X);
            return;
        }

			/* the origin mode is relative so add the top and left margin
			       to Y and X respectively */
        Y += this.TopMargin;

        if (X < 0) {
            X = 0;
        }

        if (X > this._cols - 1) {
            X = this._cols - 1;
        }

        if (Y < this.TopMargin) {
            Y = this.TopMargin;
        }

        if (Y > this.BottomMargin) {
            Y = this.BottomMargin;
        }

        this.Caret.Pos.Y = Y;
        this.Caret.Pos.X = X;
    }

    private void CaretToAbs(int Y, int X) {
        this.Caret.EOL = false;

        if (X < 0) {
            X = 0;
        }

        if (X > this._cols - 1) {
            X = this._cols - 1;
        }

        if (Y < 0 && (this.Modes.Flags & Modes.OriginRelative) == 0) {
            Y = 0;
        }

        if (Y < this.TopMargin && (this.Modes.Flags & Modes.OriginRelative) > 0) {
            Y = this.TopMargin;
        }

        if (Y > this._rows - 1 && (this.Modes.Flags & Modes.OriginRelative) == 0) {
            Y = this._rows - 1;
        }

        if (Y > this.BottomMargin && (this.Modes.Flags & Modes.OriginRelative) > 0) {
            Y = this.BottomMargin;
        }

        this.Caret.Pos.Y = Y;
        this.Caret.Pos.X = X;
    }

    private void CarriageReturn() {
        this.CaretToAbs(this.Caret.Pos.Y, 0);
    }

    public void setQmlMode(uc_Params CurParams) { // set mode for ESC?l command
        int OptInt = 0;

        for (int idxStr = 0; idxStr < CurParams.Elements.size(); ++idxStr) {
            String CurOption = CurParams.Elements.get(idxStr);
            OptInt = Integer.valueOf(CurOption);

            switch (OptInt) {
                case 1: // set cursor keys to normal cursor mode
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.CursorAppln;
                    break;
                case 2: // unlock the keyboard
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.Locked;
                    break;
                case 3: // set terminal to 80 column mode
                    this.SetSize(this._rows, 80);
                    break;
                case 5: // Dark Background Mode
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.LightBackground;
                    //this.RefreshEvent ();
                    break;

                case 6: // Origin Mode Absolute
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.OriginRelative;
                    this.CaretToAbs(0, 0);
                    break;

                case 7: // Autowrap Off
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.AutoWrap;
                    break;

                case 8: // AutoRepeat Off
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.Repeat;
                    break;

                case 42: // DECNRCM National Charset
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.National;
                    break;
                case 66: // Numeric Keypad Application Mode On
                    this.Modes.Flags = this.Modes.Flags & ~uc_Mode.KeypadAppln;
                    break;

                default:
                    break;
            }
        }
    }

    public class VtParserEvent implements VtParserImplement {

        public void onEventUcParser(Object Sender, ParserEventArgs e) {
            //CVT100 Host=(CVT100)Sender;
            CommandRouter(Sender, e);
        }
    }

    public final class KeyEventVal {
        KeyEvent event;
        int KeyCode;

        public KeyEventVal() {

        }

        public void Set(KeyEvent KeyEvet, int code) {
            KeyCode = code;
            event = KeyEvet;
        }

        public KeyEvent GetEvent() {
            return event;
        }

        public int GetEventKeycode() {
            return KeyCode;
        }
    }
    //endregion
}
