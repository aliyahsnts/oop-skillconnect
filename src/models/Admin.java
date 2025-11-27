package models;

public class Admin extends User {

    // Constructor
    public Admin(int id, String fullName, String username, String password,double money) {
        super(id, fullName, username, password, 3, 0.0); 
        setId(id);
    }

    // from CSV
    public static Admin fromCSV(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        String[] p = line.split(",", -1);
        if (p.length < 4) return null;

        int id = Integer.parseInt(p[0].trim());
        String fullName = p[1].trim();
        String username = p[2].trim();
        String password = p[3].trim();
        double money = 0.0;
        if (!p[4].trim().isEmpty()) {
            money = Double.parseDouble(p[4].trim());
        }

        return new Admin(id, fullName, username, password, money); 
    }

    // to CSV
    public String toCSV() {
        return getId() + "," + getFullName() + "," + getUsername() + "," + getPassword();
    }

}