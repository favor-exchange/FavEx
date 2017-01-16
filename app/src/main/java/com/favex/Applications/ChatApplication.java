package com.favex.Applications;

import android.app.Application;
import android.provider.SyncStateContract;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Shayan on 09-Jan-17.
 */

public class ChatApplication extends Application {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://54.201.173.243:3000"); //temp ip until chat server is up
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
