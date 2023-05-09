package com.example.passwordmanager.Password;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Passwords_Database extends SQLiteOpenHelper {
    public Passwords_Database(@Nullable Context context) {
        super(context,"PASSWORDS.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String statement = "CREATE TABLE PASSWORDS (email varchar(255), password varchar(255), icon blob)";
        db.execSQL(statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}