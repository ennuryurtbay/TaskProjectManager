package com.project.model; 

public class User {
    private int userId;
    private String username;
    private String email;

    public User(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    // Getter Metotları
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}