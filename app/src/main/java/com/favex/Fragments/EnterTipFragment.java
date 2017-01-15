package com.favex.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Activities.FavorFormActivity;
import com.favex.R;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class EnterTipFragment extends Fragment {
    private TextView mTotalCost;
    private EditText mTip;
    private Button mConfirmBtn;

    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            mTotalCost.setText(String.valueOf(((FavorFormActivity)getActivity()).getTotalCost()));
            Log.i("TIP FRAGMENT",String.valueOf(((FavorFormActivity)getActivity()).getTotalCost()));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_tip, container, false);
        mTotalCost= (TextView)view.findViewById(R.id.totalCost);
        mTotalCost.setText(String.valueOf(((FavorFormActivity)getActivity()).getTotalCost()));
        Log.i("TIP FRAGMENT",String.valueOf(((FavorFormActivity)getActivity()).getTotalCost()));
        mTip= (EditText)view.findViewById(R.id.tip);
        mConfirmBtn= (Button)view.findViewById(R.id.confirmBtn);
        mTip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if(charSequence.toString().trim().length()<1)
                    mConfirmBtn.setEnabled(false);
                else if(!mConfirmBtn.isEnabled()&&charSequence.toString().trim().length()>0)
                    mConfirmBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavorFormActivity)getActivity()).setTip(Float.parseFloat(mTip.getText().toString().trim()));
            }
        });
        return view;
    }
}
