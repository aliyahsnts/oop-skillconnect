package managers;

import java.nio.file.*;
import java.util.*;
import models.User;
import utils.CSVHelper;

public class UserManager {
    private final Path csvPath;
    private final String HEADER = "id,fullName,username,password,userType,money";
    private final List<User> users = new ArrayList<>();

    // Constructor that takes CSV file path
    public UserManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    // Load users from CSV
    private void load() {
        users.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);
        if (lines.size() <= 1) return; // header or empty

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] p = CSVHelper.split(line);
            if (p.length < 5) continue;

            try {
                int id = Integer.parseInt(p[0].trim());
                String fullName = p[1].trim();
                String username = p[2].trim();
                String password = p[3].trim();
                int userType = Integer.parseInt(p[4].trim());
                double money = (p.length >= 6) ? Double.parseDouble(p[5].trim()) : 0.0;

                users.add(new User(id, fullName, username, password, userType, money));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid user line: " + line);
            }
        }
    }

    // Save users to CSV
    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (User u : users) {
            out.add(u.getId() + "," + u.getFullName() + "," + u.getUsername() + "," +
                    u.getPassword() + "," + u.getUserType() + "," + u.getMoney());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }

    // Check if a username exists
    public boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    // Find a user by username
    public User findUser(String username) {
        return users.stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(username))
                    .findFirst()
                    .orElse(null);
    }

    // Generate next unique ID
    public int nextId() {
        return users.stream().mapToInt(User::getId).max().orElse(0) + 1;
    }

    // Add a user (auto-generates ID if not set)
    public void addUser(User user) {
        if (user.getId() <= 0) { // ID not set
            user.setId(nextId());
        }
        users.add(user);
        persist();
    }

    // Optional: Get all users
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
