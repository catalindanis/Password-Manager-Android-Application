package com.example.passwordmanager.LoadingScreen;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.LoadState;

import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.LoginPage.LoginPage;
import com.example.passwordmanager.LoginPage.SetupLoginPage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

public class LoadingScreen extends AppCompatActivity{
    Class<?> cls = SetupLoginPage.class;

    public static boolean APP_READY = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        RunningActivities.addActivity(this);

        //User.removeLoginType(this);

        //starting desired class after loading processes are finished
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(!APP_READY);
                        startActivity(new Intent(LoadingScreen.this, cls));
                        overridePendingTransition(R.anim.zoom_in,R.anim.static_animation);
                    }
                }).start();
            }
        }, 1000);

        if(User.getLoginType(this) != LoginType.NULL)
            cls = LoginPage.class;
        else User.removePasswords(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                User.init(getApplicationContext());
            }
        }).start();
    }

    //block user from exiting app when it is in loading screen
    @Override
    public void onBackPressed(){}
}