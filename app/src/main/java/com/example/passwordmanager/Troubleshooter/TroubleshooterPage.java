package com.example.passwordmanager.Troubleshooter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passwordmanager.Config.Error;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.Config.Troubleshooter;
import com.example.passwordmanager.R;

public class TroubleshooterPage extends AppCompatActivity {
    LinearLayout errorsListLayout;
    TextView errorsCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troubleshooter_page);

        initializeValues();

        setupListeners();

        loadErrors();
    }

    private void loadErrors() {
        //iterating over every password and adding it to the passwordListLayout
        for(Error error : Troubleshooter.getErrors()) {
            try {
                View errorLayout = getLayoutInflater().inflate(R.layout.error, null);

                TextView errorMessage = (TextView) errorLayout.findViewById(R.id.errorMessage);
                errorMessage.setText(error.getMessage());

                TextView errorSuggestion = (TextView) errorLayout.findViewById(R.id.errorSuggestion);
                errorSuggestion.setText(error.getSuggestion());

                errorsListLayout.addView(errorLayout);
            }catch (Exception exception){
                //in case of an exception, toast and a debug messages are sent
                Toast.makeText(this, ToastMessage.CANT_LOAD_PASSWORD, Toast.LENGTH_LONG).show();
                Log.d("DEBUG", "LOADING ONE OF THE ERRORS LEAD TO ANOTHER ERROR!");
                Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            }
        }
    }

    private void setupListeners() {
        findViewById(R.id.backButton).setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void initializeValues() {
        errorsListLayout = (LinearLayout) findViewById(R.id.errorsListLayout);
        errorsCounter = (TextView) findViewById(R.id.errorsCounter);
        if(Troubleshooter.getErrors().size() > 0)
            if(Troubleshooter.getErrors().size() == 1)
                errorsCounter.setText("1 ERROR FOUND");
            else errorsCounter.setText(Troubleshooter.getErrors().size() + " ERRORS FOUND");
        else {
            errorsCounter.setText("NO ERRORS FOUND!");
            errorsCounter.setTextColor(getColor(R.color.app_green));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
}