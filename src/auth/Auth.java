package auth;

import java.util.Scanner;
import managers.*;
import models.*;
import utils.Refresh;

public abstract class Auth {
    protected static JobPostingManager jobPostingManager;
    protected static ApplicationManager applicationManager;
    protected static ProductManager productManager;
    protected static TransactionManager transactionManager;
    protected static ReportManager reportManager;
    protected static Scanner sc = new Scanner(System.in);
    protected static UserManager userManager;
    protected static ReviewManager reviewManager;

    public static void init(UserManager um, JobPostingManager jpm, ApplicationManager am,
                            ProductManager pm, TransactionManager tm, ReportManager rm) {
        userManager = um;
        jobPostingManager = jpm;
        applicationManager = am;
        productManager = pm;
        transactionManager = tm;
        reportManager = rm;
        reviewManager = new ReviewManager("data/reviews.csv");
    }

    // ============================
    // TEMPLATE METHOD
    // ============================
    public final void process() {
        String username = askUsername();

        if (!checkUserExists(username)) {
            Refresh.refreshTerminal();
            return;
        }

        handle(username);
    }

    public void login() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        if (checkUserExists(username)) {
            handle(username);
        }
    }

    public void register() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        if (checkUserExists(username)) {
            handle(username);
        }
    }

    // ============================
    // TO BE OVERRIDDEN
    // ============================
    protected abstract boolean checkUserExists(String username);
    protected abstract void handle(String username);

    // ============================
    // SHARED METHODS
    // ============================
    protected String askUsername() {
        System.out.print("Enter username: ");
        return sc.nextLine();
    }
}
