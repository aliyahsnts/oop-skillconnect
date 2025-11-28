package managers;

import java.util.*;
import models.JobPosting;
import utils.ApplicationFormGenerator; // Import for list/CSV helpers

// Assuming this file exists and follows the BaseManager convention.
public class JobPostingManager extends BaseManager<JobPosting> {

    // UPDATED HEADER to include applicationQuestions
    public JobPostingManager(String csvFilePath) {
        super(csvFilePath, "jobId,jobName,description,hoursNeeded,payment,status,recruiterName,applicationQuestions");
    }

    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected JobPosting parseEntity(String[] parts) {
        int jobId = Integer.parseInt(parts[0].trim());
        String jobName = parts[1].trim();
        String description = parts[2].trim();
        String hoursNeeded = parts[3].trim();
        double payment = Double.parseDouble(parts[4].trim());
        String status = parts[5].trim();
        String recruiterName = parts[6].trim();
        
        // NEW FIELD: applicationQuestions
        List<String> applicationQuestions = parts.length > 7 ? 
                                            ApplicationFormGenerator.csvToList(parts[7].trim()) : 
                                            List.of(); // Default to empty list
        
        return new JobPosting(jobId, jobName, description, hoursNeeded, payment, 
                              status, recruiterName, applicationQuestions);
    }

    @Override
    protected String toCSVLine(JobPosting job) {
        return job.toCSVLine();
    }

    @Override
    protected int getId(JobPosting job) {
        return job.getJobId();
    }

    @Override
    protected int getMinimumColumns() {
        return 8; 
    }

    @Override
    protected String getEntityName() {
        return "job posting";
    }

    @Override
    protected int getStartingId() {
        return 1001;
    }

    // =========================================
    //           CUSTOM FIND METHODS
    // =========================================
    
    // Find jobs by recruiter name
    public List<JobPosting> findByRecruiterName(String recruiterName) {
        List<JobPosting> result = new ArrayList<>();
        for (JobPosting j : entities) {
            if (j.getRecruiterName().equalsIgnoreCase(recruiterName)) {
                result.add(j);
            }
        }
        return result;
    }

    // =========================================
    //           CREATE & UPDATE
    // =========================================

    // UPDATED create method signature
    public JobPosting create(String jobName, String description, String hoursNeeded, 
                             double payment, String recruiterName, List<String> applicationQuestions) {
        int id = nextId();
        // Use the new JobPosting constructor
        JobPosting job = new JobPosting(id, jobName, description, hoursNeeded, payment, 
                                        "Available", recruiterName, applicationQuestions);
        entities.add(job);
        persist();
        return job;
    }
    
    // UPDATED update method signature
    public boolean update(int jobId, String jobName, String description, String hoursNeeded, 
                          Double payment, String status, List<String> applicationQuestions) {
        JobPosting job = findById(jobId);
        if (job == null) return false;

        if (jobName != null) job.setJobName(jobName);
        if (description != null) job.setDescription(description);
        if (hoursNeeded != null) job.setHoursNeeded(hoursNeeded);
        if (payment != null) job.setPayment(payment);
        if (status != null) job.setStatus(status);
        if (applicationQuestions != null) job.setApplicationQuestions(applicationQuestions); // NEW UPDATE
        
        persist();
        return true;
    }
}