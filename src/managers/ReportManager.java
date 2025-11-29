package managers;

import models.Report;
import java.util.List;
import java.util.stream.Collectors;

public class ReportManager extends BaseManager<Report> {

    public ReportManager(String csvFilePath) {
        super(csvFilePath, "reportId,reporterId,reporterName,reportedUserId,reportedUsername,reason,timestamp,status");
    }

    /* ==== CHANGES SUMMARY =========================================
       Added findByStatus(String) to enable admin filtering.
       ============================================================== */
    @Override
    protected Report parseEntity(String[] parts) {
        int reportId      = Integer.parseInt(parts[0].trim());
        int reporterId    = Integer.parseInt(parts[1].trim());
        String reporterName   = parts[2].trim();
        int reportedUserId    = Integer.parseInt(parts[3].trim());
        String reportedUsername = parts[4].trim();
        String reason     = parts[5].trim();
        String timestamp  = parts[6].trim();
        String status     = parts[7].trim();
        return new Report(reportId, reporterId, reporterName, reportedUserId,
                         reportedUsername, reason, timestamp, status);
    }

    @Override
    protected String toCSVLine(Report report) { return report.toCSVLine(); }

    @Override
    protected int getId(Report report) { return report.getReportId(); }

    @Override
    protected int getMinimumColumns() { return 8; }

    @Override
    protected String getEntityName() { return "report"; }

    @Override
    protected int getStartingId() { return 4001; }

    /* ---------- CRUD ---------- */
    public Report create(int reporterId, String reporterName, int reportedUserId,
                        String reportedUsername, String reason) {
        int id = nextId();
        String timestamp = Report.getCurrentTimestamp();
        Report r = new Report(id, reporterId, reporterName, reportedUserId,
                             reportedUsername, reason, timestamp, Report.STATUS_PENDING);
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

    /* ---------- Queries ---------- */
    public List<Report> findByReporterId(int reporterId) {
        return entities.stream()
                .filter(r -> r.getReporterId() == reporterId)
                .collect(Collectors.toList());
    }

    public List<Report> findByReportedUserId(int reportedUserId) {
        return entities.stream()
                .filter(r -> r.getReportedUserId() == reportedUserId)
                .collect(Collectors.toList());
    }

    public List<Report> findByStatus(String status) {
        return entities.stream()
                .filter(r -> r.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Report> findPendingByReporter(int reporterId) {
        return entities.stream()
                .filter(r -> r.getReporterId() == reporterId)
                .filter(r -> r.getStatus().equalsIgnoreCase(Report.STATUS_PENDING))
                .collect(Collectors.toList());
    }
}