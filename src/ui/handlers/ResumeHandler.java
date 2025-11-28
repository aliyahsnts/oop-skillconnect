package ui.handlers;

import models.Jobseeker;
import utils.MenuPrinter;
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
            MenuPrinter.printHeader("MY RÉSUMÉ");
            viewResume();
            MenuPrinter.printOption("1", "Edit Résumé");
            MenuPrinter.printOption("0", "Back");
            MenuPrinter.prompt("Enter choice");

            switch (readInt()) {
                case 1 -> editResume();
                case 0 -> { return; }
                default -> MenuPrinter.error("Invalid selection.");
            }
        }
    }

    private void viewResume() {
        System.out.println();
        System.out.println("Name       : " + jobseeker.getFullName());
        System.out.println("Phone      : " + nullSafe(jobseeker.getPhone()));
        System.out.println("Address    : " + nullSafe(jobseeker.getAddress()));
        System.out.println("Summary    : " + nullSafe(jobseeker.getSummary()));
        System.out.println("Education  : " + nullSafe(jobseeker.getEducation()));
        System.out.println("Skills     : " + listSafe(jobseeker.getSkillList()));
        System.out.println("Experience : " + listSafe(jobseeker.getExperienceList()));
        System.out.println();
    }

    private void editResume() {
        MenuPrinter.printHeader("EDIT RÉSUMÉ");

        jobseeker.setPhone(ask("Phone number", jobseeker.getPhone()));
        jobseeker.setAddress(ask("Address", jobseeker.getAddress()));
        jobseeker.setSummary(ask("Professional summary", jobseeker.getSummary()));
        jobseeker.setEducation(ask("Education", jobseeker.getEducation()));

        MenuPrinter.prompt("Skills (comma-separated)");
        String skillsLine = scanner.nextLine().trim();
        if (!skillsLine.isEmpty()) {
            jobseeker.getSkillList().clear();
            for (String s : skillsLine.split("\\s*,\\s*")) jobseeker.addSkill(s);
        }

        MenuPrinter.prompt("Experience (comma-separated jobs)");
        String expLine = scanner.nextLine().trim();
        if (!expLine.isEmpty()) {
            jobseeker.getExperienceList().clear();
            for (String e : expLine.split("\\s*,\\s*")) jobseeker.addExperience(e);
        }

        try {
            ResumeGenerator.generateCSVForRegistration(jobseeker);
            MenuPrinter.success("Résumé saved and CSV updated.");
        } catch (Exception ex) {
            MenuPrinter.error("Could not write CSV – " + ex.getMessage());
        }
        MenuPrinter.pause();
    }

    private String ask(String field, String current) {
        MenuPrinter.prompt(field + (current == null ? "" : " (current: " + current + ")"));
        String in = scanner.nextLine().trim();
        return in.isEmpty() ? current : in;
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                MenuPrinter.error("Please enter a valid number.");
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