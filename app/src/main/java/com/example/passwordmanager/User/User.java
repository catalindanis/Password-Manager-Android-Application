package com.example.passwordmanager.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.Password.Password;

import java.util.List;

public class User {
    private static LoginType loginType = LoginType.NULL;
    private static List<Password> passwordList;
    private static final String LOGIN_TYPE_TABLE = "login_type";
    private static final String FIRST_TIME_TABLE = "first_time";
    private static final String LOGIN_PASSWORD_TABLE = "login_password";
    private static final String PREFS_NAME = "MyPrefsFile";

    public static boolean setLoginType(Context context,LoginType type){
        if(type == LoginType.PASSWORD)
            return false;
        try {
            loginType = type;
            Database db = new Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("login_type", loginType.name());
            database.insert(LOGIN_TYPE_TABLE, null, cv);

            Log.d("COMMENT","SETTED UP USER LOGIN TYPE TO " + type.name());

            return true;
        }catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","SET USER LOGIN TYPE LEAD TO ERROR!");
            return false;
        }
    }

    public static boolean setLoginType(Context context,LoginType type, String password){
        if(type == LoginType.NULL || type == LoginType.BIOMETRICS)
            return false;
        try {
            loginType = type;
            Database db = new Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues login_type_value = new ContentValues();
            login_type_value.put("login_type", loginType.name());

            removeLoginType(context);
            database.insert(LOGIN_TYPE_TABLE, null, login_type_value);
            Log.d("COMMENT","SETTED UP USER LOGIN TYPE TO " + type.name());

            ContentValues password_value = new ContentValues();
            password_value.put("password",password);

            removeLoginPassword(context);
            database.insert(LOGIN_PASSWORD_TABLE,null,password_value);
            Log.d("COMMENT","SETTED UP USER LOGIN PASSWORD");
            return true;
        }catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","SET USER LOGIN TYPE OR LOGIN PASSWORD LEAD TO ERROR!");
            return false;
        }
    }

    public static LoginType getLoginType(Context context){
        if(loginType == LoginType.NULL) {
            try {
                Database db = new Database(context);
                SQLiteDatabase database = db.getReadableDatabase();

                String statement = "SELECT login_type FROM " + LOGIN_TYPE_TABLE;
                Cursor cursor = database.rawQuery(statement, null);

                if (cursor.moveToFirst()) {
                    String value = cursor.getString(0);
                    loginType = LoginType.fromString(value);
                }

                cursor.close();
                database.close();

                Log.d("COMMENT","CURRENT USER LOGIN TYPE IS " + loginType.name());
            } catch (Exception exception) {
                Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
                Log.d("COMMENT","GET USER LOGIN TYPE LEAD TO ERROR!");
            }
        }
        return loginType;
    }

    public static void removeLoginType(Context context){
        try {
            Database db = new Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            String statement = "DROP TABLE " + LOGIN_TYPE_TABLE;
            database.execSQL(statement);

            statement = "CREATE TABLE " + LOGIN_TYPE_TABLE + "(login_type varchar(255))";
            database.execSQL(statement);
            Log.d("COMMENT","REMOVED USER LOGIN TYPE");
        }catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","REMOVE USER LOGIN TYPE LEAD TO ERROR!");
        }
    }

    public static void removeLoginPassword(Context context){
        try {
            Database db = new Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            String statement = "DROP TABLE " + LOGIN_PASSWORD_TABLE;
            database.execSQL(statement);

            statement = "CREATE TABLE " + LOGIN_PASSWORD_TABLE + "(password varchar(255))";
            database.execSQL(statement);
            Log.d("COMMENT","REMOVED USER LOGIN PASSWORD");
        }catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","REMOVE USER LOGIN PASSWORD LEAD TO ERROR!");
        }
    }

    public static boolean verifyLoginPassword(Context context,String enteredPassword) {
        if(getLoginPassword(context).equals(enteredPassword)) {
            Log.d("COMMENT","LOGIN BY PASSWORD SUCCESFULLY");
            return true;
        }
        Log.d("COMMENT","PASSWORDS NOT MATCH");
        return false;
    }

    private static String getLoginPassword(Context context) {
        String password = new String();
        try {
            Database db = new Database(context);
            SQLiteDatabase database = db.getReadableDatabase();

            String statement = "SELECT password FROM " + LOGIN_PASSWORD_TABLE;
            Cursor cursor = database.rawQuery(statement, null);

            if (cursor.moveToFirst()) {
                password = cursor.getString(0);
            }

            cursor.close();
            database.close();

            Log.d("COMMENT","RETURNED USER LOGIN PASSWORD SUCCESFULLY ");
        }catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","GET USER LOGIN PASSWORD LEAD TO ERROR!");
        }
        return password;
    }

    public static boolean isFirstTime(Context context){
        boolean value = true;
        try {
            Database db = new Database(context);
            SQLiteDatabase database = db.getReadableDatabase();

            String statement = "SELECT first_time FROM " + FIRST_TIME_TABLE;
            Cursor cursor = database.rawQuery(statement, null);

            if (cursor.moveToFirst()) {
                value = cursor.getInt(0) == 1 ? true : false;
            }

            cursor.close();
            database.close();

            Log.d("COMMENT","CURRENT USER FIRST TIME IS " + (value ? "TRUE" : "FALSE"));
        }catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","GET USER FIRST TIME LEAD TO ERROR!");
        }
        return value;
    }

    public static void setFirstTime(Context context, boolean firstTime){
        try {
            Database db = new Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("first_time", firstTime ? 1 : 0);
            database.insert(FIRST_TIME_TABLE, null, cv);
            Log.d("COMMENT","CHANGED USER FIRST TIME TO " + (firstTime ? "TRUE" : "FALSE"));
        }catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","CHANGED USER FIRST TIME LEAD TO ERROR!");
        }
    }
}

