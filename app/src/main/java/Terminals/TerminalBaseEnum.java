package Terminals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;



/**
 * Created by Franco.Liu on 2014/1/17.
 */
public class TerminalBaseEnum {


    public static  class Point
    {
        public int X, Y;
        public Point(int x, int y) {
            this.X = x;
            this.Y = y;
        }
    }

    private enum NvtCommand
    {
        SE (240),
        NOP (241),
        DM (242),
        BREAK  (243),
        IP  (244),
        AO  (245),
        AYT  (246),
        EC (247),
        EL  (248),
        GA  (249),
        SB  (250),
        WILL  (251),
        WONT  (252),
        DO (253),
        DONT  (254),
        IAC  (255);

        private int value;
        private NvtCommand(int value) {
            this.value = value;
        }

    }

    public enum TntActions
    {
        None,
        SendUp,
        Dispatch,
        Collect,
        NewCollect,
        Param,
        Execute
    }
    
    public enum NvtOption
    {
        ECHO(1), // echo
        SGA(3), // suppress go ahead
        STATUS(5), // status
        TM(6), // timing mark
        TTYPE(24), // terminal type
        NAWS(31), // window size
        TSPEED(32), // terminal speed
        LFLOW(33), // remote flow control
        LINEMODE(34), // linemode
        ENVIRON(36); // environment variables

        private final int value;
        private NvtOption(int intValue)
        {
            value = intValue;

        }

        public int getValue()
        {
            return value;
        }


    }

    public final class ActionsVal
    {
        Actions _Action;

        public ActionsVal(Actions Action)
        {
            _Action=Action;
        }
        public void Set(Actions Action)
        {
            _Action=Action;
        }
        public Actions Get()
        {
            return _Action;
        }
    }
    public enum Actions
    {
        None(0),
        Dispatch(1),
        Execute(2),
        Ignore(3),
        Collect(4),
        NewCollect(5),
        Param(6),
        OscStart(8),
        OscPut(9),
        OscEnd(10),
        Hook(11),
        Unhook(12),
        Put(13),
        Print(14);

        private final int value;


        private Actions(int intValue)
        {
            value = intValue;

        }

        public int getValue()
        {
            return value;
        }


    }

    public final class SetsVal
    {
        Sets _Set;

        public SetsVal()
        {

        }
        public SetsVal(Sets Set)
        {
            _Set=Set;
        }
        public void Set(Sets Set)
        {
            _Set=Set;
        }
        public Sets Get()
        {
            return _Set;
        }
    }
    public enum Sets
    {
        None,
        DECSG,
        DECTECH,
        DECS,
        ASCII,
        ISOLatin1S,
        NRCUK,
        NRCFinnish,
        NRCFrench,
        NRCFrenchCanadian,
        NRCGerman,
        NRCItalian,
        NRCNorDanish,
        NRCPortuguese,
        NRCSpanish,
        NRCSwedish,
        NRCSwiss;

        public int getValue()
        {
            return this.ordinal();
        }

        public static Sets getEnum(int intValue)
        {
            return values()[intValue];
        }
    }
    public enum Transitions
    {
        //Both Use
        None(0),
        Entry(1),
        Exit(2);

        private final int value;
        private Transitions(int intValue)
        {
            value = intValue;

        }

        public int getValue()
        {
            return value;
        }


    }
    public enum ucStates
    {
        None,
        Ground,
        Command,
        Anywhere,
        Synch,
        Negotiate,
        SynchNegotiate,
        SubNegotiate,
        SubNegValue,
        SubNegParam,
        SubNegEnd,
        SynchSubNegotiate
    }

    public final class CharAttribStruct
    {
        public boolean IsBold;
        public boolean IsDim;
        public boolean IsUnderscored;
        public boolean IsBlinking;
        public boolean IsInverse;
        public boolean IsPrimaryFont;
        public boolean IsAlternateFont;
        public boolean UseAltColor;
        public int AltColor;
        public boolean UseAltBGColor;
        public int AltBGColor;
        public uc_Chars GL;
        public uc_Chars GR;
        public uc_Chars GS;
        public boolean IsDECSG;

        public CharAttribStruct()
        {
        }

        public CharAttribStruct(boolean p1, boolean p2, boolean p3, boolean p4, boolean p5, boolean p6, boolean p7, boolean p12, int p13, boolean p14, int p15, uc_Chars p16, uc_Chars p17, uc_Chars p18, boolean p19)
        {
            IsBold = p1;
            IsDim = p2;
            IsUnderscored = p3;
            IsBlinking = p4;
            IsInverse = p5;
            IsPrimaryFont = p6;
            IsAlternateFont = p7;
            UseAltColor = p12;
            AltColor = p13;
            UseAltBGColor = p14;
            AltBGColor = p15;
            GL = p16;
            GR = p17;
            GS = p18;
            IsDECSG = p19;
        }


        public CharAttribStruct clone()
        {
            CharAttribStruct varCopy = new CharAttribStruct();
            varCopy.IsBold = this.IsBold;
            varCopy.IsDim = this.IsDim;
            varCopy.IsUnderscored = this.IsUnderscored;
            varCopy.IsBlinking = this.IsBlinking;
            varCopy.IsInverse = this.IsInverse;
            varCopy.IsPrimaryFont = this.IsPrimaryFont;
            varCopy.IsAlternateFont = this.IsAlternateFont;
            varCopy.UseAltColor = this.UseAltColor;
            varCopy.AltColor = this.AltColor;
            varCopy.UseAltBGColor = this.UseAltBGColor;
            varCopy.AltBGColor = this.AltBGColor;
            varCopy.GL = this.GL;
            varCopy.GR = this.GR;
            varCopy.GS = this.GS;
            varCopy.IsDECSG = this.IsDECSG;
            return varCopy;
        }
    }

    public class uc_Chars
    {
        public SetsVal Set=new SetsVal();
        public class uc_CharSet
        {
            public uc_CharSet()
            {
            }

            public uc_CharSet(int p1, int p2)
            {

                Location = p1;
                UnicodeNo = p2;
            }

            public int Location;
            public int UnicodeNo;
            public Sets Set;

            public uc_CharSet clone()
            {
                uc_CharSet varCopy = new uc_CharSet();

                varCopy.Location = this.Location;
                varCopy.UnicodeNo = this.UnicodeNo;

                return varCopy;
            }
        }
        public uc_Chars(Sets p1)
        {
            this.Set.Set(p1);
        }

        public char Get(char CurChar, Sets GL, Sets GR)
        {
            uc_CharSet[] CurSet=ASCII;

            // I'm assuming the left hand in use table will always have a 00-7F char set in it
            if ((int)CurChar < 128)
            {
                switch (GL)
                {
                    case ASCII:
                        CurSet =ASCII;
                        break;

                    case DECSG:
                        CurSet = DECSG;
                        break;

                    case NRCUK:
                        CurSet = NRCUK;
                        break;

                    case NRCFinnish:
                        CurSet = NRCFinnish;
                        break;

                    case NRCFrench:
                        CurSet =NRCFrench;
                        break;

                    case NRCFrenchCanadian:
                        CurSet = NRCFrenchCanadian;
                        break;

                    case NRCGerman:
                        CurSet = NRCGerman;
                        break;

                    case NRCItalian:
                        CurSet = NRCItalian;
                        break;

                    case NRCNorDanish:
                        CurSet = NRCNorDanish;
                        break;

                    case NRCPortuguese:
                        // CurSet = uc_Chars.NRCPortuguese;
                        break;

                    case NRCSpanish:
                        CurSet =NRCSpanish;
                        break;

                    case NRCSwedish:
                        CurSet = NRCSwedish;
                        break;

                    case NRCSwiss:
                        CurSet = NRCSwiss;
                        break;

                    default:
                        CurSet = ASCII;
                        break;
                }
            }
            // I'm assuming the right hand in use table will always have a 80-FF char set in it
            else
            {
                switch (GR)
                {
                    case ISOLatin1S:
                        CurSet = ISOLatin1S;
                        break;

                    case DECS:
                        CurSet = DECS;
                        break;

                    default:
                        CurSet = DECS;
                        break;
                }
            }

            for (int i = 0; i < CurSet.length; i++)
            {
                if (CurSet[i].Location == Integer.valueOf(CurChar))
                {
                    byte[] CurBytes = GetBytes(CurSet[i].UnicodeNo);
                    char[] NewChars = getChars(CurBytes);

                    //return NewChars[0];
                }

            }
            return CurChar;
        }

        public byte[] GetBytes(int value)
        {
            ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder());
            buffer.putInt(value);
            return buffer.array();
        }
        private char[] getChars(byte[] bytes) {
            Charset cs = Charset.forName("UTF-8");

            ByteBuffer bb = ByteBuffer.allocate(bytes.length);
            bb.put(bytes);
            bb.flip();

            CharBuffer cb = cs.decode(bb);

            return cb.array();
        }

        public  uc_CharSet[] DECSG =
                {
                        new uc_CharSet (0x5F, 0x0020), // Blank (I've used space here so you may want to change this)
                        new uc_CharSet (0x61, 0x0000), // Pi over upsidedown Pi ?
                        new uc_CharSet (0x62, 0x2409), // HT symbol
                        new uc_CharSet (0x63, 0x240C), // LF Symbol
                        new uc_CharSet (0x64, 0x240D), // CR Symbol
                        new uc_CharSet (0x65, 0x240A), // LF Symbol
                        new uc_CharSet (0x66, 0x00B0), // Degree
                        new uc_CharSet (0x67, 0x00B1), // Plus over Minus
                        new uc_CharSet (0x68, 0x2424), // NL Symbol
                        new uc_CharSet (0x69, 0x240B), // VT Symbol
                        new uc_CharSet (0x6F, 0x23BA), // Scan Line 1
                        new uc_CharSet (0x70, 0x25BB), // Scan Line 3
                        new uc_CharSet (0x72, 0x23BC), // Scan Line 7
                        new uc_CharSet (0x73, 0x23BD), // Scan Line 9
                        new uc_CharSet (0x79, 0x2264), // Less than or equal
                        new uc_CharSet (0x7A, 0x2265), // Greater than or equal
                        new uc_CharSet (0x7B, 0x03A0), // Capital Pi
                        new uc_CharSet (0x7C, 0x2260), // Not Equal
                        new uc_CharSet (0x7D, 0x00A3), // Pound Sign
                        new uc_CharSet (0x7E, 0x00B7), // Middle Dot
                };
        public uc_CharSet[] DECS =
                {
                        new uc_CharSet ((int)0xA8,(int) 0x0020), // Currency Sign
                        new uc_CharSet (0xD7, 0x0152), // latin small ligature OE
                        new uc_CharSet (0xDD, 0x0178), // Capital Y with diaeresis
                        new uc_CharSet (0xF7, 0x0153), // latin small ligature oe
                        new uc_CharSet (0xFD, 0x00FF), // Lowercase y with diaeresis
                };

        public uc_CharSet[] ASCII = // same as Basic Latin
                {
                        new uc_CharSet (0x00, 0x0000),
                };

        public uc_CharSet[] NRCUK = // UK National Replacement
                {
                        new uc_CharSet (0x23, 0x00A3),
                };

        public uc_CharSet[] NRCFinnish = // Finnish National Replacement
                {
                        new uc_CharSet (0x5B, 0x00C4), // A with diearesis
                        new uc_CharSet (0x5C, 0x00D6), // O with diearesis
                        new uc_CharSet (0x5D, 0x00C5), // A with hollow dot above
                        new uc_CharSet (0x5E, 0x00DC), // U with diearesis
                        new uc_CharSet (0x60, 0x00E9), // e with accute accent
                        new uc_CharSet (0x7B, 0x00E4), // a with diearesis
                        new uc_CharSet (0x7C, 0x00F6), // o with diearesis
                        new uc_CharSet (0x7D, 0x00E5), // a with hollow dot above
                        new uc_CharSet (0x7E, 0x00FC), // u with diearesis
                };

        public uc_CharSet[] NRCFrench = { // French National Replacement
                new uc_CharSet(0x23, 0x00A3),
                new uc_CharSet(0x40, 0x00E0),
                new uc_CharSet(0x5B, 0x00B0),
                new uc_CharSet(0x5C, 0x00E7),
                new uc_CharSet(0x5D, 0x00A7),
                new uc_CharSet(0x7B, 0x00E9),
                new uc_CharSet(0x7C, 0x00F9),
                new uc_CharSet(0x7D, 0x00E8),
                new uc_CharSet(0x7E, 0x00A8)
        };

        public uc_CharSet[] NRCFrenchCanadian = { // French Canadian National Replacement
                new uc_CharSet(0x40, 0x00E0),
                new uc_CharSet(0x5B, 0x00E2),
                new uc_CharSet(0x5C, 0x00E7),
                new uc_CharSet(0x5D, 0x00EA),
                new uc_CharSet(0x5E, 0x00CE),
                new uc_CharSet(0x60, 0x00F4),
                new uc_CharSet(0x7B, 0x00E9),
                new uc_CharSet(0x7C, 0x00F9),
                new uc_CharSet(0x7D, 0x00E8),
                new uc_CharSet(0x7E, 0x00FB)
        };

        public uc_CharSet[] NRCGerman = { // German National Replacement
                new uc_CharSet(0x40, 0x00A7),
                new uc_CharSet(0x5B, 0x00C4),
                new uc_CharSet(0x5C, 0x00D6),
                new uc_CharSet(0x5D, 0x00DC),
                new uc_CharSet(0x7B, 0x00E4),
                new uc_CharSet(0x7C, 0x00F6),
                new uc_CharSet(0x7D, 0x00FC),
                new uc_CharSet(0x7E, 0x00DF)
        };

        public uc_CharSet[] NRCItalian = { // Italian National Replacement
                new uc_CharSet(0x23, 0x00A3),
                new uc_CharSet(0x40, 0x00A7),
                new uc_CharSet(0x5B, 0x00B0),
                new uc_CharSet(0x5C, 0x00E7),
                new uc_CharSet(0x5D, 0x00E9),
                new uc_CharSet(0x60, 0x00F9),
                new uc_CharSet(0x7B, 0x00E0),
                new uc_CharSet(0x7C, 0x00F2),
                new uc_CharSet(0x7D, 0x00E8),
                new uc_CharSet(0x7E, 0x00CC)
        };

        public uc_CharSet[] NRCNorDanish = { // Norwegian Danish National Replacement
                new uc_CharSet(0x5B, 0x00C6),
                new uc_CharSet(0x5C, 0x00D8),
                new uc_CharSet(0x5D, 0x00D8),
                new uc_CharSet(0x5D, 0x00C5),
                new uc_CharSet(0x7B, 0x00E6),
                new uc_CharSet(0x7C, 0x00F8),
                new uc_CharSet(0x7D, 0x00F8),
                new uc_CharSet(0x7D, 0x00E5)
        };

        public uc_CharSet[] NRCPortuguese = // Portuguese National Replacement
                {
                        new uc_CharSet (0x5B, 0x00C3), // A with tilde
                        new uc_CharSet (0x5C, 0x00C7), // big cedilla
                        new uc_CharSet (0x5D, 0x00D5), // O with tilde
                        new uc_CharSet (0x7B, 0x00E3), // a with tilde
                        new uc_CharSet (0x7C, 0x00E7), // little cedilla
                        new uc_CharSet (0x7D, 0x00F6), // o with tilde
                };

        public uc_CharSet[] NRCSpanish = // Spanish National Replacement
                {
                        new uc_CharSet (0x23, 0x00A3), // pound sign
                        new uc_CharSet (0x40, 0x00A7), // funny s
                        new uc_CharSet (0x5B, 0x00A1), // I with dot
                        new uc_CharSet (0x5C, 0x00D1), // N with tilde
                        new uc_CharSet (0x5D, 0x00BF), // Upside down question mark
                        new uc_CharSet (0x7B, 0x0060), // back single quote
                        new uc_CharSet (0x7C, 0x00B0), // Degree Symbol
                        new uc_CharSet (0x7D, 0x00F1), // n with tilde
                        new uc_CharSet (0x7E, 0x00E7), // small cedilla
                };

        public uc_CharSet[] NRCSwedish = // Swedish National Replacement
                {
                        new uc_CharSet (0x40, 0x00C9), // E with acute
                        new uc_CharSet (0x5B, 0x00C4), // A with diearesis
                        new uc_CharSet (0x5C, 0x00D6), // O with diearesis
                        new uc_CharSet (0x5D, 0x00C5), // A with hollow dot above
                        new uc_CharSet (0x5E, 0x00DC), // U with diearesis
                        new uc_CharSet (0x60, 0x00E9), // e with accute accent
                        new uc_CharSet (0x7B, 0x00E4), // a with diearesis
                        new uc_CharSet (0x7C, 0x00F6), // o with diearesis
                        new uc_CharSet (0x7D, 0x00E5), // a with hollow dot above
                        new uc_CharSet (0x7E, 0x00FC), // u with diearesis
                };

        public uc_CharSet[] NRCSwiss = { // Swiss National Replacement
                new uc_CharSet(0x23, 0x00F9),
                new uc_CharSet(0x40, 0x00E0),
                new uc_CharSet(0x5B, 0x00E9),
                new uc_CharSet(0x5C, 0x00E7),
                new uc_CharSet(0x5D, 0x00EA),
                new uc_CharSet(0x5E, 0x00CE),
                new uc_CharSet(0x5F, 0x00E8),
                new uc_CharSet(0x60, 0x00F4),
                new uc_CharSet(0x7B, 0x00E4),
                new uc_CharSet(0x7C, 0x00F6),
                new uc_CharSet(0x7D, 0x00FC),
                new uc_CharSet(0x7E, 0x00FB)
        };

        public uc_CharSet[] ISOLatin1S = {new uc_CharSet(0x00, 0x0000)}; // Same as Latin-1 Supplemental



    }

    public class uc_Caret
    {
        public Point Pos;
        //public System.Drawing.Color Color = System.Drawing.Color.FromArgb(255, 181, 106);
        //public System.Drawing.Bitmap Bitmap = null;
        //public System.Drawing.Graphics Buffer = null;
        public boolean IsOff = false;
        public boolean EOL = false;

        public uc_Caret()
        {
            this.Pos = new Point(0, 0);
        }
    }

    public class uc_CaretAttribs
    {
        public Point Pos;
        public Sets G0Set;
        public Sets G1Set;
        public Sets G2Set;
        public Sets G3Set;
        public CharAttribStruct Attribs;

        public uc_CaretAttribs(Point p1, Sets p2, Sets p3, Sets p4, Sets p5, CharAttribStruct p6)
        {
            this.Pos = p1;
            this.G0Set = p2;
            this.G1Set = p3;
            this.G2Set = p4;
            this.G3Set = p5;
            this.Attribs = p6;
        }
    }

    public class uc_TabStops
    {
        public boolean[] Columns;

        public uc_TabStops()
        {
            Columns = new boolean[256];

            Columns[8] = true;
            Columns[16] = true;
            Columns[24] = true;
            Columns[32] = true;
            Columns[40] = true;
            Columns[48] = true;
            Columns[56] = true;
            Columns[64] = true;
            Columns[72] = true;
            Columns[80] = true;
            Columns[88] = true;
            Columns[96] = true;
            Columns[104] = true;
            Columns[112] = true;
            Columns[120] = true;
            Columns[128] = true;
        }
    }



    public final class ParserEventArgs
    {
        //  telnet
        public TntActions Action;
        public char CurChar;
        public String CurSequence;
        public uc_Params CurParams;

        public ParserEventArgs()
        {
        }

        public ParserEventArgs(TntActions p1, char p2, String p3, uc_Params p4)
        {
            Action = p1;
            CurChar = p2;
            CurSequence = p3;
            CurParams = p4;
        }
    }


    public class uc_Params
    {
        // Both
        public java.util.ArrayList<String> Elements = new java.util.ArrayList<String>();

        public uc_Params()
        {
        }

        public final int Count()
        {
            return this.Elements.size();
        }

        public final void Clear()
        {
            this.Elements.clear();
        }

        public final void Add(char CurChar)
        {
            if (this.Count() < 1)
            {
                this.Elements.add("0");
            }

            if (CurChar == ';')
            {
                this.Elements.add("0");
            }
            else
            {
                int i = this.Elements.size() - 1;
                this.Elements.set(i, ((String)this.Elements.get(i) + Character.valueOf(CurChar)));
            }
        }
    }

    public class uc_Mode
    {
        // For UC
        final static public int Locked = 1;           // Unlocked           = off
        final static public int BackSpace = 2;           // Delete             = off
        final static public int NewLine = 4;           // Line Feed          = off
        final static public int Repeat = 8;           // No Repeat          = off
        final static public int AutoWrap = 16;          // No AutoWrap        = off
        final static public int CursorAppln = 32;          // Std Cursor Codes   = off
        final static public int KeypadAppln = 64;          // Std Numeric Codes  = off
        final static public int DataProcessing = 128;         // Typewriter         = off
        final static public int PositionReports = 256;         // CharacterCodes     = off
        final static public int LocalEchoOff = 512;         // LocalEchoOn        = off
        final static public int OriginRelative = 1024;        // OriginAbsolute     = off
        final static public int LightBackground = 2048;        // DarkBackground     = off
        final static public int National = 4096;        // Multinational      = off
        final static public int Any = 0x80000000;  // Any Flags

        public int Flags = AutoWrap;

        public uc_Mode(int InitialFlags)
        {
            this.Flags = InitialFlags;
        }

        public uc_Mode()
        {
            this.Flags = 0;
        }
    }

}
