package com.example.coinvert;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button welcomeButton = findViewById(R.id.welcomeButton);
        welcomeButton.setOnClickListener(v -> startActivity(new Intent(WelcomeActivity.this, ConvertActivity.class)));

    }
}