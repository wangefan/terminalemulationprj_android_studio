package Terminals;

import TelnetVT.CVT100;

public class VT100_102KeyMapList extends KeyMapList {
    public VT100_102KeyMapList() {
        super();
    }

    @Override
    public String getServerKeyTextByKeycode(int nServerKeycode) {
        return CVT100.getServerKeyText(nServerKeycode);
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
}
