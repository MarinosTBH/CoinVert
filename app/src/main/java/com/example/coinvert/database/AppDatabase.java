// File: AppDatabase.java
package com.example.coinvert.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.coinvert.model.User;
import com.example.coinvert.model.UserDao;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
