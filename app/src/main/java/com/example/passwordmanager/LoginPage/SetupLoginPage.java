package com.example.passwordmanager.LoginPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class SetupLoginPage extends AppCompatActivity {
    Button loginPasswordType;
    Button loginBioType;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Executor executor;
    TextView chooseLoginType;
    TextView infoMessage;
    String toastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_login_page);

        RunningActivities.addActivity(this);
        RunningActivities.finishBackgroundActivitiesExcept(this);

        initializeValues();

        setupListeners();
    }

    private void setupListeners() {

        loginPasswordType.setOnClickListener((view) -> {
            startActivity(new Intent(SetupLoginPage.this, SetupPasswordPage.class));
        });

        loginBioType.setOnClickListener((view) -> {
            if(isAvailable()) {
                executor = ContextCompat.getMainExecutor(SetupLoginPage.this);
                biometricPrompt = new BiometricPrompt(SetupLoginPage.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull @NotNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull @NotNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        if(User.setLoginType(getApplicationContext(), LoginType.BIOMETRICS))
                            startActivity(new Intent(SetupLoginPage.this, HomePage.class));
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Authentification")
                        .setSubtitle("Login using your fingerprint or face").setDeviceCredentialAllowed(true).setConfirmationRequired(false).build();

                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    private void initializeValues() {
        loginPasswordType = (Button)findViewById(R.id.setupPasswordContinue);
        loginBioType = (Button)findViewById(R.id.loginBioType);

        chooseLoginType = (TextView) findViewById(R.id.textView3);
        infoMessage = (TextView) findViewById(R.id.textView4);
    }

    private boolean isAvailable(){
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                toastMessage = "No biometrics features available on this device";
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                toastMessage = "Biometrics features are currently available";
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                toastMessage = "Need to have at least one fingerprint registered";
                break;
            default:
                toastMessage = "Uknown error! Try restarting or reinstalling the app";
                break;
        }
        Toast.makeText(this,toastMessage,Toast.LENGTH_SHORT).show();
        return false;
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