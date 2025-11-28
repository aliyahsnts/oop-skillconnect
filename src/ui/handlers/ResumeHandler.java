package ui.handlers;

import models.Jobseeker;
import utils.ResumeGenerator;
import java.util.List;
import java.util.Scanner;

public class ResumeHandler {
    private final Jobseeker jobseeker;
    private final Scanner scanner;

    public ResumeHandler(Jobseeker jobseeker, Scanner scanner) {
        this.jobseeker = jobseeker;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n===== My Résumé =====");
            viewResume();
            System.out.println("1. Edit Resume");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            
            switch (readInt()) {
                case 1 -> editResume();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    private void viewResume() {
        System.out.println("\n==========  RÉSUMÉ  ==========");
        System.out.println("Name        : " + jobseeker.getFullName());
        System.out.println("Phone       : " + nullSafe(jobseeker.getPhone()));
        System.out.println("Address     : " + nullSafe(jobseeker.getAddress()));
        System.out.println("Summary     : " + nullSafe(jobseeker.getSummary()));
        System.out.println("Education   : " + nullSafe(jobseeker.getEducation()));
        System.out.println("Skills      : " + listSafe(jobseeker.getSkillList()));
        System.out.println("Experience  : " + listSafe(jobseeker.getExperienceList()));
        System.out.println("===============================");
    }

    private void editResume() {
        System.out.println("\n====== EDIT RÉSUMÉ ======");

        jobseeker.setPhone(ask("Phone number", jobseeker.getPhone()));
        jobseeker.setAddress(ask("Address", jobseeker.getAddress()));
        jobseeker.setSummary(ask("Professional summary", jobseeker.getSummary()));
        jobseeker.setEducation(ask("Education", jobseeker.getEducation()));

        System.out.print("Skills (comma-separated): ");
        String skillsLine = scanner.nextLine().trim();
        if (!skillsLine.isEmpty()) {
            jobseeker.getSkillList().clear();
            for (String s : skillsLine.split("\\s*,\\s*")) {
                jobseeker.addSkill(s);
            }
        }

        System.out.print("Experience (comma-separated jobs): ");
        String expLine = scanner.nextLine().trim();
        if (!expLine.isEmpty()) {
            jobseeker.getExperienceList().clear();
            for (String e : expLine.split("\\s*,\\s*")) {
                jobseeker.addExperience(e);
            }
        }

        try {
            ResumeGenerator.generateCSVForRegistration(jobseeker);
            System.out.println("Résumé saved and CSV updated.");
        } catch (Exception ex) {
            System.out.println("ERROR: could not write CSV – " + ex.getMessage());
        }
    }

    private String ask(String field, String current) {
        System.out.printf("%s%s: ", field, (current == null ? "" : " (current: " + current + ")"));
        String in = scanner.nextLine().trim();
        return in.isEmpty() ? current : in;
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private String nullSafe(String s) {
        return (s == null || s.isBlank()) ? "N/A" : s;
    }
    
    private String listSafe(List<String> list) {
        return list.isEmpty() ? "N/A" : String.join("; ", list);
    }
}