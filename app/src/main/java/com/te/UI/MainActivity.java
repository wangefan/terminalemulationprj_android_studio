package com.te.UI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
import com.cipherlab.terminalemulation.BuildConfig;
import com.cipherlab.terminalemulation.R;
import com.te.UI.LeftMenuFrg.LeftMenuListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import SessionProcess.TerminalProcess;
import Terminals.CipherReaderControl;
import Terminals.ContentView;
import Terminals.CursorView;
import Terminals.KeyMapList;
import Terminals.TESettingsInfo;
import Terminals.TerminalBase;
import Terminals.stdActivityRef;

public class MainActivity extends AppCompatActivity
        implements LeftMenuListener, TEKeyboardViewUtility.TEKeyboardViewListener

{
    static {
        System.loadLibrary("chilkat");
    }
    private static final String TAG_TERPROC_FRAGMENT = "TAG_TERPROC_FRAGMENT";
    private static final String KEY_FULL_SCREEEN = "KEY_FULL_SCREEEN";
    private final long UPDATE_ALERT_INTERVAL = 60000; //60 sec, 1 min
    private Toolbar mToolbar;
    private LeftMenuFrg mFragmentLeftdrawer;
    private RelativeLayout mMacroView = null;
    private FloatingActionButton mFAB = null;
    private TEKeyboardViewUtility mKeyboardViewUtility = null;
    private View mSessionStausView = null;
    private View mDecorView = null;
    private boolean mBFullScreen = false;
    private ContentView mContentView;
    private RelativeLayout mLogoView = null;
    private RelativeLayout mMainRelLayout;
    private CursorView Cursor;
    private ImageView mSessionJumpBtn = null;
    private ImageView mWiFiStatusIcon = null;
    private ImageView mBattStatusIcon = null;
    private MenuItem mMenuItemConn;
    private Handler mUpdateWifiAlertHandler = new Handler();
    private Handler mUpdateBaterAlertHandler = new Handler();
    private Handler mUpdateWiFiAndBaterIconHandler = new Handler();
    private TerminalProcessFrg mTerminalProcessFrg = null;
    // ReaderManager is using to communicate with Barcode Reader Service
    //private com.cipherlab.barcode.ReaderManager mReaderManager;
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
                TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
                // extra string from intent
                data = intent.getStringExtra(GeneralString.BcReaderData);

                //MacroRec.addMacroBarcode(data);
                termProc.processReadBarcode(data);
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
    private TerminalProcess.OnTerminalProcessListener mOnTerminalProcessListener = new TerminalProcess.OnTerminalProcessListener() {
        @Override
        public void onConnected() {
            showConnectionView(true);
            updateRecordButtonVisible();
            UpdateRecordButton();
            updateConnMenuItem();
            if (TESettingsInfo.getHostIsAutoFullScreenOnConnByIndex(TESettingsInfo.getSessionIndex()) == true) {
                procFullScreen();
            }
            UIUtility.showProgressDlg(MainActivity.this, false, 0);
            mKeyboardViewUtility.setKeyboard(TEKeyboardViewUtility.KeyboardType.KT_ABC);
            if (TESettingsInfo.getHostIsAutoPopSIPOnConnByIndex(TESettingsInfo.getSessionIndex()) == true) {
                updateFABStatus(FABStatus.Gone);
                mKeyboardViewUtility.showTEKeyboard();
            } else {
                updateFABStatus(FABStatus.Keyboard);
            }
        }

        @Override
        public void onDisConnected() {
            showConnectionView(false);
            updateRecordButtonVisible();
            updateConnMenuItem();
            updateFABStatus(FABStatus.Connect);
            UIUtility.cancelDetectNetworkOutRange();
            UIUtility.showProgressDlg(MainActivity.this, false, 0);
            mKeyboardViewUtility.hideTEKeyboard();
            Toast.makeText(MainActivity.this, getString(R.string.MSG_Disonnected), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnConnectError(String message) {
            showConnectionView(false);
            updateRecordButtonVisible();
            updateConnMenuItem();
            updateFABStatus(FABStatus.Connect);
            UIUtility.cancelDetectNetworkOutRange();
            UIUtility.showProgressDlg(MainActivity.this, false, 0);
            mKeyboardViewUtility.hideTEKeyboard();
            UIUtility.messageBox(MainActivity.this, message, null);
        }

        @Override
        public void onNotify(String action, Object... params) {
            if (action.compareToIgnoreCase(TerminalBase.NOTF_ACT_INVALIDATE) == 0) {
                mContentView.postInvalidate();
            } else if (action.compareToIgnoreCase(TerminalBase.NOTF_ACT_UPDATE_GRID) == 0) {
                mContentView.updateViewGrid();
            } else if (action.compareToIgnoreCase(TerminalBase.NOTF_ACT_CLEAR_VIEW) == 0) {
                mContentView.ClearView();
            } else if (action.compareToIgnoreCase(TerminalBase.NOTF_ACT_DRAW_SPACE) == 0) {
                mContentView.DrawSpace((Integer) params[0], (Integer) params[1], (Integer) params[2]);
            } else if (action.compareToIgnoreCase(TerminalBase.NOTF_ACT_DRAWCHAR) == 0) {
                mContentView.DrawChar((Character) params[0], (Integer) params[1], (Integer) params[2], (Boolean) params[3], (Boolean) params[4], (Boolean) params[5]);
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_CURSOR_TRACK) == 0) {
                mContentView.procCursorTrack();
            }
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
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        ImageButton ButStop = (ImageButton) findViewById(R.id.StopButton);
        ImageButton ButPlay = (ImageButton) findViewById(R.id.PlayButton);
        ImageButton ButRec = (ImageButton) findViewById(R.id.RecButton);

        if (termProc.isMacroRecording() == false) {
            ButRec.setBackgroundResource(R.drawable.record);
            if (termProc.hasMacro())
                ButPlay.setBackgroundResource(R.drawable.playicon);
            else
                ButPlay.setBackgroundResource(R.drawable.playgray);
            ButStop.setBackgroundResource(R.drawable.stopgray);
        } else {
            ButRec.setBackgroundResource(R.drawable.recordgray);
            ButPlay.setBackgroundResource(R.drawable.playgray);
            ButStop.setBackgroundResource(R.drawable.stopicon);
        }
    }

    private void updateWiFiIcon(Context context) {
        boolean bShow = TESettingsInfo.getHostShowWiFiIconOnFullScreenByIndex(TESettingsInfo.getSessionIndex()) && mBFullScreen;
        mWiFiStatusIcon.setVisibility(bShow ? View.VISIBLE : View.GONE);
        if(bShow) {
            int wifiStrength = CipherUtility.getWiFiStrength(context);
            final int LEVEL_UNIT = 20;
            int nLevel = wifiStrength / LEVEL_UNIT;
            switch (nLevel) {
                case 0:
                default:
                    mWiFiStatusIcon.setImageResource(R.drawable.wifi_0);
                    break;
                case 1:
                    mWiFiStatusIcon.setImageResource(R.drawable.wifi_1);
                    break;
                case 2:
                    mWiFiStatusIcon.setImageResource(R.drawable.wifi_2);
                    break;
                case 3:
                    mWiFiStatusIcon.setImageResource(R.drawable.wifi_3);
                    break;
                case 4:
                    mWiFiStatusIcon.setImageResource(R.drawable.wifi_4);
                    break;
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mWiFiStatusIcon.getLayoutParams();
            params.leftMargin = TESettingsInfo.getWiFiIconLocLeft();
            params.topMargin = TESettingsInfo.getWiFiIconLocTop();
            mWiFiStatusIcon.setLayoutParams(params);
        }
    }

    private void updateBattIcon(Context context) {
        boolean bShow = TESettingsInfo.getHostShowBattIconOnFullScreenByIndex(TESettingsInfo.getSessionIndex()) && mBFullScreen;
        mBattStatusIcon.setVisibility(bShow ? View.VISIBLE : View.GONE);
        if(bShow) {
            int nBatteryStrength = CipherUtility.getBatteryPct(context);
            final int LEVEL_UNIT = 20;
            int nLevel = nBatteryStrength / LEVEL_UNIT;
            switch (nLevel) {
                case 0:
                default:
                    mBattStatusIcon.setImageResource(R.drawable.batt_0);
                    break;
                case 1:
                    mBattStatusIcon.setImageResource(R.drawable.batt_1);
                    break;
                case 2:
                    mBattStatusIcon.setImageResource(R.drawable.batt_2);
                    break;
                case 3:
                    mBattStatusIcon.setImageResource(R.drawable.batt_3);
                    break;
                case 4:
                    mBattStatusIcon.setImageResource(R.drawable.batt_4);
                    break;
            }
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBattStatusIcon.getLayoutParams();
            params.leftMargin = TESettingsInfo.getBattIconLocLeft();
            params.topMargin = TESettingsInfo.getBattIconLocTop();
            mBattStatusIcon.setLayoutParams(params);
        }
    }

    private void procUpdateWiFiAndBattIconTimer() {
        mUpdateWiFiAndBaterIconHandler.removeCallbacksAndMessages(null);
        updateWiFiIcon(this);
        updateBattIcon(this);
        boolean bShowWiFi = TESettingsInfo.getHostShowWiFiIconOnFullScreenByIndex(TESettingsInfo.getSessionIndex());
        boolean bShowBatt = TESettingsInfo.getHostShowBattIconOnFullScreenByIndex(TESettingsInfo.getSessionIndex());
        boolean bUpdateWiFi_Batt_Icons = TESettingsInfo.getIsUpdateWiFiAndtBatteryByIndex(TESettingsInfo.getSessionIndex());
        if (bUpdateWiFi_Batt_Icons && mBFullScreen && (bShowWiFi || bShowBatt)) {
            final int nUpdateInterval = TESettingsInfo.getUpdateWiFiAndtBatteryIntervalByIndex(TESettingsInfo.getSessionIndex()) * 60 * 1000;
            mUpdateWiFiAndBaterIconHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateWiFiIcon(MainActivity.this);
                    updateBattIcon(MainActivity.this);
                    mUpdateWiFiAndBaterIconHandler.postDelayed(this, nUpdateInterval);
                }
            }, nUpdateInterval);
        }
    }

    private void procAlertTimer() {
        boolean bWifiAlert = TESettingsInfo.getHostIsShowWifiAlertByIndex(TESettingsInfo.getSessionIndex());
        boolean bBatAlert = TESettingsInfo.getHostIsShowBatteryAlertByIndex(TESettingsInfo.getSessionIndex());
        mUpdateWifiAlertHandler.removeCallbacksAndMessages(null);
        if (bWifiAlert) {
            final int nWifiAlert = TESettingsInfo.getHostShowWifiAltLevelByIndex(TESettingsInfo.getSessionIndex());
            mUpdateWifiAlertHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int wifiStrength = CipherUtility.getWiFiStrength(MainActivity.this);
                    if (wifiStrength < nWifiAlert && mBFullScreen) {
                        final Runnable tempRun = this;
                        UIUtility.messageBox(MainActivity.this, String.format(getResources().getString(R.string.MSG_WifiAlert), wifiStrength), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUpdateWifiAlertHandler.postDelayed(tempRun, UPDATE_ALERT_INTERVAL);
                            }
                        });
                    } else {
                        mUpdateWifiAlertHandler.postDelayed(this, UPDATE_ALERT_INTERVAL);
                    }
                }
            }, 2000);
        }

        mUpdateBaterAlertHandler.removeCallbacksAndMessages(null);
        if (bBatAlert) {
            final int nBatAlert = TESettingsInfo.getHostShowBatteryAltLevelByIndex(TESettingsInfo.getSessionIndex());
            mUpdateBaterAlertHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int batStrength = CipherUtility.getBatteryPct(MainActivity.this);
                    if (batStrength < nBatAlert && mBFullScreen) {
                        final Runnable tempRun = this;
                        UIUtility.messageBox(MainActivity.this, String.format(getResources().getString(R.string.MSG_BattAlert), batStrength), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUpdateBaterAlertHandler.postDelayed(tempRun, UPDATE_ALERT_INTERVAL);
                            }
                        });
                    } else {
                        mUpdateBaterAlertHandler.postDelayed(this, UPDATE_ALERT_INTERVAL);
                    }
                }
            }, 2000);
        }
    }

    private void updateRecordButtonVisible() {
        boolean bShow = TESettingsInfo.getHostIsShowMacroByIndex(TESettingsInfo.getSessionIndex());
        if (!bShow || isCurSessionConnected() == false) {
            mMacroView.setVisibility(View.GONE);
        } else {
            mMacroView.setVisibility(View.VISIBLE);
        }
    }

    private void syncSessionsFromSettings() {
        TerminalProcess actSession = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        actSession.setListener(mOnTerminalProcessListener);
        mContentView.setTerminalProc(actSession);
        mContentView.refresh();
        setSessionStatusView();
        updateRecordButtonVisible();
        setSessionJumpImage(TESettingsInfo.getSessionIndex());
        procAlertTimer();
        procUpdateWiFiAndBattIconTimer();
        mFragmentLeftdrawer.syncSessionsViewFromSettings();
        mFragmentLeftdrawer.clickSession(TESettingsInfo.getSessionIndex());
        setTitle(TESettingsInfo.getProgramName());
    }

    private void setSessionStatusView() {
        if (TESettingsInfo.getHostIsShowSessionStatus(TESettingsInfo.getSessionIndex()) == true && mBFullScreen == false) {
            String serverTypeName = TESettingsInfo.getHostTypeNameByIndex(TESettingsInfo.getSessionIndex());
            TextView tv = (TextView) mSessionStausView.findViewById(R.id.id_session_statuse_title);
            tv.setText(String.format(getResources().getString(R.string.Format_SessionStatus),
                    serverTypeName,
                    String.valueOf(TESettingsInfo.getSessionIndex() + 1),
                    TESettingsInfo.getHostAddrByIndex(TESettingsInfo.getSessionIndex())));
            mSessionStausView.setVisibility(View.VISIBLE);
        } else {
            mSessionStausView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stdActivityRef.setCurrActivity(this);
        //Must before setContentView
        if(getResources().getBoolean(R.bool.bSpecialHandleLicense)) {
            String appId = getResources().getString(R.string.application_Id);
            if(appId.compareTo("com.densowave.terminalemulation") == 0) {
                if(Build.DEVICE.compareTo("BHT1600") == 0) {
                    stdActivityRef.gIsActivate = true;
                } else {
                    UIUtility.messageBox(MainActivity.this, R.string.MSG_AUTH_Densowave_BHT1600_Alert, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }
            }
        } else {
            if (ActivateKeyUtility.verifyKeyFromDefaultFile(this) == true) {
                stdActivityRef.gIsActivate = true;
            }
        }

        setContentView(R.layout.activity_main);

        //Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mSessionStausView = findViewById(R.id.id_session_statuse);
        mFragmentLeftdrawer = (LeftMenuFrg)
                getSupportFragmentManager().findFragmentById(R.id.fragment_left_drawer);
        mFragmentLeftdrawer.setUp(mToolbar);
        mFragmentLeftdrawer.setDrawerListener(this);

        mMacroView = (RelativeLayout) findViewById(R.id.macro_view);

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        updateFABStatus(FABStatus.Connect);

        mDecorView = getWindow().getDecorView();

        Cursor = new CursorView(this);
        mContentView = new ContentView(this, Cursor);
        mContentView.setOnDoubleTapListener(new ContentView.OnContentViewListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    procFullScreen();
                    return true;
                }
                return false;
            }
        });
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

        mLogoView = (RelativeLayout) findViewById(R.id.logo_view);
        mLogoView.setClickable(true);
        mLogoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return dbClickDetector.onTouchEvent(event);
            }
        });
        mMainRelLayout = (RelativeLayout) findViewById(R.id.mainRelLayout);
        mMainRelLayout.addView(mContentView);
        mMainRelLayout.addView(Cursor);
        mMainRelLayout.setVisibility(View.INVISIBLE);

        mKeyboardViewUtility = new TEKeyboardViewUtility(this, (KeyboardView) findViewById(R.id.software_keyboard_view), mContentView);
        mKeyboardViewUtility.setListener(this);

        mSessionJumpBtn = (ImageView) findViewById(R.id.session_jump_id);
        MoveImage sjListener = new MoveImage(new MoveImage.MoveImageBtnListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                int nSession = TESettingsInfo.getSessionIndex(), nOriSession = TESettingsInfo.getSessionIndex();
                do {
                    ++nSession;
                    if (nSession > TESettingsInfo.getSessionCount() - 1)
                        nSession = 0;
                    if (TESettingsInfo.getHostIsShowSessionNumber(nSession) == true)
                        break;
                } while (true);
                if (nSession != nOriSession) {
                    mFragmentLeftdrawer.clickSession(nSession);
                    updateConnMenuItem();
                }
                return true;
            }

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    TESettingsInfo.setSessionNumberLoc(parms.leftMargin, parms.topMargin);
                }
                return true;
            }
        });
        mSessionJumpBtn.setOnTouchListener(sjListener);

        mWiFiStatusIcon = (ImageView) findViewById(R.id.wifi_icon_id);
        MoveImage mvWifI = new MoveImage(new MoveImage.MoveImageBtnListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                int wifiStrength = CipherUtility.getWiFiStrength(MainActivity.this);
                UIUtility.messageBox(MainActivity.this, String.format(getResources().getString(R.string.MSG_WifiAlert), wifiStrength), null);
                return true;
            }

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    TESettingsInfo.setWiFiIconLoc(parms.leftMargin, parms.topMargin);
                }
                return true;
            }
        });
        mWiFiStatusIcon.setOnTouchListener(mvWifI);
        mBattStatusIcon = (ImageView) findViewById(R.id.batt_icon_id);
        MoveImage mvBatt = new MoveImage(new MoveImage.MoveImageBtnListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                int batStrength = CipherUtility.getBatteryPct(MainActivity.this);
                UIUtility.messageBox(MainActivity.this, String.format(getResources().getString(R.string.MSG_BattAlert), batStrength), null);
                return true;
            }

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    TESettingsInfo.setBattIconLoc(parms.leftMargin, parms.topMargin);
                }
                return true;
            }
        });
        mBattStatusIcon.setOnTouchListener(mvBatt);

        registerForContextMenu(mMainRelLayout);

        CipherReaderControl.InitReader(this, myDataReceiver);
        TerminalProcess.initKeyCodeMap();
        UIUtility.init();

        mTerminalProcessFrg = (TerminalProcessFrg) getSupportFragmentManager().findFragmentByTag(TAG_TERPROC_FRAGMENT);
        // If the Fragment is null, then it is first create.
        if (mTerminalProcessFrg == null) {
            UIUtility.doLoadSettingProc(MainActivity.this, new UIUtility.OnLoadSettingProcListener() {
                @Override
                public void onLoadResult(boolean bSuccess) {
                    if (bSuccess) {
                        mTerminalProcessFrg = new TerminalProcessFrg();
                        getSupportFragmentManager().beginTransaction().add(mTerminalProcessFrg, TAG_TERPROC_FRAGMENT).commit();
                        mTerminalProcessFrg.syncSessionsFromSettings();
                        syncSessionsFromSettings();
                        Boolean bAutoConn = TESettingsInfo.getHostIsAutoconnectByIndex(TESettingsInfo.getSessionIndex());
                        if (bAutoConn)
                            SessionConnect();
                    } else {
                        //Todo: give a warning
                        finish();
                    }
                }
            });
        } else { // If the Fragment is non-null, then it is being retained over a configuration change.
            // Restore saved state.
            syncSessionsFromSettings();
            showConnectionView(isCurSessionConnected());
            mContentView.refresh();
            setSessionJumpImage(TESettingsInfo.getSessionIndex());
            if (savedInstanceState != null) {
                boolean bLastFullScreen = savedInstanceState.getBoolean(KEY_FULL_SCREEEN);
                if (bLastFullScreen) {
                    mBFullScreen = false; //To trigger full screen in procFullScreen
                    procFullScreen();
                } else {
                    setSessionStatusView();
                }
            }
            updateRecordButtonVisible();
            if (isCurSessionConnected()) {
                updateFABStatus(FABStatus.Keyboard);
            } else {
                updateFABStatus(FABStatus.Connect);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TerminalProcess.clearKeyCodeMap();

        TESettingsInfo.saveSessionSettings(this);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FULL_SCREEEN, mBFullScreen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SessionSettings.REQ_EDIT:
                mFragmentLeftdrawer.updateSessionTitles();
                mContentView.refresh();
                setSessionJumpImage(TESettingsInfo.getSessionIndex());
                setSessionStatusView();
                updateRecordButtonVisible();
                procAlertTimer();
                procUpdateWiFiAndBattIconTimer();
                if(isCurSessionConnected()) {
                    mTerminalProcessFrg.resetCurSessionKeyList();
                }
                break;
            case SessionSettings.REQ_ADD: {
                if (resultCode == SessionSettings.RESULT_ADD) {
                    TESettingsInfo.addSession(SessionSettingsBase.getAddedSession());
                    int nAddedSessionIdx = TESettingsInfo.getSessionCount() - 1;
                    TerminalProcess process = new TerminalProcess();
                    process.setMacroList(TESettingsInfo.getHostMacroListByIndex(nAddedSessionIdx));
                    mTerminalProcessFrg.addTerminalProcess(process);
                    mFragmentLeftdrawer.syncSessionsViewFromSettings();
                    mFragmentLeftdrawer.clickSession(nAddedSessionIdx);
                    setSessionJumpImage(TESettingsInfo.getSessionIndex());
                    procAlertTimer();
                    procUpdateWiFiAndBattIconTimer();
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        //Handle HW key event
        if(TESettingsInfo.getHostIsHWExitByIndex(TESettingsInfo.getSessionIndex()) && //Shift + Right
                KeyMapList.isShiftPressed(event) &&
                keyCode ==  KeyEvent.KEYCODE_DPAD_RIGHT) {
            stdActivityRef.getCurrActivity().onExit();
            return true;
        } else if(TESettingsInfo.getHostIsHWShowSIPByIndex(TESettingsInfo.getSessionIndex()) && //Shift + Esc
                KeyMapList.isShiftPressed(event) &&
                keyCode ==  KeyEvent.KEYCODE_ESCAPE) {
            UIUtility.showSIP(this, mContentView);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        mTerminalProcessFrg.removeTerminalProc(position);
        setSessionStatusView();
    }

    @Override
    public void onDrawerItemSetting(int position) {
        SessionSetting(position);
    }

    @Override
    public void onAddSession() {
        if (TESettingsInfo.getSessionCount() < TESettingsInfo.MAX_SESSION_COUNT) {
            stdActivityRef.gCurrentEditSessionIndex = -1;
            Intent intent = new Intent(this, SessionSettings.class);
            startActivityForResult(intent, SessionSettings.REQ_ADD);
        }
    }

    public void onClickStop(View v) {
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.recMacro(false);
        UpdateRecordButton();
    }

    public void onClickPlay(View v) {
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.playMacro();
        UpdateRecordButton();
    }

    public void onClickRec(View v) {
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.recMacro(true);
        UpdateRecordButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenuItemConn = menu.findItem(R.id.connection);
        if (TESettingsInfo.showEditProfile() == true) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    final View menuItemView = findViewById(R.id.sessionSettings);
                    UIUtility.showTourEditProfile(MainActivity.this, menuItemView);
                }
            });
        }
        MenuItem activation = menu.findItem(R.id.activation_key);
        activation.setEnabled(stdActivityRef.gIsActivate == false);
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
                SessionSetting(TESettingsInfo.getSessionIndex());
                break;
            case R.id.full_screen:
                procFullScreen();
                break;
            case R.id.access_ctrl:
                if (TESettingsInfo.getIsAccessCtrlProtected()) {
                    UIUtility.doCheckAccessCtrlDialog(MainActivity.this,
                            new UIUtility.OnAccessCtrlChkListener() {
                                @Override
                                public void onValid() {
                                    UIUtility.doAccessCtrlDialog(MainActivity.this);
                                }
                            });
                } else {
                    UIUtility.doAccessCtrlDialog(MainActivity.this);
                }
                break;
            case R.id.activation_key:
                UIUtility.doActivationDialog(MainActivity.this, new UIUtility.OnActivationListener() {
                    @Override
                    public void onResult(boolean bActivate) {
                        if (bActivate == false) {
                            Toast.makeText(MainActivity.this, R.string.MSG_Activate_Warn, Toast.LENGTH_SHORT).show();
                        } else {
                            ActivateKeyUtility.getInstance().genKeyFile(MainActivity.this);
                            Toast.makeText(MainActivity.this, R.string.MSG_Activate_Succ, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.export_settings:
                SimpleFileDialog exptDialog = new SimpleFileDialog(this,
                        getResources().getString(R.string.STR_expot_setting),
                        getResources().getString(R.string.STR_ExtJson),
                        SimpleFileDialog.Type.FILE_CREATE,

                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onFilePath(final String chosenDir) {
                                final File file = new File(chosenDir);
                                if(file.exists() == true) {
                                    String strMsg = getResources().getString(R.string.Msg_file_exist);
                                    UIUtility.doYesNoDialog(MainActivity.this, strMsg, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    file.delete();
                                                    if (TESettingsInfo.exportSessionSettings(chosenDir) == false) {
                                                        Toast.makeText(MainActivity.this, R.string.MSG_Export_Warn, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, R.string.MSG_Export_ok, Toast.LENGTH_SHORT).show();
                                                    }
                                                    TESettingsInfo.setExportSettingsPath(chosenDir);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                                } else {
                                    if (TESettingsInfo.exportSessionSettings(chosenDir) == false) {
                                        Toast.makeText(MainActivity.this, R.string.MSG_Export_Warn, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, R.string.MSG_Export_ok, Toast.LENGTH_SHORT).show();
                                    }
                                    TESettingsInfo.setExportSettingsPath(chosenDir);
                                }
                            }

                            @Override
                            public void onFileSel(String path) {
                            }

                            @Override
                            public void onFileSelNext(String path) {
                            }
                        });

                exptDialog.chooseFile_or_Dir(TESettingsInfo.getExportSettingsPath());
                break;
            case R.id.import_settings:
                SimpleFileDialog imptDialog = new SimpleFileDialog(this,
                        getResources().getString(R.string.str_import),
                        new ArrayList<>(Arrays.asList((getResources().getString(R.string.STR_ExtJson)))),
                        SimpleFileDialog.Type.FILE_CHOOSE,

                        new SimpleFileDialog.SimpleFileDialogListener() {
                            @Override
                            public void onFilePath(String chosenDir) {
                                if (TESettingsInfo.importSessionSettings(chosenDir) == false) {
                                    Toast.makeText(MainActivity.this, R.string.MSG_Import_Warn, Toast.LENGTH_SHORT).show();
                                } else {
                                    mTerminalProcessFrg.syncSessionsFromSettings();
                                    syncSessionsFromSettings();
                                    Toast.makeText(MainActivity.this, R.string.MSG_Import_ok, Toast.LENGTH_SHORT).show();
                                }
                                TESettingsInfo.setImportSettingsPath(chosenDir);
                            }

                            @Override
                            public void onFileSel(String path) {
                            }

                            @Override
                            public void onFileSelNext(String path) {
                            }
                        });

                imptDialog.chooseFile_or_Dir(TESettingsInfo.getImportSettingsPath());
                break;
            case R.id.language:
                int nSelLan = TESettingsInfo.getCurLanguageIdx();
                UIUtility.listMessageBox(R.string.str_language,
                        R.array.Languages,
                        nSelLan,
                        this,
                        new UIUtility.OnListMessageBoxListener() {
                            @Override
                            public void onSelResult(String result, int nSelIdx) {
                                TESettingsInfo.setCurLanguageIdx(nSelIdx);
                            }
                        });
                break;
            case R.id.id_program_name:
                UIUtility.editMessageBox(R.string.MSG_set_program_name, TESettingsInfo.getProgramName(), this, new UIUtility.OnEditMessageBoxListener() {
                    @Override
                    public void onResult(String result) {
                        TESettingsInfo.setProgramName(result);
                        MainActivity.this.setTitle(result);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case R.id.about:
                onAbout();
                break;
            case R.id.exit:
                onExit();
                break;
            //Todo:Will remove
            case R.id.key_gen:
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                // Creates a new text clip to put on the clipboard
                String key = ActivateKeyUtility.getValidKey();
                ClipData clip = ClipData.newPlainText("simple text", key);
                // Set the clipboard's primary clip.
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, String.format("%s\nSerial number copied!", key), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAbout() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        TextView AppVer = (TextView) messageView.findViewById(R.id.txt_app_ver);
        TextView ServiceVer = (TextView) messageView.findViewById(R.id.txt_service_ver);
        AppVer.setText(BuildConfig.VERSION_NAME);

        try {
            PackageManager manager = this.getPackageManager();
            String barcodeServicePKGName = getString(R.string.STR_barcode_service);
            PackageInfo info = manager.getPackageInfo(barcodeServicePKGName, 0);
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

    public void onExit() {
        if (TESettingsInfo.getIsAccessCtrlProtected() && TESettingsInfo.getIsExitProtect()) {
            UIUtility.doCheckAccessCtrlDialog(MainActivity.this,
                    new UIUtility.OnAccessCtrlChkListener() {
                        @Override
                        public void onValid() {
                            HideKeyboard();
                            finish();
                        }
                    });
        } else {
            HideKeyboard();
            finish();
        }
    }

    //Listener TEKeyboardView Begin
    @Override
    public void onSetKeyboardType(TEKeyboardViewUtility.KeyboardType kType) {
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.setKeyboardType(kType);
    }

    @Override
    public void onShowKeyboard() {
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.setShowKeyboard(true);
    }

    @Override
    public void onHideKeyboard() {
        if (isCurSessionConnected())
            updateFABStatus(FABStatus.Keyboard);
        else
            updateFABStatus(FABStatus.Connect);
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.setShowKeyboard(false);
    }
    //Listener TEKeyboardView End

    private boolean isCurSessionConnected() {
        if(mTerminalProcessFrg == null) {
            return false;
        }
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
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
        if (TESettingsInfo.getSessionIndex() == idxSession)
            return;
        TerminalProcess curSeesion = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        TerminalProcess nextSession = mTerminalProcessFrg.getTerminalProc(idxSession);

        //un-bind between TerminalProcess and MainActivity (Actually is ContentView)
        curSeesion.setListener(null);
        //bind between TerminalProcess and ContentView
        nextSession.setListener(mOnTerminalProcessListener);
        mContentView.setTerminalProc(nextSession);
        TESettingsInfo.setSessionIndex(idxSession);
        showConnectionView(isCurSessionConnected());
        mContentView.refresh();
        setSessionJumpImage(idxSession);
        setSessionStatusView();
        procAlertTimer();
        procUpdateWiFiAndBattIconTimer();
        if(mBFullScreen == true) {
            doFullScreen(false);
            doFullScreen(true);
        }
        updateRecordButtonVisible();
        mKeyboardViewUtility.setKeyboard(nextSession.getKeyboardType());
        if (isCurSessionConnected() == false) {
            mKeyboardViewUtility.hideTEKeyboard();
            updateFABStatus(FABStatus.Connect);
        } else if(nextSession.getShowKeyboard() == false) {
            mKeyboardViewUtility.hideTEKeyboard();
            updateFABStatus(FABStatus.Keyboard);
        } else {
            mKeyboardViewUtility.showTEKeyboard();
            updateFABStatus(FABStatus.Gone);
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

    private void doFullScreen(boolean bFull) {
        if(bFull) {
            boolean bShowStatusBarOnFull = TESettingsInfo.getHostShowTaskBarOnFullScreenByIndex(TESettingsInfo.getSessionIndex());
            boolean bShowNavibarOnFullScreen = TESettingsInfo.getHostIsShowNavibarOnFullScreenByIndex(TESettingsInfo.getSessionIndex());
            getSupportActionBar().hide();
            int uiFullScreenOptions =  View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            if(bShowStatusBarOnFull == false) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //Handle status bar for API level <=16
                uiFullScreenOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN; //Handle status bar for API level >16
            }
            if(bShowNavibarOnFullScreen == false) {
                uiFullScreenOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            mDecorView.setFitsSystemWindows(false);
            mDecorView.setSystemUiVisibility(uiFullScreenOptions);
            mBFullScreen = true;
            if (TESettingsInfo.showResetFullScreen() == true) {
                UIUtility.showResetFullScreen(MainActivity.this, mLogoView.findViewById(R.id.ImgLogoView));
            }
        } else {
            getSupportActionBar().show();
            int oriUIOption = mDecorView.getSystemUiVisibility();
            int newUIOption = oriUIOption;
            newUIOption ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            //Handle status bar for API level <=16
            if((getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) ==
                    WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            if((oriUIOption & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN) {
                newUIOption ^= View.SYSTEM_UI_FLAG_FULLSCREEN; //Handle status bar for API level >16
            }

            if((oriUIOption & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
                newUIOption ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION; //Handle navigation bar
            }
            mDecorView.setSystemUiVisibility(newUIOption);
            mBFullScreen = false;
        }
        setSessionStatusView();
    }

    private void procFullScreen() {
        if (mBFullScreen == false) {
            doFullScreen(true);
        } else {
            if (TESettingsInfo.getIsAccessCtrlProtected() && TESettingsInfo.getIsExitFullScreenProtect()) {
                UIUtility.doCheckAccessCtrlDialog(MainActivity.this,
                        new UIUtility.OnAccessCtrlChkListener() {
                            @Override
                            public void onValid() {
                                doFullScreen(false);
                            }
                        });
            } else {
                doFullScreen(false);
            }
        }
        procUpdateWiFiAndBattIconTimer();
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View CurrentFocus = this.getCurrentFocus();
        //imm.restartInput(CurrentFocus);
        if (CurrentFocus != null)
            imm.hideSoftInputFromWindow(CurrentFocus.getWindowToken(), 0);
    }

    private void SessionConnect() {
        UIUtility.showProgressDlg(MainActivity.this, true, R.string.MSG_Connecting);
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.processConnect();
    }

    public void SessionDisConnect() {
        TerminalProcess termProc = mTerminalProcessFrg.getTerminalProc(TESettingsInfo.getSessionIndex());
        termProc.processDisConnect();
        mMainRelLayout.setVisibility(View.INVISIBLE);
        mLogoView.setVisibility(View.VISIBLE);
    }

    private void SessionSetting(final int nSessionIdx) {
        if (TESettingsInfo.getIsAccessCtrlProtected() && TESettingsInfo.getIsSettingsProtect()) {
            UIUtility.doCheckAccessCtrlDialog(MainActivity.this,
                    new UIUtility.OnAccessCtrlChkListener() {
                        @Override
                        public void onValid() {
                            stdActivityRef.gCurrentEditSessionIndex = nSessionIdx;
                            Intent intent = new Intent(MainActivity.this, SessionSettings.class);
                            startActivityForResult(intent, SessionSettings.REQ_EDIT);
                        }
                    });
        } else {
            stdActivityRef.gCurrentEditSessionIndex = nSessionIdx;
            Intent intent = new Intent(this, SessionSettings.class);
            startActivityForResult(intent, SessionSettings.REQ_EDIT);
        }
    }

    public void setSessionJumpImage(int sessionIndex) {
        if (TESettingsInfo.getHostIsShowSessionNumber(sessionIndex) == false) {
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
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSessionJumpBtn.getLayoutParams();
            params.leftMargin = TESettingsInfo.getSessionNumberLocLeft();
            params.topMargin = TESettingsInfo.getSessionNumberLocTop();
            mSessionJumpBtn.setLayoutParams(params);
        }
    }

    enum FABStatus {
        Connect,
        Keyboard,
        Gone
    }

    private static class MoveImage implements View.OnTouchListener {
        public interface MoveImageBtnListener {
            boolean onSingleTapUp(MotionEvent event);
            boolean onTouch(View view, MotionEvent event);
        }
        private float mPrevX = 0;
        private float mPrevY = 0;
        private View mView;
        private MoveImageBtnListener mListener = null;
        final GestureDetector mDetector = new GestureDetector(stdActivityRef.getCurrActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                mPrevX = event.getX();
                mPrevY = event.getY();
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent event) {
                return mListener.onSingleTapUp(event);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float fDisX = 0, fDisY = 0;
                if (e2 != null) {
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

        public MoveImage(MoveImageBtnListener listener) {
            mListener = listener;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            mView = view;
            if(mListener != null) {
                mListener.onTouch(view, event);
            }
            return mDetector.onTouchEvent(event);
        }
    }
}
