package ui;

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
    private final ReportManager rm;
    private final Scanner scanner = new Scanner(System.in);

    public AdminMenu(Admin admin, UserManager um, JobPostingManager jpm, ApplicationManager am,
                     ProductManager pm, TransactionManager tm, ReportManager rm) {
        this.admin = admin;
        this.userManager = um;
        this.jpm = jpm;
        this.pm = pm;
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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

    /* --------------------------------------------------------- */
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
        Refresh.refreshTerminal();
        MenuPrinter.printHeader("ALL TRANSACTIONS");
        MenuPrinter.breadcrumb("Main Menu > View All Transactions");
        MenuPrinter.info("Feature not fully implemented yet.");
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
            MenuPrinter.printOption("2", "Update Report Status");
            MenuPrinter.printOption("3", "Delete Report");
            MenuPrinter.printOption("0", "<  Back");
            MenuPrinter.prompt("Enter choice");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllReports();
                case "2" -> updateReport();
                case "3" -> deleteReport();
                case "0" -> { return; }
                default  -> MenuPrinter.error("Invalid option.");
            }
        }
    }

    /* --------------------------------------------------------- */
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

        AsciiTable.print(reports,
                new String[]{"ID", "Reporter", "Reported User", "Status"},
                new int[]{4, 16, 16, 12},
                r -> new String[]{
                        String.valueOf(r.getReportId()),
                        r.getReporterName(),
                        r.getReportedUsername(),
                        r.getStatus()
                });
        MenuPrinter.pause();
    }

    /* --------------------------------------------------------- */
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
            case "1" -> "Pending";
            case "2" -> "Reviewed";
            case "3" -> "Resolved";
            case "4" -> "Dismissed";
            default  -> null;
        };
        if (newStatus == null) {
            MenuPrinter.error("Invalid choice.");
            MenuPrinter.pause();
            return;
        }

        boolean ok = rm.updateStatus(id, newStatus);
        MenuPrinter.info(ok ? "Report updated successfully!" : "Failed to update report.");
        MenuPrinter.pause();
    }

    /* --------------------------------------------------------- */
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
}