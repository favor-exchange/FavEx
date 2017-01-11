package com.favex.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.favex.Adapters.MessagesRecyclerAdapter;
import com.favex.Helpers.databaseHelper;
import com.favex.R;

public class MessagesActivity extends AppCompatActivity {


    private databaseHelper dbh;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager llm;
    private Cursor messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Intent in = getIntent();
        String facebookId = in.getStringExtra("facebookId");

        dbh = new databaseHelper(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        messages = dbh.getMessagesByFacebookId(facebookId);
        mAdapter = new MessagesRecyclerAdapter(messages);
        mRecyclerView.setAdapter(mAdapter);
    }
}
