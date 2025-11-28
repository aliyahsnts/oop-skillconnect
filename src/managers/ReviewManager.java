package managers;

import java.util.List;
import java.util.stream.Collectors;

import models.Review;

public class ReviewManager extends BaseManager<Review> {

    public ReviewManager(String csvFilePath) {
        super(csvFilePath, 
            "reviewId,recruiterId,recruiterName,jobseekerId,jobseekerName,jobId,rating,comment,timestamp,status");
    }

    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected Review parseEntity(String[] parts) {
        int reviewId = Integer.parseInt(parts[0].trim());
        int recruiterId = Integer.parseInt(parts[1].trim());
        String recruiterName = parts[2].trim();
        int jobseekerId = Integer.parseInt(parts[3].trim());
        String jobseekerName = parts[4].trim();
        int jobId = Integer.parseInt(parts[5].trim());
        int rating = Integer.parseInt(parts[6].trim());
        String comment = parts[7].trim();
        String timestamp = parts[8].trim();
        String status = parts[9].trim();

        return new Review(reviewId, recruiterId, recruiterName,
                          jobseekerId, jobseekerName, jobId,
                          rating, comment, timestamp, status);
    }

    @Override
    protected String toCSVLine(Review entity) {
        return entity.toCSVLine();
    }

    @Override
    protected int getId(Review entity) {
        return entity.getReviewId();
    }

    @Override
    protected int getMinimumColumns() {
        return 10;
    }

    @Override
    protected String getEntityName() {
        return "review";
    }

    @Override
    protected int getStartingId() {
        return 4001;
    }

    // =========================================
    //           CREATE & UPDATE
    // =========================================

    public Review create(int recruiterId, String recruiterName,
                         int jobseekerId, String jobseekerName,
                         int jobId, int rating, String comment) {

        int id = nextId();
        String timestamp = Review.getCurrentTimestamp();

        Review r = new Review(id, recruiterId, recruiterName,
                              jobseekerId, jobseekerName, jobId,
                              rating, comment, timestamp, "Published");

        entities.add(r);
        persist();
        return r;
    }

    public boolean updateStatus(int id, String newStatus) {
        Review r = findById(id);
        if (r == null) return false;
        r.setStatus(newStatus);
        persist();
        return true;
    }

    public boolean updateRating(int id, int newRating) {
        Review r = findById(id);
        if (r == null) return false;
        r.setRating(newRating);
        persist();
        return true;
    }

    // =========================================
    //           QUERY HELPERS
    // =========================================

    /** 
     * Find all reviews written *by a recruiter*
     */
    public List<Review> findByRecruiterId(int recruiterId) {
        return entities.stream()
                .filter(r -> r.getRecruiterId() == recruiterId)
                .collect(Collectors.toList());
    }

    /** 
     * Find all reviews *for a jobseeker*
     */
    public List<Review> findByJobseekerId(int jobseekerId) {
        return entities.stream()
                .filter(r -> r.getJobseekerId() == jobseekerId)
                .collect(Collectors.toList());
    }

    /**
     * Find all reviews for a job posting
     */
    public List<Review> findByJobId(int jobId) {
        return entities.stream()
                .filter(r -> r.getJobId() == jobId)
                .collect(Collectors.toList());
    }

    // =========================================
    //     ADD MATCHING findByUserId UTILITY
    // =========================================

    /**
     * Matches the pattern used in TransactionManager
     * and returns all reviews where the user participated.
     * 
     * In reviews:
     * - Recruiter → reviewer
     * - Jobseeker → receiver of the review
     */
    public List<Review> findByUserId(int userId) {
        return entities.stream()
                .filter(r -> r.getRecruiterId() == userId || r.getJobseekerId() == userId)
                .collect(Collectors.toList());
    }
}
