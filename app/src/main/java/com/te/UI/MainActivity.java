package com.te.UI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cipherlab.barcode.GeneralString;
import com.example.terminalemulation.R;
import com.te.UI.LeftMenuFrg.LeftMenuListener;

import java.util.ArrayList;
import java.util.List;

import SessionProcess.TerminalProcess;
import Terminals.CipherConnectSettingInfo;
import Terminals.CipherReaderControl;
import Terminals.ContentView;
import Terminals.CursorView;
import Terminals.TerminalBase;
import Terminals.stdActivityRef;

public class MainActivity extends AppCompatActivity
        implements LeftMenuListener, TEKeyboardViewUtility.TEKeyboardViewLsitener

{
    //MacroRecorder  MacroRec=new MacroRecorder();
    // ReaderManager is using to communicate with Barcode Reader Service
    //private com.cipherlab.barcode.ReaderManager mReaderManager;
    List<TerminalProcess> mCollSessions = new ArrayList<TerminalProcess>();
    private final BroadcastReceiver myDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Software trigger must receive this intent message
            String action = intent.getAction();
            String data;
            if (action.compareTo(GeneralString.Intent_SOFTTRIGGER_DATA) == 0) {

                // extra string from intent
                data = intent.getStringExtra(GeneralString.BcReaderData);

                // show decoded data
                //e1.setText(data);
            } else if (action.compareTo(GeneralString.Intent_PASS_TO_APP) == 0) {
                // If user disable KeyboardEmulation, barcode reader service will broadcast Intent_PASS_TO_APP
                TerminalProcess termProc = (TerminalProcess) mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
                // extra string from intent
                data = intent.getStringExtra(GeneralString.BcReaderData);

                //MacroRec.AddMacroBarcode(data);
                termProc.ProcessReadBarcode(data);
                //mReaderManager.SetActive(false);
                // show decoded data
                //e1.setText(data);

            } else if (action.equals(GeneralString.Intent_READERSERVICE_CONNECTED)) {
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

    private Toolbar mToolbar;
    private LeftMenuFrg mFragmentLeftdrawer;
    private FloatingActionButton mFAB = null;
    private TEKeyboardViewUtility mKeyboardViewUtility = null;
    private View mDecorView = null;
    private boolean mBFullScreen = false;
    private ContentView mContentView;
    private RelativeLayout mLogoView = null;
    private RelativeLayout mMainRelLayout;
    private CursorView Cursor;
    private ImageView mSessionJumpBtn = null;
    private MenuItem mMenuItemConn;
    private TerminalProcess.OnTerminalProcessListener mOnTerminalProcessListener = new TerminalProcess.OnTerminalProcessListener() {
        @Override
        public void onConnected() {
            showConnectionView(true);
            UpdateRecordButtonVisible(CipherConnectSettingInfo.getHostIsShowMacroByIndex(CipherConnectSettingInfo.GetSessionIndex()));
            UpdateRecordButton();
            updateConnMenuItem();
            updateFABStatus(FABStatus.Keyboard);
            UIUtility.showProgressDlg(false, 0);
            mKeyboardViewUtility.showTEKeyboard();
        }

        @Override
        public void onDisConnected() {
            showConnectionView(false);
            UpdateRecordButtonVisible(false);
            updateConnMenuItem();
            updateFABStatus(FABStatus.Connect);
            UIUtility.showProgressDlg(false, 0);
            mKeyboardViewUtility.hideTEKeyboard();
            Toast.makeText(MainActivity.this, getString(R.string.MSG_Disonnected), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNotify(String action, Object... params) {
            if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_DRAWCHARLIVE) == 0) {
                mContentView.DrawCharLive((Character) params[0], (Integer) params[1], (Integer) params[2]);
            } else if (action.compareToIgnoreCase(TerminalBase.NOTF_ACT_INVALIDATE) == 0) {
                mContentView.postInvalidate();
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_UPDATE_GRID) == 0) {
                mContentView.updateViewGrid();
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_CLEAR_VIEW) == 0) {
                mContentView.ClearView();
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_DRAW_SPACE) == 0) {
                mContentView.DrawSpace((Integer) params[0], (Integer) params[1], (Integer) params[2]);
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_DRAW_FIELD_CHAR) == 0) {
                mContentView.DrawFieldChar((Character) params[0], (Integer) params[1], (Integer) params[2], (Boolean) params[3], (Boolean) params[4]);
            }
        }

        @Override
        public void onDataInputEvent() {
            UpdateRecordButton();
        }
    };

    private void updateFABStatus(FABStatus fabStatus) {
        if (fabStatus == FABStatus.Connect) {
            mFAB.setImageResource(R.drawable.ic_link_variant_white_48dp);
            mFAB.setVisibility(View.VISIBLE);
            mFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionConnect();
                }
            });
        } else if (fabStatus == FABStatus.Keyboard) {
            mFAB.setImageResource(R.drawable.keyboard);
            mFAB.setVisibility(View.VISIBLE);
            mFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (false == mKeyboardViewUtility.isTEKeyboardVisible())
                        mKeyboardViewUtility.showTEKeyboard();
                    updateFABStatus(FABStatus.Gone);
                }
            });
        } else if (fabStatus == FABStatus.Gone) {
            mFAB.setVisibility(View.GONE);
        }
    }

    private void UpdateRecordButton() {
        TerminalProcess termProc = (TerminalProcess) mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
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

    private void UpdateRecordButtonVisible(boolean Show) {
        ImageButton ButStop = (ImageButton) findViewById(R.id.StopButton);
        ImageButton ButPlay = (ImageButton) findViewById(R.id.PlayButton);
        ImageButton ButRec = (ImageButton) findViewById(R.id.RecButton);

        if (!Show) {
            ButStop.setVisibility(View.INVISIBLE);
            ButPlay.setVisibility(View.INVISIBLE);
            ButRec.setVisibility(View.INVISIBLE);
        } else {
            ButStop.setVisibility(View.VISIBLE);
            ButPlay.setVisibility(View.VISIBLE);
            ButRec.setVisibility(View.VISIBLE);
        }

    }

    private void initInOnCreate() {
        for (int idxSession = 0; idxSession < CipherConnectSettingInfo.GetSessionCount(); ++idxSession) {
            TerminalProcess Process = new TerminalProcess();
            mCollSessions.add(Process);
        }
        mMainRelLayout = (RelativeLayout) findViewById(R.id.mainRelLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mFragmentLeftdrawer = (LeftMenuFrg)
                getSupportFragmentManager().findFragmentById(R.id.fragment_left_drawer);
        mFragmentLeftdrawer.setUp(mToolbar);
        mFragmentLeftdrawer.setDrawerListener(this);

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        updateFABStatus(FABStatus.Connect);

        mDecorView = getWindow().getDecorView();

        Cursor = new CursorView(this);
        mContentView = new ContentView(this, Cursor);
        final GestureDetector dbClickDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    procFullScreen();
                    return true;
                }
                return false;
            }
        });
        mContentView.setClickable(true);
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return dbClickDetector.onTouchEvent(event);
            }
        });
        mLogoView = (RelativeLayout) findViewById(R.id.logo_view);
        mLogoView.setClickable(true);
        mLogoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return dbClickDetector.onTouchEvent(event);
            }
        });

        //Bind TerminalProcess and ContentView
        TerminalProcess actSession = mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        actSession.setListener(mOnTerminalProcessListener);
        mContentView.setTerminalProc(actSession);

        mKeyboardViewUtility = new TEKeyboardViewUtility(this, (KeyboardView) findViewById(R.id.software_keyboard_view), mContentView);
        mKeyboardViewUtility.setListener(this);

        mMainRelLayout.addView(mContentView);
        mMainRelLayout.addView(Cursor);
        mMainRelLayout.setVisibility(View.INVISIBLE);

        mSessionJumpBtn = (ImageView) findViewById(R.id.session_jump_id);
        setSessionJumpImage(CipherConnectSettingInfo.GetSessionIndex());
        SessionJumpListener sjListener = new SessionJumpListener();
        mSessionJumpBtn.setOnTouchListener(sjListener);

        UIUtility.init(this);

        Boolean bAutoConn = CipherConnectSettingInfo.getHostIsAutoconnectByIndex(CipherConnectSettingInfo.GetSessionIndex());
        if (bAutoConn)
            SessionConnect();

	   /* mReaderManager = ReaderManager.InitInstance(this);
        filter = new IntentFilter();
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_PASS_TO_APP);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_READERSERVICE_CONNECTED);*/

        //mReaderManager.SetActive(false);

        this.registerForContextMenu(mMainRelLayout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stdActivityRef.SetCurrActivity(this);
        CipherReaderControl.InitReader(this, myDataReceiver);
        // Initialize User Parm
        if (true == CipherConnectSettingInfo.initSessionParms(getApplicationContext())) {
            initInOnCreate();
        } else {
            //ask user to try to clear setting, or exit app
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            CipherConnectSettingInfo.deleteCurSessionParms();
                            if (!CipherConnectSettingInfo.initSessionParms(getApplicationContext())) {
                                //Todo:popup warninig window
                                finish();
                            } else {
                                initInOnCreate();
                            }
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            finish();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.MSG_clear_setting).setPositiveButton(R.string.str_positive, dialogClickListener)
                    .setNegativeButton(R.string.str_negative, dialogClickListener);
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CipherConnectSettingInfo.SessionSettingSave();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SessionSettings.REQ_EDIT:
                mFragmentLeftdrawer.updateCurSessionTitle();
                mContentView.refresh();
                setSessionJumpImage(CipherConnectSettingInfo.GetSessionIndex());
                break;
            case SessionSettings.REQ_ADD: {
                if (resultCode == RESULT_OK && SessionSettings.gEditSessionSetting != null) {
                    CipherConnectSettingInfo.addSession(SessionSettings.gEditSessionSetting);
                    int nAddedSessionIdx = CipherConnectSettingInfo.GetSessionCount() - 1;
                    mCollSessions.add(new TerminalProcess());
                    mFragmentLeftdrawer.syncSessionsViewFromSettings();
                    mFragmentLeftdrawer.clickSession(nAddedSessionIdx);
                    SessionSettings.gEditSessionSetting = null;
                    setSessionJumpImage(CipherConnectSettingInfo.GetSessionIndex());
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onDrawerItemSelected(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                SessionChange(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDrawerItemDelete(int position) {
        mCollSessions.remove(position);
    }

    @Override
    public void onDrawerItemSetting(int position) {
        SessionSetting(position);
    }

    @Override
    public void onAddSession() {
        if (CipherConnectSettingInfo.GetSessionCount() < CipherConnectSettingInfo.MAX_SESSION_COUNT) {
            Intent intent = new Intent(this, SessionSettings.class);
            intent.putExtra(SessionSettings.ACT_SETTING, SessionSettings.ACT_SETTING_ADD);
            startActivityForResult(intent, SessionSettings.REQ_ADD);
        }
    }

    public void onClick(View v) {

        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInputFromInputMethod(this.getCurrentFocus().getWindowToken(), 0);
        if (mContentView != null) {
            if (mContentView.getVisibility() == View.VISIBLE)
                mContentView.showSoftKeyboard(mContentView);
        }

    }

    public void onClickStop(View v) {

        TerminalProcess termProc = (TerminalProcess) mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        termProc.StopRecMacro();
        UpdateRecordButton();

    }

    public void onClickPlay(View v) {
        TerminalProcess termProc = (TerminalProcess) mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        termProc.PlayMacro();
        UpdateRecordButton();


    }

    public void onClickRec(View v) {

        TerminalProcess termProc = (TerminalProcess) mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        termProc.RecMacro();
        UpdateRecordButton();

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
                if (false == isCurSessionConnected())
                    SessionConnect();
                else
                    SessionDisConnect();
                break;
            case R.id.sessionSettings:
                SessionSetting(CipherConnectSettingInfo.GetSessionIndex());
                break;
            case R.id.full_screen:
                procFullScreen();
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

    //Listener TEKeyboardView Begin
    @Override
    public void onShowKeyboard() {
        if (isCurSessionConnected())
            updateFABStatus(FABStatus.Gone);
    }
    //Listener TEKeyboardView End

    @Override
    public void onHideKeyboard() {
        if (isCurSessionConnected())
            updateFABStatus(FABStatus.Keyboard);
        else
            updateFABStatus(FABStatus.Connect);
    }

    private boolean isCurSessionConnected() {
        TerminalProcess termProc = (TerminalProcess) mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        return (termProc != null && termProc.isConnected());
    }

    private void updateConnMenuItem() {
        if (mMenuItemConn != null) {
            if (isCurSessionConnected()) {
                mMenuItemConn.setIcon(R.drawable.ic_link_variant_off_white_48dp);
            } else
                mMenuItemConn.setIcon(R.drawable.ic_link_variant_white_48dp);
        }
    }

    private void SessionChange(int idxSession) {
        if (CipherConnectSettingInfo.GetSessionIndex() == idxSession)
            return;
        TerminalProcess curSeesion = mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        TerminalProcess nextSession = mCollSessions.get(idxSession);

        //un-bind between TerminalProcess and MainActivity (Actually is ContentView)
        curSeesion.setListener(null);
        //bind between TerminalProcess and ContentView
        nextSession.setListener(mOnTerminalProcessListener);
        mContentView.setTerminalProc(nextSession);
        CipherConnectSettingInfo.SetSessionIndex(idxSession);
        mKeyboardViewUtility.hideTEKeyboard();
        showConnectionView(isCurSessionConnected());
        mContentView.refresh();
        setSessionJumpImage(idxSession);
        if (isCurSessionConnected()) {
            updateFABStatus(FABStatus.Keyboard);
        } else {
            updateFABStatus(FABStatus.Connect);
        }

        String strMsg = String.format(getResources().getString(R.string.MSG_ChangeSession), mFragmentLeftdrawer.getItemTitle(idxSession));
        Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
    }

    private void showConnectionView(boolean bShow) {
        if (bShow) {
            mMainRelLayout.setVisibility(View.VISIBLE);
            mLogoView.setVisibility(View.INVISIBLE);
        } else {
            mMainRelLayout.setVisibility(View.INVISIBLE);
            mLogoView.setVisibility(View.VISIBLE);
        }
    }

    private void ShowReaderConfig() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.iiordanov.freebVNC");
        startActivity(intent);
    }

    private void procFullScreen() {
        if(mBFullScreen == false) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            mDecorView.setFitsSystemWindows(false);
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  |  View.SYSTEM_UI_FLAG_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_IMMERSIVE);
            mBFullScreen = true;
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().show();
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            mBFullScreen = false;
        }
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View CurrentFocus = this.getCurrentFocus();
        //imm.restartInput(CurrentFocus);
        if (CurrentFocus != null)
            imm.hideSoftInputFromWindow(CurrentFocus.getWindowToken(), 0);
    }

    private void SessionConnect() {
        UIUtility.showProgressDlg(true, R.string.MSG_Connecting);
        TerminalProcess termProc = mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        termProc.ProcessConnect();
    }

    private void SessionDisConnect() {
        TerminalProcess termProc = mCollSessions.get(CipherConnectSettingInfo.GetSessionIndex());
        termProc.ProcessDisConnect();
        mMainRelLayout.setVisibility(View.INVISIBLE);
        mLogoView.setVisibility(View.VISIBLE);
    }

    private void SessionSetting(int nSessionIdx) {
        Intent intent = new Intent(this, SessionSettings.class);
        intent.putExtra(SessionSettings.ACT_SETTING, SessionSettings.ACT_SETTING_EDIT);
        intent.putExtra(SessionSettings.ACT_SETTING_EDIT_GET_SESSION_IDX, nSessionIdx);
        startActivityForResult(intent, SessionSettings.REQ_EDIT);
    }

    public void setSessionJumpImage(int sessionIndex) {
        if(CipherConnectSettingInfo.getHostIsShowSessionNumber(sessionIndex) == false) {
            mSessionJumpBtn.setVisibility(View.INVISIBLE);
        } else {
            mSessionJumpBtn.setVisibility(View.VISIBLE);
            switch (sessionIndex) {
                case 0:
                    mSessionJumpBtn.setImageResource(R.drawable.s1_64);
                    break;
                case 1:
                    mSessionJumpBtn.setImageResource(R.drawable.s2_64);
                    break;
                case 2:
                    mSessionJumpBtn.setImageResource(R.drawable.s3_64);
                    break;
                case 3:
                    mSessionJumpBtn.setImageResource(R.drawable.s4_64);
                    break;
                case 4:
                    mSessionJumpBtn.setImageResource(R.drawable.s5_64);
                    break;
            }
        }
    }

    enum FABStatus {
        Connect,
        Keyboard,
        Gone
    }

    private class SessionJumpListener implements View.OnTouchListener {
        private float mPrevX = 0;
        private float mPrevY = 0;
        private View mView;
        final GestureDetector mDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                mPrevX = event.getX();
                mPrevY = event.getY();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event) {
                int nSession = CipherConnectSettingInfo.GetSessionIndex(), nOriSession = CipherConnectSettingInfo.GetSessionIndex();
                do {
                    ++nSession;
                    if(nSession > CipherConnectSettingInfo.GetSessionCount()-1)
                        nSession = 0;
                    if(CipherConnectSettingInfo.getHostIsShowSessionNumber(nSession) == true)
                        break;
                } while (true);
                if(nSession != nOriSession) {
                    mFragmentLeftdrawer.clickSession(nSession);
                    updateConnMenuItem();
                }

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float fDisX = 0, fDisY = 0;
                if(e2 != null) {
                    fDisX = mPrevX - e2.getX();
                    fDisY = mPrevY - e2.getY();
                }
                RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mView.getLayoutParams();
                parms.leftMargin = (int) (parms.leftMargin - fDisX);
                parms.topMargin = (int) (parms.topMargin - fDisY);
                mView.setLayoutParams(parms);
                return true;
            }
        });

        public SessionJumpListener() {

        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mView = view;
            return mDetector.onTouchEvent(event);
        }
    }
}
