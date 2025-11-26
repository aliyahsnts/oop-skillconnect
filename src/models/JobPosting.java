package models;
public class JobPosting {
    private final int jobId;
    private String jobName;
    private String description;
    private String hoursNeeded;
    private double payment;
    private String status; // e.g., Available, Closed

    //constructor
    public JobPosting(int jobId, String jobName, String description, String hoursNeeded, double payment, String status) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.description = description;
        this.hoursNeeded = hoursNeeded;
        this.payment = payment;
        this.status = status;
    }

    //getters
    public final int getJobId() { return jobId; }
    public String getJobName() { return jobName; }
    public String getDescription() { return description; }
    public String getHoursNeeded() { return hoursNeeded; }
    public double getPayment() { return payment; }
    public String getStatus() { return status; }

    //setters
    public void setJobName(String jobName) { this.jobName = jobName; }
    public void setDescription(String description) { this.description = description; }
    public void setHoursNeeded(String hoursNeeded) { this.hoursNeeded = hoursNeeded; }
    public void setPayment(double payment) { this.payment = payment; }
    public void setStatus(String status) { this.status = status; }

    // CSV line representation (escape commas in text simply)
    public String toCSVLine() {
        return jobId + "," + escape(jobName) + "," + escape(description) + "," + escape(hoursNeeded) + "," + payment + "," + escape(status);
    }

    //methods
    //display string - display jobs as string
    public String displayString() {
        return "Job ID: " + jobId +
               "\nJob Name: " + jobName +
               "\nDescription: " + description +
               "\nHours Needed: " + hoursNeeded +
               "\nPayment: " + payment +
               "\nStatus: " + status;
    }

    //escape - replace "," with " " 
    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " "); // simple: remove commas to avoid CSV split issues
    }
}
