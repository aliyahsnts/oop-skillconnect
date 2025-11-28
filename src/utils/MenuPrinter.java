package utils;

public final class MenuPrinter {
    private static final String RESET  = "\u001B[0m";
    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    /* -------------- Headers / Boxes -------------- */
    public static void printHeader(String title) {
        System.out.println("+-------------------------------------------------+");
        System.out.println("|" + center(title, 49) + "|");
        System.out.println("+-------------------------------------------------+");
    }

    /* -------------- Menu Options -------------- */
    public static void printOption(String number, String label) {
        System.out.printf("  [%2s] %s%n", number, label);
    }

    /* -------------- Feedback Lines -------------- */
    public static void success(String msg) {
        System.out.println(GREEN + ">> SUCCESS: " + RESET + msg);
    }
    public static void error(String msg) {
        System.out.println(RED + ">> ERROR: " + RESET + msg);
    }
    public static void info(String msg) {
        System.out.println(CYAN + ">> INFO: " + RESET + msg);
    }
    public static void warning(String msg) {
        System.out.println(YELLOW + ">> WARNING: " + RESET + msg);
    }

    /* -------------- UX Helpers -------------- */
    public static void breadcrumb(String path) {
        System.out.println("-> You are here: " + path);
    }
    public static void prompt(String field) {
        System.out.print("-> " + field + ": ");
    }
    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        new java.util.Scanner(System.in).nextLine();
    }

    /* -------------- Internal -------------- */
    private static String center(String text, int width) {
        int pad = (width - text.length()) / 2;
        return " ".repeat(pad) + text + " ".repeat(width - text.length() - pad);
    }
}