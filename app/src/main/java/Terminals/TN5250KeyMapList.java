package Terminals;


import TelnetIBM.IBMHost5250;

public class TN5250KeyMapList extends KeyMapList {

    private TN5250KeyMapList() {
        super();
    }

    @Override
    public String getServerKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nServerKeyCode = get(position).mServerKeycode;
            result = IBMHost5250.getServerKeyText(nServerKeyCode);
        }
        return result;
    }
}
