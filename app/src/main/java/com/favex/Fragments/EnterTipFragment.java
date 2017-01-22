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
import com.favex.Interfaces.postFavorInterface;
import com.favex.R;

/**
 * Created by Tavish on 15-Jan-17.
 */

public class EnterTipFragment extends Fragment {
    private EditText mTip;
    private Button mNextBtn;
    private TextView mMinCost;
    private TextView mMaxCost;
    @Override
    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            mMinCost.setText(String.valueOf(((FavorFormActivity)getActivity()).getPriceRange()[0]));
            mMaxCost.setText(String.valueOf(((FavorFormActivity)getActivity()).getPriceRange()[1]));
            if(Integer.parseInt(mMaxCost.getText().toString())==500)
                mMaxCost.setText(mMaxCost.getText()+"+");
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_tip, container, false);
        mTip= (EditText)view.findViewById(R.id.tip);
        mMinCost= (TextView)view.findViewById(R.id.minCost);
        mMaxCost= (TextView)view.findViewById(R.id.maxCost);
        mNextBtn= (Button)view.findViewById(R.id.nextBtn);
        mMinCost.setText(String.valueOf(((FavorFormActivity)getActivity()).getPriceRange()[0]));
        mMaxCost.setText(String.valueOf(((FavorFormActivity)getActivity()).getPriceRange()[1]));
        if(Integer.parseInt(mMaxCost.getText().toString())==500)
            mMaxCost.setText(mMaxCost.getText()+"+");
        mTip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if(!mNextBtn.isEnabled()&&charSequence.toString().trim().length()>0)
                    mNextBtn.setEnabled(true);
                else if(charSequence.toString().trim().length()<=0)
                    mNextBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavorFormActivity)getActivity()).setTip(Double.parseDouble(mTip.getText().toString().trim()));
                ((FavorFormActivity)getActivity()).getVerticalViewPager().setCurrentItem(4);
            }
        });
        return view;
    }
}
