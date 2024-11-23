package com.example.coinvert.database;

import android.content.Context;
import androidx.room.Room;

/**
 * Singleton for managing the Room database instance.
 */
public class DatabaseClient {
    private static AppDatabase instance;

    private DatabaseClient() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the singleton instance of AppDatabase.
     *
     * @param context Application context
     * @return AppDatabase instance
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseClient.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "coinvert-db").build();
                }
            }
        }
        return instance;
    }
}
