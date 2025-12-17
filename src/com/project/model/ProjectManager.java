package com.project.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectManager {
    private List<User> users = new ArrayList<>();
    private List<Project> projects = new ArrayList<>(); // Private olduğu için doğrudan erişim engelliydi
    private int nextTaskId = 1;
    private int nextProjectId = 1;

    // Kullanıcı ve Proje Ekleme Metotları
    public void addUser(User user) { users.add(user); }
    public void addProject(Project project) { projects.add(project); }

    // Gerekli Özellik: Görev Oluşturma ve Projeye Atama
    public Task createTaskAndAssign(String title, String description, int priority, LocalDateTime dueDate, User assignedUser, Project project) {
        Task newTask = new Task(nextTaskId++, title, description, priority, dueDate, assignedUser);
        project.addTask(newTask); // Görevi projeye atama
        return newTask;
    }
    
    // Gerekli Özellik: Yaklaşan Görevleri Listeleme
    public List<Task> listUpcomingTasks(int daysAhead) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime threshold = today.plusDays(daysAhead);

        return projects.stream()
            .flatMap(p -> p.getTasks().stream()) // Tüm projelerdeki görevleri tek bir akışta topla
            .filter(task -> !task.isCompleted()) // Tamamlanmamış görevleri filtrele
            .filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(threshold) && task.getDueDate().isAfter(today)) // Belirtilen aralıktaki görevleri filtrele
            .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate())) // Tarihe göre sırala
            .collect(Collectors.toList());
    }

    // Polimorfik Özellik: Görevleri verilen sıralayıcıya (TaskSorter) göre sıralar
    public List<Task> getSortedTasks(Project project, TaskSorter sorter) {
        // TaskSorter arayüzü sayesinde, hangi sıralama mantığı verilirse o çalışır
        return sorter.sort(project.getTasks());
    }
    

    public List<Project> getProjects() {
        return projects;
    }
    
 // Yeni Metot: ID'ye göre görev bulur veya hata fırlatır
    public Task findTaskById(int taskId) throws TaskNotFoundException {
        // Tüm projelerdeki görevleri tek bir akışta topla
        return projects.stream()
                .flatMap(p -> p.getTasks().stream())
                .filter(t -> t.getTaskId() == taskId)
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException("ID " + taskId + " olan görev bulunamadı."));
    }
    
    // Yeni Metot: ID'ye göre kullanıcı bulur veya hata fırlatır
    public User findUserById(int userId) throws UserNotFoundException {
        return users.stream()
                .filter(u -> u.getUserId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("ID " + userId + " olan kullanıcı bulunamadı."));
    }
}




