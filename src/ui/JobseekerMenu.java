package ui;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import models.*;
import managers.*;

public class JobseekerMenu {
    private Jobseeker jobseeker;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private Scanner scanner = new Scanner(System.in);
    private double walletBalance;
    private List<Product> products = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();

    // Constructor
    public JobseekerMenu(Jobseeker jobseeker, JobPostingManager jpm, ApplicationManager am, ProductManager pm, TransactionManager tm, ReportManager rm) {
        this.jobseeker = jobseeker;
        this.jpm = jpm;
        this.am = am;
        this.walletBalance = 0; // Initialize to 0, can be updated later
        // Initialize products and reports lists if needed
    }

    public void show() {
        while (true) {
            System.out.println("\n===== JOBSEEKER INTERFACE =====");
            System.out.println("Welcome, " + jobseeker.getFullName() + "!");
            System.out.println("1. Browse Jobs");
            System.out.println("2. My Applications");
            System.out.println("3. Market");
            System.out.println("4. Reports");
            System.out.println("5. Do Job"); 
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> browseJobs();
                case 2 -> viewMyApplications();
                case 3 -> displayMarketMenu();
                case 4 -> displayReportMenu();
                case 5 -> doJobMenu(); 
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

        // Each object is a JobPosting instance
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

    // APPLY TO JOB — creates Application using OOP ApplicationManager
    private void applyToJob() {
        System.out.print("Enter Job ID to apply: ");
        int jobId = readInt();
        
        // Find job using OOP manager
        JobPosting job = jpm.findById(jobId);

        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        
        // Create new application (OOP)
        Application app = am.create(
            job.getJobId(),             // job linked
            jobseeker.getId(),          // jobseeker ID
            jobseeker.getFullName()     // jobseeker name
        );
        System.out.println("SUCCESS: Application submitted! (Application ID: " + app.getApplicationId() + ")");
    }

    // VIEW ALL APPLICATIONS FOR THE CURRENT JOBSEEKER
    private void viewMyApplications() {

    // Get all applications that belong to this jobseeker
    List<Application> myApps = am.findByApplicantId(jobseeker.getId());

    if (myApps.isEmpty()) {
        System.out.println("\nYou have no applications yet.");
        return;
    }

    System.out.println("\n===== MY APPLICATIONS =====");

    for (Application a : myApps) {
        System.out.println("-----------------------------");
        System.out.println(a.displayString());

        // Also show linked job title
        JobPosting jp = jpm.findById(a.getJobId());
        if (jp != null) {
            System.out.println("Job Title: " + jp.getName());
        }
    }

    System.out.println("-----------------------------");
    System.out.println("[1] Withdraw an Application");
    System.out.println("[0] Back");
    System.out.print("Enter choice: ");

    String choice = scanner.nextLine().trim();

    if ("1".equals(choice)) {
        withdrawApplication();
    }
}


// WITHDRAW APPLICATION
private void withdrawApplication() {

    System.out.print("Enter Application ID to withdraw: ");
    int id = readInt();

    Application app = am.findByApplicationId(id);

    if (app == null) {
        System.out.println("ERROR: Application not found.");
        return;
    }

    // Check if the application belongs to current user
    if (app.getApplicantId()!= jobseeker.getId()) {
        System.out.println("ERROR: You can only withdraw your own applications.");
        return;
    }

    // Prevent withdrawing hired/declined apps
    if (!app.getStatus().equalsIgnoreCase("Pending")) {
        System.out.println("ERROR: Only pending applications can be withdrawn.");
        return;
    }

    System.out.print("Are you sure you want to withdraw this application? (Y/N): ");
    String confirm = scanner.nextLine().trim();

    if (confirm.equalsIgnoreCase("Y")) {
        am.updateStatus(id, "Withdrawn");
        System.out.println("SUCCESS: Application withdrawn.");
    } else {
        System.out.println("Cancelled.");
    }
}

    // MARKETPLACE
    private void displayMarketMenu() {
        while (true) {
            System.out.println("\n===== MARKETPLACE =====");
            System.out.println("Wallet Balance: PHP " + walletBalance);
            System.out.println("1. View All Products");
            System.out.println("2. Purchase Product");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> viewAllProducts();
                case 2 -> purchaseProduct();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    private void viewAllProducts() {
        System.out.println("\n===== PRODUCT LIST =====");

        for (Product p : products) {
            System.out.println("ID: " + p.getProductId() + " | " + p.getProductName() + " | PHP " + p.getPrice() + " | Stock: " + p.getQuantity());
        }
    }

    private void purchaseProduct() {
        viewAllProducts();

        System.out.print("Enter Product ID: ");
        int id = readInt();

        Product product = findProduct(id);
        if (product == null) {
            System.out.println("ERROR: Product not found.");
            return;
        }

        System.out.print("Enter quantity: ");
        int qty = readInt();

        if (qty > product.getQuantity()) {
            System.out.println("ERROR: Not enough stock.");
            return;
        }

        double total = product.getPrice() * qty;

        if (walletBalance < total) {
            System.out.println("ERROR: Insufficient funds.");
            return;
        }

        walletBalance -= total;
        product.setQuantity(product.getQuantity() - qty);

        System.out.println("Purchase Successful!");
    }

    private Product findProduct(int id) {
        for (Product p : products) {
            if (p.getProductId() == id) return p;
        }
        return null;
    }

    // REPORTS MENU
    private void displayReportMenu() {
        while (true) {
            System.out.println("\n===== REPORT MENU =====");
            System.out.println("1. Create Report");
            System.out.println("2. View All Reports");
            System.out.println("3. Update Report");
            System.out.println("4. Delete Report");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> createReport();
                case 2 -> viewReports();
                case 3 -> updateReport();
                case 4 -> deleteReport();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    // CREATE REPORT
    private void createReport() {
        System.out.print("Enter title: ");
        scanner.nextLine();

        System.out.print("Enter description: ");
        scanner.nextLine();

        System.out.print("Is this correct? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            System.out.println("Report saved!");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private void viewReports() {
        if (reports.isEmpty()) {
            System.out.println("No reports available.");
            return;
        }

        System.out.println("\n===== REPORT LIST =====");

        for (int i = 0; i < reports.size(); i++) {
            Report r = reports.get(i);
            System.out.println("ID: " + (i + 1) + " | Reason: " + r.getReason() + "\n" + r.getTimestamp() + "\n");
        }
    }

    private void updateReport() {
        if (reports.isEmpty()) {
            System.out.println("No reports available.");
            return;
        }

        viewReports();

        System.out.print("Enter Report ID to update (0 to cancel): ");
        int id = readInt();

        if (id == 0) {
            System.out.println("Returning to Report Menu...");
            return;
        }

        System.out.print("\nEnter new description (or type 'BACK' to cancel): ");
        String newDesc = scanner.nextLine();

        if (newDesc.equalsIgnoreCase("BACK")) {
            System.out.println("Update cancelled.");
            return;
        }

        System.out.print("Are you sure you want to update this report? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            System.out.println("SUCCESS: Report updated!");
        } else {
            System.out.println("Update cancelled.");
        }
    }

    private void deleteReport() {
        if (reports.isEmpty()) {
            System.out.println("No reports available.");
            return;
        }

        viewReports();

        System.out.print("Enter Report ID to delete (0 to cancel): ");
        int id = readInt();

        if (id == 0) {
            System.out.println("Returning to Report Menu...");
            return;
        }

        System.out.println("\nYou are about to delete a report.");

        System.out.print("Are you sure? (Y/N): ");
        String confirm1 = scanner.nextLine().trim().toUpperCase();

        if (!confirm1.equals("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        System.out.print("Please confirm again to DELETE this report (Type DELETE to proceed): ");
        String confirm2 = scanner.nextLine().trim().toUpperCase();

        if (!confirm2.equals("DELETE")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        System.out.println("SUCCESS: Report deleted.");
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

    private List<Application> getHiredApplications() {
        List<Application> hired = new ArrayList<>();

        List<Application> myApps = am.findByApplicantId(jobseeker.getId());
        for (Application a : myApps) {
            if (a.getStatus().equalsIgnoreCase("Hired")) {
                hired.add(a);
            }
        }
        return hired;
    }

    private void doJobMenu() {
        System.out.println("\n===== DO JOB =====");

        List<Application> hired = getHiredApplications();

        if (hired.isEmpty()) {
            System.out.println("You do not have any accepted applications yet.");
            System.out.println("Apply for jobs first.");
            return;
        }

        System.out.println("Select a job to work on:");
        int index = 1;

        for (Application a : hired) {
            JobPosting jp = jpm.findById(a.getJobId());
            if (jp != null) {
                System.out.println(index + ". " + jp.getName() + " (Job ID: " + jp.getJobId() + ")");
            }
            index++;
        }

        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 0) return;
        if (choice < 1 || choice > hired.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Application selectedApp = hired.get(choice - 1);
        JobPosting job = jpm.findById(selectedApp.getJobId());

        if (job == null) {
            System.out.println("Job data not found.");
            return;
        }

        doJobTasks(job);
    }

    private void doJobTasks(JobPosting job) {
    System.out.println("\n===== WORKING ON THE JOB =====");
    System.out.println("Job: " + job.getName());

    System.out.println("You perform tasks...");
    System.out.println("* Typing...");
    System.out.println("* Reviewing...");
    System.out.println("* Completing assignment...");

    System.out.println("\nJob Complete! You earned +₱150.");
    walletBalance += 150;

    System.out.println("New Wallet Balance: ₱" + walletBalance);

    // Find and update the application status to "Completed"
    List<Application> myApps = am.findByApplicantId(jobseeker.getId());
    for (Application a : myApps) {
        if (a.getJobId() == job.getJobId() && a.getStatus().equalsIgnoreCase("Hired")) {
            am.updateStatus(a.getApplicationId(), "Completed");
            System.out.println("Application status updated to: Completed");
            break;
        }
    }
}
}

//MOVE MARKETPLACE OUT
//MOVE REPORTS OUT
//FIX INDENTATION
//ADD COMMENTS