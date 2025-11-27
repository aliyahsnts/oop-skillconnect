package managers;

import java.util.*;
import models.JobPosting;

public class JobPostingManager extends BaseManager<JobPosting> {

    public JobPostingManager(String csvFilePath) {
        super(csvFilePath, "jobId,jobName,description,hoursNeeded,payment,status,recruiterName");
    }

    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected JobPosting parseEntity(String[] parts) {
        int jobId = Integer.parseInt(parts[0].trim());
        String jobName = parts[1].trim();
        String desc = parts[2].trim();
        String hours = parts[3].trim();
        double payment = Double.parseDouble(parts[4].trim());
        String status = parts[5].trim();
        String recruiterName = parts[6].trim();
        
        return new JobPosting(jobId, jobName, desc, hours, payment, status, recruiterName);
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
        return 7;
    }

    @Override
    protected String getEntityName() {
        return "job posting";
    }

    @Override
    protected int getStartingId() {
        return 101;
    }

    // =========================================
    //           CUSTOM FIND METHODS
    // =========================================

    public List<JobPosting> findByRecruiter(String name) {
        List<JobPosting> result = new ArrayList<>();
        for (JobPosting j : entities) {
            if (j.getRecruiterName().equalsIgnoreCase(name)) {
                result.add(j);
            }
        }
        return result;
    }

    // =========================================
    //           CREATE & UPDATE
    // =========================================

    public JobPosting create(String name, String desc, String hours,
                             double payment, String recruiterName) {
        JobPosting job = new JobPosting(
            nextId(),
            name,
            desc,
            hours,
            payment,
            "Available",
            recruiterName
        );

        entities.add(job);
        persist();
        return job;
    }

    public boolean update(int jobId, String jobName, String description, 
                         String hoursNeeded, Double payment, String status) {
        JobPosting j = findById(jobId);
        if (j == null) return false;
        
        if (jobName != null && !jobName.isEmpty()) 
            j.setJobName(jobName);
        if (description != null && !description.isEmpty()) 
            j.setDescription(description);
        if (hoursNeeded != null && !hoursNeeded.isEmpty()) 
            j.setHoursNeeded(hoursNeeded);
        if (payment != null) 
            j.setPayment(payment);
        if (status != null && !status.isEmpty()) 
            j.setStatus(status);
            
        persist();
        return true;
    }
}