package Terminals;

import android.view.KeyEvent;

import com.cipherlab.terminalemulation.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class KeyMapList extends ArrayList<KeyMapItem> {
    private static final int NSHIFTVAL = 1000;
    private static final int NCTRLVAL = 2000;
    private static final int NALTVAL = 4000;

    protected KeyMapList() {
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
                result = KeyEvent.keyCodeToString(nDecodePhysicalKeyCode);
            }
        }
        return result;
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
}
