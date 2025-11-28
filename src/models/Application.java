package models;

import java.util.List;
import utils.ApplicationFormGenerator; // Import for list/CSV helpers

public class Application {
    private final int applicationId;
    private final int jobId;
    private final int applicantId;
    private final String applicantName;
    private String status; // Pending, Hired, Declined
    
    // NEW: Form Answers and Resume Path
    private List<String> applicationAnswers;
    private String resumeFilePath;

    //constructor 
    public Application(int applicationId, int jobId, int applicantId, String applicantName, 
                       String status, List<String> applicationAnswers, String resumeFilePath) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.status = status;
        this.applicationAnswers = applicationAnswers;
        this.resumeFilePath = resumeFilePath;
    }

    // Existing constructor (kept for compatibility with old CSV data)
    public Application(int applicationId, int jobId, int applicantId, String applicantName, String status) {
        this(applicationId, jobId, applicantId, applicantName, status, List.of(), "N/A");
    }

    //getters
    public int getApplicationId() { return applicationId; }
    public int getJobId() { return jobId; }
    public int getApplicantId() { return applicantId; }
    public String getApplicantName() { return applicantName; }
    public String getStatus() { return status; }
    public List<String> getApplicationAnswers() { return applicationAnswers; } // NEW GETTER
    public String getResumeFilePath() { return resumeFilePath; } // NEW GETTER

    //setters
    public void setStatus(String status) { this.status = status; }

    // toCSVLine - UPDATED
    public String toCSVLine() {
        String answersCSV = ApplicationFormGenerator.listToCSV(applicationAnswers);
        
        return applicationId + "," + jobId + "," + applicantId + "," + 
               escape(applicantName) + "," + escape(status) + "," + 
               escape(answersCSV) + "," + escape(resumeFilePath);
    }

    //methods
    //display string - display applications as string - UPDATED to show answers and resume
    public String displayString(List<String> questions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Application ID: ").append(applicationId).append("\n");
        sb.append("Job ID: ").append(jobId).append("\n");
        sb.append("Applicant ID: ").append(applicantId).append("\n");
        sb.append("Applicant Name: ").append(applicantName).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Resume File: ").append(resumeFilePath).append("\n");
        
        // Display Answers
        if (applicationAnswers != null && !applicationAnswers.isEmpty() && 
            questions != null && applicationAnswers.size() == questions.size()) {
            sb.append("\n--- Form Answers ---\n");
            for (int i = 0; i < applicationAnswers.size(); i++) {
                sb.append("Q").append(i + 1).append(": ").append(questions.get(i)).append("\n");
                sb.append("A").append(i + 1).append(": ").append(applicationAnswers.get(i)).append("\n");
                sb.append("--------------------\n");
            }
        } else if (applicationAnswers != null && !applicationAnswers.isEmpty()) {
             // Fallback if questions list is not available (e.g., from old data)
            sb.append("\n--- Form Answers (Questions Not Available) ---\n");
            for (int i = 0; i < applicationAnswers.size(); i++) {
                sb.append("A").append(i + 1).append(": ").append(applicationAnswers.get(i)).append("\n");
            }
        }
        
        return sb.toString();
    }

    // Overload for simpler display when questions aren't immediately available
    public String displayString() {
        return displayString(List.of()); 
    }

    //escape - replace "," with " " 
    private String escape(String s) {
        if (s == null) return "";
        // Replace commas and newlines with spaces for simple CSV storage
        return s.replace(",", " ").replace("\n", " "); 
    }
}