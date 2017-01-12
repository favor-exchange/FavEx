package com.favex.Adapters;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favex.Activities.MessagesActivity;
import com.favex.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shayan on 09-Jan-17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    private Cursor users;

    public ChatRecyclerAdapter(Cursor res){
        users = res;
    }

    public void reassignCursor(Cursor res){
        users = res;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder viewHolder, int position) {
        users.moveToPosition(position);

        viewHolder.mName.setText(users.getString(1));
        viewHolder.mDate.setText("temp");
        viewHolder.mProfilePicture.setImageResource(R.mipmap.ic_launcher);
        viewHolder.mFacebookId = users.getString(2);
    }

    @Override
    public int getItemCount() {
        return users.getCount();
    }




    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDate;
        private CircleImageView mProfilePicture;
        private String mFacebookId;

        public ChatViewHolder(View itemView) {

            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_textView);
            mDate = (TextView) itemView.findViewById(R.id.date_textView);
            mProfilePicture = (CircleImageView) itemView.findViewById(R.id.profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String facebookId = mFacebookId;
                    String sender = mName.getText().toString();
                    Intent in = new Intent(v.getContext(), MessagesActivity.class);
                    in.putExtra("facebookId", facebookId);
                    in.putExtra("sender", sender);
                    v.getContext().startActivity(in);
                }
            });
        }
    }
}
