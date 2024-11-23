package com.example.coinvert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coinvert.database.AppDatabase;
import com.example.coinvert.database.DatabaseClient;
import com.example.coinvert.model.User;
import com.example.coinvert.model.UserDao;

public class ForgottenPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText newPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Use a "forgot_password" layout

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.passwordEditText);

        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        backToLoginButton.setOnClickListener(v -> startActivity(new Intent(ForgottenPasswordActivity.this, MainActivity.class)));

        Button resetPasswordButton = findViewById(R.id.resetButton);
        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        // Get user input
        String email = emailEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize the database
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext());
        UserDao userDao = db.userDao();

        // Update the password
        new Thread(() -> {
            User existingUser = userDao.findUserByEmail(email);
            if (existingUser != null) {
                // Update the user's password
                existingUser.password = newPassword;
                userDao.updateUser(existingUser);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgottenPasswordActivity.this, MainActivity.class));
                    finish(); // Close the forgotten password activity
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Email not registered", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
