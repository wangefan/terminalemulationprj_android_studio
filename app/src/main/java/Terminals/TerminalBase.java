package Terminals;

import android.util.Log;
import android.view.KeyEvent;

import com.cipherlab.barcode.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import SessionProcess.TelnetConnMgr;
import SessionProcess.TelnetSshConnMgr;

/**
 * Created by Franco.Liu on 2014/1/9.
 */
public abstract class TerminalBase extends TerminalBaseEnum {
    public final static String NOTF_ACT_DRAWCHARLIVE = "NOTF_ACT_DRAWCHARLIVE";
    public final static String NOTF_ACT_INVALIDATE ="NOTF_ACT_INVALIDATE";
    public final static String NOTF_ACT_CLEAR_VIEW ="NOTF_ACT_CLEAR_VIEW";
    public final static String NOTF_ACT_UPDATE_GRID ="NOTF_ACT_UPDATE_GRID";
    public final static String NOTF_ACT_DRAW_SPACE ="NOTF_ACT_DRAW_SPACE";
    public final static String NOTF_ACT_DRAW_FIELD_CHAR = "NOTF_ACT_DRAW_FIELD_CHAR";
    public TerminalLogWriter LogFile;
    public char[][] CharGrid = null;
    public char[][] AttribGrid = null;
    public int _cols;
    public int _rows;
    protected OnTerminalListener mTerminalListener;
    protected String mIp = "";
    protected String mPort = "";
    protected boolean mSsh = false;
    private boolean mBAutoLoginProcessed = false;
    private TelnetConnMgr mTelConn;
    private TelnetParser mTelnetParser = null;
    public TerminalBase() {
        this.mTelnetParser = new TelnetParser();
        mTerminalListener = null;
    }

    protected void outputHexForDBG(char[] charArray) {
        if (BuildConfig.DEBUG) {
            String strHex = "";
            for (char C : charArray) {
                strHex += String.format("[%02x]", (byte) C);
            }
            Log.d("TE:", strHex);
        }
    }

    public void setOnTerminalListener(OnTerminalListener onTerminalListener) {
        mTerminalListener = onTerminalListener;
    }

    public void setIP(String ip) {
        mIp = ip;
    }

    public void setPort(String port) {
        mPort = port;
    }

    public void setSsh(Boolean ssh) {
        mSsh = ssh;
    }

    public Boolean IsWriteToLogFile() {
        Boolean IsLog = CipherConnectSettingInfo.getHostIsWriteLogkByIndex(CipherConnectSettingInfo.GetSessionIndex());

        if (!IsLog)
            return false;

        if (LogFile == null)
            return false;

        return true;
    }

    public boolean TelnetsStart() {
        if (mSsh)
            mTelConn = new TelnetSshConnMgr(mIp, Integer.valueOf(mPort), this);
        else
            mTelConn = new TelnetConnMgr(mIp, Integer.valueOf(mPort), this);
        return mTelConn.TelnetsStart();
    }

    public void TelnetDisconnect() {
        mTelConn.TelnetDisconnect();
    }

    public boolean isConnected() {
        return mTelConn.isConnected();
    }

    public void OnConnected() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());


        Boolean IsLog = CipherConnectSettingInfo.getHostIsWriteLogkByIndex(CipherConnectSettingInfo.GetSessionIndex());
        if (IsLog)
            LogFile = new TerminalLogWriter(formattedDate + ".txt");

        byte[] SendData = CipherConnectSettingInfo.GetHostSendToHostByIndex(CipherConnectSettingInfo.GetSessionIndex());

        if (SendData != null) {
            DispatchMessageRaw(this, SendData, SendData.length);
        }
        if (mTerminalListener != null) {
            mTerminalListener.onConnected();
        }
    }

    public void OnDisconnected() {
        if (mTerminalListener != null) {
            mTerminalListener.onDisconnected();
        }
    }

    abstract public Point getCursorGridPos();

    abstract public String GetTerminalTypeName();

    public void OnKeyDownFire(int keyCode, KeyEvent event) {

    }

    public void OnBarcodeFire(String Code) {

    }

    public void OnScreenBufferPos(int x, int y) {


    }

    abstract public void drawAll();

    public char GetCharFromCurrentIndex(int index) {
        return mTelnetParser.GetCharFromCurrent(index);
    }

    public Character TryGetMultiChar() {
        return mTelnetParser.TryGetMultiChar();
    }

    public void DrawCharLive(Character c, Integer x, Integer y, Boolean IsBold, Boolean IsUnderLine) {
        if (mTerminalListener != null)
            mTerminalListener.onNotify(NOTF_ACT_DRAWCHARLIVE, c, x, y, IsBold, IsUnderLine);
    }

    public void ViewPostInvalidate() {
        if (mTerminalListener != null)
            mTerminalListener.onNotify(NOTF_ACT_INVALIDATE);
    }

    public void ViewClear() {
        if (mTerminalListener != null)
            mTerminalListener.onNotify(NOTF_ACT_CLEAR_VIEW);
    }

    public void ViewDrawSpace(Integer x, Integer y, Integer space) {
        if (mTerminalListener != null)
            mTerminalListener.onNotify(NOTF_ACT_DRAW_SPACE, x, y, space);
    }

    public void DrawFieldChar(Character c, Integer x, Integer y, Boolean IsBold, Boolean IsUnderLine) {
        if (mTerminalListener != null)
            mTerminalListener.onNotify(NOTF_ACT_DRAW_FIELD_CHAR, c, x, y, IsBold, IsUnderLine);
    }

    public String GetLogTitle() {
        return "";
    }

    public void handleBufferReceived(byte[] data, int offset, int lenth) {
        char[] charArr = new char[lenth];

        for (int i = 0; i < lenth; i++) {
            //charArr[i]=(char)data[1];
            charArr[i] = ((char) (int) data[i]);
            charArr[i] &= 255;
        }

        if (IsWriteToLogFile())
            LogFile.Write(this.GetLogTitle(), data, lenth, true);

        mTelnetParser.ParseData(charArr);
        ParseEnd();
        if(CipherConnectSettingInfo.getHostIsAutoconnectByIndex(CipherConnectSettingInfo.GetSessionIndex()) == true &&
                CipherConnectSettingInfo.getHostIsAutoSignByIndex(CipherConnectSettingInfo.GetSessionIndex()) == true &&
                mBAutoLoginProcessed == false) {
            if(autoLogin())
                mBAutoLoginProcessed = true;
        }

        ViewPostInvalidate();
    }
    //region  TelnetInt Parser Entry

    //endregion
    //region  TelnetInterpreter
    public void ParseEnd() {
        // Boolean ChkisUserprompt=false;
        //Boolean ChkisPwdprompt=false;


    }

    public abstract void processChar(char ch);

    protected abstract boolean autoLogin();

    private void terminalInterpret(Object Sender, ParserEventArgs parserArgs) {
        switch (parserArgs.Action) {
            case SendUp:
                processChar(parserArgs.CurChar);
                break;
            case Execute:

                break;
            default:
                break;
        }

        if (parserArgs.CurSequence.startsWith("\u00FD")) {
            char CurCmd = (char) (parserArgs.CurSequence.substring(1, 2).charAt(0));

            switch (CurCmd) {
                // 24 - terminal type
                case '\u0018':
                    NvtSendWill(CurCmd);
                    break;
                case '\u0019':
                    NvtSendWill(CurCmd);
                    break;
                case 39:
                    NvtSendWill(CurCmd);
                    break;
                case '\u0000':
                    NvtSendWill(CurCmd);
                    break;
                default:
                    NvtSendWont(CurCmd);

                    break;
            }
        }

        // if the sequence is a WILL message
        if (parserArgs.CurSequence.startsWith("\u00FB")) {
            char CurCmd = (char) (parserArgs.CurSequence.substring(1, 2).charAt(0));
            //System.Char CurCmd = System.Convert.ToChar(e.CurSequence.Substring(1, 1));

            switch (CurCmd) {
                case '\u0001':
                    NvtSendDo(CurCmd);
                    break;

                default:

                    NvtSendDo(CurCmd);
                    //NvtSendDont(CurCmd);
                    //System.Console.Write ("unsupported telnet WILL sequence {0} happened\n",
                    //System.Convert.ToInt32 (System.Convert.ToChar (e.CurSequence.Substring (1,1))));
                    break;
            }
        }

        // if the sequence is a SUBNEGOTIATE message
        if (parserArgs.CurSequence.startsWith("\u00FA")) {
            if (parserArgs.CurSequence.charAt(2) != '\u0001') {
                // not interested in data from host just yet as we don't ask for it at the moment
                return;
            }

            char CurCmd = (char) (parserArgs.CurSequence.substring(1, 2).charAt(0));


            switch (CurCmd) {
                // 24 - terminal type
                case '\u0018':
                    //NvtSendSubNeg(CurCmd, "vt100");
                    //NvtSendSubNeg(CurCmd, "IBM-5292-2");
                    NvtSendSubNeg(CurCmd, GetTerminalTypeName());
                    break;
                case 39:

                    //NvtSendSubNeg(CurCmd, BuildUserVar("IBMRSEED", "") );
                    break;
                default:
                    NvtSendSubNeg(CurCmd, "");
                    //System.Console.Write("unsupported telnet SUBNEG sequence {0} happened\n",
                    //System.Convert.ToInt32(System.Convert.ToChar(e.CurSequence.Substring(1, 1))));
            }
        }


    }

    //endregion
    //region  Telnet Send Massage
    private void NvtSendWill(char CurChar) {
        DispatchMessage(this, "\u00FF\u00FB" + (Character.valueOf(CurChar)));
    }

    private void NvtSendWont(char CurChar) {
        DispatchMessage(this, "\u00FF\u00FC" + (Character.valueOf(CurChar)));
    }

    private void NvtSendDont(char CurChar) {
        DispatchMessage(this, "\u00FF\u00FE" + (Character.valueOf(CurChar)));
    }

    private void NvtSendDo(char CurChar) {
        DispatchMessage(this, "\u00FF\u00FD" + (Character.valueOf(CurChar)));
    }

    private void NvtSendSubNeg(char CurChar, String CurString) {
        DispatchMessage(this, "\u00FF\u00FA" + Character.valueOf(CurChar) + "\u0000" + CurString + "\u00FF\u00F0");
    }

    public void DispatchMessageRaw(Object sender, byte[] Data, int lenth) {
        if (IsWriteToLogFile())
            LogFile.Write(this.GetLogTitle(), Data, lenth, false);
        mTelConn.Send(Data);
    }

    public void DispatchMessage(Object sender, String strText) {
        byte[] bytes = new byte[strText.length()];
        for (int i = 0; i < strText.length(); i++) {
            bytes[i] = (byte) strText.charAt(i);
        }

        if (IsWriteToLogFile())
            LogFile.Write(this.GetLogTitle(), bytes, strText.length(), false);
        mTelConn.Send(bytes);
    }

    public interface OnTerminalListener {
        void onConnected();
        void onDisconnected();
        void onNotify(String action, Object ... params);
    }

    protected class TelnetParser {
        //region members
        ucStates mLastState = ucStates.Ground;
        char mCurChar = '\0';
        String mCurSequence = "";
        uc_CharEvents mTerParserCharEvents = new uc_CharEvents();
        uc_StateChangeEvents StateChangeEvents = new uc_StateChangeEvents();
        uc_Params CurParams = new uc_Params();

        int CurIndex = 0;
        char[] CurDataArray;

        public TelnetParser() {

        }

        public final void ParseData(char[] charArray) {
            AtomicReference<ucStates> curState = new AtomicReference<ucStates>(ucStates.None);
            AtomicReference<TntActions> curAction = new AtomicReference<TntActions>(TntActions.None);
            AtomicReference<TntActions> stateExitAction = new AtomicReference<TntActions>(TntActions.None);
            AtomicReference<TntActions> stateEntryAction = new AtomicReference<TntActions>(TntActions.None);

            CurDataArray = charArray;
            outputHexForDBG(CurDataArray);

            //char[] charArray = InString.toCharArray();
            for (CurIndex = 0; CurIndex < charArray.length; CurIndex++) {
                mCurChar = charArray[CurIndex];

                // Get the next state and associated action based
                // on the current state and char event
                mTerParserCharEvents.GetStateEventAction(mLastState, mCurChar, curState, curAction);
                // execute any actions arising from leaving the current state
                if (curState.get() != ucStates.None && curState.get() != mLastState) {
                    // check for state exit actions
                    StateChangeEvents.GetStateChangeAction(mLastState, Transitions.Exit, stateExitAction);
                    // Process the exit action
                    if (stateExitAction.get() != TntActions.None) {
                        DoAction(stateExitAction.get());
                    }
                }

                // process the action specified
                if (curAction.get() != TntActions.None) {
                    DoAction(curAction.get());
                }

                // set the new parser state and execute any actions arising entering the new state
                if (curState.get() != ucStates.None && curState.get() != mLastState) {
                    // change the parsers state attribute
                    mLastState = curState.get();

                    // check for state entry actions
                    StateChangeEvents.GetStateChangeAction(mLastState, Transitions.Entry, stateEntryAction);

                    // Process the entry action
                    if (stateEntryAction.get() != TntActions.None) {
                        DoAction(stateEntryAction.get());
                    }
                }
            }
        }

        public char GetCharFromCurrent(int index) {
            try {

                if ((this.CurIndex + index) >= CurDataArray.length)
                    return (char) 0;


                return CurDataArray[this.CurIndex + index];
            } catch (Exception exc) {
                String Msg = exc.getMessage();
                return (char) 0;
            }
        }

        public Character TryGetMultiChar() {
            byte CurChar = (byte) (CurDataArray[this.CurIndex] & 0xff);
            String str = null;

            int nCharSet = CipherConnectSettingInfo.getHostCharSetByIndex(CipherConnectSettingInfo.GetSessionIndex());

            if (nCharSet == 0)
                return null;
            int len = 0;

            for (int i = 0; i < 6; i++) {
                if (GetBit(CurChar, i) == false)
                    break;


                len++;

            }

            if (len > 0) {
                byte UtfBtye[] = new byte[len];

                for (int i = 0; i < len; i++) {
                    UtfBtye[i] = (byte) (CurDataArray[this.CurIndex + i] & 0xff);
                }

                try {
                    str = new String(UtfBtye, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    return null;
                }

            } else
                return null;

            CurIndex += (len - 1);
            return Character.valueOf(str.charAt(0));
        }

        public Boolean GetBit(byte Data, int position) {
            return ((Data << position) & 0x80) > 0;
        }

        private void DoAction(TntActions NextAction) {
            //  uc_TelnetParser DoAction
            switch (NextAction) {
                case Dispatch:
                case Collect:
                    mCurSequence += String.valueOf(mCurChar);
                    break;

                case NewCollect:
                    mCurSequence = String.valueOf(mCurChar);
                    this.CurParams.Clear();
                    break;

                case Param:
                    this.CurParams.Add(mCurChar);
                    break;

                default:
                    break;
            }


            switch (NextAction) {
                case Dispatch:
                    //NvtParserEventArgs Args=new NvtParserEventArgs();
                    terminalInterpret(this, new ParserEventArgs(NextAction, mCurChar, mCurSequence, CurParams));
                    break;

                case Execute:
                case SendUp:
                    terminalInterpret(this, new ParserEventArgs(NextAction, mCurChar, mCurSequence, CurParams));
                    break;
                default:
                    break;
            }


            switch (NextAction) {
                case Dispatch:
                    mCurSequence = "";
                    this.CurParams.Clear();
                    break;

                default:
                    break;
            }
        }
        //endregion
        //region classs

        private final class uc_StateChangeInfo {
            // tel net
            public ucStates State;
            public Transitions Transition; // the next state we are going to
            public TntActions NextAction;

            public uc_StateChangeInfo(ucStates p1, Transitions p2, TntActions p3) {
                this.State = p1;
                this.Transition = p2;
                this.NextAction = p3;
            }
        }

        private final class uc_CharEventInfo {

            //in telnet
            public ucStates CurState;
            public char CharFrom;
            public char CharTo;
            public TntActions NextAction;
            public ucStates NextState; // the next state we are going to

            public uc_CharEventInfo(ucStates p1, char p2, char p3, TntActions p4, ucStates p5) {
                this.CurState = p1;
                this.CharFrom = p2;
                this.CharTo = p3;
                this.NextAction = p4;
                this.NextState = p5;
            }
        }

        private class uc_StateChangeEvents {
            private uc_StateChangeInfo[] Elements = {new uc_StateChangeInfo(ucStates.None, Transitions.None, TntActions.None)};

            public uc_StateChangeEvents() {
            }

            public final boolean GetStateChangeAction(ucStates State, Transitions Transition, AtomicReference<TntActions> nextAction) {
                //  this is telnet
                for (int i = 0; i < Elements.length; i++) {
                    uc_StateChangeInfo Element = Elements[i];

                    if (State == Element.State && Transition == Element.Transition) {
                        nextAction.set(Element.NextAction);
                        return true;
                    }
                }

                return false;
            }
        }

        private class uc_CharEvents {
            public uc_CharEventInfo[] Elements = {
                    //CurState ,             CharFrom,    CharTo,    NextAction,            NextState
                    new uc_CharEventInfo(ucStates.Ground, (char) 000, (char) 254, TntActions.SendUp, ucStates.None),
                    new uc_CharEventInfo(ucStates.Ground, (char) 255, (char) 255, TntActions.None, ucStates.Command),
                    new uc_CharEventInfo(ucStates.Command, (char) 000, (char) 239, TntActions.SendUp, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.Command, (char) 240, (char) 241, TntActions.None, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.Command, (char) 242, (char) 249, TntActions.Execute, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.Command, (char) 250, (char) 250, TntActions.NewCollect, ucStates.SubNegotiate),
                    new uc_CharEventInfo(ucStates.Command, (char) 251, (char) 254, TntActions.NewCollect, ucStates.Negotiate),
                    new uc_CharEventInfo(ucStates.Command, (char) 255, (char) 255, TntActions.SendUp, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.SubNegotiate, (char) 000, (char) 255, TntActions.Collect, ucStates.SubNegValue),
                    new uc_CharEventInfo(ucStates.SubNegValue, (char) 000, (char) 001, TntActions.Collect, ucStates.SubNegParam),
                    new uc_CharEventInfo(ucStates.SubNegValue, (char) 002, (char) 255, TntActions.None, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.SubNegParam, (char) 000, (char) 254, TntActions.Param, ucStates.None),
                    new uc_CharEventInfo(ucStates.SubNegParam, (char) 255, (char) 255, TntActions.None, ucStates.SubNegEnd),
                    new uc_CharEventInfo(ucStates.SubNegEnd, (char) 000, (char) 239, TntActions.None, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.SubNegEnd, (char) 240, (char) 240, TntActions.Dispatch, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.SubNegEnd, (char) 241, (char) 254, TntActions.None, ucStates.Ground),
                    new uc_CharEventInfo(ucStates.SubNegEnd, (char) 255, (char) 255, TntActions.Param, ucStates.SubNegParam),
                    new uc_CharEventInfo(ucStates.Negotiate, (char) 000, (char) 255, TntActions.Dispatch, ucStates.Ground),
            };

            public uc_CharEvents() {
            }

            // in Telnet
            public final boolean GetStateEventAction(ucStates CurState, char CurChar, AtomicReference<ucStates> nextStateRef, AtomicReference<TntActions> nextAction) {
                uc_CharEventInfo Element;


                for (int i = 0; i < Elements.length; i++) {
                    Element = Elements[i];

                    if (CurChar >= Element.CharFrom && CurChar <= Element.CharTo && (CurState == Element.CurState || Element.CurState == ucStates.Anywhere)) {
                        nextStateRef.set(Element.NextState);
                        nextAction.set(Element.NextAction);
                        return true;
                    }
                }

                return false;
            }
        }

        //endregion
    }
    //endregion
}
