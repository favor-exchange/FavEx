package com.favex.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Tavish on 22-Jan-17.
 */

public class GalleryAdapter extends PagerAdapter{
    private ArrayList<Bitmap> bitmaps;
    private LayoutInflater inflater;
    private Context context;
    public GalleryAdapter(Context context, ArrayList<Bitmap> bitmaps) {
        this.context = context;
        this.bitmaps=bitmaps;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv=new ImageView(context);
        iv.setLayoutParams(container.getLayoutParams());
        iv.setImageBitmap(bitmaps.get(position));
        container.addView(iv);
        return iv;
    }
}
