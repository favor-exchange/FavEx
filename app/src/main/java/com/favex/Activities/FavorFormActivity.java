package com.favex.Activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Adapters.QuestionFragmentAdapter;
import com.favex.Interfaces.favorFormInterface;
import com.favex.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.util.Locale;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by Tavish on 08-Jan-17.
 */

public class FavorFormActivity extends AppCompatActivity implements favorFormInterface,
        GoogleApiClient.OnConnectionFailedListener  {
    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.75f;
    VerticalViewPager mVerticalQuestionViewPager;
    private Place favorLocation;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favor_form_activity);
        mVerticalQuestionViewPager = (VerticalViewPager) findViewById(R.id.verticalQuestionViewPager);
        initVerticalViewPager(mVerticalQuestionViewPager, MIN_SCALE, MIN_ALPHA);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }
    public void setFavorLocation(Place place)
    {
        favorLocation = place;
    }

    public VerticalViewPager getVerticalViewPager()
    {
        return mVerticalQuestionViewPager;
    }

    public Place getFavorLocation()
    {
        return favorLocation;
    }
    private void initVerticalViewPager(VerticalViewPager v, final float MIN_SCALE, final float MIN_ALPHA)
    {
        v.setPageMargin(getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin));
        v.setAdapter(new QuestionFragmentAdapter(getSupportFragmentManager()));
        v.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                int pageWidth = view.getWidth();
                int pageHeight = view.getHeight();

                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);

                } else if (position <= 1) { // [-1,1]
                    // Modify the default slide transition to shrink the page as well
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        view.setTranslationY(vertMargin - horzMargin / 2);
                    } else {
                        view.setTranslationY(-vertMargin + horzMargin / 2);
                    }

                    // Scale the page down (between MIN_SCALE and 1)
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                    // Fade the page relative to its size.
                    view.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                } else { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        });
    }

    public GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    @Override
    public void onNext(int fragmentPos, VerticalViewPager v) {
        v.setCurrentItem(fragmentPos+1);
        Toast.makeText(this,"Successful fragment callback!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"FATAL ERROR: GOOGLE PLAY SERVICES CONNECTION FAILED!",Toast.LENGTH_SHORT);
        //TODO: Provide more sophisticated implementation before submission
    }
}
