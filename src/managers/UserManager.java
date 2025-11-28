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

    // =========================================
    //           BALANCE HELPERS
    // =========================================

    /**
     * Forward to the BaseManager to find by ID.
     */
    public User findById(int id) {
        return super.findById(id);
    }

    /**
     * Returns the current balance (money) for a user.
     * Returns 0.0 if user not found.
     */
    public double getBalance(int userId) {
        User u = findById(userId);
        return (u == null) ? 0.0 : u.getMoney();
    }

    /**
     * Adjusts the user's balance by 'delta'. 
     * Returns true on success and persists the change.
     * Will not allow resulting negative balance.
     */
    public boolean adjustBalance(int userId, double delta) {
        User u = findById(userId);
        if (u == null) return false;
        double newBalance = u.getMoney() + delta;
        if (newBalance < 0) return false; // prevent negative balances
        u.setMoney(newBalance);
        persist();
        return true;
    }

    /**
     * Set the user's balance to an absolute value. Returns false if user doesn't exist
     * or if the requested balance is negative.
     */
    public boolean setBalance(int userId, double newBalance) {
        if (newBalance < 0) return false;
        User u = findById(userId);
        if (u == null) return false;
        u.setMoney(newBalance);
        persist();
        return true;
    }
}