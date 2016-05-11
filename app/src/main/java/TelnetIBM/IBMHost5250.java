package TelnetIBM;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;
import com.te.UI.UIUtility;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import Terminals.CipherConnectSettingInfo;
import Terminals.stdActivityRef;
import Terminals.typeConvertion;

/**
 * Created by Franco.Liu on 2015/1/21.
 */

public class IBMHost5250 extends IBMHostBase {

    /* 
    
    bool mbInsert = false;
    bool mbLock = false;
    bool mbError = false;
    private Boolean LogInSend = false;
    System.Collections.ArrayList ErrorDataList;
    private Font _font;
    Tn_CharEvents TnCharEvents = new Tn_CharEvents();
    */


    // #{{  Key Board Input
    static final int KEY_SPACE = -10;
    static final int KEY_F13 = -100;
    static final int KEY_F14 = -101;
    static final int KEY_F15 = -102;
    static final int KEY_F16 = -103;
    static final int KEY_F17 = -104;
    static final int KEY_F18 = -105;
    static final int KEY_F19 = -106;
    static final int KEY_F20 = -107;
    static final int KEY_F21 = -108;
    static final int KEY_F22 = -109;
    static final int KEY_F23 = -110;
    static final int KEY_F24 = -111;
    static final int IBMKEY_PA1 = -200;
    static final int IBMKEY_PA2 = -201;
    static final int IBMKEY_PA3 = -202;
    static final int IBMKEY_FPLUS = -203;
    static final int IBMKEY_FMINUS = -204;
    static final int IBMKEY_FEXIT = -205;
    static final int IBMKEY_ATTN = -206;
    static final int IBMKEY_FBEGIN = -207;
    static final int IBMKEY_DEL = -208;
    static final int IBMKEY_FEND = -209;
    static final int IBMKEY_FMARK = -210;
    static final int IBMKEY_ERINPUT = -211;
    static final int IBMKEY_SYSRQ = -212;
    static final int IBMKEY_LAST = -213;
    static final int IBMKEY_NEXT = -214;
    static final int IBMKEY_CLREOF = -215;
    static final int IBMKEY_ROLDN = -216;
    static final int IBMKEY_ROLUP = -217;
    static final int IBMKEY_PREV = -218;

    // }}#
    static final int IBMKEY_RECORD = -219;
    static final int IBMKEY_BKTAB = -220;
    static final int IBMKEY_DUP = -221;
    static final int IBMKEY_INS = -222;
    static final int IBMKEY_RESET = -223;
    static final int IBMKEY_HOME = -224;
    static final int IBMKEY_NEWLINE = -225;
    static final int IBMKEY_BS = -226;
    static final int IBMKEY_TAB = 61;
    static final int IBMKEY_CLR = -228;
    public static char[] szEBCDIC =
            {
                    0x00, 0x01, 0x02, 0x03, 0x00, 0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0b, 0x0c, 0x0d, 0x20, 0x20, 0x10, 0x11, 0x12, 0x13, 0x00, 0x00, 0x08, 0x00, 0x18, 0x19, 0x00, 0x00, 0x1c, 0x1d, 0x1e, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a, 0x17, 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06, 0x07, 0x00, 0x00, 0x16, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x14, 0x15, 0x00, 0x00,
                    0x20, 0x20, 0xe2, 0xe4, 0xe0, 0xe1, 0xe3, 0xe5, 0xe7, 0xf1, 0x5b, 0x2e, 0x3C, 0x28, 0x2B, 0x21, 0x26, 0xe9, 0xea, 0xeb, 0xe8, 0xed, 0xee, 0xef, 0xec, 0xdf, 0x5d, 0x24, 0x2A, 0x29, 0x3B, 0x5e, 0x2D, 0x2F, 0xc2, 0xc4, 0xc0, 0xc1, 0xc3, 0xc5, 0xc7, 0xd1, 0xa6, 0x2c, 0x25, 0x5F, 0x3E, 0x3F, 0xf8, 0xc9, 0xca, 0xcb, 0xc8, 0xcd, 0xce, 0xcf, 0xcc, 0x60, 0x3A, 0x23, 0x40, 0x27, 0x3D, 0x22,
                    0xd8, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0xab, 0xbb, 0xf0, 0xfd, 0xde, 0xb1, 0xb0, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F, 0x70, 0x71, 0x72, 0xaa, 0xba, 0xe6, 0xb8, 0xc6, 0x80, 0xb5, 0x7e, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A, 0xa1, 0xbf, 0xd0, 0xdd, 0xfe, 0xae, 0xa2, 0xa3, 0xa5, 0xb7, 0xd7, 0xa7, 0xb6, 0xbc, 0xbd, 0xbe, 0xac, 0x7c, 0xaf, 0xa8, 0xb4, 0xa9,
                    0x7b, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0xad, 0xf4, 0xf6, 0xf2, 0xf3, 0xf5, 0x7d, 0x4A, 0x4B, 0x4C, 0x4D, 0x4E, 0x4F, 0x50, 0x51, 0x52, 0xb9, 0xfb, 0xfc, 0xf9, 0xfa, 0xff, 0x5c, 0xf7, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0xb2, 0xd4, 0xd6, 0xd2, 0xd3, 0xd5, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0xb3, 0xdb, 0xdc, 0xd9, 0xda, 0xa0
            };
    public static char[] szGrEBCDIC =   // Robin 2006.1.17  Greek(278) fix bugs in previous conversion table
            {
                    0x00, 0x01, 0x02, 0x03, 0x00, 0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0b, 0x0c, 0x0d, 0x20, 0x20, 0x10, 0x11, 0x12, 0x13, 0x00, 0x00, 0x08, 0x00, 0x18, 0x19, 0x00, 0x00, 0x1c, 0x1d, 0x1e, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a, 0x17, 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06, 0x07, 0x00, 0x00, 0x16, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x14, 0x15, 0x00, 0x00,
                    0x20, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0x5b, 0x2e, 0x3c, 0x28, 0x2b, 0x21, 0x26, 0xca, 0xcb, 0xcc, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd3, 0x21, 0x24, 0x2a, 0x29, 0x3b, 0xac, 0x2d, 0x2f, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xdb, 0x7c, 0x2c, 0x25, 0x5f, 0x3e, 0x3f, 0xa8, 0xa2, 0xb8, 0xb9, 0xa0, 0xba, 0xbc, 0xbe, 0xbf, 0x60, 0x3a, 0x23, 0x40, 0x27, 0x3d, 0x22,
                    0xa1, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xb0, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71, 0x72, 0xe7, 0xe8, 0xe9, 0xea, 0xeb, 0xec, 0xb5, 0x7e, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0xed, 0xee, 0xef, 0xf0, 0xf1, 0xf3, 0xa3, 0xdc, 0xdd, 0xde, 0xfa, 0xdf, 0xfc, 0xfd, 0xfb, 0xfe, 0xf2, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8,
                    0x7b, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0xad, 0xf9, 0xc0, 0xe0, 0x91, 0xaf, 0x7d, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0xb1, 0xbd, 0x90, 0xb7, 0x92, 0xa6, 0x5c, 0xf7, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0xb2, 0xa7, 0x88, 0x8a, 0xab, 0xac, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0xb3, 0xa9, 0xd2, 0x97, 0xbb, 0xff
            };
    public static char[] szFrEBCDIC =   // Robin+ 2005.10.17 support French(297)
            {
                    0x00, 0x01, 0x02, 0x03, 0x00, 0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0b, 0x0c, 0x0d, 0x20, 0x20, 0x10, 0x11, 0x12, 0x13, 0x00, 0x00, 0x08, 0x00, 0x18, 0x19, 0x00, 0x00, 0x1c, 0x1d, 0x1e, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a, 0x17, 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06, 0x07, 0x00, 0x00, 0x16, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x14, 0x15, 0x00, 0x00,
                    0x20, 0x20, 0xe2, 0xe4, 0x40, 0xe1, 0xe3, 0xe5, 0x5c, 0xf1, 0xb0, 0x2e, 0x3c, 0x28, 0x2b, 0x21, 0x26, 0x7b, 0xea, 0xeb, 0x7d, 0xed, 0xee, 0xef, 0xec, 0xdf, 0xa7, 0x24, 0x2a, 0x29, 0x3b, 0x5e, 0x2d, 0x2f, 0xc2, 0xc4, 0xc0, 0xc1, 0xc3, 0xc5, 0xc7, 0xd1, 0xf9, 0x2c, 0x25, 0x5f, 0x3e, 0x3f, 0xf8, 0xc9, 0xca, 0xcb, 0xc8, 0xcd, 0xce, 0xcf, 0xcc, 0xb5, 0x3a, 0xa3, 0xe0, 0x27, 0x3d, 0x22,
                    0xd8, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0xab, 0xbb, 0xf0, 0xfd, 0xde, 0xb1, 0x5b, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71, 0x72, 0xaa, 0xba, 0xe6, 0xb8, 0xc6, 0x80, 0x60, 0xa8, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0xa1, 0xbf, 0xd0, 0xdd, 0xfe, 0xae, 0xa2, 0x23, 0xa5, 0xb7, 0xd7, 0x5d, 0xb6, 0xbc, 0xbd, 0xbe, 0xac, 0x7c, 0xaf, 0x7e, 0xb4, 0xa9,
                    0xe9, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0xad, 0xf4, 0xf6, 0xf2, 0xf3, 0xf5, 0xe8, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0xb9, 0xfb, 0xfc, 0xa6, 0xfa, 0xff, 0xe7, 0xf7, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0xb2, 0xd4, 0xd6, 0xd2, 0xd3, 0xd5, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0xb3, 0xdb, 0xdc, 0xd9, 0xda, 0xa0
            };
    public static char[] szJpEBCDIC =   // Robin+ 2004.12.6 support hostcode(290) to pccode (1041)
            {
                    0x00, 0x01, 0x02, 0x03, 0x00, 0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0b, 0x0c, 0x0d, 0x20, 0x20, 0x10, 0x11, 0x12, 0x13, 0x00, 0x00, 0x08, 0x00, 0x18, 0x19, 0x00, 0x00, 0x1c, 0x1d, 0x1e, 0x1f, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a, 0x17, 0x1b, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06, 0x07, 0x00, 0x00, 0x16, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x14, 0x15, 0x00, 0x00,
                    0x20, 0xa1, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xa0, 0x2e, 0x3c, 0x28, 0x2b, 0x7c, 0x26, 0xaa, 0xab, 0xac, 0xad, 0xae, 0xaf, 0x20, 0xb0, 0x20, 0x21, 0x5c, 0x2a, 0x29, 0x3b, 0x5e, 0x2d, 0x2f, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x2c, 0x25, 0x5f, 0x3e, 0x3f, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x60, 0x3a, 0x23, 0x40, 0x27, 0x3d, 0x22,
                    0x20, 0xb1, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0x20, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xc0, 0xc1, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0x20, 0x20, 0xca, 0xcb, 0xcc, 0x5b, 0x7e, 0xcd, 0xce, 0xcf, 0xd0, 0xd1, 0xd2, 0xd3, 0xd4, 0xd5, 0x20, 0xd6, 0xd7, 0xd8, 0xd9, 0x5d, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf,
                    0x7b, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x7d, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x24, 0x20, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20
            };
    //private char[][] CharGrid = null;
    //private char[][] AttribGrid = null;
    final char mAttrMaskNotShow = 0x07;
    final int FW1_DUP = 0x10;
    final int FW1_BYPASS = 0x20;
    final int FW1_MDT = 0x08;
    final int FW1_SHIFT_EDIT = 0x01 | 0x02 | 0x04;
    final int FW2_ENTER = 0x80;
    final int FW2_EXIT = 0x40;
    final int FW2_MONO = 0x20;
    final int FW2_MAND = 0x08;


    // }}#
    final int CC_MoveCursor = 0x04;
    final int CC_ResetBlinkingCursor = 0x02;
    final int CC_SetBlinkingCursor = 0x01;
    final int CC_UnlockKeyboard = 0x08;
    final int CC_SoundAlarm = 0x04;
    final int CC_SetMessageWaitingOff = 0x02;
    final int CC_SetMessageWaitingOn = 0x01;
    final int CC_BlinkingCursor = CC_ResetBlinkingCursor | CC_SetBlinkingCursor;
    public char mCurChar;
    // #{{ Variables
    IBmOrders CurOrder = IBmOrders.None;

    // }}#

    // #{{ Parser Actions
    int mIndexTab = 0;
    int mIndexCaret = 0;
    char CurAttrib = 0;
    FieldArray FieldList = new FieldArray();
    IBmCommands mCurCommand = IBmCommands.None;
    boolean bKeybaordLock = false;
    boolean bMoveCursor = false;
    int RecordToField = 0;
    Ibm_Caret InsertCaret = null;
    Ibm_Caret BufferAddr = new Ibm_Caret();
    Ibm_Caret Caret = new Ibm_Caret();
    int PanddingLenth = 0;
    IBmStates mLastIBMState = IBmStates.Ground;

    // }}#
    IBmStates mNewNextState = IBmStates.None;
    IBmActions mLastAction = IBmActions.None;
    boolean mbChangeNextStatus = false;
    //IBmCommands mCurCommad;
    IBmCommands ReadCommandType = IBmCommands.None;
    IBmStateChangeEvents mTnIBmStateChangeEvents = new IBmStateChangeEvents();
    byte mbyCommandBuf = 0x00;
    byte[] mCurControlCode = {0x00, 0x00};
    int CurRecordLenth = 0;
    private int TopMargin;
    private int BottomMargin;
    private int LeftMargin;
    private int RightMargin;
    private java.util.ArrayList<Character> DataList = new java.util.ArrayList<Character>();
    private java.util.ArrayList<Character> ParamList = new java.util.ArrayList<Character>();
    private java.util.ArrayList<Character> ControlCodeList = new java.util.ArrayList<Character>();
    // #{{  Char Event List
    private Tn_CharEventInfo[] mTn_CharEvents = {
            //	previous IBMState ,  CharFrom,  CharTo,  Action,             Cur State
            new Tn_CharEventInfo(IBmStates.Ground, '\u0000', '\u00FF', IBmActions.RecordLenth, IBmStates.RecLenthStrat),
            new Tn_CharEventInfo(IBmStates.RecLenthStrat, '\u0000', '\u00FF', IBmActions.RecordLenth, IBmStates.RecLenthEnd),
            new Tn_CharEventInfo(IBmStates.RecLenthEnd, '\u0012', '\u0012', IBmActions.None, IBmStates.RecTypeStrat),
            new Tn_CharEventInfo(IBmStates.RecTypeStrat, '\u00A0', '\u00A0', IBmActions.None, IBmStates.RecTypeEnd),
            new Tn_CharEventInfo(IBmStates.RecTypeEnd, '\u0000', '\u00FF', IBmActions.None, IBmStates.RecReverseStrat),
            new Tn_CharEventInfo(IBmStates.RecReverseStrat, '\u0000', '\u00FF', IBmActions.None, IBmStates.RecReverseEnd),
            new Tn_CharEventInfo(IBmStates.RecReverseEnd, '\u0004', '\u0004', IBmActions.None, IBmStates.Header),
            new Tn_CharEventInfo(IBmStates.Header, '\u0000', '\u00FF', IBmActions.None, IBmStates.Flags),
            new Tn_CharEventInfo(IBmStates.Flags, '\u0000', '\u00FF', IBmActions.None, IBmStates.Option),
            new Tn_CharEventInfo(IBmStates.Option, '\u0000', '\u00FF', IBmActions.None, IBmStates.OptionEnd),
            new Tn_CharEventInfo(IBmStates.OptionEnd, '\u00FF', '\u00FF', IBmActions.None, IBmStates.Ground),
            new Tn_CharEventInfo(IBmStates.OptionEnd, '\u0004', '\u0004', IBmActions.RecordCommad, IBmStates.Command),
            new Tn_CharEventInfo(IBmStates.Command, '\u0000', '\u00FE', IBmActions.RecordCommad, IBmStates.CommandEnd),
            new Tn_CharEventInfo(IBmStates.CommandEnd, '\u0004', '\u0004', IBmActions.RecordCommad, IBmStates.Command),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0004', '\u0004', IBmActions.RecordCommad, IBmStates.Command),
            new Tn_CharEventInfo(IBmStates.CommandCcode, '\u0000', '\u00ff', IBmActions.RecordCcode, IBmStates.CommandCcode2),
            new Tn_CharEventInfo(IBmStates.CommandCcode2, '\u0000', '\u00ff', IBmActions.RecordCcode, IBmStates.CommandCcodeEnd),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0011', '\u0011', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0013', '\u0013', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0002', '\u0002', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u001d', '\u001d', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0001', '\u0001', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0014', '\u0014', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0003', '\u0003', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0012', '\u0012', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0004', '\u0004', IBmActions.RecordCommad, IBmStates.Command),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u00EF', '\u00EF', IBmActions.None, IBmStates.Ground),
            new Tn_CharEventInfo(IBmStates.CommandCcodeEnd, '\u0000', '\u00fE', IBmActions.RecordData, IBmStates.CommandEndEx),
            new Tn_CharEventInfo(IBmStates.Anywhere, '\u00FF', '\u00FF', IBmActions.None, IBmStates.Ground),

            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0011', '\u0011', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0013', '\u0013', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0002', '\u0002', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u001d', '\u001d', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0001', '\u0001', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0014', '\u0014', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0003', '\u0003', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0012', '\u0012', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u00EF', '\u00EF', IBmActions.None, IBmStates.Ground),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0011', '\u0011', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0013', '\u0013', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0002', '\u0002', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u001d', '\u001d', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0001', '\u0001', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0014', '\u0014', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0003', '\u0003', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0012', '\u0012', IBmActions.OrdersRecord, IBmStates.OrdersEx),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0004', '\u0004', IBmActions.RecordCommad, IBmStates.Command),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0011', '\u0011', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0013', '\u0013', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0002', '\u0002', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u001d', '\u001d', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0001', '\u0001', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0014', '\u0014', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0003', '\u0003', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0012', '\u0012', IBmActions.OrdersRecord, IBmStates.Orders),
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0004', '\u0004', IBmActions.RecordCommad, IBmStates.Command),
            new Tn_CharEventInfo(IBmStates.Orders, '\u0000', '\u00ff', IBmActions.RecordData, IBmStates.CommandEndEx),//after padding
            new Tn_CharEventInfo(IBmStates.OrdersEx, '\u0000', '\u00ff', IBmActions.RecordData, IBmStates.CommandEndEx),//after padding
            new Tn_CharEventInfo(IBmStates.CommandEnd, '\u00ef', '\u00ef', IBmActions.None, IBmStates.Ground),

            new Tn_CharEventInfo(IBmStates.CommandEnd, '\u0000', '\u00ff', IBmActions.RecordData, IBmStates.CommandEndEx),
            new Tn_CharEventInfo(IBmStates.CommandEndEx, '\u0000', '\u00ff', IBmActions.RecordData, IBmStates.CommandEndEx),

    };

    // #{{ constructor & members
    public IBMHost5250() {


        this.SetSize(25, 80);


    }
    // }}#

    private boolean movePosToNext(Point pos) {
        if (pos.X == RightMargin && pos.Y == BottomMargin)
            return false;
        if (pos.X >= RightMargin) {
            pos.X = LeftMargin;
            pos.Y++;
        } else
            pos.X++;
        return true;
    }

    private boolean isScreenAttributeVisible(byte attr) {
        return (attr & mAttrMaskNotShow) != mAttrMaskNotShow;
    }

    public String GetLogTitle() {
        return "TN";
    }

    @Override
    public Point getCursorGridPos() {
        return Caret.Pos;
    }

    private void SetSize(int Rows, int Columns) {
        this._rows = Rows;
        this._cols = Columns;

        this.TopMargin = 0;
        this.BottomMargin = Rows - 1;
        this.LeftMargin = 0;
        this.RightMargin = Columns - 1;
        this.CharGrid = new char[Rows][];
        this.AttribGrid = new char[Rows][];
        for (int i = 0; i < this.CharGrid.length; i++) {
            this.CharGrid[i] = new char[Columns];
            this.AttribGrid[i] = new char[Columns];
        }

    }

    public String GetTerminalTypeName() {
        return "IBM-5292-2";
    }

    public void drawAll() {
        for (int idxRow = this.TopMargin; idxRow < this.BottomMargin; idxRow++) {
            for (int idxCol = 0; idxCol < this._cols; ++idxCol) {
                boolean isNeedLine = FieldList.isNeedLine(idxCol, idxRow);
                if(isNeedLine) {
                    DrawFieldChar(this.CharGrid[idxRow][idxCol], idxCol, idxRow, false, isNeedLine);
                } else {
                    DrawCharLive(this.CharGrid[idxRow][idxCol], idxCol, idxRow, false, false);
                }
            }
        }
    }

    @Override
    public void processChar(char ch) {
        if (BuildConfig.DEBUG)
            Log.d("TE:", String.format("IBMHost5250.processChar, char = [%02x]", (byte) ch));
        AtomicReference<IBmStates> curState = new AtomicReference<IBmStates>(IBmStates.None);
        AtomicReference<IBmActions> curAction = new AtomicReference<IBmActions>(IBmActions.None);
        AtomicReference<IBmActions> stateExitAction = new AtomicReference<IBmActions>(IBmActions.None);
        AtomicReference<IBmActions> stateEntryAction = new AtomicReference<IBmActions>(IBmActions.None);

        mCurChar = ch;
        if (CheckPadding() > 0) {
            curState.set(mLastIBMState);
            curAction.set(mLastAction);
        } else
            GetStateEventAction(mCurCommand, mLastIBMState, mCurChar, curState, curAction);

        if (BuildConfig.DEBUG)
            Log.d("TE:", String.format("preState = %s, curState = %s, curAction = %s", mLastIBMState.toString(), curState, curAction));

        boolean bStateChanged = (curState.get() != IBmStates.None && curState.get() != mLastIBMState);
        if (bStateChanged) {
            // check for state exit actions
            mTnIBmStateChangeEvents.GetStateChangeAction(mLastIBMState, Transitions.Exit, stateExitAction);
            if (BuildConfig.DEBUG)
                Log.d("TE:", String.format("[Exit] action = %s", stateExitAction));
            // Process the exit action
            if (stateExitAction.get() != IBmActions.None) DoAction(stateExitAction.get());
        } else {
            if (BuildConfig.DEBUG)
                Log.d("TE:", String.format("[Exit] no exit action."));
        }

        if (curAction.get() != IBmActions.None) {
            if (BuildConfig.DEBUG)
                Log.d("TE:", String.format("[Cur Action] do action = %s", curAction));
            DoAction(curAction.get());
        } else {
            if (BuildConfig.DEBUG)
                Log.d("TE:", String.format("[Cur Action] no action"));
        }

        mLastAction = curAction.get();

        if (bStateChanged) {
            // check for state entry actions
            mTnIBmStateChangeEvents.GetStateChangeAction(curState.get(), Transitions.Entry, stateEntryAction);

            if (BuildConfig.DEBUG)
                Log.d("TE:", String.format("[Entry] action = %s", stateEntryAction));

            // Process the entry action
            if (stateEntryAction.get() != IBmActions.None) DoAction(stateEntryAction.get());

            // change the parsers state attribute
            mLastIBMState = curState.get();
        } else {
            if (BuildConfig.DEBUG)
                Log.d("TE:", String.format("[Entry] no entry action"));
        }

        if (mbChangeNextStatus) {
            mLastIBMState = mNewNextState;
            mbChangeNextStatus = false;
        }
    }

    @Override
    protected boolean autoLogin() {
        if (FieldList.size() <= 1)
            return false;
        String loginName = CipherConnectSettingInfo.getHostUserNameByIndex(CipherConnectSettingInfo.GetSessionIndex());
        String pwd = CipherConnectSettingInfo.getHostPassWordByIndex(CipherConnectSettingInfo.GetSessionIndex());
        //get first field and set login name.
        IBM_FIELD nameField = FieldList.get(0);
        IBM_FIELD pwdField = FieldList.get(1);
        if (nameField == null || pwdField == null)
            return false;
        FillField(nameField, loginName);
        nameField.Modified = true;
        FillField(pwdField, pwd);
        pwdField.Modified = true;
        ProcessIbmEnter();
        return true;
    }

    public final boolean GetStateEventAction(IBmCommands CurCommand, IBmStates preState, char curChar, AtomicReference<IBmStates> state, AtomicReference<IBmActions> action) {
        if (CurCommand == IBmCommands.Restore_Screen && (preState == IBmStates.CommandEndEx || preState == IBmStates.CommandEnd)) {
            if (curChar == 0xff || curChar == 0xef) {
                state.set(IBmStates.Ground);
                action.set(IBmActions.None);
                return true;
            }

            state.set(IBmStates.CommandEndEx);
            action.set(IBmActions.RecordData);
            return true;
        }

        for (int i = 0; i < mTn_CharEvents.length; i++) {
            Tn_CharEventInfo tnEvent = mTn_CharEvents[i];

            if (curChar >= tnEvent.mCharFrom &&
                    curChar <= tnEvent.mCharTo &&
                    (preState == tnEvent.mPreState || tnEvent.mPreState == IBmStates.Anywhere)) {
                state.set(tnEvent.mState);
                action.set(tnEvent.mAction);
                return true;
            }
        }
        return false;
    }

    private void ParserOrders() {
        //this.Order;
        //this.CurCommand;
        IBmOrders Order = this.CurOrder;
        switch (Order) {
            case Set_Buffer_Address://2bytes : position  Y ,X
                typeConvertion.CharacterObjectToInt(ParamList.get(0));
                int Y = typeConvertion.CharacterObjectToInt(ParamList.get(0));
                int X = typeConvertion.CharacterObjectToInt(ParamList.get(1));
                SetBufferAddress(Y - 1, X - 1);
                break;
            case Start_of_Field://5bytes : //FFW (2),attribute (1),lenth(2)
                SetStartOfField();
                break;
            case Start_of_Header://5bytes : //FFW (2),attribute (1),lenth(2)
                FieldList.clear();
                break;
            case Repeat_to_Address://3bytes : position  Y ,X ,Data
                RepeatFillData(typeConvertion.CharacterObjectToInt(ParamList.get(0)) - 1, typeConvertion.CharacterObjectToInt(ParamList.get(1)) - 1, (Character) ParamList.get(2));
                break;
            default:
                break;

        }


    }

    private void ParseExData() {
        // Parse Data after command or order
        switch (mCurCommand) {
            case Write_Structured_Field:
                ParserCommandData();
                break;
            case Write_to_Display:
                ParserFieldData();
                SetRecordToField(0);
                break;
            case Write_Error_Code:
                bKeybaordLock = true;
                ParserFieldDataPos(new Point(1, 24));
                SetRecordToField(0);
                break;
            case Restore_Screen:
                ParserRestoreData();

                break;
            default:
                break;

        }

        UpDateAllField();
    }

    private void ParserFieldData() {
        for (Object ch : DataList) {
            char c = ((Character) ch).charValue();
            if (IsCharAttributes(c)) {
                this.CurAttrib = c;
            } else {
                char Chater = (char) szEBCDIC[(int) c];
                if (isScreenAttributeVisible((byte) CurAttrib))
                    PrintChar(Chater, BufferAddr.Pos.X, BufferAddr.Pos.Y, (IsRecordToField() > 0));
            }

            movePosToNext(BufferAddr.Pos);
        }
    }

    private void ParserRestoreData() {
        //DataList
        Character array[] = new Character[DataList.size()];
        array = (Character[]) DataList.toArray(array);

        int j = 0;
        while (array.length >= j) {
            if (array[j] == 0)
                break;

            int Len = array[j++];
            int X = array[j++];
            int Y = array[j++];

            for (int i = 0; i < Len; i++) {
                //if (Y<this.BottomMargin && X<this.RightMargin)
                {
                    this.AttribGrid[Y][X] = array[j++];
                    this.CharGrid[Y][X] = array[j++];
                }

                X = X + 1;
                if (X > this.RightMargin) {


                    Y = Y + 1;
                    X = 0;
                    if (Y > this.BottomMargin)
                        break;
                }

            }

        }


       /*
        * void CTNTelnetIBM::ibm_restore_screen(BYTE* szBuf) //0x12  restore screen
{

	if (*szBuf!=0x12)
		return;

	BYTE* pData=szBuf+1;

	while(*pData>0)
	{
		int Len=*(pData++);
		int X=*(pData++);
		int Y=*(pData++);

		for(int i=0;i<Len;i++)
		{
			szScrBuf[Y][X]=*(pData++);
			szScrAtt[Y][X]=*(pData++);

			X=X+1;
			if (X>=SCRCOL)
			{


				Y=Y+1;
				X=0;
				if (Y>=SCRROW)
					break;
			}

		}
	}

    //TODO:


}
        */

    }

    private void ParserFieldDataPos(Point Pos) {
        Point posTemp = new Point(Pos.X, Pos.Y);

        for (Object ch : DataList) {
            char c = ((Character) ch).charValue();
            if (IsCharAttributes(c)) {
                this.CurAttrib = c;
            } else {
                char Chater = (char) szEBCDIC[(int) c];
                PrintChar(Chater, posTemp.X, posTemp.Y, (IsRecordToField() > 0));
            }
            movePosToNext(posTemp);
        }
    }

    private void ParserControlCode() {

        byte c = 0;

        mCurControlCode[0] = typeConvertion.CharacterObjectToByte(ControlCodeList.get(0));
        mCurControlCode[1] = typeConvertion.CharacterObjectToByte(ControlCodeList.get(1));

        switch (mCurControlCode[0]) {
            case 2:
                ResetMDT(true);
                break;
            case 3:
                ResetMDT(false);
                break;
            case 4:
                NullFields(true);
                break;
            case 5:
                ResetMDT(true);
                NullFields(false);
                break;
            case 6:
                ResetMDT(true);
                NullFields(true);
                break;
            case 7:
                ResetMDT(false);
                NullFields(false);
                break;
        }

        c = mCurControlCode[1];

        if ((c & CC_MoveCursor) > 0)
            this.bMoveCursor = true;

        if ((c & CC_BlinkingCursor) == CC_BlinkingCursor) {

        } else if ((c & CC_ResetBlinkingCursor) > 0) {

        }


        if ((c & CC_UnlockKeyboard) > 0)
            this.bKeybaordLock = false;

        if ((c & CC_SoundAlarm) > 0)
            PlayWarningSounds();

        switch (mCurCommand) {
            case Write_to_Display:
            case Read_Input_Fields:
            case Read_MDT_Fields:
            case Read_Immediate:
            default:
                break;
        }

        ControlCodeList.clear();
    }

    private void PrePareCommandInfo() {
        //if (CurCommand[1]!=0x04)
        //Error

        IBmCommands Command = IBmCommands.convert(mbyCommandBuf);
        switch (Command) {
            case Read_Immediate:

                ReadCommandType = IBmCommands.Read_Immediate;

                String Data = PackInboundData(IBmAID.None);
                byte[] OutData = ConverPackToRawData(Data);
                DispatchMessageRaw(this, OutData, OutData.length);
                break;
            case Read_Input_Fields:

                ChangeNextStatus(IBmStates.CommandCcode);
                ReadCommandType = IBmCommands.Read_Input_Fields;

                break;


            case Read_MDT_Fields:

                ChangeNextStatus(IBmStates.CommandCcode);
                ReadCommandType = IBmCommands.Read_MDT_Fields;
                break;

            case Read_Screen:

                break;

            case Save:

                break;
            default:
                break;
        }

        switch (Command) {

            case Clear_Format_Table:
                bKeybaordLock = true;
                break;
            case Clear_Unit:
                bKeybaordLock = true;
                ViewClear();
                this.FieldList.clear();
                break;

            case Restore_Screen:
                mCurCommand = Command;
                break;

            case Roll:

                break;

            case Write_Error_Code:

                break;

            case Write_Structured_Field:

                break;

            case Write_to_Display:
                InsertCaret = null;
                ChangeNextStatus(IBmStates.CommandCcode);
                break;
            default:
                break;
        }

        switch (Command) {
            case Read_Immediate:
            case Read_Input_Fields:
            case Read_MDT_Fields:
            case Read_Screen:
            case Save:
            case Restore_Screen:
            case Roll:
            case Write_Error_Code:
            case Write_Structured_Field:
            case Write_to_Display:
                mCurCommand = Command;
                break;
            default:
                break;
        }

            /*




        }



        switch (Command)
        {

            case IBmCommands.Clear_Format_Table:

                break;

            case IBmCommands.Clear_Unit:

                this._ViewContainer.Clear();
                this.FieldList.Clear();
                break;

            case IBmCommands.Restore_Screen:

                break;

            case IBmCommands.Roll:

                break;

            case IBmCommands.Write_Error_Code:

                break;

            case IBmCommands.Write_Structured_Field:

                break;

            case IBmCommands.Write_to_Display:

                break;
        }

        switch ((IBmCommands)CurCommandBuf[0])
        {
            case IBmCommands.Read_Immediate:
            case IBmCommands.Read_Input_Fields:
            case IBmCommands.Read_MDT_Fields:
            case IBmCommands.Read_Screen:
            case IBmCommands.Save:
            case IBmCommands.Clear_Format_Table:
            case IBmCommands.Clear_Unit:
            case IBmCommands.Restore_Screen:
            case IBmCommands.Roll:
            case IBmCommands.Write_Error_Code:
            case IBmCommands.Write_Structured_Field:
            case IBmCommands.Write_to_Display:
                CurCommand = (IBmCommands)CurCommandBuf[0];
                break;
        }
        */

    }

    private void ParserCommandData() {
        //Datas After command
        if (mCurCommand == IBmCommands.Write_Structured_Field) {

            Character array[] = new Character[DataList.size()];
            array = (Character[]) DataList.toArray(array);


            //String[] array =  (String[]) DataList.toArray(new String[DataList.size()]);
            Character[] array1 = {(char) 0x00, (char) 0x05, (char) 0xd9, (char) 0x70, (char) 0x00};
            byte[] Resp = {0X12, (byte) 0Xa0, 0X00, 0X00, 0X04, 0X00, 0X00, 0X03, 0X00, 0X00, (byte) 0X88, 0X00, 0X3a, (byte) 0Xd9, 0X70, (byte) 0X80, 0X06, 0X00, 0X01, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X01, (byte) 0Xf3, (byte) 0Xf4, (byte) 0Xf7, (byte) 0Xf7, (byte) 0Xf0, (byte) 0Xc6, (byte) 0Xc3, 0X02, 0X00, 0X00, 0X01, 0X01, 0X01, 0X01, 0X01, 0X00, 0X00, 0X00, 0X00, 0X63, 0X31, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};
            if (Arrays.equals(array, array1)) {
                byte[] OutData = ConverPackToRawData(Resp);
                DispatchMessageRaw(this, OutData, OutData.length);

            }

            /*if (array.equals(array1))
            {
                byte[] OutData = ConverPackToRawData(Resp);
                DispatchMessageRaw(this, OutData, OutData.length);
            }*/

        }

    }

    private void OrderLenthPanding() {
        IBmOrders Command = IBmOrders.convert((byte) mCurChar);
        switch (Command) {
            case Start_of_Header:
                SetPadding(8);
                break;
            case Set_Buffer_Address:
                SetPadding(2);
                break;
            case Start_of_Field:
                char c = GetCharFromCurrentIndex(1);
                char c2 = GetCharFromCurrentIndex(3);

                if ((c & 0xC0) == 0)
                    SetPadding(3);
                else if ((c2 & 0xC0) == 0)
                    SetPadding(5);
                else
                    SetPadding(7);
                break;
            case Repeat_to_Address:
                SetPadding(3);
                break;
            case Insert_Cursor:
                SetPadding(2);
                break;
            case Erase_to_Address:
            case Move_Cursor:

            case Write_Extended_Attribute:
                break;
            default:


                break;
        }

        switch (Command) {
            case Start_of_Header:
            case Set_Buffer_Address:
            case Start_of_Field:
            case Erase_to_Address:
            case Insert_Cursor:
            case Move_Cursor:
            case Repeat_to_Address:
            case Write_Extended_Attribute:
                CurOrder = (IBmOrders) Command;
                ParamList.clear();
                break;
            default:
                break;
        }
    }

    private void SetBufferAddress(int Y, int X) {
        if (X > this.RightMargin && Y > BottomMargin)
            return;

        this.BufferAddr.Pos.Y = Y;
        this.BufferAddr.Pos.X = X;
    }

    private void SetStartOfField() {
        this.movePosToNext(this.BufferAddr.Pos);
        IBM_FIELD DataField = new IBM_FIELD();

        if ((typeConvertion.CharacterObjectToInt(ParamList.get(0)) & FW1_BYPASS) > 0)
            DataField.Bypass = true;

        if ((typeConvertion.CharacterObjectToInt(ParamList.get(0)) & FW1_DUP) > 0)
            DataField.Duplication = true;

        if ((typeConvertion.CharacterObjectToInt(ParamList.get(0)) & FW1_MDT) > 0)
            DataField.Modified = true;

        DataField.ShiftEdit = (byte) (typeConvertion.CharacterObjectToInt(ParamList.get(0)) & FW1_SHIFT_EDIT);
        /*  000 Alphabetic shift.
			001 Alphabetic only. (1)
			010 Numeric shift.(2)
			011 Numeric only. (3)
			100 Katakana shift. (4)
			101 Reserved. (5)
			110 (6)I/O (magnetic stripe can turn on the master MDT bit by placing a field
			    reader, selector light format word with bit 4 on in the display data stream.
			    pen input only).
            111 (7)Signed numeric.*/

        int FieldSpc = (typeConvertion.CharacterObjectToInt(ParamList.get(0)) & FW1_MDT);


        switch (FieldSpc) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                break;
        }

        if ((typeConvertion.CharacterObjectToInt(ParamList.get(1)) & FW2_ENTER) > 0)
            DataField.AutoEnter = true;

        if ((typeConvertion.CharacterObjectToInt(ParamList.get(1)) & FW2_EXIT) > 0)
            DataField.FieldExit = true;

        if ((typeConvertion.CharacterObjectToInt(ParamList.get(1)) & FW2_MONO) > 0)
            DataField.Monocase = true;

        if ((typeConvertion.CharacterObjectToInt(ParamList.get(1)) & FW2_MAND) > 0)
            DataField.Mandatory = true;

        int RightAdjustMandatoryfill = (typeConvertion.CharacterObjectToInt(ParamList.get(1)) & FW2_MAND);

        switch (RightAdjustMandatoryfill) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                break;
        }


        DataField.Attrib = (Character) ParamList.get(2);
        this.CurAttrib = DataField.Attrib;

        int cnt = 3;

        if (ParamList.size() == 7)
            cnt = 5;

        DataField.Lenth = typeConvertion.CharacterObjectToInt(ParamList.get(cnt)) * 256 + typeConvertion.CharacterObjectToInt(ParamList.get(cnt + 1));

        DataField.Data = new char[DataField.Lenth];
        Arrays.fill(DataField.Data, 0, DataField.Data.length, '\0');
        DataField.CaretAddr = new Ibm_Caret(this.BufferAddr.Pos);
        AddNewField(DataField);
    }

    private void AddNewField(IBM_FIELD Field) {

        FieldList.add(Field);

        if (FieldList.size() == 1) {
            ResetTabPosition();
            CaretUpdate();
        }

        SetRecordToField(1);

    }

    public void ParseEnd() {
        tryInsertCaret();
    }

    // }}#

    public void tryInsertCaret() {
        if (InsertCaret != null) {


        }
        TabToFirstField();
    }

    // #{{ Parser Flags & conditions
    private void SetRecordToField(int Flag) {
        this.RecordToField = Flag;
    }

    private int IsRecordToField() {
        return this.RecordToField;

    }

    private void SetPadding(int padding) {
        if (PanddingLenth > 0)
            PanddingLenth = padding; //error
        else
            PanddingLenth = padding;
    }

    private int CheckPadding() {
        if (PanddingLenth > 0)
            return PanddingLenth--;

        return 0;
    }

    private boolean IsCharAttributes(char c) {
        if (c >= 0x20 && c <= 0x3f)
            return true;

        return false;

    }

    private void ResetTabPosition() {
        mIndexTab = 0;
        mIndexCaret = 0;
    }

    private int GetIndexTab() {
        return mIndexTab;
    }

    private int GetNextTab() {
        int size = FieldList.size();
        int IndexTab = mIndexTab;

        IndexTab++;

        if (IndexTab >= size)
            IndexTab = 0;

        return IndexTab;
    }

    private void SetIndexTab(int tab) {
        mIndexTab = tab;

    }

    private void ChangeNextStatus(IBmStates Status) {
        mNewNextState = Status;
        mbChangeNextStatus = true;
    }

    private void FillField(IBM_FIELD Field, String Data) {
        int index = 0;

        for (char c : Data.toCharArray()) {
            char Code = ConvertAsciiToEBCD(c);

            if (Character.isLetter(c) && Field.Monocase) {
                Code = ConvertAsciiToEBCD(Character.toUpperCase((char) c));
            }

            Field.Data[index] = Code;
            index++;
        }

    }


    // }}#

    // #{{ constants 

    private void EraseAfterCursor() {
        int nRet = 0;
        int nCaret = 0;
        // GetIndexCaret
        IBM_FIELD cField = GetCurrentField();

        if (cField == null)
            return;


        if (cField.Bypass)
            return;

        nRet = cField.Lenth;
        nCaret = GetIndexCaret();

        if (cField.ShiftEdit == 7)
            nRet--;

        nRet--;
        while (nRet > nCaret) {
            cField.Data[nRet] = 0;
            nRet--;
        }
        UpDateActiveField();
    }

    private void DoAction(IBmActions NextAction) {
        // in UC
        // Manage the contents of the Sequence and Param Variables
        switch (NextAction) {
            case Dispatch:
            case Collect:

                break;

            case NewCollect:

                break;

            case Param:

                break;

            case ParseSteamBegin:
                CurRecordLenth = 0;
                DataList.clear();
                ParamList.clear();
                ControlCodeList.clear();
                break;
            case RecordLenth:
                CurRecordLenth = (CurRecordLenth * 256) + mCurChar;

                break;
            case RecordCommad:
                mbyCommandBuf = (byte) mCurChar;
                break;
            case ParseCommand:
                PrePareCommandInfo();
                DataList.clear();
                break;
            case OrdersBeging:
                OrderLenthPanding();
                DataList.clear();
                SetRecordToField(0);
                break;
            case OrdersRecord:
                ParamList.add(mCurChar);
                break;
            case OrdersParse:
                if (ParamList.size() > 0)
                    ParserOrders();
                break;

            case RecordCcode:
                ControlCodeList.add(mCurChar);
                break;
            case ControlCodeParse:
                if (ControlCodeList.size() > 0)
                    ParserControlCode();
                break;


            case RecordData:
                DataList.add(mCurChar);
                break;
            case ParseData:
                if (DataList.size() > 0)
                    ParseExData();


                break;/**/

            default:
                break;
        }


    }

    private void RepeatFillData(int ToY, int ToX, char Data) {
        int Repeatlen = (ToY * this._cols + ToX) - (BufferAddr.Pos.Y * this._cols + BufferAddr.Pos.X);
        if (Repeatlen <= 0)
            return;

        char Chater = (char) szEBCDIC[(int) Data];

        for (int i = 0; i < Repeatlen; i++) {
            PrintChar(Chater, BufferAddr.Pos.X, BufferAddr.Pos.Y, (IsRecordToField() > 0));
            movePosToNext(BufferAddr.Pos);
        }
    }

    private IBM_FIELD GetCurrentField() {
        if (FieldList.size() <= 0)
            return null;

        IBM_FIELD CurField = (IBM_FIELD) FieldList.get(GetIndexTab());


        return CurField;
    }

    private IBM_FIELD GetNextField() {
        if (FieldList.size() <= 0)
            return null;

        IBM_FIELD CurField = (IBM_FIELD) FieldList.get(GetNextTab());


        return CurField;
    }

    private void ResetMDT(boolean bBypass) {

        IBM_FIELD Field;


        if (FieldList.size() <= 0)
            return;

        for (int i = 0; i < FieldList.size(); i++) {

            Field = (IBM_FIELD) FieldList.get(i);

            if (!Field.Bypass || !bBypass) {
                Field.Modified = true;
            }

            FieldList.set(i, Field);
        }

    }

    private void NullFields(boolean bMDT) {

        if (FieldList.size() <= 0)
            return;

        for (int i = 0; i < FieldList.size(); i++) {
            IBM_FIELD Field = (IBM_FIELD) FieldList.get(i);
            if (!Field.Bypass) {

                if (bMDT) {
                    Field.Modified = true;
                }

                //clear fields
                for (int c = 0; c < Field.Lenth; c++) {
                    Field.Data[c] = (char) 0x00;
                }
            }
            FieldList.set(i, Field);
        }
        UpDateAllField();

    }

    private void UpDateAllField() {

        if (FieldList.size() <= 0)
            return;

        for (int i = 0; i < FieldList.size(); i++) {
            UpDateField(i);
        }
    }

    private void UpDateActiveField() {

        UpDateField(GetIndexTab());

    }

    private void UpDateField(int index) {
        if (FieldList.size() <= 0)
            return;

        IBM_FIELD CurField = (IBM_FIELD) FieldList.get(index);

        int X = CurField.CaretAddr.Pos.X;
        int Y = CurField.CaretAddr.Pos.Y;

        for (int i = 0; i < CurField.Lenth; i++) {
            if (IsCharAttributes((char) CurField.Data[i])) {
                this.CurAttrib = CurField.Data[i];
            } else {
                char Chater = szEBCDIC[(int) CurField.Data[i]];
                if (isScreenAttributeVisible((byte) CurField.Attrib))
                    PrintChar(Chater, X, Y, true);
            }

            if (X >= this.RightMargin) {
                X = this.LeftMargin;
                Y++;
            } else
                X++;
        }
    }

    private void PrintChar(char Character, int X, int Y, boolean Isfield) {
        if (Y > (this._rows - 1))
            return;

        this.AttribGrid[Y][X] = CurAttrib;
        this.CharGrid[Y][X] = Character;

        //_ViewContainer.DrawCharLive(Character, X, Y, false, false);
        if (Isfield)
            DrawFieldChar(Character, X, Y, false, Isfield);
        else
            DrawCharLive(Character, X, Y, false, false);


    }

    public void PlayWarningSounds() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(stdActivityRef.GetCurrActivity(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // #{{ Data Packing
    private byte[] ConverPackToRawData(String Str) {

        int lenth = Str.length() + 2;
        int cnt = 0;
        byte[] Data = new byte[lenth + 2]; //plus FF EF

        Data[cnt++] = (byte) (lenth / 256);
        Data[cnt++] = (byte) (lenth % 256);

        for (int i = 0; i < Str.length(); i++) {
            Data[cnt++] = (byte) Str.charAt(i);
        }

        Data[cnt++] = (byte) 0xFF;
        Data[cnt++] = (byte) 0xEF;


        return Data;
    }

    private byte[] ConverPackToRawData(byte[] DataIn) {

        int lenth = DataIn.length + 2;
        int cnt = 0;
        byte[] Data = new byte[lenth + 2]; //plus FF EF

        Data[cnt++] = (byte) (lenth / 256);
        Data[cnt++] = (byte) (lenth % 256);

        for (int i = 0; i < DataIn.length; i++) {
            Data[cnt++] = (byte) DataIn[i];
        }

        Data[cnt++] = (byte) 0xFF;
        Data[cnt++] = (byte) 0xEF;


        return Data;
    }

    private String PackInboundData(IBmAID Aid) {
        //0011 12A0000004000003 0636F1 110635F1FFEF
        //000D 12A0000004000003 0635F1 FFEF

        String Buffer = "";
        Buffer += PackInboundHeader();
        Buffer += PackCursorAddress();
        Buffer += ((char) Aid.getValue());

        if (Aid == IBmAID.ENTER || Aid == IBmAID.F3)
            Buffer += PackFieldData();

        return Buffer;
    }

    private String PackContentData() {
        String Buffer = "";
        Buffer += PackInboundHeader();
        Buffer += PackFieldData();

        return Buffer;
    }

    // }}#

    // #{{  EBCDICs 

    private String PackContentAttn() {
        String Buffer = "";
        Buffer += PackAttnHeader();


        return Buffer;
    }

    private String PackContentRecord() {
        String Buffer = "";
        Buffer += PackRecordHeader();


        return Buffer;
    }

    private String PackContentRequest() {
        String Buffer = "";
        Buffer += PackSysReqHeader();


        return Buffer;
    }

    private String PackFieldData() {
        String Data = "";
        boolean SBA = true;
        boolean REPLACE = false;
        switch (this.ReadCommandType) {
            case Read_Immediate:
            case Read_Input_Fields:
                SBA = false;
                REPLACE = true;
                break;
            case Read_MDT_Fields:
                SBA = true;
                REPLACE = false;
                break;
            default:
                break;

        }

        for (int i = 0; i < FieldList.size(); i++) {
            IBM_FIELD Field;

            Field = FieldList.get(i);

            if (Field.Modified || this.ReadCommandType == IBmCommands.Read_Input_Fields || this.ReadCommandType == IBmCommands.Read_Immediate) {
                Data += BuildReadDataByIndex(i, SBA, REPLACE);
            }
        }
        return Data;
    }

    public String BuildReadDataByIndex(int index, boolean SBA, boolean Replace) {
        IBM_FIELD Field;
        int X, Y;
        boolean HaveData = false;

        StringBuilder sb = new StringBuilder("");
        Field = FieldList.get(index);

        char[] BufData = new char[Field.Lenth];

        for (int i = Field.Lenth - 1; i >= 0; i--) {
            BufData[i] = Field.Data[i];

            if (BufData[i] != 0) {
                HaveData = true;
            } else {
                if (HaveData)
                    BufData[i] = ConvertAsciiToEBCD((int) 0x20);
            }

        }

        X = Field.CaretAddr.Pos.X + 1;
        Y = Field.CaretAddr.Pos.Y + 1;

        if (SBA) {
            sb.append((char) 0x11);
            sb.append((char) Y);
            sb.append((char) X);
        }

        for (int i = 0; i < Field.Lenth; i++) {
            char Data = BufData[i];

            if (BufData[i] == 0) {
                if (Replace) {
                    Data = ConvertAsciiToEBCD((int) 0x20);
                } else
                    break;
            }
            sb.append((char) Data);
        }
        return sb.toString();

    }

    // }}#

    private String PackInboundHeader() {
        return "\u0012\u00A0\u0000\u0000\u0004\u0000\u0000\u0003";
    }
    // }}#

    private String PackAttnHeader() {
        return "\u0012\u00A0\u0000\u0000\u0004\u0040\u0000\u0000";
    }

    private String PackSysReqHeader() {
        return "\u0012\u00A0\u0000\u0000\u0004\u0004\u0000\u0000";
    }

    private String PackSysHelpHeader() {
        // BYTE help[] = { 0x0, 0xd, 0x12, 0xa0, 0x0, 0x0, 0x4, 0x0, 0x0, 0x3, 0x1, 0x1, 0xf3 };
        return "\u0012\u00A0\u0000\u0000\u0004\u0000\u0000\u0003\u0001\u0001\u00f3";
    }

    private String PackSysPrintHeader() {
        // BYTE print[] = {0x0, 0xd, 0x12, 0xa0, 0x0, 0x0, 0x4, 0x0, 0x0, 0x3, 0xe, 0x28, 0xf6};
        return "\u0012\u00A0\u0000\u0000\u0004\u0000\u0000\u0003\u000e\u0028\u00f6";
    }

    private String PackClearHeader() {
        //BYTE clear[] = { 0x0, 0xd, 0x12, 0xa0, 0x0, 0x0, 0x4, 0x0, 0x0, 0x3, 0x6, 0x35, 0xbd };
        return "\u0012\u00A0\u0000\u0000\u0004\u0000\u0000\u0003\u0006\u0035\u00bd";
    }

    private String PackRecordHeader() {
        return "\u0012\u00A0\u0000\u0000\u0004\u0000\u0000\u0003\u000e\u0028\u00f8";
    }

    private String PackCursorAddress() {
        String str = "";
        int X, Y;
        X = Caret.Pos.X + 2;
        Y = Caret.Pos.Y + 1;
        str += (char) Y;
        str += (char) X;
        return str;
    }

    // #{{  Caret & Tabs
    private void CaretUpdate() {
        if (FieldList.size() - 1 < GetIndexTab())
            return; //Excection

        IBM_FIELD CurField;

        while (true) {
            CurField = (IBM_FIELD) FieldList.get(GetIndexTab());

            if (CurField.Lenth <= this.GetIndexCaret()) {

                NextIndexTab();
                CurField = (IBM_FIELD) FieldList.get(GetIndexTab());
                this.SetIndexCaret(0);

            }

            if (this.GetIndexCaret() < 0) {

                PrevIndexTab();
                CurField = (IBM_FIELD) FieldList.get(GetIndexTab());
                this.SetIndexCaret(CurField.Lenth - 1);

            }

            break;
        }

        this.Caret.Pos.X = CurField.CaretAddr.Pos.X;
        this.Caret.Pos.Y = CurField.CaretAddr.Pos.Y;
        // this.Caret.Pos = CurField.CaretAddr.Pos;

        for (int i = 0; i < GetIndexCaret(); i++) {
            if (this.Caret.Pos.X >= this.RightMargin) {
                this.Caret.Pos.X = this.LeftMargin;
                this.Caret.Pos.Y++;
            } else {
                this.Caret.Pos.X++;


            }
        }
    }

    private int NextIndexTab() {
        int Cnt = FieldList.size();

        if (Cnt <= 0)
            return 0;

        mIndexTab++;
        mIndexTab %= Cnt;
        return mIndexTab;
    }

    private int FirstIndexTab() {
        int Cnt = FieldList.size();

        if (Cnt <= 0)
            return 0;

        mIndexTab = 0;
        return mIndexTab;
    }

    private int LastIndexTab() {
        int Cnt = FieldList.size();

        if (Cnt <= 0)
            return 0;

        mIndexTab = Cnt - 1;
        return mIndexTab;
    }

    private int PrevIndexTab() {
        int Cnt = FieldList.size();

        if (mIndexTab <= 0) {
            mIndexTab = Cnt - 1;
        } else
            mIndexTab--;

        return mIndexTab;
    }

    private int GetIndexCaret() {
        return mIndexCaret;
    }

    private void SetIndexCaret(int i) {
        mIndexCaret = i;
    }

    private void PlusIndexCaret() {
        mIndexCaret++;
    }

    private void PrvIndexCaret() {
        mIndexCaret--;
    }

    private void PrvIndexCaretZero() {
        mIndexCaret = 0;
    }

    private void CaretOn() {
        if (this.Caret.IsOff == false) {
            return;
        }

        this.Caret.IsOff = false;

    }

    private void CaretOff() {
        if (this.Caret.IsOff == true) {
            return;
        }

        this.Caret.IsOff = true;
    }

    private void CaretRight() {
        while (true) {
            IBM_FIELD CurField = (IBM_FIELD) FieldList.get(GetIndexTab());

            if (CurField.Lenth > this.GetIndexCaret()) {
                this.PlusIndexCaret();
                break;
            } else {
                NextIndexTab();
                this.SetIndexCaret(0);
            }
        }
        //UpDateActiveField();
        CaretUpdate();
    }

    private void CaretBegin() {
        if (FieldList.size() <= 0)
            return;
        this.SetIndexCaret(0);
        //UpDateActiveField();
        CaretUpdate();
    }

    private void CaretEnd() {
        if (FieldList.size() <= 0)
            return;

        IBM_FIELD CurField = (IBM_FIELD) FieldList.get(GetIndexTab());

        this.SetIndexCaret(CurField.Lenth - 1);


        //UpDateActiveField();
        CaretUpdate();
    }

    private void CaretLeft() {
        if (FieldList.size() <= 0)
            return;


        IBM_FIELD CurField = (IBM_FIELD) FieldList.get(GetIndexTab());

        if (this.GetIndexCaret() <= 0)//?p?G???_?w?b??@??,?e???eTAB?@?????
        {
            PrevIndexTab();
            CurField = (IBM_FIELD) FieldList.get(GetIndexTab());
            this.SetIndexCaret(CurField.Lenth - 1);

        } else {
            PrvIndexCaret();

        }

        //UpDateActiveField();
        CaretUpdate();
    }

    private void CaretBack() {
        if (FieldList.size() <= 0)
            return;

        IBM_FIELD CurField = (IBM_FIELD) FieldList.get(GetIndexTab());


        if (this.GetIndexCaret() <= 0)//?p?G???_?w?b??@??,?e???eTAB?@?????
        {

            PrevIndexTab();
            CurField = (IBM_FIELD) FieldList.get(GetIndexTab());
            this.SetIndexCaret(CurField.Lenth - 1);

        } else {
            PrvIndexCaret();

        }

        for (int i = this.GetIndexCaret(); i < CurField.Lenth; i++) {
            int next = i + 1;

            if (next == CurField.Lenth)
                CurField.Data[i] = (char) 0;
            else
                CurField.Data[i] = CurField.Data[next];

        }

        UpDateActiveField();
        CaretUpdate();
    }

    private void CaretDelete() {
        if (FieldList.size() <= 0)
            return;

        IBM_FIELD CurField = (IBM_FIELD) FieldList.get(GetIndexTab());
        //CurField.Data[this.GetIndexCaret()] = 0;

        for (int i = this.GetIndexCaret(); i < CurField.Lenth; i++) {
            int next = i + 1;

            if (next == CurField.Lenth)
                CurField.Data[i] = (char) 0;
            else
                CurField.Data[i] = CurField.Data[next];

        }


        UpDateActiveField();
        CaretUpdate();
    }

    private void TabToNextField() {
        NextIndexTab();
        this.SetIndexCaret(0);

        UpDateActiveField();
        CaretUpdate();
    }

    private void TabToPrevField() {
        PrevIndexTab();
        this.SetIndexCaret(0);

        UpDateActiveField();
        CaretUpdate();
    }

    private void TabToFirstField() {
        FirstIndexTab();
        this.SetIndexCaret(0);

        UpDateActiveField();
        CaretUpdate();
    }

    private void TabToLastField() {
        LastIndexTab();
        this.SetIndexCaret(0);

        UpDateActiveField();
        CaretUpdate();
    }

    private char ConvertAsciiToEBCD(int key) {
        // szEBCDIC
        char cRet = (char) 0xff;
        for (int i = 64; i < 255; i++) {
            if (szEBCDIC[i] == key) {
                cRet = (char) i;
                break;
            }
        }
        return cRet;
    }

    private void putString(String string) {
        //Todo: handle enter and tab
        for (int idxStr = 0; idxStr < string.length(); idxStr++) {
            if(PutAsciiKey(string.charAt(idxStr)) == false) {
                PlayWarningSounds();
                break;
            }
        }
    }

    private boolean PutAsciiKey(int KeyCode) {
        char Code = ConvertAsciiToEBCD(KeyCode);

        if (FieldList.size() < 1)
            return false;

        while (true) {
            IBM_FIELD CurField = FieldList.get(GetIndexTab());

            if (CurField.Lenth > this.GetIndexCaret()) {
                if (!CurField.Modified)
                    CurField.Modified = true;

                if (CurField.Bypass) {
                    PlayWarningSounds();
                    return false;
                }
                /*  000 Alphabetic shift.
    			001 Alphabetic only. (1)
    			010 Numeric shift.(2)
    			011 Numeric only. (3)
    			100 Katakana shift. (4)
    			101 Reserved. (5)
    			110 (6)I/O (magnetic stripe can turn on the master MDT bit by placing a field
    			    reader, selector light format word with bit 4 on in the display data stream.
    			    pen input only).
                111 (7)Signed numeric.*/
                switch (CurField.ShiftEdit) {
                    case 0:
                        break;
                    case 1:
                        if (!Character.isLetter(KeyCode) && KeyCode != ',' && KeyCode != '.' && KeyCode != '-' && KeyCode != ' ') {
                            PlayWarningSounds();
                            return false;
                        }
                    case 2:
                        break;
                    case 3:
                        if (!Character.isDigit(KeyCode) && KeyCode != '+' && KeyCode != ',' && KeyCode != '.' && KeyCode != ' ') {
                            PlayWarningSounds();
                            return false;
                        }
                    case 4:
                        break;
                    case 5:
                    case 7:
                        if (!Character.isDigit(KeyCode)) {
                            PlayWarningSounds();
                            return false;
                        }
                    case 6:
                        break;

                }

                if (Character.isLetter(KeyCode)) {
                    if(CurField.Monocase || CipherConnectSettingInfo.getUpperCaseByIndex(CipherConnectSettingInfo.GetSessionIndex()) == true)
                        Code = ConvertAsciiToEBCD(Character.toUpperCase((char) KeyCode));
                }

                if (bInsert)
                    InsertChar(CurField);

                CurField.Data[this.GetIndexCaret()] = Code;

                FieldList.set(GetIndexTab(), CurField);
                this.PlusIndexCaret();
                break;
            } else {
                //todo: maybe handle auto enter
                NextIndexTab();
                this.SetIndexCaret(0);
            }
        }
        UpDateActiveField();
        CaretUpdate();
        return true;
    }

    public void InsertChar(IBM_FIELD Field) {
        int Caret = GetIndexCaret();
        int Index = Field.Lenth - 1;

        while (Index > Caret) {
            Field.Data[Index] = Field.Data[Index - 1];
            Index--;
        }
    }

    @Override
    public void OnScreenBufferPos(int x, int y) {
        Ibm_Caret cCaret = new Ibm_Caret();

        for (int Tab = 0; Tab < FieldList.size(); Tab++) {
            IBM_FIELD Field = (IBM_FIELD) FieldList.get(Tab);

            cCaret.Pos.X = Field.CaretAddr.Pos.X;
            cCaret.Pos.Y = Field.CaretAddr.Pos.Y;

            for (int Cnt = 0; Cnt < Field.Lenth; Cnt++) {
                if (cCaret.Pos.X == x && cCaret.Pos.Y == y) {
                    SetIndexTab(Tab);
                    SetIndexCaret(Cnt);
                    CaretUpdate();
                    ViewPostInvalidate();
                    return;
                }

                if (cCaret.Pos.X >= this.RightMargin) {
                    cCaret.Pos.X = this.LeftMargin;
                    cCaret.Pos.Y++;
                } else {
                    cCaret.Pos.X++;
                }
            }
        }
    }

    @Override
    public void handleBarcodeFire(String barcodeOriginal) {
        IBM_FIELD cField = GetCurrentField();
        if (cField == null)
            return;

        if(cField.Lenth >= barcodeOriginal.length()) {
            CaretBegin();
            putString(barcodeOriginal);
        } else { // check field length
            int nFDLEN = CipherConnectSettingInfo.getCheckFieldLength(CipherConnectSettingInfo.GetSessionIndex());
            switch (nFDLEN) {
                case CipherConnectSettingInfo.FDLEN_REJECT:
                    PlayWarningSounds();
                    UIUtility.messageBox(R.string.MSG_FDChek_reject);
                    break;
                case CipherConnectSettingInfo.FDLEN_TRUN:
                    CaretBegin();
                    putString(barcodeOriginal.substring(0, cField.Lenth));
                    break;
                case CipherConnectSettingInfo.FDLEN_SPLT:
                    CaretBegin();
                    putString(barcodeOriginal);
                    break;
            }
        }

        ViewPostInvalidate();
    }

    @Override
    public void handleKeyDown(int keyCode, KeyEvent event) {
        if(bKeybaordLock == true) {
            if(CipherConnectSettingInfo.getIsIBMAutoUnlock(CipherConnectSettingInfo.GetSessionIndex()) == false) {
                return;
            }
            bKeybaordLock = false;
        }

        boolean Func = false;
        char pressedKey = (char) event.getUnicodeChar();

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                CaretLeft();
                Func = true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                CaretRight();
                Func = true;
                break;
            case KeyEvent.KEYCODE_TAB:
                TabToNextField();
                Func = true;
                break;
            case KeyEvent.KEYCODE_SPACE:
                PutAsciiKey(0x20);
                Func = true;

                break;
            case KeyEvent.KEYCODE_ENTER:

                ProcessIbmEnter();
                Func = true;
                break;
            case KeyEvent.KEYCODE_BACK:
                ProcessIbmEnter();
                Func = true;
                break;
            case KeyEvent.KEYCODE_DEL:
                CaretBack();
                Func = true;
                break;
            case KeyEvent.KEYCODE_F1:
                ProcessFunctionKey(IBmAID.F1);
                Func = true;
                break;

            case KeyEvent.KEYCODE_F2:
                ProcessFunctionKey(IBmAID.F2);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F3:
                ProcessFunctionKey(IBmAID.F3);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F4:
                ProcessFunctionKey(IBmAID.F4);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F5:
                ProcessFunctionKey(IBmAID.F5);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F6:
                ProcessFunctionKey(IBmAID.F6);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F7:
                ProcessFunctionKey(IBmAID.F7);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F8:
                ProcessFunctionKey(IBmAID.F8);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F9:
                ProcessFunctionKey(IBmAID.F9);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F10:
                ProcessFunctionKey(IBmAID.F10);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F11:
                ProcessFunctionKey(IBmAID.F11);
                Func = true;
                break;
            case KeyEvent.KEYCODE_F12:
                ProcessFunctionKey(IBmAID.F12);
                Func = true;
                break;
            case KEY_F13:
                ProcessFunctionKey(IBmAID.F13);
                Func = true;
                break;
            case KEY_F14:
                ProcessFunctionKey(IBmAID.F14);
                Func = true;
                break;
            case KEY_F15:
                ProcessFunctionKey(IBmAID.F15);
                Func = true;
                break;
            case KEY_F16:
                ProcessFunctionKey(IBmAID.F16);
                Func = true;
                break;
            case KEY_F17:
                ProcessFunctionKey(IBmAID.F17);
                Func = true;
                break;
            case KEY_F18:
                ProcessFunctionKey(IBmAID.F18);
                Func = true;
                break;
            case KEY_F19:
                ProcessFunctionKey(IBmAID.F19);
                Func = true;
                break;
            case KEY_F20:
                ProcessFunctionKey(IBmAID.F20);
                Func = true;
                break;
            case KEY_F21:
                ProcessFunctionKey(IBmAID.F21);
                Func = true;
                break;
            case KEY_F22:
                ProcessFunctionKey(IBmAID.F22);
                Func = true;
                break;
            case KEY_F23:
                ProcessFunctionKey(IBmAID.F23);
                Func = true;
                break;
            case KEY_F24:
                ProcessFunctionKey(IBmAID.F24);
                Func = true;
                break;

            //3270
            case IBMKEY_PA1:
            case IBMKEY_PA2:
            case IBMKEY_PA3:
                break;

            case IBMKEY_FPLUS:
            case IBMKEY_FMINUS: //N mod
                FieldPlus();
                Func = true;
                break;
            //EraseAfterCursor


            case IBMKEY_FEXIT:
                FieldExit();
                Func = true;
                break;
            case IBMKEY_ATTN:
                ProcessFunctionKeyAttn();
                Func = true;
                break;
            case IBMKEY_FBEGIN:
                CaretBegin();
                Func = true;
                break;
            case IBMKEY_DEL:
                CaretDelete();
                Func = true;
                break;
            case IBMKEY_FEND:
                CaretEnd();
                Func = true;
                break;
            case IBMKEY_FMARK://3270

            case IBMKEY_ERINPUT:
                NullFields(true);
                TabToFirstField();
                Func = true;
                break;

            case IBMKEY_SYSRQ:
                ProcessFunctionKeyRq();
                Func = true;
                break;
            case IBMKEY_LAST:
                TabToLastField();
                Func = true;
                break;
            case IBMKEY_NEXT:
                TabToNextField();
                Func = true;
                break;
            case IBMKEY_CLREOF:
                EraseEof();
                Func = true;
                break;
            case IBMKEY_ROLDN://3270
            case IBMKEY_ROLUP:
                break;

            case IBMKEY_PREV:
                TabToPrevField();
                Func = true;
                break;
            case IBMKEY_RECORD:
                ProcessFunctionKeyRecord();
                Func = true;
                break;
            //case IBMKEY_BKTAB ://3270
            case IBMKEY_DUP:
                Duplicatefield();
                Func = true;
                break;

            case IBMKEY_INS:
                bInsert = !bInsert;
                Func = true;
                break;
            case IBMKEY_RESET:
                bKeybaordLock = false;
                Func = true;
                break;
            case IBMKEY_HOME:
                TabToFirstField();
                Func = true;
                break;
            // case IBMKEY_NEWLINE :

            case IBMKEY_BS:
                PrvIndexCaret();

                CaretUpdate();
                CaretDelete();
                Func = true;
                break;

            case IBMKEY_CLR:
                break;

      /*
       * case KeyEvent.keycode_f1:
            ProcessFunctionKey(IBmAID.F13);
            break;
        case KeyEvent.KEYCODE_F14:
            ProcessFunctionKey(IBmAID.F14);
            break;
        case KeyEvent.KEYCODE_F15:
            ProcessFunctionKey(IBmAID.F15);
            break;
        case KeyEvent.KEYCODE_F16:
            ProcessFunctionKey(IBmAID.F16);
            break;
        case KeyEvent.KEYCODE_F17:
            ProcessFunctionKey(IBmAID.F17);
            break;
        case KeyEvent.KEYCODE_F18:
            ProcessFunctionKey(IBmAID.F18);
            break;
        case KeyEvent.KEYCODE_F19:
            ProcessFunctionKey(IBmAID.F19);
            break;
        case KeyEvent.KEYCODE_F20:
            ProcessFunctionKey(IBmAID.F20);
            break;

            */


        }

        if (!Func) {
            if (pressedKey == 0)
                return;
            PutAsciiKey(pressedKey);
        }

        ViewPostInvalidate();
    }

    private void FieldPlus() {
        //EraseAfterCursor
        IBM_FIELD cField = GetCurrentField();

        if (cField == null)
            return;

        cField.Modified = true;
        FieldExit();


    }

    private void FieldExit() {

        IBM_FIELD cField = GetCurrentField();

        if (cField == null)
            return;

        EraseAfterCursor();

        TabToNextField();


    }

    private void Duplicatefield() {

        IBM_FIELD cField = GetCurrentField();
        IBM_FIELD NextField = GetNextField();

        if (cField == null)
            return;
        int DupLen = 0;

        if (cField.Lenth >= NextField.Lenth)
            DupLen = cField.Lenth;
        else
            DupLen = NextField.Lenth;

        System.arraycopy(cField.Data, 0, NextField.Data, 0, DupLen);
        TabToNextField();


    }

    private void EraseEof() {

        IBM_FIELD cField = GetCurrentField();

        if (cField == null)
            return;

        EraseAfterCursor();

        UpDateActiveField();
        CaretUpdate();


    }

    private void ProcessIbmEnter() {
        String Data = PackInboundData(IBmAID.ENTER);
        byte[] OutData = ConverPackToRawData(Data);
        DispatchMessageRaw(this, OutData, OutData.length);
    }

    private void ProcessFunctionKey(IBmAID Fun) {
        String Data = PackInboundData(Fun);

        byte[] OutData = ConverPackToRawData(Data);
        DispatchMessageRaw(this, OutData, OutData.length);
    }

    private void ProcessFunctionKeyAttn() {
        String Data = PackContentAttn();

        byte[] OutData = ConverPackToRawData(Data);
        DispatchMessageRaw(this, OutData, OutData.length);
    }

    private void ProcessFunctionKeyRecord() {
        String Data = PackContentRecord();

        byte[] OutData = ConverPackToRawData(Data);
        DispatchMessageRaw(this, OutData, OutData.length);
    }

    private void ProcessFunctionKeyRq() {
        String Data = PackContentRequest();

        byte[] OutData = ConverPackToRawData(Data);
        DispatchMessageRaw(this, OutData, OutData.length);
    }

    // #{{ enums
    public enum IBmFieldSpc {
        None(0),
        Dispatch(1),
        Execute(2),
        Ignore(3),
        Collect(4),
        NewCollect(5);

        private final int value;

        private IBmFieldSpc(int intValue) {
            value = intValue;
        }

        public int getValue() {
            return value;
        }
    }

    public enum IBmOrders {
        None(0),
        Start_of_Header(1),
        Repeat_to_Address(2),
        Erase_to_Address(3),
        Set_Buffer_Address(17),
        Write_Extended_Attribute(18),
        Insert_Cursor(19),
        Move_Cursor(20),

        Start_of_Field(29);

        private final int value;

        private IBmOrders(int intValue) {
            value = intValue;
        }

        public static IBmOrders convert(byte val) {

            for (IBmOrders Val : IBmOrders.values()) {
                if (val == (byte) Val.getValue())
                    return Val;
            }

            throw new RuntimeException("Your byte " + val + " was not a backing value for MyNum.");
        }

        public int getValue() {
            return value;
        }
    }

    public enum IBmCommands {
        None(0),
        Read_Immediate(114),
        Read_Input_Fields(66),
        Read_MDT_Fields(82),
        Read_Screen(98),
        Save(2),
        Clear_Format_Table(80),
        Clear_Unit(64),
        Restore_Screen(18),
        Roll(35),
        Write_Error_Code(33),
        Write_Structured_Field(243),
        Write_to_Display(17);

        private final int value;

        private IBmCommands(int intValue) {
            value = intValue;
        }

        public static IBmCommands convert(byte val) {

            for (IBmCommands Val : IBmCommands.values()) {
                if (val == (byte) Val.getValue())
                    return Val;
            }

            throw new RuntimeException("Your byte " + val + " was not a backing value for MyNum.");
            // return IBmCommands.values()[value];
        }

        public static IBmCommands convert(char val) {

            for (IBmCommands Val : IBmCommands.values()) {
                if (val == (char) Val.getValue())
                    return Val;
            }

            throw new RuntimeException("Your byte " + val + " was not a backing value for MyNum.");
        }

        public int getValue() {
            return value;
        }

    }

    public enum IBmAID {
        None(0),
        F1(49),
        F2(50),
        F3(51),
        F4(52),
        F5(53),
        F6(54),
        F7(55),
        F8(56),
        F9(57),
        F10(58),
        F11(59),
        F12(60),
        F13(177),
        F14(178),
        F15(179),
        F16(180),
        F17(181),
        F18(182),
        F19(183),
        F20(184),
        F21(185),
        F22(186),
        F23(187),
        F24(188),
        CLEAR(189),
        ENTER(241),
        HELP(243),
        DOWN(244),
        UP(245),
        PRINT(246),
        RBS(248);

        private final int value;

        private IBmAID(int intValue) {
            value = intValue;
        }

        public int getValue() {
            return value;
        }
    }

    private enum IBmActions {
        None,
        Dispatch,
        Execute,
        Ignore,
        NewCollect,
        Param,
        OscStart,
        OscPut,
        OscEnd,
        Hook,
        Unhook,
        Put,
        Print,
        RecordLenth,
        RecordCommad,    //save command to mbyCommandBuf.
        ParseCommand,
        RecordCcode,
        RecordData,
        ParseData,
        OrdersBeging,
        OrdersRecord,
        OrdersParse,
        ParseSteamBegin,
        ControlCodeParse,
        Collect;
    }

    private enum Transitions {
        None,
        Entry,
        Exit;
    }

    private enum IBmStates {
        None,
        Ground,
        RecLenthStrat,
        RecLenthEnd,
        RecTypeStrat,
        RecTypeEnd,
        RecReverseStrat,
        RecReverseEnd,
        Header,
        Flags,
        Option,
        OptionEnd,
        Command,
        CommandEnd,
        CommandEndEx,
        CommandCcode,
        CommandCcode2,
        CommandCcodeEnd,
        Orders,
        OrdersEx,
        OrdersStart,
        OrdersEnd,
        Anywhere
    }

    private class IBmStateChangeEvents {
        private Tn_StateChangeInfo[] Elements = {
                new Tn_StateChangeInfo(IBmStates.None, Transitions.None, IBmActions.None),
                new Tn_StateChangeInfo(IBmStates.CommandEnd, Transitions.Entry, IBmActions.ParseCommand),
                new Tn_StateChangeInfo(IBmStates.Orders, Transitions.Entry, IBmActions.OrdersBeging),
                new Tn_StateChangeInfo(IBmStates.OrdersEx, Transitions.Entry, IBmActions.OrdersBeging),

                new Tn_StateChangeInfo(IBmStates.Ground, Transitions.Exit, IBmActions.ParseSteamBegin),
                new Tn_StateChangeInfo(IBmStates.Orders, Transitions.Exit, IBmActions.OrdersParse),
                new Tn_StateChangeInfo(IBmStates.OrdersEx, Transitions.Exit, IBmActions.OrdersParse),
                new Tn_StateChangeInfo(IBmStates.CommandCcodeEnd, Transitions.Exit, IBmActions.ControlCodeParse),
                new Tn_StateChangeInfo(IBmStates.CommandEndEx, Transitions.Exit, IBmActions.ParseData),
        };

        public IBmStateChangeEvents() {
        }

        public final boolean GetStateChangeAction(IBmStates State, Transitions Transition, AtomicReference<IBmActions> action) {
            Tn_StateChangeInfo Element;

            for (int i = 0; i < Elements.length; i++) {
                Element = Elements[i];

                if (State == Element.mState &&
                        Transition == Element.mTransition) {
                    action.set(Element.mAction);
                    return true;
                }
            }

            return false;
        }
    }

    private final class Tn_CharEventInfo {
        // in UC
        public IBmStates mPreState;
        public char mCharFrom;
        public char mCharTo;
        public IBmActions mAction;
        public IBmStates mState; // the state we are going to

        public Tn_CharEventInfo(IBmStates p1, char p2, char p3, IBmActions p4, IBmStates p5) {
            mPreState = p1;
            mCharFrom = p2;
            mCharTo = p3;
            mAction = p4;
            mState = p5;
        }
    }

    private final class Tn_StateChangeInfo {
        // in UC
        private IBmStates mState;
        private Transitions mTransition; // the next state we are going to
        private IBmActions mAction;

        public Tn_StateChangeInfo(IBmStates p1, Transitions p2, IBmActions p3) {
            mState = p1;
            mTransition = p2;
            mAction = p3;
        }
    }

    private class IBM_FIELD {
        // in UC
        public int Lenth;

        //bit 0~7
        public boolean Bypass;       //bit 2 from left
        public boolean Duplication;  //bit 3 from left
        public boolean Modified;     //bit 4 from left
        public byte ShiftEdit;     //bit 5~7 from left

        //bit 0~7
        public boolean AutoEnter;    //bit 0 from left
        public boolean FieldExit;    //bit 1 from left
        public boolean Monocase;     //bit 6 from left
        public boolean Mandatory;    //bit 4 from left
        public Ibm_Caret CaretAddr;
        public char[] Data;
        public char Attrib;
        public IBM_FIELD() {
        }
    }

    private class FieldArray extends java.util.ArrayList<IBM_FIELD> {
        boolean isNeedLine(int X, int Y) {
            for (int idxFiled = 0; idxFiled < this.size(); idxFiled++) {
                IBM_FIELD field = get(idxFiled);
                if(isScreenAttributeVisible((byte) field.Attrib) &&
                        Y == field.CaretAddr.Pos.Y &&
                        X >= field.CaretAddr.Pos.X && X < field.CaretAddr.Pos.X + field.Lenth) {
                    return true;
                }
            }
            return false;
        }

        public String BuildReadDataByIndex(int index, boolean SBA, boolean Replace) {
            IBM_FIELD Field;
            int X, Y;

            StringBuilder sb = new StringBuilder("");

            Field = this.get(index);
            X = Field.CaretAddr.Pos.X;
            Y = Field.CaretAddr.Pos.Y + 1;

            if (SBA) {
                sb.append((char) 0x11);
                sb.append((char) Y);
                sb.append((char) X);
            }

            for (int i = 0; i < Field.Lenth; i++) {
                if (Field.Data[i] == 0) {
                    if (Replace) {
                        //ConvertAsciiToEBCD((int)0x20);
                    } else
                        break;
                }


                sb.append((char) Field.Data[i]);
            }
            return sb.toString();

        }


    }
    /*
       override public void OnKeyInput(int KeyCode)
        {

           
            switch ((Keys)KeyCode)
            {
                case Keys.Tab:
                    TabToNextField();
                    break;
                case Keys.Right:
                    
                    CaretRight();
                    break;
                case Keys.Left:
                    CaretLeft();
                    break;
                case Keys.Return:
                    ProcessIbmEnter();
                    break;
                case Keys.Back:
                    CaretBack();
                    break;
                case Keys.Delete:
                    CaretDelete();
                    break;
                case Keys.F1:
                    ProcessFunctionKey(IBmAID.F1);
                    break;
                case Keys.F2:
                    ProcessFunctionKey(IBmAID.F2);
                    break;
                case Keys.F3:
                    ProcessFunctionKey(IBmAID.F3);
                    break;
                case Keys.F4:
                    ProcessFunctionKey(IBmAID.F4);
                    break;
                case Keys.F5:
                    ProcessFunctionKey(IBmAID.F5);
                    break;
                case Keys.F6:
                    ProcessFunctionKey(IBmAID.F6);
                    break;
                case Keys.F7:
                    ProcessFunctionKey(IBmAID.F7);
                    break;
                case Keys.F8:
                    ProcessFunctionKey(IBmAID.F8);
                    break;
                case Keys.F9:
                    ProcessFunctionKey(IBmAID.F9);
                    break;
                case Keys.F10:
                    ProcessFunctionKey(IBmAID.F10);
                    break;
                case Keys.F11:
                    ProcessFunctionKey(IBmAID.F11);
                    break;
                case Keys.F12:
                    ProcessFunctionKey(IBmAID.F12);
                    break;
                case Keys.F13:
                    ProcessFunctionKey(IBmAID.F13);
                    break;
                case Keys.F14:
                    ProcessFunctionKey(IBmAID.F14);
                    break;
                case Keys.F15:
                    ProcessFunctionKey(IBmAID.F15);
                    break;
                case Keys.F16:
                    ProcessFunctionKey(IBmAID.F16);
                    break;
                case Keys.F17:
                    ProcessFunctionKey(IBmAID.F17);
                    break;
                case Keys.F18:
                    ProcessFunctionKey(IBmAID.F18);
                    break;
                case Keys.F19:
                    ProcessFunctionKey(IBmAID.F19);
                    break;
                case Keys.F20:
                    ProcessFunctionKey(IBmAID.F20);
                    break;

                  


            }


        }
     
         override public void OnMouseDown(int x, int y)
        {
            Point CaretPos = _ViewContainer.CalculateCaretPos(x, y);

            Ibm_Caret cCaret = new Ibm_Caret();

            for (int Tab = 0; Tab < FieldList.Count; Tab++)
            {
                IBM_FIELD Field = (IBM_FIELD)FieldList[Tab];


                cCaret.Pos = Field.CaretAddr.Pos;

                for (int Cnt = 0; Cnt < Field.Lenth; Cnt++)
                {
                    if (cCaret.Pos.X == CaretPos.X && cCaret.Pos.Y == CaretPos.Y)
                    {
                        SetIndexTab(Tab);
                        SetIndexCaret(Cnt);
                        CaretUpdate();
                        return;

                    }

                    if (cCaret.Pos.X >= this.RightMargin)
                    {
                        cCaret.Pos.X = this.LeftMargin;
                        cCaret.Pos.Y++;
                    }
                    else
                    {
                        cCaret.Pos.X++;


                    }
                }

            }


        }
        
          override public Point GetCaret()
        {
            return new Point(this.Caret.Pos.X, this.Caret.Pos.Y);

        }
        
         override public void OnTelnetParseStringEnd()
        {
           if (IsNeedAutoLogon())
                CheckAutoLogin();


        }
        */
}
