package com.project;

import com.project.model.*;
import java.time.LocalDateTime;
import java.util.List;

public class MainApp {
    
    public static void main(String[] args) {
        System.out.println("--- Task Project Manager Uygulamasi Baslatiliyor ---");

        // 1. Yönetici Sınıfını Başlatma
        ProjectManager manager = new ProjectManager();
        
        // 2. Kullanıcı Oluşturma (Kapsülleme)
        User user1 = new User(1, "Ennur", "ennur@mail.com");
        manager.addUser(user1);
        
        // 3. Proje Oluşturma
        Project p1 = new Project(101, "Java OOP Projesi", "OOP prensiplerini gosterme projesi");
        Project p2 = new Project(102, "Web Sitesi Guncellemesi", "On yuz tasarim guncellemeleri");
        manager.addProject(p1);
        manager.addProject(p2);

        // 4. Görev Oluşturma ve Atama (Kapsülleme)
        manager.createTaskAndAssign("OOP Sinif Tasarimi", "Tum siniflarin soyut ve somut yapilarini belirle", 5, 
                                   LocalDateTime.now().plusDays(2), user1, p1);
        
        // 5. Kalıtım Örneği (TimedTask)
        TimedTask tt1 = new TimedTask(3, "Toplanti Hazirligi", "Haftalik ilerleme toplantisi sunumu", 3, 
                                      LocalDateTime.now().plusHours(5), user1, LocalDateTime.now().plusHours(1), 120);
        p1.addTask(tt1);
        
        // 6. Polimorfizm Testi (Sıralama)
        System.out.println("\n--- GOREV SIRALAMA TESTI (Polimorfizm) ---");
        TaskSorter prioritySorter = new PrioritySorter(); // Polimorfik kullanım
        List<Task> sortedByPriority = manager.getSortedTasks(p1, prioritySorter);
        System.out.println("Oncelige Gore Siralama (En basta: " + sortedByPriority.get(0).getTitle() + ")");
        
        // 7. Soyutlama Testi (Raporlama)
        System.out.println("\n--- RAPORLAMA TESTI (Soyutlama) ---");
        ReportGenerator fullReport = new FullReportGenerator(); // Soyut sınıfın somut uygulaması
        System.out.println(fullReport.generateReport(manager.getProjects()));
        
        // 8. Arayüz Testi (Completable)
        tt1.completeTask(); // Arayüz metodunu çağır
        
        System.out.println("\nUygulama basariyla calisti.");
    }
}