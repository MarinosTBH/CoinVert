package com.example.coinvert.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.coinvert.MainActivity;

public class Auth {

    // Reusable logout function
    public static void logout(Context context) {
        // Clear the saved user email from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_EMAIL");  // Remove the stored email
        editor.apply();  // Commit the changes

        // Navigate to MainActivity (or any other screen)
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear backstack
        context.startActivity(intent);
    }
}
