package auth;

import java.util.Scanner;
// managers
import managers.ApplicationManager;
import managers.UserManager;
import managers.JobPostingManager;
// models
import models.Admin;
import models.Jobseeker;
import models.User;
import models.Recruiter;
// ui 
import ui.RecruiterMenu;
import ui.JobseekerMenu;
import ui.AdminMenu;
// utils
import utils.Refresh;

public class Auth {

    private static Scanner sc = new Scanner(System.in);
    private static UserManager userManager;
    private static JobPostingManager jobPostingManager;
    private static ApplicationManager applicationManager;

    // Initialize dependencies
    public static void init(UserManager um, JobPostingManager jpm, ApplicationManager am) {
        userManager = um;
        jobPostingManager = jpm;
        applicationManager = am;
    }

    // LOGIN returns User (so Main can route correctly)
    public static User login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();

        if (!userManager.usernameExists(username)) {
            System.out.println("ERROR: Username does not exist.");
            Refresh.refreshTerminal();
            return null;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = userManager.findUser(username);

        if (!user.getPassword().equals(password)) {
            System.out.println("ERROR: Incorrect password.");
            Refresh.refreshTerminal();
            return null;
        }

        System.out.println("SUCCESS: Login Successful!");
        Refresh.refreshTerminal();

        return user;
    }

    // REGISTER
    public static void register() {
        System.out.print("Enter full name: ");
        String fullName = sc.nextLine();

        System.out.print("Enter username: ");
        String username = sc.nextLine();

        if (userManager.usernameExists(username)) {
            System.out.println("ERROR: Username already exists!");
            Refresh.refreshTerminal();
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        System.out.print("Select user type (1=Jobseeker, 2=Recruiter, 3=Admin): ");
        String input = sc.nextLine();

        int userType;
        if (input.equals("1")) userType = 1;
        else if (input.equals("2")) userType = 2;
        else if (input.equals("3")) userType = 3;
        else {
            System.out.println("ERROR: Invalid user type.");
            Refresh.refreshTerminal();
            return;
        }

        int id = userManager.nextId();
        User newUser;

        switch (userType) {
            case 1 -> newUser = new Jobseeker(id, fullName, username, password);
            case 2 -> newUser = new Recruiter(id, fullName, username, password);
            case 3 -> newUser = new Admin(id, fullName, username, password);
            default -> {
                System.out.println("ERROR: Invalid user type.");
                return;
            }
        }

        userManager.addUser(newUser);
        System.out.println("SUCCESS: Account created!");
        Refresh.refreshTerminal();
    }
}
