package SessionProcess;


import android.content.Context;
import android.view.KeyEvent;

import com.example.terminalemulation.R;

import java.util.ArrayList;

import TelnetIBM.IBMHost5250;
import TelnetVT.CVT100;
import Terminals.TESettingsInfo;
import Terminals.MacroMgr;
import Terminals.MacroItem;
import Terminals.TerminalBase;
import Terminals.TerminalBaseEnum;
import Terminals.stdActivityRef;

/**
 * Created by Franco.Liu on 2014/2/26.
 */
public class TerminalProcess {
    private MacroMgr mMacroRec = new MacroMgr();
    private TerminalBase mTerminal;
    private OnTerminalProcessListener mListener = null;

    public TerminalProcess() {
    }

    public void handleKeyDown(int keyCode, KeyEvent event) {
        if(mMacroRec.isRecording()) {
            mMacroRec.addMacroKeyboard(event);
        }
        mTerminal.handleKeyDown(keyCode, event);
    }

    public void handleScreenTouch(int x, int y) {
        mTerminal.OnScreenBufferPos(x, y);
    }

    public void setListener(OnTerminalProcessListener listener) {
        mListener = listener;
    }

    public void playMacro() {
        ArrayList<?> Macroitem = mMacroRec.getItemsList();
        for (int i = 0; i < Macroitem.size(); i++) {
            MacroItem item = (MacroItem) Macroitem.get(i);
            if (item.getInputType() == 1) {
                playMacroBarcode(item.getBarcodeData());
            } else {
                KeyEvent event = item.getKeyEvent();
                if(event != null) {
                    mTerminal.handleKeyDown(event.getKeyCode(), event);
                }
            }
        }
    }

    public void recMacro(boolean bRec) {
        mMacroRec.setRecord(bRec);
    }

    public boolean isMacroRecording() {
        return mMacroRec.isRecording();
    }

    public boolean hasMacro() {
        return mMacroRec.hasMacro();
    }

    public void processReadBarcode(String Data) {
        if (mTerminal != null) {
            if (mMacroRec.isRecording()) {
                mMacroRec.addMacroBarcode(Data);
            }
            mTerminal.handleBarcodeFire(Data);
        }
    }

    public void playMacroBarcode(String Data) {
        if (mTerminal != null) {
            mTerminal.handleBarcodeFire(Data);
        }
    }

    public boolean ProcessConnect() {

        String Ip = TESettingsInfo.getHostAddrByIndex(TESettingsInfo.getSessionIndex());
        String Port = TESettingsInfo.getHostPortByIndex(TESettingsInfo.getSessionIndex());
        Boolean SSh = TESettingsInfo.getHostIsSshEnableByIndex(TESettingsInfo.getSessionIndex());
        boolean isTN = TESettingsInfo.getIsHostTNByIndex(TESettingsInfo.getSessionIndex());

        Context context = stdActivityRef.GetCurrActivity().getApplicationContext();
        if (isTN == false) {
            String serverTypeName = TESettingsInfo.getHostTypeNameByIndex(TESettingsInfo.getSessionIndex());
            assert (serverTypeName.equals(context.getResources().getString(R.string.VT100Val)) ||
                    serverTypeName.equals(context.getResources().getString(R.string.VT102Val)) ||
                    serverTypeName.equals(context.getResources().getString(R.string.VT220Val)) ||
                    serverTypeName.equals(context.getResources().getString(R.string.ANSIVal)));
            mTerminal = new CVT100();
        } else {
            String serverTypeName = TESettingsInfo.getTNHostTypeNameByIndex(TESettingsInfo.getSessionIndex());
            if (serverTypeName.compareToIgnoreCase(context.getResources().getString(R.string.IBM5250Val)) == 0)
                mTerminal = new IBMHost5250();
        }

        if(mListener != null) {
            mListener.onNotify(TerminalBase.NOTF_ACT_UPDATE_GRID);
        }

        mTerminal.setIP(Ip);
        mTerminal.setPort(Port);
        mTerminal.setSsh(SSh);
        mTerminal.setOnTerminalListener(new TerminalBase.OnTerminalListener() {
            @Override
            public void onConnected() {
                if (mListener != null)
                    mListener.onConnected();
            }

            @Override
            public void onDisconnected() {
                if (mListener != null)
                    mListener.onDisConnected();
            }

            @Override
            public void onNotify(String action, Object... params) {
                if (mListener != null)
                    mListener.onNotify(action, params);
            }
        });

        return mTerminal.TelnetsStart();
    }

    public void ProcessDisConnect() {
        if (mTerminal != null)
            mTerminal.TelnetDisconnect();
    }

    public boolean isConnected() {
        if (mTerminal != null)
            return mTerminal.isConnected();
        return false;
    }

    public int getCols() {
        if(mTerminal != null)
            return mTerminal._cols;
        return 0;
    }

    public int getRows() {
        if(mTerminal != null)
            return mTerminal._rows;
        return 0;
    }

    public TerminalBaseEnum.Point getCursorGridPos() {
        if(mTerminal != null)
            return mTerminal.getCursorGridPos();
        return new TerminalBaseEnum.Point(0,0);
    }

    public void drawAll() {
        if(mTerminal != null)
            mTerminal.drawAll();
    }

    public interface OnTerminalProcessListener {
        void onConnected();

        void onDisConnected();

        void onNotify(String action, Object... params);
    }
}
