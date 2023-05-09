package com.example.passwordmanager.LoadingScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.LoginPage.SetupLoginPage;
import com.example.passwordmanager.R;

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