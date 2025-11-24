import java.io.Serializable;
import java.util.Scanner;

// User class represents a single user account
// Implements Serializable to save User objects 
public class User implements Serializable {
    private String fullName;
    private String username;
    private String password;
    private int userType; // 1=Jobseeker, 2=Recruiter
    //add money here

    // Constructor
    public User(String fullName, String username, String password, int userType) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    // Getters for user information
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getUserType() { return userType; }

    // Setters to allow updating user information
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(int userType) { this.userType = userType; }

    // Common methods for all users
    public void viewAllJobPosts() {
      System.out.println("Viewing all job posts...");
    }

    public void createReport() {
      System.out.println("Creating report...");
    }

    public void viewReport() { 
      System.out.println("Viewing a report...");
    }

    public void viewAllReports() {
      System.out.println("Viewing all reports...");
    }

    public void updateReport() {
      System.out.println("Updating a report...");
    }

    public void deleteReport() {
      System.out.println("Deleting a report...");
    }

    public boolean confirmLogout(){
      Scanner sc = new Scanner(System.in);
      while (true) {
          System.out.print("Are you sure you would like to logout? (y/n): ");
          String input = sc.nextLine().trim().toLowerCase();

          if (input.equals("y")) {
              System.out.println("Logging out...");
              return true; // user confirmed logout
          } else if (input.equals("n")) {
              System.out.println("Returning to menu...");
              return false; // user canceled logout
          } else {
              System.out.println("Invalid input. Please enter 'y' or 'n'.");
          }
      }
    }
}

