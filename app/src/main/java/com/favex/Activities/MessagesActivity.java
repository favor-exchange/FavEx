package com.favex.Activities;

import android.content.Intent;
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


public class MessagesActivity extends AppCompatActivity{


    private databaseHelper dbh;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager llm;
    private Cursor messages;
    private EditText mEditText;
    private FloatingActionButton mSendButton;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent in = getIntent();
        final String facebookId = in.getStringExtra("facebookId");
        final String mSender = in.getStringExtra("sender");

        getSupportActionBar().setTitle(facebookId);

        mEditText = (EditText) findViewById(R.id.enter_message);
        mSendButton = (FloatingActionButton) findViewById(R.id.send_button);

        dbh = new databaseHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        messages = dbh.getMessagesByFacebookId(facebookId);
        mAdapter = new MessagesRecyclerAdapter(messages);
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

                String recipient = mSender;

                String sender = "me"; //change to useful value later

                JSONObject mJSON = new JSONObject();
                try {
                    mJSON.put("message",message);
                    mJSON.put("sender", sender);
                    mJSON.put("recipient", recipient);
                    mJSON.put("facebookId", "MY FBID");
                    mJSON.put("time", time);
                    mJSON.put("date", date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //in db stores other persons fb id but sends own fb id in json object
                mSocket.emit("new message", mJSON);
                //sender in local db should be my fb id to display messages correctly
                dbh.insertMessage(message, "MyFbId", facebookId, time, date);
                messages = dbh.getMessagesByFacebookId(facebookId);

                mAdapter = new MessagesRecyclerAdapter(messages);

                mRecyclerView.swapAdapter(mAdapter, true);
                mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount()-1);
            }
        });

    }
}
