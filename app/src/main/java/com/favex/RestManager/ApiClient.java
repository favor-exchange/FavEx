package com.favex.RestManager;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Tavish on 17-Jan-17.
 */

public class ApiClient {
    private static String baseUrl="54.201.173.243";
    //54.201.173.243
    private static int port= 80;
    private static String addFavorEndpoint= "addFavor";
    private static String addUserEndpoint = "addUser";
    private static String getUserEndpoint = "getUser";
    private static String getFavorsRequestedEndpoint = "getFavorsRequested";
    private static String getFavorsDoneEndpoint = "getFavorsDone";
    private static String getNearbyFavorsEndpoint = "getNearbyFavors";
    private static String updateLocationEndpoint = "updateLocation";
    private static String updateTipEndpoint = "updateTip";
    private static String updateFavorStatusEndpoint = "updateFavorStatus";
    private static String updateDoerEndpoint = "updateDoer";
    private static String updateRatingEndpoint = "updateRating";
    private static String deleteFavorEndpoint = "deleteFavor";
    private static String deleteUserEndpoint = "deleteUser";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    //Object should contain all user details
    public static Call addUser(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(addUserEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .post(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //Object should contain all favor details
    public static Call addFavor(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(addFavorEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .post(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //String should contain fb id of user
    public static Call getUser(String id)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(getUserEndpoint)
                .addQueryParameter("id", id)
                .build();
        Request request= new Request.Builder()
                .get()
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    public static Call getFavorsRequested(String id)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(getFavorsRequestedEndpoint)
                .addQueryParameter("id", id)
                .build();
        Request request= new Request.Builder()
                .get()
                .url(httpUrl)
                .build();
        Log.i("API CLIENT","FAVORS REQUESTED!");
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    public static Call getFavorsDone(String id)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(getFavorsDoneEndpoint)
                .addQueryParameter("id", id)
                .build();
        Request request= new Request.Builder()
                .url(httpUrl)
                .build();
        Log.i("API CLIENT","FAVORS DONE!");
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    public static Call getNearbyFavors(String lat, String lng, String distance)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(getNearbyFavorsEndpoint)
                .addQueryParameter("lat",lat)
                .addQueryParameter("lng",lng)
                .addQueryParameter("distance",distance)
                .build();
        Request request= new Request.Builder()
                .url(httpUrl)
                .build();
        Log.i("API CLIENT","NEARBY FAVORS!");
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //object must contain user object with fbid and new location
    public static Call updateLocation(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(updateLocationEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .put(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //object must contain favour object with favor _id and tip
    public static Call updateTip(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(updateTipEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .put(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //object must contain favor object with _id and isComplete bool
    public static Call updateFavorStatus(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(updateFavorStatusEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .put(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //object must contain favor object with _id and doerId
    public static Call updateDoer(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(updateDoerEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .put(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //object must contain user object with facebookId and rating
    public static Call updateRating(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(updateRatingEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .put(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //object must contain favor object with _id
    public static Call deleteFavor(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(deleteFavorEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .delete(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }

    //object must contain user object with facebookId
    public static Call deleteUser(JSONObject jsonObject)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(deleteUserEndpoint)
                .build();
        RequestBody body= RequestBody.create(JSON,jsonObject.toString());
        Request request= new Request.Builder()
                .delete(body)
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }
}
