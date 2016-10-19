package TelnetIBM;

import android.view.KeyEvent;

import com.te.UI.CipherUtility;

import java.util.concurrent.atomic.AtomicReference;

import Terminals.KeyMapList;

import static TelnetIBM.IBMHost3270.IBmOrder3270.*;
import static TelnetIBM.IBMHost3270.IBmStates.*;

public class IBMHost3270 extends IBMHostBase {
    enum IBmStates {
        IBMS_None,
        IBMS_Ground,
        IBMS_Command,
        IBMS_CommandEnd,
        IBMS_CommandEndEx,
        IBMS_Orders,
        IBMS_OrdersEx,
        IBMS_Anywhere,
    }

    enum IBmActions {
        IBMA_None,
        IBMA_ParseCommand,
        IBMA_ParseWCC,
        IBMA_RecordData,
        IBMA_ParseData,
        IBMA_OrdersRecord,
        IBMA_OrdersParse,
    }

    class IBMCommand3270 {
        public static final char IBMCm_Write = '\u0001';
        public static final char IBMCm_ReadBuffer = '\u0002';
        public static final char IBMCm_NonAction = '\u0003';
        public static final char IBMCm_Sense = '\u0004';
        public static final char IBMCm_EraseWrite = '\u0005';
        public static final char IBMCm_ReadMod = '\u0006';
        public static final char IBMCm_Select = '\u000b';
        public static final char IBMCm_WriteField = '\u0011';
    }

    class IBmOrder3270 {
        public static final char EraseUnprotectedToAddress = '\u0012' ;
        public static final char InsertCursor = '\u0013' ;
        public static final char ModifyField = '\u002c' ;
        public static final char ProgramTab = '\u0005' ;
        public static final char RepeatToAddress = '\u003c' ;
        public static final char SetAttribute = '\u0028' ;
        public static final char SetBufferAddress = '\u0011' ;
        public static final char StartField = '\u001d';
        public static final char StartFieldExtended = '\u0029';
    }

    private static class TN_StateChangeInfo {
        IBmStates mIbmState;
        Transitions mTransition;
        IBmActions mAction;
        TN_StateChangeInfo(IBmStates ibmState, Transitions tran, IBmActions act) {
            mIbmState = ibmState;
            mTransition = tran;
            mAction = act;
        }
    }

    private static class TN_StateEventInfo {
        public IBmStates mPreState;
        public char mCharFrom;
        public char mCharTo;
        public IBmActions mAction;
        public IBmStates mState; // the state we are going to

        TN_StateEventInfo(IBmStates preState, char charFrom, char charTo, IBmActions action, IBmStates state) {
            mPreState = preState;
            mCharFrom = charFrom;
            mCharTo = charTo;
            mAction = action;
            mState = state;
        }
    }

    //Tables
    final static private TN_StateChangeInfo[] gTN_StateChangeInfos = {
        new TN_StateChangeInfo(IBmStates.IBMS_Orders, Transitions.Exit, IBmActions.IBMA_OrdersParse),
        new TN_StateChangeInfo(IBmStates.IBMS_OrdersEx, Transitions.Exit, IBmActions.IBMA_OrdersParse),
        new TN_StateChangeInfo(IBmStates.IBMS_CommandEndEx, Transitions.Exit, IBmActions.IBMA_ParseData),
        new TN_StateChangeInfo(IBmStates.IBMS_CommandEnd, Transitions.Exit, IBmActions.IBMA_ParseData)
    };

    final static private TN_StateEventInfo[] gTN_StateEventInfos = {
        //	                   previous IBMState, CharFrom, CharTo, Action, Cur State
        new TN_StateEventInfo(IBmStates.IBMS_Ground, IBMCommand3270.IBMCm_Write, IBMCommand3270.IBMCm_Write, IBmActions.IBMA_ParseCommand, IBmStates.IBMS_Command),
        new TN_StateEventInfo(IBmStates.IBMS_Ground, IBMCommand3270.IBMCm_ReadBuffer, IBMCommand3270.IBMCm_ReadBuffer, IBmActions.IBMA_ParseCommand, IBmStates.IBMS_Command),
        new TN_StateEventInfo(IBmStates.IBMS_Ground, IBMCommand3270.IBMCm_EraseWrite, IBMCommand3270.IBMCm_EraseWrite, IBmActions.IBMA_ParseCommand,IBmStates.IBMS_Command),
        new TN_StateEventInfo(IBmStates.IBMS_Orders, '\u0000', '\u00FF', IBmActions.IBMA_RecordData, IBmStates.IBMS_CommandEndEx),
        new TN_StateEventInfo(IBmStates.IBMS_OrdersEx, '\u0000', '\u00FF', IBmActions.IBMA_RecordData, IBmStates.IBMS_CommandEndEx),
        new TN_StateEventInfo(IBmStates.IBMS_CommandEndEx, '\u0000', '\u00FF', IBmActions.IBMA_RecordData, IBmStates.IBMS_CommandEndEx),
        new TN_StateEventInfo(IBmStates.IBMS_Anywhere, '\u00EF', '\u00EF', IBmActions.IBMA_None, IBmStates.IBMS_Ground),
        new TN_StateEventInfo(IBmStates.IBMS_Anywhere, '\u00FF', '\u00FF', IBmActions.IBMA_None, IBmStates.IBMS_Ground),
    };

    //Members
    private char mCurChar;
    private char mCurCommand;
    private char CurOrder;
    private char mCurWcc;
    private IBmStates mPreIBMState = IBMS_Ground;
    private IBmActions mLastEventAction;
    private int mParsePanding = 0;

    private int checkPadding() {
        int value = mParsePanding;

        if (mParsePanding <= 0)
            mParsePanding = 0;
        else
            mParsePanding--;
        return value;
    }

    private void preSetOrder(char order) {
        switch(order) {
            case EraseUnprotectedToAddress:
                mParsePanding = 2;
                break;
            case InsertCursor:
                break;
            case ModifyField:
                mParsePanding = 3;
                break;
            case ProgramTab:
            case RepeatToAddress:
                mParsePanding = 3;
                break;
            case SetAttribute:
                mParsePanding = 2;
                break;
            case SetBufferAddress:
                mParsePanding = 2;
                break;
            case StartField:
                mParsePanding = 1;
                break;
            case StartFieldExtended:
                break;
        }
    }

    private boolean getStateChangeAction(IBmStates ibmState, Transitions transition, AtomicReference<IBmActions> changeAction) {
        for (int idxStateChgInfo = 0; idxStateChgInfo < gTN_StateChangeInfos.length; idxStateChgInfo++) {
            TN_StateChangeInfo stateChgInfo = gTN_StateChangeInfos[idxStateChgInfo];
            if (ibmState == stateChgInfo.mIbmState && transition == stateChgInfo.mTransition) {
                changeAction.set(stateChgInfo.mAction);
                return true;
            }
        }
        return false;
    }

    private boolean getStateEventAction(IBmStates preIBMState, char curChar, AtomicReference<IBmStates> curIBMState, AtomicReference<IBmActions> curAction) {
        if (preIBMState == IBMS_Command) {
            if (mCurCommand == IBMCommand3270.IBMCm_Write || mCurCommand == IBMCommand3270.IBMCm_EraseWrite) {
                curIBMState.set(IBmStates.IBMS_CommandEndEx);
                curAction.set(IBmActions.IBMA_ParseWCC);
                return true;
            }
        }

        if (preIBMState == IBMS_Command || preIBMState == IBMS_CommandEndEx ) {
            switch(curChar) {
                case EraseUnprotectedToAddress:
                case InsertCursor:
                case ModifyField:
                case ProgramTab:
                case RepeatToAddress:
                case SetAttribute:
                case SetBufferAddress:
                case StartField:
                case StartFieldExtended:
                    preSetOrder(curChar);
                    curIBMState.set(IBMS_Orders);
                    curAction.set(IBmActions.IBMA_OrdersRecord);
                    return true;
            }
        }

        if (preIBMState == IBMS_Orders || preIBMState == IBMS_OrdersEx) {
            switch(curChar) {
                case EraseUnprotectedToAddress:
                case InsertCursor:
                case ModifyField:
                case ProgramTab:
                case RepeatToAddress:
                case SetAttribute:
                case SetBufferAddress:
                case StartField:
                case StartFieldExtended:
                    preSetOrder(curChar);
                    if (preIBMState == IBMS_Orders) {
                        curIBMState.set(IBMS_OrdersEx);
                    } else {
                        curIBMState.set(IBMS_Orders);
                    }
                    curAction.set(IBmActions.IBMA_OrdersRecord);
                    return true;
            }
        }

        for(int idxStateEvntInfo = 0; idxStateEvntInfo < gTN_StateEventInfos.length; idxStateEvntInfo++) {
            TN_StateEventInfo stateEvntInfo = gTN_StateEventInfos[idxStateEvntInfo];
            if (curChar >= stateEvntInfo.mCharFrom &&
                    curChar <= stateEvntInfo.mCharTo &&
                    (stateEvntInfo.mPreState == preIBMState || stateEvntInfo.mPreState == IBMS_Anywhere)) {

                curIBMState.set(stateEvntInfo.mState);
                curAction.set(stateEvntInfo.mAction);
                return true;
            }
        }

        return false;
    }

    private void doAction(IBmActions action) {
        int r=0;
        switch(action) {
            case IBMA_ParseCommand:
                mCurCommand = mCurChar;
                //CommandDoAction();
                break;
            case IBMA_ParseWCC:
                mCurWcc = mCurChar;
                break;
            case IBMA_OrdersRecord:
                //OrderBuffer.push_back(mCurChar);
                break;
            case IBMA_RecordData:
                //DataBuffer.push_back(mCurChar);
                break;
            case IBMA_ParseData:
                //ParserFieldData();
               // DataBuffer.clear();
                break;
            case IBMA_OrdersParse:
                //ParseOrders();
                //OrderBuffer.clear();
                break;
            default:
                break;
        }
    }

    public IBMHost3270() {
        mPreIBMState = IBMS_Ground;
        mParsePanding = 0;
    }

    @Override
    public Point getCursorGridPos() {
        return null;
    }

    @Override
    protected int getServerKeyCode(int keyCode) {
        return 0;
    }

    @Override
    public String GetTerminalTypeName() {
        return null;
    }

    @Override
    public void handleKeyDown(int keyCode, KeyEvent event) {

    }

    @Override
    public void handleBarcodeFire(String Code) {

    }

    @Override
    public void OnScreenBufferPos(int x, int y) {

    }

    @Override
    public void drawAll() {

    }

    @Override
    public void ParseEnd() {

    }

    @Override
    public void processChar(char ch) {
        CipherUtility.Log_d("IBMHost3270", "IBMHost3270.processChar, char = [%02x]", (byte) ch);
        AtomicReference<IBmStates> curIBMState = new AtomicReference<>(IBmStates.IBMS_None);
        AtomicReference<IBmActions> curEventAction = new AtomicReference<>(IBmActions.IBMA_None);

        mCurChar = ch;
        if (checkPadding() > 0) {
            curIBMState.set(mPreIBMState);
            curEventAction.set(mLastEventAction);
        } else {
            getStateEventAction(mPreIBMState, mCurChar, curIBMState, curEventAction);
        }

        boolean bStateChanged = (curIBMState.get() != IBmStates.IBMS_None && curIBMState.get() != mPreIBMState);
        if (bStateChanged) {
            // check for state exit actions
            AtomicReference<IBmActions> stateExitAction = new AtomicReference<>(IBmActions.IBMA_None);
            getStateChangeAction(mPreIBMState, Transitions.Exit, stateExitAction);
            // Process the exit action
            if (stateExitAction.get() != IBmActions.IBMA_None) {
                doAction(stateExitAction.get());
            }
            CipherUtility.Log_d("IBMHost3270", "[Exit] mPreIBMState = %s, curIBMState = %s, state change exit Action = %s", mPreIBMState.toString(), curIBMState, stateExitAction);
        }

        if (curEventAction.get() != IBmActions.IBMA_None) {
            doAction(curEventAction.get());
            CipherUtility.Log_d("IBMHost3270", "[Event Action] do action = %s", curEventAction);
        }

        mLastEventAction = curEventAction.get();

        if (bStateChanged) {
            AtomicReference<IBmActions> stateEntryAction = new AtomicReference<>(IBmActions.IBMA_None);
            // check for state entry actions
            getStateChangeAction(curIBMState.get(), Transitions.Entry, stateEntryAction);

            // Process the entry action
            if (stateEntryAction.get() != IBmActions.IBMA_None) {
                doAction(stateEntryAction.get());
            }
            CipherUtility.Log_d("IBMHost3270", "[Entry] curIBMState = %s, state change entry Action = %s", curIBMState.toString(), stateEntryAction);
        }
        mPreIBMState = curIBMState.get();
    }

    @Override
    public void setKeyMapList(KeyMapList keyMapList) {

    }

    @Override
    protected boolean autoLogin() {
        return false;
    }
}
