package auth;

import models.*;
import utils.Refresh;
import utils.ResumeGenerator;
import utils.MenuPrinter;
import java.nio.file.Path;
import managers.*;

public class RegisterAuth extends Auth {

    @Override
    public void register() {
        handle(null);
    }

    @Override
    protected boolean checkUserExists(String username) {
        if (userManager.usernameExists(username)) {
            MenuPrinter.error("Username already exists! Please login.");
            return false;
        }
        return true;
    }

    @Override
    protected void handle(String ignored) {

        /* -------- Username Validation (improved) -------- */
        String username;
        while (true) {
            MenuPrinter.prompt("Enter username");
            username = sc.nextLine().trim();

            if (username.length() < 8) {
                MenuPrinter.error("Username must be at least 8 characters long.");
                continue;
            }

            char first = username.charAt(0);
            if (!Character.isLetter(first) && first != '_') {
                MenuPrinter.error("Username must start with a letter (A-Z, a-z) or underscore (_).");
                continue;
            }

            String illegal = username.replaceAll("[A-Za-z0-9_]", "");
            if (!illegal.isEmpty()) {
                MenuPrinter.error("Username contains illegal character(s): " + illegal);
                continue;
            }

            if (userManager.usernameExists(username)) {
                MenuPrinter.error("Username already exists! Please choose another.");
                continue;
            }
            break;
        }

        /* -------- Full Name Validation -------- */
        String fullName;
        while (true) {
            MenuPrinter.prompt("Enter full name (letters and spaces only)");
            fullName = sc.nextLine().trim();
            if (!fullName.matches("^[a-zA-Z ]+$")) {
                MenuPrinter.error("Full name must contain letters and spaces only.");
                continue;
            }
            fullName = fullName.toUpperCase();
            break;
        }

        /* -------- Password Validation -------- */
        String password;
        while (true) {
            MenuPrinter.prompt("Enter password (minimum 8 characters)");
            password = sc.nextLine();
            if (password.length() < 8) {
                MenuPrinter.error("Password must be at least 8 characters long.");
                continue;
            }
            break;
        }

        /* -------- User Type Validation -------- */
        int type;
        while (true) {
            MenuPrinter.prompt("User type (1=Jobseeker, 2=Recruiter)");
            try {
                type = Integer.parseInt(sc.nextLine());
                if (type == 1 || type == 2) break;
                MenuPrinter.error("Only 1 (Jobseeker) or 2 (Recruiter) allowed.");
            } catch (NumberFormatException e) {
                MenuPrinter.error("Please enter a number.");
            }
        }

        int id = userManager.nextId();
        User newUser = User.createUser(id, fullName, username, password, type, 0.0);
        userManager.addUser(newUser);

        /* -------- Generate resume for Jobseeker -------- */
        if (type == 1) {
            try {
                Path p = ResumeGenerator.generateCSVForRegistration((Jobseeker) newUser);
                MenuPrinter.success("Resume CSV written: " + p.toAbsolutePath());
            } catch (Exception e) {
                MenuPrinter.error("Failed to generate resume CSV.");
                e.printStackTrace();
            }
        }

        MenuPrinter.success("Account created!");
        Refresh.refreshTerminal();
    }
}