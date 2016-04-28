package SessionProcess;


import java.util.ArrayList;
import com.example.terminalemulation.R;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;
import TelnetIBM.IBMHost5250;
import TelnetVT.CVT100;
import Terminals.*;
/**
 * Created by Franco.Liu on 2014/2/26.
 */
public class TerminalProcess {

    private TelnetConnMgr mTelConn;
    private TerminalBase mTerminal;
    private boolean mBConnected = false;
    private ContentView.OnViewEventListener mViewEventHandler = new ContentView.OnViewEventListener() {
    	@Override
    	public void ActionKeyDown(int keyCode,KeyEvent event) {
    		MacroRec.AddMacroKeyboard(keyCode, event);
			mTerminal.OnKeyDownFire(keyCode,event);
			if (mListener != null)
				mListener.onDataInputEvent();
		}
		 
    	@Override
		public void ActionScreenTouch(int x,int y)
		{
			mTerminal.OnScreenBufferPos(x,y);
		}
    };
    
    private OnTerminalProcessListener mListener = null;
    
    ContentView TerminalView = null;
    
    MacroRecorder  MacroRec=new MacroRecorder();
    
    public TerminalProcess()
    {
    	
    	 
    	
    }

    public interface OnTerminalProcessListener {
    	void onConnected();
    	void onDisConnected();
    	void onDataInputEvent();
    }
    
    public void SetVewContainer(ContentView view)
    {
    	TerminalView=view;
    } 
    
    public void setListener(OnTerminalProcessListener listener)
    {
    	mListener = listener;
    }
    //IsConnect
    
    public void ResetSessionView()
    {
    	if (mTerminal == null || mTelConn == null) {
    		 if (mListener != null)
    			 mListener.onDisConnected();
    		 return;
        }
    	
    	if  (!mTelConn.IsConnect()) {
       		 if (mListener != null)
				 mListener.onDisConnected();
       		 return;
        }
    	
    	mTerminal.SetViewContainer(TerminalView);
    	TerminalView.setOnViewEventListener(mViewEventHandler);
    	mTerminal.ReflashBuffer();
    	mTerminal.ViewPostInvalidate();
    	 
    	if (mListener != null)
    		mListener.onConnected();
    }
    
    public boolean  ProcessTryAutoConnect()
    {    	 
    	 Boolean Autoflag=CipherConnectSettingInfo.getHostIsAutoconnectByIndex(CipherConnectSettingInfo.GetSessionIndex());
    	 
    	 
    	 if (Autoflag)
    	    return ProcessConnect();
    	 
    	 return false;
    }
    
    public void PlayMacro()
    {
    	ArrayList<?> Macroitem=MacroRec.GetItemsList();
    	
    	for(int i=0;i<Macroitem.size();i++)
    	{
    		Macroitem item=(Macroitem)Macroitem.get(i);
    		
    		if (item.GetInputType()==1)
    		{
    			PlayMacroBarcode(item.GetBarcodeData());
    		}
    		else
    		    mTerminal.OnKeyDownFire(item.GetKeyCode(),item.GetEvent());
    		
    	}
    	
    }
    public void StopRecMacro()
    {
    	MacroRec.SetRecord(false);
    }
    public void RecMacro()
    {
    	MacroRec.SetRecord(true);
    }
    public boolean ShowColorRecordIcon()
    {
    	return MacroRec.ShowColorRecordIcon();
    }
    public boolean ShowColorPlayIcon()
    {
    	return MacroRec.ShowColorPlayIcon();
    }
    public boolean ShowColorStopIcon()
    {
    	return MacroRec.ShowColorStopIcon();
    }
    //ShowColorRecordIcon
  	 // ShowColorPlayIcon
  	  //ShowColorStopIcon
    
    
    public void  ProcessReadBarcode(String Data)
    {
    	if (mTerminal!=null)
    	{
    		MacroRec.AddMacroBarcode(Data);
    	    mTerminal.OnBarcodeFire(Data);
    	    
    	    if (mListener != null)
    	    	mListener.onDataInputEvent();
    	}
    }
    public void  PlayMacroBarcode(String Data)
    {
    	if (mTerminal!=null)
    	{
      	    mTerminal.OnBarcodeFire(Data);
    	}
    }

    public boolean  ProcessConnect() {
    	 Context context = stdActivityRef.GetCurrActivity().getApplicationContext();
    	 String Ip = CipherConnectSettingInfo.getHostAddrByIndex(CipherConnectSettingInfo.GetSessionIndex());
    	 String Port = CipherConnectSettingInfo.getHostPortByIndex(CipherConnectSettingInfo.GetSessionIndex());
    	 int nIsTN = CipherConnectSettingInfo.getHostTypeByIndex(CipherConnectSettingInfo.GetSessionIndex());
    	 
    	 if(nIsTN == 0) {
    	     String serverTypeName = CipherConnectSettingInfo.getHostTypeNameByIndex(CipherConnectSettingInfo.GetSessionIndex());
    	     assert(serverTypeName.equals(context.getResources().getString(R.string.VT100Val))||
    	            serverTypeName.equals(context.getResources().getString(R.string.VT102Val))||
    	            serverTypeName.equals(context.getResources().getString(R.string.VT220Val))||
    	            serverTypeName.equals(context.getResources().getString(R.string.ANSIVal)));
    	     mTerminal = new CVT100();
    	 }
     	 else {
     	    String serverTypeName = CipherConnectSettingInfo.getTNHostTypeNameByIndex(CipherConnectSettingInfo.GetSessionIndex());
     	    if (serverTypeName.compareToIgnoreCase(context.getResources().getString(R.string.IBM5250Val)) == 0)
                mTerminal = new IBMHost5250();
     	 }
    	 
         TerminalView.setOnViewEventListener(mViewEventHandler);
         mTerminal.SetViewContainer(TerminalView);
         try {
        	 Boolean SSh = CipherConnectSettingInfo. getHostIsSshEnableByIndex(CipherConnectSettingInfo.GetSessionIndex());
    		 if (SSh)
    			 mTelConn = new TelnetSshConnMgr(Ip,Integer.valueOf(Port));
			 else
    			 mTelConn = new TelnetConnMgr(Ip,Integer.valueOf(Port));
         }
         catch (Exception e0) {
             return false;
         }
        
         mTerminal.setOnTerminalListener(new TerminalBase.OnTerminalListener() {
			@Override
			public void OnBufferSend(byte[] arrayOfByte, int len) {
				mTelConn.Send(arrayOfByte);
			}
		});
		mTelConn.setOnConnListener(new TelnetConnMgr.OnConnListener() {
			@Override
			public void OnConnected() {
				mBConnected = true;
				mTerminal.OnConnected();
			}
			
			@Override
			public void OnDisconnected() {
				mBConnected = false;
	             if (mListener != null)
	            	 mListener.onDisConnected();
			}
			
			@Override
			public void OnBufferReceived(byte[] arrayOfByte, int len) {
				mTerminal.OnBufferReceived(arrayOfByte,0,len);
			}
		});

		boolean IsOK = mTelConn.TelnetsStart();
        if (IsOK) {
        	 if (mListener != null)
        		 mListener.onConnected();
         } else
        	 Toast.makeText(context, "Cannot establish connection! Please check network.", Toast.LENGTH_SHORT).show();
         return IsOK;
    }
    public void  ProcessReleaseView()
    {
    	 TerminalView=null;
    	 mListener = null;
    	 if (mTerminal!=null)
             mTerminal.SetViewContainer(null);
        
    }
    public void  ProcessDisConnect()
    {
        if (mTelConn!=null)
        	mTelConn.TelnetDisconnect();
         
        if (mListener != null)
        	mListener.onDisConnected();
    }

    public boolean isConnected() {
    	return mBConnected;
    }
}
