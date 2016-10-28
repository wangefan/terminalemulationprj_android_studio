package Terminals;


public abstract class KeyMapItem {
    public static final int UNDEFINE_PHY = -1;

    public abstract int getServerKeycode();
    public abstract int getPhysicalKeycode();

    public abstract void setServerKeycode(int nServerKeycode);
    public abstract void setPhysicalKeycode(int nPhysicalKeycode);

    public KeyMapItem() {
    }

    public static KeyMapItem createItem(Integer serverKeycode, Integer phyKeyCode, boolean bIsTN) {
        if(bIsTN) {
            return new KeyMapItemTN(serverKeycode, phyKeyCode);
        } else {
            return new KeyMapItemVT(serverKeycode, phyKeyCode);
        }
    }
}
