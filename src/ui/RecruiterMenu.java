package ui;

import java.util.List;
import java.util.Scanner;
import models.JobPosting;
import models.Application;
import models.Recruiter;
import managers.JobPostingManager;
import managers.ApplicationManager;

public class RecruiterMenu {
    private Recruiter recruiter;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private Scanner scanner = new Scanner(System.in);

    // Constructor
    public RecruiterMenu(Recruiter recruiter, JobPostingManager jpm, ApplicationManager am) {
        this.recruiter = recruiter;
        this.jpm = jpm;
        this.am = am;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== RECRUITER MENU ===");
            System.out.println("Welcome, " + recruiter.getFullName() + "!");
            System.out.println("[1] Create Job Posting");
            System.out.println("[2] View All Job Postings");
            System.out.println("[3] Update Job Posting");
            System.out.println("[4] Delete Job Posting");
            System.out.println("[0] Logout");
            System.out.print("Enter your choice: ");
            
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1" -> createJob();
                case "2" -> viewAllJobs();
                case "3" -> updateJob();
                case "4" -> deleteJob();
                case "0" -> {
                    System.out.println("Logging out..."); 
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void createJob() {
        System.out.print("Enter job name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter job description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter hours needed (e.g., 2 hours): ");
        String hours = scanner.nextLine().trim();
        System.out.print("Enter payment amount: ");
        double payment = readDouble();
        
        JobPosting j = jpm.create(name, desc, hours, payment);
        System.out.println("SUCCESS: Job post for [" + name + "] successfully created! (Job ID: " + j.getJobId() + ")");
    }

    private void viewAllJobs() {
        List<Object> list = jpm.findAll();
        if (list.isEmpty()) {
            System.out.println("No job postings found.");
            return;
        }
        
        System.out.println("\nAll Job Postings:");
        for (Object j : list) {
            System.out.println("---------------------------");
            System.out.println(((JobPosting) j).displayString());
        }
        System.out.println("---------------------------");
        System.out.println("[1] View Specific Job Posting");
        System.out.println("[0] Return to Menu");
        System.out.print("Choose an option: ");
        
        String opt = scanner.nextLine().trim();
        if ("1".equals(opt)) viewSpecificJob();
    }

    private void viewSpecificJob() {
        System.out.print("Enter Job Number: ");
        int jobNum = readInt();
        JobPosting job = jpm.findById(jobNum);
        
        if (job == null) {
            System.out.println("ERROR: Job does not exist.");
            return;
        }
        
        System.out.println("\n" + job.displayString());
        System.out.println("[1] View All Applications");
        System.out.println("[0] Return to Menu");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) viewApplications(jobNum);
    }

    private void viewApplications(int jobNum) {
        List<Application> apps = am.findByJobId(jobNum);
        if (apps.isEmpty()) {
            System.out.println("No applications for this job.");
            return;
        }
        
        System.out.println("\nApplications for Job " + jobNum + ":");
        for (Application a : apps) {
            System.out.println("---------------------------");
            System.out.println(a.displayString());
        }
        System.out.println("---------------------------");
        System.out.println("[1] Hire Applicant");
        System.out.println("[2] Decline Applicant");
        System.out.println("[0] Return to Menu");
        System.out.print("Enter your choice: ");
        
        String action = scanner.nextLine().trim();
        switch (action) {
            case "1" -> hireApplicant();
            case "2" -> declineApplicant();
            default -> {}
        }
    }

    private void hireApplicant() {
        System.out.print("Enter Application ID to hire: ");
        int appId = readInt();
        Application app = am.findByApplicationId(appId);
        
        if (app == null) {
            System.out.println("ERROR: Application not found.");
            return;
        }
        
        System.out.print("Are you sure you want to hire applicant " + app.getApplicantName() + "? (Y/N): ");
        String conf = scanner.nextLine().trim();
        
        if (conf.equalsIgnoreCase("Y")) {
            am.updateStatus(appId, "Hired");
            System.out.println("SUCCESS: Applicant successfully hired!");
        } else {
            System.out.println("Hiring cancelled.");
        }
    }

    private void declineApplicant() {
        System.out.print("Enter Application ID to decline: ");
        int appId = readInt();
        Application app = am.findByApplicationId(appId);
        
        if (app == null) {
            System.out.println("ERROR: Application not found.");
            return;
        }
        
        System.out.print("Are you sure you want to decline this application for " + app.getApplicantName() + "? (Y/N): ");
        String conf = scanner.nextLine().trim();
        
        if (conf.equalsIgnoreCase("Y")) {
            am.updateStatus(appId, "Declined");
            System.out.println("SUCCESS: Application successfully declined!");
        } else {
            System.out.println("Declining cancelled.");
        }
    }

    private void updateJob() {
        System.out.print("Enter Job Number to update: ");
        int jobNum = readInt();
        JobPosting job = jpm.findById(jobNum);
        
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        
        System.out.println("Current job info:");
        System.out.println(job.displayString());

        System.out.print("Enter new job name (leave blank to keep): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new description (leave blank to keep): ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter new hours needed (leave blank to keep): ");
        String hours = scanner.nextLine().trim();
        System.out.print("Enter new payment (leave blank to keep): ");
        String paymentStr = scanner.nextLine().trim();
        System.out.print("Enter new status (Available/Closed) (leave blank to keep): ");
        String status = scanner.nextLine().trim();

        Double payment = null;
        if (!paymentStr.isEmpty()) {
            try {
                payment = Double.valueOf(paymentStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid payment. Update cancelled.");
                return;
            }
        }

        boolean ok = jpm.update(jobNum,
                name.isEmpty() ? null : name,
                desc.isEmpty() ? null : desc,
                hours.isEmpty() ? null : hours,
                payment,
                status.isEmpty() ? null : status);

        if (ok) System.out.println("SUCCESS: Job successfully updated!");
        else System.out.println("ERROR: Could not update job.");
    }

    private void deleteJob() {
        System.out.print("Enter Job Number to delete: ");
        int jobNum = readInt();
        JobPosting job = jpm.findById(jobNum);
        
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        
        System.out.println("Job info:");
        System.out.println(job.displayString());
        System.out.print("Are you sure you want to delete this job? (Y/N): ");
        String conf = scanner.nextLine().trim();
        
        if (conf.equalsIgnoreCase("Y")) {
            boolean removed = jpm.delete(jobNum);
            if (removed) System.out.println("SUCCESS: Job successfully removed!");
            else System.out.println("ERROR: Could not remove job.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private int readInt() {
        while (true) {
            String s = scanner.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            String s = scanner.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}