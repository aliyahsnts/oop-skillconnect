package utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CSVHelper {
    // Ensure the parent folder exists and file exists with header if needed
    public static void ensureFileWithHeader(Path path, String header) {
        try {
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.write(path, (header + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            System.err.println("Error ensuring file: " + e.getMessage());
        }
    }

    public static List<String> readAllLines(Path path) {
        try {
            if (!Files.exists(path)) return new ArrayList<>();
            return Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void writeAllLines(Path path, List<String> lines) {
        try {
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }

    public static void appendLine(Path path, String line) {
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error appending CSV: " + e.getMessage());
        }
    }

    // simple split - our data avoids embedded commas
    public static String[] split(String line) {
        return line.split(",", -1);
    }
}
