package com.project.model;

// Checked Exception. Bir kullanıcı bulunamadığında fırlatılacak.
public class UserNotFoundException extends Exception {
    
    // Hata mesajını alan constructor
    public UserNotFoundException(String message) {
        super(message);
    }
}