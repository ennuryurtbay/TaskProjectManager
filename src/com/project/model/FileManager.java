package com.project.model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FILE_NAME = "quizzes.dat";

    // Tüm sınav listesini dosyaya kaydeder
    public static void saveQuizzes(List<Quiz> quizzes) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(quizzes);
        } catch (IOException e) {
            System.out.println("Kayıt hatası: " + e.getMessage());
        }
    }

    // Dosyadan sınav listesini okur
    @SuppressWarnings("unchecked")
    public static List<Quiz> loadQuizzes() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Quiz>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
}