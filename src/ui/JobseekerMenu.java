package ui;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import models.*;
import managers.*;
import utils.ResumeGenerator;

public class JobseekerMenu {
    private Jobseeker jobseeker;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private Scanner scanner = new Scanner(System.in);
    private double walletBalance;
    private List<Product> products = new ArrayList<>();
    private List<Report> reports = new ArrayList<>();

    // Constructor
    public JobseekerMenu(Jobseeker jobseeker, JobPostingManager jpm, ApplicationManager am,
                         ProductManager pm, TransactionManager tm, ReportManager rm) {
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
            System.out.println("6. Edit My Résumé");   // <-- NEW
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> browseJobs();
                case 2 -> viewMyApplications();
                case 3 -> displayMarketMenu();
                case 4 -> displayReportMenu();
                case 5 -> doJobMenu();
                case 6 -> editResume();              // <-- NEW
                case 0 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("ERROR: Invalid option. Please try again.");
            }
        }
    }

    /* ====================  RESUME EDITOR  ==================== */
    private void editResume() {
        System.out.println("\n====== EDIT RÉSUMÉ ======");
        Jobseeker js = this.jobseeker;

        js.setPhone(ask("Phone number", js.getPhone()));
        js.setAddress(ask("Address", js.getAddress()));
        js.setSummary(ask("Professional summary", js.getSummary()));
        js.setEducation(ask("Education", js.getEducation()));

        System.out.print("Skills (comma-separated): ");
        String skillsLine = scanner.nextLine().trim();
        if (!skillsLine.isEmpty()) {
            js.getSkillList().clear();
            for (String s : skillsLine.split("\\s*,\\s*")) js.addSkill(s);
        }

        System.out.print("Experience (comma-separated jobs): ");
        String expLine = scanner.nextLine().trim();
        if (!expLine.isEmpty()) {
            js.getExperienceList().clear();
            for (String e : expLine.split("\\s*,\\s*")) js.addExperience(e);
        }

        try {
            ResumeGenerator.generateCSVForRegistration(js); // overwrite old file
            System.out.println("Résumé saved and CSV updated.");
        } catch (Exception ex) {
            System.out.println("ERROR: could not write CSV – " + ex.getMessage());
        }
    }

    private String ask(String field, String current) {
        System.out.printf("%s%s: ", field, (current == null ? "" : " (current: " + current + ")"));
        String in = scanner.nextLine().trim();
        return in.isEmpty() ? current : in;
    }
    /* ========================================================= */

    private void browseJobs() {
        List<JobPosting> list = jpm.findAll();
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
        Application app = am.create(job.getJobId(), jobseeker.getId(), jobseeker.getFullName());
        System.out.println("SUCCESS: Application submitted! (Application ID: " + app.getApplicationId() + ")");
    }

    private void viewMyApplications() {
        List<Application> myApps = am.findByApplicantId(jobseeker.getId());
        if (myApps.isEmpty()) {
            System.out.println("\nYou have no applications yet.");
            return;
        }
        System.out.println("\n===== MY APPLICATIONS =====");
        for (Application a : myApps) {
            System.out.println("-----------------------------");
            System.out.println(a.displayString());
            JobPosting jp = jpm.findById(a.getJobId());
            if (jp != null) System.out.println("Job Title: " + jp.getName());
        }
        System.out.println("-----------------------------");
        System.out.println("[1] Withdraw an Application");
        System.out.println("[0] Back");
        System.out.print("Enter choice: ");
        String choice = scanner.nextLine().trim();
        if ("1".equals(choice)) withdrawApplication();
    }

    private void withdrawApplication() {
        System.out.print("Enter Application ID to withdraw: ");
        int id = readInt();
        Application app = am.findByApplicationId(id);
        if (app == null) {
            System.out.println("ERROR: Application not found.");
            return;
        }
        if (app.getApplicantId() != jobseeker.getId()) {
            System.out.println("ERROR: You can only withdraw your own applications.");
            return;
        }
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

    /* -----------  MARKET / REPORT / DO JOB  ----------- */
    private void displayMarketMenu() {
        while (true) {
            System.out.println("\n===== MARKETPLACE =====");
            System.out.println("Wallet Balance: PHP " + walletBalance);
            System.out.println("1. View All Products");
            System.out.println("2. Purchase Product");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            switch (readInt()) {
                case 1 -> viewAllProducts();
                case 2 -> purchaseProduct();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    private void viewAllProducts() {
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        products.forEach(p -> System.out.println("ID: " + p.getProductId() + " | " + p.getProductName() + " | PHP " + p.getPrice() + " | Stock: " + p.getQuantity()));
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
        for (Product p : products) if (p.getProductId() == id) return p;
        return null;
    }

    private void displayReportMenu() {
        while (true) {
            System.out.println("\n===== REPORT MENU =====");
            System.out.println("1. Create Report");
            System.out.println("2. View All Reports");
            System.out.println("3. Update Report");
            System.out.println("4. Delete Report");
            System.out.println("0. Back");
            switch (readInt()) {
                case 1 -> createReport();
                case 2 -> viewReports();
                case 3 -> updateReport();
                case 4 -> deleteReport();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    private void createReport() {
        System.out.print("Enter title: ");
        scanner.nextLine();
        String title = scanner.nextLine().trim();
        System.out.print("Enter description: ");
        String desc = scanner.nextLine().trim();
        reports.add(new Report(reports.size() + 1, jobseeker.getId(), jobseeker.getFullName(),
                0, "", desc, Report.getCurrentTimestamp(), "Pending"));
        System.out.println("Report saved!");
    }

    private void viewReports() {
        if (reports.isEmpty()) {
            System.out.println("No reports available.");
            return;
        }
        reports.forEach(r -> System.out.println("ID: " + r.getReportId() + " | " + r.getReason() + "\n" + r.getTimestamp() + "\n"));
    }

    private void updateReport() {
        if (reports.isEmpty()) return;
        viewReports();
        System.out.print("Enter Report ID to update (0 to cancel): ");
        int id = readInt();
        if (id == 0) return;
        System.out.print("New description (or BACK to cancel): ");
        String d = scanner.nextLine();
        if (d.equalsIgnoreCase("BACK")) return;
        reports.get(id - 1).setReason(d);
        System.out.println("Report updated!");
    }

    private void deleteReport() {
        if (reports.isEmpty()) return;
        viewReports();
        System.out.print("Enter Report ID to delete (0 to cancel): ");
        int id = readInt();
        if (id == 0) return;
        reports.remove(id - 1);
        System.out.println("Report deleted!");
    }

    private void doJobMenu() {
        List<Application> hired = getHiredApplications();
        if (hired.isEmpty()) {
            System.out.println("You do not have any accepted applications yet.");
            return;
        }
        System.out.println("Select a job to work on:");
        for (int i = 0; i < hired.size(); i++) {
            JobPosting jp = jpm.findById(hired.get(i).getJobId());
            System.out.println((i + 1) + ". " + (jp == null ? "Unknown" : jp.getName()) + " (Job ID: " + hired.get(i).getJobId() + ")");
        }
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        int c = readInt();
        if (c == 0) return;
        if (c < 1 || c > hired.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        doJobTasks(jpm.findById(hired.get(c - 1).getJobId()));
    }

    private void doJobTasks(JobPosting job) {
        if (job == null) return;
        System.out.println("\n===== WORKING ON THE JOB =====");
        System.out.println("Job: " + job.getName());
        System.out.println("You perform tasks...");
        System.out.println("* Typing...\n* Reviewing...\n* Completing assignment...");
        System.out.println("\nJob Complete! You earned +₱150.");
        walletBalance += 150;
        System.out.println("New Wallet Balance: ₱" + walletBalance);

        // mark hired application completed
        am.findByApplicantId(jobseeker.getId())
          .stream()
          .filter(a -> a.getJobId() == job.getJobId() && a.getStatus().equalsIgnoreCase("Hired"))
          .findFirst()
          .ifPresent(a -> am.updateStatus(a.getApplicationId(), "Completed"));
    }

    private List<Application> getHiredApplications() {
        List<Application> hired = new ArrayList<>();
        for (Application a : am.findByApplicantId(jobseeker.getId()))
            if (a.getStatus().equalsIgnoreCase("Hired")) hired.add(a);
        return hired;
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