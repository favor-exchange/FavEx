package com.favex.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.favex.R;
import com.favex.Services.ChatService;

import org.json.JSONException;
import org.json.JSONObject;

public class login extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager cbm;
    private AccessToken accessToken;
    private Button tmpSkip; //DELET THIS, THIS IS BAD CONTENT

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                AppEventsLogger.activateApp(getApplication());
                if(isLoggedIn()){
                    startActivity(new Intent(login.this, MainActivity.class));
                    finish();
                }
            }
        });
        cbm = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        getSupportActionBar().hide();







        //DELET THIS
        tmpSkip = (Button) findViewById(R.id.skip_login_tmp_button);

        tmpSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = login.this.getSharedPreferences(
                        "com.favex", Context.MODE_PRIVATE);

                prefs.edit().putString("facebookId", "test").apply();
                prefs.edit().putString("facebookAccessToken", "test").apply();

                Intent in = new Intent(login.this, MainActivity.class);
                startActivity(in);
                finish();

            }
        });
        //END DELET







        loginButton.registerCallback(cbm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                final SharedPreferences prefs = login.this.getSharedPreferences(
                        "com.favex", Context.MODE_PRIVATE);

                prefs.edit().putString("facebookId", loginResult.getAccessToken().getUserId()).apply();
                Log.e("fbid", loginResult.getAccessToken().getUserId());
                prefs.edit().putString("facebookAccessToken", loginResult.getAccessToken().getToken()).apply();

                Intent mServiceIntent = new Intent(login.this, ChatService.class);
                mServiceIntent.putExtra("myFacebookId", prefs.getString("facebookId", "default"));
                startService(mServiceIntent);

                GraphRequest request = GraphRequest.newMeRequest( AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {
                                try {
                                    String  name=object.getString("name");
                                    prefs.edit().putString("name", name).apply();

                                    Log.d("user name ", name);
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }

                        });

                request.executeAsync();

                Intent in = new Intent(login.this, MainActivity.class);
                startActivity(in);
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(login.this, "Login attempt canceled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(login.this, "Login attempt failed.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cbm.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired())
            return true;
        else
            return false;
    }

}
