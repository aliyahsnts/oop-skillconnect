import java.nio.file.*;
import java.util.*;

public class JobPostingManager {
    private final Path csvPath;
    private final String HEADER = "jobId,jobName,description,hoursNeeded,payment,status";
    private final List<JobPosting> jobs = new ArrayList<>();

    public JobPostingManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    private void load() {
        jobs.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);
        if (lines.size() <= 1) return; // header or empty
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = CSVHelper.split(line);
            // Expecting 6 columns
            if (p.length < 6) continue;
            try {
                int jobId = Integer.parseInt(p[0].trim());
                String jobName = p[1].trim();
                String desc = p[2].trim();
                String hours = p[3].trim();
                double payment = Double.parseDouble(p[4].trim());
                String status = p[5].trim();
                jobs.add(new JobPosting(jobId, jobName, desc, hours, payment, status));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid job line: " + line);
            }
        }
    }

    public List<Object> findAll() {
        return new ArrayList<>(jobs);
    }

    public JobPosting findById(int id) {
        return jobs.stream().filter(j -> j.getJobId() == id).findFirst().orElse(null);
    }

    public JobPosting create(String jobName, String description, String hoursNeeded, double payment) {
        int id = nextId();
        JobPosting j = new JobPosting(id, jobName, description, hoursNeeded, payment, "Available");
        jobs.add(j);
        persist();
        return j;
    }

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

    public boolean delete(int jobId) {
        boolean removed = jobs.removeIf(j -> j.getJobId() == jobId);
        if (removed) persist();
        return removed;
    }

    public int nextId() {
        return jobs.stream().mapToInt(JobPosting::getJobId).max().orElse(100) + 1;
    }

    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (JobPosting j : jobs) {
            out.add(j.toCSVLine());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }
}
