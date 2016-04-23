package com.te.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.example.terminalemulation.R;

/**
 * Created by wange on 23/4/2016.
 */
public class MyIPPreference extends DialogPreference {
    private String mStrIp = "";
    private EditText[] mEdIp = new EditText[4];

    public MyIPPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.preference_my_ip);
    }

    public void setIp(String strIp) {
        mStrIp = strIp;
    }

    public String getIp() {
        return mStrIp;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mEdIp[0] = (EditText) view.findViewById(R.id.ip_1);
        mEdIp[1] = (EditText) view.findViewById(R.id.ip_2);
        mEdIp[2] = (EditText) view.findViewById(R.id.ip_3);
        mEdIp[3] = (EditText) view.findViewById(R.id.ip_4);
        final String IP_SEP = "\\.";
        String [] strip = mStrIp.split(IP_SEP);
        if(strip.length == 4) {
            IpTextWatcher [] ipWatchers = new IpTextWatcher[4];
            for(int idxIp = 0; idxIp <strip.length; ++idxIp) {
                ipWatchers[idxIp] = new IpTextWatcher(mEdIp[idxIp], mEdIp);
                mEdIp[idxIp].setText(strip[idxIp]);
                mEdIp[idxIp].addTextChangedListener(ipWatchers[idxIp]);
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if ( which == DialogInterface.BUTTON_POSITIVE ) {
            mStrIp = String.format("%s.%s.%s.%s", mEdIp[0].getText(), mEdIp[1].getText(), mEdIp[2].getText(), mEdIp[3].getText());
            persistBoolean(!getPersistedBoolean(true));//here to trigger SharedPreferences.OnSharedPreferenceChangeListener
            callChangeListener( mStrIp );
        }
    }
}
