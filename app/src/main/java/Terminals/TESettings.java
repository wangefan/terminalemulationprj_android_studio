package Terminals;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class TESettings {
    
    public TECommonSetting Common = new TECommonSetting();
    public ArrayList<SessionSetting> SETTINGS = new ArrayList<SessionSetting>();
    public SessionSetting getSessionSetting(int idxSession) {
        return SETTINGS.get(idxSession);
    }
    private class TECommonSetting {
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
    
    public class SessionSetting {
    	//Sync
        @SerializedName("TermNameTN") 
        String mTermNameTN="";
    	
        String TermName="";
    	
        int isTN = 1;
    	
        boolean bAutoConnect;
    	
        @SerializedName("HostIPorName") 
    	String mHostIP = "";
    	
    	@SerializedName("TelnetPort") 
    	ArrayList<Integer> mTelnetPort = new ArrayList<Integer>();// low[0] + high[1] * 256
    	
    	@SerializedName("bAutoSignOn")
    	boolean mBAutoSignOn = false;
    	
    	String LoginName = "";
        String LoginPassword = "";
    	int nCharSet = 0;	//0:Ansi, 1:UTF8
    	int CursorType = 0; //0:Default, 1:Underline, 2:Block
    	int LineBuffer = 0;
    	boolean isSaveLog = false;
    	boolean bCursorTracking = false;
    	boolean isShowMacro = false;
    	boolean SSH = false;
    	String SSHName = "";
    	String SSHPassword="";
    	String NamePrompt="";
    	String PassPrompt="";
    	int TermLogin = 0;	//0:Tab, 1:Enter
    	ReaderParam g_ReaderParam = new ReaderParam();
    	
        //Sync but Not used 
    	boolean bEcho = false;
        
        //Not sync
    	String Language="Single Byte Character";
    	String Fieldexceed="Reject";
       	byte[] SendtoHost=null; 

        
        public class ReaderParam {
        	boolean isEnableScannerByESCCmd = false;
            String scannerEnableESC = null;
            String scannerDisableESC = null;
            String goodFeedBackESC = null;  
            String errorFeedBackESC = null;
        }
    }
}
