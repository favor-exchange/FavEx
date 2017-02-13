package com.favex.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favex.Activities.MessagesActivity;
import com.favex.R;
import com.favex.RestManager.OkHttpSingleton;
import com.squareup.picasso.Picasso;

import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Shayan on 09-Jan-17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    private Cursor users;
    private Activity activity;

    public ChatRecyclerAdapter(Cursor res, Activity act) {
        users = res;
        activity = act;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder viewHolder, int position) {
        users.moveToPosition(users.getCount() - position - 1);

        viewHolder.mName.setText(users.getString(1));
        if (users.getInt(4) == 0) {
            viewHolder.mName.setTypeface(null, Typeface.BOLD);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(users.getString(3));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.mDate_Time.setText(users.getString(3));

        viewHolder.mFacebookId = users.getString(2);

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/profile_pictures/"
                + "PP_"
                + viewHolder.mFacebookId
                +".jpg" );

        if(!mediaStorageDir.exists()){
            downloadProfileImage("http://graph.facebook.com/" + viewHolder.mFacebookId + "/picture?type=large", viewHolder.mFacebookId);
        }

        //Picasso.with(viewHolder.mDate.getContext()).load(mediaStorageDir.getAbsolutePath()).into(viewHolder.mProfilePicture);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mediaStorageDir.getAbsolutePath(), options);
        viewHolder.mProfilePicture.setImageBitmap(bitmap);
        downloadProfileImage("http://graph.facebook.com/" + viewHolder.mFacebookId + "/picture?type=large", viewHolder.mFacebookId);

        //10208252840303021 - tavish
        //1856206614405397
    }

    @Override
    public int getItemCount() {
        return users.getCount();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDate_Time;
        private CircleImageView mProfilePicture;
        private String mFacebookId;

        public ChatViewHolder(View itemView) {

            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_textView);
            mDate_Time = (TextView) itemView.findViewById(R.id.date_time_textView);
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

    public void downloadProfileImage(String url, final String facebookId) {

        OkHttpSingleton oks = OkHttpSingleton.getOkHttpInstance();

        Request req = new Request.Builder()
                .url(url)
                .build();

        oks.getOkHttpClient().newCall(req)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("ChatRecyclerAdapter", "Failed to get profile picture(s)");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        final byte[] data = response.body().bytes();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                storeImage(BitmapFactory.decodeByteArray(data, 0, data.length), facebookId);
                            }
                        });
                    }
                });

    }

    private void storeImage(Bitmap image, String facebookId) {
        File pictureFile = getOutputMediaFile(facebookId);
        if (pictureFile == null) {
            Log.e("ChatRecyclerAdapter", "Error creating media file, check storage permissions");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            if(image != null) {
                image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(String facebookId){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                "/Android/data/"
                + activity.getApplicationContext().getPackageName()
                + "/profile_pictures");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = "PP_"+ facebookId +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }



}
