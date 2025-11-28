package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Utility class to generate an application form (list of questions)
 * for a new job posting.  Enforces min 1 and max 3 questions.
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
        Refresh.refreshTerminal();                // ← clean screen
        MenuPrinter.printHeader("APPLICATION FORM CREATION");
        System.out.printf("You can add between %d and %d questions for this job application.%n", MIN_QUESTIONS, MAX_QUESTIONS);

        List<String> questions = new ArrayList<>();

        /* ----- enforce MIN_QUESTIONS ----- */
        while (questions.size() < MIN_QUESTIONS) {
            MenuPrinter.prompt("Question " + (questions.size() + 1) + " (REQUIRED)");
            String q = readQuestion();
            if (!q.isEmpty()) {
                questions.add(q);
            } else {
                MenuPrinter.error("You must provide at least one question.");
            }
        }

        /* ----- optional questions up to MAX_QUESTIONS ----- */
        while (questions.size() < MAX_QUESTIONS) {
            MenuPrinter.prompt("Question " + (questions.size() + 1) + " (Optional, enter blank to finish)");
            String q = readQuestion();
            if (q.isEmpty()) break;
            questions.add(q);
        }

        MenuPrinter.success("Application form successfully created with " + questions.size() + " questions.");
        return questions;
    }

    private String readQuestion() {
        return scanner.nextLine().trim().replace(",", " ").replace(";", " ");
    }

    /* ---------- CSV helpers ---------- */
    public static String listToCSV(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        List<String> safe = list.stream().map(s -> s.replace(";", "|")).toList();
        return String.join(";", safe);
    }

    public static List<String> csvToList(String csv) {
        if (csv == null || csv.isEmpty()) return List.of();
        return List.of(csv.split(";")).stream().map(s -> s.replace("|", ";")).toList();
    }
}