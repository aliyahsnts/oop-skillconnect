package managers;

import java.util.*;
import models.Application;

public class ApplicationManager extends BaseManager<Application> {

    public ApplicationManager(String csvFilePath) {
        super(csvFilePath, "applicationId,jobId,applicantId,applicantName,status");
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
        
        return new Application(appId, jobId, applicantId, applicantName, status);
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
        return 5;
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

    public Application create(int jobId, int applicantId, String applicantName) {
        int id = nextId();
        Application a = new Application(id, jobId, applicantId, applicantName, "Pending");
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