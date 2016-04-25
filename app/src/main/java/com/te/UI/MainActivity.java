package com.te.UI;

import java.util.ArrayList;
import java.util.List;
import SessionProcess.TerminalProcess;
import Terminals.CursorView;
import Terminals.stdActivityRef;
import Terminals.ContentView;
import Terminals.CipherReaderControl;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cipherlab.barcode.*;
import com.example.terminalemulation.R;
import com.te.UI.LeftMenuFrg.LeftMenuListener;
import android.content.BroadcastReceiver;
import Terminals.CipherConnectSettingInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity implements LeftMenuListener {

	    private Toolbar mToolbar;
	    private LeftMenuFrg mFragmentLeftdrawer;
	    ContentView mContentView;
	    RelativeLayout mMainRelLayout;
	    CursorView  Cursor;
	    ImageView imageView;
	    MenuItem mMenuItemConn;
		//MacroRecorder  MacroRec=new MacroRecorder();
		
		
		// ReaderManager is using to communicate with Barcode Reader Service
		//private com.cipherlab.barcode.ReaderManager mReaderManager;
	    List<TerminalProcess> mCollSessions =new ArrayList<TerminalProcess>();
	    
	  
	    private TerminalProcess.OnTerminalProcessListener mOnTerminalProcessListener = new TerminalProcess.OnTerminalProcessListener() 
	    {   
	    	@Override
	    	public void onConnected()
	    	{
	    		RelativeLayout ConnectBut = (RelativeLayout) findViewById(R.id.ConnbuttonView);
	    		//RelativeLayout TextVer = (RelativeLayout) findViewById(R.id.TextVersionView);   
	    		 
	    		mMainRelLayout.setVisibility(View.VISIBLE);
	           	ConnectBut.setVisibility(View.INVISIBLE);
	           	//TextVer.setVisibility(View.INVISIBLE);
	           	
	             UpdateRecordButtonVisible(CipherConnectSettingInfo.getHostIsShowMacroByIndex(CipherConnectSettingInfo.GetSessionIndex()));
	             UpdateRecordButton();
	             updateConnMenuItem();
	    	}
	    	
	    	@Override
	    	public void onDisConnected()
	    	{
	    		RelativeLayout ConnectBut = (RelativeLayout) findViewById(R.id.ConnbuttonView);
	    		//RelativeLayout TextVer = (RelativeLayout) findViewById(R.id.TextVersionView);   
	    	
	    		mMainRelLayout.setVisibility(View.INVISIBLE);
    	        ConnectBut.setVisibility(View.VISIBLE);
    	        //TextVer.setVisibility(View.VISIBLE);
	    	    UpdateRecordButtonVisible(false);
	    	    updateConnMenuItem();
	    	}
	    	
	    	@Override
	    	public void onDataInputEvent()
	    	{
	    		  UpdateRecordButton();
	    	}
	    };

	    
    private void InitializeUserParm()
    {
		CipherConnectSettingInfo.InitSessionParms(getApplicationContext());
		for(int idxSession = 0; idxSession < CipherConnectSettingInfo.GetSessionCount(); ++idxSession) {
			TerminalProcess Process = new TerminalProcess();
			mCollSessions.add(Process);
		}
	}
    private void UpdateRecordButton()
    {
    	 TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
    	 ImageButton ButStop = (ImageButton) findViewById(R.id.StopButton);
    	 ImageButton ButPlay = (ImageButton) findViewById(R.id.PlayButton);
    	 ImageButton ButRec = (ImageButton) findViewById(R.id.RecButton);
     	 
    	
    	 if (termProc.ShowColorRecordIcon())
    		 ButRec.setBackgroundResource(R.drawable.record);
    	 else
    	     ButRec.setBackgroundResource(R.drawable.recordgray);
    	 
    	 if (termProc.ShowColorPlayIcon())
    		 ButPlay.setBackgroundResource(R.drawable.playicon);
    	 else
    		 ButPlay.setBackgroundResource(R.drawable.playgray);
    	 
    	 if (termProc.ShowColorStopIcon())
    		 ButStop.setBackgroundResource(R.drawable.stopicon);
    	 else
    		 ButStop.setBackgroundResource(R.drawable.stopgray);
    	 
    	 
    	
	}
    private void UpdateRecordButtonVisible(boolean Show)
    {
    	 ImageButton ButStop = (ImageButton) findViewById(R.id.StopButton);
    	 ImageButton ButPlay = (ImageButton) findViewById(R.id.PlayButton);
    	 ImageButton ButRec = (ImageButton) findViewById(R.id.RecButton);
    	 
    	 if (!Show)
    	 {
    	     ButStop.setVisibility(View.INVISIBLE);
    	     ButPlay.setVisibility(View.INVISIBLE);
    	     ButRec.setVisibility(View.INVISIBLE);
    	 }
    	 else
    	 {
    		 ButStop.setVisibility(View.VISIBLE);
    	     ButPlay.setVisibility(View.VISIBLE);
    	     ButRec.setVisibility(View.VISIBLE);
    	 }
    	
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        stdActivityRef.SetCurrActivity(this);
        InitializeUserParm();
			
        mMainRelLayout = (RelativeLayout) findViewById(R.id.mainRelLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mFragmentLeftdrawer = (LeftMenuFrg)
                getSupportFragmentManager().findFragmentById(R.id.fragment_left_drawer);
        mFragmentLeftdrawer.setUp(mToolbar);
        mFragmentLeftdrawer.setDrawerListener(this);
        
        Cursor=new CursorView(this);  
        mContentView = new ContentView(this,Cursor);
         
        
        mMainRelLayout.addView(mContentView);
        mMainRelLayout.addView(Cursor);
        mMainRelLayout.setVisibility(View.INVISIBLE);
        
      
       /* mReaderManager = ReaderManager.InitInstance(this);
    	filter = new IntentFilter(); 
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_PASS_TO_APP);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_READERSERVICE_CONNECTED);*/
		 
		//mReaderManager.SetActive(false);
        CipherReaderControl.InitReader(this, myDataReceiver);
		//registerReceiver(myDataReceiver, filter);
	    //mConnectHandler.post(this.AutoConnRun);
	    
	    
	    this.registerForContextMenu(mMainRelLayout);
	    
	   
       // if (tv!=null)
			//tv.showSoftKeyboard(tv);
        
        //HideKeyboard();
       // ShowKeyboard();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		
		// ***************************************************//
		// Unregister Broadcast Receiver before app close
		// ***************************************************//
		unregisterReceiver(myDataReceiver);
		
		// ***************************************************//
		// release(unbind) before app close
		// ***************************************************//
		CipherReaderControl.ReleaseReader();
		
	}
	
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first

	    HideKeyboard();
	    
	    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
	    
	    
	    
	}
	@Override
	public void onResume(){
	    super.onResume();
	    // put your code here...
	     ShowKeyboard();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode){
			case SessionSettings.REQ_EDIT:
				break;
			case SessionSettings.REQ_ADD:
			{
				if(resultCode == RESULT_OK && SessionSettings.gEditSessionSetting != null)
				{
					CipherConnectSettingInfo.addSession(SessionSettings.gEditSessionSetting);
					int nAddedSessionIdx = CipherConnectSettingInfo.GetSessionCount() - 1;
					mCollSessions.add(new TerminalProcess());
					mFragmentLeftdrawer.syncSessionsViewFromSettings();
					mFragmentLeftdrawer.clickSession(nAddedSessionIdx);
					SessionSettings.gEditSessionSetting = null;
				}
			}
			break;
			default:
				break;
		}
	}
	
	@Override
	public void onDrawerItemSelected(int position) {
		switch(position)
		{
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			SessionChange(position);
			break;
		default: break;
		}
	}

	@Override
	public void onDrawerItemDelete(int position) {

	}

	@Override
	public void onDrawerItemSetting(int position) {
		SessionSetting(position);
	}

	@Override
	public void onAddSession() {
		if(CipherConnectSettingInfo.GetSessionCount() < CipherConnectSettingInfo.MAX_SESSION_COUNT) {
			Intent intent = new Intent(this, SessionSettings.class);
			intent.putExtra(SessionSettings.ACT_SETTING, SessionSettings.ACT_SETTING_ADD);
			startActivityForResult(intent, SessionSettings.REQ_ADD);
		}
	}

	public void onClick(View v) {
         
	    //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
       // imm.showSoftInputFromInputMethod(this.getCurrentFocus().getWindowToken(), 0);
		if (mContentView!=null)
		{
			if (mContentView.getVisibility()==View.VISIBLE)
				mContentView.showSoftKeyboard(mContentView);
		}
		
    } 
	public void onClickStop(View v) {
        
		 TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
		 termProc.StopRecMacro();
		 UpdateRecordButton();
		
    }  
	public void onClickPlay(View v) {
		 TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
		 termProc.PlayMacro();
		 UpdateRecordButton();
		 
	   
		
    }  
	public void onClickRec(View v) {
           
		 TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
		 termProc.RecMacro();
		 UpdateRecordButton();
		
    }  
    public void onConnect(View v) {
         
    	SessionConnect(); 
		
    }  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mMenuItemConn = menu.findItem(R.id.connection);
		updateConnMenuItem();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		 switch (item.getItemId()) {
         case R.id.connection:
        	 if(false == isCurSessionConnected()) 
        		 SessionConnect();
        	 else
        		 SessionDisConnect();
             break;
         case R.id.sessionSettings:
        	 SessionSetting(CipherConnectSettingInfo.GetSessionIndex());
             break;
        }
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onAbout() {
	    View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        TextView AppVer = (TextView) messageView.findViewById(R.id.txt_app_ver);
        TextView ServiceVer = (TextView) messageView.findViewById(R.id.txt_service_ver);
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                             this.getPackageName(), 0);
            AppVer.setText(R.string.app_Version);

        } catch (NameNotFoundException e) {
            e.printStackTrace();
            AppVer.setText("0.0.0");
        }
        
         try {
            PackageInfo info = manager.getPackageInfo("com.cipherlab.clbarcodeservice", 0);
            ServiceVer.setText(info.versionName);
         } catch (NameNotFoundException e) {
             ServiceVer.setText("0.0.0");
             e.printStackTrace();
         }
         
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setIcon(R.drawable.appico);
         builder.setTitle(R.string.app_name);
         builder.setView(messageView);
         builder.setPositiveButton(R.string.STR_OK,
             new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface arg0, int arg1) {

                     }// end onClick()
             });
         builder.create();
         builder.show();
    }
	
    @Override
    public void onExit() {
        HideKeyboard();
        finish();
    }
	
	Runnable DisConnRun = new Runnable()
    {
        public void run()
        {
        	//TelnetHandleDisConnected();

            //localHandler.post(local1);
        }
    };
   
    
	Runnable AutoConnRun = new Runnable()
    {
        public void run()
        {
        	SessionAutoConnect();

            //localHandler.post(local1);
        }
    };
    
    private boolean isCurSessionConnected() {
    	TerminalProcess termProc = (TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
		return(termProc != null && termProc.isConnected());
    }
    
    private void updateConnMenuItem() {
    	if(mMenuItemConn != null) {
    		if(isCurSessionConnected()) {
    			mMenuItemConn.setIcon(R.drawable.ic_link_variant_off_white_48dp);
    		}
    		else
    			mMenuItemConn.setIcon(R.drawable.ic_link_variant_white_48dp);
    	}
    }
    
    private void SessionChange(int idxSession)
    {
		if(CipherConnectSettingInfo.GetSessionIndex() == idxSession)
			return;
        TerminalProcess curSeesion = (TerminalProcess) mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        TerminalProcess nextSession = (TerminalProcess) mCollSessions.get(idxSession);

		curSeesion.ProcessReleaseView();
        mContentView.ResetView();
		nextSession.SetVewContainer(mContentView);
		nextSession.setListener(mOnTerminalProcessListener);
		nextSession.ResetSessionView();
		CipherConnectSettingInfo.SetSessionIndex(idxSession);
		String strMsg = String.format(getResources().getString(R.string.MSG_ChangeSession), mFragmentLeftdrawer.getItemTitle(idxSession));
		Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
    }
    private void SessionAutoConnect()
    {
        TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        RelativeLayout ConnectBut = (RelativeLayout) findViewById(R.id.ConnbuttonView);
	
        if (!termProc.ProcessTryAutoConnect())
        {
        	SessionDisConnect();	
        }
        else
        {
        	 mMainRelLayout.setVisibility(View.VISIBLE);
        	 termProc.SetVewContainer(mContentView);
        	 termProc.setListener(mOnTerminalProcessListener);
        	 ConnectBut.setVisibility(View.INVISIBLE);
        	 
        }
    }
   private void ShowReaderConfig()
   {
	   Intent intent = getPackageManager().getLaunchIntentForPackage("com.iiordanov.freebVNC");
	   startActivity(intent);
   }
    
    private void HideKeyboard()
    {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	
	    View CurrentFocus=this.getCurrentFocus();
	    //imm.restartInput(CurrentFocus);
	    if (CurrentFocus!=null)
          imm.hideSoftInputFromWindow(CurrentFocus.getWindowToken(), 0);
    }
    private void ShowKeyboard()
    {
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	
	    View CurrentFocus=this.getCurrentFocus();
	    //imm.restartInput(CurrentFocus);
	    if (CurrentFocus!=null)
          imm.showSoftInputFromInputMethod(CurrentFocus.getWindowToken(), 0);
    }
    
    private void SessionConnect()
    {
	    TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
	    RelativeLayout ConnectBut = (RelativeLayout) findViewById(R.id.ConnbuttonView);
       
        
	    ShowKeyboard();
	    SessionDisConnect(); 
	    termProc.SetVewContainer(mContentView);
	    termProc.setListener(mOnTerminalProcessListener);
        //llScroll.addView(mMainRelLayout);
	        if (!termProc.ProcessConnect())
	        {
	        	SessionDisConnect();	
	        }
	        else
	        {
	        	
	        	mMainRelLayout.setVisibility(View.VISIBLE);
	        	mMainRelLayout.invalidate();
	        	ConnectBut.setVisibility(View.INVISIBLE);
 
	        }
	        
	        	

	    }
	 
    public void TelnetHandleDisConnected()
	{
		 RelativeLayout ConnectBut = (RelativeLayout) findViewById(R.id.ConnbuttonView);
		 //RelativeLayout TextVer = (RelativeLayout) findViewById(R.id.TextVersionView);   
	
        mMainRelLayout.setVisibility(View.INVISIBLE);
        ConnectBut.setVisibility(View.VISIBLE);
        //TextVer.setVisibility(View.VISIBLE);
	    UpdateRecordButtonVisible(false);
		
	}
 
    private void SessionDisConnect()
    {
	    RelativeLayout ConnectBut = (RelativeLayout) findViewById(R.id.ConnbuttonView);
	    
	    TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        termProc.ProcessDisConnect();
        mMainRelLayout.setVisibility(View.INVISIBLE);
        ConnectBut.setVisibility(View.VISIBLE);

    }

	private void SessionSetting(int nSessionIdx) {
		Intent intent = new Intent(this, SessionSettings.class);
		intent.putExtra(SessionSettings.ACT_SETTING, SessionSettings.ACT_SETTING_EDIT);
		intent.putExtra(SessionSettings.ACT_SETTING_EDIT_GET_SESSION_IDX, nSessionIdx);
        startActivityForResult(intent, SessionSettings.REQ_EDIT);
    }
	 
	 private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// Software trigger must receive this intent message
				
				String data;
				if (intent.getAction().equals(GeneralString.Intent_SOFTTRIGGER_DATA)) {
					
					// extra string from intent
					data = intent.getStringExtra(GeneralString.BcReaderData);
				 
					// show decoded data
					//e1.setText(data);
				}else if (intent.getAction().equals(GeneralString.Intent_PASS_TO_APP)){
					// If user disable KeyboardEmulation, barcode reader service will broadcast Intent_PASS_TO_APP
					TerminalProcess termProc=(TerminalProcess)mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
					// extra string from intent
					data = intent.getStringExtra(GeneralString.BcReaderData);
					
					//MacroRec.AddMacroBarcode(data);
					termProc.ProcessReadBarcode(data);
					 //mReaderManager.SetActive(false);
					// show decoded data
					//e1.setText(data);
					
				}else if(intent.getAction().equals(GeneralString.Intent_READERSERVICE_CONNECTED)){
					// Make sure this app bind to barcode reader sevice , then user can use APIs to get/set settings from barcode reader service 
					
					//BcReaderType myReaderType =  mReaderManager.GetReaderType();
					//mReaderManager.SetActive(true);
					//boolean rt=mReaderManager.GetActive();
				 
					//e1.setText(myReaderType.toString());
					
					/*NotificationParams settings = new NotificationParams();
					mReaderManager.Get_NotificationParams(settings);
					
					ReaderOutputConfiguration settings2 = new ReaderOutputConfiguration();
					mReaderManager.Get_ReaderOutputConfiguration(settings2);
					*/
				}
				 
			}
		};
}
