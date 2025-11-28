package models;

import java.util.List;
import utils.ApplicationFormGenerator; // Import for list/CSV helpers

public class JobPosting {
    private final int jobId;
    private String jobName;
    private String description;
    private String hoursNeeded;
    private double payment;
    private String status; // e.g., Available, Closed
    private String recruiterName;
    
    // new: Application Form Questions
    private List<String> applicationQuestions; 


    //constructor - UPDATED to include applicationQuestions
    public JobPosting(int jobId, String jobName, String description, String hoursNeeded, 
                      double payment, String status, String recruiterName, 
                      List<String> applicationQuestions) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.description = description;
        this.hoursNeeded = hoursNeeded;
        this.payment = payment;
        this.status = status;
        this.recruiterName = recruiterName;
        this.applicationQuestions = applicationQuestions; // New field
    }

    // Existing constructor (kept for parsing compatibility)
    public JobPosting(int jobId, String jobName, String description, String hoursNeeded, 
                      double payment, String status, String recruiterName) {
        // Calls the new constructor with an empty list of questions
        this(jobId, jobName, description, hoursNeeded, payment, status, recruiterName, List.of());
    }


    //getters
    public final int getJobId() { return jobId; }
    public String getJobName() { return jobName; }
    public String getName() { return jobName; }  // Alias for compatibility
    public String getDescription() { return description; }
    public String getHoursNeeded() { return hoursNeeded; }
    public double getPayment() { return payment; }
    public String getStatus() { return status; }
    public String getRecruiterName() { return recruiterName; }
    public List<String> getApplicationQuestions() { return applicationQuestions; } // NEW GETTER

    //setters
    public void setJobName(String jobName) { this.jobName = jobName; }
    public void setDescription(String description) { this.description = description; }
    public void setHoursNeeded(String hoursNeeded) { this.hoursNeeded = hoursNeeded; }
    public void setPayment(double payment) { this.payment = payment; }
    public void setStatus(String status) { this.status = status; }
    public void setApplicationQuestions(List<String> applicationQuestions) { 
        this.applicationQuestions = applicationQuestions; 
    } // NEW SETTER

    // CSV line representation (escape commas in text simply) - UPDATED
   public String toCSVLine() {
        // Use ApplicationFormGenerator.listToCSV to handle the questions
        String questionsCSV = ApplicationFormGenerator.listToCSV(applicationQuestions);
        
        return jobId + "," + escape(jobName) + "," + escape(description) + "," +
        escape(hoursNeeded) + "," + payment + "," + escape(status) + "," + escape(recruiterName) + 
        "," + escape(questionsCSV); // Appended the new questions field
    }

    //methods
    //display string - display jobs as string - UPDATED to show questions
    public String displayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------\n");
        sb.append("Job ID: ").append(jobId).append("\n");
        sb.append("Job Name: ").append(jobName).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Hours Needed: ").append(hoursNeeded).append("\n");
        sb.append("Payment: ").append(payment).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Recruiter: ").append(recruiterName).append("\n");
        
        // Display Application Questions
        if (applicationQuestions != null && !applicationQuestions.isEmpty()) {
            sb.append("\nApplication Questions (").append(applicationQuestions.size()).append("):\n");
            for (int i = 0; i < applicationQuestions.size(); i++) {
                sb.append("  Q").append(i + 1).append(": ").append(applicationQuestions.get(i)).append("\n");
            }
        } else {
            sb.append("\nApplication Questions: None required.\n");
        }
        
        sb.append("-----------------------------");
        return sb.toString();
    }

    //escape - replace "," with " " 
    private String escape(String s) {
        if (s == null) return "";
        // Replace commas and newlines with spaces for simple CSV storage
        return s.replace(",", " ").replace("\n", " "); 
    }
}