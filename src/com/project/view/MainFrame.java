package com.project.view;

import com.project.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;      
import java.util.List;
import javax.swing.JFileChooser; 

public class MainFrame extends JFrame {
    private QuizManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalQuestions, lblTotalPoints;
    private JList<Quiz> quizJList; 
    private DefaultListModel<Quiz> listModel;
    private Quiz selectedQuiz;

    public MainFrame(QuizManager manager) {
        // GÜN 16: Program açıldığında verileri dosyadan yükle
        List<Quiz> savedQuizzes = FileManager.loadQuizzes();
        if (savedQuizzes != null && !savedQuizzes.isEmpty()) {
            for (Quiz q : savedQuizzes) {
                manager.addQuiz(q);
            }
        }
        
        this.manager = manager;
        setTitle("Quiz Management System - GÜN 17 (Export Support)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 650);
        setLocationRelativeTo(null);

      
        Color headerColor = new Color(33, 37, 41);
        Color sideColor = new Color(241, 243, 245);

        // 1. SOL PANEL (Sınav Navigasyonu)
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
        stats.setBackground(sideColor);
        stats.add(lblTotalQuestions); stats.add(lblTotalPoints);
        sidePanel.add(stats, BorderLayout.SOUTH);

        // 2. ÜST PANEL (Yönetim Araç Çubuğu)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(headerColor);
        
   
        JButton btnAddQuiz = new JButton("Yeni Sınav");
        JButton btnAddQuestion = new JButton("Soru Ekle");
        JButton btnStart = new JButton("Sınavı Başlat");
        JButton btnReport = new JButton("Yönetici Raporu");
        JButton btnSave = new JButton("Verileri Kaydet (.dat)");
        JButton btnExport = new JButton("Raporu İndir (.txt)");

        topPanel.add(btnAddQuiz); 
        topPanel.add(btnAddQuestion); 
        topPanel.add(btnStart); 
        topPanel.add(btnReport);
        topPanel.add(btnSave);
        topPanel.add(btnExport);

        // 3. MERKEZ PANEL (Maskelenmiş Veri Tablosu)
        String[] columns = {"ID", "Soru Durumu", "Cevap Durumu", "Puan Değeri"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        // --- BUTON AKSİYONLARI ---

        // GÜN 17: TXT OLARAK DIŞA AKTAR
        btnExport.addActionListener(e -> {
            if (manager.getQuizzes().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Dışa aktarılacak veri bulunamadı!");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Raporu Kaydedilecek Yeri Seçin");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == 0) { // 0 = JFileChooser.APPROVE_VALUE
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
                }
                
                FullReportGenerator gen = new FullReportGenerator();
                String content = gen.generateReport(manager.getQuizzes());
                FileManager.exportToText(content, fileToSave);
                
                JOptionPane.showMessageDialog(this, "Rapor başarıyla indirildi:\n" + fileToSave.getName());
            }
        });

        // GÜN 16: VERİLERİ SİSTEME KAYDET
        btnSave.addActionListener(e -> {
            FileManager.saveQuizzes(manager.getQuizzes());
            JOptionPane.showMessageDialog(this, "Sistem verileri (quizzes.dat) güncellendi.");
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
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Yönetici Özeti", JOptionPane.INFORMATION_MESSAGE);
        });

        add(topPanel, BorderLayout.NORTH);
        add(sidePanel, BorderLayout.WEST);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
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
            JOptionPane.showMessageDialog(this, "Aktif sınavda soru bulunamadı!");
            return;
        }
        int score = 0;
        for (Question q : selectedQuiz.getQuestions()) {
            String ans = JOptionPane.showInputDialog(this, "SORU: " + q.getText());
            if (ans != null && q.checkAnswer(ans)) score += q.getPoints();
        }
        JOptionPane.showMessageDialog(this, "Sınav Tamamlandı.\nToplam Skor: " + score);
    }

    private void addNewQuestion() {
        if (selectedQuiz == null) return;
        try {
            String txt = JOptionPane.showInputDialog(this, "Soru:");
            String ans = JOptionPane.showInputDialog(this, "Cevap:");
            int pts = Integer.parseInt(JOptionPane.showInputDialog(this, "Puan:"));
            selectedQuiz.addQuestion(new MultipleChoiceQuestion(
                selectedQuiz.getQuestions().size() + 1, txt, ans, pts, java.util.Arrays.asList("A", "B")
            ));
            refreshTable(selectedQuiz.getQuestions());
        } catch (Exception ex) {}
    }

    private void refreshTable(List<Question> questions) {
        tableModel.setRowCount(0);
        int total = 0;
        for (Question q : questions) {
            tableModel.addRow(new Object[]{q.getId(), "Gizli Soru", "Gizli Cevap", q.getPoints()});
            total += q.getPoints();
        }
        lblTotalQuestions.setText("Toplam Soru: " + questions.size());
        lblTotalPoints.setText("Toplam Puan: " + total);
        if(selectedQuiz != null) setTitle("Quiz System - " + selectedQuiz.getQuizName());
    }
}