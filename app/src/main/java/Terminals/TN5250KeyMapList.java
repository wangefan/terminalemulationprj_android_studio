package Terminals;


import TelnetIBM.IBMHost5250;

public class TN5250KeyMapList extends KeyMapList {

    public TN5250KeyMapList() {
        super();
    }

    @Override
    public String getServerKeyTextByKeycode(int nServerKeycode) {
        return IBMHost5250.getServerKeyText(nServerKeycode);
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
