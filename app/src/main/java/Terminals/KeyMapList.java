package Terminals;

public interface KeyMapList {
    String getServerKeyText(int position);
    String getServerKeyTextByKeycode(int nServerKeycode);
    String getPhysicalKeyText(int position);
    void clearList();
    int listSize();
    KeyMapItem getItem(int pos);
    void addItem(KeyMapItem keyItem);
    long indexOfList(Object item);
    boolean hasShift(int position);
    boolean hasCtrl(int position);
    boolean hasAlt(int position);
    KeyMapItem createItem(Integer serverKeycode, Integer phyKeyCode);
}
