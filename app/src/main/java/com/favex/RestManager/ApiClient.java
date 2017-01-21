package com.favex.RestManager;

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
    private static String baseUrl="192.168.1.101"; //testing on local machine but request was successful on aws too
    private static int port= 3000;
    private static String addFavorEndpoint= "addFavor";
    private static String getNearbyFavorsEndpoint= "getNearbyFavors";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
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
    public static Call getNearbyFavors(String userLocationId,String distance)
    {
        HttpUrl httpUrl= new HttpUrl.Builder()
                .scheme("http")
                .host(baseUrl)
                .port(port)
                .addPathSegment(getNearbyFavorsEndpoint)
                .setQueryParameter("userLocationId",userLocationId)
                .setQueryParameter("distance",distance)
                .build();
        Request request= new Request.Builder()
                .url(httpUrl)
                .build();
        return OkHttpSingleton.getOkHttpInstance().getOkHttpClient().newCall(request);
    }
}
