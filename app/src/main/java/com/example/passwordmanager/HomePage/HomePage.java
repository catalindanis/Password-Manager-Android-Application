package com.example.passwordmanager.HomePage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passwordmanager.AboutPage.About;
import com.example.passwordmanager.AddPasswordPage.AddPassword;
import com.example.passwordmanager.Config.OnSwipeTouchListener;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.LoginPage.SetupPasswordPage;
import com.example.passwordmanager.Password.Password;
import com.example.passwordmanager.R;
import com.example.passwordmanager.SettingsPage.Settings;
import com.example.passwordmanager.User.User;

import java.io.UnsupportedEncodingException;
import android.net.Uri;

import java.net.URI;
import java.util.List;

public class HomePage extends AppCompatActivity {

    ConstraintLayout screen;
    ConstraintLayout menuLayout;
    LinearLayout passwordListLayout;
    ScrollView scrollView;
    //LinearLayout passwordListLayout;
    Button addButton;
    Button settingsButton;
    Button aboutButton;
    ImageView addButtonIcon;
    ImageView settingsButtonIcon;
    ImageView aboutButtonIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        RunningActivities.addActivity(this);
        RunningActivities.finishBackgroundActivitiesExcept(this);

        //change User.isFirstTime variable to false,
        //because if user entered in home page, then the variable must be false
        if(User.isFirstTime(this))
            User.setFirstTime(this,false);

        //User.removePasswords(this);

        initializeValues();

        setupListeners();

        loadPasswords();
    }

    private void setupListeners() {
        scrollView.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                menuLayout.setVisibility(View.VISIBLE);
                //bring layout to front, so that scrollview don't block user from pressing the buttons
                menuLayout.bringToFront();
            }

            @Override
            public void onSwipeLeft() {
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });


        //at every button, bring image layout to front, so that button don't come above it
        addButton.setOnClickListener((view) -> {
            addButtonIcon.bringToFront();
            startActivity(new Intent(HomePage.this, AddPassword.class));
        });

        settingsButton.setOnClickListener((view) -> {
            settingsButtonIcon.bringToFront();

            startActivity(new Intent(HomePage.this, Settings.class));
        });

        aboutButton.setOnClickListener((view) -> {
            aboutButtonIcon.bringToFront();

            startActivity(new Intent(HomePage.this, About.class));
        });
    }

    private void initializeValues() {
        screen = (ConstraintLayout) findViewById(R.id.ConstraintLayout);
        menuLayout = (ConstraintLayout) findViewById(R.id.MenuLayout);
        passwordListLayout = (LinearLayout) findViewById(R.id.PasswordListLayout);
        scrollView = (ScrollView) findViewById(R.id.ScrollView);
        addButton = (Button) findViewById(R.id.addButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        aboutButton = (Button) findViewById(R.id.aboutButton);
        menuLayout.setVisibility(View.INVISIBLE);
        addButtonIcon = (ImageView) findViewById(R.id.imageView6);
        settingsButtonIcon = (ImageView) findViewById(R.id.imageView8);
        aboutButtonIcon = (ImageView) findViewById(R.id.imageView7);
    }

    public void loadPasswords(){
        List<Password> passwordList = User.getPasswords(this);

        if(passwordList == null) {
            Log.d("COMMENT","THERE ARE NO PASSWORDS ADDED!");
            return;
        }

        for(Password currentPassword : passwordList){
            try {
                View passwordLayout = getLayoutInflater().inflate(R.layout.password, null);

                TextView passwordEmail = (TextView) passwordLayout.findViewById(R.id.passwordEmail);
                passwordEmail.setText(currentPassword.getEmail());

                ImageView passwordIcon = (ImageView) passwordLayout.findViewById(R.id.passwordIcon);
                Bitmap bmp = BitmapFactory.decodeByteArray(currentPassword.getIcon(), 0, currentPassword.getIcon().length);
                passwordIcon.setImageBitmap(bmp);

                ImageView editButton = (ImageView) passwordLayout.findViewById(R.id.passwordEdit);
                editButton.setOnClickListener((view) -> {
                    //edit Password
                });

                passwordListLayout.addView(passwordLayout);
            }catch (Exception exception){
                Log.d("COMMENT",exception.getMessage());
            }
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, ToastMessage.PRESS_DOUBLE_TO_EXIT, Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}