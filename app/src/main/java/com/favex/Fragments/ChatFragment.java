package com.favex.Fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.favex.Activities.MainActivity;
import com.favex.Adapters.ChatRecyclerAdapter;
import com.favex.Applications.ChatApplication;
import com.favex.Helpers.databaseHelper;
import com.favex.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class ChatFragment extends Fragment
{

    private RecyclerView mChatsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    boolean isInFront;

    private Cursor users;
    private databaseHelper dbh;

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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
