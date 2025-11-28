package ui;

import java.util.List;
import java.util.Scanner;
import models.*;
import managers.*;
import utils.MenuPrinter;
import utils.ApplicationFormGenerator;
import utils.Refresh;
import utils.AsciiTable;

public class RecruiterMenu {
    private final Recruiter recruiter;
    private final JobPostingManager jpm;
    private final ApplicationManager am;
    private final ProductManager pm;
    private final TransactionManager tm;
    private final ReportManager rm;
    private final UserManager um;   
    private final Scanner scanner = new Scanner(System.in);
    private final ApplicationFormGenerator formGen = new ApplicationFormGenerator();

    public RecruiterMenu(Recruiter recruiter,
                         JobPostingManager jpm,
                         ApplicationManager am,
                         ProductManager pm,
                         TransactionManager tm,
                         ReportManager rm,
                         UserManager um) {
        this.recruiter = recruiter;
        this.jpm = jpm;
        this.am = am;
        this.pm = pm;
        this.tm = tm;
        this.rm = rm;
        this.um = um;               
    }

    /* =========================================================
                           MAIN LOOP
     ========================================================= */
    public void show() {
        while (true) {
            Refresh.refreshTerminal();          // ← clean screen
            MenuPrinter.printHeader("RECRUITER MENU");
            MenuPrinter.breadcrumb("Main Menu");
            System.out.println(" Recruiter: " + recruiter.getFullName());
            System.out.println();
            MenuPrinter.printOption("1", "Create Job Posting");
            MenuPrinter.printOption("2", "View My Job Postings");
            MenuPrinter.printOption("3", "Update Job Posting");
            MenuPrinter.printOption("4", "Delete Job Posting");
            MenuPrinter.printOption("5", "View Applicants for a Job");
            MenuPrinter.printOption("0", "<  Logout");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> createJobPosting();
                case "2" -> viewMyJobs();
                case "3" -> updateJobPosting();
                case "4" -> deleteJobPosting();
                case "5" -> viewApplicantsByJob();
                case "0" -> {
                    MenuPrinter.info("Logging out...");
                    return;
                }
                default -> MenuPrinter.error("Invalid choice – try again.");
            }
        }
    }

    /* =========================================================
                           1. CREATE
     ========================================================= */
    private void createJobPosting() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("CREATE JOB POSTING");
        MenuPrinter.prompt("Job Name");
        String jobName = scanner.nextLine().trim();
        MenuPrinter.prompt("Description");
        String description = scanner.nextLine().trim();
        MenuPrinter.prompt("Hours Needed");
        String hoursNeeded = scanner.nextLine().trim();
        MenuPrinter.prompt("Payment ($)");
        double payment = readDouble();

        List<String> questions = formGen.generateForm();
        JobPosting job = jpm.create(jobName, description, hoursNeeded, payment,
                                    recruiter.getFullName(), questions);

        MenuPrinter.success("Job created! ID = " + job.getJobId());
        MenuPrinter.pause();
    }

    /* =========================================================
                           2. VIEW MY JOBS
     ========================================================= */
    private void viewMyJobs() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("MY JOB POSTINGS");
        MenuPrinter.breadcrumb("Main Menu > My Job Postings");

        List<JobPosting> jobs = jpm.findByRecruiterName(recruiter.getFullName());
        if (jobs.isEmpty()) {
            MenuPrinter.info("You have not posted any jobs yet.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(jobs,
                new String[]{"ID", "Job Name", "Payment", "Status"},
                new int[]{4, 26, 10, 12},
                j -> new String[]{
                        String.valueOf(j.getJobId()),
                        j.getJobName(),
                        String.format("$%.2f", j.getPayment()),
                        j.getStatus()
                });
        MenuPrinter.pause();
    }

    /* =========================================================
                           3. UPDATE
     ========================================================= */
    private void updateJobPosting() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("UPDATE JOB POSTING");
        MenuPrinter.prompt("Job ID to update");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        if (job == null || !job.getRecruiterName().equals(recruiter.getFullName())) {
            MenuPrinter.error("Job not found or not yours.");
            MenuPrinter.pause();
            return;
        }

        System.out.println("Leave blank to keep current value.");
        MenuPrinter.prompt("New name (" + job.getJobName() + ")");
        String name = scanner.nextLine().trim();
        MenuPrinter.prompt("New description (" + job.getDescription() + ")");
        String desc = scanner.nextLine().trim();
        MenuPrinter.prompt("New hours (" + job.getHoursNeeded() + ")");
        String hours = scanner.nextLine().trim();
        MenuPrinter.prompt("New payment (" + job.getPayment() + ")");
        String payStr = scanner.nextLine().trim();
        Double payment = payStr.isEmpty() ? null : Double.valueOf(payStr);
        MenuPrinter.prompt("New status (Available/Closed) (" + job.getStatus() + ")");
        String status = scanner.nextLine().trim();

        List<String> questions = null;
        MenuPrinter.prompt("Update application questions too? (y/n)");
        if (scanner.nextLine().trim().equalsIgnoreCase("y"))
            questions = formGen.generateForm();

        boolean ok = jpm.update(jobId,
                name.isEmpty() ? null : name,
                desc.isEmpty() ? null : desc,
                hours.isEmpty() ? null : hours,
                payment,
                status.isEmpty() ? null : status,
                questions);

        if (ok) MenuPrinter.success("Job updated!");
        else    MenuPrinter.error("Update failed.");
        MenuPrinter.pause();
    }

    /* =========================================================
                           4. DELETE
     ========================================================= */
    private void deleteJobPosting() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("DELETE JOB POSTING");
        MenuPrinter.prompt("Job ID to delete");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        if (job == null || !job.getRecruiterName().equals(recruiter.getFullName())) {
            MenuPrinter.error("Job not found or not yours.");
            MenuPrinter.pause();
            return;
        }

        System.out.println("Job: " + job.getJobName() + " ($" + job.getPayment() + ")");
        MenuPrinter.prompt("Are you sure? (y/n)");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            boolean removed = jpm.delete(jobId);
            if (removed) MenuPrinter.success("Job deleted!");
            else         MenuPrinter.error("Could not delete job.");
        } else {
            MenuPrinter.info("Deletion cancelled.");
        }
        MenuPrinter.pause();
    }

    /* =========================================================
           5. VIEW APPLICANTS
     ========================================================= */
    private void viewApplicantsByJob() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("APPLICANTS FOR JOB");
        MenuPrinter.prompt("Job ID to view applicants");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        if (job == null || !job.getRecruiterName().equals(recruiter.getFullName())) {
            MenuPrinter.error("Job not found or not yours.");
            MenuPrinter.pause();
            return;
        }

        List<Application> apps = am.findByJobId(jobId);
        if (apps.isEmpty()) {
            MenuPrinter.info("No applicants yet for this job.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(apps,
                new String[]{"ID", "Applicant Name", "Status"},
                new int[]{4, 24, 12},
                a -> new String[]{
                        String.valueOf(a.getApplicationId()),
                        a.getApplicantName(),
                        a.getStatus()
                });

        processApplicants(jobId);
    }

    /* ---------------------------------------------------------
       applicant sub-menu
     --------------------------------------------------------- */
    private void processApplicants(int jobId) {
        while (true) {
            Refresh.refreshTerminal();          // ← clean screen
            MenuPrinter.printHeader("APPLICANT ACTIONS");
            MenuPrinter.breadcrumb("Main Menu > Applicants for Job " + jobId);
            MenuPrinter.printOption("1", "Change Application Status (Hire/Decline)");
            MenuPrinter.printOption("2", "View Full Application Details");
            MenuPrinter.printOption("0", "<  Back");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> changeApplicationStatus(jobId);
                case "2" -> viewFullApplicationDetails(jobId);
                case "0" -> { return; }
                default  -> MenuPrinter.error("Invalid choice.");
            }
        }
    }

    private void changeApplicationStatus(int jobId) {
        MenuPrinter.prompt("Application ID to process");
        int appId = readInt();
        Application app = am.findByApplicationId(appId);
        if (app == null || app.getJobId() != jobId) {
            MenuPrinter.error("Application not found or does not belong to this job.");
            return;
        }
        MenuPrinter.prompt("New Status (Hired/Declined/Pending)");
        String newStatus = scanner.nextLine().trim();
        if (!newStatus.equalsIgnoreCase("Hired") &&
            !newStatus.equalsIgnoreCase("Declined") &&
            !newStatus.equalsIgnoreCase("Pending")) {
            MenuPrinter.error("Status must be Hired, Declined or Pending.");
            return;
        }
        boolean ok = am.updateStatus(appId, newStatus);
        if (ok) MenuPrinter.success("Status updated to " + newStatus);
        else    MenuPrinter.error("Could not update status.");
    }

    private void viewFullApplicationDetails(int jobId) {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("APPLICATION DETAILS");
        MenuPrinter.prompt("Application ID to view");
        int appId = readInt();
        Application app = am.findByApplicationId(appId);
        if (app == null || app.getJobId() != jobId) {
            MenuPrinter.error("Application not found or does not belong to this job.");
            return;
        }
        JobPosting job = jpm.findById(jobId);
        System.out.println(app.displayString(job != null ? job.getApplicationQuestions() : List.of()));
        MenuPrinter.pause();
    }

    /* =========================================================
                           HELPERS
     ========================================================= */

    private void depositFunds() {
        System.out.println("\n--- Deposit Funds ---");
        System.out.println("DEBUG: Balance BEFORE deposit: ₱" + um.getBalance(recruiter.getId())); // ADD THIS
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
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("SEND SALARY");
        
        MenuPrinter.prompt("Enter Jobseeker User ID");
        int jsId = readInt();
        User js = um.findById(jsId);
        if (js == null) {
            MenuPrinter.error("Jobseeker not found.");
            MenuPrinter.pause();
            return;
        }

        MenuPrinter.prompt("Enter job name/description");
        String jobName = scanner.nextLine().trim();
        
        MenuPrinter.prompt("Enter salary amount");
        double amount = readDouble();
        if (amount <= 0) {
            MenuPrinter.error("Amount must be positive.");
            MenuPrinter.pause();
            return;
        }

        double balance = recruiter.getMoney();
        if (balance < amount) {
            MenuPrinter.error("Insufficient funds. Current balance: $" + balance);
            MenuPrinter.pause();
            return;
        }

        boolean ok = tm.transfer(recruiter.getId(), jsId, amount, "Salary: " + jobName);
        if (ok) {
            // Also record as salary transaction for better tracking
            tm.recordSalary(recruiter.getId(), recruiter.getFullName(),
                        jsId, js.getFullName(), amount, jobName);
            MenuPrinter.success("Salary sent successfully!");
            MenuPrinter.info("Amount: $" + amount);
            MenuPrinter.info("To: " + js.getFullName());
        } else {
            MenuPrinter.error("Failed to send salary.");
        }
        MenuPrinter.pause();
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
        boolean recorded = tm.recordPurchase(
            recruiter.getId(),
            recruiter.getFullName(),
            product.getProductId(),
            product.getProductName(),
            qty,
            total); // Note: Pass the POSITIVE total here.

        if (!recorded) {
            System.out.println("ERROR: Failed to record purchase transaction.");
        }

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

    /* =========================================================
                           HELPERS
     ========================================================= */
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                MenuPrinter.error("Please enter a valid integer.");
            }
        }
    }
    private double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                MenuPrinter.error("Please enter a valid number.");
            }
        }
    }
}