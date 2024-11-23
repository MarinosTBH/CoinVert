package com.example.coinvert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coinvert.model.User;
import com.example.coinvert.model.UserDao;
import com.example.coinvert.database.DatabaseClient;
import com.example.coinvert.database.AppDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private EditText profileName, profileEmail;
    private ImageView profileImageView;
    private EditText oldPassword, confirmPassword;

    private static final int PICK_IMAGE_REQUEST = 1;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve the logged-in user's email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("USER_EMAIL", null);  // Retrieve the saved email

        if (userEmail == null) {
            // If no user is logged in, navigate back to MainActivity or Login screen
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }

        // Initialize views
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        Button saveProfileButton = findViewById(R.id.saveProfileButton);
        profileImageView = findViewById(R.id.profileAvatar);
        oldPassword = findViewById(R.id.passwordEditText);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);

        // Initialize Room Database
        userDao = DatabaseClient.getInstance(getApplicationContext()).userDao();

        // Load profile data from the database
        loadProfileData(userEmail);

        // Save profile button click listener
        saveProfileButton.setOnClickListener(v -> saveProfileData(userEmail));

        // Change image button click listener
        profileImageView.setOnClickListener(v -> openImagePicker());

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        // Set the listener for bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item selection
                if (item.getItemId() == R.id.menu_profile) {
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class)); // Open ProfileActivity
                } else if (item.getItemId() == R.id.menu_convert) {
                    startActivity(new Intent(ProfileActivity.this, ConvertActivity.class)); // Open ConvertActivity
                } else if (item.getItemId() == R.id.menu_logout) {
                    logout();
                }
                return true; // Return true to indicate the item was selected
            }
        });

    }

    // Load profile data from the database using the logged-in user's email
    private void loadProfileData(String userEmail) {
        new Thread(() -> {
            User user = userDao.findUserByEmail(userEmail);

            runOnUiThread(() -> {
                if (user != null) {
                    profileName.setText(user.getName());
                    profileEmail.setText(user.getEmail());

                    if (user.getImage() != null) {
                        // Convert the byte array to a Bitmap and display it
                        Bitmap bitmap = byteArrayToBitmap(user.getImage());
                        profileImageView.setImageBitmap(bitmap);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private Bitmap byteArrayToBitmap(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    // Save profile data (including the image and password)
    private void saveProfileData(String userEmail) {
        String name = profileName.getText().toString().trim();
        String email = profileEmail.getText().toString().trim();
        String oldPasswordText = oldPassword.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!oldPasswordText.isEmpty() && !oldPasswordText.equals(confirmPasswordText)) {
            Toast.makeText(ProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            // Retrieve the current user from the database
            User existingUser = userDao.findUserByEmail(userEmail);
            if (existingUser == null) {
                runOnUiThread(() ->
                        Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // Get the image bytes (if a new image is selected)
            byte[] imageBytes = existingUser.getImage(); // Default to existing image
            profileImageView.setDrawingCacheEnabled(true);
            profileImageView.buildDrawingCache();
            Bitmap bitmap = profileImageView.getDrawingCache();
            if (bitmap != null) {
                imageBytes = bitmapToByteArray(bitmap);
            }

            // Update user object
            existingUser.setName(name);
            existingUser.setEmail(email);
            if (!oldPasswordText.isEmpty()) {
                existingUser.setPassword(oldPasswordText);
            }
            existingUser.setImage(imageBytes);

            // Update the database
            userDao.updateUser(existingUser);

            runOnUiThread(() ->
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Open image picker to select an image from the gallery
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image picker (when the user selects an image)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(ProfileActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //    Logout function
// Optional: Log out functionality
    public void logout() {
        // Clear the saved user email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_EMAIL");  // Remove the stored email
        editor.apply();  // Commit the changes

        // Navigate to MainActivity (or any other screen)
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Optionally finish the current activity
    }
}
