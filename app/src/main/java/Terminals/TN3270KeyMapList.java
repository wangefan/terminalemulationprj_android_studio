package Terminals;


import com.cipherlab.terminalemulation.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import TelnetIBM.IBMHost5250;

public class TN3270KeyMapList extends ArrayList<KeyMapItemTN> implements KeyMapList {

    public TN3270KeyMapList() {
        super();
    }

    @Override
    public String getServerKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nServerKeyCode = get(position).mServerKeycode;
            result = getServerKeyTextByKeycode(nServerKeyCode);
        }
        return result;
    }

    @Override
    public String getServerKeyTextByKeycode(int nServerKeycode) {
        //Todo: use 3270
        return IBMHost5250.getServerKeyText(nServerKeycode);
    }

    @Override
    public String getPhysicalKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nDecodePhysicalKeyCode = KeyMapUtility.decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    new AtomicBoolean(false),
                    new AtomicBoolean(false),
                    new AtomicBoolean(false));
            if(nDecodePhysicalKeyCode == KeyMapItem.UNDEFINE_PHY) {
                result = stdActivityRef.getCurrActivity().getString(R.string.undefinedPhy);
            } else {
                result = KeyMapUtility.getPhyKeycodeTextByKeycode(nDecodePhysicalKeyCode);
            }
        }
        return result;
    }

    @Override
    public void clearList() {
        clear();
    }

    @Override
    public int listSize() {
        return size();
    }

    @Override
    public KeyMapItem getItem(int pos) {
        return get(pos);
    }

    @Override
    public void addItem(KeyMapItem keyItem) {
        add((KeyMapItemTN) keyItem);
    }

    @Override
    public long indexOfList(Object item) {
        return indexOf(item);
    }

    @Override
    public boolean hasShift(int position) {
        AtomicBoolean bHasShift = new AtomicBoolean(false);
        if(position < this.size()) {
            KeyMapUtility.decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    null,
                    bHasShift,
                    null);
        }
        return bHasShift.get();
    }

    @Override
    public boolean hasCtrl(int position) {
        AtomicBoolean bHasCtrl = new AtomicBoolean(false);
        if(position < this.size()) {
            KeyMapUtility.decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    bHasCtrl,
                    null,
                    null);
        }
        return bHasCtrl.get();
    }

    @Override
    public boolean hasAlt(int position) {
        AtomicBoolean bHasAlt = new AtomicBoolean(false);
        if(position < this.size()) {
            KeyMapUtility.decodePhyCodeRetunHelpKey(get(position).mPhysicalKeycode,
                    null,
                    null,
                    bHasAlt);
        }
        return bHasAlt.get();
    }
}
