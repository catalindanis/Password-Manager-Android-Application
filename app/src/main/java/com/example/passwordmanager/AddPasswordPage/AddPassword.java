package com.example.passwordmanager.AddPasswordPage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.HomePage.HomePage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddPassword extends AppCompatActivity {

    RelativeLayout uploadIconButton;
    ImageButton uploadIconButtonIcon;
    EditText email;
    EditText password;
    Switch showPassword;
    Button addButton;
    Uri icon = null;
    TextView wrongPasswordMessage;
    ImageView spinnerArrow;
    Spinner standardIconList;

    TextView selectedFile;

    ArrayAdapter<CharSequence> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);

        RunningActivities.addActivity(this);

        initializeValues();

        setupListeners();
    }

    private void setupListeners() {

        //choosing an image from galley as icon
        uploadIconButton.setOnClickListener((view) -> {
            Intent gallery = new Intent(Intent.ACTION_PICK);
            gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, 1000);
        });

        uploadIconButtonIcon.setOnClickListener((view) -> {
            uploadIconButton.performClick();
        });

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

            if(email.getText().toString().length() == 0 || password.getText().toString().length() == 0) {
                wrongPasswordMessage.setText("Make sure that none of a field is empty!");
                return;
            }

            //transforming URI image in an input stream, re-transforming it into a byte array
            byte[] inputData = null;
            try {
                InputStream iStream = getContentResolver().openInputStream(icon);
                inputData = getBytes(iStream);

            }catch (Exception exception){
                Toast.makeText(this, ToastMessage.BAD_IMAGE, Toast.LENGTH_SHORT).show();
                Log.d("COMMENT","CAN'T TRANSFORM IMAGE IN BYTE ARRAY!");
            }

            //adding the password in database and finishing all activities
            //and restarting HomePage activity so that the new password will be also loaded
            if(User.addPassword(this,email.getText().toString(),password.getText().toString(),inputData)) {
                RunningActivities.finishAllActivities();
                startActivity(new Intent(AddPassword.this, HomePage.class));
            }
        });

        spinnerArrow.setOnClickListener((view) -> {
            standardIconList.performClick();
        });
        standardIconList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("COMMENT","NOTHING");
            }
        });
    }

    private void initializeValues() {
        uploadIconButton = (RelativeLayout) findViewById(R.id.uploadIcon);
        email = (EditText) findViewById(R.id.addUsername);
        password = (EditText) findViewById(R.id.addPassword);
        showPassword = (Switch) findViewById(R.id.switch2);
        addButton = (Button) findViewById(R.id.addPasswordSave);
        uploadIconButtonIcon = (ImageButton) findViewById(R.id.uploadIconButtonIcon);
        wrongPasswordMessage = (TextView) findViewById(R.id.addEmptyFields);
        spinnerArrow = (ImageView) findViewById(R.id.spinnerArrow);
        standardIconList = (Spinner) findViewById(R.id.spinner);
        standardIconList.setAdapter(ArrayAdapter.createFromResource(this,R.array.iconList_beforeClick, R.layout.spinner_item));
        selectedFile = (TextView) findViewById(R.id.selectedFile);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //storing the image from the gallery in variable icon
        if(resultCode == RESULT_OK){
            if(requestCode == 1000){
                icon = data.getData();
                selectedFile.setText("Selected image: "+icon.getLastPathSegment());
            }
        }

    }
}