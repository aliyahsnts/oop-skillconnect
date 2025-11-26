package models;

public class Recruiter extends User {
    // Optional: Recruiter-specific attributes
    private String companyName;
    private String companyDescription;

    // Constructor
    public Recruiter(int id, String fullName, String username, String password) {
        super(id, fullName, username, password, 2, 0.0); // userType=2, starting money=0
        setId(id);
    }

    // Getters/Setters for recruiter-specific fields
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getCompanyDescription() { return companyDescription; }
    public void setCompanyDescription(String companyDescription) { this.companyDescription = companyDescription; }

    // from CSV
    public static Recruiter fromCSV(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        String[] p = line.split(",", -1);
        if (p.length < 4) return null;

        int id = Integer.parseInt(p[0].trim());
        String fullName = p[1].trim();
        String username = p[2].trim();
        String password = p[3].trim();

        Recruiter r = new Recruiter(id, fullName, username, password);

        if (p.length >= 5) r.setCompanyName(p[4].trim());
        if (p.length >= 6) r.setCompanyDescription(p[5].trim());

        return r;
    }

    // to CSV
    public String toCSV() {
        return getId() + "," + getFullName() + "," + getUsername() + "," + getPassword() + "," +
            (getCompanyName() != null ? getCompanyName() : "") + "," +
            (getCompanyDescription() != null ? getCompanyDescription() : "");
    }
}