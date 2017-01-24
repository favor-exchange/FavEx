package com.favex.RestManager;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Tavish on 17-Jan-17.
 */

public class OkHttpSingleton{

    private static OkHttpSingleton okHttpSingleton;
    private OkHttpClient okHttpClient;

    private OkHttpSingleton(){

        File cacheDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        Cache cache = new Cache(cacheDir, 1024 * 10 * 10);

        okHttpClient= new OkHttpClient.Builder()
                .readTimeout(300, TimeUnit.SECONDS)
                //.cache(cache)
                .build();

    }

    public static OkHttpSingleton getOkHttpInstance() {
        if(okHttpSingleton==null) {

            return new OkHttpSingleton();
        }
        return okHttpSingleton;
    }

    public OkHttpClient getOkHttpClient()
    {
        return okHttpClient;
    }


}
