package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class to generate an application form (list of questions)
 * for a new job posting. Enforces min 1 and max 3 questions.
 */
public class ApplicationFormGenerator {

    private final Scanner scanner = new Scanner(System.in);
    private static final int MIN_QUESTIONS = 1;
    private static final int MAX_QUESTIONS = 3;

    /**
     * Prompts the recruiter to input questions for the application form.
     * @return A list of strings representing the application questions.
     */
    public List<String> generateForm() {
        System.out.println("\n--- Application Form Creation ---");
        System.out.printf("You can add between %d and %d questions for this job application.\n", MIN_QUESTIONS, MAX_QUESTIONS);
        
        List<String> questions = new ArrayList<>();
        
        // Loop to enforce MIN_QUESTIONS
        while (questions.size() < MIN_QUESTIONS) {
            System.out.printf("Question %d (REQUIRED): ", questions.size() + 1);
            String q = readQuestion();
            if (!q.isEmpty()) {
                questions.add(q);
            } else {
                System.out.println("ERROR: You must provide at least one question.");
            }
        }

        // Loop for optional questions up to MAX_QUESTIONS
        while (questions.size() < MAX_QUESTIONS) {
            System.out.printf("Question %d (Optional, enter blank to finish): ", questions.size() + 1);
            String q = readQuestion();
            if (q.isEmpty()) {
                break; // Recruiter is done adding questions
            }
            questions.add(q);
        }
        
        System.out.println("Application form successfully created with " + questions.size() + " questions.");
        return questions;
    }

    private String readQuestion() {
        // Read input and sanitize for CSV storage
        return scanner.nextLine().trim().replace(",", " ").replace(";", " ");
    }

    /**
     * Helper to safely join the list of questions/answers into a single CSV field.
     * Uses a semicolon (;) as a secondary delimiter that must be escaped.
     */
    public static String listToCSV(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        // Replace internal semicolons with a safe character (e.g., pipe |) before joining
        List<String> safeList = list.stream()
            .map(s -> s.replace(";", "|"))
            .toList();
        return String.join(";", safeList);
    }

    /**
     * Helper to safely split the CSV field back into a list.
     */
    public static List<String> csvToList(String csvString) {
        if (csvString == null || csvString.isEmpty()) return List.of();
        // Split by semicolon and restore internal safe character (pipe |) to semicolon
        return List.of(csvString.split(";")).stream()
            .map(s -> s.replace("|", ";"))
            .toList();
    }
}