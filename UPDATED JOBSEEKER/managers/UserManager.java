package managers;

import java.nio.file.*;
import java.util.*;

import models.User;
import models.Jobseeker;
import models.Recruiter;
import models.Admin;

import utils.CSVHelper;

public class UserManager {

    private final Path csvPath;
    private final String HEADER = "id,fullName,username,password,userType,money";

    private final List<User> users = new ArrayList<>();

    public UserManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    // Load all users from CSV
    private void load() {
        users.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);

        if (lines.size() <= 1) return;

        for (int i = 1; i < lines.size(); i++) {

            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            String[] p = CSVHelper.split(line);
            if (p.length < 5) continue;

            try {
                int id = Integer.parseInt(p[0]);
                String fullName = p[1];
                String username = p[2];
                String password = p[3];
                int userType = Integer.parseInt(p[4]);

                User user;

                // Convert to correct subclass based on userType
                switch (userType) {
                    case 1 -> user = new Jobseeker(id, fullName, username, password);
                    case 2 -> user = new Recruiter(id, fullName, username, password);
                    case 3 -> user = new Admin(id, fullName, username, password);
                    default -> user = new User(id, fullName, username, password, userType, 0);
                }

                users.add(user);

            } catch (Exception e) {
                System.err.println("Invalid user entry: " + lines.get(i));
            }
        }
    }

    // Save current users to CSV
    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);

        for (User u : users) {
            out.add(
                u.getId() + "," +
                u.getFullName() + "," +
                u.getUsername() + "," +
                u.getPassword() + "," +
                u.getUserType() + "," +
                u.getMoney()
            );
        }

        CSVHelper.writeAllLines(csvPath, out);
    }

    // Check username existence
    public boolean usernameExists(String username) {
        return users.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    // Find user
    public User findUser(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    // Generate next available ID
    public int nextId() {
        return users.stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0) + 1;
    }

    // Add new user
    public void addUser(User user) {
        if (user.getId() <= 0) user.setId(nextId());
        users.add(user);
        persist();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
