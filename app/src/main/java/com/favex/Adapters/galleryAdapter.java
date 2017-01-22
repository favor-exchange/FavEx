package com.favex.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Tavish on 22-Jan-17.
 */

public class GalleryAdapter extends PagerAdapter{
    private int[] images; //change to bitmap
    private LayoutInflater inflater;
    private Context context;
    public GalleryAdapter(Context context,int[] images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv=new ImageView(context);
        iv.setLayoutParams(container.getLayoutParams());
        iv.setImageResource(images[position]); //change to set bitmap
        container.addView(iv);
        return iv;
    }
}
