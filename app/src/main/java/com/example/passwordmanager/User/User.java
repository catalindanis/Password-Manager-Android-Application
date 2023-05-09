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
import com.example.passwordmanager.Password.Passwords_Database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class User {
    private static LoginType loginType = LoginType.NULL;
    private static List<Password> passwordList;
    private static final String LOGIN_TYPE_TABLE = "login_type";
    private static final String FIRST_TIME_TABLE = "first_time";
    private static final String LOGIN_PASSWORD_TABLE = "login_password";
    private static final String PASSWORDS_TABLE = "passwords";
    public static boolean addPassword(Context context, String email, String password, byte[] icon){
        try{
            Passwords_Database db = new Passwords_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("email", email);
            cv.put("password", password);
            cv.put("icon",icon);
            database.insert(PASSWORDS_TABLE, null, cv);

            Log.d("COMMENT","ADDED PASSWORD SUCCESFULLY!");
            return true;
        }
        catch (Exception exception){
            Toast.makeText(context, ToastMessage.CANT_ADD_PASSWORD, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","ADD PASSWORD LEAD TO ERROR!");
            return false;
        }
    }

    public static boolean removePasswords(Context context){
        try{
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            String statement = "DROP TABLE " + PASSWORDS_TABLE;
            database.execSQL(statement);

            statement = "CREATE TABLE " + PASSWORDS_TABLE + "(email varchar(255), password varchar(255), icon blob)";
            database.execSQL(statement);

            Log.d("COMMENT","REMOVED PASSWORDS");
            return true;
        }
        catch (Exception exception){
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","REMOVE PASSWORDS LEAD TO ERROR!");
            return false;
        }
    }

    //setLoginType for BIOMETRICS (no password needed)
    public static boolean setLoginType(Context context,LoginType type){
        if(type == LoginType.NULL || type == LoginType.PASSWORD)
            return false;
        try {
            loginType = type;
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            //removing the current loginType (just to be sure that the table
            //has no more than 1 element) and inserting the login type value
            //in it's database table
            removeLoginType(context);
            ContentValues cv = new ContentValues();
            cv.put("login_type", loginType.name());
            database.insert(LOGIN_TYPE_TABLE, null, cv);

            Log.d("COMMENT","SETTED UP USER LOGIN TYPE TO " + type.name());
            return true;
        }catch (Exception exception){
            //in case of an exception, a toast message is thrown, and action is cancelled
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","SET USER LOGIN TYPE LEAD TO ERROR!");
            return false;
        }
    }

    //setLoginType for PASSWORD (password needed)
    public static boolean setLoginType(Context context,LoginType type, String password){
        if(type == LoginType.NULL || type == LoginType.BIOMETRICS)
            return false;
        try {
            loginType = type;
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues login_type_value = new ContentValues();
            login_type_value.put("login_type", loginType.name());

            //removing the current loginType (just to be sure that the table
            //has no more than 1 element) and inserting the login type value
            //in it's database table
            removeLoginType(context);
            database.insert(LOGIN_TYPE_TABLE, null, login_type_value);
            Log.d("COMMENT","SETTED UP USER LOGIN TYPE TO " + type.name());

            //encrypting password using MD5 (Message Digest) algorithm
            String encryptedpassword = new String();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(password.getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            encryptedpassword = s.toString();

            ContentValues password_value = new ContentValues();
            password_value.put("password",encryptedpassword);

            //removing the current loginPassword (just to be sure that the table
            //has no more than 1 element) and inserting the login password encrypted
            //value in it's database table
            removeLoginPassword(context);
            database.insert(LOGIN_PASSWORD_TABLE,null,password_value);
            Log.d("COMMENT","SETTED UP USER LOGIN PASSWORD");
            return true;
        }catch (Exception exception){
            //in case of an exception, a toast message is thrown, and action is cancelled
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","SET USER LOGIN TYPE OR LOGIN PASSWORD LEAD TO ERROR!");
            return false;
        }
    }

    public static LoginType getLoginType(Context context){
        //checks if loginType is null. if it is null, then the value
        //is taken from the database and the loginType variable is changed and returned
        //if it isn't null, then we can directly return it, because it was
        //already readed from the database
        if(loginType == LoginType.NULL) {
            try {
                User_Database db = new User_Database(context);
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
                return loginType;
            } catch (Exception exception) {
                //in case of an exception, a toast message is thrown, and LoginType.NULL is returned
                Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
                Log.d("COMMENT","GET USER LOGIN TYPE LEAD TO ERROR!");
                return LoginType.NULL;
            }
        }
        return loginType;
    }

    public static boolean removeLoginType(Context context){
        //used before updating the loginType.
        //drops and recreates the desired table for the loginType
        try {
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            String statement = "DROP TABLE " + LOGIN_TYPE_TABLE;
            database.execSQL(statement);

            statement = "CREATE TABLE " + LOGIN_TYPE_TABLE + "(login_type varchar(255))";
            database.execSQL(statement);
            Log.d("COMMENT","REMOVED USER LOGIN TYPE");
            return true;
        }catch (Exception exception){
            //in case of an exception, a toast message is thrown, and action is cancelled
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","REMOVE USER LOGIN TYPE LEAD TO ERROR!");
        }
        return false;
    }

    public static boolean removeLoginPassword(Context context){
        //used before updating the loginPassword.
        //drops and recreates the desired table for the loginPassword
        try {
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            String statement = "DROP TABLE " + LOGIN_PASSWORD_TABLE;
            database.execSQL(statement);

            statement = "CREATE TABLE " + LOGIN_PASSWORD_TABLE + "(password varchar(255))";
            database.execSQL(statement);
            Log.d("COMMENT","REMOVED USER LOGIN PASSWORD");
            return true;
        }catch (Exception exception){
            //in case of an exception, a toast message is thrown, and action is cancelled
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","REMOVE USER LOGIN PASSWORD LEAD TO ERROR!");
        }
        return false;
    }

    public static boolean verifyLoginPassword(Context context,String enteredPassword) {
        //encrypting entered password
        String encryptedpassword = new String();
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(enteredPassword.getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            encryptedpassword = s.toString();
        }catch (NoSuchAlgorithmException noSuchAlgorithmException){
            //in case of an exception false is returned
            Log.d("COMMENT","ENCRYPTING USER ENTERED PASSWORD LEAD TO ERROR!");
            return false;
        }

        //verifying the encrypted login password from the database with the one entered by the user
        try {
            if (getLoginPassword(context).equals(encryptedpassword)) {
                Log.d("COMMENT", "LOGIN BY PASSWORD SUCCESFULLY");
                return true;
            }
            Log.d("COMMENT", "PASSWORDS NOT MATCH");
        }catch (NullPointerException nullPointerException){
            Log.d("COMMENT","ENCRYPTED PASSWORD FROM THE DATABASE IS NULL!");
        }
        return false;
    }

    private static String getLoginPassword(Context context) {
        //read the loginPassword from it's database table, and returns it
        try {
            String password = new String();
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getReadableDatabase();

            String statement = "SELECT password FROM " + LOGIN_PASSWORD_TABLE;
            Cursor cursor = database.rawQuery(statement, null);

            if (cursor.moveToFirst()) {
                password = cursor.getString(0);
            }

            cursor.close();
            database.close();

            Log.d("COMMENT","RETURNED USER LOGIN PASSWORD SUCCESFULLY ");
            return password;
        }catch (Exception exception){
            //in case of an exception, a toast message is thrown, and null is returned
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","GET USER LOGIN PASSWORD LEAD TO ERROR!");
        }

        return null;
    }

    public static boolean isFirstTime(Context context){
        try {
            //read the value from it's database table
            //returns true if the user has never setted up a loginType
            //isFirstTime will return false only aftter setting a login method,
            //after first HomePage onCreate
            boolean value = true;
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getReadableDatabase();

            String statement = "SELECT first_time FROM " + FIRST_TIME_TABLE;
            Cursor cursor = database.rawQuery(statement, null);

            if (cursor.moveToFirst()) {
                value = cursor.getInt(0) == 1 ? true : false;
            }

            cursor.close();
            database.close();

            Log.d("COMMENT","CURRENT USER FIRST TIME IS " + (value ? "TRUE" : "FALSE"));
            return value;
        }catch (Exception exception){
            //in case of an exception, a toast message is thrown, and the value true is returned
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","GET USER FIRST TIME LEAD TO ERROR!");
        }
        return true;
    }

    public static void setFirstTime(Context context, boolean firstTime){
        //updates the user isFirstTime variable directly in the database
        try {
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("first_time", firstTime ? 1 : 0);
            database.insert(FIRST_TIME_TABLE, null, cv);
            Log.d("COMMENT","CHANGED USER FIRST TIME TO " + (firstTime ? "TRUE" : "FALSE"));
        }catch (Exception exception){
            //in case of an exception, a toast message is thrown, and the action is cancelled
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            Log.d("COMMENT","CHANGED USER FIRST TIME LEAD TO ERROR!");
        }
    }
}

