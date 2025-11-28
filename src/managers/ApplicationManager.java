package managers;

import java.util.*;
import models.Application;
import utils.ApplicationFormGenerator; // Import for list/CSV helpers

public class ApplicationManager extends BaseManager<Application> {

    // UPDATED HEADER
    public ApplicationManager(String csvFilePath) {
        super(csvFilePath, "applicationId,jobId,applicantId,applicantName,status,applicationAnswers,resumeFilePath");
    }

    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected Application parseEntity(String[] parts) {
        int appId = Integer.parseInt(parts[0].trim());
        int jobId = Integer.parseInt(parts[1].trim());
        int applicantId = Integer.parseInt(parts[2].trim());
        String applicantName = parts[3].trim();
        String status = parts[4].trim();
        
        // NEW FIELDS
        List<String> applicationAnswers = parts.length > 5 ? ApplicationFormGenerator.csvToList(parts[5].trim()) : List.of();
        String resumeFilePath = parts.length > 6 ? parts[6].trim() : "N/A";
        
        return new Application(appId, jobId, applicantId, applicantName, status, applicationAnswers, resumeFilePath);
    }

    @Override
    protected String toCSVLine(Application app) {
        return app.toCSVLine();
    }

    @Override
    protected int getId(Application app) {
        return app.getApplicationId();
    }

    @Override
    protected int getMinimumColumns() {
        return 7; // UPDATED from 5 to 7
    }

    @Override
    protected String getEntityName() {
        return "application";
    }

    @Override
    protected int getStartingId() {
        return 2001;
    }

    // =========================================
    //           CUSTOM FIND METHODS
    // =========================================

    public List<Application> findByJobId(int jobId) {
        List<Application> result = new ArrayList<>();
        for (Application a : entities) {
            if (a.getJobId() == jobId) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Application> findByApplicantId(int applicantId) {
        List<Application> result = new ArrayList<>();
        for (Application a : entities) {
            if (a.getApplicantId() == applicantId) {
                result.add(a);
            }
        }
        return result;
    }

    public Application findByApplicationId(int applicationId) {
        return findById(applicationId);
    }

    // =========================================
    //           CREATE & UPDATE 
    // =========================================

    // UPDATED create method signature
    public Application create(int jobId, int applicantId, String applicantName, 
                              List<String> answers, String resumeFilePath) {
        int id = nextId();
        // Use the new Application constructor
        Application a = new Application(id, jobId, applicantId, applicantName, "Pending", answers, resumeFilePath);
        entities.add(a);
        persist();
        return a;
    }

    public boolean updateStatus(int applicationId, String newStatus) {
        Application a = findById(applicationId);
        if (a == null) return false;
        a.setStatus(newStatus);
        persist();
        return true;
    }
}