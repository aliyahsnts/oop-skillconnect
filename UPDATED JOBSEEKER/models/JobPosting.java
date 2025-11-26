package models;

public class JobPosting {

    private int jobId;
    private String name;
    private String description;
    private String hoursNeeded;
    private double payment;
    private String status;
    private String recruiterName;

    // ✔ REQUIRED 7-PARAMETER CONSTRUCTOR
    public JobPosting(int jobId, String name, String description,
                      String hoursNeeded, double payment,
                      String status, String recruiterName) {

        this.jobId = jobId;
        this.name = name;
        this.description = description;
        this.hoursNeeded = hoursNeeded;
        this.payment = payment;
        this.status = status;
        this.recruiterName = recruiterName;
    }

    // GETTERS
    public int getJobId() { return jobId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getHoursNeeded() { return hoursNeeded; }
    public double getPayment() { return payment; }
    public String getStatus() { return status; }
    public String getRecruiterName() { return recruiterName; }

    // SETTERS
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setHoursNeeded(String hoursNeeded) { this.hoursNeeded = hoursNeeded; }
    public void setPayment(double payment) { this.payment = payment; }
    public void setStatus(String status) { this.status = status; }

    // CSV OUTPUT
    public String csvFormat() {
        return jobId + "," +
               name + "," +
               description + "," +
               hoursNeeded + "," +
               payment + "," +
               status + "," +
               recruiterName;
    }

    // DISPLAY TEXT
    public String displayString() {
        return "-----------------------------" +
               "\nJob ID: " + jobId +
               "\nTitle: " + name +
               "\nDescription: " + description +
               "\nHours Needed: " + hoursNeeded +
               "\nPayment: " + payment +
               "\nStatus: " + status +
               "\nRecruiter: " + recruiterName +
               "\n-----------------------------";
    }
}
