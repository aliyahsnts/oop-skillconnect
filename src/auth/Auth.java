package auth;

import java.util.Scanner;
// managers
import managers.*;
// models
import models.*;
//ui 
import ui.RecruiterMenu;
import ui.JobseekerMenu;
import ui.AdminMenu;
//utils
import utils.Refresh;

// Auth class - handles login and registration logic

public class Auth {
    // static to share across all Auth methods
    private static Scanner sc = new Scanner(System.in);
    private static UserManager userManager;
    private static JobPostingManager jobPostingManager;
    private static ApplicationManager applicationManager;
    private static ProductManager productManager;
    private static TransactionManager transactionManager;
    private static ReportManager reportManager;

    // Initialize Auth with all managers instance
    public static void init(UserManager um, JobPostingManager jpm, ApplicationManager am, ProductManager pm, TransactionManager tm, ReportManager rm) {
        userManager = um;
        jobPostingManager = jpm;
        applicationManager = am;
        productManager = pm;
        transactionManager = tm;
        reportManager = rm;
    }

    // =========================================
    //             LOGIN FUNCTION
    // =========================================
    public static void login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        // Check if user exists
        if (!userManager.usernameExists(username)) {
            System.out.println("ERROR: Username does not exist. Please create an account first!");
            Refresh.refreshTerminal(); 
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        // Retrieve user object
        User user = userManager.findUser(username);

        // Validate password
        if (!user.getPassword().equals(password)) {
            System.out.println("ERROR: Password incorrect. Please try again.");
            Refresh.refreshTerminal(); 
            return;
        }

        System.out.println("SUCCESS: Login successful!");
        Refresh.refreshTerminal(); 

        // Display user interface based on user type
        switch (user.getUserType()) {
            case 1 -> {
                System.out.println("Displaying Jobseeker Interface...");
                Refresh.refreshTerminal(); 
                Jobseeker js = (Jobseeker) user;
                JobseekerMenu menu = new JobseekerMenu(js, jobPostingManager,
                applicationManager,
                productManager,
                transactionManager,
                reportManager);
                menu.show();
            }
            case 2 -> {
                System.out.println("Displaying Recruiter Interface...");
                Refresh.refreshTerminal();
                Recruiter rec = (Recruiter) user;
                RecruiterMenu menu = new RecruiterMenu(rec, jobPostingManager,
                applicationManager,
                productManager,
                transactionManager,
                reportManager);
                menu.show();
            }
            case 3 -> {
                System.out.println("Displaying Admin Interface...");
                Refresh.refreshTerminal();
                Admin admin = (Admin) user;
                AdminMenu menu = new AdminMenu(admin, userManager, jobPostingManager, applicationManager, productManager, transactionManager, reportManager); 
                menu.show(); 
            }
            default -> System.out.println("Unknown user type.");
        }
    }

    // =========================================
    //           REGISTER FUNCTION
    // =========================================

    public static void register() {
        System.out.print("Enter full name: ");
        String fullName = sc.nextLine();

        System.out.print("Enter username: ");
        String username = sc.nextLine();

        // Check if username already exists
        if (userManager.usernameExists(username)) {
            System.out.println("ERROR: Account with username '" + username + "' already exists! Please proceed to login.");
            Refresh.refreshTerminal(); 
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.print("Select user type (1=Jobseeker, 2=Recruiter): ");
        String uType = sc.nextLine();
        int userType;

        // Validate user type input
        if (uType.equals("1")) userType = 1;
        else if (uType.equals("2")) userType = 2;
        else {
            System.out.println("ERROR: Invalid user type. Please only enter either '1' for Jobseeker, or '2' for Recruiter.");
            Refresh.refreshTerminal(); 
            return;
        }

        // Create new user based on type
        User newUser;
        int newId = userManager.nextId();
        if (userType == 1) {
            newUser = new Jobseeker(newId, fullName, username, password, 0.0);
        } else if (userType == 2) {
            newUser = new Recruiter(newId, fullName, username, password, 0.0);
        } else {
            System.out.println("ERROR: Invalid user type.");
                return;
        }

        userManager.addUser(newUser);
        System.out.println("SUCCESS: Account created successfully!");
        Refresh.refreshTerminal(); 
    }
    
}
