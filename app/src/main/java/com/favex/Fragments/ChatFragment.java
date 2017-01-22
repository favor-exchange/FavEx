package com.favex.Fragments;


import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    SharedPreferences prefs;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);

        dbh = new databaseHelper(getActivity());

        mChatsRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        mChatsRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mChatsRecyclerView.setLayoutManager(mLayoutManager);

        users = dbh.getAllChats(prefs.getString("facebookId", "default"));
        mAdapter = new ChatRecyclerAdapter(users, getActivity());
        mChatsRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotificationManager =  (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        isStoragePermissionGranted();
        prefs = getActivity().getSharedPreferences("com.favex", Context.MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mNotificationManager.cancelAll();

        mReceiver = new Receiver();

        IntentFilter filter = new IntentFilter("com.favex.NEW_MESSAGE");
        getActivity().registerReceiver(mReceiver, filter);

        users = dbh.getAllChats(prefs.getString("facebookId", "default"));
        mAdapter = new ChatRecyclerAdapter(users, getActivity());

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
            users = dbh.getAllChats(prefs.getString("facebookId", "default"));

            mAdapter = new ChatRecyclerAdapter(users, getActivity());

            mChatsRecyclerView.swapAdapter(mAdapter, false  );
            mChatsRecyclerView.getLayoutManager();
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("write","Permission is granted");
                return true;
            } else {

                Log.e("write","Permission is revoked");
                //Toast.makeText(getActivity(), "Can't load profile pictures without storage permission", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.e("write","Permission is granted");
            return true;
        }
    }

}
