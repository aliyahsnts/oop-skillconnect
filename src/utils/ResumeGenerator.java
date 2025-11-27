package utils;

import models.Jobseeker;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResumeGenerator {

    /**
     * Generates a unique resume CSV for a newly registered jobseeker.
     * File name: resumes/jobseeker_<id>_<username>_resume.csv
     * Returns the Path to the created file.
     */
    public static Path generateCSVForRegistration(Jobseeker jobseeker) throws IOException {
        // Ensure resumes folder exists
        Path folder = Paths.get("resumes");
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        // Unique file name
        String fileName = String.format("jobseeker_%d_%s_resume.csv",
                jobseeker.getId(), jobseeker.getUsername());
        Path filePath = folder.resolve(fileName);

        // Build CSV content
        String header = "Field,Value";
        String content = """
                Full Name,%s
                Username,%s
                Phone,%s
                Address,%s
                Summary,%s
                Education,%s
                Skills,%s
                Experience,%s
                Generated On,%s
                """.formatted(
                jobseeker.getFullName(),
                jobseeker.getUsername(),
                safe(jobseeker.getPhone()),
                safe(jobseeker.getAddress()),
                safe(jobseeker.getSummary()),
                safe(jobseeker.getEducation()),
                listSafe(jobseeker.getSkillList()),
                listSafe(jobseeker.getExperienceList()),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        // Write file
        Files.write(filePath, (header + "\n" + content).getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return filePath;
    }

    // Helper: avoid nulls
    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "N/A" : s;
    }

    // Helper: join lists safely
    private static String listSafe(java.util.List<String> list) {
        return (list == null || list.isEmpty()) ? "N/A" : String.join("; ", list);
    }
}