package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Review {
    private int reviewId;
    private int recruiterId;
    private String recruiterName;
    private int jobseekerId;
    private String jobseekerName;
    private int jobId;
    private int rating;
    private String comment;
    private String timestamp;
    private String status;

    // Full constructor
    public Review(int reviewId, int recruiterId, String recruiterName,
                  int jobseekerId, String jobseekerName, int jobId,
                  int rating, String comment, String timestamp, String status) {
        this.reviewId = reviewId;
        this.recruiterId = recruiterId;
        this.recruiterName = recruiterName;
        this.jobseekerId = jobseekerId;
        this.jobseekerName = jobseekerName;
        this.jobId = jobId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.status = status;
    }

    // =======================
    //        GETTERS
    // =======================
    public int getReviewId() { return reviewId; }
    public int getRecruiterId() { return recruiterId; }
    public String getRecruiterName() { return recruiterName; }
    public int getJobseekerId() { return jobseekerId; }
    public String getJobseekerName() { return jobseekerName; }
    public int getJobId() { return jobId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getTimestamp() { return timestamp; }
    public String getStatus() { return status; }

    // =======================
    //        SETTERS
    // =======================
    public void setStatus(String status) { this.status = status; }
    public void setRating(int rating) { this.rating = rating; }

    // =======================
    //   HELPER METHODS
    // =======================
    public static String getCurrentTimestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String toCSVLine() {
        return reviewId + "," + recruiterId + "," + recruiterName + "," + jobseekerId + "," +
               jobseekerName + "," + jobId + "," + rating + "," + comment + "," + timestamp + "," + status;
    }
}
