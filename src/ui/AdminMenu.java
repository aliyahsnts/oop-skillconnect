package ui;

import java.util.List;
import java.util.Scanner;
import models.*;
import utils.Refresh;
import managers.*;

public class AdminMenu {
    private Admin admin;
    private UserManager userManager;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private ProductManager pm;
    private TransactionManager tm;
    private ReportManager rm;
    private Scanner scanner = new Scanner(System.in);

    // Constructor
    public AdminMenu(Admin admin, UserManager um, JobPostingManager jpm, ApplicationManager am,
                    ProductManager pm, TransactionManager tm, ReportManager rm) {
        this.admin = admin;
        this.userManager = um;
        this.jpm = jpm;
        this.am = am;
        this.pm = pm;
        this.tm = tm;
        this.rm = rm;
    }
    // show - display menu
    public void show() {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("Welcome, Admin " + admin.getFullName() + "!");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Jobs");
            System.out.println("3. Manage Marketplace");
            System.out.println("4. View All Transactions");
            System.out.println("5. Manage Reports");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> manageUsers();
                case "2" -> manageJobs();
                case "3" -> manageMarketplace();
                case "4" -> viewAllTransactions();
                case "5" -> manageReports();
                case "0" -> {
                    if (confirmLogout()) return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // ========================================================================
    //   USER FUNCTIONS
    // ========================================================================

    //manage users - manage users menu
    private void manageUsers() {
        System.out.println("\n=== Manage Users ===");
        System.out.println("1. View All Users");
        System.out.println("2. Create Account");
        System.out.println("3. Update Account");
        System.out.println("4. Delete Account");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> viewAllAccounts();
            case "2" -> createAccount();
            case "3" -> updateAccount();
            case "4" -> deleteAccount();
            case "0" -> { return; }
            default -> System.out.println("Invalid option.");
        }
    }

    //view all accounts - view all users
    private void viewAllAccounts() {
        List<User> users = userManager.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        System.out.println("\n=== All Users ===");
        for (User u : users) {
            String userTypeStr = switch (u.getUserType()) {
                case 1 -> "Jobseeker";
                case 2 -> "Recruiter";
                case 3 -> "Admin";
                default -> "Unknown";
            };
            System.out.println("----------------------------");
            System.out.println("ID: " + u.getId());
            System.out.println("Name: " + u.getFullName());
            System.out.println("Username: " + u.getUsername());
            System.out.println("Type: " + userTypeStr);
            System.out.println("Money: $" + u.getMoney());
        }
        System.out.println("----------------------------");
    }

    // create account
    private void createAccount() {
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine().trim();
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        if (userManager.usernameExists(username)) {
            System.out.println("ERROR: Username already exists!");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        System.out.print("Enter user type (1=Jobseeker, 2=Recruiter, 3=Admin): ");
        int type = readInt();

        if (type < 1 || type > 3) {
            System.out.println("ERROR: Invalid user type.");
            return;
        }

        int newId = userManager.nextId();
        User newUser;
        
        switch (type) {
            case 1 -> newUser = new Jobseeker(newId, fullName, username, password);
            case 2 -> newUser = new Recruiter(newId, fullName, username, password);
            case 3 -> newUser = new Admin(newId, fullName, username, password);
            default -> {
                System.out.println("ERROR: Invalid user type.");
                return;
            }
        }
        
        userManager.addUser(newUser);
        System.out.println("SUCCESS: Account created! ID=" + newUser.getId());
    }

    //update account
    private void updateAccount() {
        System.out.print("Enter username to update: ");
        String username = scanner.nextLine().trim();
        User user = userManager.findUser(username);
        
        if (user == null) {
            System.out.println("ERROR: User not found.");
            return;
        }

        System.out.println("Current user info:");
        System.out.println("Name: " + user.getFullName());
        System.out.println("Username: " + user.getUsername());

        System.out.print("Enter new full name (leave blank to keep current): ");
        String fullName = scanner.nextLine().trim();
        
        System.out.print("Enter new password (leave blank to keep current): ");
        String password = scanner.nextLine().trim();

        if (!fullName.isEmpty()) user.setFullName(fullName);
        if (!password.isEmpty()) user.setPassword(password);

        userManager.saveUsers();
        System.out.println("SUCCESS: Account updated!");
    }

    //delete account
    private void deleteAccount() {
        System.out.print("Enter username to delete: ");
        String username = scanner.nextLine().trim();
        User user = userManager.findUser(username);
        
        if (user == null) {
            System.out.println("ERROR: User not found.");
            return;
        }

        System.out.println("User info:");
        System.out.println("Name: " + user.getFullName());
        System.out.println("Username: " + user.getUsername());
        
        System.out.print("Are you sure you want to delete this account? (Y/N): ");
        String conf = scanner.nextLine().trim();
        
        if (conf.equalsIgnoreCase("Y")) {
            boolean removed = userManager.deleteUser(username);
            if (removed) {
                System.out.println("SUCCESS: Account deleted!");
            } else {
                System.out.println("ERROR: Could not delete account.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ========================================================================
    //   JOBS FUNCTIONS
    // ========================================================================
    private void manageJobs() {
        System.out.println("\n=== Manage Jobs ===");
        System.out.println("1. View All Jobs");
        System.out.println("2. Update Job");
        System.out.println("3. Delete Job");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> viewAllJobs();
            case "2" -> updateJob();
            case "3" -> deleteJob();
            case "0" -> {}
            default -> System.out.println("Invalid option.");
        }
    }

    private void viewAllJobs() {
        List<Object> list = jpm.findAll();
        if (list.isEmpty()) {
            System.out.println("No job postings found.");
            return;
        }
        
        System.out.println("\n=== All Job Postings ===");
        for (Object obj : list) {
            JobPosting job = (JobPosting) obj;
            System.out.println("---------------------------");
            System.out.println(job.displayString());
        }
        System.out.println("---------------------------");
    }

    private void updateJob() {
        System.out.print("Enter Job ID to update: ");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        
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

        boolean ok = jpm.update(jobId,
                name.isEmpty() ? null : name,
                desc.isEmpty() ? null : desc,
                hours.isEmpty() ? null : hours,
                payment,
                status.isEmpty() ? null : status);

        if (ok) System.out.println("SUCCESS: Job successfully updated!");
        else System.out.println("ERROR: Could not update job.");
    }

    private void deleteJob() {
        System.out.print("Enter Job ID to delete: ");
        int jobId = readInt();
        JobPosting job = jpm.findById(jobId);
        
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        
        System.out.println("Job info:");
        System.out.println(job.displayString());
        System.out.print("Are you sure you want to delete this job? (Y/N): ");
        String conf = scanner.nextLine().trim();
        
        if (conf.equalsIgnoreCase("Y")) {
            boolean removed = jpm.delete(jobId);
            if (removed) System.out.println("SUCCESS: Job successfully deleted!");
            else System.out.println("ERROR: Could not delete job.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ========================================================================
    //   MARKETPLACE FUNCTIONS
    // ========================================================================
    private void manageMarketplace() {
        System.out.println("\n=== Manage Marketplace ===");
        System.out.println("1. View All Products");
        System.out.println("2. Add product");
        System.out.println("3. Update product");
        System.out.println("4. Delete product");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> viewAllProducts();
            case "2" -> createProduct();
            case "3" -> updateProduct();
            case "4" -> deleteProduct();
            case "0" -> { return; }
            default -> System.out.println("Invalid option.");
        }
    }

    private void viewAllProducts() {
        List<Product> products = pm.findAll();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }
        
        System.out.println("\n=== All Products ===");
        for (Product p : products) {
            System.out.println("---------------------------");
            System.out.println(p.displayString());
        }
        System.out.println("---------------------------");
    }

    private void createProduct() {
        Refresh.refreshTerminal();
        System.out.println("=== CREATE PRODUCT ===");

        System.out.print("Enter product name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter description: ");
        String desc = scanner.nextLine().trim();
        
        System.out.print("Enter price: ");
        double price = readDouble();
        
        System.out.print("Enter quantity: ");
        int qty = readInt();
        
        Product p = pm.create(name, desc, price, qty);

        System.out.println("SUCCESS: Product created! (Product ID: " + p.getProductId() + ")");
    }

    private void updateProduct(){
        Refresh.refreshTerminal();
        System.out.println("=== UPDATE PRODUCT ===");

        System.out.print("Enter product ID: ");
        int id = readInt();
        Product existing = pm.findById(id);

        if (existing == null) {
            System.out.println("ERROR: Product not found!");
            pause();
            return;
        }

        System.out.println("\nLeave a field blank to keep the current value.");

        System.out.print("New name (" + existing.getProductName() + "): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) name = null;

        System.out.print("New description (" + existing.getDescription() + "): ");
        String desc = scanner.nextLine().trim();
        if (desc.isEmpty()) desc = null;

        System.out.print("New price (" + existing.getPrice() + "): ");
        String priceStr = scanner.nextLine().trim();
        Double price = priceStr.isEmpty() ? null : Double.parseDouble(priceStr);

        System.out.print("New quantity (" + existing.getQuantity() + "): ");
        String qtyStr = scanner.nextLine().trim();
        Integer qty = qtyStr.isEmpty() ? null : Integer.parseInt(qtyStr);

        boolean ok = pm.update(id, name, desc, price, qty, null);

        System.out.println(ok ? "Product updated successfully!" : "Failed to update product.");
        pause();
    }

    private void deleteProduct(){
        Refresh.refreshTerminal();
        System.out.println("=== UPDATE PRODUCT ===");

        System.out.print("Enter product ID to delete: ");
        int id = readInt();

        Product p = pm.findById(id);

        if (p == null) {
            System.out.println("ERROR: Product not found!");
            pause();
            return;
        }

        System.out.println(p.displayString());
        System.out.print("\nAre you sure you want to delete this product? (y/n): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        boolean removed = pm.delete(id);
        System.out.println(removed ? "Product deleted successfully!" : "Failed to delete product.");

        pause();
    }
    

    // ========================================================================
    //   TRANSACTIONS FUNCTIONS
    // ========================================================================
    private void viewAllTransactions() {
        System.out.println(">>> View All Transactions (Work in Progress)");
    }

    // ========================================================================
    //   REPORTS FUNCTIONS
    // ========================================================================
    private void manageReports() {
        System.out.println("\n=== Manage Reports ===");
        System.out.println("1. View All Reports");
        System.out.println("2. Update Report");
        System.out.println("3. Delete Report");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> viewAllReports();
            case "2" -> updateReport();
            case "3" -> deleteReport();
            case "0" -> { return; }
            default -> System.out.println("Invalid option.");
        }
    }

    private void viewAllReports() {
        Refresh.refreshTerminal();
        System.out.println("=== ALL REPORTS ===");

        List<Report> reports = rm.findAll();

        if (reports.isEmpty()) {
            System.out.println("No reports found.");
        } else {
            for (Report r : reports) {
                System.out.println("-------------------------");
                System.out.println(r.displayString());
            }
        }
        pause();
    }

    private void updateReport() {
        Refresh.refreshTerminal();
        System.out.println("=== UPDATE REPORT STATUS ===");

        System.out.print("Enter report ID: ");
        int id = readInt();

        Report r = rm.findById(id);

        if (r == null) {
            System.out.println("ERROR: Report not found!");
            pause();
            return;
        }

        System.out.println("\nCurrent status: " + r.getStatus());
        System.out.println("Choose new status:");
        System.out.println("1. Pending");
        System.out.println("2. Reviewed");
        System.out.println("3. Resolved");
        System.out.println("4. Dismissed");

        System.out.print("Enter choice: ");
        String choice = scanner.nextLine().trim();
        String newStatus = switch (choice) {
            case "1" -> "Pending";
            case "2" -> "Reviewed";
            case "3" -> "Resolved";
            case "4" -> "Dismissed";
            default -> null;
        };

        if (newStatus == null) {
            System.out.println("Invalid choice.");
            pause();
            return;
        }

        boolean ok = rm.updateStatus(id, newStatus);
        System.out.println(ok ? "Report updated successfully!" : "Failed to update report.");
        pause();
    }

    private void deleteReport() {
        Refresh.refreshTerminal();
        System.out.println("=== DELETE REPORT ===");

        System.out.print("Enter report ID to delete: ");
        int id = readInt();

        Report r = rm.findById(id);

        if (r == null) {
            System.out.println("ERROR: Report not found!");
            pause();
            return;
        }

        System.out.println(r.displayString());
        System.out.print("\nAre you sure you want to delete this report? (y/n): ");

        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        boolean removed = rm.delete(id);
        System.out.println(removed ? "Report deleted successfully!" : "Failed to delete report.");
        pause();
    }

    // ========================================================================
    //   OTHER FUNCTIONS
    // ========================================================================
    private boolean confirmLogout() {
        while (true) {
            System.out.print("Are you sure you would like to logout? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) {
                System.out.println("Logging out...");
                return true;
            } else if (input.equals("n")) {
                System.out.println("Returning to menu...");
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
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
            System.out.println("Invalid number. Try again.");
        }
    }
    
    }

    private void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}