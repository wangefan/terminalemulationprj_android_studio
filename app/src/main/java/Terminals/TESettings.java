package Terminals;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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
        public enum AutoTrackType {
            AutoTrackType_Visible,
            AutoTrackType_Center,
            AutoTrackType_Lock
        }
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

        public AutoTrackType getAutoTrackType() {
            if(mIsCursorTrackingVisibleMode) {
                return AutoTrackType.AutoTrackType_Visible;
            } else if(mIsCursorTrackingCenterMode) {
                return AutoTrackType.AutoTrackType_Center;
            } else { //(mIsCursorTrackingLockMode)
                return AutoTrackType.AutoTrackType_Lock;
            }
        }

        public void setAutoTrackType(AutoTrackType trackingType) {
            mIsCursorTrackingVisibleMode = mIsCursorTrackingCenterMode = mIsCursorTrackingLockMode = false;
            switch (trackingType) {
                case AutoTrackType_Visible:
                    mIsCursorTrackingVisibleMode = true;
                    break;
                case AutoTrackType_Center:
                    mIsCursorTrackingCenterMode = true;
                    break;
                case AutoTrackType_Lock:
                    mIsCursorTrackingLockMode = true;
                    break;
            }
        }

        public int getFontColor() {
            return Color.rgb(mFontsColor.get(0), mFontsColor.get(1), mFontsColor.get(2));
        }

        public void setFontColor(int nColor) {
            mFontsColor.set(0, Color.red(nColor));
            mFontsColor.set(1, Color.green(nColor));
            mFontsColor.set(2, Color.blue(nColor));
        }

        public int getBGColor() {
            return Color.rgb(mBGColor.get(0), mBGColor.get(1), mBGColor.get(2));
        }

        public void setBGColor(int nColor) {
            mBGColor.set(0, Color.red(nColor));
            mBGColor.set(1, Color.green(nColor));
            mBGColor.set(2, Color.blue(nColor));
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

        @SerializedName("nCharSet")
        public int mNCharSet = 0;	//0:Ansi, 1:UTF8

        @SerializedName("sendStringOnConnect")
        public String mVTSendtoHost = "";

        @SerializedName("isShowSession")
        public boolean mIsShowSessionNumber = false;

        @SerializedName("isShowSessionStatus")
        public boolean mIsShowSessionStatus = false;

        @SerializedName("isShowWifiAlert")
        public boolean mIsShowWifiAlert = false;

        @SerializedName("isShowBatteryAlert")
        public boolean mIsShowBatteryAlert = false;

        @SerializedName("batteryAlertCBValue")
        public int mNShowBatteryAlertLevel = 10;

        @SerializedName("wifiAlertCBValue")
        public int mNShowWifiAlertLevel = 10;

        @SerializedName("isShowMacro")
        public boolean mIsActMacro = false;

        @SerializedName("macroKeyList")
        public ArrayList<MacroItem> mMacroList = null;

        @SerializedName("CursorType")
        public int mNCursorType = 0; //0:Default, 1:Underline, 2:Block

        @SerializedName("bCursorTracking")
        public boolean mIsCursorTracking = false;

        @SerializedName("bCursorVisible")
        public boolean mIsCursorTrackingVisibleMode = false;

        @SerializedName("bCursorCenter")
        public boolean mIsCursorTrackingCenterMode = false;

        @SerializedName("bCursorLocked")
        public boolean mIsCursorTrackingLockMode = false;

        @SerializedName("nCursorLockCol")
        public int mNCursorLockCol = 0;

        @SerializedName("nCursorLockRow")
        public int mNCursorLockRow = 0;

        @SerializedName("fontColor")
        public ArrayList<Integer> mFontsColor = new ArrayList<Integer>();

        @SerializedName("fontBgColor")
        public ArrayList<Integer> mBGColor = new ArrayList<Integer>();

        @SerializedName("nFontType")
        public int mNFontType = 0;  //0:Courier New, 1:Lucida Console, 2:Excalibur Monospace, 3:NetTerm ANSI, 4:NetTerm OEM

        @SerializedName("nFontHeight")
        public int mNFontHeight = 0;

        @SerializedName("nFontWidth")
        public int mNFontWidth = 0;

        @SerializedName("isAutoFullScreen")
        public boolean mIsAutoFullscreenOnConn = false;

        @SerializedName("isShowTaskBarOnFullScreen")
        public boolean mIsShowTaskbarOnConn = false;

        @SerializedName("bScanControl")
        public boolean mIsScanControl = false;

        public boolean SSH = false;
        public String SSHName = "";
        public String SSHPassword="";
        public ReaderParam g_ReaderParam = new ReaderParam();

        //Not sync
        public class ReaderParam {
            public boolean isEnableScannerByESCCmd = false; //Not used in TE C++
            @SerializedName("FeedbackControlByCmd")
            public int mIsFeedbackControlByCmd = 0;

            public String scannerEnableESC = null;
            public String scannerDisableESC = null;
            public String goodFeedBackESC = null;
            public String errorFeedBackESC = null;
        }
    }
}
