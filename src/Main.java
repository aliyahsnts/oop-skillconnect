import java.util.Scanner;
import managers.*;
import utils.Refresh;
import auth.Auth;
import auth.LoginAuth;
import auth.RegisterAuth;

// Main class handles program flow and menu
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager userManager = new UserManager("data/users.csv");
        JobPostingManager jobPostingManager = new JobPostingManager("data/jobpostings.csv");
        ApplicationManager applicationManager = new ApplicationManager("data/applications.csv");
        ProductManager productManager = new ProductManager("data/products.csv");
        TransactionManager transactionManager = new TransactionManager("data/transactions.csv");
        ReportManager reportManager = new ReportManager("data/reports.csv");

        // Initialize Auth with managers
        Auth.init(userManager, jobPostingManager, applicationManager, productManager, transactionManager, reportManager);
        LoginAuth loginAuth = new LoginAuth();
        RegisterAuth registerAuth = new RegisterAuth();

        while (true) {
            Refresh.refreshTerminal(); 
            // Display Login/Register Menu
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
            System.out.println("║   ▒▒▒▒▒▒▒▒▒  ▒▒▒▒▒   ▒▒▒▒ ▒▒▒▒▒ ▒▒▒▒▒▒▒▒▒▒▒ ▒▒▒▒▒▒▒▒▒▒▒      ▒▒▒▒▒▒▒▒▒     ▒▒▒▒▒▒▒    ▒▒▒▒▒    ▒▒▒▒▒ ▒▒▒▒▒▒▒▒▒▒   ▒▒▒▒▒▒▒▒▒     ▒▒▒▒▒     ║");
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
            System.out.println("                                │         2. Register             │");
            System.out.println("                                │         0. Exit                 │");
            System.out.println("                                ├─────────────────────────────────┤");
            System.out.print("                                  │    Enter choice: ");
            
            String choice = sc.nextLine();
            System.out.println("                                └─────────────────────────────────┘");

            // Handle menu choices
            switch (choice) {
                case "1" -> loginAuth.login();
                case "2" -> registerAuth.register();
                case "0" -> {
                    System.out.println("Exiting program...");
                    Refresh.refreshTerminal(); 
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("ERROR: Invalid option. Please enter either ‘1’ for Login, ‘2’ for Register, or ‘0’ to Exit.");
            }
        }
    }
}