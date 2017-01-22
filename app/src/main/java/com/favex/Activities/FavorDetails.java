package com.favex.Activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Adapters.FavorRecyclerAdapter;
import com.favex.Adapters.GalleryAdapter;
import com.favex.Adapters.OrderRecyclerAdapter;
import com.favex.POJOs.OrderItem;
import com.favex.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.zip.Inflater;

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
    private ViewPager mViewPager;
    private TextView mImageLabel;
    private GalleryAdapter adapter;
    private ArrayList<Bitmap> bitmaps= new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
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
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        adapter = new GalleryAdapter(this, bitmaps);
        mViewPager.setAdapter(adapter);
        String favorJsonString= getIntent().getStringExtra("favorJsonString");
        try
        {
            final JSONObject favorJson = new JSONObject(favorJsonString);
            mFavorTitle.setText(favorJson.getString("title"));
            mDistance.setText(String.valueOf(favorJson.getInt("distance"))+" m");
            mFavorAddress.setText(favorJson.getString("locationFavorAddress"));
            mRecipientAddress.setText(favorJson.getString("locationRecipientAddress"));
            mMinPrice.setText(String.valueOf(favorJson.getJSONObject("priceRange").getInt("min")));
            mMaxPrice.setText(String.valueOf(favorJson.getJSONObject("priceRange").getInt("max")));
            mTip.setText(String.valueOf(favorJson.getDouble("tip")));
            mImageLabel.setText(favorJson.getString("locationFavorName"));
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
