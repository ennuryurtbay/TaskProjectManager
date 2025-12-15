package com.project.model;

import java.util.List;


public abstract class ReportGenerator {
    

    public abstract String generateReport(List<Project> projects);
    

    public void printHeader(String reportType) {
        System.out.println("--- " + reportType + " Rapor Basligi ---");
    }
}