package com.favex.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.favex.Adapters.QuestionFragmentAdapter;
import com.favex.RestManager.ApiClient;
import com.favex.Interfaces.postFavorInterface;
import com.favex.POJOs.OrderItem;
import com.favex.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Tavish on 08-Jan-17.
 */

public class FavorFormActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        postFavorInterface
{
    private static final float MIN_SCALE = 0.75f;
    private static final float MIN_ALPHA = 0.75f;
    VerticalViewPager mVerticalQuestionViewPager;
    private Place favorLocation;
    private Place destination;
    private String favorTitle;
    private String destinationDetails;
    private ArrayList<OrderItem> orderItems;
    private int[] priceRange= new int[2];
    private double tip;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    private AppEventsLogger logger;

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
        prefs = getSharedPreferences("com.favex", Context.MODE_PRIVATE);
    }
    public void setFavorLocation(Place place)
    {
        favorLocation = place;
    }
    public void setDestination(Place place)
    {
        destination = place;
    }
    public void setDestinationDetails(String value)
    {
        destinationDetails=value;
    }
    public void shallowCopyArrayList(ArrayList<OrderItem> listCopied)
    {
        orderItems= new ArrayList<>(listCopied);
    }
    public void setTip(double t)
    {
        tip=t;
    }
    public void setTitle(String value){favorTitle=value;}
    public VerticalViewPager getVerticalViewPager()
    {
        return mVerticalQuestionViewPager;
    }
    public int[] getPriceRange() {return priceRange;}
    public Place getFavorLocation()
    {
        return favorLocation;
    }
    public Place getDestination(){return destination;}
    private void initVerticalViewPager(VerticalViewPager v, final float MIN_SCALE, final float MIN_ALPHA) {
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"FATAL ERROR: GOOGLE PLAY SERVICES CONNECTION FAILED!",Toast.LENGTH_SHORT).show();
        //TODO: Provide more sophisticated implementation before submission
    }

    @Override
    public void postFavorToServer() {

        try
        {
            JSONObject favorJSON= new JSONObject();
            favorJSON.put("locationFavorId",favorLocation.getId());
            favorJSON.put("locationRecipientId",destination.getId());
            favorJSON.put("locationFavorAddress",favorLocation.getAddress());
            favorJSON.put("locationRecipientAddress",destination.getAddress());
            favorJSON.put("locationFavorName",favorLocation.getName());
            favorJSON.put("locationRecipientName",destination.getName());
            favorJSON.put("isComplete",false);
            favorJSON.put("title",favorTitle);
            favorJSON.put("details",destinationDetails);
            favorJSON.put("orderItems",OrderItem.orderItemsListToJsonArray(orderItems));
            favorJSON.put("priceRange",new JSONObject().put("min",priceRange[0]).put("max",priceRange[1]));
            favorJSON.put("recipientId",prefs.getString("facebookId","default"));
            favorJSON.put("doerId",JSONObject.NULL);
            favorJSON.put("tip",tip);

            JSONObject favorMainJSON=new JSONObject().put("favor",favorJSON);

            ApiClient.addFavor(favorMainJSON).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(Boolean.parseBoolean(response.body().string())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logger = AppEventsLogger.newLogger(FavorFormActivity.this);
                                logger.logEvent("newFavorAdded");
                                Toast.makeText(FavorFormActivity.this,"Favor added successfully",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
        catch (JSONException e)
        {
            Log.i("FavorFormJsonException",e.toString());
        }
    }
}
