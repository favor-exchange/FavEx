package com.favex.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.favex.R;
import com.favex.Services.ChatService;

public class login extends AppCompatActivity {

    private TextView info;
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
                if(isLoggedIn()){
                    startActivity(new Intent(login.this, MainActivity.class));
                    finish();
                }
            }
        });
        cbm = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        info = (TextView) findViewById(R.id.login_info);
        loginButton = (LoginButton) findViewById(R.id.login_button);





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

                SharedPreferences prefs = login.this.getSharedPreferences(
                        "com.favex", Context.MODE_PRIVATE);

                prefs.edit().putString("facebookId", loginResult.getAccessToken().getUserId()).apply();
                prefs.edit().putString("facebookAccessToken", loginResult.getAccessToken().getToken()).apply();

                Intent mServiceIntent = new Intent(login.this, ChatService.class);
                mServiceIntent.putExtra("myFacebookId", prefs.getString("facebookId", "default"));
                startService(mServiceIntent);

                Intent in = new Intent(login.this, MainActivity.class);
                startActivity(in);
                finish();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                info.setText("Login attempt failed.");
            }
        });

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
