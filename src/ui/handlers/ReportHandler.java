package ui.handlers;

import models.Report;
import models.User;
import managers.ReportManager;
import java.util.List;
import java.util.Scanner;

public class ReportHandler {
    private final User user;
    private final ReportManager rm;
    private final Scanner scanner;

    public ReportHandler(User user, ReportManager rm, Scanner scanner) {
        this.user = user;
        this.rm = rm;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n===== REPORT MENU =====");
            System.out.println("1. Create Report");
            System.out.println("2. View My Reports");
            System.out.println("3. Update Report");
            System.out.println("4. Delete Report");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            
            switch (readInt()) {
                case 1 -> createReport();
                case 2 -> viewReports();
                case 3 -> updateReport();
                case 4 -> deleteReport();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    private void createReport() {
        System.out.println("\n--- Create New Report ---");
        
        System.out.print("Enter User ID to report: ");
        int reportedId = readInt();
        
        System.out.print("Enter username of reported user: ");
        String reportedUsername = scanner.nextLine().trim();
        
        System.out.print("Enter reason for report: ");
        String reason = scanner.nextLine().trim();
        
        if (reason.isEmpty()) {
            System.out.println("ERROR: Reason cannot be empty.");
            return;
        }
        
        rm.create(user.getId(), user.getFullName(), reportedId, reportedUsername, reason);
        System.out.println("SUCCESS: Report submitted!");
    }

    private void viewReports() {
        List<Report> myReports = rm.findByReporterId(user.getId());
            
        if (myReports.isEmpty()) {
            System.out.println("You have no reports.");
            return;
        }
        
        System.out.println("\n--- My Reports ---");
        for (Report r : myReports) {
            System.out.println(r.displayString());
            System.out.println("-------------------------------------");
        }
    }

    private void updateReport() {
        List<Report> myReports = rm.findPendingByReporter(user.getId());
            
        if (myReports.isEmpty()) {
            System.out.println("You have no pending reports to update.");
            return;
        }
        
        viewReports();
        
        System.out.print("\nEnter Report ID to update (0 to cancel): ");
        int id = readInt();
        if (id == 0) return;
        
        Report report = rm.findById(id);
        if (report == null || report.getReporterId() != user.getId()) {
            System.out.println("ERROR: Report not found or you don't have permission.");
            return;
        }
        
        if (!report.getStatus().equalsIgnoreCase("Pending")) {
            System.out.println("ERROR: Can only update pending reports.");
            return;
        }
        
        System.out.print("New reason: ");
        String newReason = scanner.nextLine().trim();
        
        if (newReason.isEmpty()) {
            System.out.println("ERROR: Reason cannot be empty.");
            return;
        }
        
        report.setReason(newReason);
        rm.persist();
        System.out.println("SUCCESS: Report updated!");
    }

    private void deleteReport() {
        List<Report> myReports = rm.findPendingByReporter(user.getId());
            
        if (myReports.isEmpty()) {
            System.out.println("You have no pending reports to delete.");
            return;
        }
        
        viewReports();
        
        System.out.print("\nEnter Report ID to delete (0 to cancel): ");
        int id = readInt();
        if (id == 0) return;
        
        Report report = rm.findById(id);
        if (report == null || report.getReporterId() != user.getId()) {
            System.out.println("ERROR: Report not found or you don't have permission.");
            return;
        }
        
        if (!report.getStatus().equalsIgnoreCase("Pending")) {
            System.out.println("ERROR: Can only delete pending reports.");
            return;
        }
        
        if (rm.delete(id)) {
            System.out.println("SUCCESS: Report deleted!");
        } else {
            System.out.println("ERROR: Could not delete report.");
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}