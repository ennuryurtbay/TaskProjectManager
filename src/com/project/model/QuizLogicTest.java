package com.project.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

public class QuizLogicTest {
    private QuizManager manager;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        manager = new QuizManager();
        testQuiz = new Quiz(1, "Test Sınavı");
        manager.addQuiz(testQuiz);
    }

    @Test
    void testAddQuestionAndPoints() {
        // Soru ekleme ve puan hesaplama testi
        MultipleChoiceQuestion q1 = new MultipleChoiceQuestion(
            1, "Java nedir?", "Dil", 10, Arrays.asList("Dil", "Kahve")
        );
        testQuiz.addQuestion(q1);
        
        assertEquals(1, testQuiz.getQuestions().size(), "Soru başarıyla eklenmeli.");
        assertEquals(10, testQuiz.getQuestions().get(0).getPoints(), "Soru puanı doğru kaydedilmeli.");
    }

    @Test
    void testCheckAnswer() {
        // Cevap kontrol mantığı testi
        Question q = new MultipleChoiceQuestion(
            2, "OOP nedir?", "Nesne", 20, Arrays.asList("Nesne", "Fonksiyon")
        );
        
        assertTrue(q.checkAnswer("Nesne"), "Doğru cevap true dönmeli.");
        assertTrue(q.checkAnswer("NESNE"), "Büyük/küçük harf duyarsız olmalı.");
        assertFalse(q.checkAnswer("Yanlış"), "Yanlış cevap false dönmeli.");
    }

    @Test
    void testQuizManagerIntegrity() {
        // Manager listesinin bütünlük testi
        assertNotNull(manager.getQuizzes(), "Sınav listesi null olmamalı.");
        assertEquals(1, manager.getQuizzes().size(), "Eklenen sınav listede görünmeli.");
    }
}