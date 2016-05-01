package SessionProcess;


import android.content.Context;
import android.view.KeyEvent;

import com.example.terminalemulation.R;

import java.util.ArrayList;

import TelnetIBM.IBMHost5250;
import TelnetVT.CVT100;
import Terminals.CipherConnectSettingInfo;
import Terminals.ContentView;
import Terminals.MacroRecorder;
import Terminals.Macroitem;
import Terminals.TerminalBase;
import Terminals.stdActivityRef;
/**
 * Created by Franco.Liu on 2014/2/26.
 */
public class TerminalProcess {
    private TerminalBase mTerminal;
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
    
    ContentView mTerminalView = null;
    
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
    	mTerminalView =view;
    } 
    
    public void setListener(OnTerminalProcessListener listener)
    {
    	mListener = listener;
    }
    //IsConnect
    
    public void ResetSessionView() {
    	if (mTerminal == null) {
    		 return;
        }
    	
    	mTerminal.SetViewContainer(mTerminalView);
    	mTerminalView.setOnViewEventListener(mViewEventHandler);
    	mTerminal.ReflashBuffer();
    	mTerminal.ViewPostInvalidate();
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
		Boolean SSh = CipherConnectSettingInfo. getHostIsSshEnableByIndex(CipherConnectSettingInfo.GetSessionIndex());
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

		mTerminal.setIP(Ip);
		mTerminal.setPort(Port);
		mTerminal.setSsh(SSh);
        mTerminalView.setOnViewEventListener(mViewEventHandler);
        mTerminal.SetViewContainer(mTerminalView);
		mTerminal.setOnTerminalListener(new TerminalBase.OnTerminalListener() {
			@Override
			public void OnConnected() {
				if (mListener != null)
					mListener.onConnected();
			}

			@Override
			public void OnDisconnected() {
				if (mListener != null)
					mListener.onDisConnected();
			}
		});

		return mTerminal.TelnetsStart();
    }
    public void  ProcessReleaseView()
    {
    	 mTerminalView =null;
    	 mListener = null;
    	 if (mTerminal!=null)
             mTerminal.SetViewContainer(null);
        
    }
    public void  ProcessDisConnect() {
        if (mTerminal!=null)
			mTerminal.TelnetDisconnect();
    }

    public boolean isConnected() {
    	if(mTerminal != null)
			return mTerminal.isConnected();
		return  false;
    }
}
