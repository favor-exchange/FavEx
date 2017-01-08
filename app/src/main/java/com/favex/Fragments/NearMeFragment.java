package com.favex.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.favex.Adapters.FavorRecyclerAdapter;
import com.favex.R;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class NearMeFragment extends Fragment
{
    private RecyclerView mFavorRecycler ;
    protected FavorRecyclerAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.near_me_fragment, container, false);
    }
}
