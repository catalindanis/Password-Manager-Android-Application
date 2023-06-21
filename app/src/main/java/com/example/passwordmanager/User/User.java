package com.example.passwordmanager.User;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.Config.LoginType;
import com.example.passwordmanager.LoadingScreen.LoadingScreen;
import com.example.passwordmanager.Password.Password;
import com.example.passwordmanager.Password.PasswordList;
import com.example.passwordmanager.Password.Passwords_Database;
import com.example.passwordmanager.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class User {
    private static PasswordList passwordList = new PasswordList();
    private static final String LOGIN_TYPE_TABLE = "login_type";
    private static final String FIRST_TIME_TABLE = "first_time";
    private static final String LOGIN_PASSWORD_TABLE = "login_password";
    private static String loginPassword = new String();
    private static int[] autoGenIcons = new int[100];
    public static void addPassword(Context context, Password password){ passwordList.save(password,context); }

    public static void updatePassword(Context context, int id, Password password){ passwordList.update(context, id, password); }

    public static PasswordList getPasswords(){
        return passwordList;
    }

    public static void removePassword(Context context, int id){
        passwordList.remove(id,context);
    }

    public static void removePasswords(Context context){
        passwordList.removeAll(context);
    }

    public static int generateId(){
        //generating random numbers and verifying not to be one
        //of the other passwords id
        boolean found = false;
        Random random = new Random();
        int id = -1;
        while(!found) {
            id = random.nextInt();
            found = true;
            for (Password password : getPasswords()) {
                if(password.getId() == id) {
                    found = false;
                    break;
                }
            }
        }
        return id;
    }

    public static boolean setLoginType(Context context,LoginType type){
        //setLoginType for BIOMETRICS (no password needed)
        if(type == LoginType.NULL || type == LoginType.PASSWORD)
            return false;
        try {
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues login_type_value = new ContentValues();
            login_type_value.put("login_type", type.name());

            //removing the current loginType (just to be sure that the table
            //has no more than 1 element) and inserting the login type value
            //in it's database table
            removeLoginType(context);
            database.insert(LOGIN_TYPE_TABLE, null, login_type_value);

            Log.d("DEBUG","SET USER LOGIN TYPE TO " + type.name() + " SUCCESSFULLY!");
            return true;
        }catch (Exception exception){
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.SETUP_LOGIN_TYPE_ERROR, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "SET USER LOGIN TYPE TO " + type.name() + " LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            return false;
        }
    }

    public static boolean setLoginType(Context context,LoginType type, String password){
        //setLoginType for PASSWORD (password needed)
        if(type == LoginType.NULL || type == LoginType.BIOMETRICS)
            return false;
        try {
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            ContentValues login_type_value = new ContentValues();
            login_type_value.put("login_type", type.name());

            //removing the current loginType (just to be sure that the table
            //has no more than 1 element) and inserting the login type value
            //in it's database table
            removeLoginType(context);
            database.insert(LOGIN_TYPE_TABLE, null, login_type_value);

            ContentValues password_value = new ContentValues();
            //encrypting login password
            password_value.put("password",encryptData(password));

            //removing the current loginPassword (just to be sure that the table
            //has no more than 1 element) and inserting the login password encrypted
            //value in it's database table
            removeLoginPassword(context);
            database.insert(LOGIN_PASSWORD_TABLE,null,password_value);

            Log.d("DEBUG","SET USER LOGIN TYPE TO " + type.name() + " SUCCESSFULLY!");
            return true;
        }catch (Exception exception){
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.SETUP_LOGIN_TYPE_ERROR, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "SET USER LOGIN TYPE TO " + type.name() + " LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            return false;
        }
    }

    public static LoginType getLoginType(Context context){
        //the value loginType is taken from the database
        try {
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getReadableDatabase();

            String statement = "SELECT login_type FROM " + LOGIN_TYPE_TABLE;
            Cursor cursor = database.rawQuery(statement, null);

            String value = new String();
            if (cursor.moveToFirst()) {
                value = cursor.getString(0);
                setLoginType(context,LoginType.fromString(value));
            }

            cursor.close();
            database.close();

            Log.d("DEBUG","CURRENT USER LOGIN TYPE IS " + LoginType.fromString(value));
            return LoginType.fromString(value);
        } catch (Exception exception) {
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.GET_LOGIN_TYPE_ERROR, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "GET USER LOGIN TYPE LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            return LoginType.NULL;
        }
    }

    public static boolean removeLoginType(Context context) {
        //used before updating the loginType.
        //drops and recreates the desired table for the loginType
        try {
            User_Database db = new User_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            String statement = "DROP TABLE " + LOGIN_TYPE_TABLE;
            database.execSQL(statement);

            statement = "CREATE TABLE " + LOGIN_TYPE_TABLE + "(login_type varchar(255))";
            database.execSQL(statement);
            Log.d("DEBUG", "REMOVED USER LOGIN TYPE SUCCESSFULLY!");
            return true;
        } catch (Exception exception) {
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.REMOVE_LOGIN_TYPE_ERROR, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "REMOVE USER LOGIN TYPE LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            return false;
        }
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
            Log.d("DEBUG","REMOVED USER LOGIN PASSWORD SUCCESSFULLY!");
            return true;
        }catch (Exception exception){
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.REMOVE_LOGIN_PASSWORD_ERROR, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "REMOVE USER LOGIN PASSWORD LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            return false;
        }
    }

    public static boolean verifyLoginPassword(Context context,String enteredPassword) {
        try{
            if (getLoginPassword(context).equals(enteredPassword)) {
                Log.d("DEBUG", "USER LOGIN PASSWORD IS CORRECT!");
                return true;
            }

            Log.d("DEBUG", "USER LOGIN PASSWORD IS INCORRECT!");
            return false;
        }catch (Exception exception){
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.VERIFY_LOGIN_PASSWORD_ERROR, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "VERIFY USER LOGIN PASSWORD LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            return false;
        }
    }

    private static String getLoginPassword(Context context) {
        return loginPassword;
    }

    private static void getLoginPasswordFromDB(Context context) {
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

            Log.d("DEBUG","GET USER LOGIN PASSWORD SUCCESSFULLY!");
            loginPassword = decryptData(password);
        }catch (Exception exception){
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.GET_LOGIN_PASSWORD_ERROR, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "GET USER LOGIN PASSWORD LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            loginPassword = null;
        }
    }

    public static void eraseData(Context context){
        User.removeLoginType(context);
        User.removePasswords(context);
    }

    public static String encryptData(String word) throws Exception{
        byte[] ivBytes;
        String password = "Hello";
        /*you can give whatever you want for password. This is for testing purpose*/
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        byte[] saltBytes = bytes;
        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65556, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        //encrypting the word
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(word.getBytes("UTF-8"));
        //prepend salt and vi
        byte[] buffer = new byte[saltBytes.length + ivBytes.length + encryptedTextBytes.length];
        System.arraycopy(saltBytes, 0, buffer, 0, saltBytes.length);
        System.arraycopy(ivBytes, 0, buffer, saltBytes.length, ivBytes.length);
        System.arraycopy(encryptedTextBytes, 0, buffer, saltBytes.length + ivBytes.length, encryptedTextBytes.length);
        return Base64.getEncoder().encodeToString(buffer);
    }

    public static String decryptData(String encryptedText) throws Exception{
        try {
            String password = "Hello";
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //strip off the salt and iv
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedText));
            byte[] saltBytes = new byte[20];
            buffer.get(saltBytes, 0, saltBytes.length);
            byte[] ivBytes1 = new byte[cipher.getBlockSize()];
            buffer.get(ivBytes1, 0, ivBytes1.length);
            byte[] encryptedTextBytes = new byte[buffer.capacity() - saltBytes.length - ivBytes1.length];

            buffer.get(encryptedTextBytes);
            // Deriving the key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65556, 256);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes1));
            byte[] decryptedTextBytes = null;
            try {
                decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }

            return new String(decryptedTextBytes);
        }catch (Exception exception){
            return null;
        }
    }

    public static Uri getPreinstalledIcon(Context context, String preInstalledIcon) {
        //returning the desired icon based on spinner element
        Resources resources = context.getResources();
        int id = -1;
        switch (preInstalledIcon) {
            case "Google":
                id = R.drawable.google;
                break;
            case "Gmail":
                id = R.drawable.gmail;
                break;
            case "Steam":
                id = R.drawable.steam;
                break;
            case "Instagram":
                id = R.drawable.instagram;
                break;
            case "Facebook":
                id = R.drawable.facebook;
                break;
            case "Discord":
                id = R.drawable.discord;
                break;
            case "Paypal":
                id = R.drawable.paypal;
                break;
            case "Snapchat":
                id = R.drawable.snapchat;
                break;
            case "Github":
                id = R.drawable.github;
                break;
            case "Bybit":
                id = R.drawable.bybit;
                break;
            case "Binance":
                id = R.drawable.binance;
                break;
            case "Maxbounty":
                id = R.drawable.maxbounty;
                break;
            case "Epic Games":
                id = R.drawable.epicgames;
                break;
            case "EA Desktop":
                id = R.drawable.eadesktop;
                break;
            case "Xbox":
                id = R.drawable.xbox;
                break;
        }

        if(id == -1)
            return null;

        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(id))
                .appendPath(resources.getResourceTypeName(id))
                .appendPath(resources.getResourceEntryName(id))
                .build();
    }

    public static Uri getAutoIcon(Context context, EditText email) {
        //returning the desired icon based on first letter of the email
        Resources resources = context.getResources();
        int id;
        if(email.getText().toString().toLowerCase().charAt(0) >= 'a' && email.getText().toString().toLowerCase().charAt(0) <= 'z')
            id = autoGenIcons[email.getText().toString().toLowerCase().charAt(0) - 'a'];
        else id = autoGenIcons[26];

        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(id))
                .appendPath(resources.getResourceTypeName(id))
                .appendPath(resources.getResourceEntryName(id))
                .build();

    }

    public static Uri getAutoIcon(Context context, String email) {
        //returning the desired icon based on first letter of the email
        Resources resources = context.getResources();
        int id;
        if(email.toLowerCase().charAt(0) >= 'a' && email.toLowerCase().charAt(0) <= 'z')
            id = autoGenIcons[email.toLowerCase().charAt(0) - 'a'];
        else id = autoGenIcons[26];

        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(id))
                .appendPath(resources.getResourceTypeName(id))
                .appendPath(resources.getResourceEntryName(id))
                .build();

    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        //transforming the inputStream image in a byte array
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static void init(Context context){
        //getting all the passwords from the database and
        //initializing the array for auto-generated icons
        passwordList.getAll(context);
        getLoginPasswordFromDB(context);
        autoGenIcons[0] = R.drawable.a;
        autoGenIcons[1] = R.drawable.b;
        autoGenIcons[2] = R.drawable.c;
        autoGenIcons[3] = R.drawable.d;
        autoGenIcons[4] = R.drawable.e;
        autoGenIcons[5] = R.drawable.f;
        autoGenIcons[6] = R.drawable.g;
        autoGenIcons[7] = R.drawable.h;
        autoGenIcons[8] = R.drawable.i;
        autoGenIcons[9] = R.drawable.j;
        autoGenIcons[10] = R.drawable.k;
        autoGenIcons[11] = R.drawable.l;
        autoGenIcons[12] = R.drawable.m;
        autoGenIcons[13] = R.drawable.n;
        autoGenIcons[14] = R.drawable.o;
        autoGenIcons[15] = R.drawable.p;
        autoGenIcons[16] = R.drawable.q;
        autoGenIcons[17] = R.drawable.r;
        autoGenIcons[18] = R.drawable.s;
        autoGenIcons[19] = R.drawable.t;
        autoGenIcons[20] = R.drawable.u;
        autoGenIcons[21] = R.drawable.v;
        autoGenIcons[22] = R.drawable.w;
        autoGenIcons[23] = R.drawable.x;
        autoGenIcons[24] = R.drawable.y;
        autoGenIcons[25] = R.drawable.z;
        autoGenIcons[26] = R.drawable.unknown;

        LoadingScreen.APP_READY = true;
    }
}



