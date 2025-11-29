package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import models.*;
import managers.*;
import utils.MenuPrinter;
import utils.Refresh;
import utils.AsciiTable;

public class AdminMenu {

    private final Admin admin;
    private final UserManager userManager;
    private final JobPostingManager jpm;
    private final ProductManager pm;
    private final TransactionManager tm;
    private final ReportManager rm;
    private final Scanner scanner = new Scanner(System.in);

    public AdminMenu(Admin admin,
                     UserManager um,
                     JobPostingManager jpm,
                     ApplicationManager am,
                     ProductManager pm,
                     TransactionManager tm,
                     ReportManager rm) {
        this.admin = admin;
        this.userManager = um;
        this.jpm = jpm;
        this.pm = pm;
        this.tm = tm;
        this.rm = rm;
    }

    /* =========================================================
                           MAIN LOOP
    ========================================================= */
    public void show() {
        while (true) {
            Refresh.refreshTerminal();
            MenuPrinter.printHeader("ADMIN MENU");
            MenuPrinter.breadcrumb("Main Menu");
            System.out.println(" Admin: " + admin.getFullName());
            System.out.println();
            MenuPrinter.printOption("1", "Manage Users");
            MenuPrinter.printOption("2", "Manage Jobs");
            MenuPrinter.printOption("3", "Manage Marketplace");
            MenuPrinter.printOption("4", "View All Transactions");
            MenuPrinter.printOption("5", "Manage Reports");
            MenuPrinter.printOption("0", "<  Logout");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> manageUsers();
                case "2" -> manageJobs();
                case "3" -> manageMarketplace();
                case "4" -> viewAllTransactions();
                case "5" -> manageReports();
                case "0" -> {
                    if (confirmLogout()) return;
                }
                default -> MenuPrinter.error("Invalid choice – try again.");
            }
        }
    }

    /* =========================================================
                           1. MANAGE USERS
    ========================================================= */
    private void manageUsers() {
        while (true) {
            Refresh.refreshTerminal();
            MenuPrinter.printHeader("MANAGE USERS");
            MenuPrinter.breadcrumb("Main Menu > Manage Users");
            MenuPrinter.printOption("1", "View All Users");
            MenuPrinter.printOption("2", "Create Account");
            MenuPrinter.printOption("3", "Update Account");
            MenuPrinter.printOption("4", "Delete Account");
            MenuPrinter.printOption("0", "<  Back");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllAccounts();
                case "2" -> createAccount();
                case "3" -> updateAccount();
                case "4" -> deleteAccount();
                case "0" -> { return; }
                default  -> MenuPrinter.error("Invalid option.");
            }
        }
    }

    private void viewAllAccounts() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("ALL USERS");
        MenuPrinter.breadcrumb("Main Menu > Manage Users > View All Users");

        List<User> users = userManager.getAllUsers();
        if (users.isEmpty()) {
            MenuPrinter.info("No users found.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(users,
                new String[]{"ID", "Name", "Username", "Type", "Money"},
                new int[]{4, 20, 15, 10, 10},
                u -> new String[]{
                        String.valueOf(u.getId()),
                        u.getFullName(),
                        u.getUsername(),
                        typeToString(u.getUserType()),
                        String.format("$%.2f", u.getMoney())
                });
        MenuPrinter.pause();
    }

    private String typeToString(int type) {
        return switch (type) {
            case 1 -> "Jobseeker";
            case 2 -> "Recruiter";
            case 3 -> "Admin";
            default -> "Unknown";
        };
    }

    private void createAccount() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("CREATE ACCOUNT");
        MenuPrinter.breadcrumb("Main Menu > Manage Users > Create Account");

        MenuPrinter.prompt("Full name");
        String fullName = scanner.nextLine().trim();
        MenuPrinter.prompt("Username");
        String username = scanner.nextLine().trim();
        if (userManager.usernameExists(username)) {
            MenuPrinter.error("Username already exists!");
            MenuPrinter.pause();
            return;
        }
        MenuPrinter.prompt("Password");
        String password = scanner.nextLine().trim();
        MenuPrinter.prompt("User type (1=Jobseeker, 2=Recruiter, 3=Admin)");
        int type = readInt();
        if (type < 1 || type > 3) {
            MenuPrinter.error("Invalid user type.");
            MenuPrinter.pause();
            return;
        }

        int newId = userManager.nextId();
        User newUser = switch (type) {
            case 1 -> new Jobseeker(newId, fullName, username, password, 0.0);
            case 2 -> new Recruiter(newId, fullName, username, password, 0.0);
            case 3 -> new Admin(newId, fullName, username, password, 0.0);
            default -> null;
        };
        userManager.addUser(newUser);
        MenuPrinter.success("Account created! ID=" + newUser.getId());
        MenuPrinter.pause();
    }

    private void updateAccount() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("UPDATE ACCOUNT");
        MenuPrinter.breadcrumb("Main Menu > Manage Users > Update Account");

        MenuPrinter.prompt("Username to update");
        String username = scanner.nextLine().trim();
        User user = userManager.findUser(username);
        if (user == null) {
            MenuPrinter.error("User not found.");
            MenuPrinter.pause();
            return;
        }

        System.out.println("Current info:");
        System.out.println("Name: " + user.getFullName());
        System.out.println("Username: " + user.getUsername());

        MenuPrinter.prompt("New full name (blank to keep)");
        String fullName = scanner.nextLine().trim();
        MenuPrinter.prompt("New password (blank to keep)");
        String password = scanner.nextLine().trim();

        if (!fullName.isEmpty()) user.setFullName(fullName);
        if (!password.isEmpty()) user.setPassword(password);
        userManager.saveUsers();
        MenuPrinter.success("Account updated!");
        MenuPrinter.pause();
    }

    private void deleteAccount() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("DELETE ACCOUNT");
        MenuPrinter.breadcrumb("Main Menu > Manage Users > Delete Account");

        MenuPrinter.prompt("Username to delete");
        String username = scanner.nextLine().trim();
        User user = userManager.findUser(username);
        if (user == null) {
            MenuPrinter.error("User not found.");
            MenuPrinter.pause();
            return;
        }

        System.out.println("User info:");
        System.out.println("Name: " + user.getFullName());
        System.out.println("Username: " + user.getUsername());

        MenuPrinter.prompt("Are you sure? (y/n)");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            boolean removed = userManager.deleteUser(username);
            if (removed) MenuPrinter.success("Account deleted!");
            else         MenuPrinter.error("Could not delete account.");
        } else {
            MenuPrinter.info("Deletion cancelled.");
        }
        MenuPrinter.pause();
    }

    /* =========================================================
                           2. MANAGE JOBS
    ========================================================= */
    private void manageJobs() {
        while (true) {
            Refresh.refreshTerminal();
            MenuPrinter.printHeader("MANAGE JOBS");
            MenuPrinter.breadcrumb("Main Menu > Manage Jobs");
            MenuPrinter.printOption("1", "View All Jobs");
            MenuPrinter.printOption("2", "Update Job");
            MenuPrinter.printOption("3", "Delete Job");
            MenuPrinter.printOption("0", "<  Back");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllJobs();
                case "2" -> updateJob();
                case "3" -> deleteJob();
                case "0" -> { return; }
                default  -> MenuPrinter.error("Invalid option.");
            }
        }
    }

    private void viewAllJobs() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("ALL JOBS");
        MenuPrinter.breadcrumb("Main Menu > Manage Jobs > View All Jobs");

        List<JobPosting> jobs = jpm.findAll();
        if (jobs.isEmpty()) {
            MenuPrinter.info("No job postings found.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(jobs,
                new String[]{"ID", "Job Name", "Payment", "Status", "Recruiter"},
                new int[]{4, 24, 10, 12, 15},
                j -> new String[]{
                        String.valueOf(j.getJobId()),
                        j.getJobName(),
                        String.format("$%.2f", j.getPayment()),
                        j.getStatus(),
                        j.getRecruiterName()
                });
        MenuPrinter.pause();
    }

    private void updateJob() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("UPDATE JOB");
        MenuPrinter.breadcrumb("Main Menu > Manage Jobs > Update Job");

        MenuPrinter.prompt("Job ID to update");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        if (job == null) {
            MenuPrinter.error("Job not found.");
            MenuPrinter.pause();
            return;
        }

        System.out.println("Current job info:");
        System.out.println(job.displayString());

        System.out.println("\nLeave blank to keep current value.");
        MenuPrinter.prompt("New job name");
        String name = scanner.nextLine().trim();
        MenuPrinter.prompt("New description");
        String desc = scanner.nextLine().trim();
        MenuPrinter.prompt("New hours needed");
        String hours = scanner.nextLine().trim();
        MenuPrinter.prompt("New payment");
        String payStr = scanner.nextLine().trim();
        Double payment = null;
        if (!payStr.isEmpty()) {
            try {
                payment = Double.valueOf(payStr);
            } catch (NumberFormatException e) {
                MenuPrinter.error("Invalid payment. Update cancelled.");
                MenuPrinter.pause();
                return;
            }
        }
        MenuPrinter.prompt("New status (Available/Closed)");
        String status = scanner.nextLine().trim();

        boolean ok = jpm.update(jobId,
                name.isEmpty() ? null : name,
                desc.isEmpty() ? null : desc,
                hours.isEmpty() ? null : hours,
                payment,
                status.isEmpty() ? null : status,
                null);

        if (ok) MenuPrinter.success("Job successfully updated!");
        else    MenuPrinter.error("Could not update job.");
        MenuPrinter.pause();
    }

    private void deleteJob() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("DELETE JOB");
        MenuPrinter.breadcrumb("Main Menu > Manage Jobs > Delete Job");

        MenuPrinter.prompt("Job ID to delete");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        if (job == null) {
            MenuPrinter.error("Job not found.");
            MenuPrinter.pause();
            return;
        }

        System.out.println(job.displayString());
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
                      3. MANAGE MARKETPLACE
    ========================================================= */
    private void manageMarketplace() {
        while (true) {
            Refresh.refreshTerminal();
            MenuPrinter.printHeader("MANAGE MARKETPLACE");
            MenuPrinter.breadcrumb("Main Menu > Manage Marketplace");
            MenuPrinter.printOption("1", "View All Products");
            MenuPrinter.printOption("2", "Add Product");
            MenuPrinter.printOption("3", "Update Product");
            MenuPrinter.printOption("4", "Delete Product");
            MenuPrinter.printOption("0", "<  Back");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllProducts();
                case "2" -> createProduct();
                case "3" -> updateProduct();
                case "4" -> deleteProduct();
                case "0" -> { return; }
                default  -> MenuPrinter.error("Invalid option.");
            }
        }
    }

    private void viewAllProducts() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("ALL PRODUCTS");
        MenuPrinter.breadcrumb("Main Menu > Manage Marketplace > View All Products");

        List<Product> products = pm.findAll();
        if (products.isEmpty()) {
            MenuPrinter.info("No products found.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(products,
                new String[]{"ID", "Product Name", "Price", "Quantity", "Status"},
                new int[]{4, 20, 8, 8, 12},
                p -> new String[]{
                        String.valueOf(p.getProductId()),
                        p.getProductName(),
                        String.format("$%.2f", p.getPrice()),
                        String.valueOf(p.getQuantity()),
                        p.getStatus()
                });
        MenuPrinter.pause();
    }

    private void createProduct() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("CREATE PRODUCT");
        MenuPrinter.breadcrumb("Main Menu > Manage Marketplace > Create Product");

        MenuPrinter.prompt("Product name");
        String name = scanner.nextLine().trim();
        MenuPrinter.prompt("Description");
        String desc = scanner.nextLine().trim();
        MenuPrinter.prompt("Price ($)");
        double price = readDouble();
        MenuPrinter.prompt("Quantity");
        int qty = readInt();

        Product p = pm.create(name, desc, price, qty);
        MenuPrinter.success("Product created! ID = " + p.getProductId());
        MenuPrinter.pause();
    }

    private void updateProduct() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("UPDATE PRODUCT");
        MenuPrinter.breadcrumb("Main Menu > Manage Marketplace > Update Product");

        MenuPrinter.prompt("Product ID");
        int id = readInt();
        Product existing = pm.findById(id);
        if (existing == null) {
            MenuPrinter.error("Product not found!");
            MenuPrinter.pause();
            return;
        }

        System.out.println("\nLeave blank to keep current value.");
        MenuPrinter.prompt("New name (" + existing.getProductName() + ")");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = null;

        MenuPrinter.prompt("New description (" + existing.getDescription() + ")");
        String desc = scanner.nextLine().trim();
        if (desc.isEmpty()) desc = null;

        MenuPrinter.prompt("New price (" + existing.getPrice() + ")");
        String priceStr = scanner.nextLine().trim();
        Double price = priceStr.isEmpty() ? null : Double.valueOf(priceStr);

        MenuPrinter.prompt("New quantity (" + existing.getQuantity() + ")");
        String qtyStr = scanner.nextLine().trim();
        Integer qty = qtyStr.isEmpty() ? null : Integer.valueOf(qtyStr);

        boolean ok = pm.update(id, name, desc, price, qty, null);
        MenuPrinter.info(ok ? "Product updated successfully!" : "Failed to update product.");
        MenuPrinter.pause();
    }

    private void deleteProduct() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("DELETE PRODUCT");
        MenuPrinter.breadcrumb("Main Menu > Manage Marketplace > Delete Product");

        MenuPrinter.prompt("Product ID to delete");
        int id = readInt();
        Product p = pm.findById(id);
        if (p == null) {
            MenuPrinter.error("Product not found!");
            MenuPrinter.pause();
            return;
        }

        System.out.println(p.displayString());
        MenuPrinter.prompt("Are you sure? (y/n)");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            MenuPrinter.info("Cancelled.");
            MenuPrinter.pause();
            return;
        }
        boolean removed = pm.delete(id);
        MenuPrinter.info(removed ? "Product deleted successfully!" : "Failed to delete product.");
        MenuPrinter.pause();
    }

    /* =========================================================
                      4. VIEW ALL TRANSACTIONS
    ========================================================= */
    private void viewAllTransactions() {
        while (true) {
            Refresh.refreshTerminal();
            MenuPrinter.printHeader("TRANSACTIONS");
            MenuPrinter.breadcrumb("Main Menu > Transactions");
            MenuPrinter.printOption("1", "View All Transactions");
            MenuPrinter.printOption("2", "Filter by Type");
            MenuPrinter.printOption("3", "Filter by User");
            MenuPrinter.printOption("4", "View Transaction Details");
            MenuPrinter.printOption("0", "<  Back");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllTransactionsList();
                case "2" -> filterByType();
                case "3" -> filterByUser();
                case "4" -> viewTransactionDetails();
                case "0" -> { return; }
                default  -> MenuPrinter.error("Invalid option.");
            }
        }
    }

    private void viewAllTransactionsList() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("ALL TRANSACTIONS");
        MenuPrinter.breadcrumb("Main Menu > Transactions > View All");

        List<Transaction> txs = tm.findAll();
        if (txs.isEmpty()) {
            MenuPrinter.info("No transactions found.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(txs,
                new String[]{"ID", "Type", "From", "To", "Amount", "Date"},
                new int[]{6, 10, 20, 20, 12, 20},
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

    private void filterByType() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("FILTER BY TYPE");
        MenuPrinter.breadcrumb("Main Menu > Transactions > Filter by Type");

        MenuPrinter.printOption("1", "Deposits");
        MenuPrinter.printOption("2", "Withdrawals");
        MenuPrinter.printOption("3", "Transfers");
        MenuPrinter.printOption("4", "Purchases");
        MenuPrinter.printOption("5", "Salaries");
        MenuPrinter.prompt("Select type");

        String type = switch (scanner.nextLine().trim()) {
            case "1" -> "DEPOSIT";
            case "2" -> "WITHDRAW";
            case "3" -> "TRANSFER";
            case "4" -> "PURCHASE";
            case "5" -> "SALARY";
            default -> null;
        };

        if (type == null) {
            MenuPrinter.error("Invalid selection.");
            MenuPrinter.pause();
            return;
        }

        List<Transaction> filtered = tm.findByType(type);
        if (filtered.isEmpty()) {
            MenuPrinter.info("No " + type + " transactions found.");
            MenuPrinter.pause();
            return;
        }

        AsciiTable.print(filtered,
                new String[]{"ID", "From", "To", "Amount", "Description", "Date"},
                new int[]{6, 18, 18, 10, 24, 18},
                t -> new String[]{
                        String.valueOf(t.getTransactionId()),
                        t.getFromUsername(),
                        t.getToUsername(),
                        String.format("$%.2f", t.getAmount()),
                        t.getDescription(),
                        t.getTimestamp()
                });
        MenuPrinter.pause();
    }

    private void filterByUser() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("FILTER BY USER");
        MenuPrinter.breadcrumb("Main Menu > Transactions > Filter by User");

        MenuPrinter.prompt("Enter User ID");
        int userId = readInt();

        User user = userManager.findById(userId);
        if (user == null) {
            MenuPrinter.error("User not found.");
            MenuPrinter.pause();
            return;
        }

        List<Transaction> txs = tm.findByUserId(userId);
        if (txs.isEmpty()) {
            MenuPrinter.info("No transactions found for " + user.getFullName());
            MenuPrinter.pause();
            return;
        }

        System.out.println("Transactions for: " + user.getFullName() + " (ID: " + userId + ")");
        System.out.println();

        AsciiTable.print(txs,
                new String[]{"ID", "Type", "From", "To", "Amount", "Date"},
                new int[]{6, 10, 18, 18, 10, 18},
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

    private void viewTransactionDetails() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("TRANSACTION DETAILS");
        MenuPrinter.breadcrumb("Main Menu > Transactions > Details");

        MenuPrinter.prompt("Transaction ID");
        int id = readInt();

        Transaction tx = tm.findById(id);
        if (tx == null) {
            MenuPrinter.error("Transaction not found.");
            MenuPrinter.pause();
            return;
        }

        System.out.println(tx.displayString());
        MenuPrinter.pause();
    }

    /* =========================================================
                      5. MANAGE REPORTS
    ========================================================= */
    private void manageReports() {
        while (true) {
            Refresh.refreshTerminal();
            MenuPrinter.printHeader("MANAGE REPORTS");
            MenuPrinter.breadcrumb("Main Menu > Manage Reports");
            MenuPrinter.printOption("1", "View All Reports");
            MenuPrinter.printOption("2", "View Reports by Status");
            MenuPrinter.printOption("3", "Update Report Status");
            MenuPrinter.printOption("4", "Delete Report");
            MenuPrinter.printOption("0", "<  Back");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllReports();
                case "2" -> viewReportsByStatus();
                case "3" -> updateReport();
                case "4" -> deleteReport();
                case "0" -> { return; }
                default  -> MenuPrinter.error("Invalid option.");
            }
        }
    }

   /* ---------- View All Reports ---------- */
private void viewAllReports() {
    Refresh.refreshTerminal();
    MenuPrinter.printHeader("ALL REPORTS");
    MenuPrinter.breadcrumb("Main Menu > Manage Reports > View All Reports");

    List<Report> reports = rm.findAll();
    if (reports.isEmpty()) {
        MenuPrinter.info("No reports found.");
        MenuPrinter.pause();
        return;
    }

    int[] widths = fitReportColumns(reports, false);
    int totalWidth = Arrays.stream(widths).sum() + (widths.length - 1); // + separators

    System.out.printf("Total reports: %d%n", reports.size());
    System.out.println("┌" + "─".repeat(totalWidth) + "┐");

    /* header */
    String[] heads = {"ID", "Reporter", "Reported", "Status", "Reason"};
    StringBuilder hb = new StringBuilder("│");
    for (int i = 0; i < heads.length; i++) hb.append(center(heads[i], widths[i])).append("│");
    System.out.println(hb);

    System.out.println("├" + "─".repeat(totalWidth) + "┤");

    /* data rows (word-wrapped) */
    for (Report r : reports) {
        List<String> idLines   = List.of(String.valueOf(r.getReportId()));
        List<String> repLines  = wordWrap(r.getReporterName(), widths[1]);
        List<String> repULines = wordWrap(r.getReportedUsername(), widths[2]);
        List<String> stLines   = wordWrap(r.getStatus(), widths[3]);
        List<String> reLines   = wordWrap(r.getReason(), widths[4]);

        int rows = Math.max(Math.max(Math.max(idLines.size(), repLines.size()),
                                 Math.max(repULines.size(), stLines.size())), reLines.size());

        for (int line = 0; line < rows; line++) {
            StringBuilder rb = new StringBuilder("│");
            rb.append(pad(getLine(idLines, line), widths[0])).append("│");
            rb.append(pad(getLine(repLines, line), widths[1])).append("│");
            rb.append(pad(getLine(repULines, line), widths[2])).append("│");
            rb.append(pad(getLine(stLines, line), widths[3])).append("│");
            rb.append(pad(getLine(reLines, line), widths[4])).append("│");
            System.out.println(rb);
        }
    }

    System.out.println("└" + "─".repeat(totalWidth) + "┘");
    MenuPrinter.pause();
}

/* ---------- Filter By Status ---------- */
private void viewReportsByStatus() {
    Refresh.refreshTerminal();
    MenuPrinter.printHeader("FILTER REPORTS BY STATUS");
    MenuPrinter.breadcrumb("Main Menu > Manage Reports > Filter by Status");
    MenuPrinter.printOption("1", "Pending");
    MenuPrinter.printOption("2", "Reviewed");
    MenuPrinter.printOption("3", "Resolved");
    MenuPrinter.printOption("4", "Dismissed");
    MenuPrinter.prompt("Select status");

    String status = switch (scanner.nextLine().trim()) {
        case "1" -> Report.STATUS_PENDING;
        case "2" -> Report.STATUS_REVIEWED;
        case "3" -> Report.STATUS_RESOLVED;
        case "4" -> Report.STATUS_DISMISSED;
        default -> null;
    };

    if (status == null) {
        MenuPrinter.error("Invalid selection.");
        MenuPrinter.pause();
        return;
    }

    List<Report> filtered = rm.findByStatus(status);
    if (filtered.isEmpty()) {
        MenuPrinter.info("No " + status + " reports found.");
        MenuPrinter.pause();
        return;
    }

    int[] widths = fitReportColumns(filtered, true);
    int totalWidth = Arrays.stream(widths).sum() + (widths.length - 1);

    System.out.printf("Total %s reports: %d%n", status, filtered.size());
    System.out.println("┌" + "─".repeat(totalWidth) + "┐");

    String[] heads = {"ID", "Reporter", "Reported", "Reason"};
    StringBuilder hb = new StringBuilder("│");
    for (int i = 0; i < heads.length; i++) hb.append(center(heads[i], widths[i])).append("│");
    System.out.println(hb);

    System.out.println("├" + "─".repeat(totalWidth) + "┤");

    for (Report r : filtered) {
        List<String> idLines   = List.of(String.valueOf(r.getReportId()));
        List<String> repLines  = wordWrap(r.getReporterName(), widths[1]);
        List<String> repULines = wordWrap(r.getReportedUsername(), widths[2]);
        List<String> reLines   = wordWrap(r.getReason(), widths[3]);

        int rows = Math.max(Math.max(idLines.size(), repLines.size()),
                         Math.max(repULines.size(), reLines.size()));

        for (int line = 0; line < rows; line++) {
            StringBuilder rb = new StringBuilder("│");
            rb.append(pad(getLine(idLines, line), widths[0])).append("│");
            rb.append(pad(getLine(repLines, line), widths[1])).append("│");
            rb.append(pad(getLine(repULines, line), widths[2])).append("│");
            rb.append(pad(getLine(reLines, line), widths[3])).append("│");
            System.out.println(rb);
        }
    }

    System.out.println("└" + "─".repeat(totalWidth) + "┘");
    MenuPrinter.pause();
}
    private void updateReport() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("UPDATE REPORT STATUS");
        MenuPrinter.breadcrumb("Main Menu > Manage Reports > Update Report Status");

        MenuPrinter.prompt("Report ID");
        int id = readInt();
        Report r = rm.findById(id);
        if (r == null) {
            MenuPrinter.error("Report not found!");
            MenuPrinter.pause();
            return;
        }

        System.out.println("Current status: " + r.getStatus());
        MenuPrinter.printOption("1", "Pending");
        MenuPrinter.printOption("2", "Reviewed");
        MenuPrinter.printOption("3", "Resolved");
        MenuPrinter.printOption("4", "Dismissed");
        MenuPrinter.prompt("Choose new status");
        String choice = scanner.nextLine().trim();
        String newStatus = switch (choice) {
            case "1" -> Report.STATUS_PENDING;
            case "2" -> Report.STATUS_REVIEWED;
            case "3" -> Report.STATUS_RESOLVED;
            case "4" -> Report.STATUS_DISMISSED;
            default  -> null;
        };
        if (newStatus == null || !Report.isValidStatus(newStatus)) {
            MenuPrinter.error("Invalid choice.");
            MenuPrinter.pause();
            return;
        }

        boolean ok = rm.updateStatus(id, newStatus);
        MenuPrinter.info(ok ? "Report updated successfully!" : "Failed to update report.");
        MenuPrinter.pause();
    }

    private void deleteReport() {
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("DELETE REPORT");
        MenuPrinter.breadcrumb("Main Menu > Manage Reports > Delete Report");

        MenuPrinter.prompt("Report ID to delete");
        int id = readInt();
        Report r = rm.findById(id);
        if (r == null) {
            MenuPrinter.error("Report not found!");
            MenuPrinter.pause();
            return;
        }

        System.out.println(r.displayString());
        MenuPrinter.prompt("Are you sure? (y/n)");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            MenuPrinter.info("Cancelled.");
            MenuPrinter.pause();
            return;
        }
        boolean removed = rm.delete(id);
        MenuPrinter.info(removed ? "Report deleted successfully!" : "Failed to delete report.");
        MenuPrinter.pause();
    }

    /* =========================================================
                           MISC
    ========================================================= */
    private boolean confirmLogout() {
        while (true) {
            MenuPrinter.prompt("Are you sure you would like to logout? (y/n)");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                MenuPrinter.info("Logging out...");
                return true;
            } else if (input.equals("n")) {
                MenuPrinter.info("Returning to menu...");
                return false;
            } else {
                MenuPrinter.error("Please enter 'y' or 'n'.");
            }
        }
    }

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

 /* return int[] {idW, reporterW, reportedW, statusW, reasonW}  (no total slot) */
private int[] fitReportColumns(List<Report> list, boolean skipStatus) {
    int idW = 4;
    int reporterW = 8;      // "Reporter"
    int reportedW = 8;      // "Reported"
    int statusW = skipStatus ? 0 : Math.max(6, Report.STATUS_PENDING.length());
    int reasonW = 6;        // "Reason"

    /* real max widths */
    for (Report r : list) {
        reporterW = Math.max(reporterW, r.getReporterName().length());
        reportedW = Math.max(reportedW, r.getReportedUsername().length());
        if (!skipStatus) statusW = Math.max(statusW, r.getStatus().length());
        reasonW = Math.max(reasonW, r.getReason().length());
    }

    /* cap so total <= 80 (including | separators) */
    int total = idW + 1 + reporterW + 1 + reportedW + 1 + (skipStatus ? 0 : statusW + 1) + reasonW;
    if (total > 80) {
        int excess = total - 80;
        /* shave first from reason, then usernames */
        int shave = Math.min(excess, reasonW - 10);
        reasonW -= shave;
        excess -= shave;
        if (excess > 0) {
            shave = Math.min(excess, reporterW - 8);
            reporterW -= shave;
            excess -= shave;
        }
        if (excess > 0) {
            shave = Math.min(excess, reportedW - 8);
            reportedW -= shave;
            excess -= shave;
        }
        if (excess > 0 && !skipStatus) {
            shave = Math.min(excess, statusW - 6);
            statusW -= shave;
        }
    }

    int cols = skipStatus ? 4 : 5;
    int[] w = new int[cols];   // **no total slot**
    w[0] = idW;
    w[1] = reporterW;
    w[2] = reportedW;
    if (!skipStatus) {
        w[3] = statusW;
        w[4] = reasonW;
    } else {
        w[3] = reasonW;
    }
    return w;
}

/* ---------- word-wrap that keeps whole words ---- */
private List<String> wordWrap(String text, int width) {
    if (text == null || text.isEmpty()) return List.of("");
    List<String> lines = new ArrayList<>();
    int start = 0, end;
    while (start < text.length()) {
        end = Math.min(start + width, text.length());
        if (end < text.length() && text.charAt(end) != ' ') {
            while (end > start && text.charAt(end - 1) != ' ') end--;
            if (end == start) end = start + width; // force break long word
        }
        lines.add(text.substring(start, end).trim());
        start = end;
        while (start < text.length() && text.charAt(start) == ' ') start++;
    }
    return lines;
}

private String getLine(List<String> lines, int idx) {
    return idx < lines.size() ? lines.get(idx) : "";
}

private String pad(String s, int width) {
    if (s.length() > width) return s.substring(0, width);
    return s + " ".repeat(width - s.length());
}

private String center(String s, int width) {
    int pad = (width - s.length()) / 2;
    return " ".repeat(pad) + s + " ".repeat(width - s.length() - pad);
}
}

