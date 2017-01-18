package com.favex.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.favex.R;

/**
 * Created by Tavish on 19-Jan-17.
 */

public class EnterTitleFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_title_fragment, container, false);
        return view;
    }
}
