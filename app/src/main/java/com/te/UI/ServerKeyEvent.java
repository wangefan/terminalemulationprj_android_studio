package com.te.UI;


import android.view.KeyEvent;

public class ServerKeyEvent extends KeyEvent {
    //server fun key begin, for TN(F1~F24) and VT(F1~F20) use
    static public final int FUN_KEYCODE_F1 = 1;
    static public final int FUN_KEYCODE_F2 = 2;
    static public final int FUN_KEYCODE_F3 = 3;
    static public final int FUN_KEYCODE_F4 = 4;
    static public final int FUN_KEYCODE_F5 = 5;
    static public final int FUN_KEYCODE_F6 = 6;
    static public final int FUN_KEYCODE_F7 = 7;
    static public final int FUN_KEYCODE_F8 = 8;
    static public final int FUN_KEYCODE_F9 = 9;
    static public final int FUN_KEYCODE_F10 = 10;
    static public final int FUN_KEYCODE_F11 = 11;
    static public final int FUN_KEYCODE_F12 = 12;
    static public final int FUN_KEYCODE_F13 = 13;
    static public final int FUN_KEYCODE_F14 = 14;
    static public final int FUN_KEYCODE_F15 = 15;
    static public final int FUN_KEYCODE_F16 = 16;
    static public final int FUN_KEYCODE_F17 = 17;
    static public final int FUN_KEYCODE_F18 = 18;
    static public final int FUN_KEYCODE_F19 = 19;
    static public final int FUN_KEYCODE_F20 = 20;
    static public final int FUN_KEYCODE_F21 = 21;
    static public final int FUN_KEYCODE_F22 = 22;
    static public final int FUN_KEYCODE_F23 = 23;
    static public final int FUN_KEYCODE_F24 = 24;
    //server fun key end

    //TN server key begin
    static public final int TN_KEYCODE_ATTN = 25;
    static public final int TN_KEYCODE_FBEGIN = 26;
    static public final int TN_KEYCODE_DEL = 27;
    static public final int TN_KEYCODE_FEND = 28;
    static public final int TN_KEYCODE_ERINPUT = 29;
    static public final int TN_KEYCODE_FPLUS = 30;
    static public final int TN_KEYCODE_FMINUS = 31;
    static public final int TN_KEYCODE_FEXIT = 32;
    static public final int TN_KEYCODE_LAST = 33;
    static public final int TN_KEYCODE_NEXT = 34;
    static public final int TN_KEYCODE_ROLDN = 35;
    static public final int TN_KEYCODE_ROLUP = 36;
    static public final int TN_KEYCODE_PREV = 37;
    static public final int TN_KEYCODE_RECORD = 38;
    static public final int TN_KEYCODE_SYSRQ = 39;
    static public final int TN_KEYCODE_INS = 40;
    static public final int TN_KEYCODE_DUP = 41;
    static public final int TN_KEYCODE_HELP = 42;
    static public final int TN_KEYCODE_CLR = 43;
    static public final int TN_KEYCODE_RESET = 44;
    static public final int TN_KEYCODE_ENTER = 45;
    static public final int TN_KEYCODE_LEFT = 46;
    static public final int TN_KEYCODE_LEFTDELETE = 47;
    static public final int TN_KEYCODE_PRINT = 48;
    static public final int TN_KEYCODE_HOME = 57;
    static public final int TN_KEYCODE_FMARK = 59;
    static public final int TN_KEYCODE_NEWLINE = 60;
    static public final int TN_KEYCODE_CLREOF = 61;
    static public final int TN_KEYCODE_RIGHT = 63;
    static public final int TN_KEYCODE_UP = 64;
    static public final int TN_KEYCODE_DOWN = 65;
    //TN server key end

    //VT server key begin
    static public final int VT_KEYCODE_PGUP = 21;
    static public final int VT_KEYCODE_PGDW = 22;
    static public final int VT_KEYCODE_HOME = 23;
    static public final int VT_KEYCODE_END = 24;
    static public final int VT_KEYCODE_INS = 25;
    static public final int VT_KEYCODE_BS = 26;
    static public final int VT_KEYCODE_TAB = 27;
    static public final int VT_KEYCODE_LEFT = 28;
    static public final int VT_KEYCODE_ENTER = 29;
    static public final int VT_KEYCODE_DEL = 30;
    static public final int VT_KEYCODE_UP = 31;
    static public final int VT_KEYCODE_DW = 32;
    static public final int VT_KEYCODE_RIGHT = 33;
    static public final int VT_KEYCODE_ESC = 34;
    static public final int VT_KEYCODE_LF = 35;
    static public final int VT_KEYCODE_FIND = 36;
    static public final int VT_KEYCODE_SELECT = 37;
    static public final int VT_KEYCODE_REMOVE = 38;
    static public final int VT_KEYCODE_PRESCREEN = 39;
    static public final int VT_KEYCODE_NEXTSCREEN = 40;
    //VT server key end

    public ServerKeyEvent(int action, int code) {
        super(action, code);
    }
}
