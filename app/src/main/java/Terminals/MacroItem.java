package Terminals;

import android.view.KeyEvent;

import com.google.gson.annotations.SerializedName;

public class MacroItem {

    @SerializedName("BarcodeData")
    private String mBarcodeData = "";

    @SerializedName("InputType")
    private int mInputType = 0; // 0:key 1:bar

    @SerializedName("macroKeyCode")
    private int mMacroKeyCode = 0;

    @SerializedName("macroActCode")
    private int mMacroActCode = 0; //

    private KeyEvent mKeyEvent = null;

    public MacroItem() {
    }

    public void syncFromDeSerialize() {
        if(mInputType == 0) {
            mKeyEvent = new KeyEvent(mMacroActCode, mMacroKeyCode);
        }
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
        mMacroActCode = evt.getAction();
        mMacroKeyCode = evt.getKeyCode();
    }
}
