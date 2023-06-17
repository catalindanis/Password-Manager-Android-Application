package com.example.passwordmanager.LoginPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class SetupPasswordPage extends AppCompatActivity {

    EditText password;
    EditText confirmPassword;
    Switch showPassword;
    Button continueButton;
    TextView wrongPasswordMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_password_page);

        RunningActivities.addActivity(this);

        initializeValues();

        setupListeners();
    }

    private void setupListeners() {
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //switch that toogles between hiding/not hiding the password
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

        });

        continueButton.setOnClickListener((view) -> {
            //hiding keyboard from phone screen
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if(password.getText().toString().length() > 0 && password.getText().toString().equals(confirmPassword.getText().toString())){
                if(User.setLoginType(this,LoginType.PASSWORD,password.getText().toString()))
                    startActivity(new Intent(SetupPasswordPage.this, HomePage.class));
                    overridePendingTransition(R.anim.zoom_in,R.anim.static_animation);
            }
            else
                wrongPasswordMessage.setText("Passwords might not match or fields are empty");
        });
    }

    private void initializeValues() {
        wrongPasswordMessage = (TextView) findViewById(R.id.setupWrongPassword);

        password = (EditText) findViewById(R.id.setupPassword);
        confirmPassword = (EditText) findViewById(R.id.setupPasswordConfirm);

        showPassword = (Switch) findViewById(R.id.switch1);

        continueButton = (Button) findViewById(R.id.setupPasswordContinue);
    }
}