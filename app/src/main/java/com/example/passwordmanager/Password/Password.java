package com.example.passwordmanager.Password;

import android.content.Context;

public class Password {
    String email;
    String password;
    byte[] icon;
    Context context;

    public Password(String email, String password, byte[] icon, Context context){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
}
