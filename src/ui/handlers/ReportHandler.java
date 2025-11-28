package ui.handlers;

import models.Report;
import models.User;
import managers.ReportManager;
import utils.MenuPrinter;
import utils.AsciiTable;

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
            MenuPrinter.printHeader("REPORT MENU");
            MenuPrinter.printOption("1", "Create Report");
            MenuPrinter.printOption("2", "View My Reports");
            MenuPrinter.printOption("3", "Update Report");
            MenuPrinter.printOption("4", "Delete Report");
            MenuPrinter.printOption("0", "Back");
            MenuPrinter.prompt("Enter choice");

            switch (readInt()) {
                case 1 -> createReport();
                case 2 -> viewReports();
                case 3 -> updateReport();
                case 4 -> deleteReport();
                case 0 -> { return; }
                default -> MenuPrinter.error("Invalid selection.");
            }
        }
    }

    private void createReport() {
        MenuPrinter.printHeader("CREATE REPORT");

        MenuPrinter.prompt("Enter User ID to report");
        int reportedId = readInt();

        MenuPrinter.prompt("Enter username of reported user");
        String reportedUsername = scanner.nextLine().trim();

        MenuPrinter.prompt("Enter reason for report");
        String reason = scanner.nextLine().trim();

        if (reason.isEmpty()) {
            MenuPrinter.error("Reason cannot be empty.");
            return;
        }

        rm.create(user.getId(), user.getFullName(), reportedId, reportedUsername, reason);
        MenuPrinter.success("Report submitted!");
        MenuPrinter.pause();
    }

    private void viewReports() {
        List<Report> myReports = rm.findByReporterId(user.getId());

        if (myReports.isEmpty()) {
            MenuPrinter.info("You have no reports.");
            MenuPrinter.pause();
            return;
        }

        /* bullet-proof table */
        AsciiTable.print(myReports,
                new String[]{"ID", "Reported User", "Status", "Reason"},
                new int[]{4, 16, 12, 40},
                r -> new String[]{
                        String.valueOf(r.getReportId()),
                        r.getReportedUsername(),
                        r.getStatus(),
                        r.getReason()
                });
        MenuPrinter.pause();
    }

    private void updateReport() {
        List<Report> pending = rm.findPendingByReporter(user.getId());

        if (pending.isEmpty()) {
            MenuPrinter.info("You have no pending reports to update.");
            MenuPrinter.pause();
            return;
        }

        viewReports();

        MenuPrinter.prompt("Enter Report ID to update (0 to cancel)");
        int id = readInt();
        if (id == 0) return;

        Report report = rm.findById(id);
        if (report == null || report.getReporterId() != user.getId()) {
            MenuPrinter.error("Report not found or you don't have permission.");
            return;
        }
        if (!report.getStatus().equalsIgnoreCase("Pending")) {
            MenuPrinter.error("Can only update pending reports.");
            return;
        }

        MenuPrinter.prompt("New reason");
        String newReason = scanner.nextLine().trim();
        if (newReason.isEmpty()) {
            MenuPrinter.error("Reason cannot be empty.");
            return;
        }

        report.setReason(newReason);
        rm.persist();
        MenuPrinter.success("Report updated!");
        MenuPrinter.pause();
    }

    private void deleteReport() {
        List<Report> pending = rm.findPendingByReporter(user.getId());

        if (pending.isEmpty()) {
            MenuPrinter.info("You have no pending reports to delete.");
            MenuPrinter.pause();
            return;
        }

        viewReports();

        MenuPrinter.prompt("Enter Report ID to delete (0 to cancel)");
        int id = readInt();
        if (id == 0) return;

        Report report = rm.findById(id);
        if (report == null || report.getReporterId() != user.getId()) {
            MenuPrinter.error("Report not found or you don't have permission.");
            return;
        }
        if (!report.getStatus().equalsIgnoreCase("Pending")) {
            MenuPrinter.error("Can only delete pending reports.");
            return;
        }

        if (rm.delete(id))
            MenuPrinter.success("Report deleted!");
        else
            MenuPrinter.error("Could not delete report.");
        MenuPrinter.pause();
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                MenuPrinter.error("Please enter a valid number.");
            }
        }
    }
}