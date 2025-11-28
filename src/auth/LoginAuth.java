package auth;

import models.*;
import ui.*;
import utils.Refresh;
import utils.MenuPrinter;
import managers.*;

public class LoginAuth extends Auth {
    @Override
    protected boolean checkUserExists(String username) {
        if (!userManager.usernameExists(username)) {
            MenuPrinter.error("Username does not exist. Please create an account first!");
            return false;
        }
        return true;
    }

    @Override
    protected void handle(String username) {
        MenuPrinter.prompt("Enter password");
        String password = sc.nextLine();

        User user = userManager.findUser(username);

        if (!user.getPassword().equals(password)) {
            MenuPrinter.error("Incorrect password.");
            Refresh.refreshTerminal();
            return;
        }

        MenuPrinter.success("Login successful!");
        Refresh.refreshTerminal();

        switch (user.getUserType()) {
        case 1 -> {
            Jobseeker js = (Jobseeker) user;
            js.loadResumeFromCSV();
            new JobseekerMenu(js, jobPostingManager, applicationManager,
                              productManager, transactionManager, reportManager).show();
        }
        case 2 -> new RecruiterMenu(
                    (Recruiter) user,
                    jobPostingManager,
                    applicationManager,
                    productManager,
                    transactionManager,
                    reportManager,
                    userManager).show();
                    
        case 3 -> new AdminMenu(
                    (Admin) user,
                    userManager,
                    jobPostingManager,
                    applicationManager,
                    productManager,
                    transactionManager,
                    reportManager).show();
        }
    }
}