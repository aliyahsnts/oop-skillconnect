package ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import models.*;
import managers.*;
import utils.ResumeGenerator;
import ui.handlers.*;

public class JobseekerMenu {
    private Jobseeker jobseeker;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private Scanner scanner = new Scanner(System.in);
    
    // Handlers
    private ReportHandler reportHandler;
    private MarketplaceHandler marketplaceHandler;
    private ResumeHandler resumeHandler;

    public JobseekerMenu(Jobseeker jobseeker, JobPostingManager jpm, ApplicationManager am,
                         ProductManager pm, TransactionManager tm, ReportManager rm) {
        this.jobseeker = jobseeker;
        this.jpm = jpm;
        this.am = am;
        
        // Initialize handlers
        this.reportHandler = new ReportHandler(jobseeker, rm, scanner);
        this.marketplaceHandler = new MarketplaceHandler(jobseeker, pm, scanner);
        this.resumeHandler = new ResumeHandler(jobseeker, scanner);
    }

    public void show() {
        while (true) {
            System.out.println("\n===== JOBSEEKER INTERFACE =====");
            System.out.println("Welcome, " + jobseeker.getFullName() + "!");
            System.out.println("1. Browse Jobs");
            System.out.println("2. My Applications");
            System.out.println("3. Marketplace");
            System.out.println("4. My Reports");
            System.out.println("5. Do Job");
            System.out.println("6. Update Resume"); 
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> browseJobs();
                case "2" -> viewApplications();
                case "3" -> marketplaceHandler.showMenu();
                case "4" -> reportHandler.showMenu();
                case "5" -> doJob();
                case "6" -> resumeHandler.showMenu();
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /* ====================  JOBS  ==================== */

    private void browseJobs() {
        System.out.println("\n--- Available Job Postings ---");
        List<JobPosting> availableJobs = jpm.findAll().stream()
            .filter(j -> j.getStatus().equalsIgnoreCase("Available"))
            .toList();

        if (availableJobs.isEmpty()) {
            System.out.println("No available jobs found.");
            return;
        }

        for (JobPosting job : availableJobs) {
            System.out.println(job.displayString());
            System.out.println("-------------------------------------");
        }

        System.out.print("\nEnter Job ID to apply for (or 0 to cancel): ");
        int jobId = readInt();

        if (jobId != 0) {
            JobPosting job = jpm.findById(jobId);
            if (job != null && job.getStatus().equalsIgnoreCase("Available")) {
                applyForJob(job);
            } else {
                System.out.println("ERROR: Invalid Job ID or job is not available.");
            }
        }
    }

    private void applyForJob(JobPosting job) {
        // Check if already applied
        boolean alreadyApplied = am.findByApplicantId(jobseeker.getId()).stream()
            .anyMatch(a -> a.getJobId() == job.getJobId());
        
        if (alreadyApplied) {
            System.out.println("ERROR: You have already applied for this job.");
            return;
        }
        
        System.out.println("\n--- Applying for: " + job.getJobName() + " ---");

        List<String> questions = job.getApplicationQuestions();
        List<String> answers = new ArrayList<>();
        
        // Fill out Application Form (if questions exist)
        if (questions != null && !questions.isEmpty()) {
            System.out.println("\n--- Application Form Required ---");
            for (int i = 0; i < questions.size(); i++) {
                System.out.println("Q" + (i + 1) + ": " + questions.get(i));
                System.out.print("Your Answer: ");
                String answer = scanner.nextLine().trim().replace(",", " ").replace(";", " ");
                answers.add(answer);
            }
        }
        
        // Attach Resume
        String resumePath;
        try {
            Path path = ResumeGenerator.generateCSV(jobseeker);
            resumePath = path.toString();
            System.out.println("\nSUCCESS: Resume attached from: " + resumePath);
        } catch (IOException e) {
            System.out.println("ERROR: Could not generate/attach resume. Applying without a resume path.");
            e.printStackTrace();
            resumePath = "ERROR_FILE_GENERATION";
        }

        // Create Application
        am.create(job.getJobId(), jobseeker.getId(), jobseeker.getFullName(), answers, resumePath);
        System.out.println("\nSUCCESS: Application submitted for Job ID " + job.getJobId() + "!");
    }

    private void viewApplications() {
        System.out.println("\n--- My Applications ---");
        List<Application> myApps = am.findByApplicantId(jobseeker.getId());

        if (myApps.isEmpty()) {
            System.out.println("You have no applications yet.");
            return;
        }

        for (Application app : myApps) {
            JobPosting job = jpm.findById(app.getJobId());
            List<String> questions = (job != null) ? job.getApplicationQuestions() : List.of();
            
            System.out.println(app.displayString(questions));
            System.out.println("-------------------------------------");
        }
    }

    private void doJob() {
        List<Application> hiredApps = am.findByApplicantId(jobseeker.getId()).stream()
            .filter(a -> a.getStatus().equalsIgnoreCase("Hired"))
            .toList();

        if (hiredApps.isEmpty()) {
            System.out.println("You have no jobs currently in 'Hired' status.");
            return;
        }

        System.out.println("\n--- Hired Jobs ---");
        for (int i = 0; i < hiredApps.size(); i++) {
            JobPosting job = jpm.findById(hiredApps.get(i).getJobId());
            if (job != null) {
                System.out.printf("[%d] Job ID: %d, Name: %s\n", i + 1, job.getJobId(), job.getJobName());
            }
        }

        System.out.print("Select job number to complete (or 0 to cancel): ");
        int choice = readInt();

        if (choice > 0 && choice <= hiredApps.size()) {
            Application app = hiredApps.get(choice - 1);
            JobPosting job = jpm.findById(app.getJobId());
            if (job != null) {
                simulateJobCompletion(job);
            } else {
                System.out.println("Error: Job not found.");
            }
        }
    }

    private void simulateJobCompletion(JobPosting job) {
        System.out.println("\n===== WORKING ON THE JOB =====");
        System.out.println("Job: " + job.getName());
        System.out.println("You perform tasks...");
        System.out.println("* Typing...\n* Reviewing...\n* Completing assignment...");
        System.out.println("\nJob Complete! You earned +₱" + job.getPayment() + ".");
        
        double newBalance = jobseeker.getMoney() + job.getPayment();
        jobseeker.setMoney(newBalance);
        
        System.out.println("New Wallet Balance: ₱" + newBalance);

        // Mark hired application as completed
        am.findByApplicantId(jobseeker.getId())
          .stream()
          .filter(a -> a.getJobId() == job.getJobId() && a.getStatus().equalsIgnoreCase("Hired"))
          .findFirst()
          .ifPresent(a -> am.updateStatus(a.getApplicationId(), "Completed"));
    }

    /* ====================  HELPERS  ==================== */

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}

