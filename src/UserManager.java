import java.io.*;
import java.util.*;

// UserManager handles storage and retrieval of users
// Loads users from CSV file and saves new users to CSV
public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private final String FILE_NAME = "users.csv"; // CSV file to store users

    // Constructor: loads users when program starts
    public UserManager() {
        loadUsersFromCSV();
    }

    // Load users from CSV file into ArrayList
    private void loadUsersFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4) {
                    String fullName = data[0];
                    String username = data[1];
                    String password = data[2];
                    int userType = Integer.parseInt(data[3]);
                    users.add(new User(fullName, username, password, userType));
                }
            }
        } catch (IOException e) {
            System.out.println("No existing user file found. Starting fresh.");
        }
    }

    // Save current ArrayList of users to CSV
    private void saveUsersToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (User u : users) {
                pw.println(u.getFullName() + "," + u.getUsername() + "," + u.getPassword() + "," + u.getUserType());
            }
        } catch (IOException e) {
            System.out.println("Error saving users.");
        }
    }

    // Check if a username already exist
    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    // Find and return a user by username
    public User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    // Add a new user and save to CSV
    public void addUser(User user) {
        users.add(user);
        saveUsersToCSV();
    }
}
