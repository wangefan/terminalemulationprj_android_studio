package com.te.UI;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.SystemClock;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cipherlab.terminalemulation.R;

import java.util.ArrayList;
import java.util.List;

import Terminals.ContentView;
import Terminals.TESettingsInfo;

public class TEKeyboardViewUtility implements KeyboardView.OnKeyboardActionListener {
    public interface TEKeyboardViewListener {
        void onSetKeyboardType(KeyboardType kType);
        void onShowKeyboard();
        void onHideKeyboard();
    }
    public enum KeyboardType {
        KT_ABC,
        KT_ABC_UPPER_SHIFT_ONCE,
        KT_ABC_UPPER_SHIFT_TOGGLE,
        KT_Symbol,
        KT_Fun,
        KT_Server,
    }

    //Top function keys begin
    private final int MY_KEYCODE_ABC = -12;
    private final int MY_KEYCODE_SYMBOL = -13;
    private final int MY_KEYCODE_FUNC = -14;
    private final int MY_KEYCODE_SERVER = -15;
    private final int MY_KEYCODE_HIDE = -16;
    private final int MY_KEYCODE_SYSKEY = -17;
    //Top function keys end

    //ABC begin
    private final int MY_KEYCODE_DOWN = -7;
    private final int MY_KEYCODE_UP = -8;
    private final int MY_KEYCODE_LEFT = -9;
    private final int MY_KEYCODE_RIGHT = -10;
    private final int MY_KEYCODE_TAB = -11;
    private final int MY_KEYCODE_SHIFT = -1;
    private final int MY_KEYCODE_SHIFT_ONCE = -2;
    private final int MY_KEYCODE_SHIFT_TOGGLE = -3;
    //ABC end

    //F1~F24
    private final int MY_KEYCODE_F1 = -88;
    private final int MY_KEYCODE_F2 = -89;
    private final int MY_KEYCODE_F3 = -90;
    private final int MY_KEYCODE_F4 = -91;
    private final int MY_KEYCODE_F5 = -92;
    private final int MY_KEYCODE_F6 = -93;
    private final int MY_KEYCODE_F7 = -94;
    private final int MY_KEYCODE_F8 = -95;
    private final int MY_KEYCODE_F9 = -96;
    private final int MY_KEYCODE_F10 = -97;
    private final int MY_KEYCODE_F11 = -98;
    private final int MY_KEYCODE_F12 = -99;
    private final int MY_KEYCODE_F13 = -100;
    private final int MY_KEYCODE_F14 = -101;
    private final int MY_KEYCODE_F15 = -102;
    private final int MY_KEYCODE_F16 = -103;
    private final int MY_KEYCODE_F17 = -104;
    private final int MY_KEYCODE_F18 = -105;
    private final int MY_KEYCODE_F19 = -106;
    private final int MY_KEYCODE_F20 = -107;
    private final int MY_KEYCODE_F21 = -108;
    private final int MY_KEYCODE_F22 = -109;
    private final int MY_KEYCODE_F23 = -110;
    private final int MY_KEYCODE_F24 = -111;

    //TN server key begin
    private final int MY_KEYCODE_TN_ATTN = -18;
    private final int MY_KEYCODE_TN_BKSP = -19;
    private final int MY_KEYCODE_TN_PRVS = -20;
    private final int MY_KEYCODE_TN_CLR = -21;
    private final int MY_KEYCODE_TN_CLREOF = -22;
    private final int MY_KEYCODE_TN_DEL = -23;
    private final int MY_KEYCODE_TN_FIELDMARK = -24;
    private final int MY_KEYCODE_TN_FIELD_EXIT = -25;
    private final int MY_KEYCODE_TN_FIELD_BEG = -26;
    private final int MY_KEYCODE_TN_FIELD_END = -27;
    private final int MY_KEYCODE_TN_FIELD_DUP = -28;
    private final int MY_KEYCODE_TN_ERA_INP = -29;
    private final int MY_KEYCODE_TN_FIELD_PLUS = -30;
    private final int MY_KEYCODE_TN_FIELD_MIN = -31;
    private final int MY_KEYCODE_TN_LAST = -32;
    private final int MY_KEYCODE_TN_HOME = -33;
    private final int MY_KEYCODE_TN_INSERT = -34;
    private final int MY_KEYCODE_TN_NEWLINE = -35;
    private final int MY_KEYCODE_TN_RESET = -36;
    private final int MY_KEYCODE_TN_ROLLUP = -37;
    private final int MY_KEYCODE_TN_ROLLDN = -38;
    private final int MY_KEYCODE_TN_SYSREQ = -39;
    private final int MY_KEYCODE_TN_NEXT = -40;
    private final int MY_KEYCODE_TN_RECORD = -41;
    //TN server key end

    //VT server key begin
    static final int MY_KEYCODE_VT_PGUP = -42;
    static final int MY_KEYCODE_VT_PGDW = -43;
    static final int MY_KEYCODE_VT_HOME = -44;
    static final int MY_KEYCODE_VT_END = -45;
    static final int MY_KEYCODE_VT_INS = -46;
    static final int MY_KEYCODE_VT_BS = -47;
    static final int MY_KEYCODE_VT_DEL = -48;
    static final int MY_KEYCODE_VT_ESC = -49;
    static final int MY_KEYCODE_VT_LF = -50;
    static final int MY_KEYCODE_VT_FIND = -51;
    static final int MY_KEYCODE_VT_SELECT = -52;
    static final int MY_KEYCODE_VT_REMOVE = -53;
    static final int MY_KEYCODE_VT_PRESCREEN = -54;
    static final int MY_KEYCODE_VT_NEXTSCREEN = -55;
    //VT server key end

    private Context mContext = null;
    private ContentView mTargetView = null;
    private KeyboardView mKeyboardView = null;
    private KeyboardType mCurKBType = KeyboardType.KT_ABC;
    private Keyboard mABCKeyboard = null;
    private Keyboard mABCUpperShiftOnceKeyboard = null;
    private Keyboard mABCUpperShiftToggleKeyboard = null;
    private List<Keyboard.Key> mListABCKeys = new ArrayList<>();
    private Keyboard mSymbolKeyboard = null;
    private List<Keyboard.Key> mListSymbolKeys = new ArrayList<>();
    private Keyboard mVTFunKeyboard = null;
    private Keyboard mTNFunKeyboard = null;
    private List<Keyboard.Key> mListFunKeys = new ArrayList<>();
    private Keyboard mTNServerKeyboard = null;
    private Keyboard mVTServerKeyboard = null;
    private List<Keyboard.Key> mListServerKeys = new ArrayList<>();
    private KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
    private TEKeyboardViewListener mListener = null;

    public TEKeyboardViewUtility(Context context, KeyboardView view, ContentView contentView) {
        mContext = context;
        mKeyboardView = view;
        mTargetView = contentView;
        mABCKeyboard = new Keyboard(context, R.xml.keyboard_abc);
        mABCUpperShiftOnceKeyboard = new Keyboard(context, R.xml.keyboard_abc_upper_shift_once);
        mABCUpperShiftToggleKeyboard = new Keyboard(context, R.xml.keyboard_abc_upper_shift_toogle);
        mSymbolKeyboard = new Keyboard(context, R.xml.keyboard_symbol);
        mTNFunKeyboard = new Keyboard(context, R.xml.keyboard_tn_funl);
        mVTFunKeyboard = new Keyboard(context, R.xml.keyboard_vt_funl);
        mTNServerKeyboard = new Keyboard(context, R.xml.keyboard_tn_server);
        mVTServerKeyboard = new Keyboard(context, R.xml.keyboard_vt_server);

        collectKeysToContainer(mABCKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mABCUpperShiftOnceKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mABCUpperShiftToggleKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mABCKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mSymbolKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mTNFunKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mVTFunKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mTNServerKeyboard, MY_KEYCODE_ABC, mListABCKeys);
        collectKeysToContainer(mVTServerKeyboard, MY_KEYCODE_ABC, mListABCKeys);

        collectKeysToContainer(mABCKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);
        collectKeysToContainer(mABCUpperShiftOnceKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);
        collectKeysToContainer(mABCUpperShiftToggleKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);
        collectKeysToContainer(mSymbolKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);
        collectKeysToContainer(mTNFunKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);
        collectKeysToContainer(mVTFunKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);
        collectKeysToContainer(mTNServerKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);
        collectKeysToContainer(mVTServerKeyboard, MY_KEYCODE_SYMBOL, mListSymbolKeys);

        collectKeysToContainer(mABCKeyboard, MY_KEYCODE_FUNC, mListFunKeys);
        collectKeysToContainer(mABCUpperShiftOnceKeyboard, MY_KEYCODE_FUNC, mListFunKeys);
        collectKeysToContainer(mABCUpperShiftToggleKeyboard, MY_KEYCODE_FUNC, mListFunKeys);
        collectKeysToContainer(mSymbolKeyboard, MY_KEYCODE_FUNC, mListFunKeys);
        collectKeysToContainer(mTNFunKeyboard, MY_KEYCODE_FUNC, mListFunKeys);
        collectKeysToContainer(mVTFunKeyboard, MY_KEYCODE_FUNC, mListFunKeys);
        collectKeysToContainer(mTNServerKeyboard, MY_KEYCODE_FUNC, mListFunKeys);
        collectKeysToContainer(mVTServerKeyboard, MY_KEYCODE_FUNC, mListFunKeys);

        collectKeysToContainer(mABCKeyboard, MY_KEYCODE_SERVER, mListServerKeys);
        collectKeysToContainer(mABCUpperShiftOnceKeyboard, MY_KEYCODE_SERVER, mListServerKeys);
        collectKeysToContainer(mABCUpperShiftToggleKeyboard, MY_KEYCODE_SERVER, mListServerKeys);
        collectKeysToContainer(mSymbolKeyboard, MY_KEYCODE_SERVER, mListServerKeys);
        collectKeysToContainer(mTNFunKeyboard, MY_KEYCODE_SERVER, mListServerKeys);
        collectKeysToContainer(mVTFunKeyboard, MY_KEYCODE_SERVER, mListServerKeys);
        collectKeysToContainer(mTNServerKeyboard, MY_KEYCODE_SERVER, mListServerKeys);
        collectKeysToContainer(mVTServerKeyboard, MY_KEYCODE_SERVER, mListServerKeys);

        setKeyboard(KeyboardType.KT_ABC);
        mKeyboardView.setOnKeyboardActionListener(this);
        mKeyboardView.setPreviewEnabled(false);
    }

    private void keyDownUp(int keyEventCode) {
        sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void serverKeyDownUp(int keyEventCode) {
        sendKeyEvent(new ServerKeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        sendKeyEvent(new ServerKeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKeyEvent(KeyEvent event) {
        mTargetView.dispatchKeyEvent(event);
    }

    private void collectKeysToContainer(Keyboard keyboard, int nKeycode, List<Keyboard.Key> list) {
        for (int idxKey = 0; idxKey < keyboard.getKeys().size(); idxKey++) {
            Keyboard.Key key = keyboard.getKeys().get(idxKey);
            if (key.codes[0] == nKeycode) {
                list.add(key);
            }
        }
    }

    private void setListKeysToggle(List<Keyboard.Key> list, boolean bToggle) {
        for (Keyboard.Key key : list) {
            key.on = bToggle;
        }
    }

    //Functions
    public void setListener(TEKeyboardViewListener listener) {
        mListener = listener;
    }

    public void setKeyboard(KeyboardType kType) {
        mCurKBType = kType;
        setListKeysToggle(mListABCKeys, false);
        setListKeysToggle(mListSymbolKeys, false);
        setListKeysToggle(mListFunKeys, false);
        setListKeysToggle(mListServerKeys, false);

        switch (kType) {
            case KT_ABC:
            default:
                setListKeysToggle(mListABCKeys, true);
                mKeyboardView.setKeyboard(mABCKeyboard);
                break;
            case KT_ABC_UPPER_SHIFT_ONCE:
                mKeyboardView.setKeyboard(mABCUpperShiftOnceKeyboard);
                setListKeysToggle(mListABCKeys, true);
                break;
            case KT_ABC_UPPER_SHIFT_TOGGLE:
                mKeyboardView.setKeyboard(mABCUpperShiftToggleKeyboard);
                setListKeysToggle(mListABCKeys, true);
                break;
            case KT_Symbol:
                setListKeysToggle(mListSymbolKeys, true);
                mKeyboardView.setKeyboard(mSymbolKeyboard);
                break;
            case KT_Fun:
            {
                setListKeysToggle(mListFunKeys, true);
                if(TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex()) == true) {
                    mKeyboardView.setKeyboard(mTNFunKeyboard);
                }
                else {
                    mKeyboardView.setKeyboard(mVTFunKeyboard);
                }
            }
            break;
            case KT_Server:
            {
                setListKeysToggle(mListServerKeys, true);
                if(TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex()) == true) {
                    mKeyboardView.setKeyboard(mTNServerKeyboard);
                }
                else {
                    mKeyboardView.setKeyboard(mVTServerKeyboard);
                }
            }
            break;
        }
    }

    public void showTEKeyboard() {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if(mTargetView !=null)
            ((InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mTargetView.getWindowToken(), 0);
        if(mListener != null)
            mListener.onShowKeyboard();
    }

    public void hideTEKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
        if(mListener != null)
            mListener.onHideKeyboard();
    }

    public boolean isTEKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    //KeyboardView.OnKeyboardActionListener Begin
    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        CipherUtility.vibration(100);
        switch(primaryCode) {
            case MY_KEYCODE_ABC:
            {
                setKeyboard(KeyboardType.KT_ABC);
                mListener.onSetKeyboardType(KeyboardType.KT_ABC);
            }
            break;
            case MY_KEYCODE_SHIFT:
            {
                setKeyboard(KeyboardType.KT_ABC_UPPER_SHIFT_ONCE);
                mListener.onSetKeyboardType(KeyboardType.KT_ABC_UPPER_SHIFT_ONCE);
            }
            break;
            case MY_KEYCODE_SHIFT_ONCE:
            {
                setKeyboard(KeyboardType.KT_ABC_UPPER_SHIFT_TOGGLE);
                mListener.onSetKeyboardType(KeyboardType.KT_ABC_UPPER_SHIFT_TOGGLE);
            }
            break;
            case MY_KEYCODE_SHIFT_TOGGLE:
            {
                setKeyboard(KeyboardType.KT_ABC);
                mListener.onSetKeyboardType(KeyboardType.KT_ABC);
            }
            break;
            case MY_KEYCODE_SYMBOL:
            {
                setKeyboard(KeyboardType.KT_Symbol);
                mListener.onSetKeyboardType(KeyboardType.KT_Symbol);
            }
            break;
            case MY_KEYCODE_FUNC:
            {
                setKeyboard(KeyboardType.KT_Fun);
                mListener.onSetKeyboardType(KeyboardType.KT_Fun);
            }
            break;
            case MY_KEYCODE_SERVER:
            {
                setKeyboard(KeyboardType.KT_Server);
                mListener.onSetKeyboardType(KeyboardType.KT_Server);
            }
            break;
            case MY_KEYCODE_HIDE:
            {
                hideTEKeyboard();
            }
            break;
            case MY_KEYCODE_SYSKEY:
            {
                hideTEKeyboard();
                UIUtility.showSIP(mContext, mTargetView);
            }
            break;
            case MY_KEYCODE_DOWN:
            {
                keyDownUp(KeyEvent.KEYCODE_DPAD_DOWN);
            }
            break;
            case MY_KEYCODE_UP:
            {
                keyDownUp(KeyEvent.KEYCODE_DPAD_UP);
            }
            break;
            case MY_KEYCODE_LEFT:
            {
                keyDownUp(KeyEvent.KEYCODE_DPAD_LEFT);
            }
            break;
            case MY_KEYCODE_RIGHT:
            {
                keyDownUp(KeyEvent.KEYCODE_DPAD_RIGHT);
            }
            break;
            case Keyboard.KEYCODE_DELETE:
            {
                keyDownUp(KeyEvent.KEYCODE_DEL);
            }
            break;
            case MY_KEYCODE_F1:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F1);
                break;
            case MY_KEYCODE_F2:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F2);
                break;
            case MY_KEYCODE_F3:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F3);
                break;
            case MY_KEYCODE_F4:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F4);
                break;
            case MY_KEYCODE_F5:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F5);
                break;
            case MY_KEYCODE_F6:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F6);
                break;
            case MY_KEYCODE_F7:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F7);
                break;
            case MY_KEYCODE_F8:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F8);
                break;
            case MY_KEYCODE_F9:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F9);
                break;
            case MY_KEYCODE_F10:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F10);
                break;
            case MY_KEYCODE_F11:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F11);
                break;
            case MY_KEYCODE_F12:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F12);
                break;
            case MY_KEYCODE_F13:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F13);
                break;
            case MY_KEYCODE_F14:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F14);
                break;
            case MY_KEYCODE_F15:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F15);
                break;
            case MY_KEYCODE_F16:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F16);
                break;
            case MY_KEYCODE_F17:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F17);
                break;
            case MY_KEYCODE_F18:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F18);
                break;
            case MY_KEYCODE_F19:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F19);
                break;
            case MY_KEYCODE_F20:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F20);
                break;
            case MY_KEYCODE_F21:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F21);
                break;
            case MY_KEYCODE_F22:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F22);
                break;
            case MY_KEYCODE_F23:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F23);
                break;
            case MY_KEYCODE_F24:
                serverKeyDownUp(ServerKeyEvent.FUN_KEYCODE_F24);
                break;
            case MY_KEYCODE_TAB:
            {
                keyDownUp(KeyEvent.KEYCODE_TAB);
            }
            break;
            case MY_KEYCODE_TN_ATTN:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ATTN);
            }
            break;
            case MY_KEYCODE_TN_BKSP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_LEFTDELETE);
            }
            break;
            case MY_KEYCODE_TN_PRVS:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_PREV);
            }
            break;
            case MY_KEYCODE_TN_CLR:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_CLR);
            }
            break;
            case MY_KEYCODE_TN_CLREOF:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_CLREOF);
            }
            break;
            case MY_KEYCODE_TN_DEL:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_DEL);
            }
            break;
            case MY_KEYCODE_TN_FIELDMARK:
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FMARK);
                break;
            case MY_KEYCODE_TN_FIELD_EXIT:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FEXIT);
            }
            break;
            case MY_KEYCODE_TN_FIELD_BEG:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FBEGIN);
            }
            break;
            case MY_KEYCODE_TN_FIELD_END:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FEND);
            }
            break;
            case MY_KEYCODE_TN_FIELD_DUP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_DUP);
            }
            break;
            case MY_KEYCODE_TN_ERA_INP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ERINPUT);
            }
            break;
            case MY_KEYCODE_TN_FIELD_PLUS:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FPLUS);
            }
            break;
            case MY_KEYCODE_TN_FIELD_MIN:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FMINUS);
            }
            break;
            case MY_KEYCODE_TN_LAST:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_LAST);
            }
            break;
            case MY_KEYCODE_TN_HOME:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_HOME);
            }
            break;
            case MY_KEYCODE_TN_INSERT:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_INS);
            }
            break;
            case MY_KEYCODE_TN_NEWLINE:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_NEWLINE);
            }
            break;
            case MY_KEYCODE_TN_RESET:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_RESET);
            }
            break;
            case MY_KEYCODE_TN_ROLLUP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ROLUP);
            }
            break;
            case MY_KEYCODE_TN_ROLLDN:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ROLDN);
            }
            break;
            case MY_KEYCODE_TN_SYSREQ:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_SYSRQ);
            }
            break;
            case MY_KEYCODE_TN_NEXT:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_NEXT);
            }
            break;
            case MY_KEYCODE_TN_RECORD:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_RECORD);
            }
            break;
            case MY_KEYCODE_VT_PGUP:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_PGUP);
            }
            break;
            case MY_KEYCODE_VT_PGDW:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_PGDW);
            }
            break;
            case MY_KEYCODE_VT_HOME:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_HOME);
            }
            break;
            case MY_KEYCODE_VT_END:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_END);
            }
            break;
            case MY_KEYCODE_VT_INS:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_INS);
            }
            break;
            case MY_KEYCODE_VT_BS:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_BS);
            }
            break;
            case MY_KEYCODE_VT_DEL:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_DEL);
            }
            break;
            case MY_KEYCODE_VT_ESC:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_ESC);
            }
            break;
            case MY_KEYCODE_VT_LF:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_LF);
            }
            break;
            case MY_KEYCODE_VT_FIND:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_FIND);
            }
            break;
            case MY_KEYCODE_VT_SELECT:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_SELECT);
            }
            break;
            case MY_KEYCODE_VT_REMOVE:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_REMOVE);
            }
            break;
            case MY_KEYCODE_VT_PRESCREEN:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_PRESCREEN);
            }
            break;
            case MY_KEYCODE_VT_NEXTSCREEN:
            {
                serverKeyDownUp(ServerKeyEvent.VT_KEYCODE_NEXTSCREEN);
            }
            break;
            default://Enter(10), space(32), Characters
            {
                char [] chars = String.valueOf((char)primaryCode).toCharArray();
                if (chars.length == 1) {
                    if(mCurKBType == KeyboardType.KT_ABC_UPPER_SHIFT_ONCE && Character.isUpperCase(chars[0])) {
                        setKeyboard(KeyboardType.KT_ABC);
                        mListener.onSetKeyboardType(KeyboardType.KT_ABC);
                    }
                    // If it's 1 character, we have a chance of being
                    // able to generate normal key events...
                    if (mKeyCharacterMap == null) {
                        mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
                    }
                    KeyEvent[] events = mKeyCharacterMap.getEvents(chars);
                    if (events != null) {
                        for (int i=0; i<events.length; i++) {
                            sendKeyEvent(events[i]);
                        }
                        return;
                    }
                }

                // Otherwise, revert to the special key event containing
                // the actual characters.
                KeyEvent event = new KeyEvent(SystemClock.uptimeMillis(),
                        chars.toString(), KeyCharacterMap.VIRTUAL_KEYBOARD, 0);
                sendKeyEvent(event);
            }
            break;
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
    //KeyboardView.OnKeyboardActionListener End
}
