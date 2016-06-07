package Terminals;

import android.view.KeyEvent;

import java.util.ArrayList;

public class MacroRecorder {
    private boolean mIsRecording;
    private java.util.ArrayList<MacroItem> mMacroList = new java.util.ArrayList<MacroItem>();

    public MacroRecorder() {
        reset();
    }

    public void reset() {
        mMacroList.clear();
    }

    public void addMacroKeyboard(KeyEvent event) {
        MacroItem item = new MacroItem();
        item.setInputType(0);
        item.setEvent(event);
        mMacroList.add(item);
    }

    public void addMacroBarcode(String code) {
        MacroItem item = new MacroItem();
        item.setInputType(1);
        item.setBarcodeData(code);
        mMacroList.add(item);
    }

    public ArrayList<MacroItem> getItemsList() {
        return mMacroList;
    }

    public void setRecord(boolean isRecord) {
        mIsRecording = isRecord;
        if(mIsRecording) {
            reset();
        }
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public boolean hasMacro() {
        if (mMacroList.size() > 0)
            return true;
        return false;
    }
}
