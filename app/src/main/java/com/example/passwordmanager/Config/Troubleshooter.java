package com.example.passwordmanager.Config;

import com.example.passwordmanager.Config.Error;

import java.util.ArrayList;
import java.util.List;

public class Troubleshooter {
    public static List<Error> errors = new ArrayList<>();

    public static List<Error> getErrors(){
        return errors;
    }

    public static void addError(Error error){
        errors.add(error);
    }
}
