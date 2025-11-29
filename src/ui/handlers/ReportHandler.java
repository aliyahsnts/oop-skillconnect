package ui.handlers;

import models.Report;
import models.User;
import managers.ReportManager;
import managers.UserManager;
import utils.MenuPrinter;
import utils.AsciiTable;
import utils.Refresh;

import java.util.List;
import java.util.Scanner;

public class ReportHandler {
    private final User user;
    private final ReportManager rm;
    private final UserManager um;
    private final Scanner scanner;

    public ReportHandler(User user, ReportManager rm, UserManager um, Scanner scanner) {
        this.user = user;
        this.rm = rm;
        this.um = um;
        this.scanner = scanner;
    }

    /* ----------------------------------------------------------
       MAIN REPORT SUB-MENU (clear-screen + slim table)
    ---------------------------------------------------------- */
    public void showMenu() {
        while (true) {
            Refresh.refreshTerminal();
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

    /* ----------------------------------------------------------
       CREATE
    ---------------------------------------------------------- */
    private void createReport() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("CREATE REPORT");

        MenuPrinter.prompt("Enter User ID to report");
        int reportedId = readInt();

        if (reportedId == user.getId()) {
            MenuPrinter.error("You cannot report yourself.");
            MenuPrinter.pause();
            return;
        }
        User reportedUser = um.findById(reportedId);
        if (reportedUser == null) {
            MenuPrinter.error("User not found.");
            MenuPrinter.pause();
            return;
        }

        MenuPrinter.prompt("Enter username of reported user");
        String reportedUsername = scanner.nextLine().trim();
        if (!reportedUser.getUsername().equalsIgnoreCase(reportedUsername)) {
            MenuPrinter.error("Username does not match the User ID.");
            MenuPrinter.pause();
            return;
        }

        MenuPrinter.prompt("Enter reason for report (min 10 characters)");
        String reason = scanner.nextLine().trim();
        if (reason.length() < 10) {
            MenuPrinter.error("Reason must be at least 10 characters.");
            MenuPrinter.pause();
            return;
        }

        rm.create(user.getId(), user.getFullName(), reportedId, reportedUsername, reason);
        MenuPrinter.success("Report submitted!");
        MenuPrinter.pause();
    }

    /* ----------------------------------------------------------
       VIEW (clear-screen + slim table)
    ---------------------------------------------------------- */
    private void viewReports() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("MY REPORTS");

        List<Report> myReports = rm.findByReporterId(user.getId());
        if (myReports.isEmpty()) {
            MenuPrinter.info("You have no reports.");
            MenuPrinter.pause();
            return;
        }

        /* 80-char layout:  ID(4) | Reported(14) | Status(10) | Reason(48) */
        System.out.printf("Your reports: %d%n", myReports.size());
        System.out.println("─".repeat(79));

        AsciiTable.print(myReports,
                new String[]{"ID", "Reported User", "Status", "Reason"},
                new int[]{4, 14, 10, 48},
                r -> new String[]{
                        String.valueOf(r.getReportId()),
                        truncate(r.getReportedUsername(), 14),
                        truncate(r.getStatus(), 10),
                        wordWrap(r.getReason(), 48)   // <-- wrapped, not chopped
                });
        System.out.println("─".repeat(79));
        MenuPrinter.pause();
    }

    /* ----------------------------------------------------------
       UPDATE (only pending)
    ---------------------------------------------------------- */
    private void updateReport() {
        List<Report> pending = rm.findPendingByReporter(user.getId());
        if (pending.isEmpty()) {
            MenuPrinter.info("You have no pending reports to update.");
            MenuPrinter.pause();
            return;
        }
        viewReports();   // show the table again
        MenuPrinter.prompt("Enter Report ID to update (0 to cancel)");
        int id = readInt();
        if (id == 0) return;
        Report report = rm.findById(id);
        if (report == null || report.getReporterId() != user.getId()) {
            MenuPrinter.error("Report not found or no permission.");
            return;
        }
        if (!report.getStatus().equalsIgnoreCase(Report.STATUS_PENDING)) {
            MenuPrinter.error("Can only update pending reports.");
            return;
        }
        MenuPrinter.prompt("New reason (min 10 characters)");
        String newReason = scanner.nextLine().trim();
        if (newReason.length() < 10) {
            MenuPrinter.error("Reason too short.");
            return;
        }
        report.setReason(newReason);
        rm.persist();
        MenuPrinter.success("Report updated!");
        MenuPrinter.pause();
    }

    /* ----------------------------------------------------------
       DELETE (only pending)
    ---------------------------------------------------------- */
    private void deleteReport() {
        List<Report> pending = rm.findPendingByReporter(user.getId());
        if (pending.isEmpty()) {
            MenuPrinter.info("You have no pending reports to delete.");
            MenuPrinter.pause();
            return;
        }
        viewReports();   // show the table again
        MenuPrinter.prompt("Enter Report ID to delete (0 to cancel)");
        int id = readInt();
        if (id == 0) return;
        Report report = rm.findById(id);
        if (report == null || report.getReporterId() != user.getId()) {
            MenuPrinter.error("Report not found or no permission.");
            return;
        }
        if (!report.getStatus().equalsIgnoreCase(Report.STATUS_PENDING)) {
            MenuPrinter.error("Can only delete pending reports.");
            return;
        }
        boolean ok = rm.delete(id);
        MenuPrinter.info(ok ? "Report deleted!" : "Could not delete report.");
        MenuPrinter.pause();
    }

    /* ----------------------------------------------------------
       HELPERS
    ---------------------------------------------------------- */
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                MenuPrinter.error("Please enter a valid number.");
            }
        }
    }

    /* truncate without breaking in the middle of a word */
    private static String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        int lastSpace = s.lastIndexOf(' ', max - 3);
        return (lastSpace > 0 ? s.substring(0, lastSpace) : s.substring(0, max - 3)) + "...";
    }

    /* simple word-wrap that preserves whole words */
    private static String wordWrap(String text, int width) {
        if (text == null || text.length() <= width) return text;
        StringBuilder out = new StringBuilder();
        int start = 0, end;
        while (start < text.length()) {
            end = Math.min(start + width, text.length());
            if (end < text.length() && text.charAt(end) != ' ') {
                while (end > start && text.charAt(end - 1) != ' ') end--;
                if (end == start) end = start + width; // force break long word
            }
            out.append(text, start, end).append('\n');
            start = end;
            while (start < text.length() && text.charAt(start) == ' ') start++; // trim leading space
        }
        return out.toString().trim();
    }
}