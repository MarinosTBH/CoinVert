package com.example.coinvert.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "User")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String email;
    public String password;
    private byte[] image;  // Store the image as a byte array

    // Constructor with optional parameters for name and imagePath
    public User(String email, String password, String name, byte[] image) {
        this.email = email;
        this.password = password;
        // Set defaults for optional fields
        this.name = name;
        this.image = image;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and setters
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Other getters and setters
}
