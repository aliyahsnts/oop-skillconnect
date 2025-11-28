package ui;

import java.util.List;
import java.util.Scanner;
import models.*;
import managers.*;
import utils.ApplicationFormGenerator; // Import the new generator

public class RecruiterMenu {
    private final Recruiter recruiter;
    private final JobPostingManager jpm;
    private final ApplicationManager am;
    private final ProductManager pm;
    private final TransactionManager tm;
    private final ReviewManager vm;
    private final UserManager um;   
    private final Scanner scanner = new Scanner(System.in);
    private final ApplicationFormGenerator formGenerator = new ApplicationFormGenerator(); // Instance of the generator

    // NEW constructor signature – added UserManager
    public RecruiterMenu(Recruiter recruiter,
                         JobPostingManager jpm,
                         ApplicationManager am,
                         ProductManager pm,
                         TransactionManager tm,
                         ReviewManager vm,
                         UserManager um) {
        this.recruiter = recruiter;
        this.jpm = jpm;
        this.am = am;
        this.pm = pm;
        this.tm = tm;
        this.vm = vm;
        this.um = um;               
    }

    public void show() {
        while (true) {
            System.out.println("\n=== RECRUITER MENU ===");
            System.out.println("Welcome, " + recruiter.getFullName() + "!");
            System.out.println("[1] Create Job Posting");
            System.out.println("[2] View All Job Postings");
            System.out.println("[3] Update Job Posting");
            System.out.println("[4] Delete Job Posting");
            System.out.println("[5] View Applicants for a Job"); 
            System.out.println("[6] Deposit Funds");// NEW OPTION
            System.out.println("[7] Withdraw Funds");
            System.out.println("[8] Send Salary (to Jobseeker)");
            System.out.println("[9] Purchase Product");
            System.out.println("[10] View Transactions");
            System.out.println("[0] Logout");
            System.out.print("Enter your choice: ");
        
            switch (scanner.nextLine().trim()) {
                case "1" -> createJobPosting();
                case "2" -> viewAllJobs();
                case "3" -> updateJobPosting();
                case "4" -> deleteJob();
                case "5" -> viewApplicantsByJob();
                case "6" -> depositFunds();
                case "7" -> withdrawFunds();
                case "8" -> sendSalary();
                case "9" -> purchaseProduct();
                case "10" -> viewTransactions();
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }

        }
    }

    private void createJobPosting() {
        System.out.println("\n--- Create New Job Posting ---");
        System.out.print("Job Name: ");
        String jobName = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Hours Needed: ");
        String hoursNeeded = scanner.nextLine();
        System.out.print("Payment (₱): ");
        double payment = readDouble();
        
        // NEW: Generate Application Form Questions
        List<String> questions = formGenerator.generateForm(); 

        JobPosting job = jpm.create(jobName, description, hoursNeeded, payment, recruiter.getFullName(), questions);
        System.out.println("SUCCESS: Job Posting created with ID: " + job.getJobId());
        System.out.println(job.displayString());
    }

    // ... (viewAllJobs, updateJobPosting, deleteJob methods remain similar)

    private void viewAllJobs() {
        System.out.println("\n--- Your Job Postings ---");
        List<JobPosting> jobs = jpm.findByRecruiterName(recruiter.getFullName());
        if (jobs.isEmpty()) {
            System.out.println("No job postings found.");
            return;
        }
        for (JobPosting job : jobs) {
            System.out.println(job.displayString());
            System.out.println("-------------------------------------");
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
        System.out.println("Current job info:\n" + job.displayString());

        System.out.print("Enter new job name (blank to keep): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new description (blank to keep): ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter new hours (blank to keep): ");
        String hours = scanner.nextLine().trim();
        System.out.print("Enter new payment (blank to keep): ");
        String payStr = scanner.nextLine().trim();
        System.out.print("Enter new status (Available/Closed) (blank to keep): ");
        String status = scanner.nextLine().trim();

        Double payment = null;
        if (!payStr.isEmpty()) {
            try {
                payment = Double.valueOf(payStr);
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
            status.isEmpty() ? null : status,
            null);
        System.out.println(ok ? "SUCCESS: Job updated!" : "ERROR: Could not update job.");
    }

    private void deleteJob() {
        System.out.print("Enter Job Number to delete: ");
        int jobNum = readInt();
        JobPosting job = jpm.findById(jobNum);
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        System.out.println("Job info:\n" + job.displayString());
        System.out.print("Are you sure you want to delete this job? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            boolean removed = jpm.delete(jobNum);
            System.out.println(removed ? "SUCCESS: Job removed!" : "ERROR: Could not remove job.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void updateJobPosting() {
        System.out.print("Enter Job ID to update: ");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }

        if (!job.getRecruiterName().equals(recruiter.getFullName())) {
            System.out.println("ERROR: You can only update your own job postings.");
            return;
        }

        System.out.println("Updating Job: " + job.getJobName());
        System.out.println("Enter new value or leave blank to keep current value.");

        System.out.print("Job Name [" + job.getJobName() + "]: ");
        String jobName = scanner.nextLine().trim();

        System.out.print("Description [" + job.getDescription() + "]: ");
        String description = scanner.nextLine().trim();

        System.out.print("Hours Needed [" + job.getHoursNeeded() + "]: ");
        String hoursNeeded = scanner.nextLine().trim();

        System.out.print("Payment [" + job.getPayment() + "]: ");
        String paymentStr = scanner.nextLine().trim();
        Double payment = paymentStr.isEmpty() ? null : readDouble(paymentStr);

        System.out.print("Status [" + job.getStatus() + "]: ");
        String status = scanner.nextLine().trim();
        
        // Option to update questions
        List<String> questions = null;
        System.out.print("Do you want to update the Application Form questions? (Y/N, current " + job.getApplicationQuestions().size() + "): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
             questions = formGenerator.generateForm(); 
        }

        boolean ok = jpm.update(
            jobId, 
            jobName.isEmpty() ? null : jobName, 
            description.isEmpty() ? null : description, 
            hoursNeeded.isEmpty() ? null : hoursNeeded,
            payment, 
            status.isEmpty() ? null : status,
            questions // Pass null or the new list
        );
        System.out.println(ok ? "SUCCESS: Job updated!" : "ERROR: Could not update job.");
    }
    
    // NEW METHOD: View Applicants for a Specific Job
    private void viewApplicantsByJob() {
        System.out.print("Enter Job ID to view applicants for: ");
        int jobId = readInt();
        
        JobPosting job = jpm.findById(jobId);
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }

        if (!job.getRecruiterName().equals(recruiter.getFullName())) {
            System.out.println("ERROR: You can only view applicants for your own job postings.");
            return;
        }
        
        System.out.println("\n--- Applicants for Job ID " + jobId + ": " + job.getJobName() + " ---");
        List<Application> applicants = am.findByJobId(jobId);
        
        if (applicants.isEmpty()) {
            System.out.println("No applicants for this job yet.");
            return;
        }
        
        for (Application app : applicants) {
            // Retrieve job posting again to get the current questions for display
            JobPosting currentJob = jpm.findById(app.getJobId()); 
            List<String> questions = currentJob != null ? currentJob.getApplicationQuestions() : List.of();

            System.out.println(app.displayString(questions)); // Display with context
            System.out.println("Status: " + app.getStatus());
            System.out.println("-------------------------------------");
        }
        
        // Offer a menu for actions (e.g., Hire/Decline)
        processApplicants(jobId);
    }

    private void processApplicants(int jobId) {
        while(true) {
            System.out.println("\n[1] Change Application Status (Hire/Decline)");
            System.out.println("[2] View Applicant Resume (WIP: File system viewing)");
            System.out.println("[0] Back to Recruiter Menu");
            System.out.print("Enter choice: ");
            
            switch (scanner.nextLine().trim()) {
                case "1" -> changeApplicationStatus(jobId);
                case "2" -> System.out.println("Feature not fully implemented. Resume path saved in application data.");
                case "0" -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void changeApplicationStatus(int jobId) {
        System.out.print("Enter Application ID to process: ");
        int appId = readInt();
        Application app = am.findByApplicationId(appId);

        if (app == null || app.getJobId() != jobId) {
            System.out.println("ERROR: Application not found or does not belong to this job.");
            return;
        }

        System.out.print("New Status (Hired/Declined/Pending): ");
        String newStatus = scanner.nextLine().trim();

        if (newStatus.equalsIgnoreCase("Hired") || 
            newStatus.equalsIgnoreCase("Declined") ||
            newStatus.equalsIgnoreCase("Pending")) {
            
            boolean ok = am.updateStatus(appId, newStatus);
            System.out.println(ok ? "SUCCESS: Application status updated to " + newStatus + "." : "ERROR: Failed to update status.");
        } else {
            System.out.println("ERROR: Invalid status. Must be Hired, Declined, or Pending.");
        }
    }


    // Withdraw funds for the recruiter
    private void withdrawFunds() {
        System.out.println("\n--- Withdraw Funds ---");
        System.out.print("Enter amount to withdraw (₱): ");
        double amount = readDouble();
        if (amount <= 0) {
            System.out.println("ERROR: Amount must be positive.");
            return;
        }

        double balance = um.getBalance(recruiter.getId());
        if (balance < amount) {
            System.out.println("ERROR: Insufficient funds. Current balance: ₱" + balance);
            return;
        }

        boolean ok = tm.withdraw(recruiter.getId(), amount, "Withdraw by recruiter");
        System.out.println(ok ? "SUCCESS: Withdrawal completed." : "ERROR: Withdrawal failed.");
    }

    // Send salary from recruiter to jobseeker
    private void sendSalary() {
        System.out.println("\n--- Send Salary to Jobseeker ---");
        System.out.print("Enter Jobseeker User ID: ");
        int jsId = readInt();
        User js = um.findById(jsId);
        if (js == null) {
            System.out.println("ERROR: Jobseeker not found.");
            return;
        }

        System.out.print("Enter amount to send (₱): ");
        double amount = readDouble();
        if (amount <= 0) {
            System.out.println("ERROR: Amount must be positive.");
            return;
        }

        double balance = um.getBalance(recruiter.getId());
        if (balance < amount) {
            System.out.println("ERROR: Insufficient funds. Current balance: ₱" + balance);
            return;
        }

        boolean ok = tm.transfer(recruiter.getId(), jsId, amount, "Salary");
        System.out.println(ok ? "SUCCESS: Sent ₱" + amount + " to " + js.getFullName() : "ERROR: Failed to send salary.");
    }

    // Purchase product as recruiter
    private void purchaseProduct() {
        System.out.println("\n--- Purchase Product ---");
        List<Product> products = pm.findAll();
        if (products == null || products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        for (Product p : products) {
            System.out.println(p.displayString());
            System.out.println("-------------------------------------");
        }

        System.out.print("Enter Product ID to purchase: ");
        int pid = readInt();
        Product product = pm.findById(pid);
        if (product == null) {
            System.out.println("ERROR: Product not found.");
            return;
        }

        System.out.print("Enter quantity: ");
        int qty = readInt();
        if (qty <= 0) {
            System.out.println("ERROR: Quantity must be positive.");
            return;
        }

        double total = product.getPrice() * qty;
        double balance = um.getBalance(recruiter.getId());
        if (balance < total) {
            System.out.println("ERROR: Insufficient funds. Current balance: ₱" + balance);
            return;
        }

        // Debit recruiter balance first to ensure atomic behavior
        boolean adjusted = um.adjustBalance(recruiter.getId(), -total);
        if (!adjusted) {
            System.out.println("ERROR: Failed to deduct balance; purchase aborted.");
            return;
        }

        // Record transaction (store negative amount to reflect debit)
        tm.create(
            recruiter.getId(),
            recruiter.getFullName(),
            product.getProductId(),
            product.getProductName(),  // <-- changed from getName()
            qty,
            -total);
             System.out.println("SUCCESS: Purchased " + qty + " x " + product.getProductName() + " for $" + total + ".");
    }

    // View transaction history for recruiter
    private void viewTransactions() {
        System.out.println("\n--- Transaction History ---");
        List<Transaction> txs = tm.findByUserId(recruiter.getId());
        if (txs == null || txs.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction tx : txs) {
            System.out.println(tx.displayString());
            System.out.println("-------------------------------------");
        }
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }
    
    // Helper method to parse double when input is guaranteed to be a number string
    private Double readDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            // Should not happen if called correctly in updateJobPosting, but robust
            System.out.print("Invalid number format. Using 0.0: ");
            return 0.0;
        }
    }
    
    private void depositFunds() {
    System.out.println("\n--- Deposit Funds ---");
    System.out.print("Enter amount to deposit (₱): ");
    double amount = readDouble();
    if (amount <= 0) {
        System.out.println("ERROR: Amount must be positive.");
        return;
    }

    // Delegate deposit behavior to transaction manager
    boolean ok = tm.deposit(recruiter.getId(), amount, "Deposit by recruiter");
    System.out.println(ok ? "SUCCESS: Funds deposited!" : "ERROR: Deposit failed.");
}
    
    private double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number for payment: ");
            }
        }
    }
}