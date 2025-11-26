package managers;

import java.nio.file.*;
import java.util.*;
import models.Product;
import utils.CSVHelper;

public class ProductManager {
    private final Path csvPath;
    private final String HEADER = "productId,productName,description,price,quantity,status";
    private final List<Product> products = new ArrayList<>();

    public ProductManager(String csvFilePath) {
        this.csvPath = Paths.get(csvFilePath);
        CSVHelper.ensureFileWithHeader(csvPath, HEADER);
        load();
    }

    private void load() {
        products.clear();
        List<String> lines = CSVHelper.readAllLines(csvPath);
        if (lines.size() <= 1) return;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] p = CSVHelper.split(line);
            if (p.length < 6) continue;

            try {
                int id = Integer.parseInt(p[0].trim());
                String name = p[1].trim();
                String desc = p[2].trim();
                double price = Double.parseDouble(p[3].trim());
                int qty = Integer.parseInt(p[4].trim());
                String status = p[5].trim();
                products.add(new Product(id, name, desc, price, qty, status));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid product line: " + line);
            }
        }
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public Product findById(int id) {
        return products.stream().filter(p -> p.getProductId() == id).findFirst().orElse(null);
    }

    public Product create(String name, String description, double price, int quantity) {
        int id = nextId();
        String status = quantity > 0 ? "Available" : "Out of Stock";
        Product p = new Product(id, name, description, price, quantity, status);
        products.add(p);
        persist();
        return p;
    }

    public boolean update(int id, String name, String description, Double price, Integer quantity, String status) {
        Product p = findById(id);
        if (p == null) return false;
        if (name != null && !name.isEmpty()) p.setProductName(name);
        if (description != null && !description.isEmpty()) p.setDescription(description);
        if (price != null) p.setPrice(price);
        if (quantity != null) {
            p.setQuantity(quantity);
            p.setStatus(quantity > 0 ? "Available" : "Out of Stock");
        }
        if (status != null && !status.isEmpty()) p.setStatus(status);
        persist();
        return true;
    }

    public boolean delete(int id) {
        boolean removed = products.removeIf(p -> p.getProductId() == id);
        if (removed) persist();
        return removed;
    }

    public int nextId() {
        return products.stream().mapToInt(Product::getProductId).max().orElse(1000) + 1;
    }

    private void persist() {
        List<String> out = new ArrayList<>();
        out.add(HEADER);
        for (Product p : products) {
            out.add(p.toCSVLine());
        }
        CSVHelper.writeAllLines(csvPath, out);
    }
}