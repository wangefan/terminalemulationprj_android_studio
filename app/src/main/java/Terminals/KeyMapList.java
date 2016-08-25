package Terminals;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class KeyMapList extends ArrayList<KeyMapItem> {
    private static final int NSHIFTVAL = 1000;
    private static final int NCTRLVAL = 2000;
    private static final int NALTVAL = 4000;

    protected KeyMapList() {
    }

    protected int decodeVKCodeRetunHelpKey(int nEncodedVKCode, AtomicBoolean bHasCtrl, AtomicBoolean bHasShift, AtomicBoolean bHasAlt) {
        int nVKResult = nEncodedVKCode;
        if(nEncodedVKCode >= (NSHIFTVAL + NCTRLVAL + NALTVAL))
        {
            nVKResult =  nEncodedVKCode - (NSHIFTVAL + NCTRLVAL + NALTVAL);
            bHasCtrl.set(true);
            bHasShift.set(true);
            bHasAlt.set(true);
            return nVKResult;
        }
        if(nEncodedVKCode >= (NCTRLVAL + NALTVAL)) {
            nVKResult =  nEncodedVKCode - (NCTRLVAL + NALTVAL);
            bHasCtrl.set(true);
            bHasAlt.set(true);
            bHasShift.set(false);
            return nVKResult;
        }
        if(nEncodedVKCode >= (NSHIFTVAL + NALTVAL)) {
            nVKResult =  nEncodedVKCode - (NSHIFTVAL + NALTVAL);
            bHasShift.set(true);
            bHasAlt.set(true);
            bHasCtrl.set(false);
            return nVKResult;
        }
        if(nEncodedVKCode >= NALTVAL) {
            nVKResult =  nEncodedVKCode - NALTVAL;
            bHasAlt.set(true);
            bHasCtrl.set(false);
            bHasShift.set(false);
            return nVKResult;
        }
        if(nEncodedVKCode >= (NSHIFTVAL + NCTRLVAL)) {
            nVKResult =  nEncodedVKCode - (NSHIFTVAL + NCTRLVAL);
            bHasCtrl.set(true);
            bHasShift.set(true);
            bHasAlt.set(false);
            return nVKResult;
        }
        if(nEncodedVKCode >= NCTRLVAL) {
            nVKResult =  nEncodedVKCode - NCTRLVAL;
            bHasCtrl.set(true);
            bHasAlt.set(false);
            bHasShift.set(false);
            return nVKResult;
        }
        if(nEncodedVKCode >= NSHIFTVAL) {
            nVKResult =  nEncodedVKCode - NSHIFTVAL;
            bHasShift.set(true);
            bHasCtrl.set(false);
            bHasAlt.set(false);
            return nVKResult;
        }
        return nVKResult;
    }

    abstract public String getServerKeyText(int position);

    public String getPhysicalKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nDecodePhysicalKeyCode = decodeVKCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    new AtomicBoolean(false),
                    new AtomicBoolean(false),
                    new AtomicBoolean(false));
            //Todo:Map from Window VT_Code to Android Key Code
            result = String.format("VK code = %s", String.valueOf(nDecodePhysicalKeyCode));
        }
        return result;
    }

    public boolean hasShift(int position) {
        AtomicBoolean bHasShift = new AtomicBoolean(false);
        if(position < this.size()) {
            decodeVKCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    new AtomicBoolean(false),
                    bHasShift,
                    new AtomicBoolean(false));
        }
        return bHasShift.get();
    }

    public boolean hasCtrl(int position) {
        AtomicBoolean bHasCtrl = new AtomicBoolean(false);
        if(position < this.size()) {
            decodeVKCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    bHasCtrl,
                    new AtomicBoolean(false),
                    new AtomicBoolean(false));
        }
        return bHasCtrl.get();
    }

    public boolean hasAlt(int position) {
        AtomicBoolean bHasAlt = new AtomicBoolean(false);
        if(position < this.size()) {
            decodeVKCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    new AtomicBoolean(false),
                    new AtomicBoolean(false),
                    bHasAlt);
        }
        return bHasAlt.get();
    }
}
