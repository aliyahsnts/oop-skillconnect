package managers;

import java.util.*;
import java.nio.file.*;

import models.Application;
import models.JobPosting;
import utils.CSVHelper;

public class JobPostingManager {

    private final Path csvPath;
    private final String HEADER = "jobId,name,description,hours,payment,status,recruiterName";

    private List<JobPosting> jobs = new ArrayList<>();

    // NEW: applications list
    private List<Application> applications = new ArrayList<>();

    public JobPostingManager(String csvPathStr) {
        this.csvPath = Paths.get(csvPathStr);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();               // load jobs
        loadApplications();   // load applications
    }

    // LOAD job postings from CSV
    private void load() {
        jobs.clear();

        List<String> lines = CSVHelper.readAllLines(csvPath);
        for (int i = 1; i < lines.size(); i++) {
            String[] p = CSVHelper.split(lines.get(i));
            if (p.length < 7) continue;

            jobs.add(new JobPosting(
                Integer.parseInt(p[0].trim()),
                p[1].trim(),
                p[2].trim(),
                p[3].trim(),
                Double.parseDouble(p[4].trim()),
                p[5].trim(),
                p[6].trim()
            ));
        }
    }

    // NEW: LOAD applications from CSV
    private void loadApplications() {
        applications.clear();

        Path appPath = Paths.get("data/applications.csv");

        CSVHelper.ensureFileWithHeader(appPath,
            "applicationId,jobId,applicantId,applicantName,status");

        List<String> lines = CSVHelper.readAllLines(appPath);

        for (int i = 1; i < lines.size(); i++) {
            String[] p = CSVHelper.split(lines.get(i));
            if (p.length < 5) continue;

            applications.add(new Application(
                Integer.parseInt(p[0].trim()),  // applicationId
                Integer.parseInt(p[1].trim()),  // jobId
                Integer.parseInt(p[2].trim()),  // applicantId
                p[3].trim(),                     // applicantName
                p[4].trim()                      // status
            ));
        }
    }

    // SAVE job postings CSV
    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);

        for (JobPosting j : jobs) {
            out.add(j.csvFormat());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }

    // AUTO job ID
    public int nextId() {
        return jobs.stream()
                .mapToInt(JobPosting::getJobId)
                .max()
                .orElse(0) + 1;
    }

    // CREATE JOB
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

    public List<Object> findAll() {
        return new ArrayList<>(jobs);
    }

    public JobPosting findById(int id) {
        return jobs.stream()
                .filter(j -> j.getJobId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<JobPosting> findByRecruiter(String name) {
        List<JobPosting> result = new ArrayList<>();
        for (JobPosting j : jobs) {
            if (j.getRecruiterName().equalsIgnoreCase(name)) {
                result.add(j);
            }
        }
        return result;
    }

    // ✅ FIXED: Now works because applications list exists
    public List<Application> findByApplicantId(int applicantId) {
        List<Application> result = new ArrayList<>();
        for (Application app : applications) {
            if (app.getApplicantId() == applicantId) {
                result.add(app);
            }
        }
        return result;
    }

    public boolean delete(int id) {
        boolean removed = jobs.removeIf(j -> j.getJobId() == id);
        if (removed) persist();
        return removed;
    }

    public boolean update(int jobId, String name, String desc, String hours,
                          Double payment, String status) {

        JobPosting job = findById(jobId);
        if (job == null) return false;

        if (name != null) job.setName(name);
        if (desc != null) job.setDescription(desc);
        if (hours != null) job.setHoursNeeded(hours);
        if (payment != null) job.setPayment(payment);
        if (status != null) job.setStatus(status);

        persist();
        return true;
    }
}
