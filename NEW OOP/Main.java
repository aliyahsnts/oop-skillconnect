import java.util.Scanner;

import managers.UserManager;
import managers.JobPostingManager;
import managers.ApplicationManager;

import utils.Refresh;
import auth.Auth;

// models + menus
import models.User;
import models.Admin;
import models.Jobseeker;
import models.Recruiter;

import ui.AdminMenu;
import ui.JobseekerMenu;
import ui.RecruiterMenu;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        UserManager userManager = new UserManager("data/users.csv");
        JobPostingManager jobPostingManager = new JobPostingManager("data/jobs.csv");
        ApplicationManager applicationManager = new ApplicationManager("data/applications.csv");

        // initialize Auth
        Auth.init(userManager, jobPostingManager, applicationManager);

        while (true) {

            Refresh.refreshTerminal();
            
            // Centered ASCII Art Header - SKILL CONNECT with Box
            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                                                                                                                            ║");
            System.out.println("║  █████████  █████   ████ █████ █████       █████            █████████     ███████    ██████   █████ ██████   █████ ██████████   █████████  ███████████ ║");
            System.out.println("║   ███▒▒▒▒▒███▒▒███   ███▒ ▒▒███ ▒▒███       ▒▒███            ███▒▒▒▒▒███  ███▒▒▒▒▒███ ▒▒██████ ▒▒███ ▒▒██████ ▒▒███ ▒▒███▒▒▒▒▒█  ███▒▒▒▒▒███▒█▒▒▒███▒▒▒█ ║");
            System.out.println("║  ▒███    ▒▒▒  ▒███  ███    ▒███  ▒███        ▒███           ███     ▒▒▒  ███     ▒▒███ ▒███▒███ ▒███  ▒███▒███ ▒███  ▒███  █ ▒  ███     ▒▒▒ ▒   ▒███  ▒  ║");
            System.out.println("║  ▒▒█████████  ▒███████     ▒███  ▒███        ▒███          ▒███         ▒███      ▒███ ▒███▒▒███▒███  ▒███▒▒███▒███  ▒██████   ▒███             ▒███     ║");
            System.out.println("║   ▒▒▒▒▒▒▒▒███ ▒███▒▒███    ▒███  ▒███        ▒███          ▒███         ▒███      ▒███ ▒███ ▒▒██████  ▒███ ▒▒██████  ▒███▒▒█   ▒███             ▒███     ║");
            System.out.println("║   ███    ▒███ ▒███ ▒▒███   ▒███  ▒███      █ ▒███      █   ▒▒███     ███▒▒███     ███  ▒███  ▒▒█████  ▒███  ▒▒█████  ▒███ ▒   █▒▒███     ███    ▒███     ║");
            System.out.println("║  ▒▒█████████  █████ ▒▒████ █████ ███████████ ███████████    ▒▒█████████  ▒▒▒███████▒   █████  ▒▒█████ █████  ▒▒█████ ██████████ ▒▒█████████     █████    ║");
            System.out.println("║   ▒▒▒▒▒▒▒▒▒  ▒▒▒▒▒   ▒▒▒▒ ▒▒▒▒▒ ▒▒▒▒▒▒▒▒▒▒▒ ▒▒▒▒▒▒▒▒▒▒▒      ▒▒▒▒▒▒▒▒▒     ▒▒▒▒▒▒▒    ▒▒▒▒▒    ▒▒▒▒▒ ▒▒▒▒▒    ▒▒▒▒▒ ▒▒▒▒▒▒▒▒▒▒   ▒▒▒▒▒▒▒▒▒     ▒▒▒▒▒     ║");
            System.out.println("║                                                                                                                                            ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("                                    The #1 Freelancing System ");
            System.out.println();
            System.out.println();
            System.out.println("                                ┌─────────────────────────────────┐");
            System.out.println("                                │ === Login/Register Interface === │");
            System.out.println("                                ├─────────────────────────────────┤");
            System.out.println("                                │         1. Login                │");
            System.out.println("                                │         2. Register              │");
            System.out.println("                                │         0. Exit                 │");
            System.out.println("                                ├─────────────────────────────────┤");
            System.out.print("                                │    Enter choice: ");
            
            String choice = sc.nextLine();
            System.out.println("                                └─────────────────────────────────┘");

            switch (choice) {

                case "1" -> {
                    User loggedIn = Auth.login();

                    if (loggedIn == null) continue;

                    int type = loggedIn.getUserType();  // 1 = jobseeker, 2 = recruiter, 3 = admin

                    switch (type) {
                        case 1 -> {
                            Jobseeker js = (Jobseeker) loggedIn;
                            new JobseekerMenu(js, jobPostingManager, applicationManager).show();
                        }
                        case 2 -> {
                            Recruiter rec = (Recruiter) loggedIn;
                            new RecruiterMenu(rec, jobPostingManager, applicationManager).show();
                        }
                        case 3 -> {
                            Admin admin = (Admin) loggedIn;
                            new AdminMenu(admin, userManager, jobPostingManager, applicationManager).show();
                        }
                        default -> System.out.println("Unknown user type.");
                    }
                }

                case "2" -> Auth.register();

                case "0" -> {
                    System.out.println("                                  Exiting program...");
                    sc.close();
                    return;
                }

                default -> System.out.println("                               Invalid option.");
            }
        }
    }
}
