package Terminals;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.te.UI.CipherUtility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import TelnetIBM.IBMHost5250;
import TelnetVT.CVT100;
import Terminals.TESettings.SessionSetting;

/**
 * Created by Franco.Liu on 2015/1/29.
 */
public class TESettingsInfo {
    final static String TN5250TYPENAME = "ibm5250";//stdActivityRef.getCurrActivity().getResources().getString(R.string.IB5250Val);
    final static String TN3270TYPENAME = "ibm3270";//stdActivityRef.getCurrActivity().getResources().getString(R.string.IBM3270Val);
    final static String VT220TYPENAME = "vt220";//stdActivityRef.getCurrActivity().getResources().getString(R.string.VT220Val);
    final static String VT100TYPENAME = "vt100";//stdActivityRef.getCurrActivity().getResources().getString(R.string.VT100Val);
    final static String VT102TYPENAME = "vt102";//stdActivityRef.getCurrActivity().getResources().getString(R.string.VT102Val);
    final static String VTANSITYPENAME = "ansi";//stdActivityRef.getCurrActivity().getResources().getString(R.string.ANSIVal);

    final public static int MAX_SESSION_COUNT = 5;
    public static final boolean gIsActivation = false;
    public static final String _NAME = "TerminalEmulation";
    public static final int TERM_TAB = 0;
    public static final int TERM_ENTER = 1;

    //0: reject (default), 1: Truncate, 2: splite to next field
    public static final int FDLEN_REJECT = 0;
    public static final int FDLEN_TRUN = 1;
    public static final int FDLEN_SPLT = 2;

    //1: User Cust Local name, 0:Use default (means not set)
    public static final int DEVNAME_DEFAULT = 0;
    public static final int DEVNAME_CUST = 1;

    //0:Courier New, 1:Lucida Console, 2:Excalibur Monospace, 3:NetTerm ANSI, 4:NetTerm OEM
    public static final int COURIER_NEW = 0;
    public static final int LU_CONSOLE = 1;
    public static final int EXCA_MONO = 2;
    public static final int NET_ANSI = 3;
    public static final int NET_OEM = 4;

    final private static String TE_JASONFILE_NAME = "TE_settings.json";
    final private static String mDefaultSettingFilename = "TE_Default_setting.json";

    static TESettings mTESettings = null;
    private static SharedPreferences mSp = null;
    private static int mCurrentSessionIndex;
    private static ArrayList<Long> mListVBTime = new ArrayList<>();

    //Persist values, not in jason
    private static final String SNMBER_LEFT_MARGIN_KEY = "SNMBER_LEFT_MARGIN_KEY";
    private static final String SNMBER_TOP_MARGIN_KEY = "SNMBER_TOP_MARGIN_KEY";
    private static final String WIFI_LEFT_MARGIN_KEY = "WIFI_LEFT_MARGIN_KEY";
    private static final String WIFI_TOP_MARGIN_KEY = "WIFI_TOP_MARGIN_KEY";
    private static final String BATT_LEFT_MARGIN_KEY = "BATT_LEFT_MARGIN_KEY";
    private static final String BATT_TOP_MARGIN_KEY = "BATT_TOP_MARGIN_KEY";
    private static final String EDIT_PROFILE_SHOWED = "EDIT_PROFILE_SHOWED";
    private static final String ADD_SESSION_SHOWED = "ADD_SESSION_SHOWED";
    private static final String DEL_SESSION_SHOWED = "DEL_SESSION_SHOWED";
    private static final String DEL_RESET_FULL_SHOWED = "DEL_RESET_FULL_SHOWED";
    private static final String EXPORT_PATH = "EXPORT_PATH";
    private static final String IMPORT_PATH = "IMPORT_PATH";
    private static final String SSH_KEY_PATH = "SSH_KEY_PATH";

    public static boolean loadSessionSettings(Context context) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(_NAME, Context.MODE_PRIVATE);
        }
        mListVBTime.clear();
        mListVBTime.add(500l);
        mListVBTime.add(1000l);
        mListVBTime.add(1500l);
        mListVBTime.add(2000l);
        mListVBTime.add(2500l);
        mListVBTime.add(3000l);
        mListVBTime.add(3500l);
        mListVBTime.add(4000l);
        mListVBTime.add(4500l);
        mListVBTime.add(5000l);
        mListVBTime.add(5500l);
        File teJsonFile = new File(CipherUtility.getTESettingsPath(context), TE_JASONFILE_NAME);
        if (!teJsonFile.exists()) {  //Copy default TE_settings.json from asset to internal
            try {
                teJsonFile.getParentFile().mkdirs();
                InputStream inputStream = context.getAssets().open(TE_JASONFILE_NAME);
                FileOutputStream fileOutputStream = new FileOutputStream(teJsonFile.getAbsolutePath());
                CipherUtility.copyFile(inputStream, fileOutputStream);
                inputStream.close();
                inputStream = null;
                fileOutputStream.flush();
                fileOutputStream.close();
                fileOutputStream = null;
            }
            catch (Exception e) {
                return false;
            }
        }
        return importSettings(teJsonFile);
    }

    private static boolean importSettings(File teJsonFile) {
        mTESettings = deSerialize(teJsonFile);
        if (mTESettings == null || mTESettings.SETTINGS == null)
            return false;

        //Get active session index and process KeyMapList
        for (int idxSession = 0; idxSession < mTESettings.SETTINGS.size(); ++idxSession) {
            SessionSetting setting = mTESettings.SETTINGS.get(idxSession);
            if (setting.mIsSelected)
                mCurrentSessionIndex = idxSession;
        }
        return true;
    }

    public static void fillMaps(KeyMapList keyMapListDest, Map<Integer, Integer> keyCodeMapSrc) {
        Iterator entries = keyCodeMapSrc.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Integer phyKeyCode = (Integer) entry.getKey();
            Integer serverKeycode = (Integer) entry.getValue();
            keyMapListDest.add(new KeyMapItem(serverKeycode, phyKeyCode));
        }
    }

    private static boolean createJsonFile(File file) {
        try {
            if (file.createNewFile()) {
                //Set active session index
                for (int idxSession = 0; idxSession < mTESettings.SETTINGS.size(); ++idxSession) {
                    if (mCurrentSessionIndex == idxSession)
                        mTESettings.SETTINGS.get(idxSession).mIsSelected = true;
                    else
                        mTESettings.SETTINGS.get(idxSession).mIsSelected = false;
                }
                serialize(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void saveSessionSettings(Context context) {
        if (mTESettings == null || mTESettings.SETTINGS == null)
            return;
        deleteJsonFile(context);
        File teJsonFile = new File(CipherUtility.getTESettingsPath(context), TE_JASONFILE_NAME);
        createJsonFile(teJsonFile);
    }

    public static boolean exportSessionSettings(String path) {
        if (mTESettings == null || mTESettings.SETTINGS == null)
            return false;
        File teJsonFile = new File(path);
        if(teJsonFile.isDirectory() || teJsonFile.exists() == true)
            return false;
        return createJsonFile(teJsonFile);
    }

    public static boolean importSessionSettings(String path) {
        File teJsonFile = new File(path);
        if(teJsonFile.isDirectory() || teJsonFile.exists() == false)
            return false;
        return importSettings(teJsonFile);
    }

    public static void deleteJsonFile(Context context) {
        File teJsonFile = new File(CipherUtility.getTESettingsPath(context), TE_JASONFILE_NAME);
        if (teJsonFile.exists()) {
            teJsonFile.delete();
        }
    }

    private static TESettings deSerialize(File teJsonFile) {
        try {
            FileInputStream inStream = new FileInputStream(teJsonFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            TESettings teSettings = gson.fromJson(reader, TESettings.class);
            for (int idxSetting = 0; idxSetting < teSettings.SETTINGS.size(); idxSetting++) {
                SessionSetting setting = teSettings.SETTINGS.get(idxSetting);
                processAfterLoadSetting(setting);
            }
            return teSettings;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void processAfterLoadSetting(SessionSetting setting) {
        for (int idxMacro = 0; idxMacro < setting.mMacroList.size(); idxMacro++) {
            MacroItem item = setting.mMacroList.get(idxMacro);
            item.syncFromDeSerialize();
        }
        if(setting.mTN3270KeyConfig == null) {
            setting.mTN3270KeyConfig = new TN3270KeyMapList();
            if(stdActivityRef.gIs53Keys) {
                fillMaps(setting.mTN3270KeyConfig, IBMHost5250.gDefaultTN_3270KeyCodeMap_Taurus);
            } else {
                fillMaps(setting.mTN3270KeyConfig, IBMHost5250.gDefaultTN_3270KeyCodeMap);
            }

            setting.mTN3270KeyConfigCount = setting.mTN3270KeyConfig.size();
        }
        if(setting.mTN5250KeyConfig == null) {
            setting.mTN5250KeyConfig = new TN5250KeyMapList();
            if(stdActivityRef.gIs53Keys) {
                fillMaps(setting.mTN5250KeyConfig, IBMHost5250.gDefaultTN_5250KeyCodeMap_Taurus);
            } else {
                fillMaps(setting.mTN5250KeyConfig, IBMHost5250.gDefaultTN_5250KeyCodeMap);
            }
            setting.mTN5250KeyConfigCount = setting.mTN5250KeyConfig.size();
        }
        if(setting.mVT100_102KeyConfig == null) {
            setting.mVT100_102KeyConfig = new VT100_102KeyMapList();
            if(stdActivityRef.gIs53Keys) {
                fillMaps(setting.mVT100_102KeyConfig, CVT100.gDefaultVT100_102KeyCodeMap_Taurus);
            } else {
                fillMaps(setting.mVT100_102KeyConfig, CVT100.gDefaultVT100_102KeyCodeMap);
            }
            setting.mVT100_102KeyConfigCount = setting.mVT100_102KeyConfig.size();
        }
        if(setting.mVT220KeyConfig == null) {
            setting.mVT220KeyConfig = new VT220KeyMapList();
            if(stdActivityRef.gIs53Keys) {
                fillMaps(setting.mVT220KeyConfig, CVT100.gDefaultVT220KeyCodeMap_Taurus);
            } else {
                fillMaps(setting.mVT220KeyConfig, CVT100.gDefaultVT220KeyCodeMap);
            }
            setting.mVT220KeyConfigCount = setting.mVT220KeyConfig.size();
        }
    }

    private static void serialize(File teJsonFile) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(teJsonFile, true);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String Sjson = gson.toJson(mTESettings);
            outputStream.write(Sjson.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SessionSetting createNewDefaultSessionSetting(Context context) {
        SessionSetting setting = null;
        try {
            InputStream inputStream = context.getAssets().open(mDefaultSettingFilename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            setting = gson.fromJson(reader, SessionSetting.class);
            processAfterLoadSetting(setting);
        } catch (Exception e) {

        }

        return setting;
    }

    public static ArrayList<TESettings.CSsh_Keys> getCommonSSHKeys() {
        return mTESettings.Common.mSSHKeyFiles;
    }

    public static void setCommonSSHKeys(ArrayList<TESettings.CSsh_Keys> sshKeys) {
        mTESettings.Common.mSSHKeyFiles = sshKeys;
    }

    public static ArrayList<String> getLanguages() {
        return mTESettings.Common.mLanguages;
    }

    public static int getCurLanguageIdx() {
        return mTESettings.Common.mCurLanIdx;
    }

    public static void setCurLanguageIdx(int index) {
        mTESettings.Common.mCurLanIdx = index;
    }

    public static void setSessionIndex(int Index) {
        mCurrentSessionIndex = Index;
    }

    public static void setAccessCtrlProtectedPassword(String password) { //always use first session
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        Setting.mProtectorPassword = password;
    }

    public static String getAccessCtrlProtectedPassword() { //always use first session
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        return Setting.mProtectorPassword;
    }

    public static void setAccessCtrlProtect(boolean bProctect) {
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        Setting.mIsProtectedAccessControl = bProctect;
    }

    public static boolean getIsAccessCtrlProtected() { //always use first session
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        return Setting.mIsProtectedAccessControl;
    }

    public static void setSettingsProtect(boolean bProctect) {
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        Setting.mIsProtectedSettings = bProctect;
    }

    public static boolean getIsSettingsProtect() {
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        return Setting.mIsProtectedSettings;
    }

    public static void setExitProtect(boolean bProctect) {
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        Setting.mIsProtectedExit = bProctect;
    }

    public static boolean getIsExitProtect() {
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        return Setting.mIsProtectedExit;
    }

    public static void setExitFullScreenProtect(boolean bProctect) {
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        Setting.mIsProtectedExitFullScreen = bProctect;
    }

    public static boolean getIsExitFullScreenProtect() {
        SessionSetting Setting = mTESettings.getSessionSetting(0);
        return Setting.mIsProtectedExitFullScreen;
    }

    public static int getSessionIndex() {
        return mCurrentSessionIndex;
    }

    public static int getSessionCount() {
        return mTESettings.SETTINGS.size();
    }

    public static SessionSetting getSessionSetting(int index) {
        return mTESettings.getSessionSetting(index);
    }

    public static void addSession(SessionSetting snSetting) {
        if (snSetting != null)
            mTESettings.SETTINGS.add(snSetting);
    }

    public static void removeSession(int pos) {
        mTESettings.SETTINGS.remove(pos);
        if (mCurrentSessionIndex >= pos)
            --mCurrentSessionIndex;
    }

    public static boolean getIsHostTNByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsTN == 1;
    }

    public static String getTNHostTypeNameByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mTermNameTN;
    }

    public static String getHostTypeNameByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        if(Setting.mIsTN == 1) {
            return Setting.mTermNameTN;
        }
        return Setting.mTermName;
    }

    public static int getHostTermLoginByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mTermLogin;
    }

    public static boolean getUpperCaseByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBUpperCase;
    }

    public static String getHostAddrByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mHostIP;
    }

    //SendtoHost
    public static byte[] getVTHostSendToHostByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mVTSendtoHost.getBytes();
    }

    public static String getHostPortByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.getHostPort();
    }

    //0:ANSI , 1:utf-8
    public static int getHostCharSetByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNCharSet;
    }

    public static Boolean getHostIsLineBufferByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return (Setting.mLineBuffer == 1);
    }

    public static boolean getHostIsLocalEchoByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBEcho;
    }

    public static boolean getHostIsWriteLogkByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsSaveLog;
    }

    public static boolean getHostIsShowWifiAlertByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsShowWifiAlert;
    }

    public static boolean getHostIsShowBatteryAlertByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsShowBatteryAlert;
    }

    public static int getHostShowWifiAltLevelByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNShowWifiAlertLevel;
    }

    public static int getHostShowBatteryAltLevelByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNShowBatteryAlertLevel;
    }

    public static int getHostFontWidthByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNFontWidth;
    }

    public static int getHostFontsColorByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.getFontColor();
    }

    public static int getHostFontsTypeByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNFontType;
    }

    public static int getHostBgColorByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.getBGColor();
    }

    public static Boolean getHostIsShowMacroByIndex(int index) {
        if(stdActivityRef.gIsActivate == false) {
            return false;
        }
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsActMacro;
    }

    public static ArrayList<MacroItem> getHostMacroListByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mMacroList;
    }

    public static void setHostMacroListByIndex(int index, ArrayList<MacroItem> macroList) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mMacroList = macroList;
    }

    public static boolean getHostIsCursorTrackByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsCursorTracking;
    }

    public static SessionSetting.AutoTrackType getHostAutoTrackTypeByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.getAutoTrackType();
    }

    public static int getHostLockerRowIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNCursorLockRow;
    }

    public static int getHostLockerColIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNCursorLockCol;
    }

    public static Boolean getHostIsAutoconnectByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBAutoConnect;
    }

    public static Boolean getHostIsAutoFullScreenOnConnByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsAutoFullscreenOnConn;
    }

    public static Boolean getHostIsShowNavibarOnFullScreenByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsShowNavibarOnFullScreen;
    }

    public static Boolean getHostIsAutoPopSIPOnConnByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsAutoPopSIPOnConn;
    }

    public static Boolean getHostShowTaskBarOnFullScreenByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsShowStatusbarOnFull;
    }

    public static Boolean getHostShowWiFiIconOnFullScreenByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsShowWifiIconOnFull;
    }

    public static Boolean getHostShowBattIconOnFullScreenByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsShowBatteryIconOnFull;
    }

    public static boolean getIsUpdateWiFiAndtBatteryByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsUpdateWiFiIconOnFull;
    }

    //Miniutes
    public static int getUpdateWiFiAndtBatteryIntervalByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mWiFiIntervalValue;
    }

    public static Boolean getHostIsAutoSignByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBAutoSignOn;
    }

    public static boolean getHostIsShowSessionNumber(int index) {
        if(stdActivityRef.gIsActivate == false) {
            return false;
        }
        SessionSetting setting = mTESettings.getSessionSetting(index);
        return setting.mIsShowSessionNumber;
    }

    public static boolean getHostIsScreenPanningByIndex(int index) {
        SessionSetting setting = mTESettings.getSessionSetting(index);
        return setting.mIsScreenPanning;
    }

    public static boolean getHostIsShowSessionStatus(int index) {
        SessionSetting setting = mTESettings.getSessionSetting(index);
        return setting.mIsShowSessionStatus;
    }

    public static String getHostLoginPromtByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNamePrompt;
    }

    public static String getHostPassWordPromtByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mPassPrompt;
    }

    public static String getHostUserNameByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mLoginName;
    }

    public static String getHostPassWordByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mLoginPassword;
    }

    public static boolean getHostIsKeepAliveByIndex(int nSessionIndex) {
        SessionSetting Setting = mTESettings.getSessionSetting(nSessionIndex);
        return Setting.mNetKeepAlive;
    }

    public static boolean getHostIsDetectOFRByIndex(int nSessionIndex) {
        SessionSetting Setting = mTESettings.getSessionSetting(nSessionIndex);
        return Setting.mIsDetectOutRange;
    }

    public static boolean getHostIsSshEnableByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mUseSSH;
    }

    public static String getHostSshUserByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mSSHName;
    }

    public static String getHostSshPasswordByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mSSHPassword;
    }

    public static int getHostCousorTypeByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNCursorType;
    }

    public static boolean getHostIsFeedbackByTextCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsFeedbackControlByCmd;
    }

    public static boolean getHostIsGoodFeedbackByTextByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mGoodFBType == 1;
    }

    public static boolean getHostIsGoodFeedbackByCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mGoodFBType == 0;
    }

    public static boolean getHostIsErrorFeedbackByTextByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mErrorFBType == 1;
    }

    public static boolean getHostIsErrorFeedbackByCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mErrorFBType == 0;
    }

    public static String getHostGoodFeedbackCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mGoodFeedBackESC;
    }

    public static String getHostErrorFeedbackCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mErrorFeedBackESC;
    }

    public static String getHostGoodFeedbackTextByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mGoodFeedBackText;
    }

    public static String getHostErrorFeedbackTextByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mErrorFeedBackText;
    }

    public static String getHostGoodFeedbackSoundByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mGoodSoundFile;
    }

    public static long getHostGoodFBVBByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return mListVBTime.get(Setting.g_ReaderParam.mGoodVBIndex);
    }

    public static String getHostErrorFeedbackSoundByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mErrorSoundFile;
    }

    public static long getHostErrorFBVBByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return mListVBTime.get(Setting.g_ReaderParam.mErrorVBIndex);
    }

    public static Boolean getHostIsReaderControlByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsScanControl;
    }

    public static String getHostEnableReaderCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mScannerEnableCmd;
    }

    public static String getHostDisableReaderCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mScannerDisableCmd;
    }

    public static String getHostReaderSoundByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.mScannerSoundFile;
    }

    public static long getHostReaderVBByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return mListVBTime.get(Setting.g_ReaderParam.mScannerVBIndex);
    }

    public static int getCheckFieldLength(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mCheckFieldLength;
    }

    //0: single byte char, 1:TC, 2:SC, 3:Kor, 4:Jap, 5:Gre, 6:Fre
    public static int getTELanguage(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mTELanguage;
    }

    public static boolean getIsIBMAutoUnlock(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBIBMAutoReset;
    }

    public static int getErrorRow(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNErrorRowIndexg;
    }

    public static boolean getPopupErrorDialog(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.misPopUpErrorDialog;
    }

    public static int getDevNameType(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mDevNameType;
    }

    public static String getCustDevName(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mDevName;
    }

    public static KeyMapList getKeyMapListByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.getKeyMapList();
    }

    public static void setExportSettingsPath(String path) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(EXPORT_PATH, path);
        editor.commit();
    }

    public static String getSSHKeyPath() {
        return mSp.getString(SSH_KEY_PATH, "");
    }

    public static void setSSHKeyPath(String path) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(SSH_KEY_PATH, path);
        editor.commit();
    }

    public static String getExportSettingsPath() {
        return mSp.getString(EXPORT_PATH, "");
    }

    public static void setImportSettingsPath(String path) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(IMPORT_PATH, path);
        editor.commit();
    }

    public static String getImportSettingsPath() {
        return mSp.getString(IMPORT_PATH, "");
    }

    public static void setSessionNumberLoc(int leftMargin, int topMargin) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putInt(SNMBER_LEFT_MARGIN_KEY, leftMargin);
        editor.putInt(SNMBER_TOP_MARGIN_KEY, topMargin);
        editor.commit();
    }

    public static void setWiFiIconLoc(int leftMargin, int topMargin) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putInt(WIFI_LEFT_MARGIN_KEY, leftMargin);
        editor.putInt(WIFI_TOP_MARGIN_KEY, topMargin);
        editor.commit();
    }

    public static void setBattIconLoc(int leftMargin, int topMargin) {
        SharedPreferences.Editor editor = mSp.edit();
        editor.putInt(BATT_LEFT_MARGIN_KEY, leftMargin);
        editor.putInt(BATT_TOP_MARGIN_KEY, topMargin);
        editor.commit();
    }

    public static int getSessionNumberLocLeft() {
        return mSp.getInt(SNMBER_LEFT_MARGIN_KEY, 0);
    }

    public static int getSessionNumberLocTop() {
        return mSp.getInt(SNMBER_TOP_MARGIN_KEY, 0);
    }

    public static int getWiFiIconLocLeft() {
        return mSp.getInt(WIFI_LEFT_MARGIN_KEY, 100);
    }

    public static int getWiFiIconLocTop() {
        return mSp.getInt(WIFI_TOP_MARGIN_KEY, 0);
    }

    public static int getBattIconLocLeft() {
        return mSp.getInt(BATT_LEFT_MARGIN_KEY, 200);
    }

    public static int getBattIconLocTop() {
        return mSp.getInt(BATT_TOP_MARGIN_KEY, 0);
    }

    private static boolean showFirstByTag(final String tag) {
        boolean bShowed = mSp.getBoolean(tag, false);
        if(!bShowed) {
            SharedPreferences.Editor editor = mSp.edit();
            editor.putBoolean(tag, true);
            editor.commit();
        }
        return bShowed == false;
    }

    public static boolean showEditProfile() {
        return showFirstByTag(EDIT_PROFILE_SHOWED);
    }

    public static boolean showAddSession() {
        return showFirstByTag(ADD_SESSION_SHOWED);
    }

    public static boolean showDelSession() {
        return showFirstByTag(DEL_SESSION_SHOWED);
    }

    public static boolean showResetFullScreen() {
        return showFirstByTag(DEL_RESET_FULL_SHOWED);
    }

    public static String getDefaultIP() {
        return "192.168.1.100";
    }
}