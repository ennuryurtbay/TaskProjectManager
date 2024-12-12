package com.project.model; 

import java.time.LocalDateTime;

public class Task implements Completable {
    private int taskId;
    private String title;
    private String description;


    private int priority;
    private LocalDateTime dueDate; 

    private boolean isCompleted = false;


    private User assignedUser; 

    // Constructor
    public Task(int taskId, String title, String description, int priority, LocalDateTime dueDate, User assignedUser) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignedUser = assignedUser;
    }

    // Arayüz
    @Override
    public boolean isCompleted() {
        return this.isCompleted;
    }

    @Override
    public void completeTask() {
        this.isCompleted = true;
        System.out.println(this.title + " görevi tamamlandı.");
    }

    // Kapsülleme için Getter/Setter'lar
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    // Diğer getter'lar
    public String getTitle() { return title; }
    public int getTaskId() { return taskId; }
    public User getAssignedUser() { return assignedUser; }
}