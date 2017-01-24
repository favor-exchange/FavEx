package com.favex.Fragments;

import android.content.Intent;
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
import com.favex.Activities.MainActivity;
import com.favex.Interfaces.postFavorInterface;
import com.favex.R;

/**
 * Created by Tavish on 19-Jan-17.
 */

public class EnterTitleFragment extends Fragment {
    private EditText mTitle;
    private Button mConfirmBtn;
    private Button mCancelBtn;
    private postFavorInterface postInterface;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_title_fragment, container, false);
        postInterface= (postFavorInterface)getActivity();
        mConfirmBtn= (Button)view.findViewById(R.id.confirmBtn);
        mCancelBtn= (Button)view.findViewById(R.id.cancelBtn);
        mTitle= (EditText)view.findViewById(R.id.title);
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if(!mConfirmBtn.isEnabled()&& charSequence.toString().trim().length()>0)
                    mConfirmBtn.setEnabled(true);
                else if(charSequence.toString().trim().length()<=0)
                    mConfirmBtn.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(i);
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavorFormActivity)getActivity()).setTitle(mTitle.getText().toString().trim());
                postInterface.postFavorToServer();
                Intent i = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(i);
            }
        });
        return view;
    }
}
