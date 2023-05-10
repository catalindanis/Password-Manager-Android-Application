package com.example.passwordmanager.Password;

import android.net.Uri;
import android.widget.ImageView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
public class Password {
    private int id;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String password;
    private String email;
    private byte[] icon;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public Password(String email, String password, byte[] icon){
        this.email = email;
        this.password = password;
        if(icon == null)
            this.icon = autoGenerateIcon();
        this.icon = icon;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private byte[] autoGenerateIcon(){
        byte[] icon = email.getBytes();
        return icon;
    }
}
