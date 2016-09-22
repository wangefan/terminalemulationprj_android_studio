package Terminals;

import android.view.KeyEvent;

import com.cipherlab.terminalemulation.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class KeyMapList extends ArrayList<KeyMapItem> {
    private static final int NSHIFTVAL = 1000;
    private static final int NCTRLVAL = 2000;
    private static final int NALTVAL = 4000;
    final static public LinkedHashMap<Integer, Integer> mKeyCodeWithCombinMap = new LinkedHashMap<>();//Key: original Key code, Val: Keycode with Blue key

    protected KeyMapList() {
        mKeyCodeWithCombinMap.clear();
        //Alphabets
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_COMMA, KeyEvent.KEYCODE_A);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_B);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_GRAVE, KeyEvent.KEYCODE_C);
        //<Blue><D>  System HW used
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, KeyEvent.KEYCODE_E);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, KeyEvent.KEYCODE_F);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_BACKSLASH, KeyEvent.KEYCODE_G);
        //<Blue><H>  System HW used
        //<Blue><I>  System HW used
        //Todo:<Blue><J>  is the same with <Blue><C>, maybe system wrong
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_F12, KeyEvent.KEYCODE_K);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_F11, KeyEvent.KEYCODE_L);
        //<Blue><M>  System HW used
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_MINUS, KeyEvent.KEYCODE_N);
        //Todo:<Blue><O>  is 281, in windows is VK_F13
        //Todo:<Blue><P>  is 300, green button.
        //Todo:<Blue><Q>  is 301, red button
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_SEMICOLON, KeyEvent.KEYCODE_R);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, KeyEvent.KEYCODE_S);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, KeyEvent.KEYCODE_T);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, KeyEvent.KEYCODE_U);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_NUMPAD_DIVIDE, KeyEvent.KEYCODE_V);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_NUMPAD_EQUALS, KeyEvent.KEYCODE_W);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_TAB, KeyEvent.KEYCODE_SPACE);
        mKeyCodeWithCombinMap.put(KeyEvent.KEYCODE_PAGE_UP, KeyEvent.KEYCODE_PERIOD);
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

    abstract public String getServerKeyText(int position);
    abstract public String getServerKeyTextByKeycode(int nServerKeycode);

    public String getPhysicalKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nDecodePhysicalKeyCode = decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    new AtomicBoolean(false),
                    new AtomicBoolean(false),
                    new AtomicBoolean(false));
            if(nDecodePhysicalKeyCode == KeyMapItem.UNDEFINE_PHY) {
                result = stdActivityRef.getCurrActivity().getString(R.string.undefinedPhy);
            } else {
                result = KeyMapList.getPhyKeycodeTextByKeycode(nDecodePhysicalKeyCode);
            }
        }
        return result;
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
            result += KeyMapList.getPhyKeycodeTextByKeycode(nDecodePhysicalKeyCode);
        }
        return result;
    }

    private static boolean isBlueKeyCombin(int keycode, AtomicInteger combinKey) {
        if(mKeyCodeWithCombinMap.containsKey(keycode)) {
            combinKey.set(mKeyCodeWithCombinMap.get(keycode));
            return true;
        }
        return false;
    }

    public static String getPhyKeycodeTextByKeycode(int nDecodedKeyCode) {
        if(stdActivityRef.gDeviceHasKeys) {
            AtomicInteger combinKey = new AtomicInteger(KeyEvent.KEYCODE_UNKNOWN);
            if(isBlueKeyCombin(nDecodedKeyCode, combinKey) && combinKey.get() != KeyEvent.KEYCODE_UNKNOWN) {
                String format = stdActivityRef.getCurrActivity().getResources().getString(R.string.Format_phy_key_text);
                return String.format(format, KeyEvent.keyCodeToString(nDecodedKeyCode).replace("KEYCODE_",""), KeyEvent.keyCodeToString(combinKey.get()).replace("KEYCODE_",""));
            } else {
                return KeyEvent.keyCodeToString(nDecodedKeyCode).replace("KEYCODE_","");
            }
        } else {
            return KeyEvent.keyCodeToString(nDecodedKeyCode).replace("KEYCODE_","");
        }
    }

    public boolean hasShift(int position) {
        AtomicBoolean bHasShift = new AtomicBoolean(false);
        if(position < this.size()) {
            decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    null,
                    bHasShift,
                    null);
        }
        return bHasShift.get();
    }

    public boolean hasCtrl(int position) {
        AtomicBoolean bHasCtrl = new AtomicBoolean(false);
        if(position < this.size()) {
            decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    bHasCtrl,
                    null,
                    null);
        }
        return bHasCtrl.get();
    }

    public boolean hasAlt(int position) {
        AtomicBoolean bHasAlt = new AtomicBoolean(false);
        if(position < this.size()) {
            decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    null,
                    null,
                    bHasAlt);
        }
        return bHasAlt.get();
    }
    private static boolean isCtrlPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_CTRL_MASK) != 0;
    }

    private static boolean isShiftPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_SHIFT_MASK) != 0;
    }

    private static boolean isAltPressed(KeyEvent event) {
        return (event.getMetaState() & KeyEvent.META_ALT_MASK) != 0;
    }
    public static int getEncodePhyKeyCode(KeyEvent event) {
        return encodePhyKeyCode(event.getKeyCode(), isCtrlPressed(event), isShiftPressed(event), isAltPressed(event));
    }
}
