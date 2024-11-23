package com.example.coinvert.utils;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.coinvert.CompareActivity;
import com.example.coinvert.ConvertActivity;
import com.example.coinvert.ProfileActivity;
import com.example.coinvert.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavBar {

    public static void setupBottomNav(BottomNavigationView bottomNavigationView, Context context) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<?> targetActivity = null;

            // Map menu items to their respective activities
            if (itemId == R.id.menu_profile) {
                targetActivity = ProfileActivity.class;
            } else if (itemId == R.id.menu_convert) {
                targetActivity = ConvertActivity.class;
            } else if (itemId == R.id.menu_compare) {
                targetActivity = CompareActivity.class;
            } else if (itemId == R.id.menu_logout) {
                Auth.logout(context); // Logout action
                return true; // No activity switch needed for logout
            }

            if (targetActivity != null && !context.getClass().equals(targetActivity)) {
                // Avoid recreating the current activity
                Intent intent = new Intent(context, targetActivity);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Use existing instance if it's on top
                context.startActivity(intent);
            }

            return true; // Return true to indicate the item was selected
        });
    }
}
