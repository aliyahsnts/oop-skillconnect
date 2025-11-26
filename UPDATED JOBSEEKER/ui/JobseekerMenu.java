package ui;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import models.Jobseeker;
import models.JobPosting;
import models.Application;
import managers.JobPostingManager;
import managers.ApplicationManager;

public class JobseekerMenu {

    private Jobseeker user;
    private Jobseeker jobseeker;
    private JobPostingManager jpm;    // Handles job postings (OOP connection)
    private ApplicationManager am;    // Handles applications (OOP connection)
    private Scanner scanner = new Scanner(System.in);

    // Product class (Market menu)
    class Product {
        int id;
        String name;
        double price;
        int stock;

        Product(int id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }

    // Report class (Reports menu)
    class Report {
        int id;
        String title;
        String description;

        Report(int id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }
    }

    private List<Product> products = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();
    private double walletBalance = 500;

    // Constructor now expects OOP managers
    public JobseekerMenu(Jobseeker jobseeker, JobPostingManager jpm, ApplicationManager am) {
        this.jobseeker = jobseeker;
        this.jpm = jpm;
        this.am = am;

        // Preloaded sample products
        products.add(new Product(1, "Keyboard", 300, 10));
        products.add(new Product(2, "Mouse", 150, 15));
        products.add(new Product(3, "USB Drive", 200, 20));
    }

    // JOBSEEKER MAIN MENU LOOP
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

    // BROWSE JOBS — now fully connected to Recruiter postings through OOP
    private void browseJobs() {
        // Load all jobs using OOP manager
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
            System.out.println(job.displayString()); // Uses formatted display from JobPosting
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
            System.out.println("ID: " + p.id + " | " + p.name + " | PHP " + p.price + " | Stock: " + p.stock);
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

        if (qty > product.stock) {
            System.out.println("ERROR: Not enough stock.");
            return;
        }

        double total = product.price * qty;

        if (walletBalance < total) {
            System.out.println("ERROR: Insufficient funds.");
            return;
        }

        walletBalance -= total;
        product.stock -= qty;

        System.out.println("Purchase Successful!");
    }

    private Product findProduct(int id) {
        for (Product p : products) {
            if (p.id == id) return p;
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
        String title = scanner.nextLine();

        System.out.print("Enter description: ");
        String desc = scanner.nextLine();

        System.out.print("Is this correct? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            reports.add(new Report(reports.size() + 1, title, desc));
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

        for (Report r : reports) {
            System.out.println("ID: " + r.id + " | " + r.title + "\n" + r.description + "\n");
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

        Report r = findReport(id);
        if (r == null) {
            System.out.println("ERROR: Report not found.");
            return;
        }

        System.out.println("\nCurrent Description:");
        System.out.println(r.description);

        System.out.print("\nEnter new description (or type 'BACK' to cancel): ");
        String newDesc = scanner.nextLine();

        if (newDesc.equalsIgnoreCase("BACK")) {
            System.out.println("Update cancelled.");
            return;
        }

        System.out.print("Are you sure you want to update this report? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            r.description = newDesc;
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

        Report r = findReport(id);
        if (r == null) {
            System.out.println("ERROR: Report not found.");
            return;
        }

        System.out.println("\nYou are about to delete:");
        System.out.println("Title: " + r.title);
        System.out.println("Description: " + r.description);

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

        reports.remove(r);
        System.out.println("SUCCESS: Report deleted.");
    }

    private Report findReport(int id) {
        for (Report r : reports) {
            if (r.id == id) return r;
        }
        return null;
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
