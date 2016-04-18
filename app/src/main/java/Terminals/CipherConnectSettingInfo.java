package Terminals;

import Terminals.TESettings.SessionSetting;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.JsonArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 * Created by Franco.Liu on 2015/1/29.
 */
public class CipherConnectSettingInfo {
    //public static final String TAG = "CipherConnectSettingInfo";
    final private static String mSettingFilename = "TE_settings.json";
    private static Context mContext = null;
    public static final boolean _DEBUG = false;
    public static int LastHostNumber = 0;
    public static final String _NAME = "TerminalEmulation";
    private static SharedPreferences _sp = null;
    private static int CurrentIndex;
    
    static TESettings mTESettings = new TESettings();
    
	JsonArray GsonSetting = new JsonArray();
	
	private static TESettings deSerialize(File teJsonFile)
	{
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
	
	private static void serialize(File teJsonFile)
    {
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
	
	public static void InitSessionParms(Context context)
    {
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
        } catch (Exception e) {
            
        }
    }
	
	public static void SessionSettingSave() {
	    File teJsonFile = new File(mContext.getFilesDir(), mSettingFilename);
        if (teJsonFile.exists()) {
            teJsonFile.delete();
        }
        try {
            if(teJsonFile.createNewFile()) {
                serialize(teJsonFile);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public static void SetSessionIndex(int Index)
	{
		CurrentIndex=Index;
	}
	public static int GetSessionIndex()
	{
		return CurrentIndex;
	}
	public static int GetSessionSettingListCount()
	{
		if (mTESettings.SETTINGS != null)
			return mTESettings.SETTINGS.size();
		
		return 0;
	}
	
    public static void initSharedPreferences(Context c) {
        if (_sp==null) {
            _sp = c.getSharedPreferences(_NAME, 0);
            LastHostNumber=getLastDeviceNumber(c);
        }
    }
    public static void PlusLastDeviceNumber(Context c) {
        LastHostNumber=getLastDeviceNumber(c);
        LastHostNumber++;
        setLastDeviceNumber(c,LastHostNumber);
        LastHostNumber=getLastDeviceNumber(c);
    }
    public static int getLastDeviceNumber(Context c) {
        initSharedPreferences(c);
        return _sp.getInt("LastDeviceNumber",0);
    }
    public static void setLastDeviceNumber(Context c,int Number) {
        initSharedPreferences(c);
        Editor editor = _sp.edit();
        editor.putInt("LastDeviceName", Number);
        editor.commit();
    }

    public static int getHostTypeByIndex(int index) {
        SessionSetting Setting=mTESettings.getSessionSetting(index);
        return Setting.isTN;
         
    }
    
    public static void SetHostTypeByIndex(int index, int nIsTN) {
        SessionSetting Setting=mTESettings.getSessionSetting(index);
        Setting.isTN = nIsTN;
        SessionSettingSave();
    }
    
    public static String getTNHostTypeNameByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	return Setting.mTermNameTN;
         
    }
    
    public static void SetTNHostTypeNameByIndex(int index,String HostType) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.mTermNameTN = HostType;
    	SessionSettingSave();
    }
    
    
    public static String getHostTypeNameByIndex(int index) {
        SessionSetting Setting=mTESettings.getSessionSetting(index);
        return Setting.TermName;
         
    }
    
    public static void SetHostTypeNameByIndex(int index,String HostType) {
        SessionSetting Setting=mTESettings.getSessionSetting(index);
        Setting.TermName = HostType;
        SessionSettingSave();
    }
   
    public static int getHostTermLoginByIndex(int index) {
    	SessionSetting Setting = mTESettings.getSessionSetting(index);
    	return Setting.TermLogin;
         
    }
    public static void SetHostTermLoginByIndex(int index, int nTerm) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.TermLogin = nTerm;
    }
    
   /* public static String getHostNameByIndex(int index) {
        initSharedPreferences(c);
        String Default="Server"+ Integer.toString(index);
        String Name="CipherHost"+ Integer.toString(index);
        return _sp.getString(Name,Default);
    }  */
    
    /*public static void SetHostNameByIndex(int index,String HostName) {
        initSharedPreferences(c);
        String Name="CipherHost"+ Integer.toString(index);
        Editor editor = _sp.edit();
        editor.putString(Name, HostName);
        editor.commit();
    }*/

    public static String getHostAddrByIndex(int index) {
        SessionSetting Setting=mTESettings.getSessionSetting(index);
    	return Setting.mHostIP;
    }

    public static void SetHostAddrByIndex(int index,String HostAddr) {
    	SessionSetting Setting= mTESettings.getSessionSetting(index);
    	Setting.mHostIP = HostAddr;
    	SessionSettingSave();
    }
    //SendtoHost
    public static byte[] GetHostSendToHostByIndex(int index) {
    	 SessionSetting Setting = mTESettings.getSessionSetting(index);
     	 return Setting.SendtoHost;
    }
    
    public static void SetHostSendToHostByIndex(int index,byte[] Data) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.SendtoHost=Data;
    	SessionSettingSave();
    }
    public static String GetHostPortByIndex(int index) {
    	 SessionSetting Setting = mTESettings.getSessionSetting(index);
    	 Integer nPortLow = Setting.mTelnetPort.get(0);
    	 Integer nPortHi = Setting.mTelnetPort.get(1);
    	 Integer nPort = nPortLow + nPortHi * 256;
     	 return nPort.toString();
    }
    
    public static void SetHostPortByIndex(int index,String Port) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Integer nPort = Integer.valueOf(Port);
    	Integer nPortLow = nPort % 256;
        Integer nPortHi = nPort / 256;
        Setting.mTelnetPort.set(0, nPortLow);
        Setting.mTelnetPort.set(1, nPortHi);
    }
    
    //0:ANSI , 1:utf-8
    public static int getHostCharSetByIndex(int index) {
    	 SessionSetting Setting=mTESettings.getSessionSetting(index);
     	 return Setting.nCharSet;
    }
    
    public static Boolean getHostIsLineBufferByIndex(int index) {
    	 SessionSetting Setting=mTESettings.getSessionSetting(index);
     	 return (Setting.LineBuffer == 1);
    }
    public static void setHostIsLineBufferByIndex(int index,Boolean IsLinebuffer) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.LineBuffer = IsLinebuffer ? 1 : 0;
    }
    
    public static boolean getHostIsLocalEchoByIndex(int index) {
    	 SessionSetting Setting=mTESettings.getSessionSetting(index);
     	 return Setting.bEcho;
    }
    
    public static void SetHostIsLocalEchoByIndex(int index,boolean IsLocalEcho) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.bEcho = IsLocalEcho;
    }
    
    
    public static boolean getHostIsWriteLogkByIndex(int index) {
    	 SessionSetting Setting=mTESettings.getSessionSetting(index);
     	 return Setting.isSaveLog;
    }
    public static void SetHostIsWriteLogByIndex(int index, boolean IsWriteLog) {
    	SessionSetting Setting = mTESettings.getSessionSetting(index);
    	Setting.isSaveLog = IsWriteLog;
    }
    
    public static Boolean getHostIsShowMacroByIndex(int index) {
   	 SessionSetting Setting = mTESettings.getSessionSetting(index);
   	 return Setting.isShowMacro;
   }
   public static void SetHostIsShowMacroByIndex(int index, boolean IsShowMacro) {
   	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	Setting.isShowMacro = IsShowMacro;
   }

    
    public static boolean getHostIsCursorTrackByIndex(int index) {
    	 SessionSetting Setting=mTESettings.getSessionSetting(index);
     	 return Setting.bCursorTracking;
    }
    public static void SetHostIsCursorTrackByIndex(int index,boolean IsCursorTrack) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.bCursorTracking = IsCursorTrack;
    }
    
    //general_language
    public static String getHostLanguageByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	 return Setting.Language;
    }
    public static void SetHostLanguageByIndex(int index,String Language) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.Language=Language;
    	SessionSettingSave();
    }
    
    
    public static String getHostFieldexceedByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.Fieldexceed;
    }
    
    public static void SetHostFieldexceedByIndex(int index,String Fieldexceed) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.Fieldexceed=Fieldexceed;
    	SessionSettingSave();
    }
    
    public static Boolean getHostIsAutoconnectByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.bAutoConnect;
    }
    
    public static void SetHostIsAutoconnectByIndex(int index,Boolean IsAutoconnect) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.bAutoConnect=IsAutoconnect;
    	SessionSettingSave();
    }
    
    
    public static Boolean getHostIsAutoSignByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.mBAutoSignOn;
    }
    
    public static void SetHostIsAutoSignByIndex(int index,Boolean IsAutoSign) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.mBAutoSignOn=IsAutoSign;
    	SessionSettingSave();
    }
    
    public static String getHostLoginPromtByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.NamePrompt;
    }

    public static void SetHostLoginPromtByIndex(int index,String LoginPromt) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.NamePrompt = LoginPromt;
    }
    
    public static String getHostPassWordPromtByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.PassPrompt;
    }

    public static void SetHostPassWordPromtByIndex(int index,String PassWordPromt) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.PassPrompt = PassWordPromt;
    }
    
    
    
    
    public static String getHostUserNameByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.LoginName;
    }

    public static void SetHostUserNameByIndex(int index,String Name) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.LoginName = Name;
    }
    
    public static String getHostPassWordByIndex(int index) {
    	//
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.LoginPassword;
    }

    public static void SetHostPassWordByIndex(int index,String PassWord) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.LoginPassword = PassWord;
    }
    
    
    public static boolean getHostIsSshEnableByIndex(int index) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.SSH;
    }
    
    public static void SetHostIsSshEnableByIndex(int index, boolean IsEnable) {
    	SessionSetting Setting = mTESettings.getSessionSetting(index);
    	Setting.SSH = IsEnable;
    }
    
    public static String getHostSshUserByIndex(int index) {
    	//
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	    return Setting.SSHName;
    }

    public static void SetHostSshUserByIndex(int index,String UserName) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
    	Setting.SSHName = UserName;
    }
    
    public static String getHostSshPasswordByIndex(int index) {
    	//
    	SessionSetting Setting = mTESettings.getSessionSetting(index);
   	    return Setting.SSHPassword;
    }

    public static void SetHostSshPasswordByIndex(int index,String PassWord) {
    	SessionSetting Setting=mTESettings.getSessionSetting(index);
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
   	SessionSetting Setting=mTESettings.getSessionSetting(index);
   	Setting.g_ReaderParam.goodFeedBackESC = sData;
   }
  
   public static String GetHostErrorfeedbackCmdByIndex(int index) {
	   	 	SessionSetting Setting = mTESettings.getSessionSetting(index);
	    	return Setting.g_ReaderParam.errorFeedBackESC;
	   }
	   
   public static void SetHostErrorfeedbackCmdByIndex(int index, String sData) {
	   SessionSetting Setting=mTESettings.getSessionSetting(index);
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
	    	SessionSetting Setting=mTESettings.getSessionSetting(index);
	   	    return Setting.g_ReaderParam.isEnableScannerByESCCmd;
	    }
	    public static void SetHostIsReaderControlByIndex(int index,Boolean IsEnable) {
	    	SessionSetting Setting=mTESettings.getSessionSetting(index);
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
	    	SessionSetting Setting=mTESettings.getSessionSetting(index);
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
		SessionSetting Setting=mTESettings.getSessionSetting(index);
		Setting.g_ReaderParam.scannerDisableESC = sData;
	}    
    
    


}
