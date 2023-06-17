package com.example.passwordmanager.Password;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.passwordmanager.Config.ToastMessage;
import com.example.passwordmanager.User.User;

import java.util.ArrayList;

public class PasswordList extends ArrayList<Password> {

    private static final String PASSWORDS_TABLE = "passwords";
    public void save(Password password, Context context){

        this.add(password);

        try{
            new Thread(new Runnable() {
                @Override
                public void run() {

                    //adding a password directly in the passwords table
                    Passwords_Database db = new Passwords_Database(context);
                    SQLiteDatabase database = db.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put("id",password.getId());
                    cv.put("email", password.getEmail());
                    cv.put("password", User.encryptData(password.getPassword()));
                    cv.put("icon", password.getIcon());
                    cv.put("auto_generate", password.isAuto_generate());

                    database.insert(PASSWORDS_TABLE, null, cv);

                    //Log.d("COMMENT", "ADDED PASSWORD SUCCESFULLY!");
                }

            }).start();
        }
        catch(Exception exception) {
            //in case of an exception, a toast message is thrown, and action is cancelled
            //Toast.makeText(context, ToastMessage.CANT_ADD_PASSWORD, Toast.LENGTH_SHORT).show();
            //Log.d("COMMENT", "ADD PASSWORD LEAD TO ERROR!");
            //Log.d("COMMENT", exception.getMessage());
        }
    }

    public void getAll(Context context){
        try {
            //taking all the values from the password database and adding them to a list
            //if list is empty, then we can directly return null, otherwise return the list

            Passwords_Database db = new Passwords_Database(context);
            SQLiteDatabase database = db.getReadableDatabase();

            String statement = "SELECT * FROM " + PASSWORDS_TABLE;
            Cursor cursor = database.rawQuery(statement, null);

            while(cursor.moveToNext()) {
                try {
                    int id = cursor.getInt(0);
                    String email = cursor.getString(1);
                    String password = cursor.getString(2);
                    byte[] icon = cursor.getBlob(3);
                    int auto_generate = cursor.getInt(4);
                    Log.d("COMMENT",Integer.toString(auto_generate));

                    this.add(new Password(id, email, User.decryptData(password), icon, auto_generate));

                }catch (Exception exception){
                    //in case of an exception, a toast message is thrown, and it continues to next password
                    //Toast.makeText(context, ToastMessage.CANT_GET_PASSWORD, Toast.LENGTH_SHORT).show();
                    //Log.d("COMMENT","GET USER PASSWORD #" + cursor.getCount() + " LEAD TO ERROR!");
                    Log.d("COMMENT",exception.getMessage());
                }
            }

            cursor.close();
            database.close();

            //Log.d("COMMENT","SUCCESSFULLY GOT " + this.size() + " PASSWORDS!");
        } catch (Exception exception) {
            //in case of an exception, a toast message is thrown, and null is returned
            //Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            //Log.d("COMMENT","GET USER PASSWORDS LEAD TO ERROR!");
            //Log.d("COMMENT",exception.getMessage());
        }
    }

    public void remove(int id, Context context){

        for(Password password : this){
            if(password.getId() == id) {
                this.remove(password);
                //Log.d("COMMENT","REMOVED PASSWORD SUCCESFULLY!");
                break;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //removing a password directly in the passwords table
                    Passwords_Database db = new Passwords_Database(context);
                    SQLiteDatabase database = db.getWritableDatabase();

                    String statement = "DELETE FROM " + PASSWORDS_TABLE + " WHERE id=\""+ id + "\";";
                    database.execSQL(statement);

                    //Log.d("COMMENT","REMOVED PASSWORD SUCCESFULLY!");
                }
                catch (Exception exception){
                    //in case of an exception, a toast message is thrown, and action is cancelled
//                    Toast.makeText(context, ToastMessage.CANT_ADD_PASSWORD, Toast.LENGTH_SHORT).show();
//                    Log.d("COMMENT","REMOVE PASSWORD LEAD TO ERROR!");
//                    Log.d("COMMENT",exception.getMessage());
                }
            }
        }).start();
    }

    public void removeAll(Context context){
        //drops and recreates the desired table for the passwords
        try{
            Passwords_Database db = new Passwords_Database(context);
            SQLiteDatabase database = db.getWritableDatabase();

            String statement = "DROP TABLE " + PASSWORDS_TABLE;
            database.execSQL(statement);

            statement = "CREATE TABLE " + PASSWORDS_TABLE + "(id int, email varchar(255), password varchar(255), icon blob, auto_generate number(1))";
            database.execSQL(statement);

            //Log.d("COMMENT","REMOVED PASSWORDS");
        }
        catch (Exception exception){
            //in case of an exception, a toast message is thrown, and action is cancelled
            Toast.makeText(context, ToastMessage.CORRUPTED_FILES, Toast.LENGTH_SHORT).show();
            //Log.d("COMMENT","REMOVE PASSWORDS LEAD TO ERROR!");
            //Log.d("COMMENT",exception.getMessage());
        }
    }

    public void update(Context context, int id, Password password){

        for(Password currentPassword : this){
            if(currentPassword.getId() == id){
                currentPassword.setId(password.getId());
                currentPassword.setEmail(password.getEmail());
                currentPassword.setPassword(password.getPassword());
                currentPassword.setIcon(password.getIcon());
                currentPassword.setAuto_generate(password.isAuto_generate());
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Passwords_Database db = new Passwords_Database(context);
                    SQLiteDatabase database = db.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put("id",password.getId());
                    cv.put("email", password.getEmail());
                    cv.put("password", User.encryptData(password.getPassword()));
                    cv.put("icon", password.getIcon());
                    cv.put("auto_generate", password.isAuto_generate());

                    database.update(PASSWORDS_TABLE, cv, "id = ?", new String[]{Integer.toString(id)});

                }catch (Exception exception){
                    Log.d("COMMENT",exception.getMessage());
                }
            }
        }).start();
    }

}
