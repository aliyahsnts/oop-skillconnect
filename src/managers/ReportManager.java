package managers;

import java.nio.file.*;
import java.util.*;
import models.Report;
import utils.CSVHelper;

public class ReportManager {
    private final Path csvPath;
    private final String HEADER = "reportId,reporterId,reporterName,reportedUserId,reportedUsername,reason,timestamp,status";
    private final List<Report> reports = new ArrayList<>();

    public ReportManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    private void load() {
        reports.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);
        if (lines.size() <= 1) return;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = CSVHelper.split(line);
            if (p.length < 8) continue;

            try {
                int reportId = Integer.parseInt(p[0].trim());
                int reporterId = Integer.parseInt(p[1].trim());
                String reporterName = p[2].trim();
                int reportedUserId = Integer.parseInt(p[3].trim());
                String reportedUsername = p[4].trim();
                String reason = p[5].trim();
                String timestamp = p[6].trim();
                String status = p[7].trim();
                reports.add(new Report(reportId, reporterId, reporterName, reportedUserId, reportedUsername, reason, timestamp, status));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid report line: " + line);
            }
        }
    }

    public List<Report> findAll() {
        return new ArrayList<>(reports);
    }

    public Report findById(int id) {
        return reports.stream().filter(r -> r.getReportId() == id).findFirst().orElse(null);
    }

    public Report create(int reporterId, String reporterName, int reportedUserId, String reportedUsername, String reason) {
        int id = nextId();
        String timestamp = Report.getCurrentTimestamp();
        Report r = new Report(id, reporterId, reporterName, reportedUserId, reportedUsername, reason, timestamp, "Pending");
        reports.add(r);
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

    public boolean delete(int id) {
        boolean removed = reports.removeIf(r -> r.getReportId() == id);
        if (removed) persist();
        return removed;
    }

    public int nextId() {
        return reports.stream().mapToInt(Report::getReportId).max().orElse(4000) + 1;
    }

    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (Report r : reports) {
            out.add(r.toCSVLine());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }
}