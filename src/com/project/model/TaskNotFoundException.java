package com.project.model;

// Java'da Checked Exception oluşturmak için Exception sınıfından miras alınır.
public class TaskNotFoundException extends Exception {
    
    // Hata mesajını alan constructor
    public TaskNotFoundException(String message) {
        super(message);
    }
}