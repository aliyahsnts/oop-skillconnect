import java.util.Scanner;
import managers.UserManager;
import managers.JobPostingManager;
import managers.ApplicationManager;
import utils.Refresh;
import auth.Auth;

// Main class handles program flow and menu
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager userManager = new UserManager();
        JobPostingManager jobPostingManager = new JobPostingManager("data/jobs.csv");
        ApplicationManager applicationManager = new ApplicationManager("data/jobs.csv");

        // Initialize Auth with UserManager, JobPostingManager, ApplicationManager
        Auth.init(userManager, jobPostingManager, applicationManager);

        while (true) {
            Refresh.refreshTerminal(); 
            // Display Login/Register Menu
            System.out.println("=== Login/Register Interface ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            String choice = sc.nextLine();

            // Handle menu choices
            switch (choice) {
                case "1" -> Auth.login();
                case "2" -> Auth.register();
                case "0" -> {
                    System.out.println("Exiting program...");
                    Refresh.refreshTerminal(); 
                    System.exit(0);
                }
                default -> System.out.println("ERROR: Invalid option. Please enter either ‘1’ for Login, ‘2’ for Register, or ‘0’ to Exit.");
            }
        }
    }
}
