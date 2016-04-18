package com.te.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;
import java.util.List; 

import com.example.terminalemulation.R;

public class SessionSettings extends AppCompatActivity {
    
    private final List<BaseFragment> mFragmentList = new ArrayList<BaseFragment>();    
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_settings);
 
        Toolbar toolbar = (Toolbar) findViewById(R.id.sessinsettingtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageButton btnCancel = (ImageButton) toolbar.findViewById(R.id.idcancel);
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mFragmentList.add(new SessionSettingsFrg());
                            
        ImageButton btnOK = (ImageButton) toolbar.findViewById(R.id.idok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if(mFragmentList != null) {
                    for(int idxFrag = 0; idxFrag < mFragmentList.size(); ++idxFrag) {
                        BaseFragment page = (BaseFragment) mFragmentList.get(idxFrag);
                        if(page == null)
                            continue;
                        page.commitUpdate();
                    }
                }
                finish();
            }
        });
 
     // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SessionSettingsFrg())
                .commit();
    }
}
