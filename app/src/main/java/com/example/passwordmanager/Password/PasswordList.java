package com.example.passwordmanager.Password;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.passwordmanager.Config.Error;
import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.Config.Troubleshooter;
import com.example.passwordmanager.Troubleshooter.TroubleshooterPage;
import com.example.passwordmanager.User.User;

import java.util.ArrayList;

public class PasswordList extends ArrayList<Password> {
    private static final String PASSWORDS_TABLE = "passwords";
    public void save(Password password, Context context){
        try{
            //adding password in memory password list
            this.add(password);
            //adding password in the passwords table, on a separate thread for performance increasing
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Passwords_Database db = new Passwords_Database(context);
                        SQLiteDatabase database = db.getWritableDatabase();

                        ContentValues cv = new ContentValues();
                        cv.put("id", password.getId());
                        cv.put("email", password.getEmail());
                        cv.put("extra", password.getExtra());
                        cv.put("password", User.encryptData(password.getPassword()));
                        cv.put("icon", password.getIcon());
                        cv.put("auto_generate", password.isAuto_generate());

                        database.insert(PASSWORDS_TABLE, null, cv);

                        Log.d("DEBUG", "PASSWORD ID=" + password.getId() + " ADDED SUCCESSFULLY!");
                    }catch (Exception exception){
                        //in case of an exception, toast and a debug messages are sent
                        Toast.makeText(context, ToastMessage.CANT_ADD_PASSWORD, Toast.LENGTH_LONG).show();
                        Log.d("DEBUG", "ADD PASSWORD ID=" + password.getId() + " LEAD TO ERROR!");
                        Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                        Troubleshooter.errors.add(new Error(
                                String.format("Can't add password with email = %s! (in database)",password.getEmail()), "Tip: The data from your password might contain " +
                                "corrupted data (illegal characters, icons can't be formatted, etc) or the passwords database is corrupted. Try revision your data from password" +
                                ",restarting the app, removing all passwords " +
                                "(from settings, not manually) and if it doesn't work try reinstalling the app."
                        ));
                        context.startActivity(new Intent(context, TroubleshooterPage.class));
                    }
                }

            }).start();
        }
        catch(Exception exception) {
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.CANT_ADD_PASSWORD, Toast.LENGTH_LONG).show();
            Log.d("DEBUG", "ADD PASSWORD ID=" + password.getId() + " LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            Troubleshooter.errors.add(new Error(
                    String.format("Can't add password with email = %s! (in memory list)",password.getEmail()), "Tip: The data from your password might contain " +
                    "corrupted data (illegal characters, icons can't be formatted, etc) or the passwords list is corrupted. Try revision your data from password" +
                    ",restarting the app, removing all passwords " +
                    "(from settings, not manually if possible) and if it doesn't work try reinstalling the app."
            ));
            context.startActivity(new Intent(context, TroubleshooterPage.class));
        }
    }

    public void getAll(Context context){
        try {
            //taking all the values from the password database and adding them to a list
            Passwords_Database db = new Passwords_Database(context);
            SQLiteDatabase database = db.getReadableDatabase();

            String statement = "SELECT * FROM " + PASSWORDS_TABLE;
            Cursor cursor = database.rawQuery(statement, null);

            while(cursor.moveToNext()) {
                int id = -1;
                String email = "null";
                try {
                    id = cursor.getInt(0);
                    email = cursor.getString(1);
                    String password = cursor.getString(2);
                    String extra = cursor.getString(3);
                    byte[] icon = cursor.getBlob(4);
                    int auto_generate = cursor.getInt(5);

                    this.add(new Password(id, email, User.decryptData(password), icon, extra, auto_generate));
                }catch (Exception exception){
                    //in case of an exception, toast and a debug messages are sent, and it continues to next password
                    Toast.makeText(context, ToastMessage.CANT_GET_PASSWORD, Toast.LENGTH_LONG).show();
                    Log.d("DEBUG","GET PASSWORD ID=" + id + " LEAD TO ERROR!");
                    Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
                    Troubleshooter.errors.add(new Error(
                            String.format(String.format("Can't get password with email = %s!",email)), "Tip: If email = \"null\"," +
                            "it doesn't mean that email is null, but error occurred before getting email from database, which might be corrupted. Try " +
                            "restarting the app, removing all passwords (from settings, not manually if possible) " +
                            "and if it doesn't work try reinstalling the app."
                    ));
                    context.startActivity(new Intent(context, TroubleshooterPage.class));
                }
            }

            cursor.close();
            database.close();

            Log.d("DEBUG","SUCCESSFULLY GOT " + this.size() + " PASSWORDS!");
        } catch (Exception exception) {
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.CORRUPTED_TABLE_PASSWORDS, Toast.LENGTH_LONG).show();
            Log.d("DEBUG","GET USER PASSWORDS LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            Troubleshooter.errors.add(new Error(
                    String.format(String.format("Can't get none of your passwords!")), "Tip: " +
                    "database might be corrupted. Try " +
                    "restarting the app, removing all passwords (from settings, not manually if possible) " +
                    "and if it doesn't work try reinstalling the app."
            ));
            context.startActivity(new Intent(context, TroubleshooterPage.class));
        }
    }

    public void remove(int id, Context context){
        String email = "null";
        try {
            //removing password from memory list
            for (Password password : this) {
                if (password.getId() == id) {
                    email = password.getEmail();
                    this.remove(password);
                    Log.d("DEBUG","PASSWORD ID=" + id + " REMOVED SUCCESSFULLY! (memory list)");
                    break;
                }
            }
            //removing password from database, on a separate thread for performance increasing
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Passwords_Database db = new Passwords_Database(context);
                    SQLiteDatabase database = db.getWritableDatabase();

                    String statement = "DELETE FROM " + PASSWORDS_TABLE + " WHERE id=\"" + id + "\";";
                    database.execSQL(statement);

                    Log.d("DEBUG","PASSWORD ID=" + id + " REMOVED SUCCESSFULLY! (database)");
                }
            }).start();
        }catch (Exception exception){
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.CANT_REMOVE_PASSWORD, Toast.LENGTH_LONG).show();
            Log.d("DEBUG","REMOVE PASSWORD ID=" + id + " LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            Troubleshooter.errors.add(new Error(
                    String.format(String.format("Can't remove password with email = %s!",email)), "Tip: If email = \"null\"," +
                    "it doesn't mean that email is null, but password might not be found or an error occurred before finding the password. Try " +
                    "restarting the app, removing all passwords (from settings, not manually if possible) " +
                    "and if it doesn't work try reinstalling the app."
            ));
            context.startActivity(new Intent(context, TroubleshooterPage.class));
        }
    }

    public void removeAll(Context context){
        try{
            //removing all passwords from memory list
            this.removeAll(User.getPasswords());
            Log.d("DEBUG","REMOVED ALL PASSWORDS SUCCESSFULLY! (memory list)");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //drops and recreates the desired table for the passwords
                    Passwords_Database db = new Passwords_Database(context);
                    SQLiteDatabase database = db.getWritableDatabase();

                    String statement = "DROP TABLE " + PASSWORDS_TABLE;
                    database.execSQL(statement);

                    statement = "CREATE TABLE " + PASSWORDS_TABLE + "(id int, email varchar(255), password varchar(255), extra varchar(255), icon blob, auto_generate number(1))";
                    database.execSQL(statement);

                    Log.d("DEBUG","REMOVED ALL PASSWORDS SUCCESSFULLY! (database)");
                }
            }).start();
        }
        catch (Exception exception){
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.CANT_REMOVE_ALL_PASSWORDS, Toast.LENGTH_LONG).show();
            Log.d("DEBUG","REMOVE ALL PASSWORDS LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            Troubleshooter.errors.add(new Error(
                    String.format(String.format("Can't get none of your passwords!")), "Tip: " +
                    "Memory list or database might be corrupted. Try " +
                    "restarting the app and if it doesn't work try reinstalling the app."
            ));
            context.startActivity(new Intent(context, TroubleshooterPage.class));
        }
    }

    public void update(Context context, int id, Password password) {
        try {
            //updating password fields from memory list
            for (Password currentPassword : this) {
                if (currentPassword.getId() == id) {
                    currentPassword.setId(password.getId());
                    currentPassword.setEmail(password.getEmail());
                    currentPassword.setExtra(password.getExtra());
                    currentPassword.setPassword(password.getPassword());
                    currentPassword.setIcon(password.getIcon());
                    currentPassword.setAuto_generate(password.isAuto_generate());
                    break;
                }
            }
            //updating password fields from database, on a separate thread for performance increasing
            new Thread(new Runnable() {
                @Override
                public void run() {
                        Passwords_Database db = new Passwords_Database(context);
                        SQLiteDatabase database = db.getWritableDatabase();

                        ContentValues cv = new ContentValues();
                        cv.put("id", password.getId());
                        cv.put("email", password.getEmail());
                        cv.put("password", User.encryptData(password.getPassword()));
                        cv.put("extra", password.getExtra());
                        cv.put("icon", password.getIcon());
                        cv.put("auto_generate", password.isAuto_generate());

                        database.update(PASSWORDS_TABLE, cv, "id = ?", new String[]{Integer.toString(id)});
                }
            }).start();
        } catch (Exception exception) {
            //in case of an exception, toast and a debug messages are sent
            Toast.makeText(context, ToastMessage.CANT_UPDATE_PASSWORD, Toast.LENGTH_LONG).show();
            Log.d("DEBUG","UPDATING PASSWORD ID=" + id + " LEAD TO ERROR!");
            Log.d("DEBUG", "ERROR MESSAGE: " + exception.getMessage());
            Troubleshooter.errors.add(new Error(
                    String.format(String.format("Can't update password with email = %s!",password.getEmail())), "Tip: " +
                    "Memory list or database might be corrupted. Try " +
                    "restarting the app, removing all passwords (from settings, not manually if possible) " +
                    "and if it doesn't work try reinstalling the app."
            ));
            context.startActivity(new Intent(context, TroubleshooterPage.class));
        }
    }
}
