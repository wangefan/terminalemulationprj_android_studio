package Terminals;

import android.view.KeyEvent;

import com.cipherlab.terminalemulation.R;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyMapUtility {
    public  static final int KEYCODE__CUSTOM_281 = 281;
    public  static final int KEYCODE__CUSTOM_300 = 300;
    public  static final int KEYCODE__CUSTOM_301 = 301;

    private static final int NSHIFTVAL = 1000;
    private static final int NCTRLVAL = 2000;
    private static final int NALTVAL = 4000;
    final static public LinkedHashMap<Integer, Integer> mKeyCodeWithCombinFrom53KeyPadMap = new LinkedHashMap<>();//Key: original Key code, Val: Keycode with Blue key
    static {
        mKeyCodeWithCombinFrom53KeyPadMap.clear();
        //Alphabets
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_COMMA, KeyEvent.KEYCODE_A);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_B);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_APOSTROPHE, KeyEvent.KEYCODE_C);
        //<Blue><D>  System HW used
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, KeyEvent.KEYCODE_E);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, KeyEvent.KEYCODE_F);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_BACKSLASH, KeyEvent.KEYCODE_G);
        //<Blue><H>  System HW used
        //<Blue><I>  System HW used
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_GRAVE, KeyEvent.KEYCODE_J);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F12, KeyEvent.KEYCODE_K);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F11, KeyEvent.KEYCODE_L);
        //<Blue><M>  System HW used
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_MINUS, KeyEvent.KEYCODE_N);
        //Todo:<Blue><O>  is 281, in windows is VK_F13
        //Todo:<Blue><P>  is 300, green button.
        //Todo:<Blue><Q>  is 301, red button
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_SEMICOLON, KeyEvent.KEYCODE_R);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, KeyEvent.KEYCODE_S);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, KeyEvent.KEYCODE_T);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, KeyEvent.KEYCODE_U);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_NUMPAD_DIVIDE, KeyEvent.KEYCODE_V);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_NUMPAD_EQUALS, KeyEvent.KEYCODE_W);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_TAB, KeyEvent.KEYCODE_SPACE);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_PAGE_UP, KeyEvent.KEYCODE_PERIOD);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_PAGE_DOWN, KeyEvent.KEYCODE_STAR);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_MOVE_END, KeyEvent.KEYCODE_DEL);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F1, KeyEvent.KEYCODE_1);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F2, KeyEvent.KEYCODE_2);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F3, KeyEvent.KEYCODE_3);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F4, KeyEvent.KEYCODE_4);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F5, KeyEvent.KEYCODE_5);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F6, KeyEvent.KEYCODE_6);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F7, KeyEvent.KEYCODE_7);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F8, KeyEvent.KEYCODE_8);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F9, KeyEvent.KEYCODE_9);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyEvent.KEYCODE_F10, KeyEvent.KEYCODE_0);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyMapUtility.KEYCODE__CUSTOM_281, KeyEvent.KEYCODE_O);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyMapUtility.KEYCODE__CUSTOM_300, KeyEvent.KEYCODE_P);
        mKeyCodeWithCombinFrom53KeyPadMap.put(KeyMapUtility.KEYCODE__CUSTOM_301, KeyEvent.KEYCODE_Q);
    }

    public static int decodePhyCodeRetunHelpKey(int nEncodedPhyCode, AtomicBoolean bHasCtrl, AtomicBoolean bHasShift, AtomicBoolean bHasAlt) {
        int nPhyResult = nEncodedPhyCode;
        if(nEncodedPhyCode >= (NSHIFTVAL + NCTRLVAL + NALTVAL)) {
            nPhyResult =  nEncodedPhyCode - (NSHIFTVAL + NCTRLVAL + NALTVAL);
            if(bHasCtrl != null)
                bHasCtrl.set(true);
            if(bHasShift != null)
                bHasShift.set(true);
            if(bHasAlt != null)
                bHasAlt.set(true);
            return nPhyResult;
        }
        if(nEncodedPhyCode >= (NCTRLVAL + NALTVAL)) {
            nPhyResult =  nEncodedPhyCode - (NCTRLVAL + NALTVAL);
            if(bHasCtrl != null)
                bHasCtrl.set(true);
            if(bHasAlt != null)
                bHasAlt.set(true);
            if(bHasShift != null)
                bHasShift.set(false);
            return nPhyResult;
        }
        if(nEncodedPhyCode >= (NSHIFTVAL + NALTVAL)) {
            nPhyResult =  nEncodedPhyCode - (NSHIFTVAL + NALTVAL);
            if(bHasShift != null)
                bHasShift.set(true);
            if(bHasAlt != null)
                bHasAlt.set(true);
            if(bHasCtrl != null)
                bHasCtrl.set(false);
            return nPhyResult;
        }
        if(nEncodedPhyCode >= NALTVAL) {
            nPhyResult =  nEncodedPhyCode - NALTVAL;
            if(bHasAlt != null)
                bHasAlt.set(true);
            if(bHasCtrl != null)
                bHasCtrl.set(false);
            if(bHasShift != null)
                bHasShift.set(false);
            return nPhyResult;
        }
        if(nEncodedPhyCode >= (NSHIFTVAL + NCTRLVAL)) {
            nPhyResult =  nEncodedPhyCode - (NSHIFTVAL + NCTRLVAL);
            if(bHasCtrl != null)
                bHasCtrl.set(true);
            if(bHasShift != null)
                bHasShift.set(true);
            if(bHasAlt != null)
                bHasAlt.set(false);
            return nPhyResult;
        }
        if(nEncodedPhyCode >= NCTRLVAL) {
            nPhyResult =  nEncodedPhyCode - NCTRLVAL;
            if(bHasCtrl != null)
                bHasCtrl.set(true);
            if(bHasAlt != null)
                bHasAlt.set(false);
            if(bHasShift != null)
                bHasShift.set(false);
            return nPhyResult;
        }
        if(nEncodedPhyCode >= NSHIFTVAL) {
            nPhyResult =  nEncodedPhyCode - NSHIFTVAL;
            if(bHasShift != null)
                bHasShift.set(true);
            if(bHasCtrl != null)
                bHasCtrl.set(false);
            if(bHasAlt != null)
                bHasAlt.set(false);
            return nPhyResult;
        }
        return nPhyResult;
    }

    public static int encodePhyKeyCode(int nPhyOrgCode, boolean bHasCtrl, boolean bHasShift, boolean bHasAlt) {
        int nPhyResult = nPhyOrgCode;
        if(bHasCtrl)
            nPhyResult += NCTRLVAL;
        if(bHasShift)
            nPhyResult += NSHIFTVAL;
        if(bHasAlt)
            nPhyResult += NALTVAL;
        return nPhyResult;
    }

    public static String getPhysicalKeyTextByEncode(int nEncodedPhyKeycode) {
        String result = "";
        AtomicBoolean bHasCtrl = new AtomicBoolean(false);
        AtomicBoolean bHasShift = new AtomicBoolean(false);
        AtomicBoolean bHasAlt = new AtomicBoolean(false);
        int nDecodePhysicalKeyCode = decodePhyCodeRetunHelpKey(nEncodedPhyKeycode,
                bHasCtrl,
                bHasShift,
                bHasAlt);
        if(nDecodePhysicalKeyCode == KeyMapItem.UNDEFINE_PHY) {
            result = stdActivityRef.getCurrActivity().getString(R.string.undefinedPhy);
        } else {
            if(bHasCtrl.get()) {
                result += stdActivityRef.getCurrActivity().getString(R.string.ctrl);
            }
            if(bHasShift.get()) {
                result += stdActivityRef.getCurrActivity().getString(R.string.shift);
            }
            if(bHasAlt.get()) {
                result += stdActivityRef.getCurrActivity().getString(R.string.Alt);
            }
            result += getPhyKeycodeTextByKeycode(nDecodePhysicalKeyCode);
        }
        return result;
    }

    private static boolean is53KeyBlueKeyCombin(int keycode, AtomicInteger combinKey) {
        if(mKeyCodeWithCombinFrom53KeyPadMap.containsKey(keycode)) {
            combinKey.set(mKeyCodeWithCombinFrom53KeyPadMap.get(keycode));
            return true;
        }
        return false;
    }

    public static String getPhyKeycodeTextByKeycode(int nDecodedKeyCode) {
        if(stdActivityRef.is53Key()) {
            AtomicInteger combinKey = new AtomicInteger(KeyEvent.KEYCODE_UNKNOWN);
            if(is53KeyBlueKeyCombin(nDecodedKeyCode, combinKey) && combinKey.get() != KeyEvent.KEYCODE_UNKNOWN) {
                String format = stdActivityRef.getCurrActivity().getResources().getString(R.string.Format_phy_key_text);
                return String.format(format, KeyEvent.keyCodeToString(nDecodedKeyCode).replace("KEYCODE_",""), KeyEvent.keyCodeToString(combinKey.get()).replace("KEYCODE_",""));
            } else {
                return KeyEvent.keyCodeToString(nDecodedKeyCode).replace("KEYCODE_","");
            }
        } else {
            return KeyEvent.keyCodeToString(nDecodedKeyCode).replace("KEYCODE_","");
        }
    }

    private static boolean isCtrlPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_CTRL_MASK) != 0;
    }

    public static boolean isShiftPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_SHIFT_MASK) != 0;
    }

    private static boolean isAltPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_ALT_MASK) != 0;
    }
    public static int getEncodePhyKeyCode(KeyEvent event) {
        return encodePhyKeyCode(event.getKeyCode(), isCtrlPressed(event), isShiftPressed(event), isAltPressed(event));
    }
}
