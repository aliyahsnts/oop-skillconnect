package managers;
import java.nio.file.*;
import java.util.*;

import models.Application;
import models.JobPosting;  
import utils.CSVHelper;  

public class JobPostingManager {
    //define Job path & header
    private final Path csvPath;
    private final String HEADER = "jobId,jobName,description,hoursNeeded,payment,status";

    //creating array of Jobs
    private final List<JobPosting> jobs = new ArrayList<>();

    // constructor
    public JobPostingManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load(); //load jobs
        loadApplications();   // load applications
    }

    // load - loading data from csv
    private void load() {
        //clear in-memory and reads all lines from csv
        jobs.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);

        // skip header 
        if (lines.size() <= 1) return; // header or empty

        //loop through array
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = CSVHelper.split(line);
            // Expecting 6 columns
            if (p.length < 6) continue;

            //parse info to job ID, job Name, description, hours, payment, status. then add as valid object to list. otherwise skip if invalid
            try {
                int jobId = Integer.parseInt(p[0].trim());
                String jobName = p[1].trim();
                String desc = p[2].trim();
                String hours = p[3].trim();
                double payment = Double.parseDouble(p[4].trim());
                String status = p[5].trim();
                String recruiterName = p[6].trim();
                jobs.add(new JobPosting(jobId, jobName, desc, hours, payment, status, recruiterName));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid job line: " + line);
            }
        }
    }

    // NEW: LOAD applications from CSV
    private void loadApplications() {
        // Placeholder: applications are loaded and managed by ApplicationManager
        // This method can be expanded if needed to cache applications here
    }

    //return all jobs
    public List<Object> findAll() {
        return new ArrayList<>(jobs);
    }

    //return jobs by JobID
    public JobPosting findById(int id) {
        return jobs.stream().filter(j -> j.getJobId() == id).findFirst().orElse(null);
    }

    //return jobs by recruiterID
    public List<JobPosting> findByRecruiter(String name) {
        List<JobPosting> result = new ArrayList<>();
        for (JobPosting j : jobs) {
            if (j.getRecruiterName().equalsIgnoreCase(name)) {
                result.add(j);
            }
        }
        return result;
    }

    //find by applicationID
    public List<Application> findByApplicantId(int applicantId) {
        // This method should be implemented in ApplicationManager
        // Returning empty list as placeholder
        return new ArrayList<>();
    }

    // create Job Posting - generate id, create job obj j with default Available status, add to memory, save to CSV, return j
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

        jobs.add(job);
        persist();
        return job;
    }

   // update - find job by id. update only if if fields are not null or empty. save to csv then return true if job existed and is updated successfully. otherwise, return false
    public boolean update(int jobId, String jobName, String description, String hoursNeeded, Double payment, String status) {
        JobPosting j = findById(jobId);
        if (j == null) return false;
        
        if (jobName != null && !jobName.isEmpty()) j.setJobName(jobName);
        if (description != null && !description.isEmpty()) j.setDescription(description);
        if (hoursNeeded != null && !hoursNeeded.isEmpty()) j.setHoursNeeded(hoursNeeded);
        if (payment != null) j.setPayment(payment);
        if (status != null && !status.isEmpty()) j.setStatus(status);
        persist();
        return true;
    }

    // delete - removes job by specified Job ID from list, then return true. otherwise false
    public boolean delete(int jobId) {
        boolean removed = jobs.removeIf(j -> j.getJobId() == jobId);
        if (removed) persist();
        return removed;
    }

    // generate new id - finds highest application id, return next available id by +1. if list is empty, start at 101
    public int nextId() {
        return jobs.stream().mapToInt(JobPosting::getJobId).max().orElse(100) + 1;
    }

    // persist - saving to csv persistently
    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (JobPosting j : jobs) {
            out.add(j.toCSVLine());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }
}
