package Terminals;


import TelnetIBM.IBMHost5250;

public class TN3270KeyMapList extends KeyMapList {

    TN3270KeyMapList() {
        super();
    }

    @Override
    public String getServerKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nServerKeyCode = get(position).mServerKeycode;
            //Todo: use 3270
            result = IBMHost5250.getServerKeyText(nServerKeyCode);
        }
        return result;
    }
}
