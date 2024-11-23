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

public class RegisterActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button backToLoginButton = findViewById(R.id.backToLoginButton);
        backToLoginButton.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, MainActivity.class)));

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // Get user input
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize the database
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext());
        UserDao userDao = db.userDao();

        // Check if the user already exists
        new Thread(() -> {
            User existingUser = userDao.findUserByEmail(email);
            if (existingUser != null) {
                runOnUiThread(() -> Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show());
            } else {
                // Create and insert new user
                User newUser = new User(email, password, null, null);
                userDao.insertUser(newUser);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish(); // Close the register activity
                });
            }
        }).start();
    }
}
