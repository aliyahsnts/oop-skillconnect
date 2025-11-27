package ui;

import java.util.List;
import java.util.Scanner;

import models.Admin;
import models.User;

import managers.UserManager;
import managers.JobPostingManager;
import managers.ApplicationManager;

public class AdminMenu {

    private Admin admin;
    private UserManager userManager;
    private JobPostingManager jpm;
    private ApplicationManager am;

    private Scanner scanner = new Scanner(System.in);

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
            System.out.println("0. Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> manageUsers();
                case "2" -> manageJobs();
                case "3" -> manageMarketplace();
                case "4" -> viewAllTransactions();
                case "0" -> { if (confirmLogout()) return; }

                default -> System.out.println("Invalid option.");
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

        String c = scanner.nextLine();

        switch (c) {
            case "1" -> viewAllAccounts();
            case "2" -> createAccount();
            case "3" -> updateAccount();
            case "4" -> deleteAccount();
        }
    }

    private void manageJobs() {
        System.out.println("\n=== Manage Jobs ===");
        System.out.println("1. View All Jobs");
        System.out.println("2. Update Job");
        System.out.println("3. Delete Job");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");

        scanner.nextLine(); // placeholder
        System.out.println(">>> Feature under development.");
    }

    private void manageMarketplace() {
        System.out.println("\n=== Manage Marketplace ===");
        System.out.println("1. View All Products");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");

        scanner.nextLine(); // placeholder
        System.out.println(">>> Feature under development.");
    }

    private void viewAllAccounts() {
        List<User> users = userManager.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\nAll Users:");
        for (User u : users) {
            System.out.println("ID: " + u.getId() +
                               " | Name: " + u.getFullName() +
                               " | Username: " + u.getUsername() +
                               " | Type: " + u.getUserType());
        }
    }

    private void createAccount() {
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        if (userManager.usernameExists(username)) {
            System.out.println("ERROR: Username already exists!");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter user type (1=Jobseeker, 2=Recruiter, 3=Admin): ");
        int type = Integer.parseInt(scanner.nextLine());

        User newUser = userManager.createUser(fullName, username, password, type);

        if (newUser != null)
            System.out.println("SUCCESS: Account created!");
        else
            System.out.println("ERROR: Invalid user type.");
    }

    private void updateAccount() {
        System.out.print("Enter username to update: ");
        String username = scanner.nextLine();

        User user = userManager.findUser(username);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter new full name (blank to keep): ");
        String fullName = scanner.nextLine();

        System.out.print("Enter new password (blank to keep): ");
        String password = scanner.nextLine();

        if (!fullName.isBlank()) user.setFullName(fullName);
        if (!password.isBlank()) user.setPassword(password);

        userManager.saveUsers();
        System.out.println("SUCCESS: Account updated.");
    }

    private void deleteAccount() {
        System.out.print("Enter username to delete: ");
        String username = scanner.nextLine();

        if (!userManager.deleteUser(username)) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("SUCCESS: User deleted.");
    }

    private void viewAllTransactions() {
        System.out.println(">>> Feature under development.");
    }

    private boolean confirmLogout() {
        System.out.print("Logout? (y/n): ");
        String ans = scanner.nextLine();
        return ans.equalsIgnoreCase("y");
    }
}
