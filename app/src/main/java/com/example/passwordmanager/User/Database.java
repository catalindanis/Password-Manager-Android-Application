package com.example.passwordmanager.User;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.passwordmanager.Password.Password;
import com.example.passwordmanager.Password.PasswordRepository;

public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context) {
        super(context,"USER.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String statement = "CREATE TABLE login_type (login_type varchar(255))";
        db.execSQL(statement);
        statement = "CREATE TABLE first_time (first_time NUMBER(1))";
        db.execSQL(statement);
        statement = "CREATE TABLE login_password (password varchar(255))";
        db.execSQL(statement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}