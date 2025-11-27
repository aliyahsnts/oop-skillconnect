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
        System.out.print("Enter full name: ");
        String fullName = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

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

        User newUser = User.createUser(id, fullName, username, password, type, 0.0);
        userManager.addUser(newUser);

        System.out.println("SUCCESS: Account created!");
        Refresh.refreshTerminal();
    }
}
