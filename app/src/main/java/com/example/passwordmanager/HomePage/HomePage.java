package com.example.passwordmanager.HomePage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.passwordmanager.Config.RunningActivities;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.R;
import com.example.passwordmanager.User.User;

public class HomePage extends AppCompatActivity {

    ConstraintLayout menuLayout;
    ConstraintLayout passwordListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        RunningActivities.addActivity(this);
        RunningActivities.finishBackgroundActivitiesExcept(this);

        if(User.isFirstTime(this))
            User.setFirstTime(this,false);

        initializeValues();

        setupListeners();
    }

    private void setupListeners() {

    }

    private void initializeValues() {
        menuLayout = (ConstraintLayout) findViewById(R.id.MenuLayout);
        //passwordListLayout = (ConstraintLayout) findViewById(R.id.PasswordListLayout);

        menuLayout.setVisibility(View.INVISIBLE);
    }


    float x1 = 0,x2 = 0;
    int MIN_DISTANCE = 100;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (deltaX > 0)
                    {
                        menuLayout.setVisibility(View.VISIBLE);
                    }
                    // Right to left swipe action
                    else
                    {
                        menuLayout.setVisibility(View.INVISIBLE);
                    }

                }
                break;
        }
        return super.onTouchEvent(event);
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