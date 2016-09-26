package Terminals;


import TelnetIBM.IBMHost5250;

public class TN3270KeyMapList extends KeyMapList {

    public TN3270KeyMapList() {
        super();
    }

    @Override
    public String getServerKeyTextByKeycode(int nServerKeycode) {
        //Todo: use 3270
        return IBMHost5250.getServerKeyText(nServerKeycode);
    }

    @Override
    public String getServerKeyText(int position) {
        String result = "";
        if(position < this.size()) {
            int nServerKeyCode = get(position).mServerKeycode;
            //Todo: use 3270
            result = getServerKeyTextByKeycode(nServerKeyCode);
        }
        return result;
    }
}
