package Terminals;


import TelnetVT.CVT100;

public class VT220KeyMapList extends KeyMapList {

    private VT220KeyMapList() {
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
