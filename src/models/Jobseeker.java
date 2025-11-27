package models;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Jobseeker extends User {

    /* old compatibility – keep */
    private String skills;
    private String resume;

    /* new résumé fields */
    private String phone;
    private String address;
    private String summary;
    private String education;
    private final List<String> skillList      = new ArrayList<>();
    private final List<String> experienceList = new ArrayList<>();

    public Jobseeker(int id, String fullName, String username, String password, double money) {
        super(id, fullName, username, password, 1, money);
    }

    /* ======  CSV → object loader  ====== */
    public void loadResumeFromCSV() {
        Path p = Paths.get("resumes", "jobseeker_" + getId() + "_" + getUsername() + "_resume.csv");
        if (!Files.exists(p)) return;
        try (var lines = Files.lines(p).skip(1)) {          // skip header
            lines.forEach(l -> {
                String[] kv = l.split(",", 2);
                if (kv.length != 2) return;
                String field = kv[0].trim();
                String val  = kv[1].trim();
                switch (field) {
                    case "Phone"       -> setPhone(val.equals("N/A") ? null : val);
                    case "Address"     -> setAddress(val.equals("N/A") ? null : val);
                    case "Summary"     -> setSummary(val.equals("N/A") ? null : val);
                    case "Education"   -> setEducation(val.equals("N/A") ? null : val);
                    case "Skills"      -> {
                        skillList.clear();
                        if (!val.equals("N/A"))  skillList.addAll(List.of(val.split("\\s*;\\s*")));
                    }
                    case "Experience"  -> {
                        experienceList.clear();
                        if (!val.equals("N/A"))  experienceList.addAll(List.of(val.split("\\s*;\\s*")));
                    }
                }
            });
        } catch (IOException ignore) {}
    }

    /* public one-liner for recruiter reload */
    public void reloadResume() { loadResumeFromCSV(); }
    /* ===================================== */

    /* ---------- getters / setters ---------- */
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public List<String> getSkillList() { return skillList; }
    public void addSkill(String skill) { skillList.add(skill); }

    public List<String> getExperienceList() { return experienceList; }
    public void addExperience(String exp) { experienceList.add(exp); }

    /* old compatibility – keep */
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    /* CSV helpers – unchanged */
    public static Jobseeker fromCSV(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split(",", -1);
        if (p.length < 5) return null;
        int id = Integer.parseInt(p[0].trim());
        String fullName = p[1].trim();
        String username = p[2].trim();
        String password = p[3].trim();
        double money = (p.length >= 5 && !p[4].trim().isEmpty()) ? Double.parseDouble(p[4].trim()) : 0.0;
        return new Jobseeker(id, fullName, username, password, money);
    }

    public String toCSV() {
        return getId() + "," + getFullName() + "," + getUsername() + "," + getPassword() + "," + getMoney();
    }
}