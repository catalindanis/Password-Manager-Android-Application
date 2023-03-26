package com.example.passwordmanager.LoginPage;

import static android.media.audiofx.HapticGenerator.isAvailable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.concurrent.Executor;

public class LoginPage extends AppCompatActivity {

    ImageView icon;
    TextView message;
    TextView passwordText;
    EditText password;
    Switch showPassword;
    Button continueButton;
    Button loginBioType;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Executor executor;
    TextView wrongPasswordMessage;
    String toastMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        RunningActivities.addActivity(this);
        RunningActivities.finishBackgroundActivitiesExcept(this);

        initializeValues();

        setupListeners();

        hideElements();

        login(User.getLoginType(this));
    }

    private void setupListeners() {
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

        });

        continueButton.setOnClickListener((view) -> {
            //hiding keyboard from phone screen
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if(User.verifyLoginPassword(this,password.getText().toString())) {
                startActivity(new Intent(LoginPage.this, HomePage.class));
            }
            else
                wrongPasswordMessage.setText("Password is incorrect!");
        });

        loginBioType.setOnClickListener((view) -> {
            if(isAvailable()) {
                executor = ContextCompat.getMainExecutor(LoginPage.this);
                biometricPrompt = new BiometricPrompt(LoginPage.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull @NotNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull @NotNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        startActivity(new Intent(LoginPage.this, HomePage.class));
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

    private void initializeValues() {
        icon = (ImageView) findViewById(R.id.imageView2);
        message = (TextView) findViewById(R.id.textView5);
        wrongPasswordMessage = (TextView) findViewById(R.id.setupWrongPassword1);
        passwordText = (TextView) findViewById(R.id.textView3);
        password = (EditText) findViewById(R.id.setupPassword);
        showPassword = (Switch) findViewById(R.id.switch1);
        continueButton = (Button) findViewById(R.id.setupPasswordContinue);
        loginBioType = (Button) findViewById(R.id.loginBioType1);
    }

    private void login(LoginType loginType){
        switch (loginType){
            case PASSWORD:
                passwordLoginPrompt();
                break;
            case BIOMETRICS:
                biometricsLoginPrompt();
                break;
            default:
                startActivity(new Intent(LoginPage.this,SetupLoginPage.class));
        }
    }

    private void passwordLoginPrompt(){
        passwordText.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        showPassword.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.VISIBLE);
        password.requestFocus();
        //showing keyboard from phone screen
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void biometricsLoginPrompt(){
        message.setY(loginBioType.getY()+200);
        icon.setY(message.getY());
        loginBioType.setVisibility(View.VISIBLE);
        loginBioType.callOnClick();
    }

    private void hideElements() {
        passwordText.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        showPassword.setVisibility(View.INVISIBLE);
        continueButton.setVisibility(View.INVISIBLE);
        loginBioType.setVisibility(View.INVISIBLE);
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