package managers;
import java.nio.file.*;
import java.util.*;

import models.Application;  
import utils.CSVHelper;  

public class ApplicationManager {
    private final Path csvPath;
    private final String HEADER = "applicationId,jobId,applicantId,applicantName,status";
    private final List<Application> apps = new ArrayList<>();

    public ApplicationManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    private void load() {
        apps.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);
        if (lines.size() <= 1) return;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = CSVHelper.split(line);
            if (p.length < 5) continue;
            try {
                int appId = Integer.parseInt(p[0].trim());
                int jobId = Integer.parseInt(p[1].trim());
                int applicantId = Integer.parseInt(p[2].trim());
                String applicantName = p[3].trim();
                String status = p[4].trim();
                apps.add(new Application(appId, jobId, applicantId, applicantName, status));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid application line: " + line);
            }
        }
    }

    public List<Application> findByJobId(int jobId) {
        List<Application> result = new ArrayList<>();
        for (Application a : apps) if (a.getJobId() == jobId) result.add(a);
        return result;
    }

    public Application findByApplicationId(int applicationId) {
        return apps.stream().filter(a -> a.getApplicationId() == applicationId).findFirst().orElse(null);
    }

    public Application create(int jobId, int applicantId, String applicantName) {
        int id = nextId();
        Application a = new Application(id, jobId, applicantId, applicantName, "Pending");
        apps.add(a);
        persist();
        return a;
    }

    public boolean updateStatus(int applicationId, String newStatus) {
        Application a = findByApplicationId(applicationId);
        if (a == null) return false;
        a.setStatus(newStatus);
        persist();
        return true;
    }

    public int nextId() {
        return apps.stream().mapToInt(Application::getApplicationId).max().orElse(2000) + 1;
    }

    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (Application a : apps) {
            out.add(a.toCSVLine());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }
}
