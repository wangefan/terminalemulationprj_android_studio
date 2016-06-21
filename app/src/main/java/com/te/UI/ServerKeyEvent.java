package com.te.UI;


import android.view.KeyEvent;

public class ServerKeyEvent extends KeyEvent {
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

    public ServerKeyEvent(int action, int code) {
        super(action, code);
    }
}
