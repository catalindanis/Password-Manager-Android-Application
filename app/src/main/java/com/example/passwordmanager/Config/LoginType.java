package com.example.passwordmanager.Config;

public enum LoginType {
    PASSWORD,
    BIOMETRICS,
    NULL;

    public static LoginType fromString(String string){
        switch (string){
            case "PASSWORD":
                return PASSWORD;
            case "BIOMETRICS":
                return BIOMETRICS;
            default:
                return NULL;
        }
    }
}
