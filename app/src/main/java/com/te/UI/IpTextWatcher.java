package com.te.UI;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class IpTextWatcher implements TextWatcher {
    private EditText mEdit[] = new EditText[4];
    private EditText mDestEdit = null;

    public IpTextWatcher(EditText destEdit, EditText [] editTexts) {
        mDestEdit = destEdit;
        mEdit[0] = editTexts[0];
        mEdit[1] = editTexts[1];
        mEdit[2] = editTexts[2];
        mEdit[3] = editTexts[3];
    }
    
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() == 3)  
        {  
            if(Integer.valueOf(mDestEdit.getText().toString()) > 255) {
                mDestEdit.setText("255");
            }
            if(mDestEdit == mEdit[0])  
            {  
                mEdit[1].requestFocus();  
            }  
            else if(mDestEdit == mEdit[1])  
            {  
                mEdit[2].requestFocus();  
            }  
            else if(mDestEdit == mEdit[2])  
            {  
                mEdit[3].requestFocus();  
            }  
        }  
        else if(s.length() == 0)  
        {  
            if(mDestEdit == mEdit[3])  
            {  
                mEdit[2].requestFocus();  
            }  
            else if(mDestEdit == mEdit[2])  
            {  
                mEdit[1].requestFocus();  
            }  
            else if(mDestEdit == mEdit[1])  
            {  
                mEdit[0].requestFocus();  
            }  
        }  
    }
}
