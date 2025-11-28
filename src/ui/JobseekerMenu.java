package ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import managers.*;
import models.*;
import ui.handlers.*;
import utils.MenuPrinter;
import utils.ResumeGenerator;
import utils.AsciiTable;
import utils.Refresh;

public class JobseekerMenu {
    private Jobseeker jobseeker;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private UserManager um;
    private TransactionManager tm;
    private ProductManager pm;
    private Scanner scanner = new Scanner(System.in);

    private final ReportHandler reportHandler;
    private final MarketplaceHandler marketplaceHandler;
    private final ResumeHandler resumeHandler;

    public JobseekerMenu(Jobseeker jobseeker,
                         JobPostingManager jpm,
                         ApplicationManager am,
                         ProductManager pm,
                         TransactionManager tm,
                         ReportManager rm) {
        this.jobseeker = jobseeker;
        this.jpm = jpm;
        this.am = am;
        this.tm = tm;
        this.reportHandler = new ReportHandler(jobseeker, rm, scanner);
        this.marketplaceHandler = new MarketplaceHandler(jobseeker, pm, tm, scanner);
        this.resumeHandler = new ResumeHandler(jobseeker, scanner);
    }

    /* =========================================================
                           MAIN LOOP
     ========================================================= */
    public void show() {
        while (true) {
            Refresh.refreshTerminal();          // ← clean screen
            MenuPrinter.printHeader("JOBSEEKER MENU");
            MenuPrinter.breadcrumb("Main Menu");
            System.out.printf(" Wallet Balance: $%.2f%n%n", jobseeker.getMoney());
            MenuPrinter.printOption("1", "Browse Available Jobs");
            MenuPrinter.printOption("2", "My Applications");
            MenuPrinter.printOption("3", "Marketplace");
            MenuPrinter.printOption("4", "My Reports");
            MenuPrinter.printOption("5", ">  Do Job (Hired)");
            MenuPrinter.printOption("6", "Update Resume");
            MenuPrinter.printOption("7", "Deposit Funds");
            MenuPrinter.printOption("8", "Withdraw Funds");
            MenuPrinter.printOption("9", "Send Money");
            MenuPrinter.printOption("10", "View My Transactions");
            MenuPrinter.printOption("0", "<  Logout");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> browseJobs();
                case "2" -> viewApplications();
                case "3" -> marketplaceHandler.showMenu();
                case "4" -> reportHandler.showMenu();
                case "5" -> doJob();
                case "6" -> resumeHandler.showMenu();
                case "7" -> depositFunds();
                case "8" -> withdrawFunds();
                case "9" -> sendMoney();
                case "10" -> viewMyTransactions();
                case "0" -> {
                    MenuPrinter.info("Logging out...");
                    return;
                }
                default  -> MenuPrinter.error("Invalid choice – try again.");
            }
        }
    }

    /* =========================================================
                           1. BROWSE / APPLY
     ========================================================= */
    private void browseJobs() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("AVAILABLE JOBS");
        MenuPrinter.breadcrumb("Main Menu > Browse Jobs");
        List<JobPosting> available = jpm.findAll().stream()
                                        .filter(j -> j.getStatus().equalsIgnoreCase("Available"))
                                        .toList();

        if (available.isEmpty()) {
            MenuPrinter.info("No available jobs right now – check again later!");
            MenuPrinter.pause();
            return;
        }

        /* bullet-proof table */
        AsciiTable.print(available,
                new String[]{"ID", "Job Name", "Payment", "Questions"},
                new int[]{4, 26, 10, 12},
                j -> new String[]{
                        String.valueOf(j.getJobId()),
                        j.getJobName(),
                        String.format("$%.2f", j.getPayment()),
                        j.getApplicationQuestions().size() + " question(s)"
                });

        MenuPrinter.prompt("Enter Job ID to apply (0 to cancel)");
        int jobId = readInt();
        if (jobId == 0) return;

        JobPosting job = jpm.findById(jobId);
        if (job == null || !job.getStatus().equalsIgnoreCase("Available")) {
            MenuPrinter.error("Invalid Job ID or job no longer available.");
            MenuPrinter.pause();
            return;
        }
        applyForJob(job);
    }

    private void applyForJob(JobPosting job) {
        Refresh.refreshTerminal();          // ← clean screen
        boolean already = am.findByApplicantId(jobseeker.getId()).stream()
                            .anyMatch(a -> a.getJobId() == job.getJobId());
        if (already) {
            MenuPrinter.warning("You already applied for this job.");
            MenuPrinter.pause();
            return;
        }

        MenuPrinter.printHeader("APPLY FOR JOB");
        System.out.println("Job: " + job.getJobName() + " ($" + job.getPayment() + ")");
        System.out.println();

        List<String> questions = job.getApplicationQuestions();
        List<String> answers = new ArrayList<>();
        if (!questions.isEmpty()) {
            MenuPrinter.info("Answer the following questions:");
            for (int i = 0; i < questions.size(); i++) {
                MenuPrinter.prompt("Q" + (i + 1) + ") " + questions.get(i));
                answers.add(scanner.nextLine().trim().replace(",", " ").replace(";", " "));
            }
        }

        String resumePath;
        try {
            Path p = ResumeGenerator.generateCSV(jobseeker);
            resumePath = p.toString();
            MenuPrinter.success("Resume attached: " + resumePath);
        } catch (IOException ex) {
            MenuPrinter.error("Could not generate resume – proceeding without it.");
            resumePath = "N/A";
        }

        am.create(job.getJobId(), jobseeker.getId(), jobseeker.getFullName(), answers, resumePath);
        MenuPrinter.success("Application submitted!");
        MenuPrinter.pause();
    }

    /* =========================================================
                           2. MY APPLICATIONS
     ========================================================= */
    private void viewApplications() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("MY APPLICATIONS");
        MenuPrinter.breadcrumb("Main Menu > My Applications");
        List<Application> apps = am.findByApplicantId(jobseeker.getId());
        if (apps.isEmpty()) {
            MenuPrinter.info("You have not applied to any jobs yet.");
            MenuPrinter.pause();
            return;
        }
        apps.forEach(a -> {
            JobPosting j = jpm.findById(a.getJobId());
            System.out.println(a.displayString(j != null ? j.getApplicationQuestions() : List.of()));
        });
        MenuPrinter.pause();
    }

    /* =========================================================
                           5. DO JOB
     ========================================================= */
    private void doJob() {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("COMPLETE A JOB");
        MenuPrinter.breadcrumb("Main Menu > Do Job");
        List<Application> hired = am.findByApplicantId(jobseeker.getId()).stream()
                                      .filter(a -> a.getStatus().equalsIgnoreCase("Hired"))
                                      .toList();
        if (hired.isEmpty()) {
            MenuPrinter.warning("You are not hired for any job yet.");
            MenuPrinter.pause();
            return;
        }

        /* bullet-proof table for hired jobs */
        AsciiTable.print(hired,
                new String[]{"#", "Job Name", "Payment"},
                new int[]{4, 26, 10},
                a -> {
                    JobPosting j = jpm.findById(a.getJobId());
                    return new String[]{
                            String.valueOf(hired.indexOf(a) + 1),
                            j != null ? j.getJobName() : "N/A",
                            j != null ? String.format("$%.2f", j.getPayment()) : "$0.00"
                    };
                });

        MenuPrinter.prompt("Job number (0 to cancel)");
        int idx = readInt();
        if (idx < 1 || idx > hired.size()) return;

        Application app = hired.get(idx - 1);
        JobPosting job = jpm.findById(app.getJobId());
        if (job == null) return;

        simulateJobCompletion(job, app);
    }

    private void simulateJobCompletion(JobPosting job, Application app) {
        Refresh.refreshTerminal();          // ← clean screen
        MenuPrinter.printHeader("WORKING ON JOB");
        System.out.println("Job: " + job.getJobName());
        System.out.println("-> Typing... -> Reviewing... -> Finalising...");
        double earned = job.getPayment();
        jobseeker.setMoney(jobseeker.getMoney() + earned);
        am.updateStatus(app.getApplicationId(), "Completed");
        MenuPrinter.success("Job complete! You earned $" + earned);
        MenuPrinter.info("New wallet balance: $" + jobseeker.getMoney());
        MenuPrinter.pause();
    }

    /* ====================  FINANCIAL OPERATIONS  ==================== */

    private void depositFunds() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("DEPOSIT FUNDS");
        MenuPrinter.breadcrumb("Main Menu > Deposit");
        
        System.out.println("Current Balance: $" + String.format("%.2f", jobseeker.getMoney()));
        MenuPrinter.prompt("Enter amount to deposit");
        double amount = readDouble();
        
        if (amount <= 0) {
            MenuPrinter.error("Amount must be positive.");
            MenuPrinter.pause();
            return;
        }

        boolean ok = tm.deposit(jobseeker.getId(), amount, "Jobseeker Deposit");
        if (ok) {
            MenuPrinter.success("Deposit successful!");
            MenuPrinter.info("New Balance: $" + String.format("%.2f", jobseeker.getMoney()));
        } else {
            MenuPrinter.error("Deposit failed.");
        }
        MenuPrinter.pause();
    }

    private void withdrawFunds() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("WITHDRAW FUNDS");
        MenuPrinter.breadcrumb("Main Menu > Withdraw");
        
        System.out.println("Current Balance: $" + String.format("%.2f", jobseeker.getMoney()));
        MenuPrinter.prompt("Enter amount to withdraw");
        double amount = readDouble();
        
        if (amount <= 0) {
            MenuPrinter.error("Amount must be positive.");
            MenuPrinter.pause();
            return;
        }

        if (jobseeker.getMoney() < amount) {
            MenuPrinter.error("Insufficient funds.");
            MenuPrinter.pause();
            return;
        }

        boolean ok = tm.withdraw(jobseeker.getId(), amount, "Jobseeker Withdrawal");
        if (ok) {
            MenuPrinter.success("Withdrawal successful!");
            MenuPrinter.info("New Balance: $" + String.format("%.2f", jobseeker.getMoney()));
        } else {
            MenuPrinter.error("Withdrawal failed.");
        }
        MenuPrinter.pause();
    }

    private void sendMoney() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("SEND MONEY");
        MenuPrinter.breadcrumb("Main Menu > Send Money");
        
        MenuPrinter.prompt("Enter recipient User ID");
        int recipientId = readInt();
        
        if (recipientId == jobseeker.getId()) {
            MenuPrinter.error("Cannot send money to yourself.");
            MenuPrinter.pause();
            return;
        }

        MenuPrinter.prompt("Enter amount to send");
        double amount = readDouble();
        
        if (amount <= 0) {
            MenuPrinter.error("Amount must be positive.");
            MenuPrinter.pause();
            return;
        }

        if (jobseeker.getMoney() < amount) {
            MenuPrinter.error("Insufficient funds.");
            MenuPrinter.pause();
            return;
        }

        boolean ok = tm.transfer(jobseeker.getId(), recipientId, amount, "Money Transfer");
        if (ok) {
            MenuPrinter.success("Transfer successful!");
            MenuPrinter.info("New Balance: $" + String.format("%.2f", jobseeker.getMoney()));
        } else {
            MenuPrinter.error("Transfer failed.");
        }
        MenuPrinter.pause();
    }

    private void viewMyTransactions() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("MY TRANSACTIONS");
        MenuPrinter.breadcrumb("Main Menu > My Transactions");

        List<Transaction> txs = tm.findByUserId(jobseeker.getId());
        if (txs.isEmpty()) {
            MenuPrinter.info("No transactions found.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(txs,
                new String[]{"ID", "Type", "From", "To", "Amount", "Date"},
                new int[]{6, 10, 16, 16, 10, 18},
                t -> new String[]{
                        String.valueOf(t.getTransactionId()),
                        t.getType(),
                        t.getFromUsername(),
                        t.getToUsername(),
                        String.format("$%.2f", t.getAmount()),
                        t.getTimestamp()
                });
        MenuPrinter.pause();
    }

    /* =========================================================
                           HELPERS
     ========================================================= */
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                MenuPrinter.error("Please enter a valid number.");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}