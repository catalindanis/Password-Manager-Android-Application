package com.example.passwordmanager.Password;

import android.content.Context;

import com.example.passwordmanager.User.User;

import java.util.Random;

public class Password {
    private int id;

    private String email;
    private String password;
    private byte[] icon;
    private int auto_generate;

    public Password(int id, String email, String password, byte[] icon, int auto_generate){
        this.id = id;
        this.email = email;
        this.password = password;
        this.icon = icon;
        this.auto_generate = auto_generate;
    }

    public Password(String email, String password, byte[] icon, int auto_generate){
        this.id = User.generateId();
        this.email = email;
        this.password = password;
        this.icon = icon;
        this.auto_generate = auto_generate;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int isAuto_generate() {
        return auto_generate;
    }

    public void setAuto_generate(int auto_generate) {
        this.auto_generate = auto_generate;
    }
}
