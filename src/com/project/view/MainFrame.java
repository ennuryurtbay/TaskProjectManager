package com.project.view;

import com.project.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    private QuizManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalQuestions, lblTotalPoints;
    private JList<Quiz> quizJList; 
    private DefaultListModel<Quiz> listModel;
    private Quiz selectedQuiz;

    public MainFrame(QuizManager manager) {
        // GÜN 16: Program açıldığında dosyadan verileri yükle
        List<Quiz> savedQuizzes = FileManager.loadQuizzes();
        if (savedQuizzes != null && !savedQuizzes.isEmpty()) {
            for (Quiz q : savedQuizzes) {
                manager.addQuiz(q);
            }
        }
        
        this.manager = manager;
        setTitle("Quiz Management System - Dashboard (File I/O)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        // Renkler
        Color headerColor = new Color(33, 37, 41);
        Color sideColor = new Color(241, 243, 245);

        // 1. SOL PANEL (Sınav Listesi)
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(250, 650));
        sidePanel.setBackground(sideColor);
        
        listModel = new DefaultListModel<>();
        quizJList = new JList<>(listModel);
        quizJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectedQuiz = quizJList.getSelectedValue();
                if (selectedQuiz != null) refreshTable(selectedQuiz.getQuestions());
            }
        });

        sidePanel.add(new JLabel("  MEVCUT SINAVLAR"), BorderLayout.NORTH);
        sidePanel.add(new JScrollPane(quizJList), BorderLayout.CENTER);

        lblTotalQuestions = new JLabel("Toplam Soru: 0");
        lblTotalPoints = new JLabel("Toplam Puan: 0");
        JPanel stats = new JPanel(new GridLayout(2,1));
        stats.add(lblTotalQuestions); stats.add(lblTotalPoints);
        sidePanel.add(stats, BorderLayout.SOUTH);

        // 2. ÜST PANEL (Yönetim Butonları)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(headerColor);
        
        JButton btnAddQuiz = new JButton("Yeni Sınav");
        JButton btnAddQuestion = new JButton("Soru Ekle");
        JButton btnStart = new JButton("Sınavı Başlat");
        JButton btnReport = new JButton("Yönetici Raporu");
        
        // GÜN 16: Kaydet Butonu
        JButton btnSave = new JButton("Değişiklikleri Kaydet");
        btnSave.setBackground(new Color(40, 167, 69)); // Yeşil renk
        btnSave.setForeground(Color.WHITE);

        topPanel.add(btnAddQuiz); 
        topPanel.add(btnAddQuestion); 
        topPanel.add(btnStart); 
        topPanel.add(btnReport);
        topPanel.add(btnSave);

        // 3. MERKEZ PANEL (Soru ve Cevap Maskelenmiş Tablo)
        String[] columns = {"ID", "Soru Durumu", "Cevap Durumu", "Puan Değeri"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        // --- BUTON AKSİYONLARI ---

        // Kaydet Aksiyonu (GÜN 16)
        btnSave.addActionListener(e -> {
            FileManager.saveQuizzes(manager.getQuizzes());
            JOptionPane.showMessageDialog(this, "Tüm veriler 'quizzes.dat' dosyasına başarıyla kaydedildi!");
        });

        btnAddQuiz.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Sınav Adı:");
            if (name != null && !name.trim().isEmpty()) {
                Quiz nq = new Quiz(manager.getQuizzes().size() + 1, name);
                manager.addQuiz(nq);
                updateQuizList();
                quizJList.setSelectedValue(nq, true);
            }
        });

        btnAddQuestion.addActionListener(e -> addNewQuestion());
        
        btnStart.addActionListener(e -> startQuizSession());

        btnReport.addActionListener(e -> {
            FullReportGenerator gen = new FullReportGenerator();
            JTextArea area = new JTextArea(20, 50);
            area.setText(gen.generateReport(manager.getQuizzes()));
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Yönetici Raporu", JOptionPane.INFORMATION_MESSAGE);
        });

        add(topPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Başlangıçta listeyi doldur
        updateQuizList();
    }

    private void updateQuizList() {
        listModel.clear();
        for (Quiz q : manager.getQuizzes()) listModel.addElement(q);
        if (!manager.getQuizzes().isEmpty() && selectedQuiz == null) {
            quizJList.setSelectedIndex(0);
        }
    }

    private void startQuizSession() {
        if (selectedQuiz == null || selectedQuiz.getQuestions().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir sınav seçin ve soru ekleyin!");
            return;
        }
        int score = 0;
        for (Question q : selectedQuiz.getQuestions()) {
            String ans = JOptionPane.showInputDialog(this, "SORU: " + q.getText());
            if (ans != null && q.checkAnswer(ans)) score += q.getPoints();
        }
        JOptionPane.showMessageDialog(this, "Sınav Bitti. Toplam Puan: " + score);
    }

    private void addNewQuestion() {
        if (selectedQuiz == null) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir sınav seçin!");
            return;
        }
        try {
            String txt = JOptionPane.showInputDialog(this, "Soru Metni:");
            String ans = JOptionPane.showInputDialog(this, "Doğru Cevap:");
            int pts = Integer.parseInt(JOptionPane.showInputDialog(this, "Puan Değeri:"));
            
            selectedQuiz.addQuestion(new MultipleChoiceQuestion(
                selectedQuiz.getQuestions().size() + 1, txt, ans, pts, java.util.Arrays.asList("A", "B", "C")
            ));
            refreshTable(selectedQuiz.getQuestions());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: Geçersiz giriş.");
        }
    }

    private void refreshTable(List<Question> questions) {
        tableModel.setRowCount(0);
        int total = 0;
        for (Question q : questions) {
            tableModel.addRow(new Object[]{
                q.getId(), 
                "Soru Kayıtlı (Gizli)", 
                "Cevap Kayıtlı (Gizli)", 
                q.getPoints()
            });
            total += q.getPoints();
        }
        lblTotalQuestions.setText("Toplam Soru: " + questions.size());
        lblTotalPoints.setText("Toplam Puan: " + total);
        if(selectedQuiz != null) setTitle("Quiz Dashboard - " + selectedQuiz.getQuizName());
    }
}