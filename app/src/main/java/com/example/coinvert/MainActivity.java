package com.example.coinvert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coinvert.database.AppDatabase;
import com.example.coinvert.database.DatabaseClient;
import com.example.coinvert.model.User;
import com.example.coinvert.model.UserDao;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database
        AppDatabase db = DatabaseClient.getInstance(getApplicationContext());

        // Access the DAO
        UserDao userDao = db.userDao();

        // Get references to UI components
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            // Get user input
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate login credentials (must be done on a background thread)
            new Thread(() -> {
                User user = userDao.findUserByEmail(email);
                runOnUiThread(() -> {
                    if (user != null && user.password.equals(password)) {
                        // Login successful
                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to WelcomeActivity
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        intent.putExtra("USER_NAME", user.email);
                        startActivity(intent);
                    } else {
                        // Login failed
                        Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // Register
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        // Register
        TextView forgotPasswordButton = findViewById(R.id.forgotPasswordText);
        forgotPasswordButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ForgottenPasswordActivity.class)));


    }
}
