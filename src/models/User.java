package models;

import java.io.Serializable;

public class User implements Serializable {
    // USER Attributes (Encapsulation)
    private int id; 
    private String fullName;
    private String username;
    private String password;
    private int userType; // 1=Jobseeker, 2=Recruiter, 3=Admin
    private double money;

    // Constructor
    public User(int id, String fullName, String username, String password, int userType, double money) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.money = money;
    }

    // Getters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getUserType() { return userType; }
    public double getMoney() { return money; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setUserType(int userType) { this.userType = userType; }
    public void setMoney(double money) { this.money = money; }

    //create new user
    public static User createUser(int id, String fullName, String username, String password, int userType, double money) {
        return switch (userType) {
            case 1 -> new Jobseeker(id, fullName, username, password, money);
            case 2 -> new Recruiter(id, fullName, username, password, money);
            case 3 -> new Admin(id, fullName, username, password, money);
            default -> throw new IllegalArgumentException("Invalid user type: " + userType);
        };
    }
}