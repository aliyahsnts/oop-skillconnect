package ui;

// import java.util.List;
import java.util.Scanner;
import models.Admin;
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
        // List<User> users = userManager.getAllUsers();
        // if (users.isEmpty()) {
        //     System.out.println("No users found.");
        //     return;
        // }

        // System.out.println("\n=== All Users ===");
        // for (User u : users) {
        //     System.out.println(u);
        // }
    }

    private void createAccount() {
        // System.out.print("Enter full name: ");
        // String fullName = scanner.nextLine();
        // System.out.print("Enter username: ");
        // String username = scanner.nextLine();

        // if (userManager.usernameExists(username)) {
        //     System.out.println("ERROR: Username already exists!");
        //     return;
        // }

        // System.out.print("Enter password: ");
        // String password = scanner.nextLine();
        // System.out.print("User type (1=Jobseeker, 2=Recruiter, 3=Admin): ");
        // int type = Integer.parseInt(scanner.nextLine());

        // User user = switch (type) {
        //     case 1 -> new models.Jobseeker(fullName, username, password);
        //     case 2 -> new models.Recruiter(fullName, username, password);
        //     case 3 -> new Admin(fullName, username, password);
        //     default -> null;
        // };

        // if (user != null) {
        //     userManager.addUser(user);
        //     System.out.println("SUCCESS: Account created!");
        // } else {
        //     System.out.println("ERROR: Invalid user type.");
        // }
    }

    private void updateAccount() {
        // System.out.print("Enter username to update: ");
        // String username = scanner.nextLine();

        // User user = userManager.findUser(username);
        // if (user == null) {
        //     System.out.println("User not found.");
        //     return;
        // }

        // System.out.print("Enter new full name (leave blank to keep current): ");
        // String fullName = scanner.nextLine();
        // System.out.print("Enter new password (leave blank to keep current): ");
        // String password = scanner.nextLine();

        // if (!fullName.isBlank()) user.setFullName(fullName);
        // if (!password.isBlank()) user.setPassword(password);

        // userManager.saveUsers(); // Make sure UserManager has a save method
        // System.out.println("SUCCESS: Account updated!");
    }

    private void deleteAccount() {
        // System.out.print("Enter username to delete: ");
        // String username = scanner.nextLine();

        // User user = userManager.findUser(username);
        // if (user == null) {
        //     System.out.println("User not found.");
        //     return;
        // }

        // System.out.print("Confirm delete? (y/n): ");
        // if (scanner.nextLine().equalsIgnoreCase("y")) {
        //     userManager.removeUser(user);
        //     System.out.println("SUCCESS: Account deleted!");
        // } else {
        //     System.out.println("Deletion cancelled.");
        // }
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