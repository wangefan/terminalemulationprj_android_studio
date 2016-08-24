package com.te.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cipherlab.terminalemulation.R;

public class KeyMappingSetting extends BaseFragment {
	public KeyMappingSetting() {
        // Required empty public constructor
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.keymapping_setting, container, false);
    }

    @Override
    void commitUpdate() {
        // TODO Auto-generated method stub
        
    }
}
