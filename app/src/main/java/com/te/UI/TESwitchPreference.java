package com.te.UI;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.cipherlab.terminalemulation.R;

/**
 * Created by wange on 10/5/2016.
 */
public class TESwitchPreference extends SwitchPreference {

    public interface OnTESwitchListener {
        void onClick();
        void onChecked(boolean isChecked);
    }

    private OnTESwitchListener mListener = null;

    public TESwitchPreference(Context context) {
        super(context);
    }

    public TESwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TESwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnTESwitchListener(OnTESwitchListener listener) {
        mListener = listener;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        Switch teSwitch = (Switch) view.findViewById(R.id.te_switch);
        if(teSwitch != null) {
            teSwitch.setOnCheckedChangeListener(null);
            teSwitch.setChecked(isChecked());
            teSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setChecked(isChecked);
                    if(mListener != null) {
                        mListener.onChecked(isChecked);
                    }
                }
            });
        }
    }

    @Override
    protected void onClick() {
        if(mListener != null) {
            mListener.onClick();
        }
    }
}