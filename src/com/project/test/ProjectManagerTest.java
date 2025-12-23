package com.project.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.project.model.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ProjectManager sınıfının iş mantığını test eden birim test sınıfı.
 * Rüvrik gereği JUnit kullanılarak yazılmıştır. [cite: 19, 28]
 */
public class ProjectManagerTest {

    @Test
    public void testAddUser() throws UserNotFoundException {
        ProjectManager manager = new ProjectManager();
        User user = new User(1, "Ennur", "ennur@mail.com");
        manager.addUser(user);
        
        assertNotNull(manager.findUserById(1));
        assertEquals("Ennur", manager.findUserById(1).getUsername());
    }

    @Test
    public void testTaskCreation() {
        ProjectManager manager = new ProjectManager();
        Project project = new Project(101, "Test Projesi", "Açıklama");
        User user = new User(1, "Ennur", "ennur@mail.com");
        
        Task task = manager.createTaskAndAssign("Test Görevi", "Detay", 5, 
                                               LocalDateTime.now(), user, project);
        
        assertEquals(1, project.getTasks().size());
        assertEquals("Test Görevi", project.getTasks().get(0).getTitle());
    }

    @Test
    public void testUserNotFoundException() {
        ProjectManager manager = new ProjectManager();
        
        // Hata fırlatma mantığının (Exception Handling) testi [cite: 23]
        assertThrows(UserNotFoundException.class, () -> {
            manager.findUserById(999);
        });
    }

    @Test
    public void testTaskSortingByPriority() {
        ProjectManager manager = new ProjectManager();
        Project p = new Project(1, "Sıralama Testi", "Açıklama");
        User u = new User(1, "Ennur", "e@mail.com");
        
        manager.createTaskAndAssign("Düşük", "D", 1, LocalDateTime.now(), u, p);
        manager.createTaskAndAssign("Yüksek", "Y", 5, LocalDateTime.now(), u, p);
        
        PrioritySorter sorter = new PrioritySorter();
        List<Task> sortedTasks = manager.getSortedTasks(p, sorter);
        
        // Polimorfizm sonucunda en yüksek önceliğin ilk sırada olduğu doğrulanır [cite: 23]
        assertEquals(5, sortedTasks.get(0).getPriority());
    }
}