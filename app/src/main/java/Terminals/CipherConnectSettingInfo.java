package Terminals;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import Terminals.TESettings.SessionSetting;

/**
 * Created by Franco.Liu on 2015/1/29.
 */
public class CipherConnectSettingInfo {
    final public static int MAX_SESSION_COUNT = 5;
    public static final boolean _DEBUG = false;
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

    final private static String mSettingFilename = "TE_settings.json";
    final private static String mDefaultSettingFilename = "TE_Default_setting.json";
    public static int LastHostNumber = 0;
    static TESettings mTESettings = null;
    private static Context mContext = null;
    private static SharedPreferences _sp = null;
    private static int mCurrentSessionIndex;
    JsonArray GsonSetting = new JsonArray();

    static void deleteCurrentSetting() {
        File teJsonFile = new File(mContext.getFilesDir(), mSettingFilename);
        if (teJsonFile.exists()) {
            teJsonFile.delete();
        }
    }

    private static TESettings deSerialize(File teJsonFile) {
        try {
            FileInputStream inStream = new FileInputStream(teJsonFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
            Gson gson = new Gson();
            TESettings teSettings = (TESettings) gson.fromJson(reader, TESettings.class);
            return teSettings;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void serialize(File teJsonFile) {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(teJsonFile, true);
            Gson gson = new Gson();
            String Sjson = gson.toJson(mTESettings);
            outputStream.write(Sjson.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean initSessionParms(Context context) {
        mContext = context;
        try {
            File teJsonFile = new File(mContext.getFilesDir(), mSettingFilename);
            if (!teJsonFile.exists()) {  //Copy default TE_settings.json from asset to internal
                InputStream inputStream = mContext.getAssets().open(mSettingFilename);
                FileOutputStream fileOutputStream = new FileOutputStream(teJsonFile.getAbsolutePath());
                byte[] buffer = new byte[100];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }
                inputStream.close();
                inputStream = null;
                fileOutputStream.flush();
                fileOutputStream.close();
                fileOutputStream = null;
            }

            mTESettings = deSerialize(teJsonFile);
            if (mTESettings == null || mTESettings.SETTINGS == null)
                return false;

            //Get active session index
            for (int idxSession = 0; idxSession < mTESettings.SETTINGS.size(); ++idxSession) {
                if (mTESettings.SETTINGS.get(idxSession).mIsSelected)
                    mCurrentSessionIndex = idxSession;
            }
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    public static void deleteCurSessionParms() {
        deleteCurrentSetting();
    }

    public static SessionSetting createNewDefaultSessionSetting() {
        SessionSetting setting = null;
        try {
            InputStream inputStream = mContext.getAssets().open(mDefaultSettingFilename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            Gson gson = new Gson();
            setting = gson.fromJson(reader, SessionSetting.class);
        } catch (Exception e) {

        }

        return setting;
    }

    public static void SessionSettingSave() {
        if (mTESettings == null || mTESettings.SETTINGS == null)
            return;
        deleteCurrentSetting();
        File teJsonFile = new File(mContext.getFilesDir(), mSettingFilename);
        try {
            if (teJsonFile.createNewFile()) {
                //Set active session index
                for (int idxSession = 0; idxSession < mTESettings.SETTINGS.size(); ++idxSession) {
                    if (mCurrentSessionIndex == idxSession)
                        mTESettings.SETTINGS.get(idxSession).mIsSelected = true;
                    else
                        mTESettings.SETTINGS.get(idxSession).mIsSelected = false;
                }
                serialize(teJsonFile);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void SetSessionIndex(int Index) {
        mCurrentSessionIndex = Index;
    }

    public static int GetSessionIndex() {
        return mCurrentSessionIndex;
    }

    public static int GetSessionCount() {
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

    public static void initSharedPreferences(Context c) {
        if (_sp == null) {
            _sp = c.getSharedPreferences(_NAME, 0);
            LastHostNumber = getLastDeviceNumber(c);
        }
    }

    public static void PlusLastDeviceNumber(Context c) {
        LastHostNumber = getLastDeviceNumber(c);
        LastHostNumber++;
        setLastDeviceNumber(c, LastHostNumber);
        LastHostNumber = getLastDeviceNumber(c);
    }

    public static int getLastDeviceNumber(Context c) {
        initSharedPreferences(c);
        return _sp.getInt("LastDeviceNumber", 0);
    }

    public static void setLastDeviceNumber(Context c, int Number) {
        initSharedPreferences(c);
        Editor editor = _sp.edit();
        editor.putInt("LastDeviceName", Number);
        editor.commit();
    }

    public static boolean getIsHostTNByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsTN == 1;

    }

    public static String getTNHostTypeNameByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mTermNameTN;
    }

    public static void SetTNHostTypeNameByIndex(int index, String HostType) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mTermNameTN = HostType;
    }


    public static String getHostTypeNameByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mTermName;

    }

    public static void SetHostTypeNameByIndex(int index, String HostType) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mTermName = HostType;
    }

    public static int getHostTermLoginByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mTermLogin;

    }

    public static void SetHostTermLoginByIndex(int index, int nTerm) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mTermLogin = nTerm;
    }

    public static boolean getUpperCaseByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBUpperCase;
    }

    public static String getHostAddrByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mHostIP;
    }

    public static void SetHostAddrByIndex(int index, String HostAddr) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mHostIP = HostAddr;
    }

    //SendtoHost
    public static byte[] GetHostSendToHostByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.SendtoHost;
    }

    public static void SetHostSendToHostByIndex(int index, byte[] Data) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.SendtoHost = Data;
    }

    public static String getHostPortByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.getHostPort();
    }

    //0:ANSI , 1:utf-8
    public static int getHostCharSetByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.nCharSet;
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

    public static Boolean getHostIsShowMacroByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.isShowMacro;
    }

    public static void SetHostIsShowMacroByIndex(int index, boolean IsShowMacro) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.isShowMacro = IsShowMacro;
    }


    public static boolean getHostIsCursorTrackByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.bCursorTracking;
    }

    public static void SetHostIsCursorTrackByIndex(int index, boolean IsCursorTrack) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.bCursorTracking = IsCursorTrack;
    }

    public static Boolean getHostIsAutoconnectByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBAutoConnect;
    }

    public static Boolean getHostIsAutoSignByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBAutoSignOn;
    }

    public static void SetHostIsAutoSignByIndex(int index, Boolean IsAutoSign) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mBAutoSignOn = IsAutoSign;
    }

    public static String getHostLoginPromtByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNamePrompt;
    }

    public static void SetHostLoginPromtByIndex(int index, String LoginPromt) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mNamePrompt = LoginPromt;
    }

    public static String getHostPassWordPromtByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mPassPrompt;
    }

    public static void SetHostPassWordPromtByIndex(int index, String PassWordPromt) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mPassPrompt = PassWordPromt;
    }


    public static String getHostUserNameByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mLoginName;
    }

    public static void SetHostUserNameByIndex(int index, String Name) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mLoginName = Name;
    }

    public static String getHostPassWordByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mLoginPassword;
    }

    public static void SetHostPassWordByIndex(int index, String PassWord) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.mLoginPassword = PassWord;
    }


    public static boolean getHostIsSshEnableByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.SSH;
    }

    public static void SetHostIsSshEnableByIndex(int index, boolean IsEnable) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.SSH = IsEnable;
    }

    public static String getHostSshUserByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.SSHName;
    }

    public static void SetHostSshUserByIndex(int index, String UserName) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.SSHName = UserName;
    }

    public static String getHostSshPasswordByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.SSHPassword;
    }

    public static void SetHostSshPasswordByIndex(int index, String PassWord) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.SSHPassword = PassWord;
    }

    public static int getHostCousorTypeByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.CursorType;
    }

    //0:Default, 1:Underline, 2:Block
    public static void SetHostCousorTypeByIndex(int index, int nCousorType) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.CursorType = nCousorType;
    }
    
    /*
      getHostIsGoodfeedbackByIndex
      SetHostIsGoodfeedbackByIndex
      getHostIsErrorfeedbackByIndex
      SetHostIsErrorfeedbackByIndex
      
      GetHostGoodfeedbackCmdByIndex
      SetHostGoodfeedbackCmdByIndex
      GetHostErrorfeedbackCmdByIndex
      SetHostErrorfeedbackCmdByIndex
      
     */

    public static String GetHostGoodfeedbackCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.goodFeedBackESC;
    }

    public static void SetHostGoodfeedbackCmdByIndex(int index, String sData) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.g_ReaderParam.goodFeedBackESC = sData;
    }

    public static String GetHostErrorfeedbackCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.errorFeedBackESC;
    }

    public static void SetHostErrorfeedbackCmdByIndex(int index, String sData) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.g_ReaderParam.errorFeedBackESC = sData;
    }

	   
    /*Boolean IsGoodfeedback=false;
	   Boolean IsErrorfeedback=false;
	   byte[] GoodfeedbackCmd=null;  
	   byte[] ErrorfeedbackCmd=null; 
	    byte[] EnableReaderCmd=null;  
	   byte[] DisableReaderCmd=null;
	    */

    public static Boolean getHostIsReaderControlByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.isEnableScannerByESCCmd;
    }

    public static void SetHostIsReaderControlByIndex(int index, Boolean IsEnable) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.g_ReaderParam.isEnableScannerByESCCmd = IsEnable;
    }
	    
	    /*
	     GetHostEnableReaderCmdByIndex
	     SetHostEnableReaderCmdByIndex
	     
	     GetHostDisableReaderCmdByIndex
	     SetHostDisableReaderCmdByIndex
	     GetHostDisableReaderCmdByIndex
	     SetHostDisableReaderCmdByIndex
	     
	     */

    public static String GetHostEnableReaderCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.scannerEnableESC;
    }

    public static void SetHostEnableReaderCmdByIndex(int index, String sData) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.g_ReaderParam.scannerEnableESC = sData;
    }

    public static String GetHostDisableReaderCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.scannerDisableESC;
    }

    public static void SetHostDisableReaderCmdByIndex(int index, String sData) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        Setting.g_ReaderParam.scannerDisableESC = sData;
    }

    public static int getCheckFieldLength(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mCheckFieldLength;
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
}
