package TelnetVT;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.KeyEvent;

import com.cipherlab.barcode.BuildConfig;
import com.example.terminalemulation.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import Terminals.CipherReaderControl;
import Terminals.TESettingsInfo;
import Terminals.TerminalLogWriter;
import Terminals.stdActivityRef;

//import TelnetIBM.IBMHost5250.IBmAID;
//import TelnetIBM.IBMHost5250.IBmActions;

/**
 * Created by Franco.Liu on 2014/1/7.
 */
//region Description

//sample

//endregion

public class CVT100 extends CVT100Enum {

    //region Field
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
    static final int VKEY_DEL = -112;
    static final int VKEY_BS = -113;
    static final int VKEY_LF = -115;
    static final int VKEY_FIND = -116;
    static final int VKEY_SELECT = -117;
    static final int VKEY_Rmv = -118;


    //endregion
    //region interface
    static final int VKEY_INS = -119;
    static final int VKEY_PREV = -120;
    static final int VKEY_NEXT = -121;
    public int Rows;
    public int Columns;
    public String Username;
    public String Password;
    VtParserEvent vtParserEvent = new VtParserEvent();
    private uc_Parser Parser = null;
    private int TopMargin;
    private int BottomMargin;
    private java.util.ArrayList<uc_CaretAttribs> SavedCarets = new java.util.ArrayList<uc_CaretAttribs>();
    private java.util.ArrayList<KeyEventVal> LineBufferList = new java.util.ArrayList<KeyEventVal>();
    private uc_TabStops TabStops = null;  //defined inbase
    private CharAttribStruct[][] AttribGrid = null;//defined inbase
    private CharAttribStruct CharAttribs = new CharAttribStruct();
    private uc_Caret Caret;
    private uc_Chars G0;
    //endregion
    //region  Tab
    private uc_Chars G1;
    private uc_Chars G2;
    private uc_Chars G3;
    private uc_Chars EmUc_Chars;
    private uc_Mode Modes;

    //endregion
    //region CVT100 Member
    public CVT100() {
        this.Parser = new uc_Parser();
        this.Parser.UcParserEvent = vtParserEvent;

        this.G0 = new uc_Chars(Sets.ASCII);
        this.G1 = new uc_Chars(Sets.ASCII);
        this.G2 = new uc_Chars(Sets.DECSG);
        this.G3 = new uc_Chars(Sets.DECSG);
        this.EmUc_Chars = new uc_Chars(Sets.ASCII);

        this.CharAttribs.GL = this.G0;
        this.CharAttribs.GR = this.G2;

        this.SetSize(25, 80);
        this.Caret = new uc_Caret();
        this.Modes = new uc_Mode();
        this.TabStops = new uc_TabStops();
        this.LineBufferList.clear();
    }

    public String GetLogTitle() {
        return "VT";
    }

    public String GetTerminalTypeName() {
        return "vt100";
    }

    @Override
    public void processChar(char ch) {
        Parser.processChar(ch);
    }

    @Override
    protected boolean autoLogin() {
        String loginNameProm = TESettingsInfo.getHostLoginPromtByIndex(TESettingsInfo.getSessionIndex());
        String pwdProm = TESettingsInfo.getHostPassWordPromtByIndex(TESettingsInfo.getSessionIndex());
        boolean bHasNameProm = false, bHasPwdProm = false;
        //Parse content to see if loginNameProm and pwdProm exist or not
        for (int idxRow = 0; idxRow < CharGrid.length; idxRow++) {
            String strRow = String.valueOf(CharGrid[idxRow]);
            if (strRow.toLowerCase().contains(loginNameProm.toLowerCase()))
                bHasNameProm = true;
            if (strRow.toLowerCase().contains(pwdProm.toLowerCase()))
                bHasPwdProm = true;
        }
        if (bHasNameProm == false && bHasPwdProm == false)
            return false;

        int nTerm = TESettingsInfo.getHostTermLoginByIndex(TESettingsInfo.getSessionIndex());
        String sendString = "";
        if (bHasNameProm) {
            String loginName = TESettingsInfo.getHostUserNameByIndex(TESettingsInfo.getSessionIndex());
            if (nTerm == TESettingsInfo.TERM_TAB) {
                sendString = loginName + "\t";
            } else {
                sendString = loginName + "\r\n";
            }
        }
        if (bHasPwdProm) {
            String loginPwd = TESettingsInfo.getHostPassWordByIndex(TESettingsInfo.getSessionIndex());
            sendString = sendString + loginPwd + "\r\n";
        }

        if (sendString.length() > 0)
            DispatchMessage(this, sendString);
        return true;
    }

    @Override
    public Point getCursorGridPos() {
        return Caret.Pos;
    }

    //endregion
    //region  TelnetInterpreter
    public void ParseEnd() {

    }

    public void drawAll() {
        for (int idxRow = this.TopMargin; idxRow < this.BottomMargin; idxRow++) {
            for (int idxCol = 0; idxCol < this._cols; ++idxCol) {
                if (this.AttribGrid[idxRow][idxCol] == null) {
                    DrawChar(this.CharGrid[idxRow][idxCol], idxCol, idxRow, false, false);
                }
                else {
                    DrawChar(this.CharGrid[idxRow][idxCol], idxCol, idxRow, this.AttribGrid[idxRow][idxCol].IsBold, this.AttribGrid[idxRow][idxCol].IsUnderscored);
                }
            }
        }
    }

    private void SetSize(int Rows, int Columns) {
        this._rows = Rows;
        this._cols = Columns;

        this.TopMargin = 0;
        this.BottomMargin = Rows - 1;

        this.CharGrid = new char[Rows][];

        for (int i = 0; i < this.CharGrid.length; i++) {
            this.CharGrid[i] = new char[Columns];
        }

        this.AttribGrid = new CharAttribStruct[Rows][];

        for (int i = 0; i < this.AttribGrid.length; i++) {
            this.AttribGrid[i] = new CharAttribStruct[Columns];
            for (int y = 0; y < Columns; y++) {
                this.AttribGrid[i][y] = new CharAttribStruct();

            }

        }
    }

    private void CheckCostomCommand(ParserEventArgs e)//not finish
    {
        Context context = stdActivityRef.GetCurrActivity().getApplicationContext();
        String StrCmd = GetActionString(e);


        String strGood = TESettingsInfo.getHostGoodfeedbackCmdByIndex(TESettingsInfo.getSessionIndex());
        if (strGood != null && strGood.isEmpty() == false) {
            if (StrCmd.equals(strGood)) {

                SoundPool soundPool;
                soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
                soundPool.load(context, R.raw.good, 1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //soundPool.play(1, 0.99f, 0.1f, 0, -1, 0.7f);
                soundPool.play(1, 1, 1, 0, 0, 1);
                stdActivityRef.ApplicationVibration();

            }
        }

        String strErr = TESettingsInfo.getHostErrorfeedbackCmdByIndex(TESettingsInfo.getSessionIndex());
        if (strErr != null && strErr.isEmpty() == false) {
            if (StrCmd.equals(strErr)) {
                SoundPool soundPool;
                soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
                soundPool.load(context, R.raw.bad, 1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //soundPool.play(1, 0.99f, 0.1f, 0, -1, 0.7f);
                soundPool.play(1, 1, 1, 0, 0, 1);
                stdActivityRef.ApplicationVibration();
            }
        }

        String strReader = TESettingsInfo.getHostEnableReaderCmdByIndex(TESettingsInfo.getSessionIndex());
        if (strReader != null && strReader.isEmpty() == false) {
            if (StrCmd.equals(strReader)) {
                SoundPool soundPool;
                soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
                soundPool.load(context, R.raw.enable, 1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //soundPool.play(1, 0.99f, 0.1f, 0, -1, 0.7f);
                soundPool.play(1, 1, 1, 0, 0, 1);
                stdActivityRef.ApplicationVibration();
                CipherReaderControl.SetActived(true);

            }
        }

        String strDisableReader = TESettingsInfo.getHostDisableReaderCmdByIndex(TESettingsInfo.getSessionIndex());
        if (strDisableReader != null && strDisableReader.isEmpty() == false) {
            if (StrCmd.equals(strDisableReader)) {
                SoundPool soundPool;
                soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
                soundPool.load(context, R.raw.disable, 1);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                //soundPool.play(1, 0.99f, 0.1f, 0, -1, 0.7f);
                soundPool.play(1, 1, 1, 0, 0, 1);
                stdActivityRef.ApplicationVibration();
                CipherReaderControl.SetActived(false);

            }
        }


    }

    private String GetActionString(ParserEventArgs e)//not finish
    {
        String Sequence = "";
        String strParm = "";


        for (int i = 0; i < e.CurParams.Elements.size(); i++) {
            strParm += e.CurParams.Elements.get(i);
        }

        if (strParm.length() > 0) {
            int IntParm = Integer.parseInt(strParm);
            strParm = String.valueOf(IntParm);
        }

        if (e.CurSequence.startsWith("\u001b[")) // CUD
        {
            if (e.CurSequence.length() == 3)
                Sequence = "\u001b[" + strParm + e.CurSequence.charAt(2);
        } else if (e.CurSequence.startsWith("\u001b")) {
            if (e.CurSequence.length() == 2)
                Sequence = "\u001b" + strParm + e.CurSequence.charAt(1);
        }

        return Sequence;

    }

    private void CommandRouter(Object Sender, ParserEventArgs e)//not finish
    {
        Character MultiChar;
        switch (e.Action) {
            case Print:

                MultiChar = TryGetMultiChar();

                if (MultiChar != null)
                    this.PrintChar(MultiChar);
                else
                    this.PrintChar(e.CurChar);

                break;

            case Execute:
                this.ExecuteChar(e.CurChar);
                break;

            case Dispatch:
                break;

            default:
                break;
        }

        int Param = 0;

        int Inc = 1; // increment

        //region  Switch Sequence

        if (e.CurSequence.equals("")) {
        } else if (e.CurSequence.equals("\u001b" + "7"))//DECSC Save Cursor position and attributes//DECSC Save Cursor position and attributes
        {
            this.SavedCarets.add(new uc_CaretAttribs(this.Caret.Pos, this.G0.Set.Get(), this.G1.Set.Get(), this.G2.Set.Get(), this.G3.Set.Get(), this.CharAttribs));
        } else if (e.CurSequence.equals("\u001b" + "8")) //DECRC Restore Cursor position and attributes
        {
            this.Caret.Pos = ((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).Pos;
            this.CharAttribs = ((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).Attribs;

            this.G0.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G0Set);
            this.G1.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G1Set);
            this.G2.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G2Set);
            this.G3.Set.Set(((uc_CaretAttribs) this.SavedCarets.get(this.SavedCarets.size() - 1)).G3Set);

            this.SavedCarets.remove(this.SavedCarets.size() - 1);

        } else if (e.CurSequence.equals("\u001b~")) //LS1R Locking Shift G1 -> GR
        {
            this.CharAttribs.GR = G1;
        } else if (e.CurSequence.equals("\u001bn")) //LS2 Locking Shift G2 -> GL
        {
            this.CharAttribs.GL = G2;
        } else if (e.CurSequence.equals("\u001b}")) //LS2R Locking Shift G2 -> GR
        {
            this.CharAttribs.GR = G2;
        } else if (e.CurSequence.equals("\u001bo")) //LS3 Locking Shift G3 -> GL
        {
            this.CharAttribs.GL = G3;
        } else if (e.CurSequence.equals("\u001b|")) //LS3R Locking Shift G3 -> GR
        {
            this.CharAttribs.GR = G3;
        } else if (e.CurSequence.equals("\u001b#8")) //DECALN
        {
            e.CurParams.Elements.add("1");
            e.CurParams.Elements.add(String.valueOf(this._rows));


            // put E's on the entire screen
            for (int y = 0; y < this._rows; y++) {
                this.CaretToAbs(y, 0);

                for (int x = 0; x < this._cols; x++) {
                    this.PrintChar('E');
                }
            }
        } else if (e.CurSequence.equals("\u001b=")) // Keypad to Application mode
        {
            this.Modes.Flags = this.Modes.Flags | Modes.KeypadAppln;
        } else if (e.CurSequence.equals("\u001b>")) // Keypad to Numeric mode
        {
            this.Modes.Flags = this.Modes.Flags ^ Modes.KeypadAppln;
        } else if (e.CurSequence.equals("\u001b[B")) // CUD
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);

            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y + Inc, this.Caret.Pos.X);
        } else if (e.CurSequence.equals("\u001b[A")) // CUU
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);
            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y - Inc, this.Caret.Pos.X);
        } else if (e.CurSequence.equals("\u001b[C")) // CUF
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);
            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y, this.Caret.Pos.X + Inc);
        } else if (e.CurSequence.equals("\u001b[D")) // CUB
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Inc = Integer.valueOf(str);
            }

            if (Inc == 0) {
                Inc = 1;
            }

            this.CaretToAbs(this.Caret.Pos.Y, this.Caret.Pos.X - Inc);
        } else if (e.CurSequence.equals("\u001b[H") || e.CurSequence.equals("\u001b[f")) // CUP
        {

            int X = 0;
            int Y = 0;

            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Y = Integer.valueOf(str) - 1;

            }

            if (e.CurParams.Count() > 1) {
                String str = (String) e.CurParams.Elements.get(1);
                X = Integer.valueOf(str) - 1;

            }

            if (Y > 20)
                this.CaretToRel(Y, X);
            else
                this.CaretToRel(Y, X);
        } else if (e.CurSequence.equals("\u001b[J")) {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            this.ClearDown(Param);

        } else if (e.CurSequence.equals("\u001b[K")) {

            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
            }

            this.ClearRight(Param);
        } else if (e.CurSequence.equals("\u001b[L"))  // INSERT LINE
        {
            this.InsertLine(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[M")) // DELETE LINE
        {
            this.DeleteLine(e.CurParams);
        } else if (e.CurSequence.equals("\u001bN")) // SS2 Single Shift (G2 -> GL)
        {
            this.CharAttribs.GS = this.G2;
        } else if (e.CurSequence.equals("\u001bO")) // SS3 Single Shift (G3 -> GL)
        {
            this.CharAttribs.GS = this.G3;
            //System.Console.WriteLine ("SS3: GS = {0}", this.CharAttribs.GS);
        } else if (e.CurSequence.equals("\u001b[m")) {
            this.SetCharAttribs(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[?h")) {
            //this.SetqmhMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[?l")) {
            //this.SetqmlMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[c")) // DA Device Attributes
        {
            //                    this.DispatchMessage (this, "\x1b[?64;1;2;6;7;8;9c");
            this.DispatchMessage(this, "\u001b[?6c");
        } else if (e.CurSequence.equals("\u001b[g")) {
            this.ClearTabs(e.CurParams);
        } else if (e.CurSequence.equals("\u001B[h")) {
            //this.SethMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[l")) {
            // this.SetlMode(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[r"))// DECSTBM Set Top and Bottom Margins
        {
            //this.SetScrollRegion(e.CurParams);
        } else if (e.CurSequence.equals("\u001b[t")) // DECSLPP Set Lines Per Page
        {

            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            if (Param > 0) {
                this.SetSize(Param, this._cols);
            }

        } else if (e.CurSequence.equals("\u001b" + "D")) // IND
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            this.Index(Param);
        } else if (e.CurSequence.equals("\u001b" + "E")) // NEL
        {
            this.LineFeed();
            this.CarriageReturn();
        } else if (e.CurSequence.equals("\u001bH")) // HTS
        {
            this.TabSet();
        } else if (e.CurSequence.equals("\u001bM"))// RI
        {
            if (e.CurParams.Count() > 0) {
                String str = (String) e.CurParams.Elements.get(0);
                Param = Integer.valueOf(str);
                //Param = (int)(e.CurParams.Elements[0]);
            }

            this.ReverseIndex(Param);
        }

        CheckCostomCommand(e);
        //endregion


        if (e.CurSequence.startsWith("\u001b(")) {
            this.SelectCharSet(this.G0.Set, e.CurSequence.substring(2));
        } else if (e.CurSequence.startsWith("\u001b-") || e.CurSequence.startsWith("\u001b)")) {
            this.SelectCharSet(this.G1.Set, e.CurSequence.substring(2));
        } else if (e.CurSequence.startsWith("\u001b.") || e.CurSequence.startsWith("\u001b*")) {
            this.SelectCharSet(this.G2.Set, e.CurSequence.substring(2));
        } else if (e.CurSequence.startsWith("\u001b/") || e.CurSequence.startsWith("\u001b+")) {
            this.SelectCharSet(this.G3.Set, e.CurSequence.substring(2));
        }

    }

    private void ClearCharAttribs() {
        this.CharAttribs.IsBold = false;
        this.CharAttribs.IsDim = false;
        this.CharAttribs.IsUnderscored = false;
        this.CharAttribs.IsBlinking = false;
        this.CharAttribs.IsInverse = false;
        this.CharAttribs.IsPrimaryFont = false;
        this.CharAttribs.IsAlternateFont = false;
        this.CharAttribs.UseAltBGColor = false;
        this.CharAttribs.UseAltColor = false;
        this.CharAttribs.AltColor = Color.WHITE;
        this.CharAttribs.AltBGColor = Color.BLACK;
    }

    private void SetCharAttribs(uc_Params CurParams) {
        if (CurParams.Count() < 1) {
            this.ClearCharAttribs();
            return;
        }

        for (int i = 0; i < CurParams.Count(); i++) {
            String str = (String) CurParams.Elements.get(i);
            int c = Integer.valueOf(str);
            switch (c) {
                case 0:
                    this.ClearCharAttribs();
                    break;

                case 1:
                    this.CharAttribs.IsBold = true;
                    break;

                case 4:
                    this.CharAttribs.IsUnderscored = true;
                    break;

                case 5:
                    this.CharAttribs.IsBlinking = true;
                    break;

                case 7:
                    this.CharAttribs.IsInverse = true;
                    break;

                case 22:
                    this.CharAttribs.IsBold = false;
                    break;

                case 24:
                    this.CharAttribs.IsUnderscored = false;
                    break;

                case 25:
                    this.CharAttribs.IsBlinking = false;
                    break;

                case 27:
                    this.CharAttribs.IsInverse = false;
                    break;

                case 30:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.BLACK;
                    break;

                case 31:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.RED;
                    break;

                case 32:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.GREEN;
                    break;

                case 33:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.YELLOW;
                    break;

                case 34:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.BLUE;
                    break;

                case 35:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.MAGENTA;
                    break;

                case 36:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.CYAN;
                    break;

                case 37:
                    this.CharAttribs.UseAltColor = true;
                    this.CharAttribs.AltColor = Color.WHITE;
                    break;
                case 40:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.BLACK;
                    break;

                case 41:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.RED;
                    break;

                case 42:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.GREEN;
                    break;

                case 43:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.YELLOW;
                    break;

                case 44:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.BLUE;
                    break;

                case 45:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.MAGENTA;
                    break;

                case 46:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.CYAN;
                    break;

                case 47:
                    this.CharAttribs.UseAltBGColor = true;
                    this.CharAttribs.AltBGColor = Color.WHITE;
                    break;

                default:
                    break;
            }
        }
    }

    private void ExecuteChar(char CurChar) {
        switch (CurChar) {
            case '\u0005':
                this.DispatchMessage(this, "vt220");
                break;

            case '\u0007':

                break;

            case '\u0008':
                this.CaretLeft();
                break;

            case '\u0009':
                this.Tab();
                break;

            case 10:// 0A
            case '\u000B':
            case '\u000C':
            case '\u0084':
                this.LineFeed();
                break;

            case 13:
                this.CarriageReturn();
                break;

            case '\u000E':
                this.CharAttribs.GL = this.G1;
                break;

            case '\u000F':
                this.CharAttribs.GL = this.G0;
                break;

            case '\u0011':
                this.DispatchMessage(this, "");
                break;

            case '\u0013':
                break;

            case '\u0085':
                this.LineFeed();
                this.CaretToAbs(this.Caret.Pos.Y, 0);
                break;

            case '\u0088':
                this.TabSet();
                break;

            case '\u008D':
                this.ReverseLineFeed();
                break;

            case '\u008E':
                this.CharAttribs.GS = this.G2;
                break;

            case '\u008F':
                this.CharAttribs.GS = this.G3;
                break;

            default:
                break;
        }
    }

    private void SelectCharSet(SetsVal CurTarget, String CurString) {
        if (CurString.equals("B"))
            CurTarget.Set(Sets.ASCII);
        else if (CurString.equals("%5"))
            CurTarget.Set(Sets.DECS);
        else if (CurString.equals("0"))
            CurTarget.Set(Sets.DECSG);
        else if (CurString.equals(">"))
            CurTarget.Set(Sets.DECTECH);
        else if (CurString.equals("<"))
            CurTarget.Set(Sets.DECSG);
        else if (CurString.equals("A")) {
            if ((this.Modes.Flags & Modes.National) == 0) {
                CurTarget.Set(Sets.ISOLatin1S);
            } else {
                CurTarget.Set(Sets.NRCUK);
            }
        } else if (CurString.equals("4") || CurString.equals("5"))
            CurTarget.Set(Sets.NRCFinnish);
        else if (CurString.equals("R"))
            CurTarget.Set(Sets.NRCFrench);
        else if (CurString.equals("9"))
            CurTarget.Set(Sets.NRCFrenchCanadian);
        else if (CurString.equals("K"))
            CurTarget.Set(Sets.NRCGerman);
        else if (CurString.equals("Y"))
            CurTarget.Set(Sets.NRCItalian);
        else if (CurString.equals("6"))
            CurTarget.Set(Sets.NRCNorDanish);
        else if (CurString.equals("'"))
            CurTarget.Set(Sets.NRCNorDanish);
        else if (CurString.equals("%6"))
            CurTarget.Set(Sets.NRCPortuguese);
        else if (CurString.equals("Z"))
            CurTarget.Set(Sets.NRCSpanish);
        else if (CurString.equals("7"))
            CurTarget.Set(Sets.NRCSwedish);
        else if (CurString.equals("="))
            CurTarget.Set(Sets.NRCSwiss);
    }

    //region  PrintToBmp
    private void PrintChar(char CurChar)//not finish
    {

        if (this.Caret.EOL == true) {
            //if ((this.Modes.Flags & Modes.AutoWrap) == Modes.AutoWrap)
            {
                this.LineFeed();
                this.CarriageReturn();

            }
        }

        int X = this.Caret.Pos.X;
        int Y = this.Caret.Pos.Y;

        this.AttribGrid[Y][X] = this.CharAttribs;

        if (this.CharAttribs.GS != null) {
            CurChar = EmUc_Chars.Get(CurChar, this.AttribGrid[Y][X].GS.Set.Get(), this.AttribGrid[Y][X].GR.Set.Get());

            if (this.CharAttribs.GS.Set.Get() == Sets.DECSG) {
                this.AttribGrid[Y][X].IsDECSG = true;
            }

            this.CharAttribs.GS = null;
        } else {
            CurChar = EmUc_Chars.Get(CurChar, this.AttribGrid[Y][X].GL.Set.Get(), this.AttribGrid[Y][X].GR.Set.Get());

            if (this.CharAttribs.GL.Set.Get() == Sets.DECSG) {
                this.AttribGrid[Y][X].IsDECSG = true;
            }
        }

        this.CharGrid[Y][X] = CurChar;
        if (CurChar != 0x20 && CurChar != 0) {
            int po = 0;
        }
        DrawChar(CurChar, X, Y, this.CharAttribs.IsBold, this.CharAttribs.IsUnderscored);
        this.CaretRight();
    }

    private void Tab() {
        for (int i = 0; i < this.TabStops.Columns.length; i++) {
            if (i > this.Caret.Pos.X && this.TabStops.Columns[i] == true) {
                this.CaretToAbs(this.Caret.Pos.Y, i);
                return;
            }
        }

        this.CaretToAbs(this.Caret.Pos.Y, this._cols - 1);
        return;
    }

    private void TabSet() {
        this.TabStops.Columns[this.Caret.Pos.X] = true;
    }

    //endregion
    //region  Clear
    private void ClearRight(int Param) {
        int X = this.Caret.Pos.X;
        int Y = this.Caret.Pos.Y;

        switch (Param) {
            case 0: // from cursor to end of line inclusive

                //Arrays.fill(this.CharGrid[Y], X, this.CharGrid[Y].length - X,0);
                Arrays.fill(this.CharGrid[Y], X, this.AttribGrid[Y].length - X, (char) 0);
                for (int i = 0; i < this.AttribGrid[Y].length - X; i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][X + i] = Obj;
                }
                //Arrays.fill(this.AttribGrid[Y], X, this.AttribGrid[Y].length - X,Obj);
                break;

            case 1: // from beginning to cursor inclusive

                Arrays.fill(this.CharGrid[Y], 0, X + 1, (char) 0);

                for (int i = 0; i < (X + 1); i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][i] = Obj;
                }
                //Arrays.fill(this.AttribGrid[Y], 0, X + 1,0);

                break;

            case 2: // entire line

                //System.Array.Clear(this.CharGrid[Y], 0, this.CharGrid[Y].getLength());
                Arrays.fill(this.CharGrid[Y], (char) 0);
                for (int i = 0; i < (this.AttribGrid[Y].length); i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][i] = Obj;
                }
                Arrays.fill(this.AttribGrid[Y], 0, this.AttribGrid[Y].length, 0);
                break;

            default:
                break;
        }
    }

    private void ClearDown(int Param) {
        int X = this.Caret.Pos.X;
        int Y = this.Caret.Pos.Y;

        switch (Param) {
            case 0: // from cursor to bottom inclusive

                Arrays.fill(this.CharGrid[Y], X, this.CharGrid[Y].length, (char) 0);
                //Arrays.fill(this.AttribGrid[Y], X, this.AttribGrid[Y].length - X,0);
                for (int i = 0; i < this.AttribGrid[Y].length - X; i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][X + i] = Obj;
                }


                ViewDrawSpace(X, Y, this.CharGrid[Y].length - X);
                for (int i = Y + 1; i < this._rows; i++) {
                    //Arrays.asList(this.CharGrid[i]).clear();
                    Arrays.fill(this.CharGrid[i], (char) 0);
                    //Arrays.fill(this.AttribGrid[i], 0, this.AttribGrid[i].length,0);
                    ViewDrawSpace(0, i, this.CharGrid[i].length);
                }
                break;

            case 1: // from top to cursor inclusive

                //Arrays.asList(this.CharGrid[Y]).subList(0, X + 1).clear();
                Arrays.fill(this.CharGrid[Y], 0, X + 1, (char) 0);
                // Arrays.fill(this.AttribGrid[Y], 0, X + 1,0);
                for (int i = 0; i < (X + 1); i++) {
                    CharAttribStruct Obj = new CharAttribStruct();
                    this.AttribGrid[Y][i] = Obj;
                }
                ViewDrawSpace(0, Y, X + 1);
                for (int i = 0; i < Y; i++) {
                    //Arrays.asList(this.CharGrid[i]).clear();
                    Arrays.fill(this.CharGrid[i], (char) 0);
                    //Arrays.fill(this.AttribGrid[i], 0, this.AttribGrid[i].length,0);
                    ViewDrawSpace(0, i, this.CharGrid[i].length);
                }
                break;

            case 2: // entire screen

                for (int i = 0; i < this._rows; i++) {
                    Arrays.fill(this.CharGrid[i], (char) 0);
                }

                ViewClear();
                break;

            default:
                break;
        }
    }

    private void ClearTabs(uc_Params CurParams) // TBC
    {
        int Param = 0;

        if (CurParams.Count() > 0) {
            String str = (String) CurParams.Elements.get(0);
            Param = Integer.valueOf(str);

        }

        switch (Param) {
            case 0: // Current Position
                this.TabStops.Columns[this.Caret.Pos.X] = false;
                break;

            case 3: // All Tabs
                for (int i = 0; i < this.TabStops.Columns.length; i++) {
                    this.TabStops.Columns[i] = false;
                }
                break;

            default:
                break;
        }
    }

    //endregion
    //region  Lines
    private void ReverseLineFeed() {

        // if we're at the top of the scroll region (top margin)
        if (this.Caret.Pos.Y == this.TopMargin) {
            // we need to add a new line at the top of the screen margin
            // so shift all the rows in the scroll region down in the array and
            // insert a new row at the top

            int i;


            for (i = this.BottomMargin; i > this.TopMargin; i--) {
                this.CharGrid[i] = this.CharGrid[i - 1];
                this.AttribGrid[i] = this.AttribGrid[i - 1];
            }

            this.CharGrid[this.TopMargin] = new char[this._cols];

            this.AttribGrid[this.TopMargin] = new CharAttribStruct[this._cols];
        }

        this.CaretUp();
    }

    private void InsertLine(uc_Params CurParams) {

        // if we're not in the scroll region then bail
        if (this.Caret.Pos.Y < this.TopMargin || this.Caret.Pos.Y > this.BottomMargin) {
            return;
        }

        int NbrOff = 1;

        if (CurParams.Count() > 0) {
            String str = (String) CurParams.Elements.get(0);
            NbrOff = Integer.valueOf(str);
        }

        while (NbrOff > 0) {

            // Shift all the rows from the current row to the bottom margin down one place
            for (int i = this.BottomMargin; i > this.Caret.Pos.Y; i--) {
                this.CharGrid[i] = this.CharGrid[i - 1];
                this.AttribGrid[i] = this.AttribGrid[i - 1];
            }


            this.CharGrid[this.Caret.Pos.Y] = new char[this._cols];
            this.AttribGrid[this.Caret.Pos.Y] = new CharAttribStruct[this._cols];

            NbrOff--;
        }

    } //need paint

    private void DeleteLine(uc_Params CurParams) {
        // if we're not in the scroll region then bail
        if (this.Caret.Pos.Y < this.TopMargin || this.Caret.Pos.Y > this.BottomMargin) {
            return;
        }


        int NbrOff = 1;

        if (CurParams.Count() > 0) {
            String str = (String) CurParams.Elements.get(0);
            NbrOff = Integer.valueOf(str);
        }

        while (NbrOff > 0) {

            // Shift all the rows from below the current row to the bottom margin up one place
            for (int i = this.Caret.Pos.Y; i < this.BottomMargin; i++) {
                this.CharGrid[i] = this.CharGrid[i + 1];
                this.AttribGrid[i] = this.AttribGrid[i + 1];
            }

            this.CharGrid[this.BottomMargin] = new char[this._cols];
            this.AttribGrid[this.BottomMargin] = new CharAttribStruct[this._cols];

            NbrOff--;
        }
    } //need paint

    private void LineFeed() {

        String s = "";
        for (int x = 0; x < this._cols; x++) {
            char CurChar = this.CharGrid[this.Caret.Pos.Y][x];

            if (CurChar == '\0') {
                continue;
            }
            s = s + String.valueOf(CurChar);
        }

        if (this.Caret.Pos.Y == this.BottomMargin || this.Caret.Pos.Y == this._rows - 1) {
            // we need to add a new line so shift all the rows up in the array and
            // insert a new row at the bottom

            int i;
            this.AttribGrid[this.BottomMargin - 1] = this.AttribGrid[0];

            for (i = this.TopMargin; i < this.BottomMargin; i++) {
                this.CharGrid[i] = this.CharGrid[i + 1];
                this.AttribGrid[i] = this.AttribGrid[i + 1];

                for (int xcol = 0; xcol < this._cols; ++xcol) {
                    if (this.AttribGrid[i][xcol] == null) {
                        DrawChar(this.CharGrid[i][xcol], xcol, i, false, false);
                    }
                    else {
                        DrawChar(this.CharGrid[i][xcol], xcol, i, this.AttribGrid[i][xcol].IsBold, this.AttribGrid[i][xcol].IsUnderscored);
                    }
                }

            }

            this.CharGrid[i] = new char[this._cols];
            Arrays.fill(CharGrid[i], (char) 0);

            //this.AttribGrid[i] = new CharAttribStruct[this._cols];

            //for (int y = 0; y < this._cols; y++)
            // {
            //this.AttribGrid[i][ y] = new CharAttribStruct();

            // }


        }

        this.CaretDown();


    }

    private void Index(int Param) {

        if (Param == 0) {
            Param = 1;
        }

        for (int i = 0; i < Param; i++) {
            this.LineFeed();
        }
    }

    @Override
    public void handleBarcodeFire(String Code) {
        if(TESettingsInfo.getUpperCaseByIndex(TESettingsInfo.getSessionIndex()) == true)
            Code = Code.toUpperCase();
        DispatchMessage(this, Code);
        ViewPostInvalidate();
    }

    @Override
    public void OnScreenBufferPos(int x, int y) {

    }

    @Override
    public void OnConnected() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());


        Boolean IsLog = TESettingsInfo.getHostIsWriteLogkByIndex(TESettingsInfo.getSessionIndex());
        if (IsLog)
            LogFile = new TerminalLogWriter(formattedDate + ".txt");

        byte[] SendData = TESettingsInfo.getVTHostSendToHostByIndex(TESettingsInfo.getSessionIndex());

        if (SendData != null && SendData.length > 0) {
            DispatchMessageRaw(this, SendData, SendData.length);
        }
        if (mTerminalListener != null) {
            mTerminalListener.onConnected();
        }
    }

    @Override
    public void handleKeyDown(int keyCode, KeyEvent event) {
        Boolean IsLineBuffer = TESettingsInfo.getHostIsLineBufferByIndex(TESettingsInfo.getSessionIndex());

        if (IsLineBuffer) {
            KeyEventVal Key = new KeyEventVal();
            Key.Set(event, keyCode);
            LineBufferList.add(Key);

            if (Key.GetEventKeycode() == KeyEvent.KEYCODE_TAB || Key.GetEventKeycode() == KeyEvent.KEYCODE_ENTER)
            {
                for (int i = 0; i < LineBufferList.size(); i++) {
                    KeyEventVal KeyVal = (KeyEventVal) LineBufferList.get(i);
                    KeyInput(KeyVal.GetEventKeycode(), KeyVal.GetEvent());
                }

                LineBufferList.clear();
                return;
            } else {
                Boolean IsEcho = TESettingsInfo.getHostIsLocalEchoByIndex(TESettingsInfo.getSessionIndex());
                if (IsEcho)
                    LineBufferInput(keyCode, event);
            }
        } else
            KeyInput(keyCode, event);
    }

    public void LineBufferInput(int keyCode, KeyEvent event) {

        //LineBufferList
        boolean Func = false;
        char pressedKey = (char) event.getUnicodeChar();

        switch (keyCode) {
            case KeyEvent.KEYCODE_TAB:

            case KeyEvent.KEYCODE_ENTER:

            case VKEY_BS:


            case KeyEvent.KEYCODE_DEL:
            case VKEY_DEL:

            case KeyEvent.KEYCODE_F1:

            case KeyEvent.KEYCODE_F2:

            case KeyEvent.KEYCODE_F3:

            case KeyEvent.KEYCODE_F4:

            case KeyEvent.KEYCODE_F5:

            case KeyEvent.KEYCODE_F6:

            case KeyEvent.KEYCODE_F7:

            case KeyEvent.KEYCODE_F8:

            case KeyEvent.KEYCODE_F9:

            case KeyEvent.KEYCODE_F10:

            case KeyEvent.KEYCODE_F11:

            case KeyEvent.KEYCODE_F12:


            case KEY_F13:

            case KEY_F14:

            case KEY_F15:

            case KEY_F16:

            case KEY_F17:

            case KEY_F18:

            case KEY_F19:

            case KEY_F20:

                Func = true;
                break;


        }

        if (!Func) {
            if (pressedKey == 0)
                return;

            this.PrintChar(pressedKey);
        }


        ViewPostInvalidate();

    }

    public void KeyInput(int keyCode, KeyEvent event) {

        //LineBufferList
        boolean Func = false;
        char pressedKey = (char) event.getUnicodeChar();
        byte SendData[] = new byte[30];
        int SendLenth = 0;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'D';
                Func = true;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'C';
                Func = true;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'A';
                Func = true;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'B';
                Func = true;
                break;
            case KeyEvent.KEYCODE_TAB:
                SendData[SendLenth++] = 0x09;
                Func = true;
                break;
            case KeyEvent.KEYCODE_ENTER:
                SendData[SendLenth++] = 0x0d;
                Func = true;
                break;
            case VKEY_BS:
                SendData[SendLenth++] = 0x08;
                Func = true;
                break;
            case KeyEvent.KEYCODE_DEL:
            case VKEY_DEL:
                SendData[SendLenth++] = 127;
                Func = true;
                break;
            case KeyEvent.KEYCODE_F1:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'P';
                Func = true;
                break;

            case KeyEvent.KEYCODE_F2:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'Q';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F3:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'R';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F4:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = 'O';
                SendData[SendLenth++] = 'S';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F5:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = 'M';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F6:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '1';
                SendData[SendLenth++] = '7';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F7:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '1';
                SendData[SendLenth++] = '8';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F8:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '1';
                SendData[SendLenth++] = '9';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F9:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '0';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F10:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '1';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F11:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '3';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KeyEvent.KEYCODE_F12:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '4';
                SendData[SendLenth++] = '~';
                Func = true;
                break;

            case KEY_F13:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '5';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KEY_F14:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '6';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KEY_F15:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '8';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KEY_F16:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '9';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KEY_F17:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '3';
                SendData[SendLenth++] = '1';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KEY_F18:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '3';
                SendData[SendLenth++] = '2';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KEY_F19:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '3';
                SendData[SendLenth++] = '3';
                SendData[SendLenth++] = '~';
                Func = true;
                break;
            case KEY_F20:
                SendData[SendLenth++] = 27;
                SendData[SendLenth++] = '[';
                SendData[SendLenth++] = '3';
                SendData[SendLenth++] = '4';
                SendData[SendLenth++] = '~';
                Func = true;
                break;


        }

        if (!Func) {
            if (pressedKey == 0)
                return;
            PutAsciiKey(pressedKey);
        } else
            DispatchMessageRaw(this, SendData, SendLenth);

        ViewPostInvalidate();

    }

    private void PutAsciiKey(int KeyCode) {
        if (Character.isLetter((char)KeyCode) && TESettingsInfo.getUpperCaseByIndex(TESettingsInfo.getSessionIndex()) == true) {
            KeyCode = Character.toUpperCase((char) KeyCode);
        }

        byte[] OutData = {0};
        OutData[0] = (byte) KeyCode;
        DispatchMessageRaw(this, OutData, OutData.length);
    }

    private void ReverseIndex(int Param) {

        if (Param == 0) {
            Param = 1;
        }

        for (int i = 0; i < Param; i++) {
            this.ReverseLineFeed();
        }
    }

    //endregion
    //region Caret
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

    private void CaretDown() {
        this.Caret.EOL = false;

        if ((this.Caret.Pos.Y < this._rows - 1 && (this.Modes.Flags & Modes.OriginRelative) == 0) || (this.Caret.Pos.Y < this.BottomMargin && (this.Modes.Flags & Modes.OriginRelative) > 0)) {
            this.Caret.Pos.Y += 1;
        }
    }

    private void CaretUp() {
        this.Caret.EOL = false;

        if ((this.Caret.Pos.Y > 0 && (this.Modes.Flags & Modes.OriginRelative) == 0) || (this.Caret.Pos.Y > this.TopMargin && (this.Modes.Flags & Modes.OriginRelative) > 0)) {
            this.Caret.Pos.Y -= 1;
        }
    }

    private void CaretRight() {
        if (this.Caret.Pos.X < this._cols - 1) {
            this.Caret.Pos.X += 1;
            this.Caret.EOL = false;
        } else {
            this.Caret.EOL = true;
        }
    }

    private void CaretLeft() {
        this.Caret.EOL = false;

        if (this.Caret.Pos.X > 0) {
            this.Caret.Pos.X -= 1;
        }
    }

    private void CaretToRel(int Y, int X) {

        this.Caret.EOL = false;
            /* This code is used when we get a cursor position command from
			       the host. Even if we're not in relative mode we use this as this will
			       sort that out for us. The ToAbs code is used internally by this prog
			       but is smart enough to stay within the margins if the originrelative
			       flagis set. */

        if ((this.Modes.Flags & Modes.OriginRelative) == 0) {
            this.CaretToAbs(Y, X);
            return;
        }

			/* the origin mode is relative so add the top and left margin
			       to Y and X respectively */
        Y += this.TopMargin;

        if (X < 0) {
            X = 0;
        }

        if (X > this._cols - 1) {
            X = this._cols - 1;
        }

        if (Y < this.TopMargin) {
            Y = this.TopMargin;
        }

        if (Y > this.BottomMargin) {
            Y = this.BottomMargin;
        }

        this.Caret.Pos.Y = Y;
        this.Caret.Pos.X = X;
    }

    private void CaretToAbs(int Y, int X) {
        this.Caret.EOL = false;

        if (X < 0) {
            X = 0;
        }

        if (X > this._cols - 1) {
            X = this._cols - 1;
        }

        if (Y < 0 && (this.Modes.Flags & Modes.OriginRelative) == 0) {
            Y = 0;
        }

        if (Y < this.TopMargin && (this.Modes.Flags & Modes.OriginRelative) > 0) {
            Y = this.TopMargin;
        }

        if (Y > this._rows - 1 && (this.Modes.Flags & Modes.OriginRelative) == 0) {
            Y = this._rows - 1;
        }

        if (Y > this.BottomMargin && (this.Modes.Flags & Modes.OriginRelative) > 0) {
            Y = this.BottomMargin;
        }

        this.Caret.Pos.Y = Y;
        this.Caret.Pos.X = X;
    }

    private void CarriageReturn() {
        this.CaretToAbs(this.Caret.Pos.Y, 0);
    }

    public class VtParserEvent implements VtParserImplement {

        public void onEventUcParser(Object Sender, ParserEventArgs e) {
            //CVT100 Host=(CVT100)Sender;
            CommandRouter(Sender, e);
        }
    }

    public final class KeyEventVal {
        KeyEvent event;
        int KeyCode;

        public KeyEventVal() {

        }

        public void Set(KeyEvent KeyEvet, int code) {
            KeyCode = code;
            event = KeyEvet;
        }

        public KeyEvent GetEvent() {
            return event;
        }

        public int GetEventKeycode() {
            return KeyCode;
        }
    }
    //endregion
}
