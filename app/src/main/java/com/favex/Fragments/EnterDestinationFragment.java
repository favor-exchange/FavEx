package com.favex.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Activities.FavorFormActivity;
import com.favex.Interfaces.currentLocationInterface;
import com.favex.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Tavish on 08-Jan-17.
 */

public class EnterDestinationFragment extends Fragment implements currentLocationInterface {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Button mCurrentLocationBtn;
    private Button mNextBtn;
    private TextView mSendTo;
    private TextView mAddDetails;
    private EditText mDestination;
    private GoogleApiClient mGoogleApiClient;
    private Place[] possibleLocationsArray;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_destination_fragment, container, false);
        mGoogleApiClient = ((FavorFormActivity) getActivity()).getGoogleApiClient();
        // Would be more efficient to implement Google API Client as singleton, but currently not a priority
        mSendTo= (TextView)view.findViewById(R.id.sendTo);
        mDestination= (EditText)view.findViewById(R.id.destination);
        mAddDetails= (TextView)view.findViewById(R.id.addDetails);
        mNextBtn= (Button)view.findViewById(R.id.nextBtn);
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavorFormActivity)getActivity()).getVerticalViewPager().setCurrentItem(2);
            }
        });
        mAddDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length()>0)
                    ((FavorFormActivity)getActivity()).setDestinationDetails(charSequence
                            .toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDestination.setText("");
                openPlacesAutoComplete();
            }
        });
        mDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openPlacesAutoComplete();
                }
            }
        });
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
                    Log.i("Picking Destination","location permession was approved prior");
                    PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
                    result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                        @Override
                        public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                            Bundle possibleLocationsBundle= new Bundle();
                            possibleLocationsArray= new Place[likelyPlaces.getCount()];
                            String[] locationNames= new String[likelyPlaces.getCount()];
                            for (int i=0;i<likelyPlaces.getCount();i++) {
                                locationNames[i]=likelyPlaces.get(i).getPlace().getName().toString();
                                possibleLocationsArray[i]=likelyPlaces.get(i).getPlace().freeze();
                            }
                            possibleLocationsBundle.putStringArray("locationNames",locationNames);
                            final FragmentManager fm = getFragmentManager();
                            final LocationPicker dialogFragment = new LocationPicker ();
                            /*EnterDestinationFragment fragmentInstance =
                                    (EnterDestinationFragment) getFragmentManager().findFragmentByTag("android:switcher:" +
                                            R.id.verticalQuestionViewPager + ":" + ((FavorFormActivity)getActivity()).
                                            getVerticalViewPager().getCurrentItem());
                            //Method needs revision as it depends upon implementation in android library
                            //Method would not work if updates were to occur*/
                            dialogFragment.setTargetFragment(EnterDestinationFragment.this/*fragmentInstance*/,0);
                            dialogFragment.setArguments(possibleLocationsBundle);
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
                            Bundle possibleLocationsBundle= new Bundle();
                            possibleLocationsArray= new Place[likelyPlaces.getCount()];
                            String[] locationNames= new String[likelyPlaces.getCount()];
                            for (int i=0;i<locationNames.length;i++) {
                                locationNames[i]=likelyPlaces.get(i).getPlace().getName().toString();
                                possibleLocationsArray[i]=likelyPlaces.get(i).getPlace().freeze();;
                            }
                            possibleLocationsBundle.putStringArray("locationNames",locationNames);
                            final FragmentManager fm = getFragmentManager();
                            final LocationPicker dialogFragment = new LocationPicker ();
                            /*EnterDestinationFragment fragmentInstance =
                                    (EnterDestinationFragment) getFragmentManager().findFragmentByTag("android:switcher:" +
                                            R.id.verticalQuestionViewPager + ":" + ((FavorFormActivity)getActivity()).
                                            getVerticalViewPager().getCurrentItem());*/
                            dialogFragment.setTargetFragment(EnterDestinationFragment.this/*fragmentInstance*/,0);
                            dialogFragment.setArguments(possibleLocationsBundle);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mDestination.setText(place.getName());
                ((FavorFormActivity)getActivity()).setDestination(place);
                Log.i("Place API Test",((FavorFormActivity)getActivity()).getFavorLocation().getName().toString());
                mNextBtn.setEnabled(true);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                mNextBtn.setEnabled(false);
                Log.i("Place API Error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                mNextBtn.setEnabled(false);
                Toast.makeText(getActivity(),"Please pick a location",Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onLocationPicked(int index) {
        Place currentLocation= possibleLocationsArray[index];
        if(currentLocation.isDataValid()) {
            mSendTo.setText(currentLocation.getName());
            ((FavorFormActivity) getActivity()).setDestination(currentLocation);
            ((FavorFormActivity) getActivity()).setDestinationDetails(mAddDetails.getText().toString());
            mNextBtn.setEnabled(true);
        }
        else {
            Toast.makeText(getActivity(),"Error picking location",Toast.LENGTH_SHORT).show();
        }
    }
    void openPlacesAutoComplete()
    {
        try
        {
            Intent startPlaceAuto = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(getActivity());
            startActivityForResult(startPlaceAuto, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Place API Error", String.valueOf(e));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Place API Error", String.valueOf(e));
        }
    }
}
