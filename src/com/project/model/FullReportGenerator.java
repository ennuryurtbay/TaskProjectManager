package com.project.model;

import java.util.List;

public class FullReportGenerator extends ReportGenerator {
    

    @Override
    public String generateReport(List<Project> projects) {
        printHeader("TAM PROJE VE GOREV RAPORU");
        StringBuilder report = new StringBuilder();
        
        if (projects.isEmpty()) {
            report.append("Proje kaydi bulunamadi.\n");
            return report.toString();
        }
        
        for (Project p : projects) {
            report.append("\nPROJE: ").append(p.getProjectName()).append(" (ID: ").append(p.getProjectId()).append(")\n");
            report.append("----------------------------------------------------\n");
            
            if (p.getTasks().isEmpty()) {
                report.append("  Bu projede gorev bulunmamaktadir.\n");
                continue;
            }
            
            for (Task t : p.getTasks()) {
                String completionStatus = t.isCompleted() ? "[TAMAMLANDI]" : "[DEVAM EDIYOR]";
                report.append(String.format("  - %s (Oncelik: %d, Bitis: %s) %s\n", 
                                            t.getTitle(), 
                                            t.getPriority(), 
                                            t.getDueDate().toString(),
                                            completionStatus));
            }
        }
        return report.toString();
    }
}