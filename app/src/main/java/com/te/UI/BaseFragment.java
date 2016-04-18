package com.te.UI;

import android.preference.PreferenceFragment;

public abstract class BaseFragment extends PreferenceFragment {
    abstract void commitUpdate();

}
