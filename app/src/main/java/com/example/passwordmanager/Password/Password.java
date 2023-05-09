package com.example.passwordmanager.Password;

import android.net.Uri;
import android.widget.ImageView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Passwords")
public class Password {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "icon")
    private Uri icon;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Uri getIcon() {
        return icon;
    }

    public void setIcon(Uri icon) {
        this.icon = icon;
    }

    public Password(String username, String password, Uri icon){
        this.username = username;
        this.password = password;
        this.icon = icon;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
