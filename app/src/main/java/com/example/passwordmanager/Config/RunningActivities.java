package com.example.passwordmanager.Config;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RunningActivities {
    //this class is designed for having control over all running activities and stop them whenever is needed
    //addActivity will add a currently running activity to the currentActivities list
    //finishBackgroundActivitiesExcept will finish all the activities from list excepting the ones that you provide as parameters
    //finishAllActivities will finish all the activities from list
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

    public static void finishAllActivities(){
        for(AppCompatActivity activity : currentActivities){
                activity.finishAfterTransition();
        }
    }
}
