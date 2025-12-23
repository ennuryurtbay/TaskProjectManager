package com.project.model;
/**
 * Proje, kullanıcı ve görevler arasındaki ilişkileri yöneten merkezi iş mantığı sınıfıdır.
 * Verileri hızlı erişim için HashMap yapısında saklar.
 */
import java.time.LocalDateTime;

import java.util.*; // Map ve HashMap için eklendi
import java.util.stream.Collectors;
import java.io.PrintWriter;
import java.io.IOException;
public class ProjectManager {
    // list yerine Map kullanarak ID üzerinden hızlı erişim sağladım (GÜN 6 Geliştirmesi)
    private Map<Integer, User> userMap = new HashMap<>();
    private Map<Integer, Project> projectMap = new HashMap<>();
    private int nextTaskId = 1;

    // kullanıcı ekleme - map kullanımı
    public void addUser(User user) { 
        userMap.put(user.getUserId(), user); 
    }

    // proje Ekleme - Map kullanımı
    public void addProject(Project project) { 
        projectMap.put(project.getProjectId(), project); 
    }

    // Getter metodu güncellendi (MainApp uyumu için)
    public List<Project> getProjects() {
        return new ArrayList<>(projectMap.values());
    }
    /**
     * Verilen ID'ye sahip görevi tüm projeler içinde arar.
     * @param taskId Aranan görevin ID'si
     * @return Bulunan Task nesnesi
     * @throws TaskNotFoundException Görev bulunamazsa fırlatılır[cite: 23].
     */
    // ID'ye göre kullanıcı bulma 
    public User findUserById(int userId) throws UserNotFoundException {
        User user = userMap.get(userId);
        if (user == null) {
            throw new UserNotFoundException("ID " + userId + " olan kullanıcı bulunamadı.");
        }
        return user;
    }

    // Görev oluşturma ve projeye atama
    public Task createTaskAndAssign(String title, String description, int priority, LocalDateTime dueDate, User assignedUser, Project project) {
        Task newTask = new Task(nextTaskId++, title, description, priority, dueDate, assignedUser);
        project.addTask(newTask);
        return newTask;
    }
    public void saveProjectsToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new java.io.FileWriter(fileName))) {
            for (Project p : projectMap.values()) {
                writer.println("Proje: " + p.getProjectName() + " | ID: " + p.getProjectId());
                for (Task t : p.getTasks()) {
                    writer.println("  - Görev: " + t.getTitle() + " | Öncelik: " + t.getPriority());
                }
            }
            System.out.println("Veriler başarıyla " + fileName + " dosyasına kaydedildi.");
        } catch (IOException e) {
            System.err.println("Dosya kaydedilirken hata oluştu: " + e.getMessage());
        }
    }

    /**
     * Polimorfik Özellik: Görevleri verilen sıralayıcıya (TaskSorter) göre sıralar.
     * @param project Sıralanacak görevlerin ait olduğu proje
     * @param sorter Strateji desenine göre kullanılacak sıralama mantığı
     * @return Sıralanmış görev listesi
     */
    public List<Task> getSortedTasks(Project project, TaskSorter sorter) {
        return sorter.sort(project.getTasks());
    }
}


