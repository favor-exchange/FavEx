package com.favex.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Adapters.FavorRecyclerAdapter;
import com.favex.Adapters.GalleryAdapter;
import com.favex.Adapters.OrderRecyclerAdapter;
import com.favex.Helpers.databaseHelper;
import com.favex.POJOs.OrderItem;
import com.favex.R;
import com.favex.RestManager.ApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * Created by Tavish on 21-Jan-17.
 */

public class FavorDetails extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private ArrayList<OrderItem> orderItems;
    private TextView mFavorTitle;
    private TextView mFavorAddress;
    private TextView mRecipientAddress;
    private TextView mDistance;
    private TextView mMinPrice;
    private TextView mMaxPrice;
    private TextView mTip;
    private String favorId;
    private ViewPager mViewPager;
    private TextView mImageLabel;
    private GalleryAdapter adapter;
    private ArrayList<Bitmap> bitmaps= new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private FloatingActionButton mChatFAB;
    private FloatingActionButton mDoFAB;
    private FloatingActionButton mDoneFAB;
    SharedPreferences prefs;
    databaseHelper dbh;

    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback= new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            bitmaps.add(placePhotoResult.getBitmap());
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favor_details_activity);
        mFavorTitle= (TextView)findViewById(R.id.title);
        mFavorAddress= (TextView)findViewById(R.id.favorAddress);
        mRecipientAddress= (TextView)findViewById(R.id.recipientAddress);
        mDistance= (TextView)findViewById(R.id.distance);
        mMinPrice= (TextView)findViewById(R.id.minPrice);
        mMaxPrice= (TextView)findViewById(R.id.maxPrice);
        mImageLabel= (TextView)findViewById(R.id.imageLabel);
        LinearLayout mOrderList= (LinearLayout)findViewById(R.id.orderList);
        mTip= (TextView)findViewById(R.id.tip);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mChatFAB = (FloatingActionButton) findViewById(R.id.chatBtn);
        mDoFAB = (FloatingActionButton) findViewById(R.id.doBtn);
        mDoneFAB = (FloatingActionButton) findViewById(R.id.doneBtn);
        prefs = getSharedPreferences("com.favex", Context.MODE_PRIVATE);
        dbh = new databaseHelper(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        adapter = new GalleryAdapter(this, bitmaps);

        mViewPager.setAdapter(adapter);
        final String favorJsonString= getIntent().getStringExtra("favorJsonString");
        try
        {
            final JSONObject favorJson = new JSONObject(favorJsonString);
            mFavorTitle.setText(favorJson.getString("title"));
            if(favorJson.has("distance")) {
                mDistance.setText(favorJson.getString("distance") + " m");
            }
            else{
                mDistance.setVisibility(View.INVISIBLE);
                findViewById(R.id.distanceLabel).setVisibility(View.INVISIBLE);
            }
            mFavorAddress.setText(favorJson.getString("locationFavorAddress"));
            mRecipientAddress.setText(favorJson.getString("locationRecipientAddress"));
            mMinPrice.setText(String.valueOf(favorJson.getJSONObject("priceRange").getInt("min")));
            mMaxPrice.setText(String.valueOf(favorJson.getJSONObject("priceRange").getInt("max")));
            mTip.setText(String.valueOf(favorJson.getDouble("tip")));
            mImageLabel.setText(favorJson.getString("locationFavorName"));
            favorId = favorJson.getString("_id");


            placePhotosAsync(favorJson.getString("locationFavorId"));
            placePhotosAsync(favorJson.getString("locationRecipientId"));

            for (int i = 0; i < favorJson.getJSONArray("orderItems").length(); i++) {
                View item = getLayoutInflater().inflate(R.layout.order_list_item,mOrderList,false);
                TextView mItemName= (TextView) item.findViewById(R.id.itemName);
                mItemName.setText(favorJson.getJSONArray("orderItems").getJSONObject(i).getString("itemName"));
                TextView mQuantity= (TextView) item.findViewById(R.id.quantity);
                mQuantity.setText(favorJson.getJSONArray("orderItems").getJSONObject(i).getString("quantity"));
                mOrderList.addView(item);
            }
            final String myFacebookId =  prefs.getString("facebookId", "default");

            if(myFacebookId.compareTo(favorJson.getString("recipientId")) == 0){
                mChatFAB.setVisibility(View.INVISIBLE);
                mDoFAB.setVisibility(View.INVISIBLE);
                mDoneFAB.setVisibility(View.VISIBLE);

                mDoneFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!favorJson.isNull("doerId")) {

                            try {

                                if(favorJson.getBoolean("isComplete")){
                                    new AlertDialog.Builder(FavorDetails.this)
                                            .setTitle("Done")
                                            .setMessage("This favor has been completed!")
                                            .show();
                                }
                                else {

                                    new AlertDialog.Builder(FavorDetails.this)
                                            .setTitle("Favor Complete")
                                            .setMessage("Has this favor been completed?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    JSONObject favorObj = new JSONObject();
                                                    JSONObject details = new JSONObject();

                                                    try {
                                                        details.put("_id", favorId);
                                                        details.put("isComplete", true);
                                                        favorObj.put("favor", details);
                                                        ApiClient.updateFavorStatus(favorObj).enqueue(new Callback() {
                                                            @Override
                                                            public void onFailure(Call call, IOException e) {
                                                                Log.e("UpdateFavorStatus", "failed");
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(FavorDetails.this, "Failed to update favor", Toast.LENGTH_LONG);
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onResponse(Call call, Response response) throws IOException {
                                                                Log.e("UpdateFavorStatus", "success");
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(FavorDetails.this, "Successfully updated favor!", Toast.LENGTH_LONG);
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(FavorDetails.this, "Failed to update favor", Toast.LENGTH_LONG);
                                                    }
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            })
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            new AlertDialog.Builder(FavorDetails.this)
                                    .setTitle("Favor Not Accepted")
                                    .setMessage("This favor has not been accepted yet, cancel it?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            JSONObject favorObj = new JSONObject();
                                            JSONObject details = new JSONObject();

                                            try {
                                                details.put("_id", favorId);
                                                favorObj.put("favor", details);
                                                ApiClient.deleteFavor(favorObj).enqueue(new Callback() {
                                                    @Override
                                                    public void onFailure(Call call, IOException e) {
                                                        Log.e("DeleteFavor", "failed");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(FavorDetails.this, "Failed to cancel favor", Toast.LENGTH_LONG);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onResponse(Call call, Response response) throws IOException {
                                                        Log.e("DeleteFavor", "success");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(FavorDetails.this, "Successfully cancelled favor!", Toast.LENGTH_LONG);
                                                            }
                                                        });
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(FavorDetails.this, "Failed to cancel favor", Toast.LENGTH_LONG);
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
            }
            else {
                mDoneFAB.setVisibility(View.INVISIBLE);
                mChatFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(FavorDetails.this, MessagesActivity.class);
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String time = sdf.format(cal.getTime());

                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int month = cal.get(Calendar.MONTH) + 1;
                        int year = cal.get(Calendar.YEAR);
                        String date = String.valueOf(day)+ "/" + String.valueOf(month) + "/" + String.valueOf(year);
                        try {
                            in.putExtra("facebookId", favorJson.getString("recipientId"));

                            String mSender = dbh.getUserName(myFacebookId, favorJson.getString("recipientId"));
                            in.putExtra("sender", mSender);
                            dbh.insertUser(mSender, favorJson.getString("recipientId"), date, prefs.getString("facebookId", "default"),time);
                            startActivity(in);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                mDoFAB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(favorJson.isNull("doerId")) {
                            new AlertDialog.Builder(FavorDetails.this)
                                    .setTitle("Accept Favor")
                                    .setMessage("Are you sure you want to accept this favor?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            JSONObject favorObj = new JSONObject();
                                            JSONObject details = new JSONObject();

                                            try {
                                                details.put("_id", favorId);
                                                details.put("doerId", myFacebookId);
                                                favorObj.put("favor", details);
                                                ApiClient.updateDoer(favorObj).enqueue(new Callback() {
                                                    @Override
                                                    public void onFailure(Call call, IOException e) {
                                                        Log.e("UpdateDoer", "failed");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(FavorDetails.this, "Failed to accept favor", Toast.LENGTH_LONG);
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onResponse(Call call, Response response) throws IOException {
                                                        Log.e("UpdateDoer", "success");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(FavorDetails.this, "Successfully accepted favor!", Toast.LENGTH_LONG);
                                                            }
                                                        });
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(FavorDetails.this, "Failed to accept favor", Toast.LENGTH_LONG);
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .show();
                        }
                        else {
                            new AlertDialog.Builder(FavorDetails.this)
                                    .setTitle("Can't Accept this Favor")
                                    .setMessage("Sorry, somebody else is already doing this favor!")
                                    .show();
                        }
                    }
                });
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    try
                    {
                        switch (position)
                        {
                            case 0: mImageLabel.setText(favorJson.getString("locationFavorName"));
                                    break;
                            case 1: mImageLabel.setText(favorJson.getString("locationRecipientName"));
                                    break;
                            default: mImageLabel.setText("No place name available");
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    private void placePhotosAsync(String placeId) {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(@NonNull PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            photoMetadataBuffer.get(0)
                                    .getPhoto(mGoogleApiClient)
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }
                        photoMetadataBuffer.release();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"FATAL ERROR: GOOGLE PLAY SERVICES CONNECTION FAILED!",Toast.LENGTH_SHORT).show();
    }
}
