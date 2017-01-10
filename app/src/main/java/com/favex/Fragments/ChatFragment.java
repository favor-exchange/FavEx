package com.favex.Fragments;

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
import android.widget.EditText;
import android.widget.Toast;

import com.favex.Adapters.ChatRecyclerAdapter;
import com.favex.Applications.ChatApplication;
import com.favex.Helpers.chatDatabaseHelper;
import com.favex.R;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class ChatFragment extends Fragment
{

    private Socket mSocket;
    private RecyclerView mMessagesRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Cursor users;
    private chatDatabaseHelper db;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.chat_fragment, container, false);

        db = new chatDatabaseHelper(getActivity());

        mMessagesRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        mMessagesRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mMessagesRecyclerView.setLayoutManager(mLayoutManager);

        users = db.getAllChats();
        mAdapter = new ChatRecyclerAdapter(users);
        mMessagesRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("stored messages", onStoredMessages);
        mSocket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("onConnect", "onConnect");
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("onDisconnect", "onDisconnect");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("onConnectError", "onConnectError");
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("onNewMessage", "onNewMessage");

            JSONObject data = (JSONObject) args[0];
            Log.e("data length", String.valueOf(data.length()));
        }
    };

    private Emitter.Listener onStoredMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("onStoredMessages", "onStoredMessages");

            JSONObject data = (JSONObject) args[0];
            Log.e("data length", String.valueOf(data.length()));
        }
    };

    private void sendMessage(){
        if(!mSocket.connected()){
            return;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("stored messages", onStoredMessages);

    }
}
