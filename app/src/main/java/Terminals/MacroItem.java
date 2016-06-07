package Terminals;

import android.view.KeyEvent;

public class MacroItem {

    private String mBarcodeData = "";
    private KeyEvent mKeyEvent = null;
    private int mKeyCode = -1;
    private int mInputType = 0;// 0:key 1:bar

    public MacroItem() {
    }

    public int getInputType() {
        return mInputType;
    }

    public void setInputType(int type) {
        mInputType = type;
    }

    public String getBarcodeData() {
        return mBarcodeData;
    }

    public void setBarcodeData(String Data) {
        mBarcodeData = Data;
    }


    public KeyEvent getKeyEvent() {
        return mKeyEvent;
    }

    public void setEvent(KeyEvent evt) {
        mKeyEvent = evt;
    }

    public int getKeyCode() {
        return mKeyCode;
    }

    public void setKeyCode(int key) {
        mKeyCode = key;
    }
}
