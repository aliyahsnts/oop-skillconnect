package auth;

import models.*;
import utils.Refresh;
import utils.ResumeGenerator;

import java.nio.file.Path;
import managers.*;

public class RegisterAuth extends Auth {

    /* -------- override register to skip the extra prompt -------- */
    @Override
    public void register() {
        handle(null); // we ask inside handle()
    }

    @Override
    protected boolean checkUserExists(String username) {
        if (userManager.usernameExists(username)) {
            System.out.println("ERROR: Username already exists! Please login.");
            return false;
        }
        return true;
    }

    @Override
    protected void handle(String ignored) {

        /* -------- Username Validation (improved) -------- */
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = sc.nextLine().trim();

            if (username.length() < 8) {
                System.out.println("ERROR: Username must be at least 8 characters long.");
                continue;
            }

            char first = username.charAt(0);
            if (!Character.isLetter(first) && first != '_') {
                System.out.println("ERROR: Username must start with a letter (A-Z, a-z) or underscore (_).");
                continue;
            }

            String illegal = username.replaceAll("[A-Za-z0-9_]", "");
            if (!illegal.isEmpty()) {
                System.out.println("ERROR: Username contains illegal character(s): " + illegal);
                continue;
            }

            if (userManager.usernameExists(username)) {
                System.out.println("ERROR: Username already exists! Please choose another.");
                continue;
            }
            break;
        }

        /* -------- Full Name Validation -------- */
        String fullName;
        while (true) {
            System.out.print("Enter full name (letters and spaces only): ");
            fullName = sc.nextLine().trim();
            if (!fullName.matches("^[a-zA-Z ]+$")) {
                System.out.println("ERROR: Full name must contain letters and spaces only.");
                continue;
            }
            fullName = fullName.toUpperCase();
            break;
        }

        /* -------- Password Validation -------- */
        String password;
        while (true) {
            System.out.print("Enter password (minimum 8 characters): ");
            password = sc.nextLine();
            if (password.length() < 8) {
                System.out.println("ERROR: Password must be at least 8 characters long.");
                continue;
            }
            break;
        }

        /* -------- User Type Validation -------- */
        int type;
        while (true) {
            System.out.print("User type (1=Jobseeker, 2=Recruiter): ");
            try {
                type = Integer.parseInt(sc.nextLine());
                if (type == 1 || type == 2) break;
                System.out.println("ERROR: Only 1 (Jobseeker) or 2 (Recruiter) allowed.");
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a number.");
            }
        }

        int id = userManager.nextId();
        User newUser = User.createUser(id, fullName, username, password, type, 0.0);
        userManager.addUser(newUser);

        /* -------- Generate resume for Jobseeker -------- */
        if (type == 1) {
            try {
                Path p = ResumeGenerator.generateCSVForRegistration((Jobseeker) newUser);
                System.out.println("Resume CSV written: " + p.toAbsolutePath());
            } catch (Exception e) {
                System.out.println("ERROR: Failed to generate resume CSV.");
                e.printStackTrace();
            }
        }

        System.out.println("SUCCESS: Account created!");
        Refresh.refreshTerminal();
    }
}