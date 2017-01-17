package com.favex.Fragments;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.favex.Adapters.ChatRecyclerAdapter;
import com.favex.Helpers.databaseHelper;
import com.favex.R;

import java.util.List;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class ChatFragment extends Fragment
{

    private RecyclerView mChatsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    boolean isInFront;

    private Cursor users;
    private databaseHelper dbh;
    private NotificationManager mNotificationManager;
    private Receiver mReceiver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);

        dbh = new databaseHelper(getActivity());

        mChatsRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        mChatsRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mChatsRecyclerView.setLayoutManager(mLayoutManager);

        users = dbh.getAllChats();
        mAdapter = new ChatRecyclerAdapter(users);
        mChatsRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotificationManager =  (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("onResume", "called");

        mNotificationManager.cancelAll();

        mReceiver = new Receiver();

        IntentFilter filter = new IntentFilter("com.favex.NEW_MESSAGE");
        getActivity().registerReceiver(mReceiver, filter);

        users = dbh.getAllChats();
        mAdapter = new ChatRecyclerAdapter(users);

        mChatsRecyclerView.setAdapter(mAdapter);
        mChatsRecyclerView.getLayoutManager();
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;

        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class Receiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            users = dbh.getAllChats();

            mAdapter = new ChatRecyclerAdapter(users);

            mChatsRecyclerView.swapAdapter(mAdapter, false  );
            mChatsRecyclerView.getLayoutManager();
        }
    }
}
