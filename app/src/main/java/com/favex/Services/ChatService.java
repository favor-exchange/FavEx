package com.favex.Services;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.favex.Activities.MainActivity;
import com.favex.Applications.ChatApplication;
import com.favex.Helpers.databaseHelper;
import com.favex.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatService extends Service {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     *
     */

    private Socket mSocket;
    private databaseHelper dbh;
    private NotificationManager mNotificationManager;
    private String myFacebookId;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("new message", onNewMessage);
            mSocket.on("stored messages", onStoredMessages);
            mSocket.connect();

            JSONObject NUObj = new JSONObject();

            try {
                NUObj.put("facebookId", myFacebookId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("new user", NUObj);

            mNotificationManager = (NotificationManager) ChatService.this.getSystemService(Context.NOTIFICATION_SERVICE);

            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("FavEx Chat Thread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ChatService", "Starting");

        ChatApplication app = (ChatApplication) this.getApplication();
        mSocket = app.getSocket();

        prefs = getSharedPreferences("com.favex", Context.MODE_PRIVATE);
        myFacebookId = prefs.getString("facebookId", "default");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ChatService", "Stopping");

        /*mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("stored messages", onStoredMessages);*/
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SocketService", "onConnect");
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SocketService", "onDisconnect");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("SocketService", "onConnectError");
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("SocketService", "onNewMessage");

            dbh = new databaseHelper(ChatService.this);

            String sender;
            String message;

            JSONObject data = (JSONObject) args[0];
            try {
                Log.e("message", data.getString("message"));

                message = data.getString("message");
                sender = data.getString("sender");
                String facebookIdReceived = data.getString("facebookId");
                String time = data.getString("time");
                String date = data.getString("date");

                dbh.insertMessage(message, sender, facebookIdReceived, time, date, myFacebookId);
                dbh.insertUser(sender, facebookIdReceived, date, myFacebookId);

                Intent in = new Intent();
                in.setAction("com.favex.NEW_MESSAGE");
                sendBroadcast(in);

                pushNotification(sender, message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onStoredMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("SocketService", "onStoredMessages");

            dbh = new databaseHelper(ChatService.this);

            JSONArray data = (JSONArray) args[0];
            if(data != null)
                try {
                    for(int i = 0; i < data.length(); i++) {

                        Log.e("message", data.getJSONObject(i).getString("message"));

                        String message = data.getJSONObject(i).getString("message");
                        String sender = data.getJSONObject(i).getString("sender");
                        String facebookIdReceived = data.getJSONObject(i).getString("facebookId");
                        String time = data.getJSONObject(i).getString("time");
                        String date = data.getJSONObject(i).getString("date");

                        dbh.insertMessage(message, sender, facebookIdReceived, time, date, myFacebookId);
                        dbh.insertUser(sender, facebookIdReceived, date, myFacebookId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            JSONObject RObj = new JSONObject();
            try {
                RObj.put("facebookId", myFacebookId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("received", RObj);

            if(data.length() != 0)
            {
                Intent in = new Intent();
                in.setAction("com.favex.NEW_MESSAGE");
                sendBroadcast(in);
                pushNotification("Chat", "New Messages");
            }
        }
    };

    private void pushNotification(String contentTitle, String contentText){

        long pattern[] = {0, 100, 300, 300};
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ChatService.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setVibrate(pattern);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(ChatService.this, MainActivity.class);
        resultIntent.putExtra("ARG_PAGE", 1);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ChatService.this);
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
                (NotificationManager) ChatService.this.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }
}