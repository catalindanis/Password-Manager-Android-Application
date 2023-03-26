package com.example.passwordmanager.Config;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RunningActivities {
    private static List<AppCompatActivity> currentActivities = new ArrayList<>();

    public static void addActivity(AppCompatActivity activity){
        currentActivities.add(activity);
    }

    public static void finishBackgroundActivitiesExcept(AppCompatActivity... currentRunningActivity){
        for(AppCompatActivity activity : currentActivities){
            boolean remove = true;
            for(AppCompatActivity exceptActivity : currentRunningActivity)
                if(activity.getClass().getSimpleName().equals(exceptActivity.getClass().getSimpleName()))
                    remove = false;
            if(remove)
                activity.finishAfterTransition();
        }
    }
}
