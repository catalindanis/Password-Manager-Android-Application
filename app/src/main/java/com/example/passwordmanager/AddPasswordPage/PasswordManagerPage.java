package com.example.passwordmanager.AddPasswordPage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passwordmanager.Config.DialogBehaviour;
import com.example.passwordmanager.Config.DialogPrompt;
import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.Password.Password;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PasswordManagerPage extends AppCompatActivity {
    EditText email;
    EditText password;
    Switch showPassword;
    Button addButton;
    Uri icon = null;
    TextView wrongFieldsMessage;
    ImageView spinnerArrow;
    Spinner standardIconList;
    ArrayAdapter<CharSequence> arrayAdapter;
    String preInstalledIcon = null;
    ImageView deleteButton;
    byte[] inputData = null;
    ImageView copyEmail;
    ImageView copyPassword;
    ImageView passwordIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);
        RunningActivities.addActivity(this);

        initializeValues();

        setupListeners();

        try{
            //if boolean 'edit' was passed, it means that password edit config is needed
            if(getIntent().getExtras().getBoolean("edit")) {
                initializeEditValues();
                setupEditListeners();
            }
        }catch (Exception exception){}

        try{
            //if boolean 'view' was passed, it means that password edit config is needed
            if(getIntent().getExtras().getBoolean("view")) {
                initializeViewValues();
                setupViewListeners();
            }
        }catch (Exception exception){}
    }

    private void setupListeners() {
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //switch that toogles between hiding/not hiding the password
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
            //hiding keyboard from phone screen
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            //check if all fields are not empty
            if(email.getText().toString().length() == 0 || password.getText().toString().length() == 0) {
                wrongFieldsMessage.setText("Make sure that none of a field is empty!");
                return;
            }

            //if preInstalledIcon != null and preInstalledIcon == Auto => auto generate iocon by first letter of email
            //      else if preInstalledIcon != null and preInstalledIcon != Auto => get pre-installed icon
            //else preInstalledIcon == null => auto generate iocon by first letter of email
            int auto_generate = 0;
            if(preInstalledIcon != null){
                //generating auto icon
                if(preInstalledIcon.equals("Auto")){
                    icon = User.getAutoIcon(this, email);
                    auto_generate = 1;
                    try {
                        InputStream iStream = getContentResolver().openInputStream(icon);
                        inputData = getBytes(iStream);
                    } catch (Exception exception) {
                        //in case of an exception, toast and a debug messages are sent
                        Toast.makeText(this, ToastMessage.CANT_GENERATE_ICON, Toast.LENGTH_LONG).show();
                        Log.d("DEBUG", "AUTO GENERATE ICON letter=" + email.getText().toString().charAt(0) + " LEAD TO ERROR!");
                        Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                    }
                }
                else {
                        //getting selected pre-installed icon
                        icon = User.getPreinstalledIcon(this, preInstalledIcon);
                        if (icon != null) {
                            try {
                                InputStream iStream = getContentResolver().openInputStream(icon);
                                inputData = getBytes(iStream);
                                } catch (Exception exception) {
                                    //in case of an exception, toast and a debug messages are sent
                                    Toast.makeText(this, ToastMessage.CORRUPTED_PRE_INSTALLED_ICON, Toast.LENGTH_LONG).show();
                                    Log.d("DEBUG", "PRE INSTALLED ICON name=" + preInstalledIcon + " MIGHT BE CORRUPTED!");
                                    Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                                }
                            }
                    }
                }
                else{
                    //generating auto icon
                    auto_generate = 1;
                    icon = User.getAutoIcon(this,email);
                    try {
                        InputStream iStream = getContentResolver().openInputStream(icon);
                        inputData = getBytes(iStream);
                    } catch (Exception exception) {
                        //in case of an exception, toast and a debug messages are sent
                        Toast.makeText(this, ToastMessage.CORRUPTED_PRE_INSTALLED_ICON, Toast.LENGTH_LONG).show();
                        Log.d("DEBUG", "PRE INSTALLED ICON name=" + preInstalledIcon + " MIGHT BE CORRUPTED!");
                        Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                    }
                }

            //adding the password and finishing all activities
            //restarting HomePage activity so that the new password will be also loaded
            User.addPassword(this,new Password(email.getText().toString(),password.getText().toString(),inputData,auto_generate));
            startActivity(new Intent(PasswordManagerPage.this, HomePage.class));
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            RunningActivities.finishAllActivities();
        });

        spinnerArrow.setOnClickListener((view) -> {
            standardIconList.performClick();
        });

        standardIconList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().equals("Change icon"))
                    preInstalledIcon = parent.getItemAtPosition(position).toString();

                //changing password icon in real time, if the pre installed icon option is changed
                if(preInstalledIcon != null) {
                    if(preInstalledIcon.equals("Auto")){
                        if (email.getText().toString().length() > 0) {
                            icon = User.getAutoIcon(getApplicationContext(), email);
                            try {
                                InputStream iStream = getContentResolver().openInputStream(icon);
                                inputData = getBytes(iStream);
                                Bitmap bmp = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
                                passwordIcon.setImageBitmap(bmp);
                                passwordIcon.setVisibility(View.VISIBLE);

                            } catch (Exception exception) {
                                //in case of an exception, toast and a debug messages are sent
                                Toast.makeText(getApplicationContext(), ToastMessage.CORRUPTED_PRE_INSTALLED_ICON, Toast.LENGTH_LONG).show();
                                Log.d("DEBUG", "PRE INSTALLED ICON name=" + preInstalledIcon + " MIGHT BE CORRUPTED!");
                                Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                            }
                        }
                    }
                    else {
                        icon = User.getPreinstalledIcon(getApplicationContext(), preInstalledIcon);
                        if (icon != null) {
                            try {
                                InputStream iStream = getContentResolver().openInputStream(icon);
                                inputData = getBytes(iStream);
                                Bitmap bmp = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
                                passwordIcon.setImageBitmap(bmp);
                                animate(passwordIcon);
                            } catch (Exception exception) {
                                //in case of an exception, toast and a debug messages are sent
                                Toast.makeText(getApplicationContext(), ToastMessage.CORRUPTED_PRE_INSTALLED_ICON, Toast.LENGTH_LONG).show();
                                Log.d("DEBUG", "PRE INSTALLED ICON name=" + preInstalledIcon + " MIGHT BE CORRUPTED!");
                                Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //after first touch of the spinner menu, removing 'Change icon' option
        standardIconList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                standardIconList.setAdapter(ArrayAdapter.createFromResource(getApplicationContext(),R.array.iconList_afterClick, R.layout.spinner_item));
                return false;
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            CharSequence sequence;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                sequence = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //changing password icon in real time, if the first letter changes
                if (preInstalledIcon == null) {
                    if (email.getText().toString().length() > 0) {
                        icon = User.getAutoIcon(getApplicationContext(), email);
                        try {
                            InputStream iStream = getContentResolver().openInputStream(icon);
                            inputData = getBytes(iStream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
                            passwordIcon.setImageBitmap(bmp);
                            passwordIcon.setVisibility(View.VISIBLE);

                        } catch (Exception exception) {
                            //in case of an exception, toast and a debug messages are sent
                            Toast.makeText(getApplicationContext(), ToastMessage.CORRUPTED_PRE_INSTALLED_ICON, Toast.LENGTH_LONG).show();
                            Log.d("DEBUG", "PRE INSTALLED ICON name=" + preInstalledIcon + " MIGHT BE CORRUPTED!");
                            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                        }
                    }
                } else if (preInstalledIcon.equals("Auto")) {
                    if (email.getText().toString().length() > 0) {
                        icon = User.getAutoIcon(getApplicationContext(), email);
                        try {
                            InputStream iStream = getContentResolver().openInputStream(icon);
                            inputData = getBytes(iStream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
                            passwordIcon.setImageBitmap(bmp);
                            passwordIcon.setVisibility(View.VISIBLE);

                        } catch (Exception exception) {
                            //in case of an exception, toast and a debug messages are sent
                            Toast.makeText(getApplicationContext(), ToastMessage.CORRUPTED_PRE_INSTALLED_ICON, Toast.LENGTH_LONG).show();
                            Log.d("DEBUG", "PRE INSTALLED ICON name=" + preInstalledIcon + " MIGHT BE CORRUPTED!");
                            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                        }
                    }
                }
            }
        });
    }

    private void setupEditListeners() {
        addButton.setOnClickListener((view) -> {

            //if icon != null means that an image was uploaded from gallery
            //else if preInstalledIcon != null and preInstalledIcon == Auto => auto generate iocon by first letter of new email
            //      else if preInstalledIcon != null and preInstalledIcon != Auto => get pre-installed icon
            //else if autoGenerate > 0 (is true) => auto generate iocon by first letter of new email
            //else autoGenerate <= 0 (is false) && icon == null && preInstalledIcon == null => no changes to the icon
            int auto_generate = getIntent().getExtras().getInt("auto_generate");
            if (preInstalledIcon != null) {
                //generating auto icon
                if(preInstalledIcon.equals("Auto")){
                    icon = User.getAutoIcon(this, email);
                    auto_generate = 1;
                    try {
                        InputStream iStream = getContentResolver().openInputStream(icon);
                        inputData = getBytes(iStream);

                    } catch (Exception exception) {
                        //in case of an exception, toast and a debug messages are sent
                        Toast.makeText(this, ToastMessage.CANT_GENERATE_ICON, Toast.LENGTH_LONG).show();
                        Log.d("DEBUG", "AUTO GENERATE ICON letter=" + email.getText().toString().charAt(0) + " LEAD TO ERROR!");
                        Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                    }
                }
                else {
                    //getting selected pre-installed icon
                    icon = User.getPreinstalledIcon(this, preInstalledIcon);
                    if (icon != null) {
                        try {
                            InputStream iStream = getContentResolver().openInputStream(icon);
                            inputData = getBytes(iStream);
                            auto_generate = 0;
                        } catch (Exception exception) {
                            //in case of an exception, toast and a debug messages are sent
                            Toast.makeText(this, ToastMessage.CORRUPTED_PRE_INSTALLED_ICON, Toast.LENGTH_LONG).show();
                            Log.d("DEBUG", "PRE INSTALLED ICON name=" + preInstalledIcon + " MIGHT BE CORRUPTED!");
                            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                        }
                    }
                }
            } else if(auto_generate > 0) {
                //generating auto icon
                icon = User.getAutoIcon(this, email);
                auto_generate = 1;
                try {
                    InputStream iStream = getContentResolver().openInputStream(icon);
                    inputData = getBytes(iStream);

                } catch (Exception exception) {
                    //in case of an exception, toast and a debug messages are sent
                    Toast.makeText(this, ToastMessage.CANT_GENERATE_ICON, Toast.LENGTH_LONG).show();
                    Log.d("DEBUG", "AUTO GENERATE ICON letter=" + email.getText().toString().charAt(0) + " LEAD TO ERROR!");
                    Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                }
            }

            //updating the password and finishing all activities
            //restarting HomePage activity so that the new password will be also loaded
            User.updatePassword(this, getIntent().getExtras().getInt("id"), new Password(email.getText().toString(),password.getText().toString(),inputData,auto_generate));
            startActivity(new Intent(PasswordManagerPage.this, HomePage.class));
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            RunningActivities.finishAllActivities();
        });

        deleteButton.setOnClickListener((view) -> {
            //displaying a custom dialog for action confirmation
            DialogPrompt dialogPrompt = new DialogPrompt(this, new DialogBehaviour() {
                @Override
                public void onYesClick() {
                    //removing the password and finishing all activities
                    //restarting HomePage activity so that the new password will be unloaded
                    User.removePassword(getApplicationContext(), getIntent().getExtras().getInt("id"));
                    startActivity(new Intent(PasswordManagerPage.this, HomePage.class));
                    overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                    RunningActivities.finishAllActivities();
                }

                @Override
                public void onNoClick() {
                }
            });

            dialogPrompt.show();
        });
    }

    private void setupViewListeners() {

        addButton.setOnClickListener((view) -> {
            onBackPressed();
        });

        copyEmail.setOnClickListener(view -> {
            setClipboard(this,email.getText().toString());
            Toast.makeText(this, ToastMessage.COPIED_TO_CLIPBOARD, Toast.LENGTH_SHORT).show();
        });

        copyPassword.setOnClickListener(view -> {
            setClipboard(this,password.getText().toString());
            Toast.makeText(this, ToastMessage.COPIED_TO_CLIPBOARD, Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeValues() {
        email = (EditText) findViewById(R.id.addUsername);
        password = (EditText) findViewById(R.id.addPassword);
        showPassword = (Switch) findViewById(R.id.switch2);
        addButton = (Button) findViewById(R.id.addPasswordSave);
        wrongFieldsMessage = (TextView) findViewById(R.id.addEmptyFields);
        spinnerArrow = (ImageView) findViewById(R.id.spinnerArrow);
        standardIconList = (Spinner) findViewById(R.id.spinner);
        standardIconList.setAdapter(ArrayAdapter.createFromResource(this,R.array.iconList_beforeClick, R.layout.spinner_item));
        deleteButton = findViewById(R.id.deleteButton);
        copyEmail = findViewById(R.id.copyEmail);
        copyPassword = findViewById(R.id.copyPassword);
        passwordIcon = findViewById(R.id.passwordIcon);
        deleteButton.setVisibility(View.INVISIBLE);
        copyEmail.setVisibility(View.INVISIBLE);
        copyPassword.setVisibility(View.INVISIBLE);
        passwordIcon.setVisibility(View.INVISIBLE);
    }

    private void initializeEditValues() {
        Bundle bundle = getIntent().getExtras();

        deleteButton.setVisibility(View.VISIBLE);
        addButton.setText("SAVE");

        email.setText(bundle.getString("email"));
        password.setText(bundle.getString("password"));
        inputData = bundle.getByteArray("icon");

        copyEmail.setVisibility(View.INVISIBLE);
        copyPassword.setVisibility(View.INVISIBLE);

        Bitmap bmp = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
        passwordIcon.setImageBitmap(bmp);
        passwordIcon.setVisibility(View.VISIBLE);
    }

    private void initializeViewValues() {
        Bundle bundle = getIntent().getExtras();
        addButton.setText("BACK");

        copyEmail.setVisibility(View.VISIBLE);
        copyPassword.setVisibility(View.VISIBLE);
        passwordIcon.setVisibility(View.VISIBLE);

        email.setText(bundle.getString("email"));
        password.setText(bundle.getString("password"));
        inputData = bundle.getByteArray("icon");

        email.setEnabled(false);
        password.setEnabled(false);

        wrongFieldsMessage.setVisibility(View.INVISIBLE);
        spinnerArrow.setVisibility(View.INVISIBLE);
        standardIconList.setVisibility(View.INVISIBLE);

        email.animate().translationY(200f).setDuration(0);
        password.animate().translationY(200f).setDuration(0);
        findViewById(R.id.textView3).animate().translationY(200f).setDuration(0);
        findViewById(R.id.textView4).animate().translationY(200f).setDuration(0);
        copyEmail.animate().translationY(200f).setDuration(0);
        copyPassword.animate().translationY(200f).setDuration(0);
        showPassword.animate().translationY(200f).setDuration(0);
        passwordIcon.animate().translationY(100f).setDuration(0);

        Bitmap bmp = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
        passwordIcon.setImageBitmap(bmp);
    }

    private void animate(ImageView icon){
        icon.setVisibility(View.INVISIBLE);
        icon.animate().setDuration(200).alpha(0.4f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                icon.animate().setDuration(0).alpha(1);
                icon.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        //transforming the inputStream image in a byte array
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PasswordManagerPage.this, HomePage.class));
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
        RunningActivities.finishAllActivities();
    }
}