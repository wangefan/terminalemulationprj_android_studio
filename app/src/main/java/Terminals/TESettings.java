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

        public boolean isUseDefaultDevName() {
            return mDevNameType == 0;
        }

        public void setUseDefaultDevName(boolean isDefault) {
            if(isDefault) {
                mDevNameType = 0;
            } else {
                mDevNameType = 1;
            }
        }

    	//Sync
        @SerializedName("TermNameTN")
        public String mTermNameTN="";

        @SerializedName("TermName")
        public String mTermName="";

        @SerializedName("isTN")
        public int mIsTN = 1;

        @SerializedName("HostIPorName")
        public String mHostIP = "";

    	@SerializedName("TelnetPort")
        public ArrayList<Integer> mTelnetPort = new ArrayList<Integer>();// low[0] + high[1] * 256

        @SerializedName("bAutoConnect")
        public boolean mBAutoConnect;

    	@SerializedName("bAutoSignOn")
        public boolean mBAutoSignOn = false;

        @SerializedName("LoginName")
        public String mLoginName = "";

        @SerializedName("LoginPassword")
        public String mLoginPassword = "";

        @SerializedName("NamePrompt")
        public String mNamePrompt="";

        @SerializedName("PassPrompt")
        public String mPassPrompt="";

        @SerializedName("TermLogin")
        public int mTermLogin = 0;	//0:Tab, 1:Enter

        @SerializedName("isSelected")
        public boolean mIsSelected = false;

        @SerializedName("bNetKeepAlive")
        public boolean mNetKeepAlive = false;

        @SerializedName("bIsDetectOutRange")
        public boolean mIsDetectOutRange = false;

        @SerializedName("isSaveLog")
        public boolean mIsSaveLog = false;

        @SerializedName("bUpperCase")
        public boolean mBUpperCase = false;

        @SerializedName("bIBMAutoReset")
        public boolean mBIBMAutoReset = false;

        @SerializedName("cCheckFieldLength")
        public int mCheckFieldLength = 0;//0: reject (default), 1: Truncate, 2: splite to next field

        @SerializedName("TELanguage")
        public int mTELanguage = 0;//0: single byte char, 1:TC, 2:SC, 3:Kor, 4:Jap, 5:Gre, 6:Fre

        @SerializedName("errorRowIndex")
        public int mNErrorRowIndexg = 0;

        @SerializedName("isPupUpErrorDialog")
        public boolean misPopUpErrorDialog = false;

        @SerializedName("LocalNameType")
        public int mDevNameType = 0;  //1: User Cust Local name, 0:Use default (means not set)

        @SerializedName("LocalName")
        public String mDevName = "";

        @SerializedName("bEcho")
        public boolean mBEcho = false;

        @SerializedName("LineBuffer")
        public int mLineBuffer = 0; //0:un-use, 1:use

        public int nCharSet = 0;	//0:Ansi, 1:UTF8
        public int CursorType = 0; //0:Default, 1:Underline, 2:Block
        public boolean bCursorTracking = false;
        public boolean isShowMacro = false;
        public boolean SSH = false;
        public String SSHName = "";
        public String SSHPassword="";
        public ReaderParam g_ReaderParam = new ReaderParam();

        //Sync but Not used

        //Not sync
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
