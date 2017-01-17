package com.favex.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.favex.Adapters.MessagesRecyclerAdapter;
import com.favex.Applications.ChatApplication;
import com.favex.Helpers.databaseHelper;
import com.favex.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MessagesActivity extends AppCompatActivity{


    private databaseHelper dbh;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager llm;
    private Cursor messages;
    private EditText mEditText;
    private FloatingActionButton mSendButton;
    private Socket mSocket;

    private SharedPreferences prefs;

    private String senderFacebookId;
    private String senderName;
    private String myFacebookId;
    private Receiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent in = getIntent();
        senderFacebookId = in.getStringExtra("facebookId");
        senderName = in.getStringExtra("sender");

        getSupportActionBar().setTitle(senderName);

        prefs = this.getSharedPreferences(
                "com.favex", Context.MODE_PRIVATE);
        myFacebookId = prefs.getString("facebookId", "default");

        mReceiver = new Receiver();

        IntentFilter filter = new IntentFilter("com.favex.NEW_MESSAGE");
        registerReceiver(mReceiver, filter);

        mEditText = (EditText) findViewById(R.id.enter_message);
        mSendButton = (FloatingActionButton) findViewById(R.id.send_button);

        dbh = new databaseHelper(this);

        dbh.updateRead(senderFacebookId);

        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        messages = dbh.getMessagesByFacebookId(senderFacebookId);
        mAdapter = new MessagesRecyclerAdapter(messages, myFacebookId);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditText.getText().toString();
                if(message.compareTo("") == 0){
                    return;
                }
                mEditText.setText("");

                ChatApplication app = (ChatApplication) getApplication();
                mSocket = app.getSocket();
                mSocket.connect();

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String time = sdf.format(cal.getTime());

                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH) + 1;
                int year = cal.get(Calendar.YEAR);
                String date = String.valueOf(day) + String.valueOf(month) + String.valueOf(year);

                String sender = "Shayan Imran"; //change to useful value later

                JSONObject mJSON = new JSONObject();

                try {
                    mJSON.put("message",message);
                    mJSON.put("sender", sender); //my name
                    mJSON.put("recipient", senderFacebookId); //fb id of other person
                    mJSON.put("facebookId", myFacebookId); //my fb id
                    mJSON.put("time", time);
                    mJSON.put("date", date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //in db stores other persons fb id but sends own fb id in json object
                mSocket.emit("new message", mJSON);

                //sender in local db should be my fb id to display messages correctly
                dbh.insertMessage(message, myFacebookId, senderFacebookId, time, date);
                messages = dbh.getMessagesByFacebookId(senderFacebookId);

                mAdapter = new MessagesRecyclerAdapter(messages, myFacebookId);

                mRecyclerView.swapAdapter(mAdapter, true);
                mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount()-1);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            messages = dbh.getMessagesByFacebookId(senderFacebookId);

            mAdapter = new MessagesRecyclerAdapter(messages, myFacebookId);

            mRecyclerView.swapAdapter(mAdapter, true);
            mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount()-1);
            dbh.updateRead(senderFacebookId);
        }
    }
}
