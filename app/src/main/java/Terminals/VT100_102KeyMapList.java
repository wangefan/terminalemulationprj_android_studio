package Terminals;

import TelnetVT.CVT100;

public class VT100_102KeyMapList extends KeyMapList {
    public VT100_102KeyMapList() {
        super();
    }

    @Override
    public String getServerKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nServerKeyCode = get(position).mServerKeycode;
            result = CVT100.getServerKeyText(nServerKeyCode);
        }
        return result;
    }
}
