package ui;

import java.io.IOException;
import java.nio.file.Path;
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
        this.walletBalance = jobseeker.getMoney(); 
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
                case "3" -> displayMarketMenu();
                case "4" -> displayReportMenu();
                case "5" -> doJob();
                case "6" -> displayResumeMenu();
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
            System.out.println(job.displayString()); // Displays questions now
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

    // UPDATED: Apply for Job logic
    private void applyForJob(JobPosting job) {
        // 1. Check if already applied
        boolean alreadyApplied = am.findByApplicantId(jobseeker.getId()).stream()
            .anyMatch(a -> a.getJobId() == job.getJobId());
        
        if (alreadyApplied) {
            System.out.println("ERROR: You have already applied for this job.");
            return;
        }
        
        System.out.println("\n--- Applying for: " + job.getJobName() + " ---");

        List<String> questions = job.getApplicationQuestions();
        List<String> answers = new ArrayList<>();
        
        // 2. Fill out Application Form (if questions exist)
        if (questions != null && !questions.isEmpty()) {
            System.out.println("\n--- Application Form Required ---");
            for (int i = 0; i < questions.size(); i++) {
                System.out.println("Q" + (i + 1) + ": " + questions.get(i));
                System.out.print("Your Answer: ");
                // Read answer and sanitize for internal CSV storage
                String answer = scanner.nextLine().trim().replace(",", " ").replace(";", " ");
                answers.add(answer);
            }
        }
        
        // 3. Attach Resume
        String resumePath;
        try {
            // Generate/Update the resume CSV file and get its path
            Path path = ResumeGenerator.generateCSV(jobseeker);
            resumePath = path.toString();
            System.out.println("\nSUCCESS: Resume attached from: " + resumePath);
        } catch (IOException e) {
            System.out.println("ERROR: Could not generate/attach resume. Applying without a resume path.");
            e.printStackTrace();
            resumePath = "ERROR_FILE_GENERATION";
        }

        // 4. Create Application (using the updated ApplicationManager.create)
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
            
            System.out.println(app.displayString(questions)); // Display with context
            System.out.println("-------------------------------------");
        }
    }

    private void doJob() {
        List<Application> hiredApps = getHiredApplications();

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
        System.out.println("* Typing...\\n* Reviewing...\\n* Completing assignment...");
        System.out.println("\nJob Complete! You earned +₱" + job.getPayment() + ".");
        walletBalance += job.getPayment(); // Use actual payment from job
        jobseeker.setMoney(walletBalance); // Update jobseeker's money in memory
        
        System.out.println("New Wallet Balance: ₱" + walletBalance);

        // mark hired application completed
        am.findByApplicantId(jobseeker.getId())
          .stream()
          .filter(a -> a.getJobId() == job.getJobId() && a.getStatus().equalsIgnoreCase("Hired"))
          .findFirst()
          .ifPresent(a -> am.updateStatus(a.getApplicationId(), "Completed"));
    }


    /* ====================  RESUME  ==================== */
    
    private void updateResume() {
        // Reuse existing ResumeGenerator logic, but trigger the load first if not done
        jobseeker.loadResumeFromCSV(); 

        System.out.println("\n--- Update Resume Details ---");
        System.out.println("Enter new value or leave blank to keep current value.");

        System.out.print("Phone [" + nullSafe(jobseeker.getPhone()) + "]: ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) jobseeker.setPhone(phone);

        System.out.print("Address [" + nullSafe(jobseeker.getAddress()) + "]: ");
        String address = scanner.nextLine().trim();
        if (!address.isEmpty()) jobseeker.setAddress(address);

        System.out.print("Summary [" + nullSafe(jobseeker.getSummary()) + "]: ");
        String summary = scanner.nextLine().trim();
        if (!summary.isEmpty()) jobseeker.setSummary(summary);
        
        System.out.print("Education [" + nullSafe(jobseeker.getEducation()) + "]: ");
        String education = scanner.nextLine().trim();
        if (!education.isEmpty()) jobseeker.setEducation(education);


        System.out.println("Resume details updated in memory. The resume file will be regenerated upon your next job application.");
    }

    // manageResume
    private void displayResumeMenu() {
        while (true) {
            System.out.println("\n===== My Resumé =====");
            viewResume();
            System.out.println("1. Edit Resume");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            switch (readInt()) {
                case 1 -> editResume();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    private void viewResume() {
        System.out.println("\n==========  RÉSUMÉ  ==========");
        Jobseeker js = this.jobseeker;
        System.out.println("Name        : " + js.getFullName());
        System.out.println("Phone       : " + nullSafe(js.getPhone()));
        System.out.println("Address     : " + nullSafe(js.getAddress()));
        System.out.println("Summary     : " + nullSafe(js.getSummary()));
        System.out.println("Education   : " + nullSafe(js.getEducation()));
        System.out.println("Skills      : " + listSafe(js.getSkillList()));
        System.out.println("Experience  : " + listSafe(js.getExperienceList()));
        System.out.println("===============================");
    }

    private void editResume() {
        System.out.println("\n====== EDIT RESUMÉ ======");
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

    /* ====================  MARKETPLACE  ==================== */
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

    /* ====================  REPORTS  ==================== */

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

    /* ====================  HELPERS  ==================== */
    
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

    private String nullSafe(String s) {  return (s == null || s.isBlank()) ? "N/A" : s; }
    private String listSafe(List<String> list) { return list.isEmpty() ? "N/A" : String.join("; ", list); }
}