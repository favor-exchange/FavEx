package com.favex.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.favex.Activities.MainActivity;
import com.favex.Adapters.RecentFavorRecyclerAdapter;
import com.favex.R;
import com.favex.RestManager.ApiClient;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Tavish on 06-Jan-17.
 */

public class RecentFragment extends Fragment
{
    private RecyclerView mFavorsRequestedRecycler;
    private RecyclerView mFavorsDoneRecycler;
    protected RecentFavorRecyclerAdapter mRequestedAdapter;
    protected RecentFavorRecyclerAdapter mDoneAdapter;
    private GoogleApiClient mGoogleApiClient;
    private TextView label_requested;
    private TextView label_done;
    SharedPreferences prefs;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.recent_fragment, container, false);

        mFavorsRequestedRecycler = (RecyclerView) view.findViewById(R.id.recent_requested_recycler);
        mFavorsDoneRecycler = (RecyclerView) view.findViewById(R.id.recent_done_recycler);
        LinearLayoutManager llmRequested = new LinearLayoutManager(getActivity());
        LinearLayoutManager llmDone = new LinearLayoutManager(getActivity());
        mFavorsRequestedRecycler.setLayoutManager(llmRequested);
        mFavorsRequestedRecycler.setLayoutManager(llmDone);

        mGoogleApiClient = ((MainActivity) getActivity()).getGoogleApiClient();

        mRequestedAdapter = new RecentFavorRecyclerAdapter(getActivity(), mGoogleApiClient);
        mDoneAdapter = new RecentFavorRecyclerAdapter(getActivity(), mGoogleApiClient);
        mFavorsRequestedRecycler.setAdapter(mRequestedAdapter);
        mFavorsDoneRecycler.setAdapter(mDoneAdapter);

        label_requested = (TextView) view.findViewById(R.id.label_favors_requested);
        label_done = (TextView) view.findViewById(R.id.label_favors_done);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        prefs = getActivity().getSharedPreferences("com.favex", Context.MODE_PRIVATE);

        ApiClient.getFavorsRequested(prefs.getString("facebookId", "default")).enqueue(new Callback() {
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
                            String responseString= response.body().string();
                            if(!responseString.equals("false")) {
                                mRequestedAdapter.setFavorList(new JSONArray(responseString));
                            }
                            else {
                                Log.i("Near Me Fragment", "False server response");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ApiClient.getFavorsDone(prefs.getString("facebookId", "default")).enqueue(new Callback() {
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
                            String responseString= response.body().string();
                            if(!responseString.equals("false")) {
                                mDoneAdapter.setFavorList(new JSONArray(responseString));
                            }
                            else {
                                Log.i("Near Me Fragment", "False server response");
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        label_requested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View tmp = view.findViewById(R.id.recent_requested_recycler);

                if(tmp.getVisibility() == View.GONE){
                    tmp.setVisibility(View.VISIBLE);
                }
                else{
                    tmp.setVisibility(View.GONE);
                }
            }
        });

        label_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View tmp = view.findViewById(R.id.recent_done_recycler);

                if(tmp.getVisibility() == View.GONE){
                    tmp.setVisibility(View.VISIBLE);
                }
                else{
                    tmp.setVisibility(View.GONE);
                }
            }
        });


        return view;
    }
}
