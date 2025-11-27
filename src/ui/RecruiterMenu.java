package ui;

import java.util.List;
import java.util.Scanner;
import models.*;
import managers.*;

public class RecruiterMenu {
    private final Recruiter recruiter;
    private final JobPostingManager jpm;
    private final ApplicationManager am;
    private final UserManager userManager;   // NEW
    private final Scanner scanner = new Scanner(System.in);

    // NEW constructor signature – added UserManager
    public RecruiterMenu(Recruiter recruiter,
                         JobPostingManager jpm,
                         ApplicationManager am,
                         ProductManager pm,
                         TransactionManager tm,
                         ReportManager rm,
                         UserManager um) {
        this.recruiter = recruiter;
        this.jpm = jpm;
        this.am = am;
        this.userManager = um;               // NEW
    }

    public void show() {
        while (true) {
            System.out.println("\n=== RECRUITER MENU ===");
            System.out.println("Welcome, " + recruiter.getFullName() + "!");
            System.out.println("[1] Create Job Posting");
            System.out.println("[2] View All Job Postings");
            System.out.println("[3] Update Job Posting");
            System.out.println("[4] Delete Job Posting");
            System.out.println("[0] Logout");
            System.out.print("Enter your choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> createJob();
                case "2" -> viewAllJobs();
                case "3" -> updateJob();
                case "4" -> deleteJob();
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    /* ====================  RESUME VIEWER  ==================== */
private void viewApplicantResume(int applicantId) {
    User u = userManager.getAllUsers()
                        .stream()
                        .filter(us -> us.getId() == applicantId && us.getUserType() == 1)
                        .findFirst()
                        .orElse(null);
    if (u == null) {
        System.out.println("ERROR: Applicant résumé not found.");
        return;
    }
    Jobseeker js = (Jobseeker) u;
    js.reloadResume();          // <-- ALWAYS load latest CSV before printing

    System.out.println("\n==========  RÉSUMÉ  ==========");
    System.out.println("Name        : " + js.getFullName());
    System.out.println("Phone       : " + nullSafe(js.getPhone()));
    System.out.println("Address     : " + nullSafe(js.getAddress()));
    System.out.println("Summary     : " + nullSafe(js.getSummary()));
    System.out.println("Education   : " + nullSafe(js.getEducation()));
    System.out.println("Skills      : " + listSafe(js.getSkillList()));
    System.out.println("Experience  : " + listSafe(js.getExperienceList()));
    System.out.println("===============================");
}

/* tiny helpers – keep them in the same file */
private String nullSafe(String s) { return (s == null || s.isBlank()) ? "N/A" : s; }
private String listSafe(List<String> list) { return list.isEmpty() ? "N/A" : String.join("; ", list); }
/* ========================================================= */

    private void createJob() {
        System.out.print("Enter job name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter job description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter hours needed (e.g., 2 hours): ");
        String hours = scanner.nextLine().trim();
        System.out.print("Enter payment amount: ");
        double payment = readDouble();

        JobPosting job = jpm.create(name, desc, hours, payment, recruiter.getFullName());
        System.out.println("SUCCESS: Job post created! (Job ID: " + job.getJobId() + ")");
    }

    private void viewAllJobs() {
        List<JobPosting> list = jpm.findAll();
        if (list.isEmpty()) {
            System.out.println("No job postings found.");
            return;
        }
        for (JobPosting j : list) System.out.println("---------------------------\n" + j.displayString());
        System.out.println("---------------------------");
        System.out.println("[1] View Applications");
        System.out.println("[0] Return to Menu");
        System.out.print("Choose an option: ");
        if ("1".equals(scanner.nextLine().trim())) viewApplications(list.get(0).getJobId());
    }

    private void viewApplications(int jobNum) {
        List<Application> apps = am.findByJobId(jobNum);
        if (apps.isEmpty()) {
            System.out.println("No applications for this job.");
            return;
        }
        apps.forEach(a -> System.out.println("---------------------------\n" + a.displayString()));

        /* NEW ACTION MENU */
        while (true) {
            System.out.println("---------------------------");
            System.out.println("[1] View Applicant Résumé");
            System.out.println("[2] Hire Applicant");
            System.out.println("[3] Decline Applicant");
            System.out.println("[0] Return to Menu");
            System.out.print("Enter your choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Enter Application ID to view résumé: ");
                    int appId = readInt();
                    Application app = am.findByApplicationId(appId);
                    if (app == null || app.getJobId() != jobNum) {
                        System.out.println("ERROR: Invalid Application ID.");
                        continue;
                    }
                    viewApplicantResume(app.getApplicantId());
                }
                case "2" -> { hireApplicant(); return; }
                case "3" -> { declineApplicant(); return; }
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void hireApplicant() {
        System.out.print("Enter Application ID to hire: ");
        int appId = readInt();
        Application app = am.findByApplicationId(appId);
        if (app == null) {
            System.out.println("ERROR: Application not found.");
            return;
        }
        System.out.print("Are you sure you want to hire " + app.getApplicantName() + "? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            am.updateStatus(appId, "Hired");
            System.out.println("SUCCESS: Applicant hired!");
        } else {
            System.out.println("Hiring cancelled.");
        }
    }

    private void declineApplicant() {
        System.out.print("Enter Application ID to decline: ");
        int appId = readInt();
        Application app = am.findByApplicationId(appId);
        if (app == null) {
            System.out.println("ERROR: Application not found.");
            return;
        }
        System.out.print("Are you sure you want to decline " + app.getApplicantName() + "? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            am.updateStatus(appId, "Declined");
            System.out.println("SUCCESS: Application declined!");
        } else {
            System.out.println("Declining cancelled.");
        }
    }

    private void updateJob() {
        System.out.print("Enter Job Number to update: ");
        int jobNum = readInt();
        JobPosting job = jpm.findById(jobNum);
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        System.out.println("Current job info:\n" + job.displayString());

        System.out.print("Enter new job name (blank to keep): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter new description (blank to keep): ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter new hours (blank to keep): ");
        String hours = scanner.nextLine().trim();
        System.out.print("Enter new payment (blank to keep): ");
        String payStr = scanner.nextLine().trim();
        System.out.print("Enter new status (Available/Closed) (blank to keep): ");
        String status = scanner.nextLine().trim();

        Double payment = null;
        if (!payStr.isEmpty()) {
            try {
                payment = Double.valueOf(payStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid payment. Update cancelled.");
                return;
            }
        }

        boolean ok = jpm.update(jobNum,
                name.isEmpty() ? null : name,
                desc.isEmpty() ? null : desc,
                hours.isEmpty() ? null : hours,
                payment,
                status.isEmpty() ? null : status);
        System.out.println(ok ? "SUCCESS: Job updated!" : "ERROR: Could not update job.");
    }

    private void deleteJob() {
        System.out.print("Enter Job Number to delete: ");
        int jobNum = readInt();
        JobPosting job = jpm.findById(jobNum);
        if (job == null) {
            System.out.println("ERROR: Job not found.");
            return;
        }
        System.out.println("Job info:\n" + job.displayString());
        System.out.print("Are you sure you want to delete this job? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            boolean removed = jpm.delete(jobNum);
            System.out.println(removed ? "SUCCESS: Job removed!" : "ERROR: Could not remove job.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /* utility */
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}