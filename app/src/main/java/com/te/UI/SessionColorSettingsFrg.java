package com.te.UI;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.terminalemulation.R;

import Terminals.TESettings;

public class SessionColorSettingsFrg extends Fragment {

    //Data members
    protected TESettings.SessionSetting mSetting = null;
    private int [] mColorChoices = null;
    TextView mFontView = null;
    RelativeLayout mBgView = null;
    private int mSelectedFontsColor = 0;
    private int mSelectedBgColor = 0;


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
        mSelectedFontsColor = mSetting.getFontColor();
        mSelectedBgColor = mSetting.getBGColor();
        // Inflating view layout
        View colorSettingView = inflater.inflate(R.layout.color_setting_fragment, container, false);
        mFontView = (TextView) colorSettingView.findViewById(R.id.color_preview_fonts);
        mBgView = (RelativeLayout) colorSettingView.findViewById(R.id.color_preview_bg);
        mFontView.setTextColor(mSelectedFontsColor);
        mBgView.setBackgroundColor(mSelectedBgColor);
        RelativeLayout layFont = (RelativeLayout) colorSettingView.findViewById(R.id.color_font);
        layFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogDash colordashfragment = ColorPickerDialogDash
                        .newInstance(R.string.color_picker_fonts_title, mColorChoices, mSelectedFontsColor, 5);
                // Implement listener to get selected color value
                colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mSelectedFontsColor = color;
                        mFontView.setTextColor(mSelectedFontsColor);
                        mSetting.setFontColor(mSelectedFontsColor);
                    }
                });
                colordashfragment.show(getFragmentManager(), "ColorPickerDialogDash");
            }
        });

        RelativeLayout layBG = (RelativeLayout) colorSettingView.findViewById(R.id.color_bg);
        layBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogDash colordashfragment = ColorPickerDialogDash
                        .newInstance(R.string.color_picker_bg_title, mColorChoices, mSelectedBgColor, 5);
                // Implement listener to get selected color value
                colordashfragment.setOnColorSelectedListener(new ColorPickerDialogDash.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mSelectedBgColor = color;
                        mBgView.setBackgroundColor(mSelectedBgColor);
                        mSetting.setBGColor(mSelectedBgColor);
                    }
                });
                colordashfragment.show(getFragmentManager(), "ColorPickerDialogDash");
            }
        });

        return colorSettingView;
    }
}
