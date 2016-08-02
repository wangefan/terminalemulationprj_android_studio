package com.te.UI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.inputmethodservice.KeyboardView;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.terminalemulation.BuildConfig;
import com.example.terminalemulation.R;
import com.te.UI.LeftMenuFrg.LeftMenuListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SessionProcess.TerminalProcess;
import Terminals.CipherReaderControl;
import Terminals.ContentView;
import Terminals.CursorView;
import Terminals.TESettingsInfo;
import Terminals.TerminalBase;
import Terminals.stdActivityRef;

public class MainActivity extends AppCompatActivity
        implements LeftMenuListener, TEKeyboardViewUtility.TEKeyboardViewListener

{
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
                TerminalProcess termProc = (TerminalProcess) mCollSessions.get(TESettingsInfo.getSessionIndex());
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

    private final long UPDATE_ALERT_INTERVAL = 300000; //300 sec, 5 min

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
    private MenuItem mMenuItemConn;
    private Handler mUpdateWifiAlertHandler = new Handler();
    private Handler mUpdateBaterAlertHandler = new Handler();
    private TerminalProcess.OnTerminalProcessListener mOnTerminalProcessListener = new TerminalProcess.OnTerminalProcessListener() {
        @Override
        public void onConnected() {
            showConnectionView(true);
            updateRecordButtonVisible();
            UpdateRecordButton();
            updateConnMenuItem();
            updateFABStatus(FABStatus.Keyboard);
            if(TESettingsInfo.getHostIsAutoFullScreenOnConnByIndex(TESettingsInfo.getSessionIndex()) == true) {
                procFullScreen();
            }
            UIUtility.showProgressDlg(false, 0);
            mKeyboardViewUtility.showTEKeyboard();
        }

        @Override
        public void onDisConnected() {
            showConnectionView(false);
            updateRecordButtonVisible();
            updateConnMenuItem();
            updateFABStatus(FABStatus.Connect);
            UIUtility.cancelDetectNetworkOutRange();
            UIUtility.showProgressDlg(false, 0);
            mKeyboardViewUtility.hideTEKeyboard();
            Toast.makeText(MainActivity.this, getString(R.string.MSG_Disonnected), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNotify(String action, Object... params) {
            if (action.compareToIgnoreCase(TerminalBase.NOTF_ACT_INVALIDATE) == 0) {
                mContentView.postInvalidate();
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_UPDATE_GRID) == 0) {
                mContentView.updateViewGrid();
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_CLEAR_VIEW) == 0) {
                mContentView.ClearView();
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_DRAW_SPACE) == 0) {
                mContentView.DrawSpace((Integer) params[0], (Integer) params[1], (Integer) params[2]);
            } else if(action.compareToIgnoreCase(TerminalBase.NOTF_ACT_DRAWCHAR) == 0) {
                mContentView.DrawChar((Character) params[0], (Integer) params[1], (Integer) params[2], (Boolean) params[3], (Boolean) params[4]);
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
        TerminalProcess termProc = mCollSessions.get(TESettingsInfo.getSessionIndex());
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

    private void procAlertTimer() {
        boolean bWifiAlert = TESettingsInfo.getHostIsShowWifiAlertByIndex(TESettingsInfo.getSessionIndex());
        boolean bBatAlert = TESettingsInfo.getHostIsShowBatteryAlertByIndex(TESettingsInfo.getSessionIndex());
        if(bWifiAlert) {
            mUpdateWifiAlertHandler.removeCallbacksAndMessages(null);
            final int nWifiAlert = TESettingsInfo.getHostShowWifiAltLevelByIndex(TESettingsInfo.getSessionIndex());
            mUpdateWifiAlertHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int wifiStrength = CipherUtility.getWiFiStrength();
                    if(wifiStrength < nWifiAlert) {
                        final Runnable tempRun = this;
                        UIUtility.messageBox(String.format(getResources().getString(R.string.MSG_WifiAlert), wifiStrength), new DialogInterface.OnClickListener() {
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
        } else {
            mUpdateWifiAlertHandler.removeCallbacksAndMessages(null);
        }

        if(bBatAlert) {
            mUpdateBaterAlertHandler.removeCallbacksAndMessages(null);
            final int nBatAlert = TESettingsInfo.getHostShowBatteryAltLevelByIndex(TESettingsInfo.getSessionIndex());
            mUpdateBaterAlertHandler.postDelayed(new Runnable() {
                public int getBatteryPct() {
                    IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryStatus = registerReceiver(null, ifilter);
                    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    float batteryPct = level / (float) scale * 100;
                    return (int) batteryPct;
                }

                @Override
                public void run() {
                    int batStrength = getBatteryPct();
                    if(batStrength < nBatAlert) {
                        final Runnable tempRun = this;
                        UIUtility.messageBox(String.format(getResources().getString(R.string.MSG_BattAlert), batStrength), new DialogInterface.OnClickListener() {
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
        } else {
            mUpdateBaterAlertHandler.removeCallbacksAndMessages(null);
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

    private void initInOnCreate() {
        if(ActivateKeyUtility.verifyKeyFromDefaultFile() == true) {
            stdActivityRef.gIsActivate = true;
        }

        syncSessionsFromSettings();

        Boolean bAutoConn = TESettingsInfo.getHostIsAutoconnectByIndex(TESettingsInfo.getSessionIndex());
        if (bAutoConn)
            SessionConnect();
    }

    private void syncSessionsFromSettings() {
        mCollSessions.clear();
        for (int idxSession = 0; idxSession < TESettingsInfo.getSessionCount(); ++idxSession) {
            TerminalProcess Process = new TerminalProcess();
            Process.setMacroList(TESettingsInfo.getHostMacroListByIndex(idxSession));
            mCollSessions.add(Process);
        }
        TerminalProcess actSession = mCollSessions.get(TESettingsInfo.getSessionIndex());
        actSession.setListener(mOnTerminalProcessListener);
        mContentView.setTerminalProc(actSession);
        mContentView.refresh();
        setSessionStatusView();
        updateRecordButtonVisible();
        setSessionJumpImage(TESettingsInfo.getSessionIndex());
        procAlertTimer();
        mFragmentLeftdrawer.syncSessionsViewFromSettings();
        mFragmentLeftdrawer.clickSession(TESettingsInfo.getSessionIndex());
    }

    private void setSessionStatusView() {
        if(TESettingsInfo.getHostIsShowSessionStatus(TESettingsInfo.getSessionIndex()) == true && mBFullScreen == false) {
            String serverTypeName = TESettingsInfo.getHostTypeNameByIndex(TESettingsInfo.getSessionIndex());
            TextView tv = (TextView) mSessionStausView.findViewById(R.id.id_session_statuse_title);
            tv.setText(String.format(getResources().getString(R.string.Format_SessionStatus),
                    serverTypeName,
                    String.valueOf(TESettingsInfo.getSessionIndex()+1),
                    TESettingsInfo.getHostAddrByIndex(TESettingsInfo.getSessionIndex())));
            mSessionStausView.setVisibility(View.VISIBLE);
        } else {
            mSessionStausView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stdActivityRef.setCurrActivity(this);

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
        mMainRelLayout = (RelativeLayout) findViewById(R.id.mainRelLayout);
        mMainRelLayout.addView(mContentView);
        mMainRelLayout.addView(Cursor);
        mMainRelLayout.setVisibility(View.INVISIBLE);

        mKeyboardViewUtility = new TEKeyboardViewUtility(this, (KeyboardView) findViewById(R.id.software_keyboard_view), mContentView);
        mKeyboardViewUtility.setListener(this);

        mSessionJumpBtn = (ImageView) findViewById(R.id.session_jump_id);
        SessionJumpListener sjListener = new SessionJumpListener();
        mSessionJumpBtn.setOnTouchListener(sjListener);

        registerForContextMenu(mMainRelLayout);

        CipherReaderControl.InitReader(this, myDataReceiver);
        TerminalProcess.initKeyCodeMap();
        UIUtility.init(this);
        CipherUtility.init(this);

        // Initialize User Parm
        if (true == TESettingsInfo.loadSessionSettings(getApplicationContext())) {
            initInOnCreate();
        } else {
            //ask user to try to clear setting, or exit app
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            TESettingsInfo.deleteJsonFile();
                            if (!TESettingsInfo.loadSessionSettings(getApplicationContext())) {
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

        TerminalProcess.clearKeyCodeMap();

        TESettingsInfo.saveSessionSettings();
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
                setSessionJumpImage(TESettingsInfo.getSessionIndex());
                setSessionStatusView();
                updateRecordButtonVisible();
                procAlertTimer();
                break;
            case SessionSettings.REQ_ADD: {
                if (resultCode == SessionSettings.RESULT_ADD && SessionSettings.gEditSessionSetting != null) {
                    TESettingsInfo.addSession(SessionSettings.gEditSessionSetting);
                    int nAddedSessionIdx = TESettingsInfo.getSessionCount() - 1;
                    TerminalProcess process = new TerminalProcess();
                    process.setMacroList(TESettingsInfo.getHostMacroListByIndex(nAddedSessionIdx));
                    mCollSessions.add(process);
                    mFragmentLeftdrawer.syncSessionsViewFromSettings();
                    mFragmentLeftdrawer.clickSession(nAddedSessionIdx);
                    setSessionJumpImage(TESettingsInfo.getSessionIndex());
                    procAlertTimer();
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
        if (TESettingsInfo.getSessionCount() < TESettingsInfo.MAX_SESSION_COUNT) {
            Intent intent = new Intent(this, SessionSettings.class);
            intent.putExtra(SessionSettings.ACT_SETTING, SessionSettings.ACT_SETTING_ADD);
            startActivityForResult(intent, SessionSettings.REQ_ADD);
        }
    }

    public void onClickStop(View v) {
        TerminalProcess termProc = mCollSessions.get(TESettingsInfo.getSessionIndex());
        termProc.recMacro(false);
        UpdateRecordButton();
    }

    public void onClickPlay(View v) {
        TerminalProcess termProc = mCollSessions.get(TESettingsInfo.getSessionIndex());
        termProc.playMacro();
        UpdateRecordButton();
    }

    public void onClickRec(View v) {
        TerminalProcess termProc = mCollSessions.get(TESettingsInfo.getSessionIndex());
        termProc.recMacro(true);
        UpdateRecordButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenuItemConn = menu.findItem(R.id.connection);
        if(TESettingsInfo.showEditProfile() == true) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    final View menuItemView = findViewById(R.id.sessionSettings);
                    UIUtility.showTourEditProfile(menuItemView);
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
                if(TESettingsInfo.getIsAccessCtrlProtected()) {
                    UIUtility.doCheckAccessCtrlDialog(
                            new UIUtility.OnAccessCtrlChkListener() {
                                @Override
                                public void onValid() {
                                    TESettingsInfo.setAccessCtrlProtect(false);
                                    TESettingsInfo.setAccessCtrlProtectedPassword("");
                                    UIUtility.doAccessCtrlDialog();
                                }
                            });
                } else {
                    UIUtility.doAccessCtrlDialog();
                }
                break;
            case R.id.activation_key:
                UIUtility.doActivationDialog(new UIUtility.OnActivationListener() {
                    @Override
                    public void onResult(boolean bActivate) {
                        if(bActivate == false) {
                            Toast.makeText(MainActivity.this, R.string.MSG_Activate_Warn, Toast.LENGTH_SHORT).show();
                        } else {
                            ActivateKeyUtility.getInstance().genKeyFile();
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
                            public void onFilePath(String chosenDir) {
                                if(TESettingsInfo.exportSessionSettings(chosenDir) == false) {
                                    Toast.makeText(MainActivity.this, R.string.MSG_Export_Warn, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.MSG_Export_ok, Toast.LENGTH_SHORT).show();
                                }
                                TESettingsInfo.setExportSettingsPath(chosenDir);
                            }

                            @Override
                            public void onFileSel(String path) {
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
                                if(TESettingsInfo.importSessionSettings(chosenDir) == false) {
                                    Toast.makeText(MainActivity.this, R.string.MSG_Import_Warn, Toast.LENGTH_SHORT).show();
                                } else {
                                    syncSessionsFromSettings();
                                    Toast.makeText(MainActivity.this, R.string.MSG_Import_ok, Toast.LENGTH_SHORT).show();
                                }
                                TESettingsInfo.setImportSettingsPath(chosenDir);
                            }

                            @Override
                            public void onFileSel(String path) {
                            }
                        });

                imptDialog.chooseFile_or_Dir(TESettingsInfo.getImportSettingsPath());
                break;
            case R.id.about:
                onAbout();
                break;
            case R.id.exit:
                onExit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
            AppVer.setText(BuildConfig.VERSION_NAME);
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

    public void onExit() {
        if(TESettingsInfo.getIsAccessCtrlProtected() && TESettingsInfo.getIsExitProtect()) {
            UIUtility.doCheckAccessCtrlDialog(
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
        TerminalProcess termProc = (TerminalProcess) mCollSessions.get(TESettingsInfo.getSessionIndex());
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
        TerminalProcess curSeesion = mCollSessions.get(TESettingsInfo.getSessionIndex());
        TerminalProcess nextSession = mCollSessions.get(idxSession);

        //un-bind between TerminalProcess and MainActivity (Actually is ContentView)
        curSeesion.setListener(null);
        //bind between TerminalProcess and ContentView
        nextSession.setListener(mOnTerminalProcessListener);
        mContentView.setTerminalProc(nextSession);
        TESettingsInfo.setSessionIndex(idxSession);
        mKeyboardViewUtility.hideTEKeyboard();
        showConnectionView(isCurSessionConnected());
        mContentView.refresh();
        setSessionJumpImage(idxSession);
        setSessionStatusView();
        updateRecordButtonVisible();
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
            if(TESettingsInfo.showResetFullScreen() == true) {
                UIUtility.showResetFullScreen(mLogoView.findViewById(R.id.ImgLogoView));
            }
        } else {
            if(TESettingsInfo.getIsAccessCtrlProtected() && TESettingsInfo.getIsExitFullScreenProtect()) {
                UIUtility.doCheckAccessCtrlDialog(
                        new UIUtility.OnAccessCtrlChkListener() {
                            @Override
                            public void onValid() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                getSupportActionBar().show();
                                mDecorView.setSystemUiVisibility(
                                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                                mBFullScreen = false;
                                setSessionStatusView();
                            }
                        });
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
        setSessionStatusView();
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
        TerminalProcess termProc = mCollSessions.get(TESettingsInfo.getSessionIndex());
        termProc.processConnect();
    }

    public void SessionDisConnect() {
        TerminalProcess termProc = mCollSessions.get(TESettingsInfo.getSessionIndex());
        termProc.processDisConnect();
        mMainRelLayout.setVisibility(View.INVISIBLE);
        mLogoView.setVisibility(View.VISIBLE);
    }

    private void SessionSetting(final int nSessionIdx) {
        if(TESettingsInfo.getIsAccessCtrlProtected() && TESettingsInfo.getIsSettingsProtect()) {
            UIUtility.doCheckAccessCtrlDialog(
                    new UIUtility.OnAccessCtrlChkListener() {
                        @Override
                        public void onValid() {
                            Intent intent = new Intent(MainActivity.this, SessionSettings.class);
                            intent.putExtra(SessionSettings.ACT_SETTING, SessionSettings.ACT_SETTING_EDIT);
                            intent.putExtra(SessionSettings.ACT_SETTING_EDIT_GET_SESSION_IDX, nSessionIdx);
                            startActivityForResult(intent, SessionSettings.REQ_EDIT);
                        }
                    });
        } else {
            Intent intent = new Intent(this, SessionSettings.class);
            intent.putExtra(SessionSettings.ACT_SETTING, SessionSettings.ACT_SETTING_EDIT);
            intent.putExtra(SessionSettings.ACT_SETTING_EDIT_GET_SESSION_IDX, nSessionIdx);
            startActivityForResult(intent, SessionSettings.REQ_EDIT);
        }
    }

    public void setSessionJumpImage(int sessionIndex) {
        if(TESettingsInfo.getHostIsShowSessionNumber(sessionIndex) == false) {
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
                int nSession = TESettingsInfo.getSessionIndex(), nOriSession = TESettingsInfo.getSessionIndex();
                do {
                    ++nSession;
                    if(nSession > TESettingsInfo.getSessionCount()-1)
                        nSession = 0;
                    if(TESettingsInfo.getHostIsShowSessionNumber(nSession) == true)
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
            if(event.getAction() == MotionEvent.ACTION_UP) {
                RelativeLayout.LayoutParams parms = (RelativeLayout.LayoutParams) mSessionJumpBtn.getLayoutParams();
                TESettingsInfo.setSessionNumberLoc(parms.leftMargin, parms.topMargin);
            }
            return mDetector.onTouchEvent(event);
        }
    }
}
