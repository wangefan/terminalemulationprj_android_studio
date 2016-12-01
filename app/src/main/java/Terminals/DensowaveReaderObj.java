package Terminals;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.densowave.barcode.GeneralString;

public class DensowaveReaderObj extends ReaderObjBase {
    private com.densowave.barcode.ReaderManager mReaderManager;
    private CipherReaderControl.OnReaderControlListener mListener;
    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Software trigger must receive this intent message
            String action = intent.getAction();
            String data;
            if (action.compareTo(GeneralString.Intent_SOFTTRIGGER_DATA) == 0) {
                data = intent.getStringExtra(GeneralString.BcReaderData);
            } else if (action.compareTo(GeneralString.Intent_PASS_TO_APP) == 0) {
                data = intent.getStringExtra(GeneralString.BcReaderData);
                if(mListener != null) {
                    mListener.onData(data);
                }
            } else if (action.equals(GeneralString.Intent_READERSERVICE_CONNECTED)) {
                if(mListener != null) {
                    mListener.onReaderServiceConnected();
                }
            }
        }
    };

    public DensowaveReaderObj(Context context, CipherReaderControl.OnReaderControlListener listener) {
        mReaderManager = com.densowave.barcode.ReaderManager.InitInstance(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(com.densowave.barcode.GeneralString.Intent_SOFTTRIGGER_DATA);
        filter.addAction(com.densowave.barcode.GeneralString.Intent_PASS_TO_APP);
        filter.addAction(com.densowave.barcode.GeneralString.Intent_READERSERVICE_CONNECTED);
        context.registerReceiver(mDataReceiver, filter);
        mListener = listener;
    }

    @Override
    public void releaseReader(Context context) {
        context.unregisterReceiver(mDataReceiver);
        if(mReaderManager != null) {
            mReaderManager.Release();
        }
    }

    @Override
    public void setActived(Boolean Enable) {
        if(mReaderManager != null) {
            mReaderManager.SetActive(Enable);
        }
    }
}
