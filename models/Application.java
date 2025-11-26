package models;
public class Application {
    private final int applicationId;
    private final int jobId;
    private final int applicantId;
    private final String applicantName;
    private String status; // Pending, Hired, Declined

    //constructor
    public Application(int applicationId, int jobId, int applicantId, String applicantName, String status) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.status = status;
    }

    //getters
    public int getApplicationId() { return applicationId; }
    public int getJobId() { return jobId; }
    public int getApplicantId() { return applicantId; }
    public String getApplicantName() { return applicantName; }
    public String getStatus() { return status; }

    //setters
    public void setStatus(String status) { this.status = status; }

    public String toCSVLine() {
        return applicationId + "," + jobId + "," + applicantId + "," + escape(applicantName) + "," + escape(status);
    }

    //methods
    //display string - display applications as string
    public String displayString() {
        return "Application ID: " + applicationId +
               "\nJob ID: " + jobId +
               "\nApplicant ID: " + applicantId +
               "\nApplicant Name: " + applicantName +
               "\nStatus: " + status;
    }

    //escape - replace "," with " " 
    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}
