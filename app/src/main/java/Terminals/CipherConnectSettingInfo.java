package Terminals;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

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

    //0:Courier New, 1:Lucida Console, 2:Excalibur Monospace, 3:NetTerm ANSI, 4:NetTerm OEM
    public static final int COURIER_NEW = 0;
    public static final int LU_CONSOLE = 1;
    public static final int EXCA_MONO = 2;
    public static final int NET_ANSI = 3;
    public static final int NET_OEM = 4;

    final private static String mSettingFilename = "TE_settings.json";
    final private static String mDefaultSettingFilename = "TE_Default_setting.json";
    static TESettings mTESettings = null;
    private static Context mContext = null;
    private static SharedPreferences _sp = null;
    private static int mCurrentSessionIndex;

    public static boolean loadSessionSettings(Context context) {
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

    public static void saveSessionSettings() {
        if (mTESettings == null || mTESettings.SETTINGS == null)
            return;
        deleteJsonFile();
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

    public static void deleteJsonFile() {
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

    public static void setSessionIndex(int Index) {
        mCurrentSessionIndex = Index;
    }

    public static int GetSessionIndex() {
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

    public static void initSharedPreferences(Context c) {
        if (_sp == null) {
            _sp = c.getSharedPreferences(_NAME, 0);
        }
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
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mIsActMacro;
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

    public static Boolean getHostIsAutoSignByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mBAutoSignOn;
    }

    public static boolean getHostIsShowSessionNumber(int index) {
        SessionSetting setting = mTESettings.getSessionSetting(index);
        return setting.mIsShowSessionNumber;
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
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mLoginPassword;
    }

    public static boolean getHostIsSshEnableByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.SSH;
    }

    public static String getHostSshUserByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.SSHName;
    }

    public static String getHostSshPasswordByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.SSHPassword;
    }

    public static int getHostCousorTypeByIndex(int index) {
        //
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.mNCursorType;
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

    public static String getHostGoodfeedbackCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.goodFeedBackESC;
    }

    public static String getHostErrorfeedbackCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.errorFeedBackESC;
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

	    /*
	     GetHostEnableReaderCmdByIndex
	     SetHostEnableReaderCmdByIndex
	     
	     GetHostDisableReaderCmdByIndex
	     SetHostDisableReaderCmdByIndex
	     GetHostDisableReaderCmdByIndex
	     SetHostDisableReaderCmdByIndex
	     
	     */

    public static String getHostEnableReaderCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.scannerEnableESC;
    }

    public static String getHostDisableReaderCmdByIndex(int index) {
        SessionSetting Setting = mTESettings.getSessionSetting(index);
        return Setting.g_ReaderParam.scannerDisableESC;
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
