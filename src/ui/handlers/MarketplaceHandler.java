package ui.handlers;

import models.Product;
import models.Jobseeker;
import managers.ProductManager;
import java.util.List;
import java.util.Scanner;

public class MarketplaceHandler {
    private final Jobseeker jobseeker;
    private final ProductManager pm;
    private final Scanner scanner;

    public MarketplaceHandler(Jobseeker jobseeker, ProductManager pm, Scanner scanner) {
        this.jobseeker = jobseeker;
        this.pm = pm;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n===== MARKETPLACE =====");
            System.out.println("Wallet Balance: ₱" + jobseeker.getMoney());
            System.out.println("1. View All Products");
            System.out.println("2. Purchase Product");
            System.out.println("0. Back");
            System.out.print("Enter your choice: ");
            
            switch (readInt()) {
                case 1 -> viewAllProducts();
                case 2 -> purchaseProduct();
                case 0 -> { return; }
                default -> System.out.println("ERROR: Invalid selection.");
            }
        }
    }

    private void viewAllProducts() {
        List<Product> products = pm.findAll().stream()
            .filter(p -> p.getStatus().equalsIgnoreCase("Available"))
            .toList();
            
        if (products.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        
        System.out.println("\n--- Available Products ---");
        for (Product p : products) {
            System.out.println(p.displayString());
            System.out.println("-------------------------------------");
        }
    }

    private void purchaseProduct() {
        viewAllProducts();
        
        System.out.print("\nEnter Product ID (or 0 to cancel): ");
        int id = readInt();
        if (id == 0) return;
        
        Product product = pm.findById(id);
        if (product == null || !product.getStatus().equalsIgnoreCase("Available")) {
            System.out.println("ERROR: Product not found or not available.");
            return;
        }
        
        System.out.print("Enter quantity: ");
        int qty = readInt();
        
        if (qty <= 0) {
            System.out.println("ERROR: Invalid quantity.");
            return;
        }
        
        if (qty > product.getQuantity()) {
            System.out.println("ERROR: Not enough stock. Available: " + product.getQuantity());
            return;
        }
        
        double total = product.getPrice() * qty;
        if (jobseeker.getMoney() < total) {
            System.out.println("ERROR: Insufficient funds. Total cost: ₱" + total);
            return;
        }
        
        // Process purchase
        jobseeker.setMoney(jobseeker.getMoney() - total);
        pm.update(id, null, null, null, product.getQuantity() - qty, null);
        
        System.out.println("\nSUCCESS: Purchase completed!");
        System.out.println("Product: " + product.getProductName());
        System.out.println("Quantity: " + qty);
        System.out.println("Total Cost: ₱" + total);
        System.out.println("New Balance: ₱" + jobseeker.getMoney());
    }

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}