package com.te.UI;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class NumberPickerPreference extends DialogPreference {

    private NumberPicker mNumPicker;
    private String mValue = "";
    private int mNMin = 0;
    private int mNMax = 0;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private NumberPicker generateNumberPicker() {
        mNumPicker = new NumberPicker(getContext());
        mNumPicker.setMinValue(mNMin);
        mNumPicker.setMaxValue(mNMax);
        mNumPicker.setValue(Integer.valueOf(mValue));
        return mNumPicker;
    }

    public void setValue(String val, int nMin, int nMax) {
        mValue = val;
        mNMin = nMin;
        mNMax = nMax;
        if(mNumPicker != null) {
            mNumPicker.setValue(Integer.valueOf(mValue));
        }
    }

    public String getValue() {
        return mValue;
    }

    @Override
    protected View onCreateDialogView() {
        return generateNumberPicker();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current
        // setting value
        myState.mStrValue = mValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        // Set this Preference's widget to reflect the restored state
        mValue = myState.mStrValue;
        super.onRestoreInstanceState(myState.getSuperState());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            mValue = String.valueOf(mNumPicker.getValue());
            persistBoolean(!getPersistedBoolean(true));//here to trigger SharedPreferences.OnSharedPreferenceChangeListener
        }
    }

    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's mStrValue
        // Change this data type to match the type saved by your Preference
        public String mStrValue;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's mStrValue
            mStrValue = source.readString();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's mStrValue
            dest.writeString(mStrValue);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}