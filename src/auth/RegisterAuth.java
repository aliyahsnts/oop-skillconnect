package auth;

import models.*;
import utils.Refresh;
import managers.*;

public class RegisterAuth extends Auth {

  @Override
  protected boolean checkUserExists(String username) {
      if (userManager.usernameExists(username)) {
          System.out.println("ERROR: Username already exists! Please login.");
          return false;
      }
      return true;
  }

  @Override
  protected void handle(String username) {

    // -------- Username Validation --------

    while (true) {
      System.out.print("Enter username: ");
      username = sc.nextLine().trim();
      if (!username.matches("^[a-z_][a-z0-9_]*$")) {
          System.out.println("ERROR: Username must start with a letter or underscore, and contain only a-z, 0-9, or _.");
          continue;
      }

      if (username.length() < 8) {
          System.out.println("ERROR: Username must be at least 8 characters long.");
          continue;
      }
      // Already exists check
      if (userManager.usernameExists(username)) {
          System.out.println("ERROR: Username already exists! Please choose another.");
          continue;
      }
      break; // valid username
    }

    // -------- Full Name Validation --------
    String fullName = "";
    while (true) {
        System.out.print("Enter full name (letters and spaces only): ");
        fullName = sc.nextLine().trim();
        if (!fullName.matches("^[a-zA-Z ]+$")) {
            System.out.println("ERROR: Full name must contain letters and spaces only.");
            continue;
        }
        fullName = fullName.toUpperCase(); // save as uppercase
        break;
    }

    // -------- Password Validation --------
    String password = "";
    while (true) {
        System.out.print("Enter password (minimum 8 characters): ");
        password = sc.nextLine();
        if (password.length() < 8) {
            System.out.println("ERROR: Password must be at least 8 characters long.");
            continue;
        }
        break;
    }

    // -------- User Type Validation --------
    System.out.print("User type (1=Jobseeker, 2=Recruiter): ");
    int type = 0;
    while (true) {
        System.out.print("User type (1=Jobseeker, 2=Recruiter): ");
        try {
            type = Integer.parseInt(sc.nextLine());
            if (type == 1 || type == 2) break;
            System.out.println("ERROR: Invalid user type. Only 1 (Jobseeker) or 2 (Recruiter) allowed.");
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Please enter a number.");
        }
    }

    int id = userManager.nextId();

    // -------- Create User --------
    User newUser = User.createUser(id, fullName, username, password, type, 0.0);
    userManager.addUser(newUser);

    System.out.println("SUCCESS: Account created!");
    Refresh.refreshTerminal();
  }
}
