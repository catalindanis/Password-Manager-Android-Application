package com.example.passwordmanager.AddPasswordPage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.Password.Password;
import com.example.passwordmanager.R;
import com.example.passwordmanager.SettingsPage.Settings;
import com.example.passwordmanager.User.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddPassword extends AppCompatActivity {

    RelativeLayout uploadIconButton;
    EditText email;
    EditText password;
    Switch showPassword;
    Button addButton;
    Uri icon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        initializeValues();

        setupListeners();
    }

    private void setupListeners() {

        uploadIconButton.setOnClickListener((view) -> {
            Intent gallery = new Intent(Intent.ACTION_PICK);
            gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, 1000);
        });

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

        addButton.setOnClickListener((view) -> {
            try {
                InputStream iStream = getContentResolver().openInputStream(icon);
                byte[] inputData = getBytes(iStream);
                User.addPassword(this,email.getText().toString(),password.getText().toString(),inputData);
                RunningActivities.finishAllActivities();
                startActivity(new Intent(AddPassword.this,HomePage.class));
            }catch (Exception exception){
                //throw message
            }
        });
    }

    private void initializeValues() {
        uploadIconButton = (RelativeLayout) findViewById(R.id.uploadIcon);
        email = (EditText) findViewById(R.id.addUsername);
        password = (EditText) findViewById(R.id.addPassword);
        showPassword = (Switch) findViewById(R.id.switch2);
        addButton = (Button) findViewById(R.id.addPasswordSave);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == 1000){
                icon = data.getData();
            }
        }

    }
}