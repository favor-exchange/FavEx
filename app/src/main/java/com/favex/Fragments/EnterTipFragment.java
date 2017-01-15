package com.favex.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.favex.R;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class EnterTipFragment extends Fragment {
    private EditText mTip;
    private Button mConfirmBtn;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_favor_fragment, container, false);
        mTip= (EditText)view.findViewById(R.id.tip);
        mConfirmBtn= (Button)view.findViewById(R.id.confirmBtn);
        mTip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if(charSequence.toString().trim().length()<1)
                    mConfirmBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mTip.getText().toString().trim().length()>0)
                    mConfirmBtn.setEnabled(true);
            }
        });
        return view;
    }
}
