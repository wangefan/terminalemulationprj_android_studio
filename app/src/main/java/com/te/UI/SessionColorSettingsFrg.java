package com.te.UI;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.terminalemulation.R;

import Terminals.TESettings;

public class SessionColorSettingsFrg extends Fragment {

    //Data members
    protected TESettings.SessionSetting mSetting = null;
    private int [] mColorChoices = null;
    private int mSelectedColor = 0;

    public SessionColorSettingsFrg() {
    }

    public void setSessionSetting(TESettings.SessionSetting setting) {
        mSetting = setting;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] color_array = getResources().getStringArray(R.array.default_color_choice_values);
        if (color_array != null && color_array.length > 0) {
            mColorChoices = new int[color_array.length];
            for (int i = 0; i < color_array.length; i++) {
                mColorChoices[i] = Color.parseColor(color_array[i]);
            }
        }
        // Inflating view layout
        View colorSettingView = inflater.inflate(R.layout.color_setting_fragment, container, false);
        RelativeLayout layFont = (RelativeLayout) colorSettingView.findViewById(R.id.color_font);
        layFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogDash colordashfragment = ColorPickerDialogDash
                        .newInstance(R.string.color_picker_fonts_title, mColorChoices, mSelectedColor, 5);
                // Implement listener to get selected color value
                colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mSelectedColor = color;
                    }
                });
                colordashfragment.show(getFragmentManager(), "dash");
            }
        });

        return colorSettingView;
    }
}
