package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Report {
    /* ==== CHANGES SUMMARY =========================================
       1. Public constants for the four valid statuses.
       2. Validation helper isValidStatus().
       3. Constructor defaults to STATUS_PENDING if null is passed.
       ============================================================== */
    public static final String STATUS_PENDING   = "Pending";
    public static final String STATUS_REVIEWED  = "Reviewed";
    public static final String STATUS_RESOLVED  = "Resolved";
    public static final String STATUS_DISMISSED = "Dismissed";

    private final int reportId;
    private final int reporterId;
    private final String reporterName;
    private final int reportedUserId;
    private final String reportedUsername;
    private final String timestamp;
    private String reason;
    private String status;

    public Report(int reportId, int reporterId, String reporterName, int reportedUserId,
                 String reportedUsername, String reason, String timestamp, String status) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reportedUserId = reportedUserId;
        this.reportedUsername = reportedUsername;
        this.reason = reason;
        this.timestamp = timestamp;
        this.status = (status != null) ? status : STATUS_PENDING;
    }

    /* ---------- Getters ---------- */
    public int getReportId() { return reportId; }
    public int getReporterId() { return reporterId; }
    public String getReporterName() { return reporterName; }
    public int getReportedUserId() { return reportedUserId; }
    public String getReportedUsername() { return reportedUsername; }
    public String getReason() { return reason; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    /* ---------- Setters ---------- */
    public void setStatus(String status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }

    /* ---------- Helpers ---------- */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static boolean isValidStatus(String status) {
        return status != null &&
               (status.equals(STATUS_PENDING) ||
                status.equals(STATUS_REVIEWED) ||
                status.equals(STATUS_RESOLVED) ||
                status.equals(STATUS_DISMISSED));
    }

    /* ---------- CSV ---------- */
    public String toCSVLine() {
        return reportId + "," + reporterId + "," + escape(reporterName) + ","
             + reportedUserId + "," + escape(reportedUsername) + "," + escape(reason) + ","
             + escape(timestamp) + "," + escape(status);
    }

    public String displayString() {
        return "Report ID: " + reportId
             + "\nReporter: " + reporterName + " (ID: " + reporterId + ")"
             + "\nReported User: " + reportedUsername + " (ID: " + reportedUserId + ")"
             + "\nReason: " + reason
             + "\nTimestamp: " + timestamp
             + "\nStatus: " + status;
    }

    private String escape(String s) {
        return (s == null) ? "" : s.replace(",", " ");
    }
}