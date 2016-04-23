package Terminals;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class TESettings {
    
    public TECommonSetting Common = new TECommonSetting();
    public ArrayList<SessionSetting> SETTINGS = new ArrayList<SessionSetting>();
    public SessionSetting getSessionSetting(int idxSession) {
        return SETTINGS.get(idxSession);
    }
    public class TECommonSetting {
        private ArrayList<CSsh_Keys> Ssh_Keys = new ArrayList<CSsh_Keys>();
        private class CSsh_Keys {
            private String Name;
            private String Path;
            private String Password;
            private int Keyform;
            public CSsh_Keys() {
                
            }
        }
    }

    public static class SessionSetting {
        public String getHostPort() {
            Integer nPortLow = mTelnetPort.get(0);
            Integer nPortHi = mTelnetPort.get(1);
            Integer nPort = nPortLow + nPortHi * 256;
            return nPort.toString();
        }

        public void setHostPort(String port) {
            Integer nPort = Integer.valueOf(port);
            Integer nPortLow = nPort % 256;
            Integer nPortHi = nPort / 256;
            mTelnetPort.set(0, nPortLow);
            mTelnetPort.set(1, nPortHi);
        }

    	//Sync
        @SerializedName("TermNameTN")
        public String mTermNameTN="";

        @SerializedName("TermName")
        public String mTermName="";

        @SerializedName("isTN")
        public int mIsTN = 1;

        public boolean bAutoConnect;

        @SerializedName("HostIPorName")
        public String mHostIP = "";

    	@SerializedName("TelnetPort")
        public ArrayList<Integer> mTelnetPort = new ArrayList<Integer>();// low[0] + high[1] * 256

    	@SerializedName("bAutoSignOn")
        public boolean mBAutoSignOn = false;

        @SerializedName("isSelected")
        public boolean mIsSelected = false;

        @SerializedName("bNetKeepAlive")
        public boolean mNetKeepAlive = false;

        @SerializedName("bIsDetectOutRange")
        public boolean mIsDetectOutRange = false;

        @SerializedName("isSaveLog")
        public boolean mIsSaveLog = false;

        public String LoginName = "";
        public String LoginPassword = "";
        public int nCharSet = 0;	//0:Ansi, 1:UTF8
        public int CursorType = 0; //0:Default, 1:Underline, 2:Block
        public int LineBuffer = 0;
        public boolean bCursorTracking = false;
        public boolean isShowMacro = false;
        public boolean SSH = false;
        public String SSHName = "";
        public String SSHPassword="";
        public String NamePrompt="";
        public String PassPrompt="";
        public int TermLogin = 0;	//0:Tab, 1:Enter
        public ReaderParam g_ReaderParam = new ReaderParam();

        //Sync but Not used
        public boolean bEcho = false;

        //Not sync
        public String Language="Single Byte Character";
        public String Fieldexceed="Reject";
        public byte[] SendtoHost=null;


        public class ReaderParam {
            public boolean isEnableScannerByESCCmd = false;
            public String scannerEnableESC = null;
            public String scannerDisableESC = null;
            public String goodFeedBackESC = null;
            public String errorFeedBackESC = null;
        }
    }
}
