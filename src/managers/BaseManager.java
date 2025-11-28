package managers;

import java.nio.file.*;
import java.util.*;
import utils.CSVHelper;

/**
 * Abstract base class for all managers
 * Uses generics to handle different entity types
 * @param <T> The entity type (User, JobPosting, Application, etc.)
 */
public abstract class BaseManager<T> {
    protected final Path csvPath;
    protected final String header;
    protected final List<T> entities = new ArrayList<>();

    // Constructor
    protected BaseManager(String csvFilePath, String header) {
        this.csvPath = Paths.get(csvFilePath);
        this.header = header;
        CSVHelper.ensureFileWithHeader(csvPath, header);
        load();
    }

    // =========================================
    //           TEMPLATE METHODS
    // =========================================
    
    /**
     * Load entities from CSV - Template Method
     * Handles file reading, subclasses implement parsing
     */
    protected void load() {
        entities.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);
        
        if (lines.size() <= 1) return; // header or empty

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            
            String[] parts = CSVHelper.split(line);
            if (parts.length < getMinimumColumns()) continue;

            try {
                T entity = parseEntity(parts);
                if (entity != null) {
                    entities.add(entity);
                }
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid " + getEntityName() + " line: " + line);
            }
        }
    }

    /**
     * Save entities to CSV
     */
    public void persist() {
        List<String> output = new ArrayList<>();
        output.add(header);
        
        for (T entity : entities) {
            output.add(toCSVLine(entity));
        }
        
        CSVHelper.writeAllLines(csvPath, output);
    }

    // =========================================
    //           CRUD OPERATIONS
    // =========================================

    /**
     * Find all entities
     */
    public List<T> findAll() {
        return new ArrayList<>(entities);
    }

    /**
     * Find entity by ID
     */
    public T findById(int id) {
        return entities.stream()
                .filter(e -> getId(e) == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Delete entity by ID
     */
    public boolean delete(int id) {
        boolean removed = entities.removeIf(e -> getId(e) == id);
        if (removed) persist();
        return removed;
    }

    /**
     * Generate next ID
     */
    public int nextId() {
        return entities.stream()
                .mapToInt(this::getId)
                .max()
                .orElse(getStartingId() - 1) + 1;
    }

    // =========================================
    //        ABSTRACT METHODS (MUST IMPLEMENT)
    // =========================================

    /**
     * Parse a CSV line into an entity
     * @param parts Array of CSV values
     * @return Entity object or null if invalid
     */
    protected abstract T parseEntity(String[] parts);

    /**
     * Convert entity to CSV line
     * @param entity The entity to convert
     * @return CSV formatted string
     */
    protected abstract String toCSVLine(T entity);

    /**
     * Get the ID from an entity
     * @param entity The entity
     * @return The ID value
     */
    protected abstract int getId(T entity);

    /**
     * Get minimum number of columns required
     */
    protected abstract int getMinimumColumns();

    /**
     * Get the entity name for error messages
     */
    protected abstract String getEntityName();

    /**
     * Get the starting ID for this entity type
     */
    protected abstract int getStartingId();
}