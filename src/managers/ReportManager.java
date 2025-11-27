package managers;

import models.Report;

public class ReportManager extends BaseManager<Report> {

    public ReportManager(String csvFilePath) {
        super(csvFilePath, "reportId,reporterId,reporterName,reportedUserId,reportedUsername,reason,timestamp,status");
    }

    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected Report parseEntity(String[] parts) {
        int reportId = Integer.parseInt(parts[0].trim());
        int reporterId = Integer.parseInt(parts[1].trim());
        String reporterName = parts[2].trim();
        int reportedUserId = Integer.parseInt(parts[3].trim());
        String reportedUsername = parts[4].trim();
        String reason = parts[5].trim();
        String timestamp = parts[6].trim();
        String status = parts[7].trim();
        
        return new Report(reportId, reporterId, reporterName, reportedUserId, 
                         reportedUsername, reason, timestamp, status);
    }

    @Override
    protected String toCSVLine(Report report) {
        return report.toCSVLine();
    }

    @Override
    protected int getId(Report report) {
        return report.getReportId();
    }

    @Override
    protected int getMinimumColumns() {
        return 8;
    }

    @Override
    protected String getEntityName() {
        return "report";
    }

    @Override
    protected int getStartingId() {
        return 4001;
    }

    // =========================================
    //           CREATE & UPDATE
    // =========================================

    public Report create(int reporterId, String reporterName, int reportedUserId, 
                        String reportedUsername, String reason) {
        int id = nextId();
        String timestamp = Report.getCurrentTimestamp();
        Report r = new Report(id, reporterId, reporterName, reportedUserId, 
                             reportedUsername, reason, timestamp, "Pending");
        entities.add(r);
        persist();
        return r;
    }

    public boolean updateStatus(int id, String newStatus) {
        Report r = findById(id);
        if (r == null) return false;
        r.setStatus(newStatus);
        persist();
        return true;
    }
}