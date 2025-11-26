package ui;

import java.util.Scanner;
import java.util.List;
import models.Jobseeker;
import models.JobPosting;
import models.Application;
import managers.JobPostingManager;
import managers.ApplicationManager;

public class JobseekerMenu {
    private Jobseeker jobseeker;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private Scanner scanner = new Scanner(System.in);

    // Constructor
    public JobseekerMenu(Jobseeker jobseeker, JobPostingManager jpm, ApplicationManager am) {
        this.jobseeker = jobseeker;
        this.jpm = jpm;
        this.am = am;
    }

    public void show() {
        while (true) {
            System.out.println("\n===== JOBSEEKER INTERFACE =====");
            System.out.println("Welcome, " + jobseeker.getFullName() + "!");
            System.out.println("1. Browse Jobs");
            System.out.println("2. My Applications");
            System.out.println("3. Market");
            System.out.println("4. Reports");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> browseJobs();
                case 2 -> viewMyApplications();
                case 3 -> displayMarketMenu();
                case 4 -> displayReportMenu();
                case 0 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("ERROR: Invalid option. Please try again.");
            }
        }
    }

    private void browseJobs() {
        List<Object> list = jpm.findAll();
        if (list.isEmpty()) {
            System.out.println("No job postings available.");
            return;
        }
        
        System.out.println("\n=== Available Jobs ===");
        for (Object obj : list) {
            JobPosting job = (JobPosting) obj;
            System.out.println("---------------------------");
            System.out.println(job.displayString());
        }
        System.out.println("---------------------------");
        System.out.println("[1] Apply to a Job");
        System.out.println("[0] Return to Menu");
        System.out.print("Choose an option: ");
        
        String opt = scanner.nextLine().trim();
        if ("1".equals(opt)) applyToJob();
    }

    private void applyToJob() {
        System.out.print("Enter Job ID to apply: ");
        int jobId = readInt();
        
        JobPosting job = jpm.findById(jobId);
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        
        // Create application (assuming jobseeker has an ID - you may need to add this)
        Application app = am.create(jobId, 0, jobseeker.getFullName()); // Use 0 as placeholder ID
        System.out.println("SUCCESS: Application submitted! (Application ID: " + app.getApplicationId() + ")");
    }

    private void viewMyApplications() {
        System.out.println(">>> My Applications (Work in Progress)");
        // TODO: Implement view applications for this jobseeker
    }

    private void displayMarketMenu() {
        System.out.println(">>> Market Menu (Work in Progress)");
    }

    private void displayReportMenu() {
        System.out.println(">>> Report Menu (Work in Progress)");
    }

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