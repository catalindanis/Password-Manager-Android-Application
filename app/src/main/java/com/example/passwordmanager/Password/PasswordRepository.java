package com.example.passwordmanager.Password;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.passwordmanager.Password.Password;

import java.util.List;

@Dao
public interface PasswordRepository {

    @Insert
    void insert(Password password);

    @Insert
    void insertAll(Password ... passwords);

    @Delete
    void delete(Password password);

    @Query("DELETE FROM Passwords WHERE id = :id")
    void delete(int id);

    @Query("SELECT * FROM Passwords WHERE id = :id")
    Password get(int id);

    @Query("SELECT * FROM Passwords")
    List<Password> getAll();
}
