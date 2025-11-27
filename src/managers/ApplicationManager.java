package managers;
import java.nio.file.*;
import java.util.*;

import models.Application;  
import utils.CSVHelper;  

public class ApplicationManager {
    //define Application path & header
    private final Path csvPath;
    private final String HEADER = "applicationId,jobId,applicantId,applicantName,status";

    //creating array of Applications
    private final List<Application> apps = new ArrayList<>();

    // constructor
    public ApplicationManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    // load - loading data from csv
    private void load() {
        //clear in-memory and reads all lines from csv
        apps.clear(); 
        List<String> lines = CSVHelper.readAllLines(csvPath);

        // skip header 
        if (lines.size() <= 1) return;

        //loop through array
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = CSVHelper.split(line);
            if (p.length < 5) continue;

            //parse info to application ID, job ID, applicant ID, applicant Name, status, then add as valid object to list. otherwise skip if invalid
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

    //return applications by JobID
    public List<Application> findByJobId(int jobId) {
        List<Application> result = new ArrayList<>();
        for (Application a : apps) if (a.getJobId() == jobId) result.add(a);
        return result;
    }

    //return applications by ApplicationID
    public Application findByApplicationId(int applicationId) {
        return apps.stream().filter(a -> a.getApplicationId() == applicationId).findFirst().orElse(null);
    }

    // create Application - generate id, create application obj a with default Pending status, add to memory, save to CSV, return a
    public Application create(int jobId, int applicantId, String applicantName) {
        int id = nextId();
        Application a = new Application(id, jobId, applicantId, applicantName, "Pending");
        apps.add(a);
        persist();
        return a;
    }

    // update Application status - find application by id. if it exists, update status (Hired/Declined) and save to csv then return true. otherwise, return false
    public boolean updateStatus(int applicationId, String newStatus) {
        Application a = findByApplicationId(applicationId);
        if (a == null) return false;
        a.setStatus(newStatus);
        persist();
        return true;
    }

    // generate new id - finds highest application id, return next available id. if list is empty, start at 2001
    public int nextId() {
        return apps.stream().mapToInt(Application::getApplicationId).max().orElse(2000) + 1;
    }

    // persist - saving to csv persistently
    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (Application a : apps) {
            out.add(a.toCSVLine());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }
}
