import java.util.Scanner;

// Main class handles program flow and menu
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UserManager userManager = new UserManager();

        // Initialize Auth with UserManager
        Auth.init(userManager); 

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
