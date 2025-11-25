import java.util.Scanner;

public class JobSeekerInterface {

    private Scanner scanner = new Scanner(System.in);

    public void displayJobSeekerMenu() {
        int choice;

        while (true) {
            System.out.println("===== JOBSEEKER INTERFACE =====");
            System.out.println("1. Jobs");
            System.out.println("2. Market");
            System.out.println("3. Report");
            System.out.println("0. Logout");

            System.out.print("Enter your choice: ");
            choice = readInt();

            if (choice == 1) {
                displayJobsMenu();
            } else if (choice == 2) {
                displayMarketMenu();
            } else if (choice == 3) {
                displayReportMenu();
            } else if (choice == 0) {
                System.out.println("Logging out...");
                break;   // return to main menu
            } else {
                System.out.println("ERROR: Invalid option. Please enter 1, 2, 3, or 0.");
            }

            System.out.println(); // spacing
        }
    }

    // Sub Methods (Work on Progress)
    private void displayJobsMenu() {
        System.out.println(">>> Jobs Menu called.");
    }

    private void displayMarketMenu() {
        System.out.println(">>> Market Menu called.");
    }

    private void displayReportMenu() {
        System.out.println(">>> Report Menu called.");
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
