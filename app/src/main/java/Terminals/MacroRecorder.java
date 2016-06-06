package Terminals;

import android.view.KeyEvent;

import java.util.ArrayList;

public class MacroRecorder {
    private boolean mRec;
    private java.util.ArrayList<Macroitem> MacroList = new java.util.ArrayList<Macroitem>();

    public MacroRecorder() {
        Reset();
    }

    public void Reset() {
        MacroList.clear();
    }

    public void AddMacroKeyboard(int code, KeyEvent event) {
        if (!GetRecord())
            return;
        Macroitem item = new Macroitem();
        item.SetInputType(0);
        item.SetEvent(event);
        item.SetKeyCode(code);
        MacroList.add(item);
    }

    public void AddMacroBarcode(String code) {
        if (!GetRecord())
            return;

        Macroitem item = new Macroitem();
        //
        item.SetInputType(1);
        item.SetBarcodeData(code);
        MacroList.add(item);
    }

    public ArrayList<Macroitem> GetItemsList() {
        return MacroList;
    }

    public void SetRecord(boolean isRecord) {
        mRec = isRecord;
    }

    public boolean GetRecord() {
        return mRec;
    }

    public boolean ShowColorRecordIcon() {
        return (!mRec);
    }

    public boolean ShowColorPlayIcon() {
        if (MacroList.size() > 0)
            return true;
        return false;
    }

    public boolean ShowColorStopIcon() {
        return mRec;
    }
}
