package com.favex.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.favex.Activities.MainActivity;
import com.favex.Adapters.FavorRecyclerAdapter;
import com.favex.R;
import com.favex.RestManager.ApiClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class NearMeFragment extends Fragment {
    private RecyclerView mFavorRecycler;
    protected FavorRecyclerAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private String userLocationId;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.near_me_fragment, container, false);

        mFavorRecycler = (RecyclerView) view.findViewById(R.id.favorRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFavorRecycler.setLayoutManager(linearLayoutManager);
        mGoogleApiClient = ((MainActivity) getActivity()).getGoogleApiClient();
        mAdapter = new FavorRecyclerAdapter(getActivity(), mGoogleApiClient);
        mFavorRecycler.setAdapter(mAdapter);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    if (likelyPlaces.getCount() != 0) {
                        PlaceLikelihood mostLikelyLocation = likelyPlaces.get(0);
                        for (int i = 1; i < likelyPlaces.getCount(); i++) {
                            if (likelyPlaces.get(i).getLikelihood() > mostLikelyLocation.getLikelihood()) //if other places have higher probability
                                mostLikelyLocation = likelyPlaces.get(i);
                        }
                        double userLat= mostLikelyLocation.getPlace().getLatLng().latitude;
                        double userLng= mostLikelyLocation.getPlace().getLatLng().latitude;
                        likelyPlaces.release();
                        ApiClient.getNearbyFavors(String.valueOf(userLat),
                                String.valueOf(userLng), "500").enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Error getting favors", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //if(!response.body().string().equals("false"))
                                            mAdapter.setFavorList(new JSONArray(response.body().string()));
                                        } catch (IOException | JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(), "no nearby places found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
                    result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                        @Override
                        public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                            if(likelyPlaces.getCount() != 0) {
                                PlaceLikelihood mostLikelyLocation = likelyPlaces.get(0);
                                for (int i = 1; i < likelyPlaces.getCount(); i++) {
                                    if (likelyPlaces.get(i).getLikelihood() > mostLikelyLocation.getLikelihood()) //if other places have higher probability
                                        mostLikelyLocation = likelyPlaces.get(i);
                                }
                                userLocationId = mostLikelyLocation.getPlace().getId();
                                likelyPlaces.release();
                            }
                            else{
                                Toast.makeText(getActivity(), "no nearby places found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Location permissions needed to find favors near you",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
