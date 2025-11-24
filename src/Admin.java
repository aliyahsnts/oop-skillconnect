public class Admin extends User {
  // Admin constructor calls User constructor
    public Admin(String fullName, String username, String password) {
      super(fullName, username, password, 3);
    }

    // Admin-specific methods
    public void manageUsers() {
        System.out.println("Admin managing users...");
    }

    public void manageMarketplace() {
        System.out.println("Admin managing marketplace...");
    }

    public void manageReports() {
        System.out.println("Admin managing reports...");
    }
}
