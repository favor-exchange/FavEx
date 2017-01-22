package com.favex.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import com.favex.Activities.FavorDetails;
import com.favex.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import static android.text.Html.FROM_HTML_MODE_COMPACT;
import static android.text.Html.FROM_HTML_MODE_LEGACY;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class FavorRecyclerAdapter extends RecyclerView.Adapter<FavorRecyclerAdapter.FavorViewHolder>
{
    private ArrayList<JSONObject> favorList= new ArrayList<>();
    private LayoutInflater layoutInflater;
    private GoogleApiClient mGoogleApiClient;
    public FavorRecyclerAdapter(Context context, GoogleApiClient googleApiClient)
    {
        mGoogleApiClient= googleApiClient;
        layoutInflater= LayoutInflater.from(context);
    }
    public void setFavorList(JSONArray jsonArray)
    {
        try
        {
            for (int i = 0; i < jsonArray.length(); i++)
                favorList.add(jsonArray.getJSONObject(i));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public FavorViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view=layoutInflater.inflate(R.layout.near_me_favor_item, parent, false);
        return new FavorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavorViewHolder viewHolder, int position)
    {
        JSONObject favor= favorList.get(position);
        try
        {
            new getFavorLocationPhoto(viewHolder.mLocationImage.getMeasuredWidth(),
                    viewHolder.mLocationImage.getMeasuredHeight(),
                    viewHolder.mLocationImage, viewHolder.mAttribution)
                    .execute(favor.getString("locationFavorId"));
            viewHolder.mMinPrice.setText(String.valueOf(favor.getJSONObject("priceRange").getInt("min")));
            viewHolder.mMaxPrice.setText(String.valueOf(favor.getJSONObject("priceRange").getInt("max")));
            viewHolder.mTitle.setText(favor.getString("title"));
            viewHolder.mDistance.setText(String.valueOf(favor.getInt("distance")));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public int getItemCount()
    {
        return favorList.size();
    }

    public class FavorViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mLocationImage;
        private TextView mMinPrice;
        private TextView mMaxPrice;
        private TextView mTitle;
        private TextView mDistance;
        private TextView mAttribution;
        public FavorViewHolder(final View itemView)
        {
            super(itemView);
            mLocationImage= (ImageView)itemView.findViewById(R.id.locationImage);
            mMinPrice= (TextView)itemView.findViewById(R.id.minPrice);
            mMaxPrice= (TextView)itemView.findViewById(R.id.maxPrice);
            mTitle= (TextView)itemView.findViewById(R.id.title);
            mDistance= (TextView)itemView.findViewById(R.id.distance);
            mAttribution= (TextView)itemView.findViewById(R.id.attribution);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i= new Intent(view.getContext(), FavorDetails.class);
                    i.putExtra("favorJsonString", favorList.get(getAdapterPosition()).toString());
                    view.getContext().startActivity(i);
                }
            });
        }
    }
    private class getFavorLocationPhoto extends AsyncTask<String,Void,getFavorLocationPhoto.AttributedPhoto> {
        private int mHeight;
        private int mWidth;
        private ImageView mImageView;
        private TextView mTextView;
        AttributedPhoto attributedPhoto = null;

        public getFavorLocationPhoto(int width, int height, ImageView imageView, TextView attrsText) {
            mHeight = height;
            mWidth = width;
            mImageView= imageView;
            mTextView= attrsText;
        }
        protected void onPreExecute()
        {
            mImageView.setImageResource(R.drawable.ic_image_black_24dp);
        }
        protected AttributedPhoto doInBackground(String... strings) {
            PlacePhotoMetadataResult result = Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, strings[0]).await();
            if (result.getStatus().isSuccess())
            {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled())
                {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await().getBitmap();
                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        @SuppressWarnings("deprecation") //to enable from html method for versions below 24
        protected void onPostExecute(AttributedPhoto result)
        {
            if(attributedPhoto!=null)
            {
                mImageView.setImageBitmap(result.bitmap);
                if (Build.VERSION.SDK_INT >= 24)
                    mTextView.setText(Html.fromHtml(result.attribution.toString(),FROM_HTML_MODE_LEGACY));
                else
                    mTextView.setText(Html.fromHtml(result.attribution.toString()));
            }
        }

        class AttributedPhoto
        {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap)
            {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }
}