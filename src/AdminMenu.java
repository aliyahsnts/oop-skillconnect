import java.util.Scanner;

public class AdminMenu {
    private static Scanner sc = new Scanner(System.in);
    private static Admin currentAdmin; // Reference to the logged-in admin

    public static void adminMenu(Admin admin) {
        currentAdmin = admin; // Save reference to admin object

        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage Users");
            System.out.println("2. Manage Marketplace");
            System.out.println("0. Logout/Exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> currentAdmin.manageUsers();       // Call Admin method
                case "2" -> currentAdmin.manageMarketplace(); // Call Admin method
                case "0" -> {
                    if (currentAdmin.confirmLogout()) return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }
}
