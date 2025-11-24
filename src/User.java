import java.io.Serializable;

// User class represents a single user account
// Implements Serializable to save User objects 
public class User implements Serializable {
    private String fullName;
    private String username;
    private String password;
    private int userType; // 1=Jobseeker, 2=Recruiter

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
}
