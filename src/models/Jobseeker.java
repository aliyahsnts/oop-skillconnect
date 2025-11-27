package models;

public class Jobseeker extends User {
    // Optional: Jobseeker-specific attributes
    private String skills;
    private String resume;

    // Constructor
    public Jobseeker(int id, String fullName, String username, String password, double money) {
        super(id, fullName, username, password, 1, 0.0); // userType=1, starting money=0
        setId(id);
    }

    // Getters/Setters for jobseeker-specific fields
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    //from CSV
    public static Jobseeker fromCSV(String line) {
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

    // if (p.length >= 5) j.setSkills(p[4].trim());
    // if (p.length >= 6) j.setResume(p[5].trim());
    Jobseeker j = new Jobseeker(id, fullName, username, password, money);
    // j.setSkills(skills);

    return j;
    }

    //to CSV
    public String toCSV() {
    return getId() + "," + 
        getFullName() + "," + getUsername() + "," + getPassword() + ","; 
        //    + (getSkills() != null ? getSkills() : "") + "," +
        //    (getResume() != null ? getResume() : "");
    }
}