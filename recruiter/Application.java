public class Application {
    private final int applicationId;
    private final int jobId;
    private final int applicantId;
    private final String applicantName;
    private String status; // Pending, Hired, Declined

    public Application(int applicationId, int jobId, int applicantId, String applicantName, String status) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.status = status;
    }

    public int getApplicationId() { return applicationId; }
    public int getJobId() { return jobId; }
    public int getApplicantId() { return applicantId; }
    public String getApplicantName() { return applicantName; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String toCSVLine() {
        return applicationId + "," + jobId + "," + applicantId + "," + escape(applicantName) + "," + escape(status);
    }

    public String displayString() {
        return "Application ID: " + applicationId +
               "\nJob ID: " + jobId +
               "\nApplicant ID: " + applicantId +
               "\nApplicant Name: " + applicantName +
               "\nStatus: " + status;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ");
    }
}
