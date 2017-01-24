package com.favex.Services;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.favex.Activities.FavorDetails;
import com.favex.Activities.MainActivity;
import com.favex.R;
import com.favex.RestManager.ApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by Tavish on 23-Jan-17.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private GoogleApiClient mLocationClient;
    private FusedLocationProviderApi fusedLocationProviderApi;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private NotificationManager mNotificationManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LOCATION SERVICE","SERVICE STARTED!");
        mLocationClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mLocationClient.connect();
        fusedLocationProviderApi = LocationServices.FusedLocationApi;
        mLocationRequest = new LocationRequest();
        setLocationParameter();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("LOCATION SERVICE","LOCATION UPDATE NOT REGISTERED AS LOCATION PERMISSION WAS DENIED");
            return;
        }
        //initially get last known location
        mCurrentLocation = fusedLocationProviderApi.getLastLocation(mLocationClient);
        if(mCurrentLocation!=null)
        {
            double lat= mCurrentLocation.getLatitude();
            double lng= mCurrentLocation.getLongitude();
            searchForNearbyFavors(lat,lng,500);
        }
        //register for location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("LOCATION SERVICE","CONNECTION SUSPENDED");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LOCATION SERVICE","CONNECTION FAILED");
    }

    private void setLocationParameter() {
        mLocationRequest.setInterval(60000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setFastestInterval(60000);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("LOCATION SERVICE","LOCATION CHANGED!");
        double lat= location.getLatitude();
        double lng= location.getLongitude();
        searchForNearbyFavors(lat,lng,500);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient,this);
    }

    public void searchForNearbyFavors(double lat, double lng, int distance)
    {
        ApiClient.getNearbyFavors(String.valueOf(lat),String.valueOf(lng),String.valueOf(distance))
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try
                        {
                            Log.i("LOCATION SERVICE","SERVICE API RESPONSE");
                            String responseString= response.body().string();
                            if(!responseString.equals("false"))
                            {
                                JSONArray jsonArray= new JSONArray(responseString);
                                if(jsonArray.length()> 0)
                                {
                                    JSONObject closestFavor= jsonArray.getJSONObject(0);
                                    for(int i=1;i<jsonArray.length();i++)
                                    {
                                        if(closestFavor.getInt("distance") > jsonArray
                                                .getJSONObject(i).getInt("distance"))
                                            closestFavor=jsonArray.getJSONObject(i);
                                    }
                                    long pattern[] = {0, 100, 300, 300};
                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(LocationService.this)
                                                    .setSmallIcon(R.mipmap.ic_favexlauncher)
                                                    .setContentTitle("We found a favor nearby")
                                                    .setContentText(closestFavor.getString("title"))
                                                    .setVibrate(pattern);
                                    Intent resultIntent = new Intent(LocationService.this, FavorDetails.class);
                                    resultIntent.putExtra("favorJsonString", closestFavor.toString());
                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(LocationService.this);
                                    // Adds the back stack for the Intent (but not the Intent itself)
                                    stackBuilder.addParentStack(MainActivity.class);
                                    // Adds the Intent that starts the Activity to the top of the stack
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent =
                                            stackBuilder.getPendingIntent(
                                                    0,
                                                    PendingIntent.FLAG_UPDATE_CURRENT
                                            );
                                    mBuilder.setContentIntent(resultPendingIntent);
                                    mNotificationManager = (NotificationManager) LocationService.this
                                            .getSystemService(Context.NOTIFICATION_SERVICE);
                                    mNotificationManager.notify(0, mBuilder.build());
                                }
                            }
                            else
                            {
                                Log.i("LOCATION SERVICE", "SERVER SENT FAIL RESPONSE");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
