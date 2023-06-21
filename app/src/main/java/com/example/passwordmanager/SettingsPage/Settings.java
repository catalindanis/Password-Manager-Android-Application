package com.example.passwordmanager.SettingsPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.passwordmanager.AddPasswordPage.PasswordManagerPage;
import com.example.passwordmanager.Config.DialogBehaviour;
import com.example.passwordmanager.Config.DialogPrompt;
import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.LoadingScreen.LoadingScreen;
import com.example.passwordmanager.LoginPage.SetupLoginPage;
import com.example.passwordmanager.LoginPage.SetupPasswordPage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

public class Settings extends AppCompatActivity {

    ConstraintLayout removeAllPasswords;
    ConstraintLayout changeLoginPassword;
    ConstraintLayout changeLoginType;
    ConstraintLayout runTroubleshooter;
    ConstraintLayout downloadPasswords;
    ConstraintLayout eraseAllData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RunningActivities.addActivity(this);

        initializeValues();

        setupListeners();

    }

    private void setupListeners() {

        removeAllPasswords.setOnClickListener(view -> {
            removeAllPasswords.animate().setDuration(500).translationX(10).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    removeAllPasswords.animate().setDuration(500).translationX(-10);
                }
            });
            DialogPrompt dialogPrompt = new DialogPrompt(this, new DialogBehaviour() {
                @Override
                public void onYesClick() {
                    User.removePasswords(getApplicationContext());
                    onBackPressed();
                }

                @Override
                public void onNoClick() {

                }
            });
            dialogPrompt.show();
        });

        if(User.getLoginType(this) == LoginType.PASSWORD) {
            changeLoginPassword.setOnClickListener(view -> {
                changeLoginPassword.animate().setDuration(500).translationX(10).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        changeLoginPassword.animate().setDuration(500).translationX(-10);
                    }
                });

                startActivity(new Intent(Settings.this, SetupPasswordPage.class));
            });
        }

        changeLoginType.setOnClickListener(view -> {
            changeLoginType.animate().setDuration(500).translationX(10).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    changeLoginType.animate().setDuration(500).translationX(-10);
                }
            });


            startActivity(new Intent(Settings.this, SetupLoginPage.class).putExtra("override",false));
        });

        runTroubleshooter.setOnClickListener(view -> {
            runTroubleshooter.animate().setDuration(500).translationX(10).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    runTroubleshooter.animate().setDuration(500).translationX(-10);
                }
            });
        });

        downloadPasswords.setOnClickListener(view -> {
            downloadPasswords.animate().setDuration(500).translationX(10).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    downloadPasswords.animate().setDuration(500).translationX(-10);
                }
            });
        });

        eraseAllData.setOnClickListener(view -> {
            eraseAllData.animate().setDuration(500).translationX(10).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    eraseAllData.animate().setDuration(500).translationX(-10);
                }
            });

            DialogPrompt dialogPrompt = new DialogPrompt(this, new DialogBehaviour() {
                @Override
                public void onYesClick() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            User.eraseData(getApplicationContext());
                            RunningActivities.finishAllActivities();
                            startActivity(new Intent(Settings.this, LoadingScreen.class));
                        }
                    }, 1000);
                }

                @Override
                public void onNoClick() {

                }
            });
            dialogPrompt.show();
        });

        findViewById(R.id.backButton).setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void initializeValues() {
        removeAllPasswords = (ConstraintLayout) findViewById(R.id.remove_all_passwords);
        changeLoginPassword = (ConstraintLayout) findViewById(R.id.change_login_password);
        changeLoginType = (ConstraintLayout) findViewById(R.id.change_login_type);
        runTroubleshooter = (ConstraintLayout) findViewById(R.id.run_troubleshooter);
        downloadPasswords = (ConstraintLayout) findViewById(R.id.download_passwords);
        eraseAllData = (ConstraintLayout) findViewById(R.id.erase_all_data);

        if(User.getLoginType(this) != LoginType.PASSWORD) {
            changeLoginPassword.setAlpha(0.5f);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Settings.this, HomePage.class));
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
        RunningActivities.finishAllActivities();
    }
}