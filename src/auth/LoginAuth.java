package auth;

import models.*;
import ui.*;
import utils.Refresh;
import managers.*;

public class LoginAuth extends Auth {
    @Override
    protected boolean checkUserExists(String username) {
        if (!userManager.usernameExists(username)) {
            System.out.println("ERROR: Username does not exist. Please create an account first!");
            return false;
        }
        return true;
    }

    @Override
    protected void handle(String username) {
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        User user = userManager.findUser(username);

        if (!user.getPassword().equals(password)) {
            System.out.println("ERROR: Incorrect password.");
            Refresh.refreshTerminal();
            return;
        }

        System.out.println("SUCCESS: Login successful!");
        Refresh.refreshTerminal();

        switch (user.getUserType()) {
            case 1 -> new JobseekerMenu(
            (Jobseeker) user,
            jobPostingManager,
            applicationManager,
            productManager,
            transactionManager,
            reportManager
        ).show();

        case 2 -> new RecruiterMenu(
            (Recruiter) user,
            jobPostingManager,
            applicationManager,
            productManager,
            transactionManager,
            reportManager
        ).show();

        case 3 -> new AdminMenu(
            (Admin) user,
            userManager,
            jobPostingManager,
            applicationManager,
            productManager,
            transactionManager,
            reportManager
        ).show();
        }
    }
}
