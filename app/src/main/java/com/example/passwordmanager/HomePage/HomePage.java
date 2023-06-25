package com.example.passwordmanager.HomePage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passwordmanager.AboutPage.AboutPage;
import com.example.passwordmanager.PasswordManagerPage.PasswordManagerPage;
import com.example.passwordmanager.Config.OnSwipeTouchListener;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.Password.Password;
import com.example.passwordmanager.R;
import com.example.passwordmanager.SettingsPage.SettingsPage;
import com.example.passwordmanager.User.User;

public class HomePage extends AppCompatActivity {

    ConstraintLayout screen;
    ConstraintLayout menuLayout;
    LinearLayout passwordListLayout;
    ScrollView scrollView;
    ConstraintLayout add;
    ConstraintLayout settings;
    ConstraintLayout about;

    ImageView addButtonIcon;
    ImageView settingsButtonIcon;
    ImageView aboutButtonIcon;
    ImageView menuButton;
    Dialog dialog;
    TextView emptyList;
    boolean isClosing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        RunningActivities.addActivity(this);
        RunningActivities.finishBackgroundActivitiesExcept(this);

        initializeValues();

        setupListeners();

        loadPasswords();
    }

    private void setupListeners() {
        scrollView.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                openMenu();
            }

            @Override
            public void onSwipeLeft() {
                closeMenu();
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getX() >= menuLayout.getX()+menuLayout.getWidth())
                    closeMenu();
                return super.onTouch(v, event);
            }
        });

        menuButton.setOnClickListener(view -> {
            openMenu();
        });

        add.setOnClickListener((view) -> {
            closeMenu();
            startActivity(new Intent(HomePage.this, PasswordManagerPage.class));
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
        });

        settings.setOnClickListener((view) -> {
            closeMenu();
            startActivity(new Intent(HomePage.this, SettingsPage.class));
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
        });

        about.setOnClickListener((view) -> {
            closeMenu();
            startActivity(new Intent(HomePage.this, AboutPage.class));
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
        });

        findViewById(R.id.topMenu).setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getX() >= menuLayout.getX()+menuLayout.getWidth())
                    closeMenu();
                return super.onTouch(v, event);
            }
        });
    }

    private void initializeValues() {
        menuLayout = (ConstraintLayout) findViewById(R.id.MenuLayout);
        passwordListLayout = (LinearLayout) findViewById(R.id.PasswordListLayout);
        scrollView = (ScrollView) findViewById(R.id.ScrollView);
        add = (ConstraintLayout) findViewById(R.id.add);
        settings = (ConstraintLayout) findViewById(R.id.settings);
        about = (ConstraintLayout) findViewById(R.id.about);
        menuButton = (ImageView) findViewById(R.id.menuButton);
        menuLayout.setVisibility(View.INVISIBLE);
        addButtonIcon = (ImageView) findViewById(R.id.imageView6);
        emptyList = (TextView) findViewById(R.id.textView9);
    }

    public void loadPasswords(){
        if(User.getPasswords().size() > 0){
            emptyList.setVisibility(View.GONE);
        }

        //iterating over every password and adding it to the passwordListLayout
        for(Password currentPassword : User.getPasswords()) {
            try {
                View passwordLayout = getLayoutInflater().inflate(R.layout.password, null);

                TextView passwordEmail = (TextView) passwordLayout.findViewById(R.id.passwordEmail);
                //maximum text length 14 chars or @
                passwordEmail.setText(currentPassword.getEmail().substring(0, currentPassword.getEmail().length() > 14 ? 14 : currentPassword.getEmail().length()).split("@")[0]);

                ImageView passwordIcon = (ImageView) passwordLayout.findViewById(R.id.passwordIcon);
                Bitmap bmp = BitmapFactory.decodeByteArray(currentPassword.getIcon(), 0, currentPassword.getIcon().length);
                passwordIcon.setImageBitmap(bmp);

                ImageView editButton = (ImageView) passwordLayout.findViewById(R.id.passwordEdit);

                passwordListLayout.addView(passwordLayout);

                editButton.setOnTouchListener(new OnSwipeTouchListener(this){
                    @Override
                    public void onSwipeRight() {
                        openMenu();
                    }

                    @Override
                    public void onSwipeLeft() {
                        closeMenu();
                    }
                });

                editButton.setOnClickListener((view) -> {
                    closeMenu();
                    if(menuLayout.getVisibility() == View.INVISIBLE) {
                        Intent intent = new Intent(this, PasswordManagerPage.class);
                        intent.putExtra("edit", true);
                        intent.putExtra("id", currentPassword.getId());
                        intent.putExtra("email", currentPassword.getEmail());
                        intent.putExtra("password", currentPassword.getPassword());
                        intent.putExtra("extra", currentPassword.getExtra());
                        intent.putExtra("icon", currentPassword.getIcon());
                        intent.putExtra("auto_generate", currentPassword.isAuto_generate());
                        closeMenu();
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                });

                passwordLayout.setOnTouchListener(new OnSwipeTouchListener(this){
                    @Override
                    public void onSwipeRight() {
                        openMenu();
                    }

                    @Override
                    public void onSwipeLeft() {
                        closeMenu();
                    }

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getX() >= menuLayout.getX()+menuLayout.getWidth())
                            closeMenu();
                        return super.onTouch(v, event);
                    }
                });

                passwordLayout.setOnClickListener(view -> {
                    if(menuLayout.getVisibility() == View.INVISIBLE) {
                        Intent intent = new Intent(this, PasswordManagerPage.class);
                        intent.putExtra("view", true);
                        intent.putExtra("id", currentPassword.getId());
                        intent.putExtra("email", currentPassword.getEmail());
                        intent.putExtra("password", currentPassword.getPassword());
                        intent.putExtra("extra", currentPassword.getExtra());
                        intent.putExtra("icon", currentPassword.getIcon());
                        intent.putExtra("auto_generate", currentPassword.isAuto_generate());
                        closeMenu();
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                });
            }catch (Exception exception){
                //in case of an exception, toast and a debug messages are sent
                Toast.makeText(this, ToastMessage.CANT_LOAD_PASSWORD, Toast.LENGTH_LONG).show();
                Log.d("DEBUG", "LOADING PASSWORD ID=" + currentPassword.getId() + " LEAD TO ERROR!");
                Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            }
        }
    }

    private void openMenu(){
        if(menuLayout.getVisibility() != View.INVISIBLE)
            return;

        scrollView.setAlpha(0.4f);
        findViewById(R.id.textView9).setAlpha(0.4f);
        menuLayout.setVisibility(View.VISIBLE);
        menuLayout.bringToFront();
        menuLayout.animate()
                .translationX(0)
                .setListener(null)
                .setDuration(250);
    }

    private void closeMenu(){
        if(isClosing)
            return;

        if(menuLayout.getVisibility() != View.VISIBLE)
            return;

        isClosing = true;
        scrollView.setAlpha(1);
        findViewById(R.id.textView9).setAlpha(1);
        menuLayout.animate()
                .translationX(-menuLayout.getWidth())
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        menuLayout.setVisibility(View.INVISIBLE);
                        isClosing = false;
                    }
                });
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

    boolean first = false;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!first)
            menuLayout.setX(-menuLayout.getWidth());
        first = true;
    }
}