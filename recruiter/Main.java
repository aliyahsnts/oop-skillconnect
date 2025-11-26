public class Main {
    public static void main(String[] args) {
        String dataDir = "data";
        String jobsCsv = dataDir + "/jobpostings.csv";
        String appsCsv = dataDir + "/applications.csv";

        JobPostingManager jpm = new JobPostingManager(jobsCsv);
        ApplicationManager am = new ApplicationManager(appsCsv);

        System.out.println("=== SkillConnect — Recruiter Module ===");
        RecruiterMenu menu = new RecruiterMenu(jpm, am);
        menu.show();

        System.out.println("Exited Recruiter Module. Goodbye.");
    }
}
