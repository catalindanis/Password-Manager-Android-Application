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
        String statement = "CREATE TABLE PASSWORDS (id int, email varchar(255), password varchar(255), extra varchar(255), icon blob, auto_generate number(1))";
        db.execSQL(statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}