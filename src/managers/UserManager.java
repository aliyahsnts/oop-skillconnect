package managers;

import java.util.List;

import models.User;

public class UserManager extends BaseManager<User> {

    public UserManager(String csvFilePath) {
        super(csvFilePath, "id,fullName,username,password,userType,money");
    }

    // =========================================
    //        IMPLEMENT ABSTRACT METHODS
    // =========================================

    @Override
    protected User parseEntity(String[] parts) {
        int id = Integer.parseInt(parts[0].trim());
        String fullName = parts[1].trim();
        String username = parts[2].trim();
        String password = parts[3].trim();
        int userType = Integer.parseInt(parts[4].trim());
        double money = (parts.length >= 6) ? Double.parseDouble(parts[5].trim()) : 0.0;

        return User.createUser(id, fullName, username, password, userType, money);
    }

    @Override
    protected String toCSVLine(User user) {
        return user.getId() + "," + 
               user.getFullName() + "," + 
               user.getUsername() + "," +
               user.getPassword() + "," + 
               user.getUserType() + "," + 
               user.getMoney();
    }

    @Override
    protected int getId(User user) {
        return user.getId();
    }

    @Override
    protected int getMinimumColumns() {
        return 5;
    }

    @Override
    protected String getEntityName() {
        return "user";
    }

    @Override
    protected int getStartingId() {
        return 1;
    }

    // =========================================
    //           CUSTOM METHODS
    // =========================================

    public boolean usernameExists(String username) {
        return entities.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public User findUser(String username) {
        return entities.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public void addUser(User user) {
        if (user.getId() <= 0) {
            user.setId(nextId());
        }
        entities.add(user);
        persist();
    }

    public void saveUsers() {
        persist();
    }

    public boolean deleteUser(String username) {
        boolean removed = entities.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
        if (removed) persist();
        return removed;
    }

    public List<User> getAllUsers() {
        return findAll();
    }
}