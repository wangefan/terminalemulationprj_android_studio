package com.te.UI;

import android.content.Context;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.terminalemulation.R;

/**
 * Created by wange on 10/5/2016.
 */
public class TESwitchPreference extends SwitchPreference {
    public TESwitchPreference(Context context) {
        super(context);
    }

    public TESwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TESwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onClick() {
        Log.d("","");
        super.onClick();
    }
}