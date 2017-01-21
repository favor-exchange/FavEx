package com.favex.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.favex.R;
import com.favex.Adapters.TabFragmentPagerAdapter;
import com.favex.Services.ChatService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private FloatingActionButton mAddFavor;
    private SharedPreferences prefs;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        prefs = getSharedPreferences(
                "com.favex", Context.MODE_PRIVATE);

        if (!isMyServiceRunning(ChatService.class)) {

            Intent mServiceIntent = new Intent(this, ChatService.class);
            mServiceIntent.putExtra("myFacebookId", prefs.getString("facebookId", "default"));
            startService(mServiceIntent);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        int defaultValue = 0;
        int page = getIntent().getIntExtra("ARG_PAGE", defaultValue);
        viewPager.setCurrentItem(page);

        mAddFavor= (FloatingActionButton)findViewById(R.id.addFavor);
        mAddFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startFavorForm= new Intent(MainActivity.this,FavorFormActivity.class);
                startActivity(startFavorForm);
            }
        });
    }
    public GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this,"FATAL ERROR: GOOGLE PLAY SERVICES CONNECTION FAILED!",Toast.LENGTH_SHORT);
        //TODO: Provide more sophisticated implementation before submission
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout:

                FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                    @Override
                    public void onInitialized() {
                        LoginManager.getInstance().logOut();
                        prefs.edit().putString("facebookId", "none").apply();
                        prefs.edit().putString("facebookAccessToken", "none").apply();
                        Intent mServiceIntent = new Intent(MainActivity.this, ChatService.class);
                        stopService(mServiceIntent);
                        startActivity(new Intent(MainActivity.this, login.class));
                        finish();
                    }
                });
                break;
            case R.id.action_settings:
        }
        return true;
    }
}
