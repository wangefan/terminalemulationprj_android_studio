package Terminals;

import android.view.KeyEvent;

import java.util.ArrayList;

public class MacroRecorder {
    private boolean mRec;
    private java.util.ArrayList<Macroitem> MacroList = new java.util.ArrayList<Macroitem>();

    public MacroRecorder() {
        reset();
    }

    public void reset() {
        MacroList.clear();
    }

    public void addMacroKeyboard(int code, KeyEvent event) {
        Macroitem item = new Macroitem();
        item.SetInputType(0);
        item.SetEvent(event);
        item.SetKeyCode(code);
        MacroList.add(item);
    }

    public void addMacroBarcode(String code) {
        Macroitem item = new Macroitem();
        item.SetInputType(1);
        item.SetBarcodeData(code);
        MacroList.add(item);
    }

    public ArrayList<Macroitem> getItemsList() {
        return MacroList;
    }

    public void setRecord(boolean isRecord) {
        mRec = isRecord;
    }

    public boolean isRecording() {
        return mRec;
    }

    public boolean showColorRecordIcon() {
        return (!mRec);
    }

    public boolean showColorPlayIcon() {
        if (MacroList.size() > 0)
            return true;
        return false;
    }

    public boolean showColorStopIcon() {
        return mRec;
    }
}
