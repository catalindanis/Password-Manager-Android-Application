package com.example.passwordmanager.LoadingScreen;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.LoginPage.LoginPage;
import com.example.passwordmanager.LoginPage.SetupLoginPage;
import com.example.passwordmanager.LoginPage.SetupPasswordPage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.Database;
import com.example.passwordmanager.User.User;

import java.io.*;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class LoadingScreen extends AppCompatActivity{
    Class<?> cls = SetupLoginPage.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        RunningActivities.addActivity(this);

        startActivity(new Intent(LoadingScreen.this,HomePage.class));

//        //loading screen backend processes
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(LoadingScreen.this, cls),
//                        ActivityOptions.makeSceneTransitionAnimation(LoadingScreen.this).toBundle());
//            }
//        }, 2000);
//
//        //check if is the first time user enters the application
//        //if it is true, then the user must setup a login method, so checking login type is no longer needed
//        if(!User.isFirstTime(this)){
//            if(User.getLoginType(this) != LoginType.NULL)
//                cls = LoginPage.class;
//        }
    }

    //block user from exiting app when it is in loading screen
    @Override
    public void onBackPressed(){}
}