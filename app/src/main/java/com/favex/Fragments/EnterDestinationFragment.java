package com.favex.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.favex.Activities.FavorFormActivity;
import com.favex.Interfaces.currentLocationInterface;
import com.favex.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by Tavish on 08-Jan-17.
 */

public class EnterDestinationFragment extends Fragment implements currentLocationInterface {
    private Button mCurrentLocationBtn;
    private GoogleApiClient mGoogleApiClient;
    private String destinationId;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_destination_fragment, container, false);
        mGoogleApiClient = ((FavorFormActivity) getActivity()).getGoogleApiClient();
        // Would be more efficient to implement Google API Client as singleton, but currently not a priority
        mCurrentLocationBtn = (Button) view.findViewById(R.id.currentLocationBtn);
        mCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
                else{
                    PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
                    result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                        @Override
                        public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                            Bundle possibleLocations= new Bundle();
                            String[] locationIds= new String[likelyPlaces.getCount()];
                            String[] locationNames= new String[likelyPlaces.getCount()];
                            for (int i=0;i<locationNames.length;i++) {
                                locationNames[i]=likelyPlaces.get(i).getPlace().getName().toString();
                                locationIds[i]=likelyPlaces.get(i).getPlace().getId();
                            }
                            possibleLocations.putStringArray("locationNames",locationNames);
                            possibleLocations.putStringArray("locationIds",locationIds);
                            final FragmentManager fm = getFragmentManager();
                            final LocationPicker dialogFragment = new LocationPicker ();
                            EnterDestinationFragment fragmentInstance =
                                    (EnterDestinationFragment) getFragmentManager().findFragmentByTag("android:switcher:" +
                                            R.id.verticalQuestionViewPager + ":" + ((FavorFormActivity)getActivity()).
                                            getVerticalViewPager().getCurrentItem());
                            //FIGURING OUT THE ABOVE LINE WAS HELL
                            //More importantly, method needs revision as it depends upon implementation in android library
                            //Method would not work if updates were to occur
                            dialogFragment.setTargetFragment(fragmentInstance,0);
                            dialogFragment.setArguments(possibleLocations);
                            dialogFragment.show(fm, "Dialog Fragment");
                            likelyPlaces.release();
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                Log.i("Permissions","Permission interaction!");
                // If request is cancelled, the result arrays are empty.
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
                    result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                        @Override
                        public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                            Bundle possibleLocations= new Bundle();
                            String[] locationIds= new String[likelyPlaces.getCount()];
                            String[] locationNames= new String[likelyPlaces.getCount()];
                            for (int i=0;i<locationNames.length;i++) {
                                locationNames[i]=likelyPlaces.get(i).getPlace().getName().toString();
                                locationIds[i]=likelyPlaces.get(i).getPlace().getId();
                            }
                            possibleLocations.putStringArray("locationNames",locationNames);
                            possibleLocations.putStringArray("locationIds",locationIds);
                            final FragmentManager fm = getFragmentManager();
                            final LocationPicker dialogFragment = new LocationPicker ();
                            EnterDestinationFragment fragmentInstance =
                                    (EnterDestinationFragment) getFragmentManager().findFragmentByTag("android:switcher:" +
                                            R.id.verticalQuestionViewPager + ":" + ((FavorFormActivity)getActivity()).
                                            getVerticalViewPager().getCurrentItem());
                            dialogFragment.setTargetFragment(fragmentInstance,0);
                            dialogFragment.setArguments(possibleLocations);
                            dialogFragment.show(fm, "Dialog Fragment");
                            likelyPlaces.release();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(),"Location permissions needed to use your current location",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onLocationPicked(String placedId) {
        destinationId=placedId;
        Log.i("CURRENT LOCATION",destinationId);
    }

}
