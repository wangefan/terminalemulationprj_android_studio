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

import com.example.terminalemulation.R;

import Terminals.ContentView;
import Terminals.TESettingsInfo;

/**
 * Created by yifan.wang on 2016/4/28.
 */
public class TEKeyboardViewUtility implements KeyboardView.OnKeyboardActionListener {
    public interface TEKeyboardViewListener {
        public void onShowKeyboard();
        public void onHideKeyboard();
    }
    private enum KeyboardType {
        KT_ABC,
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
    //ABC end

    //TN server key begin
    private final int MY_KEYCODE_ATTN = -18;
    private final int MY_KEYCODE_BKSP = -19;
    private final int MY_KEYCODE_PRVS = -20;
    private final int MY_KEYCODE_CLR = -21;
    private final int MY_KEYCODE_CLREOF = -22;
    private final int MY_KEYCODE_DEL = -23;
    //private final int MY_KEYCODE_FIELDMARK = -24;  TN3270
    private final int MY_KEYCODE_FIELD_EXIT = -25;
    private final int MY_KEYCODE_FIELD_BEG = -26;
    private final int MY_KEYCODE_FIELD_END = -27;
    private final int MY_KEYCODE_FIELD_DUP = -28;
    private final int MY_KEYCODE_ERA_INP = -29;
    private final int MY_KEYCODE_FIELD_PLUS = -30;
    private final int MY_KEYCODE_FIELD_MIN = -31;
    private final int MY_KEYCODE_LAST = -32;
    private final int MY_KEYCODE_HOME = -33;
    private final int MY_KEYCODE_INSERT = -34;
    private final int MY_KEYCODE_NEWLINE = -35;
    private final int MY_KEYCODE_RESET = -36;
    private final int MY_KEYCODE_ROLLUP = -37;
    private final int MY_KEYCODE_ROLLDN = -38;
    private final int MY_KEYCODE_SYSREQ = -39;
    private final int MY_KEYCODE_NEXT = -40;
    private final int MY_KEYCODE_RECORD = -41;
    //TN server key end

    private KeyboardType mKeyboardType = KeyboardType.KT_ABC;
    private Context mContext = null;
    private ContentView mTargetView = null;
    private KeyboardView mKeyboardView = null;
    private Keyboard mABCKeyboard = null;
    private Keyboard mSymbolKeyboard = null;
    private Keyboard mVTFunKeyboard = null;
    private Keyboard mTNFunKeyboard = null;
    private Keyboard mTNServerKeyboard = null;
    private Keyboard mVTServerKeyboard = null;
    private KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
    private TEKeyboardViewListener mLisitener  = null;

    public TEKeyboardViewUtility(Context context, KeyboardView view, ContentView contentView) {
        mContext = context;
        mKeyboardView = view;
        mTargetView = contentView;
        mABCKeyboard = new Keyboard(context, R.xml.keyboard_abc);
        mSymbolKeyboard = new Keyboard(context, R.xml.keyboard_symbol);
        mTNFunKeyboard = new Keyboard(context, R.xml.keyboard_tn_funl);
        mVTFunKeyboard = new Keyboard(context, R.xml.keyboard_vt_funl);
        mTNServerKeyboard = new Keyboard(context, R.xml.keyboard_tn_server);
        mVTServerKeyboard = new Keyboard(context, R.xml.keyboard_vt_server);
        mKeyboardView.setKeyboard(mABCKeyboard);
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

    //Functions
    public void setListener(TEKeyboardViewListener listener) {
        mLisitener = listener;
    }

    public void hideTEKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
        if(mLisitener != null)
            mLisitener.onHideKeyboard();
    }

    public void showTEKeyboard() {
        switch (mKeyboardType) {
            case KT_ABC:
            default:
                mKeyboardView.setKeyboard(mABCKeyboard);
                break;
            case KT_Symbol:
                mKeyboardView.setKeyboard(mSymbolKeyboard);
                break;
            case KT_Fun:
            {
                if(TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex()) == true) {
                    mKeyboardView.setKeyboard(mTNFunKeyboard);
                }
                else {
                    mKeyboardView.setKeyboard(mVTFunKeyboard);
                }
            }
            break;
        }
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if(mTargetView !=null)
            ((InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mTargetView.getWindowToken(), 0);
        if(mLisitener != null)
            mLisitener.onShowKeyboard();
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
        switch(primaryCode) {
            case MY_KEYCODE_ABC:
            {
                mKeyboardView.setKeyboard(mABCKeyboard);
                mKeyboardType = KeyboardType.KT_ABC;
            }
            break;
            case MY_KEYCODE_SYMBOL:
            {
                mKeyboardView.setKeyboard(mSymbolKeyboard);
                mKeyboardType = KeyboardType.KT_Symbol;
            }
            break;
            case MY_KEYCODE_FUNC:
            {
                if(TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex()) == true) {
                    mKeyboardView.setKeyboard(mTNFunKeyboard);
                }
                else {
                    mKeyboardView.setKeyboard(mVTFunKeyboard);
                }
                mKeyboardType = KeyboardType.KT_Fun;
            }
            break;
            case MY_KEYCODE_SERVER:
            {
                if(TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex()) == true) {
                    mKeyboardView.setKeyboard(mTNServerKeyboard);
                }
                else {
                    mKeyboardView.setKeyboard(mVTServerKeyboard);
                }
                mKeyboardType = KeyboardType.KT_Server;
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
                ((InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(mTargetView, 0);
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
            case Keyboard.KEYCODE_SHIFT:
            {
                //Todo: trigger shit or not
                //keyDownUp(KeyEvent.KEYCODE_SHIFT_LEFT);
            }
            break;
            case Keyboard.KEYCODE_DELETE:
            {
                keyDownUp(KeyEvent.KEYCODE_DEL);
            }
            break;
            case KeyEvent.KEYCODE_F1:
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_F4:
            case KeyEvent.KEYCODE_F5:
            case KeyEvent.KEYCODE_F6:
            case KeyEvent.KEYCODE_F7:
            case KeyEvent.KEYCODE_F8:
            case KeyEvent.KEYCODE_F9:
            case KeyEvent.KEYCODE_F10:
            case KeyEvent.KEYCODE_F11:
            case KeyEvent.KEYCODE_F12:
            {
                keyDownUp(primaryCode);
            }
            break;
            case MY_KEYCODE_TAB:
            {
                keyDownUp(KeyEvent.KEYCODE_TAB);
            }
            break;
            case MY_KEYCODE_ATTN:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ATTN);
            }
            break;
            case MY_KEYCODE_BKSP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_LEFTDELETE);
            }
            break;
            case MY_KEYCODE_PRVS:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_PREV);
            }
            break;
            case MY_KEYCODE_CLR:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_CLR);
            }
            break;
            case MY_KEYCODE_CLREOF:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_CLREOF);
            }
            break;
            case MY_KEYCODE_DEL:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_DEL);
            }
            break;
            case MY_KEYCODE_FIELD_EXIT:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FEXIT);
            }
            break;
            case MY_KEYCODE_FIELD_BEG:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FBEGIN);
            }
            break;
            case MY_KEYCODE_FIELD_END:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FEND);
            }
            break;
            case MY_KEYCODE_FIELD_DUP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_DUP);
            }
            break;
            case MY_KEYCODE_ERA_INP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ERINPUT);
            }
            break;
            case MY_KEYCODE_FIELD_PLUS:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FPLUS);
            }
            break;
            case MY_KEYCODE_FIELD_MIN:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_FMINUS);
            }
            break;
            case MY_KEYCODE_LAST:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_LAST);
            }
            break;
            case MY_KEYCODE_HOME:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_HOME);
            }
            break;
            case MY_KEYCODE_INSERT:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_INS);
            }
            break;
            case MY_KEYCODE_NEWLINE:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_NEWLINE);
            }
            break;
            case MY_KEYCODE_RESET:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_RESET);
            }
            break;
            case MY_KEYCODE_ROLLUP:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ROLUP);
            }
            break;
            case MY_KEYCODE_ROLLDN:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_ROLDN);
            }
            break;
            case MY_KEYCODE_SYSREQ:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_SYSRQ);
            }
            break;
            case MY_KEYCODE_NEXT:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_NEXT);
            }
            break;
            case MY_KEYCODE_RECORD:
            {
                serverKeyDownUp(ServerKeyEvent.TN_KEYCODE_RECORD);
            }
            break;
            default://Enter(10), space(32), Characters
            {
                char [] chars = String.valueOf((char)primaryCode).toCharArray();
                if (chars.length == 1) {
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
