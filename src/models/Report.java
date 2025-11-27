package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Report {
    private final int reportId;
    private final int reporterId;
    private final String reporterName;
    private final int reportedUserId;
    private final String reportedUsername;
    private final String timestamp;
    private  String reason;
    private String status; // Pending, Reviewed, Resolved, Dismissed

    public Report(int reportId, int reporterId, String reporterName, int reportedUserId, 
                 String reportedUsername, String reason, String timestamp, String status) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reportedUserId = reportedUserId;
        this.reportedUsername = reportedUsername;
        this.reason = reason;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Getters
    public int getReportId() { return reportId; }
    public int getReporterId() { return reporterId; }
    public String getReporterName() { return reporterName; }
    public int getReportedUserId() { return reportedUserId; }
    public String getReportedUsername() { return reportedUsername; }
    public String getReason() { return reason; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    // Setter
    public void setStatus(String status) { this.status = status; }

    public void setReason(String reason) { this.reason = reason; }

    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String toCSVLine() {
        return reportId + "," + reporterId + "," + escape(reporterName) + "," + 
               reportedUserId + "," + escape(reportedUsername) + "," + escape(reason) + "," + 
               escape(timestamp) + "," + escape(status);
    }

    public String displayString() {
        return "Report ID: " + reportId +
               "\nReporter: " + reporterName + " (ID: " + reporterId + ")" +
               "\nReported User: " + reportedUsername + " (ID: " + reportedUserId + ")" +
               "\nReason: " + reason +
               "\nTimestamp: " + timestamp +
               "\nStatus: " + status;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}