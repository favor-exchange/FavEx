package com.favex.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.favex.Interfaces.currentLocationInterface;

/**
 * Created by Tavish on 10-Jan-17.
 */

public class LocationPicker extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final currentLocationInterface listener=(currentLocationInterface) getTargetFragment();
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick your location")
                .setItems(getArguments().getStringArray("locationNames"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        listener.onLocationPicked(getArguments().getStringArray("locationIds")[which]);
                        dismiss();
                    }
                });
        return builder.create();
    }
}
