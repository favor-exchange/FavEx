package com.favex.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.favex.Services.LocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private FloatingActionButton mAddFavor;
    private SharedPreferences prefs;
    private GoogleApiClient mGoogleApiClient;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    final private int REQUEST_CHECK_SETTINGS= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        prefs = getSharedPreferences(
                "com.favex", Context.MODE_PRIVATE);

        mAddFavor= (FloatingActionButton)findViewById(R.id.addFavor);
        mAddFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startFavorForm= new Intent(MainActivity.this,FavorFormActivity.class);
                startActivity(startFavorForm);
            }
        });

        LocationRequest mLocationRequestHighAccuracy= new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states= result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try
                        {
                            status.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("Main Activity","SETTINGS UNAVAILABLE!");
                        break;
                }
            }
        });

        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");

        if (permissionsList.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
        }
        else
        {
            if (!isMyServiceRunning(ChatService.class)) {
                Intent mServiceIntent = new Intent(this, ChatService.class);
                mServiceIntent.putExtra("myFacebookId", prefs.getString("facebookId", "default"));
                startService(mServiceIntent);
            }

            if (!isMyServiceRunning(LocationService.class)) {
                Intent mServiceIntent = new Intent(this, LocationService.class);
                startService(mServiceIntent);
            }

            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

            viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));

            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

            int defaultValue = 0;
            int page = getIntent().getIntExtra("ARG_PAGE", defaultValue);
            viewPager.setCurrentItem(page);
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    @Override
    protected void onActivityResult(int locationRequestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (locationRequestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this,"Locations needs to be activated for optimal functionality",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if(grantResults.length>0)
                {
                    for(int i=0;i<grantResults.length;i++)
                    {
                        if(grantResults[i]==PackageManager.PERMISSION_DENIED)
                        {
                            if(permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION))
                                Toast.makeText(this,"Location needed to display favors near you",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(this,"Storage permission needed to save your info",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            default: Toast.makeText(this,"Permissions Callback Error",Toast.LENGTH_SHORT).show();
        }


        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        int defaultValue = 0;
        int page = getIntent().getIntExtra("ARG_PAGE", defaultValue);

        viewPager.setCurrentItem(page);
        if (!isMyServiceRunning(ChatService.class)) {
            Intent mServiceIntent = new Intent(this, ChatService.class);
            mServiceIntent.putExtra("myFacebookId", prefs.getString("facebookId", "default"));
            startService(mServiceIntent);
        }

        if (!isMyServiceRunning(LocationService.class)) {
            Intent mServiceIntent = new Intent(this, LocationService.class);
            startService(mServiceIntent);
        }
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
        }
        return true;
    }
}
