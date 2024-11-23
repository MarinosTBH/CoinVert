package com.example.coinvert.database;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration1To2 {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add the optional fields to the User table
            database.execSQL("ALTER TABLE User ADD COLUMN name TEXT");
            database.execSQL("ALTER TABLE User ADD COLUMN imagePath TEXT");
        }
    };
}
