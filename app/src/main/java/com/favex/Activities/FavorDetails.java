package com.favex.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.favex.Adapters.GalleryAdapter;
import com.favex.R;

/**
 * Created by Tavish on 21-Jan-17.
 */

public class FavorDetails extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favor_details_activity);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        int[] images= new int []{R.mipmap.ic_launcher,R.drawable.ic_send_black_24dp};
        GalleryAdapter adapter = new GalleryAdapter(this,images);
        viewPager.setAdapter(adapter);
    }
}
