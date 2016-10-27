package Terminals;

import com.cipherlab.terminalemulation.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class KeyMapList extends ArrayList<KeyMapItem> {

    abstract public String getServerKeyText(int position);
    abstract public String getServerKeyTextByKeycode(int nServerKeycode);

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
