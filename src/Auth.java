import java.util.Scanner;

// Auth class handles login and registration logic

public class Auth {
    private static Scanner sc = new Scanner(System.in);
    private static UserManager userManager;

    // Initialize Auth with UserManager instance
    public static void init(UserManager manager) {
        userManager = manager;
    }

    // Login function
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
            case 1 -> System.out.println("Displaying Jobseeker Interface...");
            case 2 -> System.out.println("Displaying Recruiter Interface...");
            case 3 -> System.out.println("Displaying Admin Interface...");
            default -> System.out.println("Unknown user type.");
        }
    }

    // Register function
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

        // Create new user and save
        User newUser = new User(fullName, username, password, userType);
        userManager.addUser(newUser);
        System.out.println("SUCCESS: Account created successfully!");
        Refresh.refreshTerminal(); 
    }
}
