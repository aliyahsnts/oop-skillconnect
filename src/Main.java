import java.util.Scanner;
import managers.*;
import utils.Refresh;
import auth.Auth;

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
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("ERROR: Invalid option. Please enter either ‘1’ for Login, ‘2’ for Register, or ‘0’ to Exit.");
            }
        }
    }
}
