package com.favex.Adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favex.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shayan on 09-Jan-17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    private Cursor users;
    private LayoutInflater layoutInflater;

    public ChatRecyclerAdapter(Cursor res){
        users = res;
    }

    public void reAssignCursor(Cursor res){
        users = res;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.chat_item, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder viewHolder, int position) {
        users.moveToPosition(position);

        viewHolder.mName.setText(users.getString(1));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDate;
        private CircleImageView mProfilePicture;

        public ChatViewHolder(View itemView) {

            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_textView);
            mDate = (TextView) itemView.findViewById(R.id.date_textView);
            mProfilePicture = (CircleImageView) itemView.findViewById(R.id.profile_image);

        }
    }
}
