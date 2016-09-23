package Terminals;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import TelnetIBM.IBMHost5250;
import TelnetVT.CVT100;

public class TESettings {
    
    public TECommonSetting Common = new TECommonSetting();
    public ArrayList<SessionSetting> SETTINGS = new ArrayList<SessionSetting>();
    public SessionSetting getSessionSetting(int idxSession) {
        return SETTINGS.get(idxSession);
    }
    public class TECommonSetting {
        private ArrayList<CSsh_Keys> Ssh_Keys = new ArrayList<CSsh_Keys>();

        @SerializedName("Languages")
        public ArrayList<String> mLanguages = new ArrayList<String>();

        @SerializedName("Current_lan_index")
        public int mCurLanIdx = -1;

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

        public KeyMapList getKeyMapList() {
            KeyMapList keyMapList = mVT220KeyConfig;
            String strHostTypeName = "";
            if(mIsTN == 1) {
                strHostTypeName = mTermNameTN;
                if(strHostTypeName.compareTo(TESettingsInfo.TN3270TYPENAME) == 0) {
                    keyMapList = mTN3270KeyConfig;
                } else if(strHostTypeName.compareTo(TESettingsInfo.TN5250TYPENAME) == 0) {
                    keyMapList = mTN5250KeyConfig;
                }
            } else {
                strHostTypeName = mTermName;
                if(strHostTypeName.compareTo(TESettingsInfo.VT100TYPENAME) == 0) {
                    keyMapList = mVT100_102KeyConfig;
                } else if(strHostTypeName.compareTo(TESettingsInfo.VT102TYPENAME) == 0) {
                    keyMapList = mVT100_102KeyConfig;
                } else if(strHostTypeName.compareTo(TESettingsInfo.VT220TYPENAME) == 0) {
                    keyMapList = mVT220KeyConfig;
                } else if(strHostTypeName.compareTo(TESettingsInfo.VTANSITYPENAME) == 0) {
                    keyMapList = mVT220KeyConfig;
                }
            }
            return keyMapList;
        }

        public void resetKeyMapList() {
            String strHostTypeName = "";
            if(mIsTN == 1) {
                strHostTypeName = mTermNameTN;
                if(strHostTypeName.compareTo(TESettingsInfo.TN3270TYPENAME) == 0) {
                    //Todo:use 3270 default map
                    mTN3270KeyConfig.clear();
                    TESettingsInfo.fillMaps(mTN3270KeyConfig, IBMHost5250.gDefaultTN_5250KeyCodeMap_Taurus);
                    mTN3270KeyConfigCount = mTN3270KeyConfig.size();
                } else if(strHostTypeName.compareTo(TESettingsInfo.TN5250TYPENAME) == 0) {
                    mTN5250KeyConfig.clear();
                    TESettingsInfo.fillMaps(mTN5250KeyConfig, IBMHost5250.gDefaultTN_5250KeyCodeMap_Taurus);
                    mTN5250KeyConfigCount = mTN5250KeyConfig.size();
                }
            } else {
                strHostTypeName = mTermName;
                if(strHostTypeName.compareTo(TESettingsInfo.VT100TYPENAME) == 0) {
                    mVT100_102KeyConfig.clear();
                    TESettingsInfo.fillMaps(mVT100_102KeyConfig, CVT100.gDefaultVT100_102KeyCodeMap_Taurus);
                    mVT100_102KeyConfigCount = mVT100_102KeyConfig.size();
                } else if(strHostTypeName.compareTo(TESettingsInfo.VT102TYPENAME) == 0) {
                    mVT100_102KeyConfig.clear();
                    TESettingsInfo.fillMaps(mVT100_102KeyConfig, CVT100.gDefaultVT100_102KeyCodeMap_Taurus);
                    mVT100_102KeyConfigCount = mVT100_102KeyConfig.size();
                } else if(strHostTypeName.compareTo(TESettingsInfo.VT220TYPENAME) == 0) {
                    mVT220KeyConfig.clear();
                    TESettingsInfo.fillMaps(mVT220KeyConfig, CVT100.gDefaultVT220KeyCodeMap_Taurus);
                    mVT220KeyConfigCount = mVT220KeyConfig.size();
                } else if(strHostTypeName.compareTo(TESettingsInfo.VTANSITYPENAME) == 0) {
                    mVT220KeyConfig.clear();
                    TESettingsInfo.fillMaps(mVT220KeyConfig, CVT100.gDefaultVT220KeyCodeMap_Taurus);
                    mVT220KeyConfigCount = mVT220KeyConfig.size();
                }
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

        @SerializedName("nCharSet")
        public int mNCharSet = 0;	//0:Ansi, 1:UTF8

        @SerializedName("sendStringOnConnect")
        public String mVTSendtoHost = "";

        @SerializedName("isShowSession")
        public boolean mIsShowSessionNumber = false;

        @SerializedName("isShowSessionStatus")
        public boolean mIsShowSessionStatus = false;

        @SerializedName("isScreenPanning")
        public boolean mIsScreenPanning = true;

        @SerializedName("isShowWifiAlert")
        public boolean mIsShowWifiAlert = false;

        @SerializedName("wifiAlertCBValue")
        public int mNShowWifiAlertLevel = 10;

        @SerializedName("wifiAlertCBIndex")
        public int mNShowWifiAlertLevelIndex = 0;

        @SerializedName("isShowBatteryAlert")
        public boolean mIsShowBatteryAlert = false;

        @SerializedName("batteryAlertCBValue")
        public int mNShowBatteryAlertLevel = 10;

        @SerializedName("batteryAlertCBIndex")
        public int mNShowBatteryAlertLevelIndex = 0;

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

        @SerializedName("nTN3270KeyConfigCount")
        public int mTN3270KeyConfigCount = 0;

        @SerializedName("TN3270KeyConfig")
        public TN3270KeyMapList mTN3270KeyConfig = null;

        @SerializedName("nTN5250KeyConfigCount")
        public int mTN5250KeyConfigCount = 0;

        @SerializedName("TN5250KeyConfig")
        public TN5250KeyMapList mTN5250KeyConfig = null;

        @SerializedName("nVT220KeyConfigCount")
        public int mVT220KeyConfigCount = 0;

        @SerializedName("VT220KeyConfig")
        public VT220KeyMapList mVT220KeyConfig = null;

        @SerializedName("nVT100_102KeyConfigCount")
        public int mVT100_102KeyConfigCount = 0;

        @SerializedName("VT100_102KeyConfig")
        public VT100_102KeyMapList mVT100_102KeyConfig = null;

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

        @SerializedName("isShowNavigationBarOnFull")//Only for Android version
        public boolean mIsShowNavibarOnFullScreen = false;

        @SerializedName("isAutoPopSIPOnConn")
        public boolean mIsAutoPopSIPOnConn = false;

        @SerializedName("isShowTaskBarOnFullScreen")
        public boolean mIsShowStatusbarOnFull = false;

        @SerializedName("isShowWifiItem")
        public boolean mIsShowWifiIconOnFull = false;

        @SerializedName("isShowBatteryItem")
        public boolean mIsShowBatteryIconOnFull = false;

        @SerializedName("isUpdateWifi") //Both Wi-Fi and battery use this item in Android version
        public boolean mIsUpdateWiFiIconOnFull = false;

        @SerializedName("updateWifiCBIndex") // 0:1 min, 1:3 min, 2:5 min, 3:15 min, 4:30 min, 5:60 min
        public int mWiFiIntervalIndex = 3;//Both Wi-Fi and battery  use this item in Android version

        @SerializedName("updateWifiCBValue") // 1 min, 3 min, 5 min, 15 min, 30 min, 60 min
        public int mWiFiIntervalValue = 15;//Both Wi-Fi and battery  use this item in Android version

        @SerializedName("isUpdateBattery")//not use this item in Android version
        public boolean mIsUpdateBatteryIconOnFull = false;

        @SerializedName("updateBatteryCBIndex") // 0:1 min, 1:3 min, 2:5 min, 3:15 min, 4:30 min, 5:60 min
        public int mBatteryIntervalIndex = 3;//not use this item in Android version

        @SerializedName("updateBatteryCBValue") // 1 min, 3 min, 5 min, 15 min, 30 min, 60 min
        public int mBatteryIntervalValue = 15;//not use this item in Android version

        @SerializedName("bScanControl")
        public boolean mIsScanControl = false;

        @SerializedName("isControlFeedbackByCmd")
        public boolean mIsFeedbackControlByCmd = false;

        @SerializedName("protectorPassword")
        public String mProtectorPassword = "";

        @SerializedName("isEnableProtect")
        public boolean mIsProtectedAccessControl = false;

        @SerializedName("isProtectSettings")
        public boolean mIsProtectedSettings = false;

        @SerializedName("isProtectExit")
        public boolean mIsProtectedExit = false;

        @SerializedName("isProtectExitFullScreen")
        public boolean mIsProtectedExitFullScreen = false;

        public boolean SSH = false;
        public String SSHName = "";
        public String SSHPassword="";
        public ReaderParam g_ReaderParam = new ReaderParam();

        //Not sync
        public class ReaderParam {
            public boolean isEnableScannerByESCCmd = false; //Not used in TE C++

            @SerializedName("goodFeedBackType")
            public int mGoodFBType = 0; //0:By command, 1:By Text

            @SerializedName("goodFeedBackESC")
            public String mGoodFeedBackESC = "";

            @SerializedName("goodFeedBackText")
            public String mGoodFeedBackText = "";

            @SerializedName("goodFeedBackSoundPath")
            public String mGoodSoundFile = "";

            @SerializedName("goodFeedVibrationTime")
            public int mGoodVBIndex = 0; //0~11, 0 sec, 0.5 sec~ 5.0 sec

            @SerializedName("errorFeedBackType")
            public int mErrorFBType = 0; //0:By command, 1:By Text

            @SerializedName("errorFeedBackESC")
            public String mErrorFeedBackESC = "";

            @SerializedName("errorFeedBackText")
            public String mErrorFeedBackText = "";

            @SerializedName("errorFeedBackSoundPath")
            public String mErrorSoundFile = "";

            @SerializedName("errorFeedVibrationTime")
            public int mErrorVBIndex = 0; //0~11, 0 sec, 0.5 sec~ 5.0 sec

            @SerializedName("scannerEnableESC")
            public String mScannerEnableCmd = "";

            @SerializedName("scannerDisableESC")
            public String mScannerDisableCmd = "";

            @SerializedName("cmdScannerSoundPath")
            public String mScannerSoundFile = "";

            @SerializedName("cmdScannerVibrationTime")
            public int mScannerVBIndex = 0; //0~11, 0 sec, 0.5 sec~ 5.0 sec
        }
    }
}
