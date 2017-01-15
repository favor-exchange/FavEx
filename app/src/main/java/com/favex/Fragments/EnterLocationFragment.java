package com.favex.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.favex.Activities.FavorFormActivity;
import com.favex.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Tavish on 08-Jan-17.
 */

public class EnterLocationFragment extends Fragment {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText mLocation;
    private Button mNext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.enter_location_fragment, container, false);
        mLocation= (EditText)view.findViewById(R.id.location);
        mNext= (Button)view.findViewById(R.id.next);
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation.setText("");
                openPlacesAutoComplete();
            }
        });
        mLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openPlacesAutoComplete();
                }
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FavorFormActivity)getActivity()).getVerticalViewPager().setCurrentItem(2);
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mLocation.setText(place.getName());
                ((FavorFormActivity)getActivity()).setFavorLocation(place); //casted to our own activity to enable custom functions
                Log.i("Place API Test",((FavorFormActivity)getActivity()).getFavorLocation().getName().toString());
                mNext.setEnabled(true);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                mNext.setEnabled(false);
                Log.i("Place API Error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                mNext.setEnabled(false);
                Toast.makeText(getActivity(),"Please pick a location",Toast.LENGTH_SHORT).show();
            }
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
