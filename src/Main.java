import java.util.Scanner;
import managers.*;
import utils.Refresh;
import auth.Auth;
import auth.LoginAuth;
import auth.RegisterAuth;
import ui.display.Logo;

// Main class handles program flow and menu
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager userManager = new UserManager("data/users.csv");
        JobPostingManager jobPostingManager = new JobPostingManager("data/jobpostings.csv");
        ApplicationManager applicationManager = new ApplicationManager("data/applications.csv");
        ProductManager productManager = new ProductManager("data/products.csv");
        TransactionManager transactionManager = new TransactionManager("data/transactions.csv");
        transactionManager.setUserManager(userManager);
        ReportManager reportManager = new ReportManager("data/reports.csv");
        Logo logo = new Logo();

        // Initialize Auth with managers
        Auth.init(userManager, jobPostingManager, applicationManager, productManager, transactionManager, reportManager);
        LoginAuth loginAuth = new LoginAuth();
        RegisterAuth registerAuth = new RegisterAuth();
        


        while (true) {
            Refresh.refreshTerminal(); 
            // Display Logo with integrated menu
            logo.printLogoWithMenu();
            
            System.out.println();
            System.out.print("                                                            Enter choice: ");
            
            String choice = sc.nextLine();

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
                default -> System.out.println("ERROR: Invalid option. Please enter either '1' for Login, '2' for Register, or '0' to Exit.");
            }
        }
    }
}