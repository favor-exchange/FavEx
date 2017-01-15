package com.favex.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import com.favex.R;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class FavorRecyclerAdapter extends RecyclerView.Adapter<FavorRecyclerAdapter.FavorViewHolder>
{
    private ArrayList<JSONObject> favorList= new ArrayList<>();
    private LayoutInflater layoutInflater;
    public FavorRecyclerAdapter(Context context)
    {
        layoutInflater= LayoutInflater.from(context);
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
    }
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    @Override
    public int getItemCount()
    {
        return 0;
    }

    public class FavorViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mLocationImage;
        private TextView mPrice;
        private TextView mTime;
        private TextView mAttribution;
        public FavorViewHolder(final View itemView)
        {
            super(itemView);
            mLocationImage= (ImageView)itemView.findViewById(R.id.locationImage);
            mPrice= (TextView)itemView.findViewById(R.id.price);
            mTime= (TextView)itemView.findViewById(R.id.time);
            mAttribution= (TextView)itemView.findViewById(R.id.attribution);
        }
    }
}
