package com.favex.Fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    private Socket mSocket;

    private RecyclerView mChatsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private NotificationManager mNotificationManager;

    boolean isInFront;

    private Cursor users;
    private databaseHelper dbh;


    public void test(){
        for(int i = 0; i< 10; i++) {
            dbh.insertUser("test", "test");
        }
    }

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

        ChatApplication app = (ChatApplication) getActivity().getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("stored messages", onStoredMessages);
        mSocket.connect();

        JSONObject NUObj = new JSONObject();
        try {
            NUObj.put("facebookId", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("new user", NUObj);
    }

    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Socket", "onConnect");
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Socket", "onDisconnect");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("Socket", "onConnectError");
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("Socket", "onNewMessage");

            dbh = new databaseHelper(getActivity());

            JSONObject data = (JSONObject) args[0];
            try {
                Log.e("message", data.getString("message"));

                String message = data.getString("message");
                String sender = data.getString("sender");
                String facebookId = data.getString("facebookId");
                String time = data.getString("time");
                String date = data.getString("date");

                dbh.insertMessage(message, sender, facebookId, time, date);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(!isInFront) {
                pushNotification("New Message", "User");
            }
        }
    };

    private Emitter.Listener onStoredMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("Socket", "onStoredMessages");

            dbh = new databaseHelper(getActivity());

            JSONArray data = (JSONArray) args[0];
            if(data != null)
                try {
                    for(int i = 0; i < data.length(); i++) {

                        Log.e("message", data.getJSONObject(i).getString("message"));

                        String message = data.getJSONObject(i).getString("message");
                        String sender = data.getJSONObject(i).getString("sender");
                        String facebookId = data.getJSONObject(i).getString("facebookId");
                        String time = data.getJSONObject(i).getString("time");
                        String date = data.getJSONObject(i).getString("date");

                        dbh.insertMessage(message, sender, facebookId, time, date);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            JSONObject RObj = new JSONObject();
            try {
                RObj.put("facebookId", "test");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("received", RObj);

            if(data.length() != 0 && !isInFront)
            {

                pushNotification("New Message", "User");
            }
        }
    };

    private void sendMessage(){
        if(!mSocket.connected()){

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(cal.getTime());

            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            String date = String.valueOf(day) + String.valueOf(month) + String.valueOf(year);

            return;
        }


    }

    private void pushNotification(String contentTitle, String contentText){

        long pattern[] = {0, 100, 300, 300};
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setVibrate(pattern);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), MainActivity.class);
        resultIntent.putExtra("ARG_PAGE", 1);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
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
