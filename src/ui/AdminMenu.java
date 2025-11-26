package ui;

import java.util.List;
// import java.util.List;
import java.util.Scanner;
import models.Admin;
import models.User;
// import models.User;
// import models.JobPosting;
import managers.UserManager;
import managers.JobPostingManager;
import managers.ApplicationManager;

public class AdminMenu {
    private Admin admin;
    private UserManager userManager;
    private JobPostingManager jpm;
    private ApplicationManager am;
    private Scanner scanner = new Scanner(System.in);

    // Constructor
    public AdminMenu(Admin admin, UserManager um, JobPostingManager jpm, ApplicationManager am) {
        this.admin = admin;
        this.userManager = um;
        this.jpm = jpm;
        this.am = am;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("Welcome, Admin " + admin.getFullName() + "!");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Jobs");
            System.out.println("3. Manage Marketplace");
            System.out.println("4. View All Transactions");
            System.out.println("5. View All Reports");
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> manageUsers();
                case "2" -> manageJobs();
                case "3" -> manageMarketplace();
                case "4" -> viewAllTransactions();
                case "0" -> {
                    if (confirmLogout()) return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

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
            case "0" -> {}
            default -> System.out.println("Invalid option.");
        }
    }

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

    private void manageMarketplace() {
        System.out.println("\n=== Manage Marketplace ===");
        System.out.println("1. View All Products");
        System.out.println("2. Update product");
        System.out.println("3. Delete product");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
    }

    private void viewAllAccounts() {
        List<User> users = userManager.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        System.out.println("\nAll Users:");
        for (User u : users) {
            System.out.println("----------------------------");
            System.out.println("ID: " + u.getId() + ", Name: " + u.getFullName() + ", Username: " + u.getUsername() + ", Type: " + u.getUserType());
        }
        System.out.println("----------------------------");
    }

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

        User newUser = userManager.createUser(fullName, username, password, type);
        if (newUser != null) {
            System.out.println("SUCCESS: Account created! ID=" + newUser.getId());
        } else {
            System.out.println("ERROR: Invalid user type.");
        }
    }

    private void updateAccount() {
        System.out.print("Enter username to update: ");
        String username = scanner.nextLine().trim();
        User user = userManager.findUser(username);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter new full name (leave blank to keep current): ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Enter new password (leave blank to keep current): ");
        String password = scanner.nextLine().trim();

        if (!fullName.isEmpty()) user.setFullName(fullName);
        if (!password.isEmpty()) user.setPassword(password);

        userManager.saveUsers();
        System.out.println("SUCCESS: Account updated!");
    }

    private void deleteAccount() {
        System.out.print("Enter username to update: ");
        String username = scanner.nextLine().trim();
        User user = userManager.findUser(username);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter new full name (leave blank to keep current): ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Enter new password (leave blank to keep current): ");
        String password = scanner.nextLine().trim();

        if (!fullName.isEmpty()) user.setFullName(fullName);
        if (!password.isEmpty()) user.setPassword(password);

        userManager.saveUsers();
        System.out.println("SUCCESS: Account updated!");
    }

    private void viewAllJobs() {
        System.out.println(">>> View All Jobs (Work in Progress)");
    }

    private void updateJob() {
        System.out.println(">>> Update Job (Work in Progress)");
    }

    private void deleteJob() {
        System.out.println(">>> Delete Job (Work in Progress)");
    }

    private void viewAllTransactions() {
        System.out.println(">>> View All Transactions (Work in Progress)");
    }

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
}